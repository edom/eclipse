/*******************************************************************************
 *  Copyright (c) 2013 IBM Corporation and others.
 *
 *  This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License 2.0
 *  which accompanies this distribution, and is available at
 *  https://www.eclipse.org/legal/epl-2.0/
 *
 *  SPDX-License-Identifier: EPL-2.0
 * 
 *  Contributors:
 *  IBM - Initial API and implementation
 *******************************************************************************/
package org.eclipse.jdt.internal.launching.macosx;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "org.eclipse.jdt.internal.launching.macosx.messages"; //$NON-NLS-1$
	public static String MacOSXVMInstallType_0;
	public static String MacOSXVMInstallType_1;
	public static String MacOSXVMInstallType_2;
	public static String MacOSXVMInstallType_jre;
	public static String MacOSXVMInstallType_jre_version;
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
