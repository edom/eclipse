package org.eclipse.ui.internal;

/*
 * (c) Copyright IBM Corp. 2000, 2001.
 * All Rights Reserved.
 */
import org.eclipse.ui.*;
import org.eclipse.ui.help.WorkbenchHelp;
import org.eclipse.ui.actions.*;
import org.eclipse.ui.part.*;
import org.eclipse.ui.internal.IHelpContextIds;

/**
 * Closes the active editor.
 */
public class CloseEditorAction extends ActiveEditorAction {
/**
 *	Create an instance of this class
 */
public CloseEditorAction(IWorkbenchWindow window) {
	super(WorkbenchMessages.getString("CloseEditorAction.text"), window); //$NON-NLS-1$
	setToolTipText(WorkbenchMessages.getString("CloseEditorAction.toolTip")); //$NON-NLS-1$
	setId(IWorkbenchActionConstants.CLOSE);
	WorkbenchHelp.setHelp(this, new Object[] {IHelpContextIds.CLOSE_PART_ACTION});
	setAccelerator(SWT.CTRL | SWT.F4);
}
/* (non-Javadoc)
 * Method declared on IAction.
 */
public void run() {
	IEditorPart part = getActiveEditor();
	if (part != null)
		getActivePage().closeEditor(part, true);
}
}
