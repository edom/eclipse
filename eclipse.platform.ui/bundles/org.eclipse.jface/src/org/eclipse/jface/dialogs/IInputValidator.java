/*******************************************************************************
 * Copyright (c) 2000, 2015 IBM Corporation and others.
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
package org.eclipse.jface.dialogs;

/**
 * The IInputValidator is the interface for simple validators.
 * @see org.eclipse.jface.dialogs.InputDialog
 */
public interface IInputValidator {
	/**
	 * Validates the given string.  Returns an error message to display
	 * if the new text is invalid.  Returns <code>null</code> if there
	 * is no error.  Note that the empty string is not treated the same
	 * as <code>null</code>; it indicates an error state but with no message
	 * to display.
	 *
	 * @param newText the text to check for validity
	 *
	 * @return an error message or <code>null</code> if no error
	 */
	public String isValid(String newText);
}
