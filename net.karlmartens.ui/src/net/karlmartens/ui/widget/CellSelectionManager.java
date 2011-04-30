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
package net.karlmartens.ui.widget;

import net.karlmartens.platform.util.NullSafe;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;

public final class CellSelectionManager {

  private final TimeSeriesTable _table;
  private final CellNavigationStrategy _navigationStrategy;
  private final TableListener _listener;
  private final ItemListener _itemListener;
  private Point _focusCell;
  private Point _expansionCell;
  private boolean _dragExpand = false;

  CellSelectionManager(TimeSeriesTable table) {
    _table = table;
    _navigationStrategy = new CellNavigationStrategy();
    _listener = new TableListener();
    _itemListener = new ItemListener();
    hookListener();
  }

  public Point getFocusCell() {
    return _focusCell;
  }

  public void setFocusCell(Point cell) {
    if (NullSafe.equals(cell, _focusCell))
      return;

    final TimeSeriesTableItem oldItem = getItemAtIndex(_focusCell);
    if (oldItem != null && !oldItem.isDisposed()) {
      oldItem.removeDisposeListener(_itemListener);
    }

    _focusCell = cell;
    _expansionCell = cell;

    final TimeSeriesTableItem newItem = getItemAtIndex(_focusCell);
    if (newItem != null && !newItem.isDisposed()) {
      newItem.addDisposeListener(_itemListener);
    }

    if (newItem != null) {
      _table.showItem(newItem);
      _table.showColumn(_focusCell.x);
    }

    _table.setCellSelections(_focusCell == null ? new Point[0] : new Point[] { _focusCell });
  }

  void expandSelection(Point cell) {
    if (cell == null)
      return;

    if (_focusCell == null) {
      if (cell != null) {
        setFocusCell(cell);
      }
      return;
    }

    final Point vFocusCell = new Point(_focusCell.x, _focusCell.y);
    final Point vCell = new Point(cell.x, cell.y);

    final int dirX = vFocusCell.x > vCell.x ? -1 : 1;
    final int dirY = vFocusCell.y > vCell.y ? -1 : 1;
    final int dx = Math.abs(vFocusCell.x - vCell.x) + 1;
    final int dy = Math.abs(vFocusCell.y - vCell.y) + 1;

    final Point[] selection = new Point[dx * dy];
    int index = 0;
    for (int y = 0; y < dy; y++) {
      final int row = vFocusCell.y;
      vFocusCell.y += dirY;
      vFocusCell.x = _focusCell.x;
      for (int x = 0; x < dx; x++) {
        selection[index++] = new Point(vFocusCell.x, row);
        vFocusCell.x += dirX;
      }
    }

    _expansionCell = cell;
    _table.setCellSelections(selection);
  }

  private TimeSeriesTableItem getItemAtIndex(Point pt) {
    if (pt == null)
      return null;

    return _table.getItem(pt.y);
  }

  private void hookListener() {
    _table.addListener(SWT.MouseDown, _listener);
    _table.addListener(SWT.MouseUp, _listener);
    _table.addListener(SWT.MouseMove, _listener);
    _table.addListener(SWT.KeyDown, _listener);
    _table.addListener(SWT.Selection, _listener);
    _table.addListener(SWT.Dispose, _listener);
  }

  private void handleDispose() {
    _table.removeListener(SWT.Dispose, _listener);
    _table.removeListener(SWT.MouseDown, _listener);
    _table.removeListener(SWT.MouseUp, _listener);
    _table.removeListener(SWT.MouseMove, _listener);
    _table.removeListener(SWT.KeyDown, _listener);
    _table.removeListener(SWT.Selection, _listener);
  }

  private void handleMouseDown(Event e) {
    if (!_table.isFocusControl())
      _table.setFocus();

    final Point cell = getCell(new Point(e.x, e.y));
    if (cell == null)
      return;

    if ((e.stateMask & SWT.SHIFT) > 0) {
      expandSelection(cell);
    } else if (cell != null) {
      setFocusCell(cell);
      _dragExpand = true;
    }
  }

  private void handleMouseUp(Event e) {
    _dragExpand = false;
  }

  private void handleMouseMove(Event e) {
    if (!_dragExpand)
      return;

    final Point cell = getCell(new Point(e.x, e.y));
    if (cell == null) {
      return;
    }

    expandSelection(cell);
  }

  private void handleKeyDown(Event e) {
    if (_navigationStrategy.isNavigationEvent(e)) {
      final Point cell = _navigationStrategy.findSelectedCell(_table, _focusCell, e);
      if (cell != null) {
        setFocusCell(cell);
      }
    }

    if (_navigationStrategy.isExpandEvent(e)) {
      final Point cell = _navigationStrategy.findSelectedCell(_table, _expansionCell, e);
      expandSelection(cell);
    }
  }

  private void handleSelection(Event e) {
    if (_focusCell == null || getItemAtIndex(_focusCell) == e.item || e.item == null || e.item.isDisposed())
      return;

    final int row = _table.indexOf((TimeSeriesTableItem) e.item);
    setFocusCell(new Point(_focusCell.x, row));
  }

  private void handleFocusIn(Event e) {
    if (_focusCell != null || _table.isDisposed() || _table.getItemCount() <= 0 || _table.getColumnCount() + _table.getPeriodCount() <= 0)
      return;

    setFocusCell(new Point(0, 0));
  }

  private Point getCell(Point position) {
    final TimeSeriesTableItem item = _table.getItem(position);
    if (item == null)
      return null;

    for (int i = 0; i < _table.getColumnCount() + _table.getPeriodCount(); i++) {
      if (_table.getBounds(item, i).contains(position)) {
        return new Point(i, _table.indexOf(item));
      }
    }

    return null;
  }

  private final class TableListener implements Listener {

    @Override
    public void handleEvent(Event event) {
      switch (event.type) {
        case SWT.MouseDown:
          handleMouseDown(event);
          break;

        case SWT.MouseUp:
          handleMouseUp(event);
          break;

        case SWT.MouseMove:
          handleMouseMove(event);
          break;

        case SWT.KeyDown:
          handleKeyDown(event);
          break;

        case SWT.Selection:
          handleSelection(event);
          break;

        case SWT.FocusIn:
          handleFocusIn(event);
          break;

        case SWT.Dispose:
          handleDispose();
          break;
      }
    }
  }

  private final class ItemListener implements DisposeListener {
    @Override
    public void widgetDisposed(DisposeEvent e) {
      setFocusCell(null);
    }
  }
}
