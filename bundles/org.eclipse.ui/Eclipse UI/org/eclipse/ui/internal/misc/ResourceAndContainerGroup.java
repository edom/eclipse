package org.eclipse.ui.internal.misc;

/*
 * Licensed Materials - Property of IBM,
 * WebSphere Studio Workbench
 * (c) Copyright IBM Corp 2000
 */
import org.eclipse.core.resources.*;
import org.eclipse.core.runtime.*;
import org.eclipse.ui.internal.*;
import org.eclipse.ui.dialogs.*;
import org.eclipse.swt.*;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.widgets.*;

/**
 * Workbench-level composite for resource and container specification by the user.
 * Services such as field validation are performed by the group.
 * The group can be configured to accept existing resources, or only
 * new resources.
 */
public class ResourceAndContainerGroup implements Listener {
	// problem identifiers
	public static final int PROBLEM_NONE = 0;
	public static final int PROBLEM_RESOURCE_EMPTY = 1;
	public static final int PROBLEM_RESOURCE_EXIST = 2;
	public static final int PROBLEM_RESOURCE_CONTAINS_SEPARATOR = 3;
	public static final int PROBLEM_PATH_INVALID = 4;
	public static final int PROBLEM_CONTAINER_EMPTY = 5;
	public static final int PROBLEM_PROJECT_DOES_NOT_EXIST = 6;
	
	// the client to notify of changes
	private Listener client;

	// whether to allow existing resources
	private boolean allowExistingResources = false;

	// resource type (file, folder, project)
	private String resourceType = "resource";
	
	// problem indicator
	private String problemMessage = "";
	private int problemType = PROBLEM_NONE;

	// widgets
	private ContainerSelectionGroup containerGroup;
	private Button browseButton;
	private Text resourceNameField;

	// constants
	private static final int SIZING_TEXT_FIELD_WIDTH = 250;
/**
 * Create an instance of the group to allow the user
 * to enter/select a container and specify a resource
 * name.
 *
 * @param parent composite widget to parent the group
 * @param client object interested in changes to the group's fields value
 * @param resourceFieldLabel label to use in front of the resource name field
 * @param resourceType one word, in lowercase, to describe the resource to the user (file, folder, project)
 */
public ResourceAndContainerGroup(Composite parent, Listener client, String resourceFieldLabel, String resourceType) {
	super();
	this.resourceType = resourceType;
	createContents(parent,resourceFieldLabel);
	this.client = client;
}
/**
 * Returns a boolean indicating whether all controls in this group
 * contain valid values.
 *
 * @return boolean
 */
public boolean areAllValuesValid() {
	return problemType == PROBLEM_NONE;
}
/**
 * Creates this object's visual components.
 *
 * @param parent org.eclipse.swt.widgets.Composite
 */
protected void createContents(Composite parent,String resourceLabelString) {
	// server name group
	Composite composite = new Composite(parent,SWT.NONE);
	GridLayout layout = new GridLayout();
	layout.marginWidth = 0;
	layout.marginHeight = 0;
	composite.setLayout(layout);
	composite.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.GRAB_HORIZONTAL));

	// container group
	containerGroup = new ContainerSelectionGroup(composite, this, true);

	// resource name group
	Composite nameGroup = new Composite(composite,SWT.NONE);
	layout = new GridLayout();
	layout.numColumns = 2;
	layout.marginWidth = 0;
	nameGroup.setLayout(layout);
	GridData data = new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.GRAB_HORIZONTAL);
	nameGroup.setLayoutData(data);

	Label label = new Label(nameGroup,SWT.NONE);
	label.setText(resourceLabelString);
	label.setFont(parent.getFont());

	// resource name entry field
	resourceNameField = new Text(nameGroup,SWT.BORDER);
	resourceNameField.addListener(SWT.KeyDown,this);
	data = new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.GRAB_HORIZONTAL);
	data.widthHint = SIZING_TEXT_FIELD_WIDTH;
	resourceNameField.setLayoutData(data);

	validateControls();
}
/**
 * Returns the path of the currently selected container
 * or null if no container has been selected. Note that
 * the container may not exist yet if the user entered
 * a new container name in the field.
 */
public IPath getContainerFullPath() {
	return containerGroup.getContainerFullPath();
}
/**
 * Returns an error message indicating the current problem with the value
 * of a control in the group, or an empty message if all controls in the
 * group contain valid values.
 *
 * @return java.lang.String
 */
public String getProblemMessage() {
	return problemMessage;
}
/**
 * Returns the type of problem with the value of a control
 * in the group.
 *
 * @return one of the PROBLEM_* constants
 */
public int getProblemType() {
	return problemType;
}
/**
 * Returns a string that is the path of the currently selected
 * container.  Returns an empty string if no container has been
 * selected.
 */
public String getResource() {
	return resourceNameField.getText();
}
/**
 * Handles events for all controls in the group.
 *
 * @param e org.eclipse.swt.widgets.Event
 */
public void handleEvent(Event e) {
	validateControls();
	if (client != null) {
		client.handleEvent(e);
	}
}
/**
 * Sets the flag indicating whether existing resources are permitted.
 */
public void setAllowExistingResources(boolean value) {
	allowExistingResources = value;
}
/**
 * Sets the value of this page's container.
 *
 * @param value Full path to the container.
 */
public void setContainerFullPath(IPath path) {
	IResource initial = 
		ResourcesPlugin.getWorkspace().getRoot().findMember(path); 
	if (initial != null) {
		if (!(initial instanceof IContainer)) {
			initial = initial.getParent();
		}
		containerGroup.setSelectedContainer((IContainer) initial);
	}
	validateControls();
}
/**
 * Gives focus to the resource name field
 */
public void setFocus() {
	resourceNameField.setFocus();
}
/**
 * Sets the value of this page's resource name.
 *
 * @param value new value
 */
public void setResource(String value) {
	resourceNameField.setText(value);
	validateControls();
}
/**
 * Returns a <code>boolean</code> indicating whether a container name represents
 * a valid container resource in the workbench.  An error message is stored for
 * future reference if the name does not represent a valid container.
 *
 * @return <code>boolean</code> indicating validity of the container name
 */
protected boolean validateContainer() {
	IPath path = containerGroup.getContainerFullPath();
	if (path == null) {
		problemType = PROBLEM_CONTAINER_EMPTY;
		problemMessage = "The folder is empty.";
		return false;
	}
	IWorkspace workspace = ResourcesPlugin.getWorkspace();
	String projectName = path.segment(0);
	if (projectName == null || !workspace.getRoot().getProject(projectName).exists()) {
		problemType = PROBLEM_PROJECT_DOES_NOT_EXIST;
		problemMessage = "The specified project does not exist.";
		return false;
	}
	return true;
}
/**
 * Validates the values for each of the group's controls.  If an invalid
 * value is found then a descriptive error message is stored for later
 * reference.  Returns a boolean indicating the validity of all of the
 * controls in the group.
 */
protected boolean validateControls() {
	// don't attempt to validate controls until they have been created
	if (containerGroup == null) {
		return false;
	}
	problemType = PROBLEM_NONE;
	problemMessage = "";

	if (!validateContainer() || !validateResourceName())
		return false;

	IPath path = containerGroup.getContainerFullPath().append(resourceNameField.getText());
	return validateFullResourcePath(path);
}
/**
 * Returns a <code>boolean</code> indicating whether the specified resource
 * path represents a valid new resource in the workbench.  An error message
 * is stored for future reference if the path  does not represent a valid
 * new resource path.
 *
 * @param containerName the container name to validate
 * @return <code>boolean</code> indicating validity of the resource path
 */
protected boolean validateFullResourcePath(IPath resourcePath) {
	IWorkspace workspace = ResourcesPlugin.getWorkspace();

	IStatus result = workspace.validatePath(resourcePath.toString(),IResource.FOLDER);
	if (!result.isOK()) {
		problemType = PROBLEM_PATH_INVALID;
		problemMessage = result.getMessage();
		return false;
	}

	if (!allowExistingResources && (workspace.getRoot().getFolder(resourcePath).exists() || workspace.getRoot().getFile(resourcePath).exists())) {
		problemType = PROBLEM_RESOURCE_EXIST;
		problemMessage = "The same name already exists.";
		return false;
	} 
	return true;
}
/**
 * Returns a <code>boolean</code> indicating whether the resource name rep-
 * resents a valid resource name in the workbench.  An error message is stored
 * for future reference if the name does not represent a valid resource name.
 *
 * @return <code>boolean</code> indicating validity of the resource name
 */
protected boolean validateResourceName() {
	String resourceName = resourceNameField.getText();

	if (resourceName.equals("")) {
		problemType = PROBLEM_RESOURCE_EMPTY;
		problemMessage = "The " + resourceType + " name is empty.";
		return false;
	}

	if (resourceName.indexOf(IPath.SEPARATOR) != -1) {
		problemType = PROBLEM_RESOURCE_CONTAINS_SEPARATOR;
		problemMessage = "The " + resourceType + " name contains a separator.";
		return false;
	}

	return true;
}
}
