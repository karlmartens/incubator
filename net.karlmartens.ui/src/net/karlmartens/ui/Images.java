/**
 *   Copyright 2010,2011 Karl Martens
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *       
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 *
 *   net.karlmartens.ui, is a library of UI widgets
 */
package net.karlmartens.ui;

import org.eclipse.jface.resource.ImageDescriptor;

public final class Images {

  public static final ImageDescriptor ARROW_BOTTOM = ResourceSupport.imageDescriptorFromPlugin(Activator.PLUGIN_ID, Activator.class,
      "/icons/navigate_bottom.png");
  public static final ImageDescriptor ARROW_DOWN = ResourceSupport.imageDescriptorFromPlugin(Activator.PLUGIN_ID, Activator.class, "/icons/navigate_down.png");
  public static final ImageDescriptor ARROW_LEFT = ResourceSupport.imageDescriptorFromPlugin(Activator.PLUGIN_ID, Activator.class, "/icons/navigate_left.png");
  public static final ImageDescriptor ARROW_RIGHT = ResourceSupport
      .imageDescriptorFromPlugin(Activator.PLUGIN_ID, Activator.class, "/icons/navigate_right.png");
  public static final ImageDescriptor ARROW_TOP = ResourceSupport.imageDescriptorFromPlugin(Activator.PLUGIN_ID, Activator.class, "/icons/navigate_top.png");
  public static final ImageDescriptor ARROW_UP = ResourceSupport.imageDescriptorFromPlugin(Activator.PLUGIN_ID, Activator.class, "/icons/navigate_up.png");

  public static final ImageDescriptor SORT_ASCENDING = ResourceSupport.imageDescriptorFromPlugin(Activator.PLUGIN_ID, Activator.class,
      "/icons/arrow_down.gif");
  public static final ImageDescriptor SORT_DECENDING = ResourceSupport
      .imageDescriptorFromPlugin(Activator.PLUGIN_ID, Activator.class, "/icons/arrow_up.gif");

}
