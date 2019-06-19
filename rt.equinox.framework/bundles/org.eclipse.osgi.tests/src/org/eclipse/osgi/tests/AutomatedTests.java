/*******************************************************************************
 * Copyright (c) 2004, 2013 IBM Corporation and others.
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
package org.eclipse.osgi.tests;

import junit.framework.*;
import org.eclipse.osgi.tests.bundles.BundleTests;
import org.eclipse.osgi.tests.debugoptions.DebugOptionsTestCase;
import org.eclipse.osgi.tests.eventmgr.EventManagerTests;
import org.eclipse.osgi.tests.filter.FilterTests;
import org.eclipse.osgi.tests.hooks.framework.AllFrameworkHookTests;
import org.eclipse.osgi.tests.internal.plugins.InstallTests;
import org.eclipse.osgi.tests.listeners.ExceptionHandlerTests;
import org.eclipse.osgi.tests.misc.MiscTests;
import org.eclipse.osgi.tests.permissions.PermissionTests;
import org.eclipse.osgi.tests.serviceregistry.ServiceRegistryTests;

public class AutomatedTests extends TestCase {
	public final static String PI_OSGI_TESTS = "org.eclipse.osgi.tests"; //$NON-NLS-1$

	/**
	 * AllTests constructor.
	 */
	public AutomatedTests() {
		super(null);
	}

	/**
	 * AllTests constructor comment.
	 * @param name java.lang.String
	 */
	public AutomatedTests(String name) {
		super(name);
	}

	public static Test suite() {
		TestSuite suite = new TestSuite(AutomatedTests.class.getName());
		//		suite.addTest(new TestSuite(SimpleTests.class));
		suite.addTest(org.eclipse.osgi.tests.container.AllTests.suite());
		suite.addTest(AllFrameworkHookTests.suite());
		suite.addTest(new TestSuite(InstallTests.class));
		suite.addTest(org.eclipse.osgi.tests.eclipseadaptor.AllTests.suite());
		suite.addTest(org.eclipse.osgi.tests.services.resolver.AllTests.suite());
		suite.addTest(DebugOptionsTestCase.suite());
		suite.addTest(org.eclipse.equinox.log.test.AllTests.suite());
		suite.addTest(org.eclipse.osgi.tests.security.SecurityTestSuite.suite());
		suite.addTest(org.eclipse.osgi.tests.appadmin.AllTests.suite());
		suite.addTest(new TestSuite(ExceptionHandlerTests.class));
		suite.addTest(org.eclipse.osgi.tests.configuration.AllTests.suite());
		suite.addTest(org.eclipse.osgi.tests.services.datalocation.AllTests.suite());
		suite.addTest(org.eclipse.osgi.tests.util.AllTests.suite());
		suite.addTest(MiscTests.suite());
		suite.addTest(BundleTests.suite());
		suite.addTest(ServiceRegistryTests.suite());
		suite.addTest(EventManagerTests.suite());
		suite.addTest(FilterTests.suite());
		suite.addTest(PermissionTests.suite());
		suite.addTest(org.eclipse.osgi.tests.securityadmin.AllSecurityAdminTests.suite());
		suite.addTest(org.eclipse.osgi.tests.resource.AllTests.suite());
		return suite;
	}
}
