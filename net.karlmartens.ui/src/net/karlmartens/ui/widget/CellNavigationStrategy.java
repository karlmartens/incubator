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

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Event;

public final class CellNavigationStrategy {

  public boolean isNavigationEvent(Event event) {
    switch (event.keyCode) {
      case SWT.ARROW_LEFT:
      case SWT.ARROW_RIGHT:
      case SWT.ARROW_DOWN:
      case SWT.ARROW_UP:
      case SWT.HOME:
      case SWT.END:
      case SWT.PAGE_DOWN:
      case SWT.PAGE_UP:
      case SWT.CR:
        return (event.stateMask & SWT.SHIFT) == 0;

      case SWT.TAB:
        return true;
    }

    return false;
  }

  public boolean isExpandEvent(Event event) {
    switch (event.keyCode) {
      case SWT.ARROW_LEFT:
      case SWT.ARROW_RIGHT:
      case SWT.ARROW_DOWN:
      case SWT.ARROW_UP:
      case SWT.HOME:
      case SWT.END:
      case SWT.PAGE_DOWN:
      case SWT.PAGE_UP:
        return (event.stateMask & SWT.SHIFT) > 0;
    }

    return false;
  }

  public Point findSelectedCell(Table table, Point currentSelectedCell,
      Event event) {
    if (currentSelectedCell == null)
      return null;

    switch (event.keyCode) {
      case SWT.TAB:
        if ((event.stateMask & SWT.CTRL) == 0) {
          if ((event.stateMask & SWT.SHIFT) == 0) {
            return getNeighbor(table, currentSelectedCell, new Point(1, 0));
          }

          return getNeighbor(table, currentSelectedCell, new Point(-1, 0));
        }

        if ((event.stateMask & SWT.SHIFT) == 0) {
          return getNeighbor(table, currentSelectedCell, new Point(0, 1));
        }

        return getNeighbor(table, currentSelectedCell, new Point(0, -1));

      case SWT.ARROW_LEFT:
        if ((event.stateMask & SWT.CTRL) > 0) {
          return doPageLeft(table, currentSelectedCell, event);
        }

        if ((event.stateMask & SWT.MOD1) > 0) {
          return doHome(table, currentSelectedCell, event);
        }

        return getNeighbor(table, currentSelectedCell, new Point(-1, 0));

      case SWT.ARROW_RIGHT:
        if ((event.stateMask & SWT.CTRL) > 0) {
          return doPageRight(table, currentSelectedCell, event);
        }

        if ((event.stateMask & SWT.MOD1) > 0) {
          return doEnd(table, currentSelectedCell, event);
        }

        return getNeighbor(table, currentSelectedCell, new Point(1, 0));

      case SWT.ARROW_UP:
        if ((event.stateMask & SWT.MOD1) > 0) {
          return doPageUp(table, currentSelectedCell, event);
        }

        return getNeighbor(table, currentSelectedCell, new Point(0, -1));

      case SWT.ARROW_DOWN:
        if ((event.stateMask & SWT.MOD1) > 0) {
          return doPageDown(table, currentSelectedCell, event);
        }

        return getNeighbor(table, currentSelectedCell, new Point(0, 1));

      case SWT.HOME:
        return doHome(table, currentSelectedCell, event);

      case SWT.END:
        return doEnd(table, currentSelectedCell, event);

      case SWT.PAGE_UP:
        return doPageUp(table, currentSelectedCell, event);

      case SWT.PAGE_DOWN:
        return doPageDown(table, currentSelectedCell, event);
    }

    return null;
  }

  private Point getNeighbor(Table table, Point currentSelectedCell, Point delta) {
    final Point pt = new Point(currentSelectedCell.x, currentSelectedCell.y);
    final int columnCount = table.getColumnCount();
    final int itemCount = table.getItemCount();
    final int minY = table.getFixedHeaderRowCount();
    final int minX = table.getFixedHeaderColumnCount();
    if (delta.x == 0) {
      if (delta.y == 0)
        return currentSelectedCell;

      if (pt.x < minX || pt.x >= columnCount)
        return null;

      final TableColumn column = table.getColumn(pt.x);
      if (!column.isVisible())
        return null;

      for (;;) {
        pt.y += delta.y;
        if (pt.y < minY || pt.y >= itemCount)
          return null;

        final TableItem item = table.getItem(pt.y);
        if (item.isVisible())
          return pt;
      }
    }

    if (pt.y < minY || pt.y >= itemCount)
      return null;

    final TableItem item = table.getItem(pt.y);
    if (!item.isVisible())
      return null;

    for (;;) {
      pt.x += delta.x;
      if (pt.x < minX || pt.x >= columnCount)
        return null;

      final TableColumn column = table.getColumn(pt.x);
      if (column.isVisible())
        return pt;
    }
  }

  private Point doPageUp(Table table, Point currentSelectedCell, Event event) {
    final int numFixedRows = table.getFixedRowCount();
    if (numFixedRows >= table.getItemCount())
      return null;

    final int index = currentSelectedCell.y
        - table.getVisibleScrollableCells().height - 1;
    int y = Math.max(numFixedRows, index);
    while (!table.getItem(y).isVisible() && y < table.getItemCount()) {
      y++;
    }

    if (y >= table.getItemCount())
      return null;

    return new Point(currentSelectedCell.x, y);
  }

  private Point doPageDown(Table table, Point currentSelectedCell, Event event) {
    final int numFixedRows = table.getFixedRowCount();
    if (numFixedRows >= table.getItemCount())
      return null;

    final int index = currentSelectedCell.y
        + table.getVisibleScrollableCells().height - 1;
    final int maxIndex = table.getItemCount() - 1;
    int y = Math.min(index, maxIndex);
    while (!table.getItem(y).isVisible() && y >= 0) {
      y--;
    }

    if (y < 0)
      return null;

    return new Point(currentSelectedCell.x, y);
  }

  private Point doPageLeft(Table table, Point currentSelectedCell, Event event) {
    final int numFixedColumns = table.getFixedColumnCount();
    if (numFixedColumns >= table.getColumnCount())
      return null;

    final int index = currentSelectedCell.x
        - table.getVisibleScrollableCells().width - 1;
    int x = Math.max(numFixedColumns, index);
    while (!table.getColumn(x).isVisible() && x < table.getColumnCount()) {
      x++;
    }

    if (x == table.getColumnCount())
      return null;

    return new Point(x, currentSelectedCell.y);
  }

  private Point doPageRight(Table table, Point currentSelectedCell, Event event) {
    final int numFixedColumns = table.getFixedColumnCount();
    if (numFixedColumns >= table.getColumnCount())
      return null;

    final int max = table.getColumnCount() - 1;
    final int index = currentSelectedCell.x
        + table.getVisibleScrollableCells().width - 1;
    int x = Math.min(index, max);
    while (!table.getColumn(x).isVisible() && x >= 0) {
      x--;
    }

    if (x < 0)
      return null;

    return new Point(x, currentSelectedCell.y);
  }

  private Point doHome(Table table, Point currentSelectedCell, Event event) {
    final TableItem item = table.getItem(currentSelectedCell.y);
    final int fixedColumnCount = table.getFixedColumnCount();
    final int start = currentSelectedCell.x < fixedColumnCount ? 0
        : fixedColumnCount;
    final boolean skipValueTest = currentSelectedCell.x < fixedColumnCount
        || (currentSelectedCell.x != fixedColumnCount && item.getText(
            currentSelectedCell.x - 1).length() == 0);
    final int end = skipValueTest ? currentSelectedCell.x : table
        .getColumnCount();

    for (int i = start; i < end; i++) {
      if (table.getColumn(i).isVisible()
          && (item.getText(i).length() != 0 || skipValueTest)) {
        return new Point(i, currentSelectedCell.y);
      }
    }

    return null;
  }

  private Point doEnd(Table table, Point currentSelectedCell, Event event) {
    final TableItem item = table.getItem(currentSelectedCell.y);
    final int fixedColumnCount = table.getFixedColumnCount();
    final int start = fixedColumnCount
        - 1
        + (currentSelectedCell.x < fixedColumnCount ? 0 : table
            .getColumnCount() - fixedColumnCount);
    final boolean skipValueTest = currentSelectedCell.x < fixedColumnCount
        || (currentSelectedCell.x != start && item.getText(
            currentSelectedCell.x + 1).length() == 0);
    final int end = skipValueTest ? currentSelectedCell.x : fixedColumnCount;

    for (int i = start; i >= end; i--) {
      if (table.getColumn(i).isVisible()
          && (item.getText(i).length() != 0 || skipValueTest)) {
        return new Point(i, currentSelectedCell.y);
      }
    }

    return null;
  }
}
