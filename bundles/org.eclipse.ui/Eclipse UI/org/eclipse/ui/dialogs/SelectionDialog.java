package org.eclipse.ui.dialogs;

/*
 * Licensed Materials - Property of IBM,
 * WebSphere Studio Workbench
 * (c) Copyright IBM Corp 2000
 */
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.*;
import java.util.List;
import java.util.ArrayList;

/**
 * The abstract implementation of a selection dialog. It can be primed with
 * initial selections (<code>setElementSelections</code>), and returns
 * the final selection (via <code>getResult</code>) after completion.
 * <p>
 * Clients may subclass this dialog to inherit its selection facilities.
 * </p>
 */
public abstract class SelectionDialog extends Dialog {
	// the final collection of selected elements, or null if this dialog was canceled
	private List result;

	// a collection of the initially-selected elements
	private List initialSelections;

	// handle to ok button, for toggling of its enablement
	private Button okButton;

	// title of dialog
	private String title;
	
	// message to show user
	private String message;

	static String SELECT_ALL_TITLE = "Select All";
	static String DESELECT_ALL_TITLE = "Deselect All";
/**
 * Creates a dialog instance.
 * Note that the dialog will have no visual representation (no widgets)
 * until it is told to open.
 *
 * @param parentShell the parent shell
 */
protected SelectionDialog(Shell parentShell) {
	super(parentShell);
}
/* (non-Javadoc)
 * Method declared in Window.
 */
protected void configureShell(Shell shell) {
	super.configureShell(shell);
	if (title != null)
		shell.setText(title);
}
/* (non-Javadoc)
 * Method declared on Dialog.
 */
protected void createButtonsForButtonBar(Composite parent) {
	okButton = createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL, true);
	createButton(parent, IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL, false);

}
/**
 * Creates the message area for this dialog.
 * <p>
 * This method is provided to allow subclasses to decide where the message
 * will appear on the screen.
 * </p>
 *
 * @param parent the parent composite
 * @return the message label
 */
protected Label createMessageArea(Composite composite) {
	Label label = new Label(composite,SWT.NONE);
	label.setText(message); 
	return label;
}
/**
 * Returns the initial selection in this selection dialog.
 *
 * @return the list of initial selected elements 
 */
protected List getInitialSelections() {
	return initialSelections;
}
/**
 * Returns the message for this dialog.
 *
 * @return the message for this dialog
 */
protected String getMessage() {
	return message;
}
/**
 * Returns the ok button.
 *
 * @return the ok button or <code>null</code> if the button is not created
 *  yet.
 */
public Button getOkButton() {
	return okButton;
}
/**
 * Returns the list of selections made by the user, or <code>null</code> if
 * the selection was canceled.
 *
 * @return the array of selected elements, or <code>null</code> if Cancel was
 *   pressed
 */
public Object[] getResult() {
	if (result == null)
		return null;
	return result.toArray(new Object[result.size()]);
}
/**
 * Sets the initial selection in this selection dialog to the given elements.
 *
 * @param selectedElements the array of elements to select
 */
public void setInitialSelections(Object[] selectedElements) {
	initialSelections = new ArrayList(selectedElements.length);
	for (int i = 0; i < selectedElements.length; i++) 
		initialSelections.add(selectedElements[i]);
}
/**
 * Sets the message for this dialog.
 *
 * @param message the message
 */
public void setMessage(String message) {
	this.message = message;
}
/**
 * Set the selections made by the user, or <code>null</code> if
 * the selection was canceled.
 *
 * @param the list of selected elements, or <code>null</code> if Cancel was
 *   pressed
 */
protected void setResult(List newResult) {
	result = newResult;
}
/**
 * Sets the title for this dialog.
 *
 * @param title the title
 */
public void setTitle(String title) {
	this.title = title;
}
}
