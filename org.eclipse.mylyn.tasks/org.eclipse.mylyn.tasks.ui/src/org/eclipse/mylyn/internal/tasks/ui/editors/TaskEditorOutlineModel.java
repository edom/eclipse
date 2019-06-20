/*******************************************************************************
 * Copyright (c) 2010 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui.editors;

public class TaskEditorOutlineModel {

	private final TaskEditorOutlineNode root;

	public TaskEditorOutlineModel(TaskEditorOutlineNode root) {
		this.root = root;
	}

	public TaskEditorOutlineNode getRoot() {
		return root;
	}

}