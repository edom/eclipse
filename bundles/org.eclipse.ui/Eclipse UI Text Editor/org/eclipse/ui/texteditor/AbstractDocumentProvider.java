package org.eclipse.ui.texteditor;

/*
 * Licensed Materials - Property of IBM,
 * WebSphere Studio Workbench
 * (c) Copyright IBM Corp 2000
 */


import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.jface.text.DocumentEvent;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IDocumentListener;
import org.eclipse.jface.text.source.IAnnotationModel;
import org.eclipse.jface.util.Assert;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;



/**
 * An abstract base implementation of a shareable document provider.
 * <p>
 * Subclasses must implement <code>createDocument</code>,
 * <code>createAnnotationModel</code>, and <code>doSaveDocument</code>.
 * </p>
 */
public abstract class AbstractDocumentProvider implements IDocumentProvider {
	
	
		/**
		 * Collection of all information managed for a connected element.
		 */
		protected class ElementInfo implements IDocumentListener {
			
			/** The element for which the info is stored */
			public Object fElement;
			/** How often the element has been connected */
			public int fCount;
			/** Can the element be saved */
			public boolean fCanBeSaved;
			/** The element's document */
			public IDocument fDocument;
			/** The element's annotation model */
			public IAnnotationModel fModel;
			
			
			/**
			 * Creates a new element info, initialized with the given
			 * document and annotation model.
			 *
			 * @param document the document
			 * @param model the annotation model
			 */
			public ElementInfo(IDocument document, IAnnotationModel model) {
				fDocument= document;
				fModel= model;
				fCount= 0;
				fCanBeSaved= false;
			}
			
			/**
			 * An element info equals another object if this object is an element info
			 * and if the documents of the two element infos are equal.
			 * @see Object#equals
			 */
			public boolean equals(Object o) {
				if (o instanceof ElementInfo) {
					ElementInfo e= (ElementInfo) o;
					return fDocument.equals(e.fDocument);
				}
				return false;
			}
			
			/*
			 * @see Object#hashCode
			 */
			public int hashCode() {
				return fDocument.hashCode();
			}
			
			/*
			 * @see IDocumentListener#documentChanged(DocumentEvent)
			 */
			public void documentChanged(DocumentEvent event) {
				fCanBeSaved= true;
				fDocument.removeDocumentListener(this);
				fireElementDirtyStateChanged(fElement, fCanBeSaved);
			}
			
			/*
			 * @see IDocumentListener#documentAboutToBeChanged(DocumentEvent)
			 */
			public void documentAboutToBeChanged(DocumentEvent event) {
			}
		};
	
	/** Information of all connected elements */
	private Map fElementInfoMap= new HashMap();
	/** The element state listeners */
	private List fElementStateListeners= new ArrayList();
	
	
	/**
	 * Creates a new document provider.
	 */
	protected AbstractDocumentProvider() {
	}
	/**
	 * The <code>AbstractDocumentProvider</code> implementation of this 
	 * <code>IDocumentProvider</code> method does nothing. Subclasses may
	 * reimplement.
	 */
	public void aboutToChange(Object element) {
	}
	/*
	 * @see IDocumentProvider#addElementStateListener(IElementStateListener)
	 */
	public void addElementStateListener(IElementStateListener listener) {
		Assert.isNotNull(listener);
		if (!fElementStateListeners.contains(listener))
			fElementStateListeners.add(listener);
	}
	/*
	 * @see IDocumentProvider#canSaveDocument(Object)
	 */
	public boolean canSaveDocument(Object element) {
		
		if (element == null)
			return false;
			
		ElementInfo info= (ElementInfo) fElementInfoMap.get(element);
		return (info != null ? info.fCanBeSaved : false);
	}
	/**
	 * The <code>AbstractDocumentProvider</code> implementation of this 
	 * <code>IDocumentProvider</code> method does nothing. Subclasses may
	 * reimplement.
	 */
	public void changed(Object element) {
	}
	/*
	 * @see IDocumentProvider#connect
	 */
	public final void connect(Object element) throws CoreException {
		ElementInfo info= (ElementInfo) fElementInfoMap.get(element);
		if (info == null) {
			
			info= createElementInfo(element);
			if (info == null) 
				info= new ElementInfo(null, null);
			
			info.fElement= element;
			
			if (info.fDocument != null)
				info.fDocument.addDocumentListener(info);
			
			fElementInfoMap.put(element, info);
		}	
		++ info.fCount;		
	}
	/**
	 * Creates an annotation model for the given element. <p>
	 * Subclasses must implement this method.
	 *
	 * @param element the element
	 * @return the annotation model
	 * @exception CoreException if the annotation model could not be created
	 */
	protected abstract IAnnotationModel createAnnotationModel(Object element) throws CoreException;
	/**
	 * Creates a textual representation for the given element, i.e. the
	 * document for the given element.<p>
	 * Subclasses must implement this method.
	 *
	 * @param element the element
	 * @return the document
	 * @exception CoreException if the document could not be created
	 */
	protected abstract IDocument createDocument(Object element) throws CoreException;
	/**
	 * Creates a new element info object for the given element.<p>
	 * This method is called from <code>connect</code> when an element info needs
	 * to be created. The <code>AbstractDocumentProvider</code> implementation 
	 * of this method returns a new element info object whose document and 
	 * annotation model are the values of <code>createDocument(element)</code> 
	 * and  <code>createAnnotationModel(element)</code>, respectively. Subclasses 
	 * may override.
	 *
	 * @param element the element
	 * @return a new element info object
	 * @exception CoreException if the document or annotation model could not be created
	 */
	protected ElementInfo createElementInfo(Object element) throws CoreException {
		return new ElementInfo(createDocument(element), createAnnotationModel(element));
	}
	/*
	 * @see IDocumentProvider#disconnect
	 */
	public final void disconnect(Object element) {
		ElementInfo info= (ElementInfo) fElementInfoMap.get(element);
		
		if (info == null)
			return;
		
		if (info.fCount == 1) {
			
			if (info.fDocument != null)
				info.fDocument.removeDocumentListener(info);
			
			disposeElementInfo(element, info);
			
			fElementInfoMap.remove(element);
			
		} else
		 	-- info.fCount;
	}
	/**
	 * Disposes of the given element info object. <p>
	 * This method is called when an element info is disposed. The 
	 * <code>AbstractDocumentProvider</code> implementation of this
	 * method does nothing. Subclasses may reimplement.
	 *
	 * @param element the element
	 * @param info the element info object
	 */
	protected void disposeElementInfo(Object element, ElementInfo info) {
	}
	/**
	 * Performs the actual work of saving the given document provided for the 
	 * given element. <p>
	 * Subclasses must implement this method.
	 *
	 * @param monitor a progress monitor to report progress and request cancelation
	 * @param element the element
	 * @param document the document
	 * @exception CoreException if document could not be stored to the given element
	 */
	protected abstract void doSaveDocument(IProgressMonitor monitor, Object element, IDocument document) throws CoreException;
	/**
	 * Informs all registered element state listeners about an impending 
	 * replace of the given element's content.
	 *
	 * @param element the element
	 * @see IElementStateListener#elementContentAboutToBeReplaced
	 */
	protected void fireElementContentAboutToBeReplaced(Object element) {
		Iterator e= new ArrayList(fElementStateListeners).iterator();
		while (e.hasNext()) {
			IElementStateListener l= (IElementStateListener) e.next();
			l.elementContentAboutToBeReplaced(element);
		}
	}
	/**
	 * Informs all registered element state listeners about the just-completed
	 * replace of the given element's content.
	 *
	 * @param element the element
	 * @see IElementStateListener#elementContentReplaced
	 */
	protected void fireElementContentReplaced(Object element) {
		Iterator e= new ArrayList(fElementStateListeners).iterator();
		while (e.hasNext()) {
			IElementStateListener l= (IElementStateListener) e.next();
			l.elementContentReplaced(element);
		}
	}
	/**
	 * Informs all registered element state listeners about the deletion
	 * of the given element.
	 *
	 * @param element the element
	 * @see IElementStateListener#elementDeleted
	 */
	protected void fireElementDeleted(Object element) {
		Iterator e= new ArrayList(fElementStateListeners).iterator();
		while (e.hasNext()) {
			IElementStateListener l= (IElementStateListener) e.next();
			l.elementDeleted(element);
		}
	}
	/**
	 * Informs all registered element state listeners about a change in the
	 * dirty state of the given element.
	 *
	 * @param element the element
	 * @param isDirty the new dirty state
	 * @see IElementStateListener#elementDirtyStateChanged
	 */
	protected void fireElementDirtyStateChanged(Object element, boolean isDirty) {
		Iterator e= new ArrayList(fElementStateListeners).iterator();
		while (e.hasNext()) {
			IElementStateListener l= (IElementStateListener) e.next();
			l.elementDirtyStateChanged(element, isDirty);
		}
	}
	/**
	 * Informs all registered element state listeners about a move.
	 *
	 * @param originalElement the element before the move
	 * @param movedElement the element after the move
	 * @see IElementStateListener#elementMoved
	 */
	protected void fireElementMoved(Object originalElement, Object movedElement) {
		Iterator e= new ArrayList(fElementStateListeners).iterator();
		while (e.hasNext()) {
			IElementStateListener l= (IElementStateListener) e.next();
			l.elementMoved(originalElement, movedElement);
		}
	}
	/*
	 * @see IDocumentProvider#getAnnotationModel
	 */
	public IAnnotationModel getAnnotationModel(Object element) {
		
		if (element == null)
			return null;
			
		ElementInfo info= (ElementInfo) fElementInfoMap.get(element);
		return (info != null ? info.fModel : null);
	}
	/**
	 * Enumerates the elements connected via this document provider.	
	 *
	 * @return the list of elements (element type: <code>Object</code>)
	 */
	protected Iterator getConnectedElements() {
		Set s= new HashSet();
		Set keys= fElementInfoMap.keySet();
		if (keys != null)
			s.addAll(keys);
		return s.iterator();
	}
	/*
	 * @see IDocumentProvider#getDocument
	 */
	public IDocument getDocument(Object element) {
		
		if (element == null)
			return null;
			
		ElementInfo info= (ElementInfo) fElementInfoMap.get(element);
		return (info != null ? info.fDocument : null);
	}
	/**
	 * Returns the element info object for the given element.
	 *
	 * @param element the element
	 * @return the element info object, or <code>null</code> if none
	 */
	protected ElementInfo getElementInfo(Object element) {
		return (ElementInfo) fElementInfoMap.get(element);
	}
	/*
	 * @see IDocumentProvider#mustSaveDocument
	 */
	public boolean mustSaveDocument(Object element) {
		
		if (element == null)
			return false;
			
		ElementInfo info= (ElementInfo) fElementInfoMap.get(element);
		return (info != null ? info.fCount == 1 && info.fCanBeSaved : false);
	}
	/*
	 * @see IDocumentProvider#removeElementStateListener(IElementStateListener)
	 */
	public void removeElementStateListener(IElementStateListener listener) {
		Assert.isNotNull(listener);
		fElementStateListeners.remove(listener);
	}
	/*
	 * @see IDocumentProvider#resetDocument(Object)
	 */
	public void resetDocument(Object element) throws CoreException {
		if (element == null)
			return;
			
		ElementInfo info= (ElementInfo) fElementInfoMap.get(element);
		if (info != null && info.fCanBeSaved) {
			IDocument original= createDocument(element);
			if (original != null) {
				fireElementContentAboutToBeReplaced(element);
				info.fDocument.set(original.get());
				info.fCanBeSaved= false;
				info.fDocument.addDocumentListener(info);
				fireElementContentReplaced(element);
			}
		}
	}
	/*
	 * @see IDocumentProvider#saveDocument(IProgressMonitor, Object, IDocument)
	 */
	public void saveDocument(IProgressMonitor monitor, Object element, IDocument document) throws CoreException {
		
		if (element == null)
			return;
			
		ElementInfo info= (ElementInfo) fElementInfoMap.get(element);
		if (info != null) {
			Assert.isTrue(info.fDocument == document);
			doSaveDocument(monitor, element, document);
			info.fCanBeSaved= false;
			info.fDocument.addDocumentListener(info);
			
			fireElementDirtyStateChanged(element, false);
			
		} else {
			doSaveDocument(monitor, element, document);
		}	
	}
}
