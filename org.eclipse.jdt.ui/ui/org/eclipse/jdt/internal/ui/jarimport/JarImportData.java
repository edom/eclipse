/*******************************************************************************
 * Copyright (c) 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.jdt.internal.ui.jarimport;

import java.net.URI;

import org.eclipse.ltk.core.refactoring.history.RefactoringHistory;

import org.eclipse.jdt.core.IPackageFragmentRoot;

import org.eclipse.jdt.ui.jarpackager.JarPackageData;

/**
 * Modul for a JAR file which includes refactoring information to import.
 * 
 * @since 3.2
 */
public final class JarImportData extends JarPackageData {

	/** The location of the new Jar file to import, or <code>null</code> */
	private URI fJarFileLocation= null;

	/** The package fragment root to update, or <code>null</code> */
	private IPackageFragmentRoot fPackageFragmentRoot= null;

	/** The refactoring history to import, or <code>null</code> */
	private RefactoringHistory fRefactoringHistory= null;

	/**
	 * Returns the package fragment root to update.
	 * 
	 * @return the package fragment root, or <code>null</code>
	 */
	public IPackageFragmentRoot getPackageFragmentRoot() {
		return fPackageFragmentRoot;
	}

	/**
	 * Returns the location of the new jar file.
	 * 
	 * @return the location of the new jar file, or <code>null</code>
	 */
	public URI getRefactoringFileLocation() {
		return fJarFileLocation;
	}

	/**
	 * Returns the refactoring history.
	 * 
	 * @return the refactoring history, or <code>null</code>
	 */
	public RefactoringHistory getRefactoringHistory() {
		return fRefactoringHistory;
	}

	/**
	 * Sets the package fragment root to update.
	 * 
	 * @param root
	 *            the package fragment root to set, or <code>null</code>
	 */
	public void setPackageFragmentRoot(final IPackageFragmentRoot root) {
		fPackageFragmentRoot= root;
	}

	/**
	 * Sets the location of the new jar file.
	 * 
	 * @param uri
	 *            the location of the new jar file, or <code>null</code>
	 */
	public void setRefactoringFileLocation(final URI uri) {
		fJarFileLocation= uri;
	}

	/**
	 * Sets the refactoring history.
	 * 
	 * @param history
	 *            the refactoring history to set, or <code>null</code>
	 */
	public void setRefactoringHistory(final RefactoringHistory history) {
		fRefactoringHistory= history;
	}
}
