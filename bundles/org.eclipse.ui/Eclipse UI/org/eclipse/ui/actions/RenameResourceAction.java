package org.eclipse.ui.actions;

/*
 * Licensed Materials - Property of IBM,
 * WebSphere Studio Workbench
 * (c) Copyright IBM Corp 2000
 */
import org.eclipse.core.resources.*;
import org.eclipse.core.runtime.*;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.help.WorkbenchHelp;
import org.eclipse.ui.internal.WorkbenchPlugin;
import org.eclipse.ui.internal.IHelpContextIds;
import org.eclipse.ui.views.navigator.ResourceNavigator;
import org.eclipse.jface.dialogs.IInputValidator;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.*;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.TreeEditor;
import org.eclipse.swt.events.*;
import org.eclipse.swt.graphics.*;
import org.eclipse.swt.widgets.*;
import java.util.List;



/**
 * Standard action for renaming the selected resources.
 * <p>
 * This class may be instantiated; it is not intended to be subclassed.
 * </p>
 */
public class RenameResourceAction extends WorkspaceAction {

	/*The tree editing widgets. If treeEditor is null then edit using the
	dialog. We keep the editorText around so that we can close it if
	a new selection is made. */
	private TreeEditor treeEditor;
	private Tree navigatorTree;
	private Text textEditor;
	private Composite textEditorParent;

	/**
	 * The id of this action.
	 */
	public static final String ID = PlatformUI.PLUGIN_ID + ".RenameResourceAction";

	/**
	 * The new path.
	 */
	private IPath newPath;

	private static final String CHECK_RENAME_TITLE = "Check Rename";

	private static final String CHECK_RENAME_MESSAGE =
		" is read only. Do you still wish to rename it?";
	private static String RESOURCE_EXISTS_TITLE = "Resource Exists";
	private static String RESOURCE_EXISTS_MESSAGE = " exists. Do you wish to overwrite?";
	private static String RENAMING_MESSAGE = "Renaming";

/**
 * Creates a new action. Using this constructor directly will rename using a
 * dialog rather than the inline editor of a ResourceNavigator.
 *
 * @param shell the shell for any dialogs
 */
public RenameResourceAction(Shell shell) {
	super(shell, "Rena&me");
	setToolTipText("Rename the resource");
	setId(ID);
	WorkbenchHelp.setHelp(this, new Object[] {IHelpContextIds.RENAME_RESOURCE_ACTION});
}
/**
 * Creates a new action.
 *
 * @param shell the shell for any dialogs
 */
public RenameResourceAction(Shell shell, Tree tree) {
	this(shell);
	this.navigatorTree = tree;
	this.treeEditor = new TreeEditor(tree);
}
/**
 * Check if the user wishes to overwrite the supplied resource
 * @returns true if there is no collision or delete was successful
 * @param shell the shell to create the dialog in 
 * @param destination - the resource to be overwritten
 */
private boolean checkOverwrite(
	final Shell shell,
	final IResource destination) {

	final boolean[] result = new boolean[1];

	//Run it inside of a runnable to make sure we get to parent off of the shell as we are not
	//in the UI thread.

	Runnable query = new Runnable() {
		public void run() {
			result[0] =
				MessageDialog.openQuestion(
					shell,
					RESOURCE_EXISTS_TITLE,
					destination.toString() + RESOURCE_EXISTS_MESSAGE);
		}

	};

	shell.getDisplay().syncExec(query);
	return result[0];
}
/**
 * Check if the supplied resource is read only or null. If it is then ask the user if they want
 * to continue. Return true if the resource is not read only or if the user has given
 * permission.
 * @return boolean
 */
private boolean checkReadOnlyAndNull(IResource currentResource) {
	//Do a quick read only and null check
	if (currentResource == null)
		return false;

	//Do a quick read only check
	if (currentResource.isReadOnly())
		return MessageDialog.openQuestion(
			getShell(),
			CHECK_RENAME_TITLE,
			currentResource.getName() + CHECK_RENAME_MESSAGE);
	else
		return true;
}
/**
 * Close the text widget and reset the editorText field.
 */
private void disposeTextWidget() {
	if (textEditorParent != null) {
		textEditorParent.dispose();
		textEditorParent = null;
		textEditor = null;
		treeEditor.setEditor(null,null);
	}
}
/* (non-Javadoc)
 * Method declared on WorkspaceAction.
 */
String getOperationMessage() {
	return "Renaming:";
}
/* (non-Javadoc)
 * Method declared on WorkspaceAction.
 */
String getProblemsMessage() {
	return "Problems occurred renaming the selected resource.";
}
/* (non-Javadoc)
 * Method declared on WorkspaceAction.
 */
String getProblemsTitle() {
	return "Rename Problems";
}
/**
 * Get the Tree being edited.
 * @returnTree
 */
private Tree getTree() {
	return this.navigatorTree;
}
/**
 * Get the boolean that indicates if this action should be enabled or disabled.
 */
private boolean getUpdateValue(IStructuredSelection selection) {
	if (selection.size() > 1)
		return false;
	if (!super.updateSelection(selection))
		return false;

	List resources = getSelectedResources();
	if(resources.size() != 1)
		return false;
		
	return true;
}
/* (non-Javadoc)
 * Method declared on WorkspaceAction.
 */
void invokeOperation(IResource resource, IProgressMonitor monitor)
	throws CoreException {

	monitor.beginTask(RENAMING_MESSAGE, 100);
	IWorkspaceRoot workspaceRoot = resource.getWorkspace().getRoot();

	IResource newResource = workspaceRoot.findMember(newPath);
	if (newResource != null) {
		if (checkOverwrite(getShell(), newResource))
			newResource.delete(false, new SubProgressMonitor(monitor, 50));
		else {
			monitor.worked(100);
			return;
		}
	}
	resource.move(newPath, false, new SubProgressMonitor(monitor, 50));
}
/**
 *	Return the new name to be given to the target resource.
 *
 *	@return java.lang.String
 *	@param context IVisualPart
 */
protected String queryNewResourceName(final IResource resource) {
	final IWorkspace workspace = WorkbenchPlugin.getPluginWorkspace();
	final IPath prefix = resource.getFullPath().removeLastSegments(1);
	IInputValidator validator = new IInputValidator() {
		public String isValid(String string) {
			if (resource.getName().equals(string)) {
				return "You must use a different name";
			}
			IStatus status = workspace.validateName(string, resource.getType());
			if (!status.isOK()) {
				return status.getMessage();
			}
			if (workspace.getRoot().exists(prefix.append(string))) {
				return "A resource with that name already exists";
			}
			return null;
		}
	};
		
	InputDialog dialog = new InputDialog(
		getShell(),
		"Rename Resource", 
		"Enter the new resource name:", resource.getName(), validator);
	dialog.setBlockOnOpen(true);
	dialog.open();
	return dialog.getValue();
}
/**
 *	Return the new name to be given to the target resource or <code>null<code>
 *  if the query was canceled. Rename the currently selected resource using the table editor. 
 *  Continue the action when the user is done.
 *
 *	@return java.lang.String
 *	@param context IVisualPart
 */
private void queryNewResourceNameInline(final IResource resource) {

	//We assume the tree is not null as this method will not get called if it is
	Tree tree = getTree();

	// Create text editor parent.  This draws a nice bounding rect.
	textEditorParent = new Composite (tree, SWT.NONE);
	textEditorParent.setVisible(false);
	textEditorParent.addListener(SWT.Paint, new Listener() {
		public void handleEvent (Event e) {
			Point textSize = textEditor.getSize();
			Point parentSize = textEditorParent.getSize();
			e.gc.drawRectangle(0, 0, Math.min(textSize.x + 4, parentSize.x - 1), parentSize.y - 1);
		}
	});

	// Create inner text editor.
	textEditor = new Text(textEditorParent, SWT.NONE);
	textEditorParent.setBackground(textEditor.getBackground());
	textEditor.addListener(SWT.Modify, new Listener() {
		public void handleEvent (Event e) {
			Point textSize = textEditor.computeSize(SWT.DEFAULT, SWT.DEFAULT);
			textSize.x += textSize.y; // Add extra space for new characters.
			Point parentSize = textEditorParent.getSize();
			textEditor.setBounds(2, 1, Math.min(textSize.x, parentSize.x - 4), parentSize.y - 2);
			textEditorParent.redraw();
		}
	});
	textEditor.addKeyListener(new KeyAdapter() {
		public void keyReleased(KeyEvent event) {
			if (event.character == SWT.CR) {
				saveChangesAndDispose(resource);
			}
			if (event.character == SWT.ESC) {
				//Do nothing in this case
				disposeTextWidget();
			}
		}
	});
	textEditor.addFocusListener(new FocusAdapter() {
		public void focusLost(FocusEvent fe) {
			//If the focus is lost then apply the rename
			saveChangesAndDispose(resource);
		}
	});
	textEditor.setText(resource.getName());

	// Init tree editor.
	TreeItem[] selectedItems = tree.getSelection();
	treeEditor.horizontalAlignment = SWT.LEFT;
	treeEditor.grabHorizontal = true;
	treeEditor.setEditor(textEditorParent, selectedItems[0]);

	// Open text editor with initial size.
	textEditorParent.setVisible(true);
	Point textSize = textEditor.computeSize(SWT.DEFAULT, SWT.DEFAULT);
	textSize.x += textSize.y; // Add extra space for new characters.
	Point parentSize = textEditorParent.getSize();
	textEditor.setBounds(2, 1, Math.min(textSize.x, parentSize.x - 4), parentSize.y - 2);
	textEditorParent.redraw();
	textEditor.selectAll ();
	textEditor.setFocus ();
}
/* (non-Javadoc)
 * Method declared on IAction; overrides method on WorkspaceAction.
 */
public void run() {

	if (this.navigatorTree == null) {
		IResource currentResource =
			(IResource) getStructuredSelection().getFirstElement();
		//Do a quick read only and null check
		if (!checkReadOnlyAndNull(currentResource))
			return;
		String newName = queryNewResourceName(currentResource);
		if (newName == null || newName.equals(""))
			return;
		newPath = currentResource.getFullPath().removeLastSegments(1).append(newName);
		super.run();
	} else
		runWithInlineEditor();
}
/* 
 * Run the receiver using an inline editor from the supplied navigator. The
 * navigator will tell the action when the path is ready to run.
 */
private void runWithInlineEditor() {
	IResource currentResource =
		(IResource) getStructuredSelection().getFirstElement();
	if (!checkReadOnlyAndNull(currentResource))
		return;

	queryNewResourceNameInline(currentResource);

}
/* (non-Javadoc)
 * Run the action to completion using the supplied path.
 */
protected void runWithNewPath(IPath path, IResource resource) {
	this.newPath = path;
	super.run();
}
/**
 * Save the changes and dispose of the text widget.
 * @param resource - the resource to move.
 */
private void saveChangesAndDispose(IResource resource) {

	String newName = textEditor.getText();
	//Dispose the text widget regardless
	disposeTextWidget();
	if (!newName.equals(resource.getName())) {
		IPath newPath = resource.getFullPath().removeLastSegments(1).append(newName);
		runWithNewPath(newPath, resource);
	}
}
/**
 * The <code>RenameResourceAction</code> implementation of this
 * <code>SelectionListenerAction</code> method ensures that this action is
 * disabled if any of the selections are not resources or resources that are
 * not local.
 */
protected boolean updateSelection(IStructuredSelection selection) {

	if (selection.size() > 1)
		return false;
	if (!super.updateSelection(selection))
		return false;

	List resources = getSelectedResources();
	if (resources.size() != 1)
		return false;

	return true;
}
}
