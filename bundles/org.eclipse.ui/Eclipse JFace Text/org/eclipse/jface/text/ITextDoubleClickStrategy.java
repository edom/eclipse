package org.eclipse.jface.text;

/*
 * Licensed Materials - Property of IBM,
 * WebSphere Studio Workbench
 * (c) Copyright IBM Corp 1999, 2000
 */


/**
 * A text double click strategy defines the reaction of a text viewer
 * to mouse double click events. For that the strategy must be installed
 * on the text viewer.<p>
 * Clients may implements this interface or use the standard implementation
 * <code>DefaultTextDoubleClickStrategy</code>.
 *
 * @see ITextViewer
 * @see org.eclipse.swt.events.MouseListener#mouseDoubleClick(MouseEvent)
 */
public interface ITextDoubleClickStrategy {

	/**
	 * The mouse has been double clicked on the given text viewer.
	 *
	 * @param viewer the viewer into which has been double clicked
	 */
	void doubleClicked(ITextViewer viewer);
}
