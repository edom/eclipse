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

package org.eclipse.ui.views.bookmarkexplorer;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.internal.views.bookmarkexplorer.BookmarkMessages;

/**
 * Opens a properties dialog allowing the user to edit the bookmark's description.
 */
class EditBookmarkAction extends BookmarkAction {

	protected EditBookmarkAction(BookmarkNavigator view) {
		super(view, BookmarkMessages.Properties_text);
		PlatformUI.getWorkbench().getHelpSystem().setHelp(this,
				IBookmarkHelpContextIds.BOOKMARK_PROPERTIES_ACTION);
		setEnabled(false);
	}

	private IMarker marker;

	@Override
	public void run() {
		if (marker != null) {
			editBookmark();
		}
	}

	/**
	 * Sets marker to the current selection if the selection is an instance of
	 * <code>org.eclipse.core.resources.IMarker<code> and the selected marker's
	 * resource is an instance of <code>org.eclipse.core.resources.IFile<code>.
	 * Otherwise sets marker to null.
	 */
	@Override
	public void selectionChanged(IStructuredSelection selection) {
		marker = null;
		setEnabled(false);

		if (selection.size() != 1) {
			return;
		}

		Object o = selection.getFirstElement();
		if (!(o instanceof IMarker)) {
			return;
		}

		IMarker selectedMarker = (IMarker) o;
		IResource resource = selectedMarker.getResource();
		if (resource instanceof IFile) {
			marker = selectedMarker;
			setEnabled(true);
		}
	}

	private void editBookmark() {
		BookmarkPropertiesDialog dialog = new BookmarkPropertiesDialog(
				getView().getSite().getShell());
		dialog.setMarker(marker);
		dialog.open();
	}
}
