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
package net.karlmartens.ui.viewer;

import net.karlmartens.ui.widget.GridChooser;
import net.karlmartens.ui.widget.GridChooserColumn;

import org.eclipse.jface.viewers.ViewerColumn;

public final class GridChooserViewerColumn extends ViewerColumn {

  private final GridChooserColumn _column;

  public GridChooserViewerColumn(GridChooserViewer viewer, int style) {
    this(viewer, style, -1);
  }

  public GridChooserViewerColumn(GridChooserViewer viewer, int style, int index) {
    this(viewer, createColumn(viewer.getControl(), style, index));
  }

  private GridChooserViewerColumn(GridChooserViewer viewer, GridChooserColumn column) {
    super(viewer, column);
    _column = column;
  }

  public GridChooserColumn getColumn() {
    return _column;
  }

  private static GridChooserColumn createColumn(GridChooser chooser, int style, int index) {
    if (index >= 0)
      return new GridChooserColumn(chooser, style, index);

    return new GridChooserColumn(chooser, style);
  }
}
