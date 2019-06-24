/*******************************************************************************
 * Copyright (c) 2009, 2010 Tasktop Technologies and others.
 *
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.equinox.internal.p2.ui.discovery.wizards;

import org.eclipse.osgi.util.NLS;

/**
 * @author David Green
 */
public class Messages extends NLS {

	private static final String BUNDLE_NAME = "org.eclipse.equinox.internal.p2.ui.discovery.wizards.messages"; //$NON-NLS-1$

	public static String ConnectorDescriptorToolTip_detailsLink;

	public static String ConnectorDescriptorToolTip_detailsLink_tooltip;

	public static String ConnectorDiscoveryWizard_installProblems;

	public static String InstallConnectorsJob_commaSeparator;

	public static String InstallConnectorsJob_connectorsNotAvailable;

	public static String InstallConnectorsJob_questionProceed;

	public static String InstallConnectorsJob_questionProceed_long;

	public static String InstallConnectorsJob_task_configuring;

	public static String InstallConnectorsJob_unexpectedError_url;

	public static String ConnectorDiscoveryWizardMainPage_connectorDiscovery;

	public static String ConnectorDiscoveryWizardMainPage_filterLabel;

	public static String ConnectorDiscoveryWizardMainPage_message_with_cause;

	public static String ConnectorDiscoveryWizardMainPage_noConnectorsFound;

	public static String ConnectorDiscoveryWizardMainPage_noConnectorsFound_description;

	public static String ConnectorDiscoveryWizardMainPage_pageDescription;

	public static String ConnectorDiscoveryWizardMainPage_provider_and_license;

	public static String ConnectorDiscoveryWizardMainPage_tooltip_showOverview;

	public static String ConnectorDiscoveryWizardMainPage_unexpectedException;

	public static String ConnectorDiscoveryWizardMainPage_warningMessageConnectorUnavailable;

	public static String ConnectorDiscoveryWizardMainPage_warningTitleConnectorUnavailable;

	public static String DiscoveryItem_Connector_already_installed_Error_Message;

	public static String DiscoveryItem_Connector_already_installed_Error_Title;

	public static String DiscoveryItem_Extension_installed;

	public static String DiscoveryViewer_Certification_Label0;

	public static String DiscoveryViewer_Show_Installed;

	public static String DiscoveryWizard_Install_Window_Title;

	public static String PrepareInstallProfileJob_notFoundDescriptorDetail;

	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
		// constructor
	}

}
