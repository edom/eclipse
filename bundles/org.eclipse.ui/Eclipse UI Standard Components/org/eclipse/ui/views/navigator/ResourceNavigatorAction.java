package org.eclipse.ui.views.navigator;

/*
 * Licensed Materials - Property of IBM,
 * WebSphere Studio Workbench
 * (c) Copyright IBM Corp 2000
 */
import org.eclipse.ui.*;
import org.eclipse.ui.actions.*;
import org.eclipse.jface.viewers.*;
import org.eclipse.swt.widgets.*;

/**
 * Superclass of all actions provided by the resource navigator.
 */
/* package */ abstract class ResourceNavigatorAction extends SelectionProviderAction {
	private ResourceNavigator navigator;
/**
 * Creates a new instance of the class.
 */
public ResourceNavigatorAction(ResourceNavigator navigator, String label) {
	super(navigator.getResourceViewer(), label);
	this.navigator = navigator;
}
/**
 * Returns the resource navigator for which this action was created.
 */
public ResourceNavigator getNavigator() {
	return navigator;
}
/**
 * Returns the resource viewer
 */
protected TreeViewer getResourceViewer() {
	return getNavigator().getResourceViewer();
}
/**
 * Returns the shell to use within actions.
 */
protected Shell getShell() {
	return getNavigator().getShell();
}
/**
 * Returns the workbench.
 */
public IWorkbench getWorkbench() {
	return PlatformUI.getWorkbench();
}
/**
 * Returns the workbench window.
 */
public IWorkbenchWindow getWorkbenchWindow() {
	// TBD: Yuck - huge path
	return getNavigator().getSite().getWorkbenchWindow();
}
}
