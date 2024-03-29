/*******************************************************************************
 *  Copyright (c) 2000, 2017 IBM Corporation and others.
 *
 *  This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License 2.0
 *  which accompanies this distribution, and is available at
 *  https://www.eclipse.org/legal/epl-2.0/
 *
 *  SPDX-License-Identifier: EPL-2.0
 *
 *  Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.pde.core.target;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.osgi.util.NLS;
import org.eclipse.pde.internal.core.ExternalFeatureModelManager;
import org.eclipse.pde.internal.core.ICoreConstants;
import org.eclipse.pde.internal.core.PDECore;
import org.eclipse.pde.internal.core.ifeature.IFeature;
import org.eclipse.pde.internal.core.ifeature.IFeatureChild;
import org.eclipse.pde.internal.core.ifeature.IFeatureImport;
import org.eclipse.pde.internal.core.ifeature.IFeatureModel;
import org.eclipse.pde.internal.core.ifeature.IFeaturePlugin;
import org.eclipse.pde.internal.core.target.Messages;

/**
 * Describes a single feature in a target definition.
 *
 * @since 3.8
 */
public class TargetFeature {

	private IFeatureModel featureModel;

	/**
	 * Constructs a target feature for a feature on the local filesystem. The
	 * file may point at the feature.xml or a folder containing the feature.xml.
	 * The feature.xml will be read to collect the information about the feature.
	 *
	 * @param featureLocation the location of the feature (feature.xml or directory containing it)
	 * @throws CoreException if there is a problem opening the feature.xml
	 */
	public TargetFeature(File featureLocation) throws CoreException {
		initialize(featureLocation);
	}

	/**
	 * Returns the id of this feature or <code>null</code> if no id is set.
	 *
	 * @return id or <code>null</code>
	 */
	public String getId() {
		if (featureModel == null) {
			return null;
		}
		return featureModel.getFeature().getId();
	}

	/**
	 * Returns the version of this feature or <code>null</code> if no version is set.
	 *
	 * @return version or <code>null</code>
	 */
	public String getVersion() {
		if (featureModel == null) {
			return null;
		}
		return featureModel.getFeature().getVersion();
	}

	/**
	 * Returns the string path to the directory containing the feature.xml or
	 * <code>null</code> if no install location is known.
	 *
	 * @return install location path or <code>null</code>
	 */
	public String getLocation() {
		if (featureModel == null) {
			return null;
		}
		return featureModel.getInstallLocation();
	}

	/**
	 * Returns a list of name version descriptor that describes the set of
	 * plug-ins that this feature includes.
	 *
	 * @return a list of name version descriptors, possibly empty
	 */
	public NameVersionDescriptor[] getPlugins() {
		IFeaturePlugin[] plugins = featureModel.getFeature().getPlugins();
		NameVersionDescriptor[] result = new NameVersionDescriptor[plugins.length];
		for (int i = 0; i < plugins.length; i++) {
			result[i] = new NameVersionDescriptor(plugins[i].getId(), plugins[i].getVersion());
		}
		return result;
	}

	/**
	 * Returns a list of name version descriptors that describe the set of features
	 * that this feature depends on as imports or included features.
	 *
	 * @return a list of name version descriptors, possibly empty
	 */
	public NameVersionDescriptor[] getDependentFeatures() {
		List<NameVersionDescriptor> result = new ArrayList<>();
		IFeature feature = featureModel.getFeature();
		IFeatureImport[] featureImports = feature.getImports();
		for (IFeatureImport featureImport : featureImports) {
			if (featureImport.getType() == IFeatureImport.FEATURE) {
				result.add(new NameVersionDescriptor(featureImport.getId(), null, NameVersionDescriptor.TYPE_FEATURE));
			}
		}
		IFeatureChild[] featureIncludes = feature.getIncludedFeatures();
		for (IFeatureChild featureInclude : featureIncludes) {
			result.add(new NameVersionDescriptor(featureInclude.getId(), null, NameVersionDescriptor.TYPE_FEATURE));
		}
		return result.toArray(new NameVersionDescriptor[result.size()]);
	}

	/**
	 * Initializes the content of this target feature by reading the feature.xml
	 *
	 * @param file feature.xml or directory containing it
	 */
	private void initialize(File file) throws CoreException {
		if (file == null || !file.exists()) {
			throw new CoreException(new Status(IStatus.ERROR, PDECore.PLUGIN_ID, NLS.bind(Messages.TargetFeature_FileDoesNotExist, file)));
		}
		File featureXML;
		if (ICoreConstants.FEATURE_FILENAME_DESCRIPTOR.equalsIgnoreCase(file.getName())) {
			featureXML = file;
		} else {
			featureXML = new File(file, ICoreConstants.FEATURE_FILENAME_DESCRIPTOR);
			if (!featureXML.exists()) {
				throw new CoreException(new Status(IStatus.ERROR, PDECore.PLUGIN_ID, NLS.bind(Messages.TargetFeature_FileDoesNotExist, featureXML)));
			}
		}
		featureModel = ExternalFeatureModelManager.createModel(featureXML);
	}

	@Override
	public String toString() {
		StringBuilder result = new StringBuilder();
		result.append(getId()).append(' ').append(getVersion()).append(" (Feature)"); //$NON-NLS-1$
		return result.toString();
	}

}
