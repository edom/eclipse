/*******************************************************************************
 * Copyright (c) 2000, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/

package org.eclipse.jdt.text.tests.performance;

import junit.framework.Test;
import junit.framework.TestSuite;


/**
 * Measures the time to replaceAll in a large file with Quick Diff disabled.
 * 
 * @since 3.1
 */
public class JavaReplaceAllTest extends AbstractJavaReplaceAllTest {

	public static Test suite() {
		return new PerformanceTestSetup(new TestSuite(JavaReplaceAllTest.class));
	}

	protected boolean isQuickDiffEnabled() {
		
		// FIXME: temporary test
		String property= System.getProperty("test.target");
		System.err.println("test.target= '" + property + "'");
		System.err.println("isPerformanceTest= '" + "performance".equals(property) + "'");

		property= System.getProperty("eclipse.perf.dbloc");
		System.err.println("eclipse.perf.dbloc= '" + property + "'");
		
		return false;
	}
	
}
