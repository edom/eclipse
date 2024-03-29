/*******************************************************************************
 * Copyright (c) 2008, 2010 IBM Corporation and others.
 *
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 ******************************************************************************/

package org.eclipse.equinox.internal.p2.ui;

import java.util.EventObject;

/**
 * Event used to signal that a repository operation is about
 * to begin.  This event can be used to ignore lower-level repository events
 * until the operation is complete.
 *  
 * @since 2.0
 */
public class RepositoryOperationBeginningEvent extends EventObject {

	private static final long serialVersionUID = -7529156836242774280L;

	/**
	 * Construct a new instance of this event.
	 * @param source the source of the event
	 */
	public RepositoryOperationBeginningEvent(Object source) {
		super(source);
	}

}
