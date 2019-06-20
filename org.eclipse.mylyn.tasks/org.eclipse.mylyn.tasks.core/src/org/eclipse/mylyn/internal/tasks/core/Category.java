/*******************************************************************************
 * Copyright (c) 2010 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.core;

import org.eclipse.core.runtime.PlatformObject;

/**
 * Categories include Tasks, Bugs, Other...
 * 
 * @author Robert Elves
 */
public class Category extends PlatformObject {

	private final String id;

	private final String label;

	private final int rank;

	public Category(String id, String label, int rank) {
		this.id = id;
		this.label = label;
		this.rank = rank;
	}

	public String getId() {
		return id;
	}

	public int compareTo(Object arg0) {
		if (arg0 instanceof Category) {
			return this.getRank() - ((Category) arg0).getRank();
		}
		return 0;
	}

	public int getRank() {
		return rank;
	}

	public String getLabel() {
		return label;
	}

	@Override
	public String toString() {
		return getLabel();
	};

}
