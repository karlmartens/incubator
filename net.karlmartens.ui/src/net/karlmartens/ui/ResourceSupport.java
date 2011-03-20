/**
 *  net.karlmartens.ui, is a library of UI widgets
 *  Copyright (C) 2010,2011
 *  Karl Martens
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Lesser General Public License as 
 *  published by the Free Software Foundation, either version 3 of the 
 *  License, or (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */
package net.karlmartens.ui;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.plugin.AbstractUIPlugin;

public final class ResourceSupport {

	public static ImageDescriptor imageDescriptorFromPlugin(String pluginId,
			Class<?> clazz, String imageFilePath) {
		ImageDescriptor d = AbstractUIPlugin.imageDescriptorFromPlugin(
				pluginId, imageFilePath);
		if (d != null)
			return d;

		d = ImageDescriptor.createFromFile(clazz, imageFilePath);
		if (d != null)
			return d;

		return ImageDescriptor.getMissingImageDescriptor();
	}
}
