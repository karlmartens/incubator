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

import net.karlmartens.platform.util.NullSafe;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColumnViewer;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.swt.widgets.Composite;

final class TestTextEditingSupport extends EditingSupport {

  private final ColumnViewer _viewer;
  private final int _index;
  private final int _style;
  private TextCellEditor _cellEditor;

  public TestTextEditingSupport(ColumnViewer viewer, int index, int style) {
    super(viewer);
    _viewer = viewer;
    _index = index;
    _style = style;
  }

  @Override
  protected CellEditor getCellEditor(Object element) {
    if (_cellEditor == null) {
      _cellEditor = new TextCellEditor((Composite) _viewer.getControl(), _style);
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
    return (String) data[_index];
  }

  @Override
  protected void setValue(Object element, Object value) {
    final Object[] data = (Object[]) element;
    data[_index] = NullSafe.toString(value);
  }
}