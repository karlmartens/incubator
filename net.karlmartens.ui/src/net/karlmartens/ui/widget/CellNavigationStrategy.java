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

  public boolean isSelectAllEvent(Event event) {
    switch (event.keyCode) {
      case 'a':
      case 'A':
        return (event.stateMask & SWT.MOD1) > 0;
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
    for (;;) {
      pt.x += delta.x;
      pt.y += delta.y;
      if (pt.x < 0 || //
          pt.y < 0 || pt.x >= table.getColumnCount() || //
          pt.y >= table.getItemCount())
        return null;

      if (table.getColumn(pt.x).isVisible())
        return pt;
    }
  }

  private Point doPageUp(Table table, Point currentSelectedCell, Event event) {
    final int numFixedRows = table.getFixedRowCount();
    if (numFixedRows >= table.getItemCount())
      return null;

    final int y = Math.max(numFixedRows,
        currentSelectedCell.y - (table.getVisibleRowCount() - 1));
    return new Point(currentSelectedCell.x, y);
  }

  private Point doPageDown(Table table, Point currentSelectedCell, Event event) {
    final int numFixedRows = table.getFixedRowCount();
    if (numFixedRows >= table.getItemCount())
      return null;

    final int y = Math.min(table.getItemCount() - 1, currentSelectedCell.y
        + (table.getVisibleRowCount() - 1));
    return new Point(currentSelectedCell.x, y);
  }

  private Point doPageLeft(Table table, Point currentSelectedCell, Event event) {
    final int numFixedColumns = table.getFixedColumnCount();
    if (numFixedColumns >= table.getColumnCount())
      return null;

    final int x = Math.max(numFixedColumns,
        currentSelectedCell.x - (table.getVisibleColumnCount() - 1));
    return new Point(x, currentSelectedCell.y);
  }

  private Point doPageRight(Table table, Point currentSelectedCell, Event event) {
    final int numFixedColumns = table.getFixedColumnCount();
    if (numFixedColumns >= table.getColumnCount())
      return null;

    final int x = Math.min(table.getColumnCount() - 1, currentSelectedCell.x
        + (table.getVisibleColumnCount() - 1));
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
