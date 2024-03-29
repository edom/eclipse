/*******************************************************************************
 * Copyright (c) 2013 Red Hat, Inc. and others
 *
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 * 
 *  Contributors:
 *     Red Hat, Inc. - initial API and implementation
 *******************************************************************************/
package org.eclipse.equinox.p2.tests.planner;

import junit.framework.*;

public class AllRequestFlexerTests extends TestCase {

	public static Test suite() {
		TestSuite suite = new TestSuite(AllRequestFlexerTests.class.getName());
		suite.addTestSuite(TestRequestFlexerIUProperties.class);
		suite.addTestSuite(TestRequestFlexerOneInstalledOneBeingInstalled.class);
		suite.addTestSuite(TestRequestFlexerOneInstalledReplacingIt.class);
		suite.addTestSuite(TestRequestFlexerOneInstalledTwoBeingInstalled.class);
		suite.addTestSuite(TestRequestFlexerProduct.class);
		suite.addTestSuite(TestRequestFlexerProduct2.class);
		suite.addTestSuite(TestRequestFlexerProductWithLegacyMarkup.class);
		suite.addTestSuite(TestRequestFlexerProductWithMixedMarkup.class);
		suite.addTestSuite(TestRequestFlexerRequestWithOptionalInstall.class);
		suite.addTestSuite(TestRequestFlexerRequestWithRemoval.class);
		suite.addTestSuite(TestRequestFlexerSharedInstall.class);
		return suite;

	}
}
