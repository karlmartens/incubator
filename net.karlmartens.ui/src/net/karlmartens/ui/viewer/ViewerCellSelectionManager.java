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

import net.karlmartens.ui.widget.CellSelectionManager;
import net.karlmartens.ui.widget.TimeSeriesTable;

import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.graphics.Point;

public final class ViewerCellSelectionManager {

  private final TimeSeriesTableViewer _viewer;
  private final CellSelectionManager _manager;

  public ViewerCellSelectionManager(TimeSeriesTableViewer viewer) {
    _viewer = viewer;
    viewer.addSelectionChangedListener(_listener);

    final TimeSeriesTable control = _viewer.getControl();
    _manager = control.getCellSelectionManager();

    control.addDisposeListener(_listener);
  }

  private void handleDispose() {
    _viewer.removeSelectionChangedListener(_listener);

    final TimeSeriesTable control = _viewer.getControl();
    control.removeDisposeListener(_listener);
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
