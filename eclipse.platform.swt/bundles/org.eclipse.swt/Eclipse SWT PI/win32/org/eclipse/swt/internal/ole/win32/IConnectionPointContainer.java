/*******************************************************************************
 * Copyright (c) 2000, 2012 IBM Corporation and others.
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
package org.eclipse.swt.internal.ole.win32;

public class IConnectionPointContainer extends IUnknown
{
public IConnectionPointContainer(long /*int*/ address) {
	super(address);
}
public int FindConnectionPoint(GUID riid, long /*int*/[] ppCP) {
	return COM.VtblCall(4, address, riid, ppCP);
}
}