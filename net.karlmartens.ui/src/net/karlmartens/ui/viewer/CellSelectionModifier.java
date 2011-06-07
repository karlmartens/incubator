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

import java.text.NumberFormat;
import java.text.ParseException;

import net.karlmartens.ui.widget.TimeSeriesTable;
import net.karlmartens.ui.widget.TimeSeriesTableColumn;
import net.karlmartens.ui.widget.TimeSeriesTableItem;

import org.eclipse.jface.util.Policy;
import org.eclipse.swt.graphics.Point;

public abstract class CellSelectionModifier {
  private final TimeSeriesTableViewer _viewer;
  private final EditingSupportProxy _editSupport;

  protected CellSelectionModifier(TimeSeriesTableViewer viewer) {
    _viewer = viewer;
    _editSupport = new EditingSupportProxy(viewer);
  }

  protected final boolean isEditable(Point[] cells) {
    for (int i = 0; i < cells.length; i++) {
      final Point cell = cells[i];
      _editSupport._base = getViewerColumn(cell.x).doGetEditingSupport();

      final TimeSeriesTableItem item = _viewer.doGetItem(cell.y);
      if (!_editSupport.canEdit(item.getData())) {
        return false;
      }
    }

    return true;
  }

  protected final String[] getValues(Point[] cells) {
    final TimeSeriesTable control = _viewer.getControl();
    final int numFixedColumns = control.getColumnCount();
    final NumberFormat format = _viewer.getEditingSupport().getNumberFormat();

    final String[] strings = new String[cells.length];
    for (int i = 0; i < strings.length; i++) {
      final Point cell = cells[i];
      final TimeSeriesTableItem item = _viewer.doGetItem(cell.y);
      if (cell.x < numFixedColumns) {
        strings[i] = item.getText(cell.x);
        continue;
      }

      final double v = item.getValue(cell.x - numFixedColumns);
      strings[i] = format.format(v);
    }

    return strings;
  }

  protected final void setValues(Point[] cells, String[] values) {
    final TimeSeriesTable control = _viewer.getControl();
    final int numFixedColumns = control.getColumnCount();
    for (int i = 0; i < cells.length; i++) {
      final Point cell = cells[i];
      final TimeSeriesTableItem item = _viewer.doGetItem(cell.y);
      if (cell.x < numFixedColumns) {
        _editSupport._base = getViewerColumn(cell.x).doGetEditingSupport();
        _editSupport.setValue(item.getData(), values[i]);
      } else {
        final TimeSeriesEditingSupport valueEditSupport = _viewer.getEditingSupport();

        double value = 0.0;
        try {
          final NumberFormat format = valueEditSupport.getNumberFormat();
          final Number n = format.parse(values[i]);
          if (n != null) {
            value = n.doubleValue();
          }
        } catch (ParseException e) {
          // ignore
        }

        final int index = cell.x - numFixedColumns;
        valueEditSupport.setValue(item.getData(), index, value);
      }
    }
  }

  private TimeSeriesTableViewerColumn getViewerColumn(int index) {
    final TimeSeriesTableColumn column = (TimeSeriesTableColumn) _viewer.doGetColumn(index);
    return (TimeSeriesTableViewerColumn) column.getData(Policy.JFACE + ".columnViewer");
  }
}
