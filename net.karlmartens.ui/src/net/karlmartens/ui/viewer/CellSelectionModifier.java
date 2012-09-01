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

import net.karlmartens.ui.widget.TableColumn;
import net.karlmartens.ui.widget.TableItem;

import org.eclipse.jface.util.Policy;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.swt.graphics.Point;

public abstract class CellSelectionModifier {
  private final TableViewer _viewer;
  private final EditingSupportProxy _editSupport;

  protected CellSelectionModifier(TableViewer viewer) {
    _viewer = viewer;
    _editSupport = new EditingSupportProxy(viewer);
  }

  protected final boolean isEditable(Point[] cells) {
    for (int i = 0; i < cells.length; i++) {
      final Point cell = cells[i];
      _editSupport._base = getViewerColumn(cell.x).doGetEditingSupport();

      final TableItem item = _viewer.doGetItem(cell.y);
      if (!_editSupport.canEdit(item.getData())) {
        return false;
      }
    }

    return true;
  }

  protected final String[] getValues(Point[] cells) {
    final String[] values = new String[cells.length];
    for (int i = 0; i < values.length; i++) {
      final String value = getValue(cells[i]);
      values[i] = value;
    }
    return values;
  }

  protected final void setValues(Point[] cells, String[] values) {
    for (int i = 0; i < cells.length; i++) {
      setValue(cells[i], values[i]);
    }
  }

  private TableViewerColumn getViewerColumn(int index) {
    final TableColumn column = (TableColumn) _viewer.doGetColumn(index);
    return (TableViewerColumn) column.getData(Policy.JFACE + ".columnViewer");
  }

  private ViewerCell getViewerCell(Point pt) {
    final TableItem item = _viewer.doGetItem(pt.y);
    final TableViewerRow row = _viewer.getViewerRowFromItem(item);
    return row.getCell(pt.x);
  }

  private EditingSupport getEditingSupport(Point pt) {
    final EditingSupport editing = getViewerColumn(pt.x).doGetEditingSupport();
    if (editing != null)
      return editing;

    return new ReadonlyEditSupport(pt);
  }

  private String getValue(Point pt) {
    final ViewerCell cell = getViewerCell(pt);
    if (cell == null)
      return null;

    _editSupport._base = getEditingSupport(pt);
    final Object o = _editSupport.getValue(cell.getElement());
    return o.toString();
  }

  private void setValue(Point pt, String value) {
    final ViewerCell cell = getViewerCell(pt);
    if (cell == null)
      return;

    _editSupport._base = getEditingSupport(pt);
    _editSupport.setValue(cell.getElement(), value);
  }

  private class ReadonlyEditSupport extends EditingSupport {

    private final Point _pt;

    public ReadonlyEditSupport(Point pt) {
      super(_viewer);
      _pt = pt;
    }

    @Override
    protected CellEditor getCellEditor(Object element) {
      return null;
    }

    @Override
    protected boolean canEdit(Object element) {
      return false;
    }

    @Override
    protected Object getValue(Object element) {
      final ViewerCell vCell = getViewerCell(_pt);
      return vCell.getText();
    }

    @Override
    protected void setValue(Object element, Object value) {
      // Do nothing
    }

  }
}
