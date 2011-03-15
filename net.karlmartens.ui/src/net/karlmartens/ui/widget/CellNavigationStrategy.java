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

	public Point findSelectedCell(TimeSeriesTable table,
			Point currentSelectedCell, Event event) {
		if (currentSelectedCell == null)
			return null;

		switch (event.keyCode) {
			case SWT.TAB:
				if ((event.stateMask & SWT.CTRL) == 0) {
					if ((event.stateMask & SWT.SHIFT) == 0) {
						return getNeighbor(table, currentSelectedCell, new Point(1,
								0));
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

	private Point getNeighbor(TimeSeriesTable table, Point currentSelectedCell,
			Point delta) {
		final Point pt = new Point(currentSelectedCell.x, currentSelectedCell.y);
		for (;;) {
			pt.x += delta.x;
			pt.y += delta.y;
			if (pt.x < 0
					|| //
					pt.y < 0
					|| pt.x >= table.getColumnCount() + table.getPeriodCount()
					|| //
					pt.y >= table.getItemCount())
				return null;

			if (table.getColumn(pt.x).isVisible())
				return pt;
		}
	}

	private Point doPageUp(TimeSeriesTable table, Point currentSelectedCell,
			Event event) {
		if (table.getItemCount() <= 0)
			return null;

		final int y = Math.max(0,
				currentSelectedCell.y - (table.getVisibleRowCount() - 1));
		return new Point(currentSelectedCell.x, y);
	}

	private Point doPageDown(TimeSeriesTable table, Point currentSelectedCell,
			Event event) {
		if (table.getItemCount() <= 0)
			return null;

		final int y = Math.min(table.getItemCount() - 1, currentSelectedCell.y
				+ (table.getVisibleRowCount() - 1));
		return new Point(currentSelectedCell.x, y);
	}

	private Point doPageLeft(TimeSeriesTable table, Point currentSelectedCell,
			Event event) {
		if (table.getPeriodCount() <= 0)
			return null;

		final int x = Math.max(table.getColumnCount(), currentSelectedCell.x
				- (table.getVisibleColumnCount() - 1));
		return new Point(x, currentSelectedCell.y);
	}

	private Point doPageRight(TimeSeriesTable table, Point currentSelectedCell,
			Event event) {
		if (table.getPeriodCount() <= 0)
			return null;

		final int x = Math.min(table.getColumnCount() + table.getPeriodCount()
				- 1, currentSelectedCell.x
				+ (table.getVisibleColumnCount() - 1));
		return new Point(x, currentSelectedCell.y);
	}

	private Point doHome(TimeSeriesTable table, Point currentSelectedCell,
			Event event) {
		final TimeSeriesTableItem item = table.getItem(currentSelectedCell.y);
		final int columnCount = table.getColumnCount();
		final int start = currentSelectedCell.x < columnCount ? 0 : columnCount;
		final boolean skipValueTest = currentSelectedCell.x < columnCount
				|| (currentSelectedCell.x != columnCount && item.getText(
						currentSelectedCell.x - 1).length() == 0);
		final int end = skipValueTest ? currentSelectedCell.x : columnCount
				+ table.getPeriodCount();

		for (int i = start; i < end; i++) {
			if (table.getColumn(i).isVisible()
					&& (item.getText(i).length() != 0 || skipValueTest)) {
				return new Point(i, currentSelectedCell.y);
			}
		}

		return null;
	}

	private Point doEnd(TimeSeriesTable table, Point currentSelectedCell,
			Event event) {
		final TimeSeriesTableItem item = table.getItem(currentSelectedCell.y);
		final int columnCount = table.getColumnCount();
		final int start = columnCount
				- 1
				+ (currentSelectedCell.x < columnCount ? 0 : table
						.getPeriodCount());
		final boolean skipValueTest = currentSelectedCell.x < columnCount
				|| (currentSelectedCell.x != start && item.getText(
						currentSelectedCell.x + 1).length() == 0);
		final int end = skipValueTest ? currentSelectedCell.x : columnCount;

		for (int i = start; i >= end; i--) {
			if (table.getColumn(i).isVisible()
					&& (item.getText(i).length() != 0 || skipValueTest)) {
				return new Point(i, currentSelectedCell.y);
			}
		}

		return null;
	}
}
