package org.eclipse.ui.internal.dialogs;

import org.eclipse.swt.events.*;
import org.eclipse.ui.internal.registry.*;
import org.eclipse.ui.*;
import org.eclipse.jface.dialogs.*;
import org.eclipse.jface.viewers.*;
import org.eclipse.swt.*;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.widgets.*;
import java.util.*;

/**
 * A dialog for perspective creation
 */
public class SelectPerspectiveDialog extends org.eclipse.jface.dialogs.Dialog
	implements ISelectionChangedListener
{
	private ListViewer list;
	private IPerspectiveRegistry perspReg;
	private IViewDescriptor selection;
	private IPerspectiveDescriptor perspDesc;
	private Button okButton;
	private Button cancelButton;

	final private static int LIST_WIDTH = 200;
	final private static int LIST_HEIGHT = 200;
/**
 * PerspectiveDialog constructor comment.
 */
public SelectPerspectiveDialog(Shell parentShell, IPerspectiveRegistry perspReg) {
	super(parentShell);
	this.perspReg = perspReg;
}
/**
 * Notifies that the cancel button of this dialog has been pressed.
 */
protected void cancelPressed() {
	perspDesc = null;
	super.cancelPressed();
}
/* (non-Javadoc)
 * Method declared in Window.
 */
protected void configureShell(Shell shell) {
	super.configureShell(shell);
	shell.setText("Select Perspective");
}
/**
 * Adds buttons to this dialog's button bar.
 * <p>
 * The default implementation of this framework method adds 
 * standard ok and cancel buttons using the <code>createButton</code>
 * framework method. Subclasses may override.
 * </p>
 *
 * @param parent the button bar composite
 */
protected void createButtonsForButtonBar(Composite parent) {
	okButton = createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL, true);
	cancelButton = createButton(parent, IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL, false);
}
/**
 * Creates and returns the contents of the upper part 
 * of this dialog (above the button bar).
 *
 * @param the parent composite to contain the dialog area
 * @return the dialog area control
 */
protected Control createDialogArea(Composite parent) {
	// Run super.
	Composite composite = (Composite)super.createDialogArea(parent);
	GridLayout layout = (GridLayout)composite.getLayout();
	layout.makeColumnsEqualWidth = false;
	layout.numColumns = 2;

	// Add perspective list.
	list = new ListViewer(composite, SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER);
	list.setLabelProvider(new PerspLabelProvider());
	list.setContentProvider(new PerspContentProvider());
	list.setInput(perspReg);
	list.addSelectionChangedListener(this);
	list.addDoubleClickListener(new IDoubleClickListener() {
		public void doubleClick(DoubleClickEvent event) {
			handleDoubleClickEvent();
		}
	});

	// Set tree size.
	Control ctrl = list.getControl();
	GridData spec = new GridData(
		GridData.VERTICAL_ALIGN_FILL | 
		GridData.HORIZONTAL_ALIGN_FILL);
	spec.widthHint = LIST_WIDTH;
	spec.heightHint = LIST_HEIGHT;
	ctrl.setLayoutData(spec);

	// Return results.
	return composite;
}
/**
 * Returns the current selection.
 */
public IPerspectiveDescriptor getSelection() {
	return perspDesc;
}
/**
 * Handle a double click event on the list
 */
protected void handleDoubleClickEvent() {
	okPressed();
}
/**
 * Notifies that the selection has changed.
 *
 * @param event event object describing the change
 */
public void selectionChanged(SelectionChangedEvent event) {
	updateSelection();
	updateButtons();
}
/**
 * Update the button enablement state.
 */
protected void updateButtons() {
	okButton.setEnabled(getSelection() != null);	
}
/**
 * Update the selection object.
 */
protected void updateSelection() {
	perspDesc = null;
	IStructuredSelection sel = (IStructuredSelection)list.getSelection();
	if (!sel.isEmpty()) {
		Object obj = sel.getFirstElement();
		if (obj instanceof IPerspectiveDescriptor)
			perspDesc = (IPerspectiveDescriptor)obj;
	}
}
}
