package org.eclipse.ui.actions;

/*
 * Licensed Materials - Property of IBM,
 * WebSphere Studio Workbench
 * (c) Copyright IBM Corp 2000
 */
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.ui.help.WorkbenchHelp;
import org.eclipse.ui.internal.IHelpContextIds;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Shell;

/**
 * Standard action for scrubbing the local content in the local file system of
 * the selected resources and all of their descendents.
 * <p>
 * This class may be instantiated; it is not intended to be subclassed.
 * </p>
 */
public class ScrubLocalAction extends WorkspaceAction {

	/**
	 * The id of this action.
	 */
	public static final String ID = "org.eclipse.ui.ScrubLocalAction";
/**
 * Creates a new action.
 *
 * @param shell the shell for any dialogs
 */
public ScrubLocalAction(Shell shell) {
	super(shell, "Discard &Local Copy");
	setToolTipText("Discard the local contents of the resource");
	setId(ID);
	WorkbenchHelp.setHelp(this, new Object[] {IHelpContextIds.SCRUB_LOCAL_ACTION});
}
/* (non-Javadoc)
 * Method declared on WorkspaceAction.
 */
String getOperationMessage() {
	return "Discarding content:";
}
/* (non-Javadoc)
 * Method declared on WorkspaceAction.
 */
String getProblemsMessage() {
	return "Problems occurred removing the local contents of the selected resources.";
}
/* (non-Javadoc)
 * Method declared on WorkspaceAction.
 */
String getProblemsTitle() {
	return "Content Removal Problems";
}
/* (non-Javadoc)
 * Method declared on WorkspaceAction.
 */
void invokeOperation(IResource resource, IProgressMonitor monitor) throws CoreException {
	resource.setLocal(false, IResource.DEPTH_INFINITE);
}
/**
 * The <code>ScrubLocalAction</code> implementation of this
 * <code>SelectionListenerAction</code> method ensures that this action is
 * disabled if any of the selections are not resources.
 */
protected boolean updateSelection(IStructuredSelection s) {
	return super.updateSelection(s)
		&& getSelectedNonResources().size() == 0;
}
}
