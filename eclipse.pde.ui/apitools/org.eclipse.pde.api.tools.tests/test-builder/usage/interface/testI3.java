/*******************************************************************************
 * Copyright (c) 2008, 2013 IBM Corporation and others.
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
 *******************************************************************************/
package x.y.z;

import i.InterfaceUsageInterface2;

/**
 * 
 */
public interface testI3 extends InterfaceUsageInterface2 {

	interface inner extends InterfaceUsageInterface2.Iinner {
		
	}
}

interface Iouter extends InterfaceUsageInterface2.Iinner {
	
}
