package org.eclipse.jface.text.reconciler;

/*
 * Licensed Materials - Property of IBM,
 * WebSphere Studio Workbench
 * (c) Copyright IBM Corp 2000
 */


import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;


/**
 * A reconciling strategy is used by an reconciler to reconcile a model
 * based on text of a particular content type. It provides methods for 
 * incremental as well as non-incremental reconciling.<p>
 * This interface must be implemented by clients. Implementers should be
 * registered with a reconciler in order get involved in the reconciling 
 * process.
 */
public interface IReconcilingStrategy {
	
	/**
	 * Activates non-incremental reconciling. The reconciling strategy is just told
	 * that there are changes and that it should reconcile the given partition of the
	 * document most recently passed into <code>setDocument</code>.
	 *
	 * @param partition the document partition to be reconciled
	 */
	void reconcile(IRegion partition);
	/**
	 * Activates incremental reconciling of the specified dirty region.
	 * As a dirty region might span multiple content types, the segment of the
	 * dirty region which should be investigated is also provided to this 
	 * reconciling strategy. The given regions refer to the document passed into
	 * the most recent call of <code>setDocument</code>.
	 *
	 * @param dirtyRegion the document region which has been changed
	 * @param subRegion the sub region in the dirty region which should be reconciled 
	 */
	void reconcile(DirtyRegion dirtyRegion, IRegion subRegion);
	/**
	 * Tells this reconciling strategy on which document it will
	 * work. This method will be called before any other method 
	 * and can be called multiple times. The regions passed to the
	 * other methods always refer to the most recent document 
	 * passed into this method.
	 *
	 * @param document the document on which this strategy will work
	 */
	void setDocument(IDocument document);
}
