/*******************************************************************************
 * Copyright (c) 2000, 2006 IBM Corporation and others.
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
package org.eclipse.ui.tests.harness.util;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;

/**
 * <code>AutomationUtil</code> contains utility methods to mimic key events.
 * Mouse event methods can be added if needed to complete this class.
 */
public class AutomationUtil {

	/**
	 * Method to mimic a key code event on a display.
	 *
	 * @param display
	 *            The display.
	 * @param eventType
	 *            The event type.
	 * @param keyCode
	 *            The key code.
	 */
	public static void performKeyCodeEvent(Display display, int eventType,
			int keyCode) {
		Event event = new Event();
		event.type = eventType;
		event.keyCode = keyCode;
		display.post(event);
	}

	/**
	 * Method to mimic a character event on a display.
	 *
	 * @param display
	 *            The display.
	 * @param eventType
	 *            The event type.
	 * @param character
	 *            The character to mimic.
	 */
	public static void performCharacterEvent(Display display, int eventType,
			char character) {
		Event event = new Event();
		event.type = eventType;
		event.character = character;
		display.post(event);
	}
}