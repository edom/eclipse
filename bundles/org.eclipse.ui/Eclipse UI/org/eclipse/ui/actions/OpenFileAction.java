package org.eclipse.ui.actions;

/*
 * Licensed Materials - Property of IBM,
 * WebSphere Studio Workbench
 * (c) Copyright IBM Corp 2000
 */

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.ui.internal.WorkbenchPlugin;
import org.eclipse.ui.internal.misc.Assert;
import org.eclipse.ui.internal.IHelpContextIds;
import org.eclipse.ui.internal.dialogs.DialogUtil;
import org.eclipse.ui.help.WorkbenchHelp;
import org.eclipse.ui.*;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import java.util.Iterator;

/**
 * Standard action for opening an editor on the currently selected file 
 * resource(s).
 * <p>
 * Note that there is a different action for opening closed projects:
 * <code>OpenResourceAction</code>.
 * </p>
 * <p>
 * This class may be instantiated; it is not intended to be subclassed.
 * </p>
 */
public class OpenFileAction extends OpenSystemEditorAction {

	/**
	 * The id of this action.
	 */
	public static final String ID = PlatformUI.PLUGIN_ID + ".OpenFileAction";

	/**
	 * The editor to open.
	 */
	private IEditorDescriptor editorDescriptor;
/**
 * Creates a new action that will open editors on the then-selected file 
 * resources. Equivalent to <code>OpenFileAction(page,null)</code>.
 *
 * @param page the workbench page in which to open the editor
 */
public OpenFileAction(IWorkbenchPage page) {
	this(page, null);
}
/**
 * Creates a new action that will open instances of the specified editor on 
 * the then-selected file resources.
 *
 * @param page the workbench page in which to open the editor
 * @param descriptor the editor descriptor, or <code>null</code> if unspecified
 */
public OpenFileAction(IWorkbenchPage page, IEditorDescriptor descriptor) {
	super(page);
	setText(descriptor == null ? "&Open" : descriptor.getLabel());
	WorkbenchHelp.setHelp(this, new Object[] {IHelpContextIds.OPEN_FILE_ACTION});
	setToolTipText("Edit the file");
	setId(ID);
	this.editorDescriptor = descriptor;
}
/**
 * Ensures that the contents of the given file resource are local.
 *
 * @param file the file resource
 * @return <code>true</code> if the file is local, and <code>false</code> if
 *   it could not be made local for some reason
 */
boolean ensureFileLocal(final IFile file) {
	org.eclipse.ui.internal.misc.UIHackFinder.fixPR();
	//Currently fails due to Core PR.  Don't do it for now
	//1G5I6PV: ITPCORE:WINNT - IResource.setLocal() attempts to modify immutable tree
	//file.setLocal(true, IResource.DEPTH_ZERO);
	return true;
}
/**
 * Opens an editor on the given file resource.
 *
 * @param file the file resource
 */
void openFile(IFile file) {
	if (getWorkbenchPage() == null) {
		IStatus status = new Status(IStatus.ERROR, WorkbenchPlugin.PI_WORKBENCH, 1, "Workbench page must be supplied to OpenFileAction", null);
		WorkbenchPlugin.log("Error in OpenFileAction.openFile", status);
		return;
	}
	try {
		if (editorDescriptor == null)
			getWorkbenchPage().openEditor(file);
		else {
			if (ensureFileLocal(file))
				getWorkbenchPage().openEditor(file, editorDescriptor.getId());
		}
	} catch (PartInitException e) {
		DialogUtil.openError(
			getWorkbenchPage().getWorkbenchWindow().getShell(),
			"Problems Opening Editor",
			e.getMessage(),
			e);
	}
}
}
