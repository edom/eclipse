/*******************************************************************************
 * Copyright (c) 2000, 2008 IBM Corporation and others.
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
package org.eclipse.ui.tests.api;

import junit.framework.TestCase;

import org.eclipse.ui.IEditorDescriptor;
import org.eclipse.ui.IFileEditorMapping;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.tests.harness.util.ArrayUtil;

public class IFileEditorMappingTest extends TestCase {
	private IFileEditorMapping[] fMappings;

	public IFileEditorMappingTest(String testName) {
		super(testName);
	}

	@Override
	public void setUp() {
		fMappings = PlatformUI.getWorkbench().getEditorRegistry()
				.getFileEditorMappings();
	}

	public void testGetName() throws Throwable {
		for (IFileEditorMapping fMapping : fMappings) {
			assertNotNull(fMapping.getName());
		}
	}

	public void testGetLabel() throws Throwable {
		String label;
		for (IFileEditorMapping fMapping : fMappings) {
			label = fMapping.getLabel();
			assertNotNull(label);
			assertEquals(label, fMapping.getName() + "."
					+ fMapping.getExtension());
		}
	}

	public void testGetExtension() throws Throwable {
		for (IFileEditorMapping fMapping : fMappings) {
			assertNotNull(fMapping.getExtension());
		}
	}

	public void testGetEditors() throws Throwable {
		IEditorDescriptor[] editors;

		for (IFileEditorMapping fMapping : fMappings) {
			editors = fMapping.getEditors();
			assertTrue(ArrayUtil.checkNotNull(editors) == true);
		}
	}

	public void testGetImageDescriptor() throws Throwable {
		for (IFileEditorMapping fMapping : fMappings) {
			assertNotNull(fMapping.getImageDescriptor());
		}
	}

	//how do i set the default editor?
	public void testGetDefaultEditor() throws Throwable {
		/*		for( int i = 0; i < fMappings.length; i ++ )
		 assertNotNull( fMappings[ i ].getDefaultEditor() );*/
	}
}