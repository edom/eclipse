/*******************************************************************************
 * Copyright (c) 2003, 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
/*
 * Created on Jan 30, 2004
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package org.eclipse.pde.internal.ui.editor.plugin.rows;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.pde.internal.core.ischema.ISchemaAttribute;
import org.eclipse.pde.internal.ui.PDEPlugin;
import org.eclipse.pde.internal.ui.editor.IContextPart;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.widgets.FormToolkit;
/**
 * @author dejan
 * 
 * To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Generation - Code and Comments
 */
public class BooleanAttributeRow extends ExtensionAttributeRow {
	private Button button;
	/**
	 * @param att
	 */
	public BooleanAttributeRow(IContextPart part, ISchemaAttribute att) {
		super(part, att);
	}
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.pde.internal.ui.neweditor.plugin.ExtensionElementEditor#createContents(org.eclipse.swt.widgets.Composite,
	 *      org.eclipse.ui.forms.widgets.FormToolkit, int)
	 */
	public void createContents(Composite parent, FormToolkit toolkit, int span) {
		createLabel(parent, toolkit);
		button = toolkit.createButton(parent, "", SWT.CHECK); //$NON-NLS-1$
		GridData gd = new GridData();
		//gd.horizontalIndent = 10;
		gd.horizontalSpan = span - 1;
		//gd.horizontalSpan = span;
		button.setLayoutData(gd);
		button.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				if (!blockNotification) markDirty();
				updateText();
			}
		});
		button.setEnabled(part.isEditable());
	}
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.pde.internal.ui.neweditor.plugin.ExtensionElementEditor#update(org.eclipse.pde.internal.ui.neweditor.plugin.DummyExtensionElement)
	 */
	protected void update() {
		blockNotification = true;
		String value = getValue();
		boolean state = value != null && value.toLowerCase().equals("true"); //$NON-NLS-1$
		if (value==null) {
			//check the default
			ISchemaAttribute att = getAttribute();
			if (att.getUse()==ISchemaAttribute.DEFAULT) {
				Object dvalue = att.getValue();
				if (dvalue!=null && dvalue.equals("true")) //$NON-NLS-1$
					state = true;
			}
		}
		button.setSelection(state);
		updateText();
		blockNotification=false;
	}
	
	private void updateText() {
		String value = getValue();
		boolean state = button.getSelection();
		if (value!=null)
			button.setText(state?"true":"false"); //$NON-NLS-1$ //$NON-NLS-2$
		else
			button.setText(""); //$NON-NLS-1$
	}
	public void commit() {
		if (dirty && input != null) {
			try {
				input.setAttribute(getName(), button.getSelection()
						? "true" //$NON-NLS-1$
						: "false"); //$NON-NLS-1$
				dirty = false;
			} catch (CoreException e) {
				PDEPlugin.logException(e);
			}
		}
	}
	public void setFocus() {
		button.setFocus();
	}
}