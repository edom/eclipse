/*******************************************************************************
 *  Copyright (c) 2007, 2010 IBM Corporation and others.
 *
 *  This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License 2.0
 *  which accompanies this distribution, and is available at
 *  https://www.eclipse.org/legal/epl-2.0/
 *
 *  SPDX-License-Identifier: EPL-2.0
 * 
 *  Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.equinox.internal.p2.ui.model;

import java.net.URI;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.equinox.p2.repository.IRepository;

/**
 * Interface for elements that represent repositories.
 * 
 * @since 3.4
 */
public interface IRepositoryElement<T> {

	public URI getLocation();

	public String getName();

	public String getDescription();

	public boolean isEnabled();

	public void setEnabled(boolean enabled);

	public IRepository<T> getRepository(IProgressMonitor monitor);
}
