/*******************************************************************************
 * Copyright (c) 2007, 2015 IBM Corporation and others.
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
 *     Lars Vogel <Lars.Vogel@gmail.com> - Bug 440810
 *******************************************************************************/

package org.eclipse.ui.internal.keys.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.core.commands.common.NotDefinedException;
import org.eclipse.core.commands.contexts.Context;
import org.eclipse.ui.contexts.IContextService;
import org.eclipse.ui.services.IServiceLocator;

/**
 * @since 3.4
 *
 */
public class ContextModel extends CommonModel {
	private static final String CONTEXT_ID_ACTION_SETS = "org.eclipse.ui.contexts.actionSet"; //$NON-NLS-1$
	private static final String CONTEXT_ID_INTERNAL = ".internal."; //$NON-NLS-1$

	public static final String PROP_CONTEXTS = "contexts"; //$NON-NLS-1$
	public static final String PROP_CONTEXT_MAP = "contextIdElementMap"; //$NON-NLS-1$
	private List contexts;
	private Map contextIdToFilteredContexts;
	private Map contextIdToElement;
	private IContextService contextService;

	public ContextModel(KeyController kc) {
		super(kc);
	}

	/**
	 * @param locator
	 */
	public void init(IServiceLocator locator) {
		contextService = locator.getService(IContextService.class);
		contexts = new ArrayList();
		contextIdToFilteredContexts = new HashMap();
		contextIdToElement = new HashMap();

		Context[] definedContexts = contextService.getDefinedContexts();
		for (Context definedContext : definedContexts) {
			ContextElement ce = new ContextElement(controller);
			ce.init(definedContext);
			ce.setParent(this);
			contexts.add(ce);
			contextIdToElement.put(definedContext.getId(), ce);
		}
	}

	/**
	 * @return Returns the contexts.
	 */
	public List getContexts() {
		return contexts;
	}

	/**
	 * @param contexts The contexts to set.
	 */
	public void setContexts(List contexts) {
		List old = this.contexts;
		this.contexts = contexts;
		controller.firePropertyChange(this, PROP_CONTEXTS, old, contexts);
	}

	/**
	 * @return Returns the contextToElement.
	 */
	public Map getContextIdToElement() {
		return contextIdToElement;
	}

	/**
	 * @param contextToElement The contextToElement to set.
	 */
	public void setContextIdToElement(Map contextToElement) {
		Map old = this.contextIdToElement;
		this.contextIdToElement = contextToElement;
		controller.firePropertyChange(this, PROP_CONTEXT_MAP, old, contextToElement);
	}

	/**
	 * Removes any contexts according to the parameters. The contexts are stored in
	 * a {@link List} to they can be easily restored.
	 *
	 * @param actionSets <code>true</code> to filter action set contexts.
	 * @param internal   <code>true</code> to filter internal contexts
	 */
	public void filterContexts(boolean actionSets, boolean internal) {
		// Remove undesired contexts
		for (int i = 0; i < contexts.size(); i++) {
			boolean removeContext = false;
			ContextElement contextElement = (ContextElement) contexts.get(i);

			if (actionSets == true && contextElement.getId().equalsIgnoreCase(CONTEXT_ID_ACTION_SETS)) {
				removeContext = true;
			} else {
				String parentId;
				try {
					parentId = ((Context) contextElement.getModelObject()).getParentId();
					while (parentId != null) {
						if (parentId.equalsIgnoreCase(CONTEXT_ID_ACTION_SETS)) {
							removeContext = true;
						}
						parentId = contextService.getContext(parentId).getParentId();
					}
				} catch (NotDefinedException e) {
					// No parentId to check
				}
			}

			if (internal == true && contextElement.getId().contains(CONTEXT_ID_INTERNAL)) {
				removeContext = true;
			}

			if (removeContext) {
				contextIdToFilteredContexts.put(contextElement.getId(), contextElement);
				contextIdToElement.remove(contextElement);
			}
		}

		contexts.removeAll(contextIdToFilteredContexts.values());

		Iterator iterator = contextIdToFilteredContexts.keySet().iterator();
		// Restore desired contexts
		while (iterator.hasNext()) {
			boolean restoreContext = false;
			ContextElement contextElement = (ContextElement) contextIdToFilteredContexts.get(iterator.next());

			try {
				if (actionSets == false) {
					if (contextElement.getId().equalsIgnoreCase(CONTEXT_ID_ACTION_SETS)) {
						restoreContext = true;
					} else {
						String parentId = ((Context) contextElement.getModelObject()).getParentId();
						if (parentId != null && parentId.equalsIgnoreCase(CONTEXT_ID_ACTION_SETS)) {
							restoreContext = true;
						}
					}
				}
			} catch (NotDefinedException e) {
				// No parentId to check
			}
			if (internal == false && contextElement.getId().contains(CONTEXT_ID_INTERNAL)) {
				restoreContext = true;
			}

			if (restoreContext) {
				contexts.add(contextElement);
				contextIdToElement.put(contextElement.getId(), contextElement);
				iterator.remove();
			}
		}
	}
}
