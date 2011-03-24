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
package net.karlmartens.ui.widget;

import net.karlmartens.platform.util.NullSafe;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;

final class CellSelectionManager {

  private final TimeSeriesTable _table;
  private final CellNavigationStrategy _navigationStrategy;
  private final TableListener _listener;
  private final ItemListener _itemListener;
  private Point _focusCell;
  private Point _expansionCell;

  public CellSelectionManager(TimeSeriesTable table) {
    _table = table;
    _navigationStrategy = new CellNavigationStrategy();
    _listener = new TableListener();
    _itemListener = new ItemListener();
    hookListener();
  }

  public Point getFocusCell() {
    return _focusCell;
  }

  void setFocusCell(Point cell) {
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
    _table.addListener(SWT.KeyDown, _listener);
    _table.addListener(SWT.Selection, _listener);
    _table.addListener(SWT.Dispose, _listener);
  }

  private void handleDispose() {
    _table.removeListener(SWT.Dispose, _listener);
    _table.removeListener(SWT.MouseDown, _listener);
    _table.removeListener(SWT.KeyDown, _listener);
    _table.removeListener(SWT.Selection, _listener);
  }

  private void handleMouseDown(Event e) {
    final Point p = new Point(e.x, e.y);
    final TimeSeriesTableItem item = _table.getItem(p);
    if (item == null)
      return;

    if (!_table.isFocusControl())
      _table.setFocus();

    for (int i = 0; i < _table.getColumnCount() + _table.getPeriodCount(); i++) {
      if (_table.getBounds(item, i).contains(p)) {
        final Point cell = new Point(i, _table.indexOf(item));
        if ((e.stateMask & SWT.SHIFT) > 0) {
          expandSelection(cell);
        } else if (cell != null) {
          setFocusCell(cell);
        }
        return;
      }
    }
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

  private final class TableListener implements Listener {

    @Override
    public void handleEvent(Event event) {
      switch (event.type) {
        case SWT.MouseDown:
          handleMouseDown(event);
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
