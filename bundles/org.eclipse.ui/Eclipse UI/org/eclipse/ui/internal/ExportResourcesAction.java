package org.eclipse.ui.internal;

/*
 * Licensed Materials - Property of IBM,
 * WebSphere Studio Workbench
 * (c) Copyright IBM Corp 2000
 */
import org.eclipse.ui.*;
import org.eclipse.ui.dialogs.*;
import org.eclipse.ui.internal.*;
import org.eclipse.ui.internal.dialogs.*;
import org.eclipse.ui.actions.*;
import org.eclipse.ui.help.*;
import org.eclipse.jface.action.*;
import org.eclipse.jface.viewers.*;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.swt.widgets.Shell;
import java.util.*;

/**
 * Action representing the initiation of an Export operation by the user.
 */
public class ExportResourcesAction extends Action {
	private static final int SIZING_WIZARD_WIDTH = 470;
	private static final int SIZING_WIZARD_HEIGHT = 550;
	private IWorkbench workbench;
/**
 *	Create a new instance of this class
 */
public ExportResourcesAction(IWorkbench aWorkbench) {
	super("&Export...");
	setToolTipText("Export the selected resources");
	setId(IWorkbenchActionConstants.EXPORT);
	WorkbenchHelp.setHelp(this, new Object[] {IHelpContextIds.EXPORT_ACTION});
	this.workbench = aWorkbench;
}
/**
 * Invoke the Export wizards selection Wizard.
 *
 * @param browser Window
 */
public void run() {
	ExportWizard wizard = new ExportWizard();
	ISelection selection = workbench.getActiveWorkbenchWindow().getSelectionService().getSelection();
	IStructuredSelection selectionToPass = null;
	if (selection instanceof IStructuredSelection)
		selectionToPass = (IStructuredSelection) selection;
	else
		selectionToPass = StructuredSelection.EMPTY;
	wizard.init(workbench, selectionToPass);
	IDialogSettings workbenchSettings = WorkbenchPlugin.getDefault().getDialogSettings();
	IDialogSettings wizardSettings = workbenchSettings.getSection("ExportResourcesAction");
	if(wizardSettings==null)
		wizardSettings = workbenchSettings.addNewSection("ExportResourcesAction");
	wizard.setDialogSettings(wizardSettings);
	wizard.setForcePreviousAndNextButtons(true);

	Shell parent = workbench.getActiveWorkbenchWindow().getShell();
	WizardDialog dialog = new WizardDialog(parent, wizard);
	dialog.create();
	dialog.getShell().setSize(SIZING_WIZARD_WIDTH,SIZING_WIZARD_HEIGHT);
	dialog.open();
}
}
