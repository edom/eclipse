/******************************************************************************* 
* Copyright (c) 2009 EclipseSource and others. All rights reserved. This
* program and the accompanying materials are made available under the terms of
* the Eclipse Public License v1.0 which accompanies this distribution, and is
* available at http://www.eclipse.org/legal/epl-v10.html
*
* Contributors:
*   EclipseSource - initial API and implementation
******************************************************************************/
package org.eclipse.equinox.internal.p2.updatesite;

import java.net.URI;
import org.eclipse.core.runtime.*;
import org.eclipse.equinox.internal.provisional.p2.core.ProvisionException;
import org.eclipse.equinox.p2.publisher.IPublisherInfo;
import org.eclipse.equinox.p2.publisher.IPublisherResult;

/**
 * This action parses a category file and publishes all the categories whose 
 * elements are contained in the publisher results.
 */
public class CategoryXMLAction extends SiteXMLAction {

	public CategoryXMLAction(URI location, String categoryQualifier) {
		super(location, categoryQualifier);
	}

	public IStatus perform(IPublisherInfo info, IPublisherResult results, IProgressMonitor monitor) {
		try {
			updateSite = UpdateSite.loadCategoryFile(location, monitor);
		} catch (ProvisionException e) {
			return new Status(IStatus.ERROR, Activator.ID, "Error generating category xml action.", e);
		}
		if (updateSite == null)
			return new Status(IStatus.ERROR, Activator.ID, "Error generating category xml action.");
		return super.perform(info, results, monitor);
	}
}
