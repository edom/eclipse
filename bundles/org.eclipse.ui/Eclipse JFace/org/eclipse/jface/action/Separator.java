package org.eclipse.jface.action;

/*
 * Licensed Materials - Property of IBM,
 * WebSphere Studio Workbench
 * (c) Copyright IBM Corp 1999, 2000
 */
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.*;
import org.eclipse.jface.util.Assert;

/**
 * A separator is a special kind of contribution item which acts
 * as a visual separator and, optionally, acts as a group marker.
 * Unlike group markers, separators do have a visual representation.
 * <p>
 * This class may be instantiated; it is not intended to be 
 * subclassed outside the framework.
 * </p>
 */
public class Separator extends AbstractGroupMarker {
/**
 * Creates a separator which does not start a new group.
 */
public Separator() {
	super();
}
/**
 * Creates a new separator which also defines a new group having the given group name.
 * The group name must not be <code>null</code> or the empty string.
 * The group name is also used as the item id.
 * 
 * @param groupName the group name of the separator
 */
public Separator(String groupName) {
	super(groupName);
}
/* (non-Javadoc)
 * Method declared on IContributionItem.
 * Fills the given menu with a SWT separator MenuItem.
 */
public void fill(Menu menu, int index) {
	if (index >= 0)
		new MenuItem(menu, SWT.SEPARATOR, index);
	else
		new MenuItem(menu, SWT.SEPARATOR);
}
/* (non-Javadoc)
 * Method declared on IContributionItem.
 * Fills the given tool bar with a SWT separator ToolItem.
 */
public void fill(ToolBar toolbar, int index) {
	if (index >= 0)
		new ToolItem(toolbar, SWT.SEPARATOR, index);
	else
		new ToolItem(toolbar, SWT.SEPARATOR);
}
/** 
 * The <code>Separator</code> implementation of this <code>IContributionItem</code> 
 * method returns <code>true</code>
 */
public boolean isSeparator() {
	return true;
}
}
