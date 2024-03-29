/*******************************************************************************
 * Copyright (c) 2007, 2018 compeople AG and others.
 *
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 * 	compeople AG (Stefan Liebig) - initial API and implementation
 * 	IBM Corporation - ongoing development
 *******************************************************************************/
package org.eclipse.equinox.internal.p2.artifact.processors;

import java.net.URI;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.equinox.internal.p2.artifact.optimizers.AbstractDeltaStep;
import org.eclipse.equinox.p2.core.IProvisioningAgent;
import org.eclipse.equinox.p2.core.ProvisionException;
import org.eclipse.equinox.p2.repository.IRepositoryManager;
import org.eclipse.equinox.p2.repository.artifact.*;

/**
 * The <code>AbstractDeltaPatchStep</code> is an abstract processing step that
 * retrieves a local artifact repository containing the serialized/encoded
 * artifact key. It assumes that the artifact key is stored within the data
 * property of the processing step descriptor encoding the artifact key of the
 * base artifact.
 */
public abstract class AbstractDeltaProcessorStep extends AbstractDeltaStep {

	@Override
	public void initialize(IProvisioningAgent agent, IProcessingStepDescriptor descriptor,
			IArtifactDescriptor context) {
		super.initialize(agent, descriptor, context);
		if (!getStatus().isOK())
			return;
		fetchLocalArtifactRepository(agent);
	}

	/**
	 * Fetch a local artifact repository containing the fetched artifact key.
	 */
	private void fetchLocalArtifactRepository(IProvisioningAgent agent) {
		if (repository != null)
			return;
		IArtifactRepositoryManager repoMgr = (IArtifactRepositoryManager) agent
				.getService(IArtifactRepositoryManager.SERVICE_NAME);
		if (repoMgr == null) {
			setStatus(new Status(IStatus.ERROR, Activator.ID, "Could not get artifact repository manager."));
			return;
		}

		URI[] repositories = repoMgr.getKnownRepositories(IRepositoryManager.REPOSITORIES_LOCAL);
		for (URI repositorie : repositories) {
			try {
				IArtifactRepository currentRepo = repoMgr.loadRepository(repositorie, null);
				if (currentRepo.contains(key)) {
					repository = currentRepo;
					return;
				}
			} catch (ProvisionException e) {
				// just skip unreadable repositories
			}
		}
		setStatus(new Status(IStatus.ERROR, Activator.ID, "No repository available containing key " + key));
	}

}
