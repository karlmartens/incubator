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

import net.karlmartens.platform.util.ReflectSupport;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColumnViewer;
import org.eclipse.jface.viewers.EditingSupport;

class EditingSupportProxy extends EditingSupport {

  EditingSupport _base;

  EditingSupportProxy(ColumnViewer viewer) {
    super(viewer);
  }

  @Override
  protected CellEditor getCellEditor(Object element) {
    throw new UnsupportedOperationException();
  }

  @Override
  protected boolean canEdit(Object element) {
    if (_base == null)
      return false;

    return Boolean.TRUE.equals(ReflectSupport.invoke("canEdit", _base, Object.class, element));
  }

  @Override
  protected Object getValue(Object element) {
    throw new UnsupportedOperationException();
  }

  @Override
  protected void setValue(Object element, Object value) {
    if (_base == null)
      return;

    ReflectSupport.invoke("setValue", _base, new Class[] {Object.class, Object.class}, new Object[] { element, value });
  }
}