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

import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.graphics.Point;

import net.karlmartens.ui.widget.CellSelectionManager;

public final class ViewerCellSelectionManager {

  private final TimeSeriesTableViewer _viewer;
  private final CellSelectionManager _manager;

  public ViewerCellSelectionManager(TimeSeriesTableViewer viewer) {
    _viewer = viewer;
    _manager = _viewer.getControl().getCellSelectionManager();
    viewer.addSelectionChangedListener(_listener);
    viewer.getControl().addDisposeListener(_listener);
  }

  private void handleDispose() {
    _viewer.removeSelectionChangedListener(_listener);
    _viewer.getControl().removeDisposeListener(_listener);
  }

  private void handleSelectionChanged() {
    if (_viewer.getSelection().isEmpty()) {
      _manager.setFocusCell(null);
    }
  }

  private final Listener _listener = new Listener();

  private class Listener implements ISelectionChangedListener, DisposeListener {
    @Override
    public void widgetDisposed(DisposeEvent e) {
      handleDispose();
    }

    @Override
    public void selectionChanged(SelectionChangedEvent event) {
      handleSelectionChanged();
    }
  }

  public Point getFocusCell() {
    return _manager.getFocusCell();
  }

  public void setFocusCell(Point cell) {
    if (cell != null) {
      _manager.setFocusCell(cell);
    }
  }
}
