package org.eclipse.jface.action;

/*
 * Licensed Materials - Property of IBM,
 * WebSphere Studio Workbench
 * (c) Copyright IBM Corp 1999, 2000
 */
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.*;
import org.eclipse.swt.widgets.*;
import org.eclipse.swt.graphics.*;
import java.util.*;

/**
 * A tool bar manager is a contribution manager which realizes itself and its items
 * in a tool bar control.
 * <p>
 * This class may be instantiated; it may also be subclassed if a more
 * sophisticated layout is required.
 * </p>
 */
public class ToolBarManager extends ContributionManager implements IToolBarManager {

	/** 
	 * The tool bar items style; <code>SWT.NONE</code> by default.
	 */
	private int itemStyle = SWT.NONE;

	/** 
	 * The tool bat control; <code>null</code> before creation
	 * and after disposal.
	 */
	private ToolBar toolBar = null;	
/**
 * Creates a new tool bar manager with the default SWT button style.
 * Use the <code>createControl</code> method to create the 
 * tool bar control.
 */
public ToolBarManager() {
}
/**
 * Creates a tool bar manager with the given SWT button style.
 * Use the <code>createControl</code> method to create the 
 * tool bar control.
 *
 * @param style the tool bar item style
 * @see org.eclipse.swt.widgets.ToolBar#ToolBar for valid style bits
 */
public ToolBarManager(int style) {
	itemStyle= style;
}
/**
 * Creates a tool bar manager for an existing tool bar control.
 * This manager becomes responsible for the control, and will
 * dispose of it when the manager is disposed.
 *
 * @param toolbar the tool bar control
 */
public ToolBarManager(ToolBar toolbar) {
	this();
	this.toolBar = toolbar;
}
/**
 * Creates and returns this manager's tool bar control. 
 * Does not create a new control if one already exists.
 *
 * @param parent the parent control
 * @return the tool bar control
 */
public ToolBar createControl(Composite parent) {
	if (toolBar == null && parent != null) {
		toolBar = new ToolBar(parent, itemStyle);
		update(false);
	}
	return toolBar;
}
/**
 * Disposes of this tool bar manager and frees all allocated SWT resources.
 * Note that this method does not clean up references between this tool bar 
 * manager and its associated contribution items.
 * Use <code>removeAll</code> for that purpose.
 */
public void dispose() {
	if (toolBar != null) {
		toolBar.dispose();
		toolBar = null;
	}
}
/**
 * Returns the tool bar control for this manager.
 *
 * @return the tool bar control, or <code>null</code>
 *  if none (before creating or after disposal)
 */
public ToolBar getControl() {
	return toolBar;
}
/**
 * Re-lays out the tool bar.
 * <p>
 * The default implementation of this framework method re-lays out
 * the parent when the number of items crosses the zero threshold. 
 * Subclasses should override this method to implement their own 
 * re-layout strategy
 *
 * @param toolBar the tool bar control
 * @param oldCount the old number of items
 * @param newCount the new number of items
 */
protected void relayout(ToolBar toolBar, int oldCount, int newCount) {
	if ((oldCount == 0) != (newCount == 0))
		toolBar.getParent().layout();
}
/* (non-Javadoc)
 * Method declared on IContributionManager.
 */
public void update(boolean force) {

	long startTime= 0;
//	if (DEBUG) {
//		dumpStatistics();
//		startTime= (new Date()).getTime();
//	}	
				
	if (isDirty() || force) {
		
		if (toolBar != null) {
		
			int oldCount= toolBar.getItemCount();

			// clean contains all active items without double separators
			IContributionItem[] items= getItems();
			ArrayList clean= new ArrayList(items.length);
			IContributionItem separator= null;
			long cleanStartTime= 0;
//			if (DEBUG) {
//				cleanStartTime= (new Date()).getTime(); 
//			}
			for (int i = 0; i < items.length; ++i) {
				IContributionItem ci= items[i];
				if (!ci.isVisible())
					continue;
				if (ci.isSeparator()) {
					// delay creation until necessary 
					// (handles both adjacent separators, and separator at end)
					separator= ci;
				} else {
					if (separator != null) {
						if (clean.size() > 0)	// no separator if first item
							clean.add(separator);
						separator= null;
					}
					clean.add(ci);
				}
			}
//			if (DEBUG) {
//				System.out.println("   Time needed to build clean vector: " + ((new Date()).getTime() - cleanStartTime));
//			}
			
			// remove obsolete (removed or non active)
			Item[] mi= toolBar.getItems();
			for (int i= 0; i < mi.length; i++) {
				Object data= mi[i].getData();
				if (data == null || !clean.contains(data) ||
						(data instanceof IContributionItem && ((IContributionItem)data).isDynamic()))
					mi[i].dispose();
			}

			// add new
			IContributionItem src, dest;
			mi= toolBar.getItems();
			int srcIx= 0;
			int destIx= 0;
			for (Iterator e = clean.iterator(); e.hasNext();) {
				src= (IContributionItem) e.next();
					
				// get corresponding item in SWT widget
				if (srcIx < mi.length)
					dest= (IContributionItem) mi[srcIx].getData();
				else
					dest= null;
					
				if (dest != null && src.equals(dest)) {
					srcIx++;
					destIx++;
					continue;
				}
				
				if (dest != null && dest.isSeparator() && src.isSeparator()) {
					mi[srcIx].setData(src);
					srcIx++;
					destIx++;
					continue;
				}						
																								
				int start= toolBar.getItemCount();
				src.fill(toolBar, destIx);
				int newItems= toolBar.getItemCount()-start;
				Item[] tis= toolBar.getItems();
				for (int i= 0; i < newItems; i++)
					tis[destIx+i].setData(src);
				destIx+= newItems;
			}

			setDirty(false);
			
			int newCount= toolBar.getItemCount();
			relayout(toolBar, oldCount, newCount);
		}
	}
	
//	if (DEBUG) {
//		System.out.println("   Time needed for update: " + ((new Date()).getTime() - startTime));
//		System.out.println();
//	}		
}
}
