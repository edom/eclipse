package org.eclipse.ui.dialogs;

/*
 * Licensed Materials - Property of IBM,
 * WebSphere Studio Workbench
 * (c) Copyright IBM Corp 2000
 */
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.ui.internal.dialogs.PropertyPageContributorManager;
import org.eclipse.ui.internal.dialogs.PropertyPageManager;
import org.eclipse.ui.internal.dialogs.PropertyDialog;
import org.eclipse.ui.internal.IHelpContextIds;
import org.eclipse.ui.help.WorkbenchHelp;
import org.eclipse.ui.*;
import org.eclipse.ui.actions.SelectionProviderAction;
import org.eclipse.ui.model.IWorkbenchAdapter;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.preference.PreferenceManager;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.util.Assert;
import org.eclipse.swt.widgets.Shell;
import java.util.*;

/**
 * Standard action for opening a Property Pages Dialog on the currently selected
 * element.
 * <p>
 * This class may be instantiated; it is not intended to be subclassed.
 * </p>
 * <p>
 * Generally speaking, this action is useful in pop-up menus because it allows
 * the user to browse and change properties of selected elements. When performed,
 * the action will bring up a Property Pages Dialog containing property pages
 * registered with the workbench for elements of the selected type.
 * </p>
 * <p>
 * Although the action is capable of calculating if there are any applicable
 * pages for the current selection, this calculation is costly because it 
 * require searching the workbench registry. Where performance is critical, the 
 * action can simply be added to the pop-up menu. In the event of no applicable
 * pages, the action will just open an appropriate message dialog.
 * </p>
 */
public class PropertyDialogAction extends SelectionProviderAction {

	/**
	 * The shell in which to open the property dialog.
	 */
	private Shell shell;
/**
 * Creates a new action for opening a property dialog on the elements from the
 * given selection provider.
 *
 * @param shell the shell in which the dialog will open
 * @param provider the selection provider whose elements the property dialog
 *   will describe
 */
public PropertyDialogAction(Shell shell, ISelectionProvider provider) {
	super(provider, "P&roperties");
	Assert.isNotNull(shell);
	this.shell = shell;
	setToolTipText("Open the properties dialog for the resource");
	WorkbenchHelp.setHelp(this, new Object[] {IHelpContextIds.PROPERTY_DIALOG_ACTION});
}
/**
 * Returns the name of the given element.
 * 
 * @param element the element
 * @return the name of the element
 */
private String getName(IAdaptable element) {
	IWorkbenchAdapter adapter = (IWorkbenchAdapter)element.getAdapter(IWorkbenchAdapter.class);
	if (adapter != null) {
		return adapter.getLabel(element);
	} else {
		return "";
	}
}
/**
 * Returns whether the provided object has pages registered in the property page
 * manager.
 */
private boolean hasPropertyPagesFor(Object object) {
	PropertyPageContributorManager manager = PropertyPageContributorManager.getManager();
	return manager.hasContributorsFor(object);
}
/**
 * Returns whether this action is actually applicable to the current selection.
 * If this action is disabled, it will return <code>false</code> without further
 * calculation. Iif it is enabled, it will check with the workbench's property
 * page manager to see if there are any property pages registered for the
 * selected element's type.
 * <p>
 * This method is generally too expensive to use when updating the enabled state
 * of the action.
 * </p>
 *
 * @return <code>true</code> if there are property pages for the currently
 *   selected element, and <code>false</code> if more than one element is
 *   selected or there are no property pages for it
 */
public boolean isApplicableForSelection() {
	if (isEnabled()==false) return false;
	Iterator selection = getStructuredSelection().iterator();
	if (!selection.hasNext()) {
		return false;
	}
	Object object = selection.next();
	if (selection.hasNext()) {
		return false;
	}
	return hasPropertyPagesFor(object);
}
/**
 * The <code>PropertyDialogAction</code> implementation of this 
 * <code>IAction</code> method performs the action by opening the Property Page
 * Dialog for the current selection. If no pages are found, an informative 
 * message dialog is presented instead.
 */
public void run() {
	PropertyPageManager pageManager = new PropertyPageManager();
	String title = "";

	// get selection
	IAdaptable element = (IAdaptable) getStructuredSelection().getFirstElement();
	if (element == null)
		return;

	// load pages for the selection
	// fill the manager with contributions from the matching contributors
	PropertyPageContributorManager.getManager().contribute(pageManager, element);
	
	// testing if there are pages in the manager
	Iterator pages = pageManager.getElements(PreferenceManager.PRE_ORDER).iterator();
	String name = getName(element);
	if (!pages.hasNext()) {
		MessageDialog.openInformation(
			shell,
			"Property Pages",
			"No property pages for " + name + ".");
		return;
	} else
		title = "Properties for " + name;

	// TBD: don't use old selection; should be getSimpleSelection(), though we assume structured selection above.
	PropertyDialog propertyDialog = new PropertyDialog(shell, pageManager, getStructuredSelection()); 
	propertyDialog.create();
	propertyDialog.getShell().setText(title);
	propertyDialog.open();
}
/**
 * The <code>PropertyDialogAction</code> implementation of this 
 * <code>SelectionProviderAction</code> method enables the action only if the
 * given selection contains exactly one element.
 */
public void selectionChanged(IStructuredSelection selection) {
	if (selection.isEmpty()) {
		setEnabled(false);
		return;
	}
	Iterator resourcesEnum = selection.iterator();
	Object resource = null;
	if (resourcesEnum.hasNext())
		resource = resourcesEnum.next();

	setEnabled(resource != null && !resourcesEnum.hasNext());
}
}
