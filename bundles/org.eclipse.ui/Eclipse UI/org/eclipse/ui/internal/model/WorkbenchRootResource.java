package org.eclipse.ui.internal.model;

import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.jface.resource.ImageDescriptor;
import java.util.Arrays;
import java.util.Iterator;

/**
 * An IWorkbenchAdapter implementation for IWorkspaceRoot objects.
 */
public class WorkbenchRootResource extends WorkbenchAdapter {
/**
 * @see IWorkbenchAdapter#getChildren
 * Returns the children of the root resource.
 */
public Object[] getChildren(Object o) {
	IWorkspaceRoot root = (IWorkspaceRoot) o;
	return root.getProjects();
}
/**
 * @see IWorkbenchAdapter#getImageDescriptor
 */
public ImageDescriptor getImageDescriptor(Object object) {
	return null;
}
/**
 * Returns the name of this element.  This will typically
 * be used to assign a label to this object when displayed
 * in the UI.
 */
public String getLabel(Object o) {
	//root resource has no name
	return "Workspace";
}
}
