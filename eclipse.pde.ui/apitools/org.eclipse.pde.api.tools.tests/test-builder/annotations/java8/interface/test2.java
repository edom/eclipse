/*******************************************************************************
 * Copyright (c) 2014 IBM Corporation and others.
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
package a.b.c;

import org.eclipse.pde.api.tools.annotations.*;

/**
 * Invalid annotations on a functional interface with an inner functional interface
 */
@FunctionalInterface
public interface test2 {
	@NoOverride
	int m1();
	
	@FunctionalInterface
	interface inner {
		@NoOverride
		int m1();
	}
}


