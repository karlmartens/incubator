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

import net.karlmartens.ui.action.ResizeAllColumnsAction;
import net.karlmartens.ui.action.ResizeColumnAction;
import net.karlmartens.ui.action.ToggleColumnVisibiltyAction;
import net.karlmartens.ui.widget.TimeSeriesTable.KTableImpl;

import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.MenuDetectEvent;
import org.eclipse.swt.events.MenuDetectListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Shell;

final class TimeSeriesColumnManager {

	private final TimeSeriesTable _container;
	private final KTableImpl _table;
	private final MenuManager _columnMenu;
	private final ResizeColumnAction _resizeColumnAction;
	private final ResizeAllColumnsAction _resizeAllColumnsAction;

	private TimeSeriesTableColumn _column;
	private Integer _columnIndex;
	private Point _offset;
	private Image _image;
	private Shell _shell;
	private boolean _selection = false;

	TimeSeriesColumnManager(TimeSeriesTable container, KTableImpl table) {
		_container = container;
		_table = table;

		_columnMenu = new MenuManager();

		_resizeColumnAction = new ResizeColumnAction(_container, -1);
		_resizeAllColumnsAction = new ResizeAllColumnsAction(_container);

		hookControl();
	}

	private void hookControl() {
		_table.addDisposeListener(_widgetListener);
		_table.addMouseListener(_widgetListener);
		_table.addMouseMoveListener(_widgetListener);
		_table.addMenuDetectListener(_widgetListener);
	}

	private void releaseControl() {
		_columnMenu.dispose();

		_table.removeDisposeListener(_widgetListener);
		_table.removeMouseListener(_widgetListener);
		_table.removeMouseMoveListener(_widgetListener);
		_table.removeMenuDetectListener(_widgetListener);
	}

	private Display getDisplay() {
		return _table.getDisplay();
	}

	private Menu buildMenu(int columnIndex) {
		_resizeColumnAction.setColumnIndex(columnIndex);

		_columnMenu.removeAll();
		_columnMenu.add(_resizeColumnAction);
		_columnMenu.add(_resizeAllColumnsAction);
		_columnMenu.add(new Separator());
		for (int i = 0; i < _container.getColumnCount(); i++) {
			final TimeSeriesTableColumn column = _container.getColumn(i);
			_columnMenu.add(new ToggleColumnVisibiltyAction(column));
		}
		_columnMenu.update();
		_columnMenu.createContextMenu(_table);
		return _columnMenu.getMenu();
	}

	private void initiateColumnMove(MouseEvent e, int colIndex) {
		_columnIndex = colIndex;
		_table.setIgnoreMouseMove(true);

		final Rectangle cellCords = _table.getCellRect(colIndex, 0);
		final int height = _table.getClientArea().height;
		_offset = new Point(cellCords.x - e.x, cellCords.y - e.y);

		if (_image != null)
			_image.dispose();
		_image = new Image(getDisplay(), new Rectangle(0, 0, cellCords.width,
				height));
		_image.getImageData().alpha = 0;

		final GC gc = new GC(_table);
		gc.copyArea(_image, cellCords.x, cellCords.y);
		gc.dispose();
	}

	private void showColumnMoveEffect() {
		_table.setCursor(getDisplay().getSystemCursor(SWT.CURSOR_HAND));

		_shell = new Shell(getDisplay(), SWT.NO_TRIM);
		_shell.setAlpha(200);
		_shell.setLayout(new FillLayout());
		_shell.setBounds(_image.getBounds());

		final Label l = new Label(_shell, SWT.NONE);
		l.setImage(_image);
		l.setBackground(getDisplay().getSystemColor(SWT.COLOR_DARK_GRAY));

		_shell.open();
	}

	private void cancelColumnMove() {
		_columnIndex = null;
		_offset = null;
		_table.setIgnoreMouseMove(false);

		if (_image != null && !_image.isDisposed())
			_image.dispose();
		_image = null;

		if (_shell != null && !_shell.isDisposed())
			_shell.dispose();
		_shell = null;

		_table.setCursor(getDisplay().getSystemCursor(SWT.CURSOR_ARROW));
	}

	private boolean isColumnMoveActive() {
		return _columnIndex != null;
	}

	private void initiateSelection() {
		_selection = true;
	}

	private void cancelSelection() {
		_selection = false;
	}

	private boolean isSelectionActive() {
		return _selection;
	}

	private final Listener _widgetListener = new Listener();

	private final class Listener implements MouseListener, MouseMoveListener,
			MenuDetectListener, DisposeListener {

		@Override
		public void mouseDoubleClick(MouseEvent e) {
			// ignore
		}

		@Override
		public void mouseDown(MouseEvent e) {
			if (_table != e.getSource()) {
				cancelColumnMove();
				cancelSelection();
				return;
			}

			if (e.button != 1) {
				cancelColumnMove();
				cancelSelection();
				return;
			}

			final Point cellCord = _table.getCellForCoordinates(e.x, e.y);
			if (cellCord.y < 0
					|| cellCord.y >= _table.getModel().getFixedHeaderRowCount()) {
				cancelColumnMove();
				cancelSelection();
				return;
			}

			if (cellCord.x < 0
					|| cellCord.x >= (_container.getColumnCount() + _container
							.getPeriodCount())) {
				cancelColumnMove();
				cancelSelection();
				return;
			}

			final Rectangle r = _table.getCellRect(cellCord.x, cellCord.y);
			if (e.x - r.x <= 5 || r.x + r.width - e.x <= 5) {
				cancelColumnMove();
				cancelSelection();
				return;
			}

			_column = _container.getColumn(cellCord.x);
			initiateSelection();

			if (!_column.isMoveable()) {
				cancelColumnMove();
				return;
			}

			initiateColumnMove(e, cellCord.x);
		}

		@Override
		public void mouseUp(MouseEvent e) {
			if (!isColumnMoveActive())
				return;

			if (_table != e.getSource()) {
				cancelColumnMove();
				cancelSelection();
				return;
			}

			final Point cellCord = _table.getCellForCoordinates(e.x, e.y);
			final int oldColumnIndex = _columnIndex.intValue();
			if (cellCord.x < 0 || cellCord.x >= _container.getColumnCount()
					|| oldColumnIndex == cellCord.x) {
				cancelColumnMove();

				if (isSelectionActive()) {
					_column.notifyListeners(SWT.Selection, new Event());
					cancelSelection();
				}
				return;
			}

			_container.moveColumn(oldColumnIndex, cellCord.x);
			cancelColumnMove();
			cancelSelection();
		}

		@Override
		public void mouseMove(MouseEvent e) {
			cancelSelection();
			if (!isColumnMoveActive())
				return;

			if (_table != e.getSource()) {
				cancelColumnMove();
				return;
			}

			if (_shell == null) {
				showColumnMoveEffect();
			}

			final Rectangle rLastCol = _table.getCellRect(
					_container.getColumnCount() - 1, 0);
			final Point p = _container.toDisplay(_table.getLocation());
			p.x += Math.max(Math.min(e.x + _offset.x, rLastCol.x), 0);
			_shell.setLocation(p);
		}

		@Override
		public void menuDetected(MenuDetectEvent e) {
			final Point cord = _table.toControl(e.x, e.y);
			final Point cell = _table.getCellForCoordinates(cord.x, cord.y);
			if (_table.isFixedCell(cell.x, cell.y)) {
				final Menu menu = buildMenu(cell.x);
				if (menu != null && !menu.isDisposed()) {
					menu.setLocation(e.x, e.y);
					menu.setVisible(true);
				}
			}
		}

		@Override
		public void widgetDisposed(DisposeEvent e) {
			releaseControl();
		}
	}
}
