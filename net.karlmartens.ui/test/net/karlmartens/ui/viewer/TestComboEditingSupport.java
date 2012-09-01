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

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColumnViewer;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.swt.widgets.Composite;

final class TestComboEditingSupport extends EditingSupport {

  static final String[] ITEMS = { "Red", "Green", "Blue", "Orange", "Yellow",
      "Purple" };

  private final ColumnViewer _viewer;
  private final int _index;
  private ComboBoxCellEditor _cellEditor;

  public TestComboEditingSupport(ColumnViewer viewer, int index) {
    super(viewer);
    _viewer = viewer;
    _index = index;
  }

  @Override
  protected CellEditor getCellEditor(Object element) {
    if (_cellEditor == null) {
      _cellEditor = new ComboBoxCellEditor((Composite) _viewer.getControl(),
          ITEMS);
      _cellEditor
          .setActivationStyle(ComboBoxCellEditor.DROP_DOWN_ON_KEY_ACTIVATION
              | ComboBoxCellEditor.DROP_DOWN_ON_MOUSE_ACTIVATION
              | ComboBoxCellEditor.DROP_DOWN_ON_PROGRAMMATIC_ACTIVATION
              | ComboBoxCellEditor.DROP_DOWN_ON_TRAVERSE_ACTIVATION);
    }
    return _cellEditor;
  }

  @Override
  protected boolean canEdit(Object element) {
    return true;
  }

  @Override
  protected Object getValue(Object element) {
    final Object[] data = (Object[]) element;
    return data[_index];
  }

  @Override
  protected void setValue(Object element, Object value) {
    final Object[] data = (Object[]) element;
    final int index = indexOf(value);
    data[_index] = index == -1 ? null : ITEMS[index];
  }

  private int indexOf(Object value) {
    for (int i = 0; i < ITEMS.length; i++) {
      if (ITEMS[i].equals(value)) {
        return i;
      }
    }

    return -1;
  }
}