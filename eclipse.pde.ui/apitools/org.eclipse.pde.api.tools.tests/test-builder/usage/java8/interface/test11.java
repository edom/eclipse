/*******************************************************************************
 * Copyright (c) May 16, 2014 IBM Corporation and others.
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

import i.INoRefJavadocDefaultInterface2;

/**
 * Tests an impl and interface ref to a restricted default method
 */
public class test11 implements INoRefJavadocDefaultInterface2 {

	public static void main(String[] args) {
		INoRefJavadocDefaultInterface2 four = new test11();
		four.m1();
	}
}
