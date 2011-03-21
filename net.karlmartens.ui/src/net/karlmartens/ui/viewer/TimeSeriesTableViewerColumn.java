/**
 *  net.karlmartens.ui, is a library of UI widgets
 *  Copyright (C) 2011
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
