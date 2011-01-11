package net.karlmartens.ui.viewer;

import net.karlmartens.ui.widget.TimeSeriesTable;
import net.karlmartens.ui.widget.TimeSeriesTableColumn;
import net.karlmartens.ui.widget.TimeSeriesTableItem;

import org.eclipse.jface.viewers.AbstractTableViewer;
import org.eclipse.jface.viewers.ColumnViewerEditor;
import org.eclipse.jface.viewers.ColumnViewerEditorActivationStrategy;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Item;
import org.eclipse.swt.widgets.Widget;

public final class TimeSeriesTableViewer extends AbstractTableViewer {

	private final TimeSeriesTable _control;
	private TimeSeriesTableViewerRow _cachedRow;

	public TimeSeriesTableViewer(Composite parent) {
		this(new TimeSeriesTable(parent));
	}
	
	public TimeSeriesTableViewer(TimeSeriesTable control) {
		_control = control;
		hookControl(control);
	}

	@Override
	protected void doClear(int index) {
		_control.clear(index);
	}

	@Override
	protected void doClearAll() {
		_control.clearAll();
	}

	@Override
	protected void doDeselectAll() {
		_control.deselectAll();
	}

	@Override
	protected TimeSeriesTableColumn doGetColumn(int index) {
		return _control.getColumn(index);
	}

	@Override
	protected TimeSeriesTableItem doGetItem(int index) {
		return _control.getItem(index);
	}

	@Override
	protected int doGetItemCount() {
		return _control.getItemCount();
	}

	@Override
	protected TimeSeriesTableItem[] doGetItems() {
		return _control.getItems();
	}

	@Override
	protected TimeSeriesTableItem[] doGetSelection() {
		return _control.getSelection();
	}

	@Override
	protected int[] doGetSelectionIndices() {
		return _control.getSelectionIndices();
	}

	@Override
	protected int doIndexOf(Item item) {
		return _control.indexOf((TimeSeriesTableItem)item);
	}

	@Override
	protected void doRemove(int[] indices) {
		_control.remove(indices);
	}

	@Override
	protected void doRemove(int start, int end) {
		_control.remove(start, end);
	}

	@Override
	protected void doRemoveAll() {
		_control.removeAll();
	}

	@Override
	protected void doResetItem(Item item) {
		final TimeSeriesTableItem actualItem = (TimeSeriesTableItem)item;
		final int columnCount = Math.max(1, _control.getColumnCount());
		for (int i=0; i<columnCount; i++) {
			actualItem.setText(i, "");
			if (actualItem.getImage(i) != null) {
				actualItem.setImage(i, null);
			}
		}
	}

	@Override
	protected void doSelect(int[] indices) {
		_control.select(indices);
	}

	@Override
	protected void doSetItemCount(int count) {
		_control.setItemCount(count);
	}

	@Override
	protected void doSetSelection(Item[] items) {
		final TimeSeriesTableItem[] actualItems = new TimeSeriesTableItem[items.length];
		System.arraycopy(items, 0, actualItems, 0, actualItems.length);
		_control.setSelection(actualItems);
	}

	@Override
	protected void doSetSelection(int[] indices) {
		_control.setSelection(indices);
	}

	@Override
	protected void doShowItem(Item item) {
		_control.showItem((TimeSeriesTableItem)item);
	}

	@Override
	protected void doShowSelection() {
		_control.showSelection();
	}

	@Override
	protected TimeSeriesTableViewerRow internalCreateNewRowPart(int style, int rowIndex) {
		final TimeSeriesTableItem item;
		if (rowIndex >= 0) {
			item = new TimeSeriesTableItem(_control, style, rowIndex);
		} else {
			item = new TimeSeriesTableItem(_control, style);
		}

		return getViewerRowFromItem(item);
	}

	@Override
	protected TimeSeriesViewerEditor createViewerEditor() {
		return new TimeSeriesViewerEditor(this, 
				new ColumnViewerEditorActivationStrategy(this), 
				ColumnViewerEditor.DEFAULT);
	}

	@Override
	protected int doGetColumnCount() {
		return _control.getColumnCount();
	}

	@Override
	protected TimeSeriesTableItem getItemAt(Point point) {
		return _control.getItem(point);
	}

	@Override
	protected TimeSeriesTableViewerRow getViewerRowFromItem(Widget item) {
		if (_cachedRow == null) {
			_cachedRow = new TimeSeriesTableViewerRow((TimeSeriesTableItem)item);
		} else {
			_cachedRow.setItem((TimeSeriesTableItem)item);
		}

		return _cachedRow;
	}

	@Override
	public TimeSeriesTable getControl() {
		return _control;
	}

}
