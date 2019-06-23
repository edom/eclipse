/*******************************************************************************
 * Copyright (c) 2000, 2017 IBM Corporation and others.
 *
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.swt.internal.cocoa;

public class NSTextFieldCell extends NSActionCell {

public NSTextFieldCell() {
	super();
}

public NSTextFieldCell(long id) {
	super(id);
}

public NSTextFieldCell(id id) {
	super(id);
}

public void setPlaceholderString(NSString placeholderString) {
	OS.objc_msgSend(this.id, OS.sel_setPlaceholderString_, placeholderString != null ? placeholderString.id : 0);
}

public void setTextColor(NSColor textColor) {
	OS.objc_msgSend(this.id, OS.sel_setTextColor_, textColor != null ? textColor.id : 0);
}

public NSColor textColor() {
	long result = OS.objc_msgSend(this.id, OS.sel_textColor);
	return result != 0 ? new NSColor(result) : null;
}

}
