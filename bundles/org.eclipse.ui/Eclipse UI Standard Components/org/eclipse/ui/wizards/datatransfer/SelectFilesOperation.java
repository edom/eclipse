package org.eclipse.ui.wizards.datatransfer;

/*
 * Licensed Materials - Property of IBM,
 * WebSphere Studio Workbench
 * (c) Copyright IBM Corp 2000
 */
import org.eclipse.core.runtime.*;
import org.eclipse.ui.dialogs.FileSystemElement;
import org.eclipse.jface.operation.*;
import java.io.*;
import java.util.*;

/**
 *	Operation responsible for traversing a specified file system position
 *	recursively and building
 *	-	a tree that represents the container structure
 *	-	a collection containing all files meeting a specified extension criteria
 *
 *	This is implemented as an Operation in order to provide an escape to the user
 *	(the Cancel button) if the operation drags on for too long
 */
public class SelectFilesOperation implements IRunnableWithProgress {
	IProgressMonitor				monitor;
	Object							root;
	IImportStructureProvider	provider;
	String							desiredExtensions[];
	FileSystemElement				result;
/**
 * Creates a new <code>SelectFilesOperation</code>.
 */
public SelectFilesOperation(Object rootObject,IImportStructureProvider structureProvider) {
	super();
	root = rootObject;
	provider = structureProvider;
}
/**
 * Creates and returns a <code>FileSystemElement</code> if the specified
 * file system object merits one.  The criteria for this are:
 * - if the file system object is a container then it must have either a
 *   child container or an associated file
 * - if the file system object is a file then it must have an extension
 *   suitable for selection
 */
protected FileSystemElement createElement(FileSystemElement parent,Object fileSystemObject) throws InterruptedException {
	ModalContext.checkCanceled(monitor);
	boolean isContainer = provider.isFolder(fileSystemObject);
	String elementLabel = parent == null ? provider.getFullPath(fileSystemObject) : provider.getLabel(fileSystemObject);
	
	if (!isContainer && !hasDesiredExtension(elementLabel))
		return null;

	FileSystemElement result =
		new FileSystemElement(elementLabel,parent,isContainer);
	result.setFileSystemObject(fileSystemObject);

	if (isContainer) {
		boolean haveChildOrFile = false;
		List children = provider.getChildren(fileSystemObject);
		if (children == null)
			children = new ArrayList(1);
		Iterator childrenEnum = children.iterator();
		while (childrenEnum.hasNext()) {
			if (createElement(result,childrenEnum.next()) != null)
				haveChildOrFile = true;
		}

		if (!haveChildOrFile && parent != null) {
			parent.removeFolder(result);
			result = null;
		}
	}

	return result;
}
/**
 * Returns the extension portion of the passed filename string.
 */
protected String getExtensionFor(String filename) {
	int nIndex = filename.lastIndexOf('.');
	
	if (nIndex >= 0)
		return filename.substring(nIndex + 1);
		
	return "";

}
/**
 * Returns the resulting root file system element.
 */
public FileSystemElement getResult() {
	return result;
}
/**
 * Returns a boolean indicating whether the extension of the passed filename
 * is one of the extensions specified as desired by the filter.
 */
protected boolean hasDesiredExtension(String filename) {
	if (desiredExtensions == null)		// ie.- all extensions are fine
		return true;
		
	int extensionsSize = desiredExtensions.length;
	for (int i = 0; i < extensionsSize; i++) {
		if (getExtensionFor(filename).equalsIgnoreCase(desiredExtensions[i]))
			return true;
	}

	return false;
}
/**
 * Runs the operation.
 */
public void run(IProgressMonitor monitor) throws InterruptedException {
	try {
		this.monitor = monitor;
		monitor.beginTask("Scanning for matching files...",IProgressMonitor.UNKNOWN);
		result = createElement(null,root);
		if (result == null) {
			result = new FileSystemElement(provider.getLabel(root),null,provider.isFolder(root));
			result.setFileSystemObject(root);
		}
	} finally {
		monitor.done();
	}
}
/**
 * Sets the file extensions which are desired.  A value of <code>null</code>
 * indicates that all files should be kept regardless of extension.
 */
public void setDesiredExtensions(String[] extensions) {
	desiredExtensions = extensions;
}
}
