/*
 * Created on Jun 26, 2003
 *
 * To change this generated comment go to 
 * Window>Preferences>Java>Code Generation>Code Template
 */
package org.eclipse.pde.internal.runtime.logview;

import org.eclipse.update.ui.forms.internal.ScrollableSectionForm;

/**
 * @author Wassim Melhem
 */
public class SessionDataSection extends BasePreviewSection {
	
	public SessionDataSection(ScrollableSectionForm form) {
		super(form, "Session Data");
		setCollapsed(true);
	}
/*	
	protected int getTextWidthHint() {
		return 200;
	}
	
	protected int getTextStyle() {
		return SWT.WRAP;
	}
*/
	
	protected String getTextFromEntry() {
		return getEntry().getSession().getSessionData();
	}
}
