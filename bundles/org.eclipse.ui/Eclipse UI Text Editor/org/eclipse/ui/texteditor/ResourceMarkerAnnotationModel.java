package org.eclipse.ui.texteditor;

/*
 * Licensed Materials - Property of IBM,
 * WebSphere Studio Workbench
 * (c) Copyright IBM Corp 2000
 */


import org.eclipse.jface.util.Assert;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IMarkerDelta;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;



/**
 * A marker annotation model whose underlying source of markers is 
 * a resource in the workspace.
 * <p>
 * This class may be instantiated; it is not intended to be subclassed.
 * </p>
 */
public class ResourceMarkerAnnotationModel extends AbstractMarkerAnnotationModel {


	/**
	 * Internal resource change listener.
	 */
	class ResourceChangeListener implements IResourceChangeListener {
		/*
		 * @see IResourceChangeListener#resourceChanged
		 */
		public void resourceChanged(IResourceChangeEvent e) {
			IResourceDelta delta= e.getDelta();
			try {
				if (delta != null)
					delta.accept(fResourceDeltaVisitor);
			} catch (CoreException x) {
				handleCoreException(x, "ResourceMarkerAnnotationModel.resourceChanged");
			}
		}
	};
	
	/**
	 * Internal resource delta visitor.
	 */
	class ResourceDeltaVisitor implements IResourceDeltaVisitor {
		/*
		 * @see IResourceDeltaVisitor#visit
		 */
		public boolean visit(IResourceDelta delta) throws CoreException {
			if (delta != null && fResource.equals(delta.getResource())) {
				update(delta.getMarkerDeltas());
				return false;
			}
			return true;
		}
	};
	
	/** The workspace */
	private IWorkspace fWorkspace;
	/** The resource */
	private IResource fResource;
	/** The resource change listener */
	private IResourceChangeListener fResourceChangeListener= new ResourceChangeListener();
	/** The resource delta visitor */
	private IResourceDeltaVisitor fResourceDeltaVisitor= new ResourceDeltaVisitor();

	
	/**
	 * Creates a marker annotation model with the given resource as the source
	 * of the markers.
	 *
	 * @param resource the resource
	 */
	public ResourceMarkerAnnotationModel(IResource resource) {
		Assert.isNotNull(resource);
		fResource= resource;
		fWorkspace= resource.getWorkspace();
	}
	/*
	 * @see AbstractMarkerAnnotationModel#deleteMarkers(IMarker[])
	 */
	protected void deleteMarkers(final IMarker[] markers) throws CoreException {
		fWorkspace.run(new IWorkspaceRunnable() {
			public void run(IProgressMonitor monitor) throws CoreException {
				for (int i= 0; i < markers.length; ++i) {
					markers[i].delete();
				}
			}
		}, null);
	}
	/*
	 * @see AnnotationModel#isAcceptable
	 */
	protected boolean isAcceptable(IMarker marker) {
		return marker != null && fResource.equals(marker.getResource());
	}
	/*
	 * @see AbstractMarkerAnnotationModel#listenToMarkerChanges(boolean)
	 */
	protected void listenToMarkerChanges(boolean listen) {
		if (listen)
			fWorkspace.addResourceChangeListener(fResourceChangeListener);
		else
			fWorkspace.removeResourceChangeListener(fResourceChangeListener);
	}
	/*
	 * @see AbstractMarkerAnnotationModel#retrieveMarkers()
	 */
	protected IMarker[] retrieveMarkers() throws CoreException {
		return fResource.findMarkers(IMarker.MARKER, true, IResource.DEPTH_ZERO);
	}
	/**
	 * Updates this model to the given marker deltas.
	 *
	 * @param markerDeltas the list of marker deltas
	 */
	private void update(IMarkerDelta[] markerDeltas) {
		for (int i= 0; i < markerDeltas.length; i++) {
			IMarkerDelta delta= markerDeltas[i];
			switch (delta.getKind()) {
				case IResourceDelta.ADDED :
					addMarkerAnnotation(delta.getMarker());
					break;
				case IResourceDelta.REMOVED :
					removeMarkerAnnotation(delta.getMarker());
					break;
				case IResourceDelta.CHANGED :
					modifyMarkerAnnotation(delta.getMarker());
					break;
			}
		}
		
		fireModelChanged();
	}
}
