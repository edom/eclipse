/*******************************************************************************
 * Copyright (c) 2011, 2015 BestSolution.at and others.
 *
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Tom Schindl <tom.schindl@bestsolution.at> - initial API and implementation
 *     IBM Corporation - bug fixes
 *******************************************************************************/
package org.eclipse.e4.ui.di;

/**
 * Widget toolkit abstract to synchronize back into the UI-Thread from other
 * threads
 *
 * @noextend This class is not intended to be subclassed by clients.
 * @since 1.0
 */
public abstract class UISynchronize {
	/**
	 * Executes the runnable on the UI-Thread and blocks until the runnable is
	 * finished
	 *
	 * @param runnable
	 *            the runnable to execute
	 */
	public abstract void syncExec(Runnable runnable);

	/**
	 * Schedules the runnable on the UI-Thread for execution and returns
	 * immediately
	 *
	 * @param runnable
	 *            the runnable to execute
	 */
	public abstract void asyncExec(Runnable runnable);
}
