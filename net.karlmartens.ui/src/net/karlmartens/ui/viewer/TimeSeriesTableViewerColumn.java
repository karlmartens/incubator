/**
 *   Copyright 2011 Karl Martens
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

import net.karlmartens.ui.widget.TimeSeriesTable;
import net.karlmartens.ui.widget.TimeSeriesTableColumn;

import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.ViewerColumn;

public final class TimeSeriesTableViewerColumn extends ViewerColumn {

  private final TimeSeriesTableColumn _column;
  private EditingSupport _editingSupport;

  public TimeSeriesTableViewerColumn(TimeSeriesTableViewer viewer, int style) {
    this(viewer, style, -1);
  }

  public TimeSeriesTableViewerColumn(TimeSeriesTableViewer viewer, int style, int index) {
    this(viewer, createColumn(viewer.getControl(), style, index));
  }

  TimeSeriesTableViewerColumn(TimeSeriesTableViewer viewer, TimeSeriesTableColumn column) {
    super(viewer, column);
    _column = column;
  }

  public TimeSeriesTableColumn getColumn() {
    return _column;
  }

  @Override
  public void setEditingSupport(EditingSupport editingSupport) {
    super.setEditingSupport(editingSupport);
    _editingSupport = editingSupport;
  }

  EditingSupport doGetEditingSupport() {
    return _editingSupport;
  }

  private static TimeSeriesTableColumn createColumn(TimeSeriesTable table, int style, int index) {
    if (index >= 0)
      return new TimeSeriesTableColumn(table, style, index);

    return new TimeSeriesTableColumn(table, style);
  }
}
