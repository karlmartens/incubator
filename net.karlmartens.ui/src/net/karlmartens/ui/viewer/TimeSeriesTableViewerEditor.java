package net.karlmartens.ui.viewer;

import net.karlmartens.ui.widget.TimeSeriesTable;
import net.karlmartens.ui.widget.TimeSeriesTableEditor;
import net.karlmartens.ui.widget.TimeSeriesTableItem;

import org.eclipse.jface.viewers.CellEditor.LayoutData;
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

	public TimeSeriesTableViewerEditor(
			TimeSeriesTableViewer viewer,
			ColumnViewerEditorActivationStrategy strategy,
			int feature) {
		super(viewer, strategy, feature);
		_viewer = viewer;
		_editor = new TimeSeriesTableEditor(viewer.getControl());
		addEditorActivationListener(_listener);
	}

	public static void create(TimeSeriesTableViewer viewer, ColumnViewerEditorActivationStrategy activationStrategy, int feature) {
		final TimeSeriesTableViewerEditor editor = new TimeSeriesTableViewerEditor(viewer, activationStrategy, feature);
		viewer.setColumnViewerEditor(editor);
	}
	
	private TimeSeriesTableViewerRow _cachedRow;
	
	@Override
	public ViewerCell getFocusCell() {
		final TimeSeriesTable table = _viewer.getControl();
		final Point p = table.getFocusCell();
		if (p == null)
			return null;
		
		final TimeSeriesTableItem item = table.getItem(p.y);
		if (_cachedRow == null) {
			_cachedRow = new TimeSeriesTableViewerRow(item);
		} else {
			_cachedRow.setItem(item);
		}
		return _cachedRow.getCell(p.x);
	}

	@Override
	protected void setEditor(Control w, Item item, int columnIndex) {
		final TimeSeriesTableItem tableItem = (TimeSeriesTableItem)item;
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
		// nothing to do
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
			
			final ViewerCell cell = (ViewerCell)event.getSource();
			_viewer.getLabelProvider(cell.getColumnIndex()).update(cell);
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
