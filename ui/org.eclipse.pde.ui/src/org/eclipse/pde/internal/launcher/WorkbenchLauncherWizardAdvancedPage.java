/*
 * (c) Copyright IBM Corp. 2000, 2001.
 * All Rights Reserved.
 */
package org.eclipse.pde.internal.launcher;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;

import org.eclipse.core.resources.*;
import org.eclipse.core.runtime.*;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.*;
import org.eclipse.swt.events.*;
import org.eclipse.swt.graphics.*;
import org.eclipse.pde.internal.*;
import org.eclipse.pde.internal.base.model.plugin.*;
import org.eclipse.pde.internal.preferences.PDEBasePreferencePage;
import org.eclipse.pde.internal.wizards.StatusWizardPage;
import org.eclipse.swt.layout.*;
import org.eclipse.jface.viewers.*;
import org.eclipse.pde.internal.elements.*;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.pde.internal.preferences.ExternalPluginsBlock;
import org.eclipse.jface.dialogs.IDialogConstants;

import org.eclipse.jface.dialogs.IDialogSettings;

public class WorkbenchLauncherWizardAdvancedPage extends StatusWizardPage {

	private static final String SETTINGS_USECUSTOM = "default";
	private static final String SETTINGS_SHOWNAMES = "showNames";
	private static final String SETTINGS_WSPROJECT = "wsproject";
	private static final String SETTINGS_EXTPLUGINS = "extplugins";
	public static final String KEY_WORKSPACE_PLUGINS =
		"Preferences.AdvancedTracingPage.workspacePlugins";
	public static final String KEY_EXTERNAL_PLUGINS =
		"Preferences.AdvancedTracingPage.externalPlugins";

	private static final String SETTINGS_PREVPATH = "prevpath";

	private Button useDefaultCheck;
	private Button showNamesCheck;
	private CheckboxTreeViewer pluginTreeViewer;
	private Label visibleLabel;
	private Label restoreLabel;
	private Image pluginImage;
	private Image fragmentImage;
	private Image pluginsImage;
	private NamedElement workspacePlugins;
	private NamedElement externalPlugins;
	private Vector externalList;
	private Vector workspaceList;
	private Button defaultsButton;

	class PluginLabelProvider extends LabelProvider {
		public String getText(Object obj) {
			if (obj instanceof IPluginModel) {
				IPluginModelBase model = (IPluginModelBase) obj;
				IPluginBase plugin = model.getPluginBase();
				String name = plugin.getId();
				if (showNamesCheck.getSelection())
					name = plugin.getTranslatedName();
				return name + " (" + plugin.getVersion() + ")";
			}
			return obj.toString();
		}
		public Image getImage(Object obj) {
			if (obj instanceof IPluginModel)
				return pluginImage;
			if (obj instanceof IFragmentModel)
				return fragmentImage;
			if (obj instanceof NamedElement)
				return ((NamedElement) obj).getImage();
			return null;
		}
	}

	class PluginContentProvider
		extends DefaultContentProvider
		implements ITreeContentProvider {
		public boolean hasChildren(Object parent) {
			if (parent instanceof IPluginModel)
				return false;
			return true;
		}
		public Object[] getChildren(Object parent) {
			if (parent == externalPlugins) {
				return getExternalPlugins();
			}
			if (parent == workspacePlugins) {
				return getWorkspacePlugins();
			}
			return new Object[0];
		}
		public Object getParent(Object child) {
			if (child instanceof IPluginModel) {
				IPluginModel model = (IPluginModel) child;
				if (model.getUnderlyingResource() != null)
					return workspacePlugins;
				else
					return externalPlugins;
			}
			return null;
		}
		public Object[] getElements(Object input) {
			return new Object[] { workspacePlugins, externalPlugins };
		}
	}

	public WorkbenchLauncherWizardAdvancedPage(String title) {
		super("WorkbenchLauncherWizardAdvancedPage", false);
		setTitle(title);
		setDescription("Plugins visible to the plugin loader.");
		pluginImage = PDEPluginImages.DESC_PLUGIN_OBJ.createImage();
		fragmentImage = PDEPluginImages.DESC_FRAGMENT_OBJ.createImage();
		pluginsImage = PDEPluginImages.DESC_REQ_PLUGINS_OBJ.createImage();
	}

	public void dispose() {
		pluginImage.dispose();
		fragmentImage.dispose();
		pluginsImage.dispose();
		super.dispose();
	}

	public void createControl(Composite parent) {
		initializeDialogUnits(parent);

		Composite composite = new Composite(parent, SWT.NONE);

		GridLayout layout = new GridLayout();
		GridData gd;
		layout.numColumns = 2;
		composite.setLayout(layout);

		useDefaultCheck = new Button(composite, SWT.CHECK);
		useDefaultCheck.setText("&Use default");
		fillIntoGrid(useDefaultCheck, 2, false);

		Label label = new Label(composite, SWT.WRAP);
		label.setText(
			"If this option is checked, the workbench instance you are about to launch will 'see' all the plug-ins and fragments in the workspace, as well as all the external projects enabled in the Preferences.");
		gd = fillIntoGrid(label, 2, false);
		gd.widthHint = convertWidthInCharsToPixels(70);

		label = new Label(composite, SWT.SEPARATOR | SWT.HORIZONTAL);
		fillIntoGrid(label, 2, false);

		showNamesCheck = new Button(composite, SWT.CHECK);
		showNamesCheck.setText("Show plug-in and fragment full names");
		fillIntoGrid(showNamesCheck, 2, false);

		visibleLabel = new Label(composite, SWT.NULL);
		visibleLabel.setText("Visible plug-ins and fragments:");
		fillIntoGrid(visibleLabel, 2, false);

		Control list = createPluginList(composite);
		gd = new GridData(GridData.FILL_BOTH);
		gd.horizontalSpan = 2;
		//gd.verticalSpan = 2;
		gd.heightHint = 250;
		list.setLayoutData(gd);

		defaultsButton = new Button(composite, SWT.PUSH);
		defaultsButton.setText("Restore &Defaults");
		gd = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		gd.heightHint = convertVerticalDLUsToPixels(IDialogConstants.BUTTON_HEIGHT);
		int widthHint = convertHorizontalDLUsToPixels(IDialogConstants.BUTTON_WIDTH);
		gd.widthHint =
			Math.max(
				widthHint,
				defaultsButton.computeSize(SWT.DEFAULT, SWT.DEFAULT, true).x);
		defaultsButton.setLayoutData(gd);

		restoreLabel = new Label(composite, SWT.NULL);
		restoreLabel.setText(
			"You can restore plug-in and fragment visibility to the default values.");
		gd = fillIntoGrid(restoreLabel, 1, false);
		//gd.verticalAlignment = GridData.BEGINNING;

		initializeFields();
		hookListeners();
		pluginTreeViewer.reveal(workspacePlugins);
		setControl(composite);
	}

	private void hookListeners() {
		useDefaultCheck.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				useDefaultChanged();
			}
		});
		showNamesCheck.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				pluginTreeViewer.refresh();
			}
		});
		defaultsButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				initializeCheckState();
			}
		});
	}

	private void useDefaultChanged() {
		boolean useDefault = useDefaultCheck.getSelection();
		adjustCustomControlEnableState(!useDefault);		
		updateStatus();
	}
	
	private void adjustCustomControlEnableState(boolean enable) {
		visibleLabel.setEnabled(enable);
		showNamesCheck.setEnabled(enable);
		pluginTreeViewer.getTree().setEnabled(enable);
		defaultsButton.setEnabled(enable);
		restoreLabel.setEnabled(enable);
	}

	private GridData fillIntoGrid(Control control, int hspan, boolean grab) {
		GridData gd = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		gd.horizontalSpan = hspan;
		gd.grabExcessHorizontalSpace = grab;
		control.setLayoutData(gd);
		return gd;
	}

	protected Control createPluginList(Composite parent) {
		pluginTreeViewer = new CheckboxTreeViewer(parent, SWT.BORDER);
		pluginTreeViewer.setContentProvider(new PluginContentProvider());
		pluginTreeViewer.setLabelProvider(new PluginLabelProvider());
		pluginTreeViewer.setAutoExpandLevel(2);
		pluginTreeViewer.addCheckStateListener(new ICheckStateListener() {
			public void checkStateChanged(CheckStateChangedEvent event) {
				Object element = event.getElement();
				if (element instanceof IPluginModelBase) {
					IPluginModelBase model = (IPluginModelBase) event.getElement();
					handleCheckStateChanged(model, event.getChecked());
				} else {
					handleGroupStateChanged(element, event.getChecked());
				}
			}
		});
		workspacePlugins =
			new NamedElement(
				PDEPlugin.getResourceString(KEY_WORKSPACE_PLUGINS),
				pluginsImage);
		externalPlugins =
			new NamedElement(
				PDEPlugin.getResourceString(KEY_EXTERNAL_PLUGINS),
				pluginsImage);
		return pluginTreeViewer.getTree();
	}

	private static IPluginModelBase[] getExternalPlugins() {
		return PDEPlugin.getDefault().getExternalModelManager().getModels();
	}

	private static IPluginModelBase[] getWorkspacePlugins() {
		return PDEPlugin
			.getDefault()
			.getWorkspaceModelManager()
			.getWorkspacePluginModels();
	}

	private void initializeFields() {
		IDialogSettings initialSettings = getDialogSettings();
		boolean useDefault = true;
		boolean showNames = true;

		/*
		
		ArrayList checkedPlugins = new ArrayList();
		checkedPlugins.addAll(available);
		
		ArrayList externalPlugins = new ArrayList();
		*/

		if (initialSettings != null) {
			useDefault = !initialSettings.getBoolean(SETTINGS_USECUSTOM);
			showNames = !initialSettings.getBoolean(SETTINGS_SHOWNAMES);
		}
		// Need to set these before we refresh the viewer
		useDefaultCheck.setSelection(useDefault);
		showNamesCheck.setSelection(showNames);
		pluginTreeViewer.setInput(PDEPlugin.getDefault());
		initializeCheckState();

		// Now we can do the rest
		/*
		
			String deselectedPluginIDs = initialSettings.get(SETTINGS_WSPROJECT);
			if (deselectedPluginIDs != null) {
				ArrayList deselected = new ArrayList();
				StringTokenizer tok =
					new StringTokenizer(deselectedPluginIDs, File.pathSeparator);
				while (tok.hasMoreTokens()) {
					deselected.add(tok.nextToken());
				}
				for (int i = checkedPlugins.size() - 1; i >= 0; i--) {
					PluginModel desc = (PluginModel) checkedPlugins.get(i);
					if (deselected.contains(desc.getId())) {
						checkedPlugins.remove(i);
					}
				}
			}
		
			String ext = initialSettings.get(SETTINGS_EXTPLUGINS);
			if (ext != null) {
				ArrayList urls = new ArrayList();
				StringTokenizer tok = new StringTokenizer(ext, File.pathSeparator);
				while (tok.hasMoreTokens()) {
					try {
						urls.add(new URL(tok.nextToken()));
					} catch (MalformedURLException e) {
						SelfHostingPlugin.log(e);
					}
				}
				URL[] urlsArray = (URL[]) urls.toArray(new URL[urls.size()]);
				PluginModel[] descs = PluginUtil.getPluginModels(urlsArray);
				if (descs != null) {
					externalPlugins.addAll(Arrays.asList(descs));
				}
			}
		}
		*/

		//fWorkspacePluginsList.setCheckedElements(checkedPlugins);
		//fExternalPluginsList.setElements(externalPlugins);
		adjustCustomControlEnableState(!useDefault);		
	}

	private void initializeCheckState() {
		IPluginModelBase[] models = (IPluginModelBase[]) getWorkspacePlugins();
		Vector checked = new Vector();

		for (int i = 0; i < models.length; i++) {
			checked.add(models[i]);
		}
		checked.add(workspacePlugins);
		if (pluginTreeViewer.getGrayed(workspacePlugins))
			pluginTreeViewer.setGrayed(workspacePlugins, false);

		models = (IPluginModelBase[]) getExternalPlugins();
		for (int i = 0; i < models.length; i++) {
			IPluginModelBase model = models[i];
			if (model.isEnabled())
				checked.add(model);
		}
		IPreferenceStore pstore = PDEPlugin.getDefault().getPreferenceStore();
		String exMode = pstore.getString(ExternalPluginsBlock.CHECKED_PLUGINS);
		boolean externalMixed = false;
		if (exMode.length() == 0 || exMode.equals(ExternalPluginsBlock.SAVED_ALL)) {
			checked.add(externalPlugins);
		} else if (!exMode.equals(ExternalPluginsBlock.SAVED_NONE)) {
			checked.add(externalPlugins);
			externalMixed = true;
		}
		if (pluginTreeViewer.getGrayed(externalPlugins) != externalMixed)
			pluginTreeViewer.setGrayed(externalPlugins, externalMixed);
		pluginTreeViewer.setCheckedElements(checked.toArray());
	}

	private void handleCheckStateChanged(IPluginModelBase model, boolean checked) {
		boolean external = model.getUnderlyingResource() == null;
		NamedElement parent = external ? externalPlugins : workspacePlugins;
		IPluginModelBase[] siblings;

		if (external) {
			siblings = (IPluginModelBase[]) getExternalPlugins();
		} else {
			siblings = (IPluginModelBase[]) getWorkspacePlugins();
		}

		int groupState = -1;

		for (int i = 0; i < siblings.length; i++) {
			boolean state = pluginTreeViewer.getChecked(siblings[i]);
			if (groupState == -1)
				groupState = state ? 1 : 0;
			else if (groupState == 1 && state == false) {
				groupState = -1;
				break;
			} else if (groupState == 0 && state == true) {
				groupState = -1;
				break;
			}
		}
		// If group state is -1 (mixed), we should gray the parent.
		// Otherwise, we should set it to the children group state
		switch (groupState) {
			case 0 :
				pluginTreeViewer.setChecked(parent, false);
				pluginTreeViewer.setGrayed(parent, false);
				break;
			case 1 :
				pluginTreeViewer.setChecked(parent, true);
				pluginTreeViewer.setGrayed(parent, false);
				break;
			case -1 :
				pluginTreeViewer.setGrayed(parent, true);
				break;
		}
	}

	private void handleGroupStateChanged(Object group, boolean checked) {
		IPluginModelBase[] models;

		if (group.equals(workspacePlugins)) {
			models = (IPluginModelBase[]) getWorkspacePlugins();
		} else {
			models = (IPluginModelBase[]) getExternalPlugins();
		}
		for (int i = 0; i < models.length; i++) {
			pluginTreeViewer.setChecked(models[i], checked);
		}
		if (pluginTreeViewer.getGrayed(group))
			pluginTreeViewer.setGrayed(group, false);
	}

	public void storeSettings() {
		IDialogSettings initialSettings = getDialogSettings();
		initialSettings.put(SETTINGS_USECUSTOM, !useDefaultCheck.getSelection());
		initialSettings.put(SETTINGS_SHOWNAMES, !showNamesCheck.getSelection());
		/*
		StringBuffer buf = new StringBuffer();
		// store deselected projects
		List selectedProjects = fWorkspacePluginsList.getCheckedElements();
		List projects = fWorkspacePluginsList.getElements();
		for (int i = 0; i < projects.size(); i++) {
			PluginModel curr = (PluginModel) projects.get(i);
			if (!selectedProjects.contains(curr)) {
				buf.append(curr.getId());
				buf.append(File.pathSeparatorChar);
			}
		}
		initialSettings.put(SETTINGS_WSPROJECT, buf.toString());
		
		buf = new StringBuffer();
		List external = fExternalPluginsList.getElements();
		for (int i = 0; i < external.size(); i++) {
			PluginModel curr = (PluginModel) external.get(i);
			buf.append(curr.getLocation());
			if (curr instanceof PluginDescriptorModel) {
				buf.append("/plugin.xml");
			} else if (curr instanceof PluginFragmentModel) {
				buf.append("/fragment.xml");
			}
			buf.append(File.pathSeparatorChar);
		}
		initialSettings.put(SETTINGS_EXTPLUGINS, buf.toString());
		*/
	}
	
	static void setLauncherData(IDialogSettings settings, LauncherData data) {
		boolean useDefault = true;

		if (settings != null) {
			useDefault = !settings.getBoolean(SETTINGS_USECUSTOM);
		}
		ArrayList res = new ArrayList();

		if (useDefault) {
			IPluginModelBase[] models = getWorkspacePlugins();
			for (int i = 0; i < models.length; i++) {
				res.add(models[i]);
			}
			models = getExternalPlugins();
			for (int i = 0; i < models.length; i++) {
				if (models[i].isEnabled())
					res.add(models[i]);
			}
		} else {
		}
		IPluginModelBase[] plugins = (IPluginModelBase[]) res.toArray(new IPluginModelBase[res.size()]);
		data.setPlugins(plugins);
	}

	private void updateStatus() {
		IStatus genStatus = validatePlugins();
		updateStatus(genStatus);
	}

	private IStatus validatePlugins() {
		IPluginModelBase[] plugins = getPlugins();
		if (plugins.length == 0) {
			return createStatus(IStatus.ERROR, "No plugins available.");
		}
		IPluginModelBase boot = findModel("org.eclipse.core.boot", plugins);
		if (boot == null) {
			return createStatus(IStatus.ERROR, "Plugin 'org.eclipse.core.boot' not found.");
		}
		if (findModel("org.eclipse.ui", plugins) != null) {
			if (findModel("org.eclipse.sdk", plugins) == null) {
				return createStatus(
					IStatus.WARNING,
					"'org.eclipse.sdk' not found. It is implicitly required by 'org.eclipse.ui'.");
			}
			/*
			try {
				File bootDir = new File(new URL(boot.getLocation()).getFile());
				File installDir = new File(bootDir.getParentFile().getParentFile(), "install");
				if (!installDir.exists()) {
					return createStatus(
						IStatus.WARNING,
						installDir.getPath()
							+ " not found.\nThe install directory is required by 'org.eclipse.ui'.");
				}
			} catch (MalformedURLException e) {
			}
			*/
		};
		return createStatus(IStatus.OK, "");
	}

	private IPluginModelBase findModel(String id, IPluginModelBase[] models) {
		for (int i = 0; i < models.length; i++) {
			IPluginModelBase model = (IPluginModelBase) models[i];
			if (model.getPluginBase().getId().equals(id))
				return model;
		}
		return null;
	}

	/**
	 * Returns the selected plugins.
	 */

	public IPluginModelBase[] getPlugins() {
		ArrayList res = new ArrayList();
		boolean useDefault = useDefaultCheck.getSelection();
		if (useDefault) {
			IPluginModelBase[] models = getWorkspacePlugins();
			for (int i = 0; i < models.length; i++) {
				res.add(models[i]);
			}
			models = getExternalPlugins();
			for (int i = 0; i < models.length; i++) {
				if (models[i].isEnabled())
					res.add(models[i]);
			}

		} else {
			Object[] elements = pluginTreeViewer.getCheckedElements();
			for (int i = 0; i < elements.length; i++) {
				Object element = elements[i];
				if (element instanceof IPluginModelBase)
					res.add(element);
			}
		}
		return (IPluginModelBase[]) res.toArray(new IPluginModelBase[res.size()]);
	}
}