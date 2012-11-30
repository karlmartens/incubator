/**
 *   Copyright 2012 Karl Martens
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
package net.karlmartens.ui.widget;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.ui.IMemento;

/**
 * @author karl
 * 
 */
public final class TableMementoSupport {

  private static final String TAG_TABLE = "net.karlmartens.table";
  private static final String TAG_COLUMN = "column";
  private static final String TAG_INDEX = "index";
  private static final String TAG_WIDTH = "width";
  private static final String TAG_VISIBLE = "visible";

  private final Table _table;
  private final String _id;

  public TableMementoSupport(Table table, String id) {
    _table = table;
    _id = id;
  }

  public void saveState(IMemento memento) {
    if (memento == null)
      return;

    final IMemento tMemento = memento.createChild(TAG_TABLE, _id);

    for (int i = 0; i < _table.getColumnCount(); i++) {
      final TableColumn column = _table.getColumn(i);
      final String id = column.getId();
      if (id == null)
        continue;

      final IMemento cMemento = tMemento.createChild(TAG_COLUMN, id);
      cMemento.putInteger(TAG_INDEX, i);
      cMemento.putInteger(TAG_WIDTH, column.getWidth());
      cMemento.putBoolean(TAG_VISIBLE, column.isVisible());
    }
  }

  public void restoreState(IMemento memento) {
    if (memento == null)
      return;

    final IMemento tMemento = child(memento, TAG_TABLE, _id);
    if (tMemento == null)
      return;

    final Map<String, IMemento> children = new HashMap<String, IMemento>();
    for (IMemento cMemento : tMemento.getChildren(TAG_COLUMN)) {
      children.put(cMemento.getID(), cMemento);
    }

    final TableColumn[] columns = new TableColumn[_table.getColumnCount()];
    int columnCount = 0;
    for (int i = 0; i < columns.length; i++) {
      final TableColumn column = _table.getColumn(i);
      final String id = column.getId();
      if (id == null || !children.containsKey(id))
        continue;

      columns[columnCount++] = column;
    }

    for (int i = 0; i < columnCount; i++) {
      final TableColumn column = columns[i];
      final IMemento cMemento = children.get(column.getId());

      final Boolean visible = cMemento.getBoolean(TAG_VISIBLE);
      if (visible != null)
        column.setVisible(visible);

      final Integer width = cMemento.getInteger(TAG_WIDTH);
      if (width != null)
        column.setWidth(width);

      final Integer index = cMemento.getInteger(TAG_INDEX);
      if (index != null && index >= 0 && index < columns.length) {
        final int fromIdx = _table.indexOf(column);
        _table.moveColumn(fromIdx, index);
      }
    }
  }

  private IMemento child(IMemento memento, String type, String id) {
    for (IMemento candidate : memento.getChildren(type)) {
      if (id.equals(candidate.getID()))
        return candidate;
    }
    return null;
  }

}
