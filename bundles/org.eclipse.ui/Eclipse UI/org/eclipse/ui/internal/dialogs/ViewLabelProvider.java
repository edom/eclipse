package org.eclipse.ui.internal.dialogs;

import org.eclipse.jface.viewers.*;
import org.eclipse.jface.resource.*;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.*;
import org.eclipse.ui.internal.WorkbenchImages;
import org.eclipse.ui.internal.registry.*;
import java.util.HashMap;
import java.util.Iterator;

public class ViewLabelProvider extends LabelProvider {
	private HashMap images;
Image cacheImage(ImageDescriptor desc) {
	if (images == null)
		images = new HashMap(21);
	Image image = (Image) images.get(desc);
	if (image == null) {
		image = desc.createImage();
		images.put(desc, image);
	}
	return image;
}
public void dispose() {
	if (images != null) {
		for (Iterator i = images.values().iterator(); i.hasNext();) {
			((Image) i.next()).dispose();
		}
		images = null;
	}
	super.dispose();
}
public Image getImage(Object element) {
	if (element instanceof IViewDescriptor) {
		ImageDescriptor desc = ((IViewDescriptor)element).getImageDescriptor();
		if (desc != null)
			return cacheImage(desc);
	} else if (element instanceof ICategory) {
		ImageDescriptor desc = WorkbenchImages.getImageDescriptor(ISharedImages.IMG_OBJ_FOLDER);
		return cacheImage(desc);
	}
	return null;
}
public String getText(Object element) {
	String label = "Unknown";
	if (element instanceof ICategory)
		label = ((ICategory)element).getLabel();
	else if (element instanceof IViewDescriptor)
		label = ((ViewDescriptor)element).getLabel();
	int aruga = label.indexOf('&');
	if (aruga >= 0)
		label = label.substring(aruga + 1);
	return label;
}
}
