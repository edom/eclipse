/*******************************************************************************
 * Copyright (c) 2004, 2012 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui.context;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiPlugin;
import org.eclipse.mylyn.internal.tasks.ui.commands.AbstractTaskHandler;
import org.eclipse.mylyn.internal.tasks.ui.util.TasksUiInternal;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.ui.PlatformUI;

/**
 * @author Steffen Pingel
 */
public class ClearContextHandler extends AbstractTaskHandler {

	@Override
	protected void execute(ExecutionEvent event, ITask[] tasks) throws ExecutionException {
		run(tasks);
	}

	public static void run(ITask task) {
		run(new ITask[] { task });
	}

	public static void run(ITask[] tasks) {
		boolean deleteConfirmed = false;
		if (tasks.length == 1) {
			deleteConfirmed = MessageDialog.openQuestion(
					PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
					Messages.ClearContextHandler_Confirm_clear_context,
					Messages.ClearContextHandler_CLEAR_THE_CONTEXT_THE_FOR_SELECTED_TASK);
		} else if (tasks.length > 1) {
			deleteConfirmed = MessageDialog.openQuestion(
					PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
					Messages.ClearContextHandler_Confirm_clear_context,
					Messages.ClearContextHandler_CLEAR_THE_CONTEXT_THE_FOR_SELECTED_TASKS);
		}
		if (!deleteConfirmed) {
			return;
		}
		for (ITask task : tasks) {
			TasksUiPlugin.getContextStore().clearContext(task);
			TasksUiInternal.getTaskList().notifyElementChanged(task);

		}
	}

}
