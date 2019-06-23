/*******************************************************************************
 * Copyright (c) 2008, 2009 IBM Corporation and others.
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

/**
 * Tests invalid @noextend tags on nested inner annotations
 * @noextend
 */
public @interface test3 {

	/**
	 * @noextend
	 */
	@interface inner {
		
	}
	
	@interface inner1 {
		/**
		 * @noextend
		 */
		@interface inner2 {
			
		}
	}
	
	@interface inner2 {
		
	}
}

@interface outer {
	
	/**
	 * @noextend
	 */
	@interface inner {
		
	}
}
