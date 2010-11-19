package net.karlmartens.ui;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.plugin.AbstractUIPlugin;

public final class ResourceSupport {

	public static ImageDescriptor imageDescriptorFromPlugin(String pluginId, Class<?> clazz, String imageFilePath) {
		ImageDescriptor d = AbstractUIPlugin.imageDescriptorFromPlugin(pluginId, imageFilePath);
		if (d != null)
			return d;
		
		d = ImageDescriptor.createFromFile(clazz, imageFilePath);
		if (d != null)
			return d;
		
		return ImageDescriptor.getMissingImageDescriptor();
	}
}
