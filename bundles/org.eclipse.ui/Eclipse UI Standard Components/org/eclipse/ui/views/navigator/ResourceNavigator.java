package org.eclipse.ui.views.navigator;

/*
 * Licensed Materials - Property of IBM,
 * WebSphere Studio Workbench
 * (c) Copyright IBM Corp 2000
 */
import org.eclipse.core.resources.*;
import org.eclipse.core.runtime.*;
import org.eclipse.help.*;
import org.eclipse.jface.action.*;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.jface.viewers.*;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.*;
import org.eclipse.swt.events.*;
import org.eclipse.swt.widgets.*;
import org.eclipse.ui.*;
import org.eclipse.ui.actions.*;
import org.eclipse.ui.dialogs.*;
import org.eclipse.ui.help.*;
import org.eclipse.ui.views.internal.framelist.*;
import org.eclipse.ui.model.WorkbenchContentProvider;
import org.eclipse.ui.model.WorkbenchLabelProvider;
import org.eclipse.ui.part.*;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;

/**
 * Implements the Resource Navigator view.
 */
public class ResourceNavigator extends ViewPart implements ISetSelectionTarget {
	private TreeViewer viewer;
	private IDialogSettings settings;
	private IMemento memento;
	private NavigatorFrameSource frameSource;
	private FrameList frameList;
	
	private AddBookmarkAction addBookmarkAction;
	private BuildAction buildAction;
	private BuildAction rebuildAllAction;
	private CloseResourceAction closeResourceAction;
	private CopyResourceAction copyResourceAction;
	private CreateFolderAction createFolderAction;
	private CreateFileAction createFileAction;
	private DeleteResourceAction deleteResourceAction;
	private OpenFileAction openFileAction;
	private OpenResourceAction openResourceAction;
	private OpenSystemEditorAction openSystemEditorAction; 
	private PropertyDialogAction propertyDialogAction;
	private RefreshAction localRefreshAction;
	private SortViewAction sortByTypeAction;
	private SortViewAction sortByNameAction;
	private ResourceNavigatorRenameAction renameResourceAction;
	private ResourceNavigatorMoveAction moveResourceAction;
	private CopyProjectAction copyProjectAction;
	private MoveProjectAction moveProjectAction;
	private NewWizardAction newWizardAction;
	private BackAction backAction;
	private ForwardAction forwardAction;
	private GoIntoAction goIntoAction;
	private UpAction upAction;
	private GotoResourceAction gotoResourceAction;
	
	//The filter the resources are cleared up on
	private ResourcePatternFilter patternFilter = new ResourcePatternFilter();

	/** Property store constant for sort order. */
	private static final String STORE_SORT_TYPE = "ResourceViewer.STORE_SORT_TYPE";

	/**
	 * Help context id used for the resource navigator view.
	 */
	public static final String NAVIGATOR_VIEW_HELP_ID = "org.eclipse.ui.general_help_context";

	/**
	 * Preference name constant for linking editor switching to navigator selection.
	 * 
	 * [Issue: We're cheating here, by referencing a preference which is actually defined
	 * on the Workbench's preference page.  The Navigator should eventually have its own
	 * preference page with this preference on it, instead of on the Workbench's.
	 * The value must be the same as IWorkbenchPreferenceConstants.LINK_NAVIGATOR_TO_EDITOR.]
	 */
	private static final String LINK_NAVIGATOR_TO_EDITOR = "LINK_NAVIGATOR_TO_EDITOR";

	// Persistance tags.
	private static final String TAG_SORTER = "sorter";
	private static final String TAG_FILTERS = "filters";
	private static final String TAG_FILTER = "filter";
	private static final String TAG_SELECTION = "selection";
	private static final String TAG_EXPANDED = "expanded";
	private static final String TAG_ELEMENT = "element";
	private static final String TAG_PATH = "path";
	private static final String TAG_VERTICAL_POSITION = "verticalPosition";
	private static final String TAG_HORIZONTAL_POSITION = "horizontalPosition";

	private static final String SELECT_FILTERS_LABEL = "&Filters...";
	private FilterSelectionAction filterAction;

	private IPartListener partListener = new IPartListener() {
		public void partActivated(IWorkbenchPart part) {
			if (part instanceof IEditorPart)
				editorActivated((IEditorPart) part);
		}
		public void partBroughtToTop(IWorkbenchPart part) {}
		public void partClosed(IWorkbenchPart part) {}
		public void partDeactivated(IWorkbenchPart part) {}
		public void partOpened(IWorkbenchPart part) {}
	};
/**
 * Creates a new ResourceNavigator.
 */
public ResourceNavigator() {
	IDialogSettings workbenchSettings = getPlugin().getDialogSettings();
	settings = workbenchSettings.getSection("ResourceNavigator");
	if(settings == null)
		settings = workbenchSettings.addNewSection("ResourceNavigator");
}
/**
 * Converts the given selection into a form usable by the viewer,
 * where the elements are resources.
 */
StructuredSelection convertSelection(ISelection selection) {
	ArrayList list = new ArrayList();
	if (selection instanceof IStructuredSelection) {
		IStructuredSelection ssel = (IStructuredSelection) selection;
		for (Iterator i = ssel.iterator(); i.hasNext();) {
			Object o = i.next();
			IResource resource = null;
			if (o instanceof IResource) {
				resource = (IResource) o;
			}
			else {
				if (o instanceof IAdaptable) {
					resource = (IResource) ((IAdaptable) o).getAdapter(IResource.class);
				}
			}
			if (resource != null) {
				list.add(resource);
			}
		}
	}
	return new StructuredSelection(list);
}
/* (non-Javadoc)
 * Method declared on IWorkbenchPart.
 */ 
public void createPartControl(Composite parent) {
	viewer = new TreeViewer(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
//	initDrillDownAdapter(viewer);
	viewer.setUseHashlookup(true);
	viewer.setContentProvider(new WorkbenchContentProvider());
	viewer.setLabelProvider(new WorkbenchLabelProvider());
	viewer.addFilter(this.patternFilter);
	if(memento != null) restoreFilters();
	initResourceSorter();
	viewer.setInput(getSite().getPage().getInput());
	initFrameList();
	initDragAndDrop();
	initRefreshKey();
	updateTitle();
	
	MenuManager menuMgr = new MenuManager("#PopupMenu");
	menuMgr.setRemoveAllWhenShown(true);
	menuMgr.addMenuListener(new IMenuListener() {
		public void menuAboutToShow(IMenuManager manager) {
			ResourceNavigator.this.fillContextMenu(manager);
		}
	});
	Menu menu = menuMgr.createContextMenu(viewer.getTree());
	viewer.getTree().setMenu(menu);
	getSite().registerContextMenu(menuMgr, viewer);
	makeActions();

	viewer.addSelectionChangedListener(new ISelectionChangedListener() {
		public void selectionChanged(SelectionChangedEvent event) {
			handleSelectionChanged(event);
		}
	});
	viewer.addDoubleClickListener(new IDoubleClickListener() {
		public void doubleClick(DoubleClickEvent event) {
			handleDoubleClick(event);
		}
	});
	viewer.getControl().addKeyListener(new KeyAdapter() {
		public void keyPressed(KeyEvent e) {
			handleKeyPressed(e);
		}
	});

	fillActionBars();
	
	getSite().setSelectionProvider(viewer);

	getSite().getPage().addPartListener(partListener);
	
	if(memento != null) restoreState(memento);
	memento = null;	
	// Set help for the view 
	WorkbenchHelp.setHelp(viewer.getControl(), new ViewContextComputer(this, NAVIGATOR_VIEW_HELP_ID));
}
/* (non-Javadoc)
 * Method declared on IWorkbenchPart.
 */ 
public void dispose() {
	getSite().getPage().removePartListener(partListener);
	super.dispose();
}
/**
 * An editor has been activated.  Set the selection in this navigator
 * to be the editor's input, if linking is enabled.
 */
void editorActivated(IEditorPart editor) {
	if (!isLinkingEnabled())
		return;

	IEditorInput input = editor.getEditorInput();
	if (input instanceof IFileEditorInput) {
		IFileEditorInput fileInput = (IFileEditorInput) input;
		IFile file = fileInput.getFile();
		ISelection newSelection = new StructuredSelection(file);
		if (!viewer.getSelection().equals(newSelection)) {
			viewer.setSelection(newSelection);
		}
	}
	
}
/**
 * Contributes actions to the local tool bar and local pulldown menu.
 */
void fillActionBars() {
	IActionBars actionBars = getViewSite().getActionBars();
	IToolBarManager toolBar = actionBars.getToolBarManager();
	toolBar.add(backAction);
	toolBar.add(forwardAction);
	toolBar.add(upAction);
	actionBars.updateActionBars();

	IMenuManager menu = actionBars.getMenuManager();
	MenuManager submenu = new MenuManager("&Sort");
	menu.add(submenu);
	updateSortActions();
	submenu.add(sortByNameAction);
	submenu.add(sortByTypeAction);
	menu.add(filterAction);
}
/**
 * Called when the context menu is about to open.
 */
void fillContextMenu(IMenuManager menu) {
	IStructuredSelection selection = (IStructuredSelection) getResourceViewer().getSelection();

	updateActions(selection);
	
	fillFileMenu(menu, selection);
	menu.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
	menu.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS+"-end"));
	menu.add(new Separator());

	if (propertyDialogAction.isApplicableForSelection())
	   menu.add(propertyDialogAction);	
}
/**
 * Add file / resource actions to the context sensitive menu.
 * @param menu the context sensitive menu
 * @param selection the current selection in the project explorer
 */
void fillFileMenu(IMenuManager menu, IStructuredSelection selection) {
	boolean anyResourceSelected = !selection.isEmpty() && SelectionUtil.allResourcesAreOfType(selection, IResource.PROJECT | IResource.FOLDER | IResource.FILE);
	boolean onlyFilesSelected = !selection.isEmpty() && SelectionUtil.allResourcesAreOfType(selection, IResource.FILE);
	boolean onlyFoldersOrFilesSelected = !selection.isEmpty() && SelectionUtil.allResourcesAreOfType(selection, IResource.FOLDER | IResource.FILE);
	boolean onlyProjectsSelected = !selection.isEmpty() && SelectionUtil.allResourcesAreOfType(selection, IResource.PROJECT);

	MenuManager newMenu = new MenuManager("Ne&w");
	menu.add(newMenu);
	new org.eclipse.ui.internal.NewWizardMenu(newMenu, getSite().getWorkbenchWindow(), false);
	
	if (selection.size() == 1 && SelectionUtil.allResourcesAreOfType(selection, IResource.PROJECT | IResource.FOLDER)) {
		menu.add(goIntoAction);
	}
	MenuManager gotoMenu = new MenuManager("G&o To");
	menu.add(gotoMenu);
	gotoMenu.add(backAction);
	gotoMenu.add(forwardAction);
	gotoMenu.add(upAction);
	gotoMenu.add(gotoResourceAction);
		
	if (onlyFilesSelected)
		menu.add(openFileAction);

	if (anyResourceSelected) {
		fillOpenWithMenu(menu, selection);
		fillOpenToMenu(menu, selection);
	}
	menu.add(new Separator());
	
	if (onlyFoldersOrFilesSelected) {
		menu.add(copyResourceAction);
		menu.add(moveResourceAction);
	} else if (onlyProjectsSelected) {
		menu.add(copyProjectAction);
		menu.add(moveProjectAction);
	}
	if (anyResourceSelected) {
		menu.add(renameResourceAction);
		menu.add(deleteResourceAction);
	}
	if (onlyFilesSelected)
		menu.add(addBookmarkAction);

	menu.add(new Separator());
	if (onlyProjectsSelected) {
		menu.add(openResourceAction);
		menu.add(closeResourceAction);
		// Allow manual incremental build only if auto build is off.
		if (!ResourcesPlugin.getWorkspace().isAutoBuilding())
			menu.add(buildAction);
		menu.add(rebuildAllAction);
	}
	menu.add(localRefreshAction);
		
}
/**
 * Add "open to" actions to the context sensitive menu.
 * @param menu the context sensitive menu
 * @param selection the current selection in the project explorer
 */
void fillOpenToMenu(IMenuManager menu, IStructuredSelection selection) 
{
	// If one file is selected get it.
	// Otherwise, do not show the "open with" menu.
	if (selection.size() != 1)
		return;
	IAdaptable element = (IAdaptable) selection.getFirstElement();
	if (!(element instanceof IContainer))
		return;

	// Create a menu flyout.
	MenuManager submenu = new MenuManager("Open Perspective");
	submenu.add(new OpenPerspectiveMenu(getSite().getWorkbenchWindow(), element));
	menu.add(submenu);

}
/**
 * Add "open with" actions to the context sensitive menu.
 * @param menu the context sensitive menu
 * @param selection the current selection in the project explorer
 */
void fillOpenWithMenu(IMenuManager menu, IStructuredSelection selection) {

	// If one file is selected get it.
	// Otherwise, do not show the "open with" menu.
	if (selection.size() != 1)
		return;

	Object element = selection.getFirstElement();
	if (!(element instanceof IFile))
		return;

	// Create a menu flyout.
	MenuManager submenu = new MenuManager("Open Wit&h");
	submenu.add(new OpenWithMenu(getSite().getPage(), (IFile) element));
		
	// Add the submenu.
	menu.add(submenu);
}
/**
 * Returns the pattern filter for this view.
 *
 * @return the pattern filter
 */
ResourcePatternFilter getPatternFilter() {
	return this.patternFilter;
}
/**
 * Returns the navigator's plugin.
 */
AbstractUIPlugin getPlugin() {
	return (AbstractUIPlugin) Platform.getPlugin(PlatformUI.PLUGIN_ID);
}
/**
 * Returns the current sorter.
 */
ResourceSorter getResourceSorter() {
	return (ResourceSorter) getResourceViewer().getSorter();
}
/**
 * Returns the tree viewer which shows the resource hierarchy.
 */
TreeViewer getResourceViewer() {
	return viewer;
}
/**
 * Returns the shell to use for opening dialogs.
 * Used in this class, and in the actions.
 */
Shell getShell() {
	return getResourceViewer().getTree().getShell();
}
/**
 * Returns the message to show in the status line.
 *
 * @param selection the current selection
 * @return the status line message
 */
String getStatusLineMessage(IStructuredSelection selection) {
	if (selection.size() == 1) {
		Object o = selection.getFirstElement();
		if (o instanceof IResource) {
			return ((IResource) o).getFullPath().makeRelative().toString();
		}
		else {
			return "1 item selected";
		}
	}
	if (selection.size() > 1) {
		return selection.size() + " items selected";
	}
	return "";
}
/**
 * Returns the tool tip text for the given element.
 */
String getToolTipText(Object element) {
	if (element instanceof IResource) {
		IPath path = ((IResource) element).getFullPath();
		if (path.isRoot()) {
			return "Workspace";
		}
		else {
			return path.makeRelative().toString();
		}
	}
	else {
		return ((ILabelProvider) getResourceViewer().getLabelProvider()).getText(element);
	}
}
/**
 * Handles double clicks in viewer.
 * Opens editor if file double-clicked.
 */
void handleDoubleClick(DoubleClickEvent event) {
	IStructuredSelection s = (IStructuredSelection)event.getSelection();
	Object element = s.getFirstElement();
	if (element instanceof IFile) {
		openFileAction.selectionChanged(s);
		openFileAction.run();
	}
	else {
		// 1GBZIA0: ITPUI:WIN2000 - Double-clicking in navigator should expand/collapse containers
		if (viewer.isExpandable(element)) {
			viewer.setExpandedState(element, !viewer.getExpandedState(element));
		}
	}
	
}
/**
 * Handles key events in viewer.
 */
void handleKeyPressed(KeyEvent event) {
	if (event.character == SWT.DEL && event.stateMask == 0 && deleteResourceAction.isEnabled()) {
		deleteResourceAction.run();
	}
}
/**
 * Handles selection changed in viewer.
 * Updates global actions.
 * Links to editor (if option enabled)
 */
void handleSelectionChanged(SelectionChangedEvent event) {
	IStructuredSelection sel = (IStructuredSelection) event.getSelection();
	updateStatusLine(sel);
	goIntoAction.update();
	updateGlobalActions(sel);
	linkToEditor(sel);
}
/* (non-Javadoc)
 * Method declared on IViewPart.
 */
public void init(IViewSite site,IMemento memento) throws PartInitException {
	super.init(site,memento);
	this.memento = memento;
}
/**
 * Adds drag and drop support to the navigator.
 */
void initDragAndDrop() {
	int ops = DND.DROP_COPY | DND.DROP_MOVE;
	Transfer[] transfers = new Transfer[] {ResourceTransfer.getInstance(),
	    FileTransfer.getInstance(), PluginTransfer.getInstance()};
	viewer.addDragSupport(ops, transfers, new NavigatorDragAdapter((ISelectionProvider)viewer));
	viewer.addDropSupport(ops, transfers, new NavigatorDropAdapter(viewer));
}
/**
 * Initializes a drill down adapter on the viewer.
 */ 
void initDrillDownAdapter(TreeViewer viewer) {
	DrillDownAdapter drillDownAdapter = new DrillDownAdapter(viewer) {
		// need to update title whenever input changes;
		// updateNavigationButtons is called whenever any of the drill down buttons are used
		protected void updateNavigationButtons() {
			super.updateNavigationButtons();
			updateTitle();
		}
	};
	drillDownAdapter.addNavigationActions(getViewSite().getActionBars().getToolBarManager());
}
void initFrameList() {
	frameSource = new NavigatorFrameSource(this);
	frameList = new FrameList(frameSource);
	frameSource.connectTo(frameList);	
}
/**
 * Create the KeyListener for doing the refresh on the viewer.
 */
private void initRefreshKey() {

	getResourceViewer().getControl().addKeyListener(new KeyAdapter() {
		public void keyPressed(KeyEvent event) {
			if (event.keyCode == SWT.F5) {
				localRefreshAction.selectionChanged(
					(IStructuredSelection) getResourceViewer().getSelection());
				localRefreshAction.run();
			}
		}
	});
}
/**
 * Init the current sorter.
 */
void initResourceSorter() {
	int sortType = ResourceSorter.NAME;
	try {
		int sortInt = 0;
		if(memento != null) {
			String sortStr = memento.getString(TAG_SORTER);
			if(sortStr != null)
				sortInt = new Integer(sortStr).intValue();
		} else {
			sortInt = settings.getInt(STORE_SORT_TYPE);
		}
		if (sortInt == ResourceSorter.NAME || sortInt == ResourceSorter.TYPE)
			sortType = sortInt;
	} catch (NumberFormatException e) {}
	setResourceSorter(new ResourceSorter(sortType));
}
/**
 * Returns whether the preference to link navigator selection to active editor is enabled.
 */
boolean isLinkingEnabled() {
	IPreferenceStore store = getPlugin().getPreferenceStore(); 
	return store.getBoolean(LINK_NAVIGATOR_TO_EDITOR);
}
/**
 * Links to editor (if option enabled)
 */
void linkToEditor(IStructuredSelection selection) {
	if (!isLinkingEnabled())
		return;

	Object obj = selection.getFirstElement();
	if (obj instanceof IFile && selection.size() == 1) {
		IFile file = (IFile) obj;
		IWorkbenchPage page = getSite().getPage();
		IEditorPart editorArray[] = page.getEditors();
		for (int i = 0; i < editorArray.length; ++i) {
			IEditorPart editor = editorArray[i];
			IEditorInput input = editor.getEditorInput();
			if (input instanceof IFileEditorInput && file.equals(((IFileEditorInput)input).getFile())) {
				page.bringToTop(editor);
				return;
			}
		}
	}
}
/**
 *	Create self's action objects
 */
void makeActions() {
	Shell shell = getShell();
	openResourceAction = new OpenResourceAction(shell);
	openFileAction = new OpenFileAction(getSite().getPage());
	openSystemEditorAction = new OpenSystemEditorAction(getSite().getPage());
	closeResourceAction = new CloseResourceAction(shell);
	localRefreshAction = new RefreshAction(shell);
	buildAction = new BuildAction(shell, IncrementalProjectBuilder.INCREMENTAL_BUILD);
	rebuildAllAction = new BuildAction(shell, IncrementalProjectBuilder.FULL_BUILD);
	moveResourceAction = new ResourceNavigatorMoveAction(shell,this.viewer);
	copyResourceAction = new CopyResourceAction(shell);
	moveProjectAction = new MoveProjectAction(shell);
	copyProjectAction = new CopyProjectAction(shell);	
	renameResourceAction = new ResourceNavigatorRenameAction(shell,this.viewer);
	deleteResourceAction = new DeleteResourceAction(shell);
	sortByNameAction = new SortViewAction(this, false);
	sortByTypeAction = new SortViewAction(this, true);
	filterAction = new FilterSelectionAction(shell, this, SELECT_FILTERS_LABEL);
	addBookmarkAction = new AddBookmarkAction(shell);
	propertyDialogAction = new PropertyDialogAction(getShell(), getResourceViewer());
	newWizardAction = new NewWizardAction();
	backAction = new BackAction(frameList);
	forwardAction = new ForwardAction(frameList);
	goIntoAction = new GoIntoAction(frameList);
	upAction = new UpAction(frameList);

	gotoResourceAction = new GotoResourceAction(this, "&Resource");
	
	//we know these will be in a sub-folder called "New" so we can shorten the name
	createFolderAction = new CreateFolderAction(shell);
	createFolderAction.setText("&Folder");
	createFileAction = new CreateFileAction(shell);
	createFileAction.setText("Fil&e");

	IActionBars actionBars = getViewSite().getActionBars();
	actionBars.setGlobalActionHandler(IWorkbenchActionConstants.DELETE, deleteResourceAction);
	actionBars.setGlobalActionHandler(IWorkbenchActionConstants.BOOKMARK, addBookmarkAction);
}
public void restoreFilters() {
	IMemento filtersMem = memento.getChild(TAG_FILTERS);
	if(filtersMem != null) {	
		IMemento children[] = filtersMem.getChildren(TAG_FILTER);
		String filters[] = new String[children.length];
		for (int i = 0; i < children.length; i++) {
			filters[i] = children[i].getString(TAG_ELEMENT);
		}
		getPatternFilter().setPatterns(filters);
	} else {
		getPatternFilter().setPatterns(new String[0]);
	}
}
void restoreState(IMemento memento) {
	IContainer container  = ResourcesPlugin.getWorkspace().getRoot();
	IMemento childMem = memento.getChild(TAG_EXPANDED);
	if(childMem != null) {
		ArrayList elements = new ArrayList();
		IMemento[] elementMem = childMem.getChildren(TAG_ELEMENT);
		for (int i = 0; i < elementMem.length; i++){
			Object element = container.findMember(elementMem[i].getString(TAG_PATH));
			elements.add(element);
		}
		viewer.setExpandedElements(elements.toArray());
	}
	childMem = memento.getChild(TAG_SELECTION);
	if(childMem != null) {
		ArrayList list = new ArrayList();
		IMemento[] elementMem = childMem.getChildren(TAG_ELEMENT);
		for (int i = 0; i < elementMem.length; i++){
			Object element = container.findMember(elementMem[i].getString(TAG_PATH));
			list.add(element);
		}
		viewer.setSelection(new StructuredSelection(list));
	}

	Tree tree = viewer.getTree();
	//save vertical position
	ScrollBar bar = tree.getVerticalBar();
	if (bar != null) {
		try {
			String posStr = memento.getString(TAG_VERTICAL_POSITION);
			int position;
			position = new Integer(posStr).intValue();
			bar.setSelection(position);
		} catch (NumberFormatException e){}
	}
	bar = tree.getHorizontalBar();
	if (bar != null) {
		try {
			String posStr = memento.getString(TAG_HORIZONTAL_POSITION);
			int position;
			position = new Integer(posStr).intValue();
			bar.setSelection(position);
		} catch (NumberFormatException e){}
	}
}
public void saveState(IMemento memento) {
	if(viewer == null) {
		if(this.memento != null) //Keep the old state;
			memento.putMemento(this.memento);
		return;
	}

	//save sorter
	memento.putInteger(TAG_SORTER,getResourceSorter().getCriteria());
	//save filters
	String filters[] = getPatternFilter().getPatterns();
	if(filters.length > 0) {
		IMemento filtersMem = memento.createChild(TAG_FILTERS);
		for (int i = 0; i < filters.length; i++){
			IMemento child = filtersMem.createChild(TAG_FILTER);
			child.putString(TAG_ELEMENT,filters[i]);
		}
	}
	//save expanded elements
	Tree tree = viewer.getTree();
 	Object expandedElements[] = viewer.getExpandedElements();
 	if(expandedElements.length > 0) {
	 	IMemento expandedMem = memento.createChild(TAG_EXPANDED);
 		for (int i = 0; i < expandedElements.length; i++) {
	 		IMemento elementMem = expandedMem.createChild(TAG_ELEMENT);
 			elementMem.putString(TAG_PATH,((IResource)expandedElements[i]).getFullPath().toString());
 		}
 	}

 	//save selection
 	Object elements[] = ((IStructuredSelection)viewer.getSelection()).toArray();
 	if(elements.length > 0) {
 		IMemento selectionMem = memento.createChild(TAG_SELECTION);
 		for (int i = 0; i < elements.length; i++) {
	 		IMemento elementMem = selectionMem.createChild(TAG_ELEMENT);
 			elementMem.putString(TAG_PATH,((IResource)elements[i]).getFullPath().toString());
 		}
 	}

 	//save vertical position
	ScrollBar bar = tree.getVerticalBar();
	int position = bar != null ? bar.getSelection():0;
	memento.putString(TAG_VERTICAL_POSITION,String.valueOf(position));
	//save horizontal position
	bar = tree.getHorizontalBar();
	position = bar != null ? bar.getSelection():0;
	memento.putString(TAG_HORIZONTAL_POSITION,String.valueOf(position));
}
/**
 *	Reveal and select the passed element selection in self's visual component
 */
public void selectReveal(ISelection selection) {
	StructuredSelection ssel = convertSelection(selection);
	if (!ssel.isEmpty()) {
		getResourceViewer().setSelection(ssel, true);
	}
}
/**
 * @see IWorkbenchPart#setFocus()
 */ 
public void setFocus() {
	getResourceViewer().getTree().setFocus();
}
/**
 * Note: For experimental use only.
 * Sets the decorator for the navigator.
 *
 * @param decorator a label decorator or <code>null</code> for no decorations.
 */
public void setLabelDecorator(ILabelDecorator decorator) {
	
	if (decorator == null) {
		getResourceViewer().setLabelProvider(new WorkbenchLabelProvider());
	}
	else {
		getResourceViewer().setLabelProvider(new DecoratingLabelProvider(new WorkbenchLabelProvider(), decorator));
	}
}
/**
 * Set the current sorter.
 */
void setResourceSorter(ResourceSorter sorter) {
	TreeViewer viewer = getResourceViewer();
	viewer.getControl().setRedraw(false);
	viewer.setSorter(sorter);
	viewer.getControl().setRedraw(true);
	settings.put(STORE_SORT_TYPE,sorter.getCriteria());
	updateSortActions();
}
/**
 * Updates all actions with the given selection.
 * Necessary when popping up a menu, because some of the enablement criteria
 * may have changed, even if the selection in the viewer hasn't.
 * E.g. A project was opened or closed.
 */
void updateActions(IStructuredSelection selection) {
	buildAction.selectionChanged(selection);
	rebuildAllAction.selectionChanged(selection);
	closeResourceAction.selectionChanged(selection);
	copyResourceAction.selectionChanged(selection);
	createFolderAction.selectionChanged(selection);
	createFileAction.selectionChanged(selection);
	localRefreshAction.selectionChanged(selection);
	moveResourceAction.selectionChanged(selection);
	openResourceAction.selectionChanged(selection);
	openFileAction.selectionChanged(selection);
	openSystemEditorAction.selectionChanged(selection);
	propertyDialogAction.selectionChanged(selection);
	renameResourceAction.selectionChanged(selection);
	sortByTypeAction.selectionChanged(selection);
	sortByNameAction.selectionChanged(selection);
	copyProjectAction.selectionChanged(selection);
	moveProjectAction.selectionChanged(selection);
	updateGlobalActions(selection);
}
/**
 * Updates the global actions with the given selection.
 * Be sure to invoke after actions objects have updated, since can* methods delegate to action objects.
 */
void updateGlobalActions(IStructuredSelection selection) {
	deleteResourceAction.selectionChanged(selection);
	addBookmarkAction.selectionChanged(selection);

	// Ensure Copy global action targets correct action,
	// either copyProjectAction or copyResourceAction,
	// depending on selection.
	copyProjectAction.selectionChanged(selection);
	copyResourceAction.selectionChanged(selection);
	IActionBars actionBars = getViewSite().getActionBars();
	if (copyProjectAction.isEnabled())
		actionBars.setGlobalActionHandler(IWorkbenchActionConstants.COPY, copyProjectAction);
	else
		actionBars.setGlobalActionHandler(IWorkbenchActionConstants.COPY, copyResourceAction);
	actionBars.updateActionBars();
	renameResourceAction.selectionChanged(selection);
}
/**
 * Updates the checked state of the sort actions.
 */
void updateSortActions() {
	int criteria = getResourceSorter().getCriteria();
	if (sortByNameAction != null && sortByTypeAction != null) {
		sortByNameAction.setChecked(criteria == ResourceSorter.NAME);
		sortByTypeAction.setChecked(criteria == ResourceSorter.TYPE);
	}
}
/**
 * Updates the message shown in the status line.
 *
 * @param selection the current selection
 */
void updateStatusLine(IStructuredSelection selection) {
	String msg = getStatusLineMessage(selection);
	getViewSite().getActionBars().getStatusLineManager().setMessage(msg);
}
/**
 * Updates the title text and title tool tip.
 * Called whenever the input of the viewer changes.
 */ 
void updateTitle() {
	Object input = getResourceViewer().getInput();
	String viewName = getConfigurationElement().getAttribute("name");
	IWorkspace workspace = ResourcesPlugin.getWorkspace();
	if (input == null || input.equals(workspace) || input.equals(workspace.getRoot())) {
		setTitle(viewName);
		setTitleToolTip("");
	}
	else {
		ILabelProvider labelProvider = (ILabelProvider) getResourceViewer().getLabelProvider();
		setTitle(viewName + " : " + labelProvider.getText(input));
		setTitleToolTip(getToolTipText(input));
	}
}
}
