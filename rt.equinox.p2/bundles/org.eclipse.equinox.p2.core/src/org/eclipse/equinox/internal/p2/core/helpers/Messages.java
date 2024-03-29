/*******************************************************************************
 *  Copyright (c) 2007, 2009 IBM Corporation and others.
 *
 *  This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License 2.0
 *  which accompanies this distribution, and is available at
 *  https://www.eclipse.org/legal/epl-2.0/
 *
 *  SPDX-License-Identifier: EPL-2.0
 * 
 *  Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.equinox.internal.p2.core.helpers;

import org.eclipse.osgi.util.NLS;

class Messages extends NLS {

	private static final String BUNDLE_NAME = "org.eclipse.equinox.internal.p2.core.helpers.messages"; //$NON-NLS-1$

	static {
		// load message values from bundle file and assign to fields below
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
		// Do not instantiate
	}

	public static String Util_Invalid_Zip_File_Format;
	public static String Util_Error_Unzipping;
	public static String Util_Empty_Zip_Entry;
	public static String Util_Absolute_Entry;

}
