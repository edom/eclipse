package org.eclipse.ui.views.tasklist;

/*
 * Licensed Materials - Property of IBM,
 * WebSphere Studio Workbench
 * (c) Copyright IBM Corp 2000
 */

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.Platform;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

/**
 * Maintains a model of all known marker types.
 */ 
/* package */ class MarkerTypesModel {
	/**
	 * Maps from marker type id to MarkerType.
	 */
	private HashMap types;
	
/**
 * Creates a new marker types model.
 */
public MarkerTypesModel() {
	types = readTypes();
}
/**
 * Returns the marker type with the given id, or <code>null</code> if there is no such marker type.
 */
public MarkerType getType(String id) {
	return (MarkerType) types.get(id);
}
/**
 * Returns all known marker types.
 */
public MarkerType[] getTypes() {
	MarkerType[] result = new MarkerType[types.size()];
	types.values().toArray(result);
	return result;
}
/**
 * Returns the label for the given marker type.
 * Temporary workaround until we have labels in XML.
 */
String getWellKnownLabel(String type) {
	if (type.equals(IMarker.PROBLEM))
		return "Problem";
	if (type.equals(IMarker.TASK))
		return "Task";
	if (type.equals("org.eclipse.jdt.core.problem"))
		return "Java Problem";
	return type;
}
/**
 * Reads the marker types from the registry.
 */
HashMap readTypes() {
	HashMap types = new HashMap();
	IExtensionPoint point = Platform.getPluginRegistry().getExtensionPoint(ResourcesPlugin.PI_RESOURCES, ResourcesPlugin.PT_MARKERS);
	if (point != null) {
		// Gather all registered marker types.
		IExtension[] extensions = point.getExtensions();
		for (int i = 0; i < extensions.length; ++i) {
			IExtension ext = extensions[i];
			String id = ext.getUniqueIdentifier();
			String label = ext.getLabel();
			if (label.equals("")) {
				label = getWellKnownLabel(id);
			}
			ArrayList supersList = new ArrayList();
			IConfigurationElement[] configElements = ext.getConfigurationElements();
			for (int j = 0; j < configElements.length; ++j) {
				IConfigurationElement elt = configElements[j];
				if (elt.getName().equalsIgnoreCase("super")) {
					String sup = elt.getAttribute("type");
					if (sup != null) {
						supersList.add(sup);
					}
				}
			}
			String[] superTypes = new String[supersList.size()];
			supersList.toArray(superTypes);
			MarkerType type = new MarkerType(this, id, label, superTypes);
			types.put(id, type);
		}
	}
	return types;
}
}
