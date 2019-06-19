/**********************************************************************
 * Copyright (c) 2004, 2005 IBM Corporation and others.
 *
 * This program and the accompanying
 * materials are made available under the terms of
 * the Eclipse Public License 2.0 which accompanies this distribution, and is
t https://www.eclipse.org/legal/epl-2.0/
t
t SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 * IBM - Initial API and implementation
 **********************************************************************/
package org.eclipse.jdt.internal.debug.core;

import org.eclipse.osgi.util.NLS;

public class JDIDebugMessages extends NLS {
	private static final String BUNDLE_NAME = "org.eclipse.jdt.internal.debug.core.JDIDebugMessages";//$NON-NLS-1$

	public static String EventDispatcher_0;

	static {
		// load message values from bundle file
		NLS.initializeMessages(BUNDLE_NAME, JDIDebugMessages.class);
	}
}