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

package org.eclipse.ui.internal.menus;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.CoolBar;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.internal.WorkbenchPlugin;
import org.eclipse.ui.menus.AbstractWorkbenchTrimWidget;
import org.eclipse.ui.menus.IWorkbenchWidget;

/**
 * <p>
 * A proxy for a widget that has been defined in XML. This delays the class
 * loading until the widget is really asked to fill a menu collection. Asking
 * the widget for anything will instantiate the class.
 * </p>
 *
 * @since 3.2
 */
final class WidgetProxy implements IWorkbenchWidget {

	/**
	 * Used to determine whether the load has been tried to prevent multiple retries
	 * at a failed load.
	 */
	private boolean firstLoad = true;

	/**
	 * The configuration element from which the widget can be created. This value
	 * will exist until the element is converted into a real class -- at which point
	 * this value will be set to <code>null</code>.
	 */
	private IConfigurationElement configurationElement;

	/**
	 * The real widget. This value is <code>null</code> until the proxy is forced to
	 * load the real widget. At this point, the configuration element is converted,
	 * nulled out, and this widget gains a reference.
	 */
	private IWorkbenchWidget widget = null;

	/**
	 * The name of the configuration element attribute which contains the
	 * information necessary to instantiate the real widget.
	 */
	private final String widgetAttributeName;

	/**
	 * Constructs a new instance of <code>WidgetProxy</code> with all the
	 * information it needs to try to load the class at a later point in time.
	 *
	 * @param configurationElement The configuration element from which the real
	 *                             class can be loaded at run-time; must not be
	 *                             <code>null</code>.
	 * @param widgetAttributeName  The name of the attibute or element containing
	 *                             the widget executable extension; must not be
	 *                             <code>null</code>.
	 */
	public WidgetProxy(final IConfigurationElement configurationElement, final String widgetAttributeName) {
		if (configurationElement == null) {
			throw new NullPointerException("The configuration element backing a widget proxy cannot be null"); //$NON-NLS-1$
		}

		if (widgetAttributeName == null) {
			throw new NullPointerException("The attribute containing the widget class must be known"); //$NON-NLS-1$
		}

		this.configurationElement = configurationElement;
		this.widgetAttributeName = widgetAttributeName;
	}

	@Override
	public void dispose() {
		if (loadWidget()) {
			widget.dispose();
		}
	}

	@Override
	public void fill(final Composite parent) {
		if (loadWidget()) {
			widget.fill(parent);
		}
	}

	@Override
	public void fill(final CoolBar parent, final int index) {
		if (loadWidget()) {
			widget.fill(parent, index);
		}
	}

	@Override
	public void fill(final Menu parent, final int index) {
		if (loadWidget()) {
			widget.fill(parent, index);
		}
	}

	@Override
	public void fill(final ToolBar parent, final int index) {
		if (loadWidget()) {
			widget.fill(parent, index);
		}
	}

	@Override
	public void init(IWorkbenchWindow workbenchWindow) {
		if (loadWidget()) {
			widget.init(workbenchWindow);
		}
	}

	/**
	 * Convenience method that allows the trim layout manager to inform widgets if
	 * they have changed locations. If the IWidget implementation does not support
	 * the method then we default to using the simpler
	 * <code>fill(final Composite parent)</code>.
	 *
	 * @param parent  The composite to create the controls in
	 * @param oldSide The side the trim was previously displayed on
	 * @param newSide The new side that the trim will be displayed on
	 */
	public void fill(Composite parent, int oldSide, int newSide) {
		if (loadWidget()) {
			if (isMoveableTrimWidget()) {
				((AbstractWorkbenchTrimWidget) widget).fill(parent, oldSide, newSide);
			} else {
				widget.fill(parent);
			}
		}
	}

	/**
	 * Loads the widget, if possible. If the widget is loaded, then the member
	 * variables are updated accordingly.
	 *
	 * @return <code>true</code> if the widget is now non-null; <code>false</code>
	 *         otherwise.
	 */
	private boolean loadWidget() {
		if (firstLoad) {
			// Load the handler.
			try {
				widget = (IWorkbenchWidget) configurationElement.createExecutableExtension(widgetAttributeName);
				configurationElement = null;
			} catch (final ClassCastException e) {
				final String message = "The proxied widget was the wrong class"; //$NON-NLS-1$
				final IStatus status = new Status(IStatus.ERROR, WorkbenchPlugin.PI_WORKBENCH, 0, message, e);
				WorkbenchPlugin.log(message, status);

			} catch (final CoreException e) {
				final String message = "The proxied widget for '" //$NON-NLS-1$
						+ configurationElement.getAttribute(widgetAttributeName) + "' could not be loaded"; //$NON-NLS-1$
				IStatus status = new Status(IStatus.ERROR, WorkbenchPlugin.PI_WORKBENCH, 0, message, e);
				WorkbenchPlugin.log(message, status);
			}
		}

		// We're througth the first load
		firstLoad = false;

		// the load only succeeded if there's a widget..
		return widget != null;
	}

	/**
	 * Determine if the widget knows how to respond to changes in the workbench
	 * 'side' that it is being displayed on.
	 *
	 * @return <code>true</code> iff the <code>IWidget</code> implementation is
	 *         actually based on <code>AbstractTrimWidget</code>
	 */
	private boolean isMoveableTrimWidget() {
		if (loadWidget()) {
			return widget instanceof AbstractWorkbenchTrimWidget;
		}

		return false;
	}

	@Override
	public String toString() {
		if (widget == null) {
			return configurationElement.getAttribute(widgetAttributeName);
		}

		return widget.toString();
	}
}
