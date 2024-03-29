/*******************************************************************************
 * Copyright (c) 2006, 2015 IBM Corporation and others.
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
package org.eclipse.ui.internal.quickaccess;

import java.util.Arrays;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.quickaccess.QuickAccessElement;

/**
 * Returns {@link QuickAccessElement}s. It implements a cache by default.
 *
 * @noreference This class is not intended to be referenced by clients.
 */
public abstract class QuickAccessProvider {

	private QuickAccessElement[] sortedElements;

	/**
	 * Returns the unique ID of this provider.
	 *
	 * @return the unique ID
	 */
	public abstract String getId();

	/**
	 * Returns the name of this provider to be displayed to the user.
	 *
	 * @return the name
	 */
	public abstract String getName();

	/**
	 * Returns the image descriptor for this provider.
	 *
	 * @return the image descriptor, or null if not defined
	 */
	public abstract ImageDescriptor getImageDescriptor();

	/**
	 * Returns the elements provided by this provider.
	 *
	 * @return this provider's elements
	 */
	public abstract QuickAccessElement[] getElements();

	public QuickAccessElement[] getElementsSorted() {
		if (sortedElements == null) {
			sortedElements = getElements();
			Arrays.sort(sortedElements, (e1, e2) -> e1.getSortLabel().compareTo(e2.getSortLabel()));
		}
		return sortedElements;
	}

	/**
	 * Returns the element for the given ID if available, or null if no matching
	 * element is available.
	 *
	 * @param id the ID of an element
	 * @return the element with the given ID, or null if not found.
	 */
	public QuickAccessElement getElementForId(String id) {
		if (id == null) {
			return null;
		}
		if (sortedElements != null) {
			for (QuickAccessElement element : sortedElements) {
				if (id.equals(element.getId())) {
					return element;
				}
			}
		}
		return null;
	}

	public boolean isAlwaysPresent() {
		return false;
	}

	/**
	 * Resets the cache, so next invocation of {@link #getElements()} and related
	 * method will retrigger computation of elements.
	 */
	public final void reset() {
		sortedElements = null;
		doReset();
	}

	/**
	 * Additional operations to reset cache.
	 *
	 * @noreference This method is not intended to be referenced by clients. Use
	 *              {@link #reset()} instead.
	 */
	protected abstract void doReset();

	/**
	 * @return {@code true} if this provider requires UI operations to load its
	 *         elements, {@code false} otherwise.
	 */
	public boolean requiresUiAccess() {
		return false;
	}
}
