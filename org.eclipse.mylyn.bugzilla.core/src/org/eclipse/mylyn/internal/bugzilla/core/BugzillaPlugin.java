/*******************************************************************************
 * Copyright (c) 2003 - 2006 University Of British Columbia and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     University Of British Columbia - initial API and implementation
 *******************************************************************************/
package org.eclipse.mylar.internal.bugzilla.core;

import java.io.IOException;
import java.net.Proxy;
import java.net.URL;
import java.net.URLConnection;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.mylar.bugzilla.core.BugzillaReport;
import org.eclipse.mylar.internal.bugzilla.core.internal.OfflineReportsFile;
import org.eclipse.mylar.internal.bugzilla.core.internal.RepositoryConfiguration;
import org.eclipse.mylar.internal.bugzilla.core.internal.RepositoryConfigurationFactory;
import org.eclipse.mylar.internal.bugzilla.core.search.IBugzillaResultEditorMatchAdapter;
import org.eclipse.mylar.internal.core.util.MylarStatusHandler;
import org.eclipse.mylar.provisional.tasklist.TaskRepository;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * The main plugin class to be used in the desktop.
 * 
 * @author Mik Kersten (added support for multiple repositories)
 */
public class BugzillaPlugin extends AbstractUIPlugin {

	public static final String REPOSITORY_KIND = "bugzilla";

	public static final String ENCODING_UTF_8 = "UTF-8";

	/** Singleton instance of the plug-in */
	private static BugzillaPlugin plugin;

//	/** The file that contains all of the bugzilla favorites */
//	private FavoritesFile favoritesFile;

	/** The file that contains all of the offline bug reports */
	private OfflineReportsFile offlineReportsFile;

	/** Product configuration for the current server */
	private Map<String, RepositoryConfiguration> repositoryConfigurations = new HashMap<String, RepositoryConfiguration>();

	/**
	 * Constructor
	 * 
	 * @param descriptor
	 *            passed in when the plugin is loaded
	 */
	public BugzillaPlugin() {
		super();
	}

	/**
	 * Get the singleton instance for the plugin
	 * 
	 * @return The instance of the plugin
	 */
	public static BugzillaPlugin getDefault() {
		return plugin;
	}

	@Override
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;

//		readFavoritesFile();
		readOfflineReportsFile();
	}

	@Override
	public void stop(BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);
	}

//	/**
//	 * Get the favorites file contatining the favorites
//	 * 
//	 * @return The FavoritesFile
//	 */
//	public FavoritesFile getFavorites() {
//		return favoritesFile;
//	}

	/**
	 * Get the OfflineReports file contatining the offline bug reports
	 * 
	 * @return The OfflineReportsFile
	 */
	public OfflineReportsFile getOfflineReports() {
		return offlineReportsFile;
	}

	@Override
	protected void initializeDefaultPreferences(IPreferenceStore store) {
		BugzillaPreferencePage.initDefaults(store);
	}

	// /**
	// * // * Get the name of the bugzilla server // * // *
	// *
	// * @return A string containing the prefered name of the bugzilla server //
	// */
	// // public String getServerName() {
	// // return
	// //
	// plugin.getPreferenceStore().getString(IBugzillaConstants.BUGZILLA_SERVER);
	// // }
	// public boolean isServerCompatability218() {
	// return
	// IBugzillaConstants.SERVER_218.equals(getPreferenceStore().getString(IBugzillaConstants.SERVER_VERSION))
	// || IBugzillaConstants.SERVER_220.equals(getPreferenceStore().getString(
	// IBugzillaConstants.SERVER_VERSION));
	// }
	//
	// public boolean isServerCompatability220() {
	// return
	// IBugzillaConstants.SERVER_220.equals(getPreferenceStore().getString(IBugzillaConstants.SERVER_VERSION));
	// }

	/**
	 * Get the most recent query key for the preferences
	 * 
	 * @return A string containing the most recent query key
	 */
	public static String getMostRecentQuery() {
		return plugin.getPreferenceStore().getString(IBugzillaConstants.MOST_RECENT_QUERY);
	}

	public RepositoryConfiguration getProductConfiguration(TaskRepository repository) {
		if (!repositoryConfigurations.containsKey(repository.getUrl())) {
			try {
				repositoryConfigurations.put(repository.getUrl(), RepositoryConfigurationFactory.getInstance().getConfiguration(
						repository));
			} catch (IOException e) {
				MessageDialog.openInformation(null, "Retrieval of Bugzilla Configuration",
						"Bugzilla configuration retrieval failed.");
			}
		}

		return repositoryConfigurations.get(repository.getUrl());
	}

	
//	public RepositoryConfiguration getProductConfiguration(String serverUrl) {
//		if (!repositoryConfigurations.containsKey(serverUrl)) {
//			try {
//				repositoryConfigurations.put(serverUrl, RepositoryConfigurationFactory.getInstance().getConfiguration(
//						serverUrl));
//			} catch (IOException e) {
//				MessageDialog.openInformation(null, "Retrieval of Bugzilla Configuration",
//						"Bugzilla configuration retrieval failed.");
//			}
//		}
//
//		return repositoryConfigurations.get(serverUrl);
//	}

	protected void setProductConfiguration(String serverUrl, RepositoryConfiguration repositoryConfiguration) {
		repositoryConfigurations.put(serverUrl, repositoryConfiguration);
		// this.productConfiguration = productConfiguration;
	}


//	private void readFavoritesFile() {
//		IPath favoritesPath = getFavoritesFile();
//
//		try {
//			favoritesFile = new FavoritesFile(favoritesPath.toFile());
//		} catch (Exception e) {
//			logAndShowExceptionDetailsDialog(e, "occurred while restoring saved Bugzilla favorites.",
//					"Bugzilla Favorites Error");
//		}
//	}

	/**
	 * Reads cached product configuration and stores it in the
	 * <code>productConfiguration</code> field.
	 */
	private void readOfflineReportsFile() {
		IPath offlineReportsPath = getOfflineReportsFile();

		try {
			offlineReportsFile = new OfflineReportsFile(offlineReportsPath.toFile());
		} catch (Exception e) {
			MylarStatusHandler.log(e, "Could not restore offline Bugzilla reports file, creating new one (possible version incompatibility)");
			// logAndShowExceptionDetailsDialog(e, "occurred while restoring
			// saved offline Bugzilla reports.",
			// "Bugzilla Offline Reports Error");
			if (offlineReportsPath.toFile().delete()) {
				try {
					offlineReportsFile = new OfflineReportsFile(offlineReportsPath.toFile());
				} catch (Exception e1) {
					MylarStatusHandler.fail(e, "could not reset offline Bugzilla reports file", true);
				}
			} else {
				MylarStatusHandler.fail(null, "reset of Bugzilla offline reports file failed", true);
			}
		}
	}

//	/**
//	 * Returns the path to the file cacheing the query favorites.
//	 */
//	private IPath getFavoritesFile() {
//		IPath stateLocation = Platform.getStateLocation(BugzillaPlugin.getDefault().getBundle());
//		IPath configFile = stateLocation.append("favorites");
//		return configFile;
//	}

	/**
	 * Returns the path to the file cacheing the offline bug reports.
	 */
	private IPath getOfflineReportsFile() {
		IPath stateLocation = Platform.getStateLocation(BugzillaPlugin.getDefault().getBundle());
		IPath configFile = stateLocation.append("offlineReports");
		return configFile;
	}

	// /**
	// * Reads cached product configuration and stores it in the
	// * <code>productConfiguration</code> field.
	// *
	// * TODO remove this?
	// */
	// private void readCachedProductConfiguration(String serverUrl) {
	// IPath configFile = getProductConfigurationCachePath(serverUrl);
	//
	// try {
	// productConfigurations.put(serverUrl,
	// ServerConfigurationFactory.getInstance().readConfiguration(
	// configFile.toFile()));
	// } catch (IOException ex) {
	// try {
	// log(ex);
	// productConfigurations.put(serverUrl,
	// ServerConfigurationFactory.getInstance().getConfiguration(
	// serverUrl));
	// } catch (IOException e) {
	// log(e);
	// MessageDialog
	// .openInformation(
	// null,
	// "Bugzilla product attributes check",
	// "An error occurred while restoring saved Bugzilla product attributes:
	// \n\n"
	// + ex.getMessage()
	// + "\n\nUpdating them from the server also caused an error:\n\n"
	// + e.getMessage()
	// + "\n\nCheck the server URL in Bugzila preferences.\n"
	// + "Offline submission of new bugs will be disabled until valid product
	// attributes have been loaded.");
	// }
	// }
	// }

	/**
	 * Returns the path to the file cacheing the product configuration.
	 */
	protected IPath getProductConfigurationCachePath(String serverUrl) {
		IPath stateLocation = Platform.getStateLocation(BugzillaPlugin.getDefault().getBundle());
		IPath configFile = stateLocation.append("productConfig." + serverUrl.replace('/', '-'));
		return configFile;
	}

	/**
	 * Convenience method for logging statuses to the plugin log
	 * 
	 * @param status
	 *            the status to log
	 */
	public static void log(IStatus status) {
		getDefault().getLog().log(status);
	}

	/**
	 * Convenience method for logging exceptions to the plugin log
	 * 
	 * @param e
	 *            the exception to log
	 */
	public static void log(Exception e) {
		log(new Status(Status.ERROR, IBugzillaConstants.PLUGIN_ID, 0, e.getMessage(), e));
	}

	/**
	 * Returns the path to the file caching bug reports created while offline.
	 */
	protected IPath getCachedBugReportPath() {
		IPath stateLocation = Platform.getStateLocation(BugzillaPlugin.getDefault().getBundle());
		IPath bugFile = stateLocation.append("bugReports");
		return bugFile;
	}

	/**
	 * Logs the exception and shows an error dialog with exception details shown
	 * in a "Details" pane.
	 * 
	 * @param e
	 *            exception to be shown in the details pane
	 * @param message
	 *            message to be used in the dialog
	 * @param title
	 *            error dialog's title
	 */
	public IStatus logAndShowExceptionDetailsDialog(Exception e, String message, String title) {
		MultiStatus status = new MultiStatus(IBugzillaConstants.PLUGIN_ID, IStatus.ERROR, e.getClass().toString() + " "
				+ message + "\n\n" + "Click Details or see log for more information.", e);
		Status s = new Status(IStatus.ERROR, IBugzillaConstants.PLUGIN_ID, IStatus.ERROR, e.getClass().toString()
				+ ":  ", e);
		status.add(s);
		String error = (e.getMessage() == null) ? e.getClass().toString() : e.getMessage();
		s = new Status(IStatus.ERROR, IBugzillaConstants.PLUGIN_ID, IStatus.ERROR, error, e);
		status.add(s);
		log(status);
		ErrorDialog.openError(null, title, null, status);
		return status;
	}

	/**
	 * @return a list of the BugReports saved offline.
	 */
	public List<BugzillaReport> getSavedBugReports() {
		return offlineReportsFile.elements();
	}

	/**
	 * @param url
	 * @param proxy		can be null
	 */
	public URLConnection getUrlConnection(URL url, Proxy proxy) throws IOException, NoSuchAlgorithmException, KeyManagementException {
		SSLContext ctx = SSLContext.getInstance("TLS");

		javax.net.ssl.TrustManager[] tm = new javax.net.ssl.TrustManager[] { new TrustAll() };
		ctx.init(null, tm, null);
		HttpsURLConnection.setDefaultSSLSocketFactory(ctx.getSocketFactory());

		if (proxy == null) {
			proxy = Proxy.NO_PROXY;
		}
		URLConnection connection = url.openConnection(proxy);
		return connection;
	}

	private static IBugzillaResultEditorMatchAdapter resultEditorMatchAdapter = null;

	public static IBugzillaResultEditorMatchAdapter getResultEditorMatchAdapter() {
		return resultEditorMatchAdapter;
	}

	public static void setResultEditorMatchAdapter(IBugzillaResultEditorMatchAdapter resultEditorMatchAdapter) {
		BugzillaPlugin.resultEditorMatchAdapter = resultEditorMatchAdapter;
	}

	public boolean refreshOnStartUpEnabled() {
		return getPreferenceStore().getBoolean(IBugzillaConstants.REFRESH_QUERY);
	}

	public int getMaxResults() {
		return getPreferenceStore().getInt(IBugzillaConstants.MAX_RESULTS);
	}

	// private void setDefaultQueryOptions() {
	// // get the preferences store for the bugzilla preferences
	// IPreferenceStore prefs = getPreferenceStore();
	//
	// prefs.setDefault(IBugzillaConstants.VALUES_STATUS, BugzillaRepositoryUtil
	// .queryOptionsToString(IBugzillaConstants.DEFAULT_STATUS_VALUES));
	//
	// prefs.setDefault(IBugzillaConstants.VALUSE_STATUS_PRESELECTED,
	// BugzillaRepositoryUtil
	// .queryOptionsToString(IBugzillaConstants.DEFAULT_PRESELECTED_STATUS_VALUES));
	//
	// prefs.setDefault(IBugzillaConstants.VALUES_RESOLUTION,
	// BugzillaRepositoryUtil
	// .queryOptionsToString(IBugzillaConstants.DEFAULT_RESOLUTION_VALUES));
	//
	// prefs.setDefault(IBugzillaConstants.VALUES_SEVERITY,
	// BugzillaRepositoryUtil
	// .queryOptionsToString(IBugzillaConstants.DEFAULT_SEVERITY_VALUES));
	//
	// prefs.setDefault(IBugzillaConstants.VALUES_PRIORITY,
	// BugzillaRepositoryUtil
	// .queryOptionsToString(IBugzillaConstants.DEFAULT_PRIORITY_VALUES));
	//
	// prefs.setDefault(IBugzillaConstants.VALUES_HARDWARE,
	// BugzillaRepositoryUtil
	// .queryOptionsToString(IBugzillaConstants.DEFAULT_HARDWARE_VALUES));
	//
	// prefs.setDefault(IBugzillaConstants.VALUES_OS, BugzillaRepositoryUtil
	// .queryOptionsToString(IBugzillaConstants.DEFAULT_OS_VALUES));
	//
	// prefs.setDefault(IBugzillaConstants.VALUES_PRODUCT,
	// BugzillaRepositoryUtil
	// .queryOptionsToString(IBugzillaConstants.DEFAULT_PRODUCT_VALUES));
	//
	// prefs.setDefault(IBugzillaConstants.VALUES_COMPONENT,
	// BugzillaRepositoryUtil
	// .queryOptionsToString(IBugzillaConstants.DEFAULT_COMPONENT_VALUES));
	//
	// prefs.setDefault(IBugzillaConstants.VALUES_VERSION,
	// BugzillaRepositoryUtil
	// .queryOptionsToString(IBugzillaConstants.DEFAULT_VERSION_VALUES));
	//
	// prefs.setDefault(IBugzillaConstants.VALUES_TARGET, BugzillaRepositoryUtil
	// .queryOptionsToString(IBugzillaConstants.DEFAULT_TARGET_VALUES));
	// }

}
