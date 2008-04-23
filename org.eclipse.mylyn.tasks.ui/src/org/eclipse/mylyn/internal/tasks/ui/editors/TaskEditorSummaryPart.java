/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui.editors;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.mylyn.tasks.core.data.TaskAttribute;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.VerifyKeyListener;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.widgets.FormToolkit;

/**
 * @author Steffen Pingel
 */
public class TaskEditorSummaryPart extends AbstractTaskEditorPart {

	private class TabVerifyKeyListener implements VerifyKeyListener {

		public void verifyKey(VerifyEvent event) {
			// if there is a tab key, do not "execute" it and instead select the Status control
			if (event.keyCode == SWT.TAB) {
				event.doit = false;
				if (headerComposite != null) {
					headerComposite.setFocus();
				}
			}
		}

	}

	private static final int COLUMN_MARGIN = 6;

	private Composite headerComposite;

	private boolean needsHeader;

	private RichTextAttributeEditor summaryEditor;

	public TaskEditorSummaryPart() {
		setPartName("Summary");
	}

	private void addAttribute(Composite composite, FormToolkit toolkit, TaskAttribute attribute) {
		addAttribute(composite, toolkit, attribute, COLUMN_MARGIN);
	}

	private void addAttribute(Composite composite, FormToolkit toolkit, TaskAttribute attribute, int indent) {
		AbstractAttributeEditor editor = createEditor(attribute);
		if (editor != null) {
			// having editable controls in the header looks odd
			editor.setReadOnly(true);
			editor.setDecorationEnabled(false);

			editor.createLabelControl(composite, toolkit);
			GridDataFactory.defaultsFor(editor.getLabelControl()).indent(indent, 0).applyTo(editor.getLabelControl());
			editor.createControl(composite, toolkit);
			getTaskEditorPage().getAttributeEditorToolkit().adapt(editor);
		}
	}

	private void addSummaryText(Composite composite, FormToolkit toolkit) {
		TaskAttribute attribute = getTaskData().getMappedAttribute(TaskAttribute.SUMMARY);
		if (attribute != null) {
			summaryEditor = new RichTextAttributeEditor(getAttributeManager(), attribute,
					getTaskEditorPage().getTaskRepository(), SWT.SINGLE);
			summaryEditor.createControl(composite, toolkit);
			// FIXME what does this do? 
			//summaryTextViewer.getTextWidget().setIndent(2);
			GridDataFactory.fillDefaults().grab(true, false).applyTo(summaryEditor.getControl());

			getTaskEditorPage().getAttributeEditorToolkit().adapt(summaryEditor);

			// API EDITOR move to RichTextEditor?
			summaryEditor.getViewer().prependVerifyKeyListener(new TabVerifyKeyListener());
		}
	}

	@Override
	public void createControl(Composite parent, FormToolkit toolkit) {
		Composite composite = toolkit.createComposite(parent);
		GridLayout layout = new GridLayout();
		layout.verticalSpacing = 5;
		composite.setLayout(layout);

		addSummaryText(composite, toolkit);

		if (needsHeader()) {
			createHeaderLayout(composite, toolkit);
		}

		toolkit.paintBordersFor(composite);

		setControl(composite);
	}

	/**
	 * @author Raphael Ackermann (modifications) (bug 195514)
	 * @param toolkit
	 */
	protected void createHeaderLayout(Composite composite, FormToolkit toolkit) {
		headerComposite = toolkit.createComposite(composite);
		GridLayout layout = new GridLayout(11, false);
		layout.verticalSpacing = 1;
		layout.marginHeight = 1;
		layout.marginWidth = 1;
		headerComposite.setLayout(layout);

		TaskAttribute statusAtribute = getTaskData().getMappedAttribute(TaskAttribute.STATUS);
		addAttribute(headerComposite, toolkit, statusAtribute, 0);

		TaskAttribute priorityAttribute = getTaskData().getMappedAttribute(TaskAttribute.PRIORITY);
		addAttribute(headerComposite, toolkit, priorityAttribute);

		TaskAttribute keyAttribute = getTaskData().getMappedAttribute(TaskAttribute.TASK_KEY);
		addAttribute(headerComposite, toolkit, keyAttribute);

		TaskAttribute dateCreation = getTaskData().getMappedAttribute(TaskAttribute.DATE_CREATION);
		addAttribute(headerComposite, toolkit, dateCreation);

		TaskAttribute dateModified = getTaskData().getMappedAttribute(TaskAttribute.DATE_MODIFIED);
		addAttribute(headerComposite, toolkit, dateModified);
	}

	public boolean needsHeader() {
		return needsHeader;
	}

	@Override
	public void setFocus() {
		if (summaryEditor != null) {
			summaryEditor.getViewer().getControl().setFocus();
		}
	}

	public void setNeedsHeader(boolean needsHeader) {
		this.needsHeader = needsHeader;
	}

}
