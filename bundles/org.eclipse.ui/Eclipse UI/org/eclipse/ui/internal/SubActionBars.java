package org.eclipse.ui.internal;

/*
 * Licensed Materials - Property of IBM,
 * WebSphere Studio Workbench
 * (c) Copyright IBM Corp 2000
 */
import org.eclipse.core.runtime.*;
import org.eclipse.ui.internal.registry.*;
import org.eclipse.ui.*;
import org.eclipse.jface.action.*;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.jface.util.ListenerList;
import org.eclipse.jface.viewers.*;
import org.eclipse.swt.widgets.*;
import java.util.*;

/**
 * <code>PartActionBars</code> is the general implementation for an
 * <code>IActionBars</code>.  
 */
public class SubActionBars implements IActionBars
{
	private IActionBars parent;
	private boolean active = false;
	private Map actionHandlers;
	private SubMenuManager menuMgr;
	private SubStatusLineManager statusLineMgr;
	private SubToolBarManager toolBarMgr;
	private ListenerList propertyChangeListeners = new ListenerList();
	private boolean actionHandlersChanged;

	/** Property constant for changes to action handlers. */
	public static final String P_ACTION_HANDLERS = "org.eclipse.ui.internal.actionHandlers";
/**
 * Construct a new PartActionBars object.
 */
public SubActionBars(IActionBars parent) {
	this.parent = parent;
}
/**
 * Activate the contributions.
 */
public void activate() {
	setActive(true);
}
/**
 * Adds a property change listener.
 * Has no effect if an identical listener is already registered.
 *
 * @param listener a property change listener
 */
public void addPropertyChangeListener(IPropertyChangeListener listener) { 
	propertyChangeListeners.add(listener);
}
/**
 * Clear the global action handlers.
 */
public void clearGlobalActionHandlers() {
	if (actionHandlers != null) {
		actionHandlers.clear();
		actionHandlersChanged = true;
	}
}
/**
 * Deactivate the contributions.
 */
public void deactivate() {
	setActive(false);
}
/**
 * Dispose the contributions.
 */
public void dispose() {
	if (actionHandlers != null)
		actionHandlers.clear();
	if (menuMgr != null)
		menuMgr.removeAll();
	if (statusLineMgr != null)
		statusLineMgr.removeAll();
	if (toolBarMgr != null)
		toolBarMgr.removeAll();
}
/**
 * Notifies any property change listeners that a property has changed.
 * Only listeners registered at the time this method is called are notified.
 *
 * @param event the property change event
 *
 * @see IPropertyChangeListener#propertyChange
 */
protected void firePropertyChange(PropertyChangeEvent event) {
	Object[] listeners = propertyChangeListeners.getListeners();
	for (int i = 0; i < listeners.length; ++i) {
		((IPropertyChangeListener) listeners[i]).propertyChange(event);
	}
}
/**
 * Get the handler for a window action.
 *
 * @param actionID an action ID declared in the registry
 * @return an action handler which implements the action ID, or
 *		<code>null</code> if none is registered.
 */
public IAction getGlobalActionHandler(String actionID) {
	if (actionHandlers == null)
		return null;
	return (IAction) actionHandlers.get(actionID);
}
/**
 * Returns the complete list of active global action handlers.
 * If there are no global action handlers registered return null.
 */
public Map getGlobalActionHandlers() {
	return actionHandlers;
}
/**
 * Returns the abstract menu manager.  If items are added or
 * removed from the manager be sure to call <code>updateActionBars</code>.
 *
 * @return the menu manager
 */
public IMenuManager getMenuManager() {
	if (menuMgr == null) {
		menuMgr = new SubMenuManager(parent.getMenuManager());
		menuMgr.setVisible(active);
	}
	return menuMgr;
}
/**
 * Returns the status line manager.  If items are added or
 * removed from the manager be sure to call <code>updateActionBars</code>.
 *
 * @return the status line manager
 */
public IStatusLineManager getStatusLineManager() {
	if (statusLineMgr == null) {
		statusLineMgr = new SubStatusLineManager(parent.getStatusLineManager());
		statusLineMgr.setVisible(active);
	}
	return statusLineMgr;
}
/**
 * Returns the tool bar manager.  If items are added or
 * removed from the manager be sure to call <code>updateActionBars</code>.
 *
 * @return the tool bar manager
 */
public IToolBarManager getToolBarManager() {
	if (toolBarMgr == null) {
		toolBarMgr = new SubToolBarManager(parent.getToolBarManager());
		toolBarMgr.setVisible(active);
	}
	return toolBarMgr;
}
/**
 * Sets the target part for the action bars.
 * For views this is ignored because each view has its own action vector.
 * For editors this is important because all the action vector is shared by editors of the same type.
 */
public void partChanged(IWorkbenchPart part) {
}
/**
 * Removes the given property change listener.
 * Has no effect if an identical listener is not registered.
 *
 * @param listener a property change listener
 */
public void removePropertyChangeListener(IPropertyChangeListener listener) {
	propertyChangeListeners.remove(listener);
}
/**
 * Activate / Deactivate the contributions.
 */
private void setActive(boolean set) {
	active = set;
	if (menuMgr != null)
		menuMgr.setVisible(set);
	if (statusLineMgr != null)
		statusLineMgr.setVisible(set);
	if (toolBarMgr != null)
		toolBarMgr.setVisible(set);
}
/**
 * Add a handler for a window action.
 *
 * @param actionID an action ID declared in the registry
 * @param handler an action which implements the action ID.  
 *		<code>null</code> may be passed to deregister a handler.
 */
public void setGlobalActionHandler(String actionID, IAction handler) {
	if (handler != null) {
		if (actionHandlers == null)
			actionHandlers = new HashMap(11);
		actionHandlers.put(actionID, handler);
	} else {
		if (actionHandlers != null)
			actionHandlers.remove(actionID);
	}
	actionHandlersChanged = true;
}
/**
 * Commits all UI changes.  This should be called
 * after additions or subtractions have been made to a 
 * menu, status line, or toolbar.
 */
public void updateActionBars() {
	parent.updateActionBars();
	if (actionHandlersChanged) {
		// Doesn't actually pass the old and new values
		firePropertyChange(new PropertyChangeEvent(this, P_ACTION_HANDLERS, null, null));
		actionHandlersChanged = false;
	}
}
}
