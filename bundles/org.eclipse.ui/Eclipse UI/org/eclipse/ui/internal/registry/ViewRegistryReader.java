package org.eclipse.ui.internal.registry;

/*
 * Licensed Materials - Property of IBM,
 * WebSphere Studio Workbench
 * (c) Copyright IBM Corp 2000
 */
import org.eclipse.core.runtime.*;
import org.eclipse.ui.internal.misc.*;
import org.eclipse.ui.internal.*;
import org.eclipse.ui.internal.misc.UIHackFinder;

/**
 * A strategy to read view extensions from the registry.
 */
public class ViewRegistryReader extends RegistryReader {
	private static final String TAG_VIEW="view";
	private static final String TAG_CATEGORY="category";
	private ViewRegistry viewRegistry;
	
/**
 * RegistryViewReader constructor comment.
 */
public ViewRegistryReader() {
	super();
}
/**
 * Reads the category element.
 */
protected void readCategory(IConfigurationElement element) {
	try {
		Category category = new Category(element);
		viewRegistry.add(category);
	} catch (CoreException e) {
		// log an error since its not safe to show a dialog here
		WorkbenchPlugin.log("Unable to create view category.", e.getStatus());
	}
}
/**
 * readElement method comment.
 */
protected boolean readElement(IConfigurationElement element) {
	if (element.getName().equals(TAG_VIEW)) {
		readView(element);
		readElementChildren(element);
		return true;
	}
	if (element.getName().equals(TAG_CATEGORY)) {
		readCategory(element);
		readElementChildren(element);
		return true;
	}
	
	return false;
}
/**
 * Reads the view element.
 */
protected void readView(IConfigurationElement element) {
	try {
		ViewDescriptor desc = new ViewDescriptor(element);
		viewRegistry.add(desc);
	} catch (CoreException e) {
		// log an error since its not safe to open a dialog here
		WorkbenchPlugin.log("Unable to create view descriptor." , e.getStatus());
	}
}
/**
 * Read the view extensions within a registry.
 */
public void readViews(IPluginRegistry in, ViewRegistry out)
	throws CoreException {
	UIHackFinder.fixHandler(); // this does not seem to really ever be throwing an the exception
	viewRegistry = out;
	readRegistry(in, IWorkbenchConstants.PLUGIN_ID, IWorkbenchConstants.PL_VIEWS);
	out.mapViewsToCategories();
}
}
