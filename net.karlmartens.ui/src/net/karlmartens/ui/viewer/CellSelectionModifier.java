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
    final String[] strings = new String[cells.length];
    for (int i = 0; i < strings.length; i++) {
      final Point cell = cells[i];
      final TableItem item = _viewer.doGetItem(cell.y);
      strings[i] = item.getText(cell.x);
    }
    return strings;
  }

  protected final void setValues(Point[] cells, String[] values) {
    for (int i = 0; i < cells.length; i++) {
      final Point cell = cells[i];
      final TableItem item = _viewer.doGetItem(cell.y);
      final TableViewerRow row = _viewer.getViewerRowFromItem(item);
      final ViewerCell vCell = row.getCell(cell.x);
      
      _editSupport._base = getViewerColumn(cell.x).doGetEditingSupport();
      
      final CellEditor editor = _editSupport.getCellEditor(item.getData());
      editor.setValue(values[i]);
      
      _editSupport.saveCellEditorValue(editor, vCell);
    }
  }

  private TableViewerColumn getViewerColumn(int index) {
    final TableColumn column = (TableColumn) _viewer.doGetColumn(index);
    return (TableViewerColumn) column.getData(Policy.JFACE + ".columnViewer");
  }
}
