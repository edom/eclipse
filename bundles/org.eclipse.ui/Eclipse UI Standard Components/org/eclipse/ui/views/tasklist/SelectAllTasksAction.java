package org.eclipse.ui.views.tasklist;

/*
 * Licensed Materials - Property of IBM,
 * WebSphere Studio Workbench
 * (c) Copyright IBM Corp 2000
 */

import org.eclipse.jface.viewers.TableViewer;
 
/**
 * This action selects all tasks currently showing in the task list.
 */
/* package */ class SelectAllTasksAction extends TaskAction {
/**
 * Creates the action.
 */
protected SelectAllTasksAction(TaskList tasklist, String id) {
	super(tasklist, id);
}
/**
 * Selects all resources in the view.
 */
public void run() {
	getTaskList().cancelEditing();
	TableViewer viewer = getTaskList().getTableViewer();
	viewer.getTable().selectAll();
	// force viewer selection change
	viewer.setSelection(viewer.getSelection());
}
}
