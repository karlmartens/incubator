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

  public static final ImageDescriptor ARROW_BOTTOM = descriptor("/icons/navigate_bottom.png");
  public static final ImageDescriptor ARROW_DOWN = descriptor("/icons/navigate_down.png");
  public static final ImageDescriptor ARROW_LEFT = descriptor("/icons/navigate_left.png");
  public static final ImageDescriptor ARROW_RIGHT = descriptor("/icons/navigate_right.png");
  public static final ImageDescriptor ARROW_TOP = descriptor("/icons/navigate_top.png");
  public static final ImageDescriptor ARROW_UP = descriptor("/icons/navigate_up.png");
  public static final ImageDescriptor ASCENDING = descriptor("/icons/arrow_down.gif");
  public static final ImageDescriptor COPY = descriptor("/icons/copy.png");
  public static final ImageDescriptor CUT = descriptor("/icons/cut.png");
  public static final ImageDescriptor DECENDING = descriptor("/icons/arrow_up.gif");
  public static final ImageDescriptor DELETE = descriptor("/icons/delete.png");
  public static final ImageDescriptor FILTER = descriptor("/icons/filter.png");
  public static final ImageDescriptor PASTE = descriptor("/icons/paste.png");
  public static final ImageDescriptor RESIZE = descriptor("/icons/resize_column.png");
  public static final ImageDescriptor RESIZE_ALL = descriptor("/icons/resize_column_all.png");
  public static final ImageDescriptor SELECT_TABLE_COLUMN = descriptor("/icons/select_table_column.png");
  public static final ImageDescriptor SELECT_TABLE = descriptor("/icons/select_table.png");
  public static final ImageDescriptor SHOW_HIDE_COLUMN = descriptor("/icons/select_table_column.png");
  public static final ImageDescriptor SORT_ASCEND = descriptor("/icons/sort_ascend.png");
  public static final ImageDescriptor SORT_DESCEND = descriptor("/icons/sort_descend.png");

  private static ImageDescriptor descriptor(String path) {
    return ResourceSupport.imageDescriptorFromPlugin(Activator.PLUGIN_ID,
        Activator.class, path);
  }
}
