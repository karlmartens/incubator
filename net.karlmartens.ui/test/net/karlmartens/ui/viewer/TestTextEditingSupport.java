/**
 *  net.karlmartens.ui, is a library of UI widgets
 *  Copyright (C) 2010,2011
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

import net.karlmartens.platform.util.NullSafe;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColumnViewer;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.swt.widgets.Composite;

final class TestTextEditingSupport extends EditingSupport {

  private final ColumnViewer _viewer;
  private final int _index;

  public TestTextEditingSupport(ColumnViewer viewer, int index) {
    super(viewer);
    _viewer = viewer;
    _index = index;
  }

  @Override
  protected CellEditor getCellEditor(Object element) {
    return new TextCellEditor((Composite) _viewer.getControl());
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