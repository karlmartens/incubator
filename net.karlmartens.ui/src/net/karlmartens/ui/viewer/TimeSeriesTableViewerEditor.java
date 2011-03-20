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

import net.karlmartens.ui.widget.TimeSeriesTable;
import net.karlmartens.ui.widget.TimeSeriesTableEditor;
import net.karlmartens.ui.widget.TimeSeriesTableItem;
import net.karlmartens.ui.widget.ViewerCellSelectionManager;

import org.eclipse.jface.viewers.CellEditor.LayoutData;
import org.eclipse.jface.viewers.CellLabelProvider;
import org.eclipse.jface.viewers.ColumnViewerEditor;
import org.eclipse.jface.viewers.ColumnViewerEditorActivationEvent;
import org.eclipse.jface.viewers.ColumnViewerEditorActivationListener;
import org.eclipse.jface.viewers.ColumnViewerEditorActivationStrategy;
import org.eclipse.jface.viewers.ColumnViewerEditorDeactivationEvent;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Item;

public final class TimeSeriesTableViewerEditor extends ColumnViewerEditor {

	private final TimeSeriesTableViewer _viewer;
	private final TimeSeriesTableEditor _editor;
	private final ViewerCellSelectionManager _selectionManager;

	public TimeSeriesTableViewerEditor(TimeSeriesTableViewer viewer,
			ColumnViewerEditorActivationStrategy strategy, int feature) {
		super(viewer, strategy, feature);
		_viewer = viewer;
		_editor = new TimeSeriesTableEditor(viewer.getControl());
		_selectionManager = new ViewerCellSelectionManager(viewer);
		addEditorActivationListener(_listener);
	}

	public static void create(TimeSeriesTableViewer viewer,
			ColumnViewerEditorActivationStrategy activationStrategy, int feature) {
		final TimeSeriesTableViewerEditor editor = new TimeSeriesTableViewerEditor(
				viewer, activationStrategy, feature);
		viewer.setColumnViewerEditor(editor);
	}

	private TimeSeriesTableViewerRow _cachedRow;

	@Override
	public ViewerCell getFocusCell() {
		final Point p = _selectionManager.getFocusCell();
		if (p == null)
			return null;

		final TimeSeriesTableItem item = _viewer.getControl().getItem(p.y);
		if (_cachedRow == null) {
			_cachedRow = new TimeSeriesTableViewerRow(item);
		} else {
			_cachedRow.setItem(item);
		}
		return _cachedRow.getCell(p.x);
	}

	@Override
	protected void setEditor(Control w, Item item, int columnIndex) {
		final TimeSeriesTableItem tableItem = (TimeSeriesTableItem) item;
		_editor.setEditor(w, tableItem, columnIndex);
	}

	@Override
	protected void setLayoutData(LayoutData layoutData) {
		_editor.grabHorizontal = layoutData.grabHorizontal;
		_editor.horizontalAlignment = layoutData.horizontalAlignment;
		_editor.minimumWidth = layoutData.minimumWidth;
		_editor.verticalAlignment = layoutData.verticalAlignment;

		if (layoutData.minimumHeight != SWT.DEFAULT) {
			_editor.minimumHeight = layoutData.minimumHeight;
		}
	}

	@Override
	protected void updateFocusCell(ViewerCell focusCell,
			ColumnViewerEditorActivationEvent event) {
		if (event.eventType == ColumnViewerEditorActivationEvent.PROGRAMMATIC
				|| event.eventType == ColumnViewerEditorActivationEvent.TRAVERSAL) {
			final TimeSeriesTable table = _viewer.getControl();
			final int row = table.indexOf((TimeSeriesTableItem) focusCell
					.getItem());
			final int col = focusCell.getColumnIndex();
			_selectionManager.setFocusCell(new Point(col, row));
		}
	}

	private final ColumnViewerEditorActivationListener _listener = new ColumnViewerEditorActivationListener() {
		@Override
		public void beforeEditorActivated(
				ColumnViewerEditorActivationEvent event) {
			_viewer.getControl().addSelectionListener(_selectionListener);
		}

		@Override
		public void afterEditorActivated(ColumnViewerEditorActivationEvent event) {
			// nothing to do
		}

		@Override
		public void beforeEditorDeactivated(
				ColumnViewerEditorDeactivationEvent event) {
			// Nothing to do
		}

		@Override
		public void afterEditorDeactivated(
				ColumnViewerEditorDeactivationEvent event) {
			final TimeSeriesTable table = _viewer.getControl();
			table.removeSelectionListener(_selectionListener);

			final ViewerCell cell = (ViewerCell) event.getSource();
			final CellLabelProvider labelProvider = _viewer
					.getLabelProvider(cell.getColumnIndex());
			if (labelProvider != null)
				labelProvider.update(cell);

			table.redraw();
		}
	};

	private SelectionListener _selectionListener = new SelectionAdapter() {
		@Override
		public void widgetSelected(SelectionEvent e) {
			final Control control = _editor.getEditor();
			if (control == null || control.isDisposed())
				return;

			control.notifyListeners(SWT.FocusOut, new Event());
		}
	};
}
