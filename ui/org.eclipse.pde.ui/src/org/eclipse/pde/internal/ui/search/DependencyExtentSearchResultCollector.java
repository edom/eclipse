/*******************************************************************************
 * Copyright (c) 2000, 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.pde.internal.ui.search;

import java.io.File;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.pde.core.ISourceObject;
import org.eclipse.pde.core.plugin.IPluginExtensionPoint;
import org.eclipse.pde.core.plugin.IPluginModelBase;
import org.eclipse.pde.core.plugin.IPluginObject;
import org.eclipse.pde.internal.ui.*;
import org.eclipse.search.ui.IActionGroupFactory;
import org.eclipse.search.ui.IGroupByKeyComputer;
import org.eclipse.search.ui.ISearchResultView;
import org.eclipse.search.ui.ISearchResultViewEntry;
import org.eclipse.search.ui.SearchUI;
import org.eclipse.ui.actions.ActionContext;
import org.eclipse.ui.actions.ActionGroup;


public class DependencyExtentSearchResultCollector {
	
	private static final String PAGE_ID = "org.eclipse.pde.internal.ui.search.dependencyExtent"; //$NON-NLS-1$
	
	private static final String KEY_DEPENDENCY = "DependencyExtent.singular"; //$NON-NLS-1$
	private static final String KEY_DEPENDENCIES = "DependencyExtent.plural"; //$NON-NLS-1$
	private static final String KEY_FOUND = "DependencyExtent.found"; //$NON-NLS-1$
	private static final String KEY_SEARCHING = "DependencyExtent.searching"; //$NON-NLS-1$
	
	private int numMatches = 0;
	
	private ISearchResultView resultView;
	private IProgressMonitor monitor;
	private DependencyExtentSearchOperation operation;
	
	class GroupByKeyComputer implements IGroupByKeyComputer {
		public Object computeGroupByKey(IMarker marker) {
			return marker;
		}

	}
	
	class SearchActionGroupFactory implements IActionGroupFactory {
		public ActionGroup createActionGroup(ISearchResultView searchView) {
			return new SearchActionGroup();
		}
	}
	
	class SearchActionGroup extends PluginSearchActionGroup {
		public void fillContextMenu(IMenuManager menu) {
			super.fillContextMenu(menu);
			ActionContext context = getContext();
			IStructuredSelection selection =
				(IStructuredSelection) context.getSelection();
			if (selection.size() == 1) {
				ISearchResultViewEntry entry =
					(ISearchResultViewEntry) selection.getFirstElement();
				menu.add(new ReferencesInPluginAction(entry));
			}
		}
	}
	

	public DependencyExtentSearchResultCollector(DependencyExtentSearchOperation op, IProgressMonitor monitor) {
		this.operation = op;
		this.monitor = monitor;
	}
	
	public void accept(Object match) {
		try {
			IResource resource = operation.getProject();
			
			IMarker marker = resource.createMarker(SearchUI.SEARCH_MARKER);
			if (match instanceof IPluginExtensionPoint) {
				if (match instanceof ISourceObject) {
					marker.setAttribute(
						IMarker.LINE_NUMBER,
						((ISourceObject) match).getStartLine());
				}
				if (((IPluginExtensionPoint)match).getModel().getUnderlyingResource() == null)
					annotateExternalMarker(marker, (IPluginExtensionPoint)match);
			}
	
			resultView.addMatch(null, match, resource, marker);
			numMatches += 1;
			
			String text =
				(numMatches > 1)
					? PDEPlugin.getResourceString(KEY_DEPENDENCIES)
					: PDEPlugin.getResourceString(KEY_DEPENDENCY);
			
			monitor.setTaskName(
				PDEPlugin.getResourceString(KEY_SEARCHING)
					+ " " //$NON-NLS-1$
					+ numMatches
					+ " " //$NON-NLS-1$
					+ text
					+ " " //$NON-NLS-1$
					+ PDEPlugin.getResourceString(KEY_FOUND));
		} catch (CoreException e) {
		}
	}

	public void done() {
		if (resultView != null)	
			resultView.searchFinished();
	}
	
	private void annotateExternalMarker(IMarker marker, IPluginObject match) throws CoreException {
		IPluginModelBase model = match.getPluginModel();
		String path = model.getInstallLocation();
		String manifest =
				model.isFragmentModel()
			? "fragment.xml" //$NON-NLS-1$
			: "plugin.xml"; //$NON-NLS-1$
		String fileName = path + File.separator + manifest;
		marker.setAttribute(IPDEUIConstants.MARKER_SYSTEM_FILE_PATH, fileName);
	}
	
	public void searchStarted() {
		resultView = SearchUI.getSearchResultView();
		resultView.searchStarted(
			new SearchActionGroupFactory(),
			operation.getSingularLabel(),
			operation.getPluralLabel(),
			null,
			PAGE_ID,
			new DependencyExtentLabelProvider(),
			new SearchGoToAction(),
			new GroupByKeyComputer(),
			operation);
	}
	
}
