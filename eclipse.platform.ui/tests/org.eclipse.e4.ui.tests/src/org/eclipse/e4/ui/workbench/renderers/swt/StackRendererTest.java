/*******************************************************************************
 * Copyright (c) 2013, 2019 IBM Corporation and others.
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
 *     Rolf Theunissen <rolf.theunissen@gmail.com> - Bug 546632
 ******************************************************************************/

package org.eclipse.e4.ui.workbench.renderers.swt;

import static org.junit.Assert.assertEquals;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import javax.inject.Inject;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.ui.internal.workbench.swt.CSSConstants;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.model.application.ui.advanced.MPlaceholder;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.model.application.ui.basic.MPartStack;
import org.eclipse.e4.ui.model.application.ui.basic.MWindow;
import org.eclipse.e4.ui.services.IStylingEngine;
import org.eclipse.e4.ui.services.internal.events.EventBroker;
import org.eclipse.e4.ui.tests.rules.WorkbenchContextRule;
import org.eclipse.e4.ui.workbench.UIEvents;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.eclipse.swt.custom.CTabItem;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

public class StackRendererTest {

	@Rule
	public WorkbenchContextRule contextRule = new WorkbenchContextRule();

	@Inject
	private IEclipseContext context;

	@Inject
	private EModelService ems;

	@Inject
	private MApplication application;

	private MPart part;
	private CTabItemStylingMethodsListener executedMethodsListener;
	private MPartStack partStack;

	@Before
	public void setUp() throws Exception {
		MWindow window = ems.createModelElement(MWindow.class);
		partStack = ems.createModelElement(MPartStack.class);
		part = ems.createModelElement(MPart.class);
		part.setLabel("some title");

		application.getChildren().add(window);
		application.setSelectedElement(window);

		window.getChildren().add(partStack);
		partStack.getChildren().add(part);

		executedMethodsListener = new CTabItemStylingMethodsListener(part);

		context.set(IStylingEngine.class, (IStylingEngine) Proxy.newProxyInstance(getClass().getClassLoader(),
				new Class<?>[] { IStylingEngine.class }, executedMethodsListener));

		contextRule.createAndRunWorkbench(window);
	}

	@Test
	public void testTabStateHandlerWhenOneOfSupportedTagChangeEvents()
			throws Exception {
		// given
		HashMap<String, Object> params = new HashMap<String, Object>();
		params.put(UIEvents.EventTags.ELEMENT, part);
		params.put(UIEvents.EventTags.NEW_VALUE, CSSConstants.CSS_BUSY_CLASS);
		params.put(UIEvents.EventTags.OLD_VALUE, null);

		// when
		context.get(EventBroker.class).send(
				UIEvents.ApplicationElement.TOPIC_TAGS.replace(
						UIEvents.ALL_SUB_TOPICS, UIEvents.EventTypes.SET),
				params);

		// then
		assertEquals(1,
				executedMethodsListener
						.getMethodExecutionCount("setClassnameAndId(.+)"));
	}

	@Test
	public void testTabStateHandlerWhenSelectionChangedEvent() throws Exception {
		// given
		MPlaceholder placeHolder = ems.createModelElement(MPlaceholder.class);
		placeHolder.setRef(part);

		HashMap<String, Object> params = new HashMap<String, Object>();
		params.put(UIEvents.EventTags.ELEMENT, partStack);
		params.put(UIEvents.EventTags.NEW_VALUE, placeHolder);
		params.put(UIEvents.EventTags.OLD_VALUE, null);

		// when
		context.get(EventBroker.class).send(
				UIEvents.ElementContainer.TOPIC_SELECTEDELEMENT.replace(
						UIEvents.ALL_SUB_TOPICS, UIEvents.EventTypes.SET),
				params);

		// then
		assertEquals(1,
				executedMethodsListener
						.getMethodExecutionCount("setClassnameAndId(.+)"));
	}

	// helper functions
	private static class CTabItemStylingMethodsListener implements
			InvocationHandler {
		private MPart part;
		private List<String> methods;

		public CTabItemStylingMethodsListener(MPart part) {
			this.part = part;
			methods = new ArrayList<String>();
		}

		@Override
		public Object invoke(Object proxy, Method method, Object[] args)
				throws Throwable {
			if (isTabItemForPart(args[0])) {
				methods.add(String.format("%s(%s)", method.getName(),
						Arrays.toString(args)));
			}
			return null;
		}

		private boolean isTabItemForPart(Object obj) {
			return obj instanceof CTabItem
					&& part.getLabel().equals(((CTabItem) obj).getText());
		}

		public int getMethodExecutionCount(String methodPattern) {
			int result = 0;
			for (String method : methods) {
				if (method.matches(methodPattern)) {
					result++;
				}
			}
			return result;
		}
	}
}