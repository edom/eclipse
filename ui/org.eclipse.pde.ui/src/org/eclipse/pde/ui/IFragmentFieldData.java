/*******************************************************************************
 * Copyright (c) 2000, 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.pde.ui;

/**
 * In addition to field data from the 'New Project' wizard pages, this interface
 * provides choices made by the user that are unique to creating a new fragment
 * project.
 * 
 * @since 3.0
 */
public interface IFragmentFieldData extends IFieldData {
	/**
	 * Referenced plug-in id field
	 * 
	 * @return the id of the fragment's plug-in
	 */
	String getPluginId();
	/**
	 * Referenced plug-in version field
	 * 
	 * @return the version of the fragment's plug-in
	 */
	String getPluginVersion();
	/**
	 * Referenced plug-in version match choice
	 * 
	 * @return the rule for matching the version of the referenced plug-in that
	 *         can be one of the values defined in <code>IMatchRules</code>
	 * @see org.eclipse.pde.code.plugin.IMatchRules
	 */
	int getMatch();
}
