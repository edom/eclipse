/*******************************************************************************
 * Copyright (C) 2012, 2015 Robin Rosenberg <robin.rosenberg@dewire.com>
 *
 *
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Lars Vogel <Lars.Vogel@gmail.com> - Bug 430694
 *******************************************************************************/
package org.eclipse.ui.internal.ide;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourceAttributes;
import org.eclipse.core.resources.mapping.ResourceMapping;
import org.eclipse.core.runtime.Adapters;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.IDecoration;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ILightweightLabelDecorator;
import org.eclipse.ui.plugin.AbstractUIPlugin;

/**
 * Decorate symbolic links
 * @since 3.8.200
 */
public class SymlinkDecorator implements ILightweightLabelDecorator {

	private static ImageDescriptor SYMLINK;

	static {
		SYMLINK = AbstractUIPlugin.imageDescriptorFromPlugin(
				IDEWorkbenchPlugin.IDE_WORKBENCH,
				"$nl$/icons/full/ovr16/symlink_ovr.png"); //$NON-NLS-1$

	}

	@Override
	public void addListener(ILabelProviderListener listener) {
		// empty
	}

	@Override
	public void dispose() {
	}

	@Override
	public boolean isLabelProperty(Object element, String property) {
		return false;
	}

	@Override
	public void removeListener(ILabelProviderListener listener) {
		// empty
	}

	@Override
	public void decorate(Object element, IDecoration decoration) {
		if (element instanceof ResourceMapping)
			element = ((ResourceMapping) element).getModelObject();

		IResource resource = Adapters.adapt(element, IResource.class);
		if (resource != null) {
			ResourceAttributes resourceAttributes = resource.getResourceAttributes();
			if (resourceAttributes != null && resourceAttributes.isSymbolicLink())
				decoration.addOverlay(SYMLINK);
		}
	}
}
