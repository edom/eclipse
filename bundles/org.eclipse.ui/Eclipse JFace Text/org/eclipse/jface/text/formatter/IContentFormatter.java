package org.eclipse.jface.text.formatter;

/*
 * Licensed Materials - Property of IBM,
 * WebSphere Studio Workbench
 * (c) Copyright IBM Corp 2000
 */

import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;


/**
 * The interface of a document content formatter. The formatter formats ranges 
 * within documents. The documents are modified by the formatter.<p>
 * The content formatter is assumed to determine the partitioning of the document 
 * range to be formatted. For each partition, the formatter determines based 
 * on the partition's content type the formatting strategy to be used. Before 
 * the first strategy is activated all strategies are informed about the 
 * start of the formatting process. After that, the formatting strategies are 
 * activated in the sequence defined by the partitioning of the document range to be
 * formatted. It is assumed that a strategy must be finished before the next strategy
 * can be activated. After the last strategy has been finished, all strategies are 
 * informed about the termination of the formatting process.<p>
 * The interface can be implemented by clients. By default, clients use <code>ContentFormatter</code>
 * as the standard implementer of this interface.
 *
 * @see IDocument
 * @see IFormattingStrategy
 */
public interface IContentFormatter {
		
	/**
	 * Formats the given region of the specified document.The formatter may safely 
	 * assume that it is the only subject that modifies the document at this point in time.
	 *
	 * @param document the document to be formatted
	 * @param region the region within the document to be formatted
	 */
	void format(IDocument document, IRegion region);
	/**
	 * Returns the formatting strategy registered for the given content type.
	 *
	 * @param contentType the content type for which to look up the formatting strategy
	 * @return the formatting strategy for the given content type, or
	 *		<code>null</code> if there is no such strategy
	 */
	IFormattingStrategy getFormattingStrategy(String contentType);
}
