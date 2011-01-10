package net.karlmartens.ui.widget;

import java.util.Arrays;

import net.karlmartens.platform.text.LocalDateFormat;
import net.karlmartens.platform.util.Periodicity;
import net.karlmartens.ui.widget.TimeSeriesTableModel.Column;
import net.karlmartens.ui.widget.TimeSeriesTableModel.Row;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Composite;
import org.joda.time.LocalDate;

import com.jidesoft.grid.MultiTableModel;


public final class TimeSeriesTable extends Composite {
	
	private final TimeSeriesTableModel _model = new TimeSeriesTableModel();
	
	private Periodicity _periodicity = Periodicity.MONTHLY;
	private LocalDate _start = new LocalDate().withDayOfMonth(1);
	private int _numberOfPeriods = 60;
	private int _viewportIndex = 0;
	private int _viewportSize = 12;
	private LocalDateFormat _format;
	
	private TimeSeriesTableColumn[] _attributeColumns = new TimeSeriesTableColumn[4];
	private int _attributeColumnCount = 0;
	private TimeSeriesTableItem[] _items = new TimeSeriesTableItem[4];
	private int _itemCount = 0;
	private int _lastIndexOf = 0;

	public TimeSeriesTable(Composite parent) {
		super(parent, SWT.NONE);
		setLayout(new FormLayout());
		
		
	}
	
	public void setPeriodicity(Periodicity periodicity) {
		checkWidget();
		if (periodicity == null)
			SWT.error(SWT.ERROR_NULL_ARGUMENT);
		
		_periodicity = periodicity;
		// TODO cause widgets to refresh
	}
	
	public Periodicity getPeriodicity() {
		checkWidget();
		return _periodicity;
	}
	
	public void setStart(LocalDate date) {
		checkWidget();
		if (date == null)
			SWT.error(SWT.ERROR_NULL_ARGUMENT);
		
		_start = date;
		// TODO cause widgets to refresh
	}
	
	public LocalDate getStart() {
		checkWidget();
		return _start;
	}
	
	public void setNumberOfPeriods(int size) {
		checkWidget();
		if (size <= 0)
			SWT.error(SWT.ERROR_INVALID_RANGE);
		
		_numberOfPeriods = size;
		// TODO cause widgets to refresh
	}
	
	public int getNumberOfPeriods() {
		checkWidget();
		return _numberOfPeriods;
	}
	
	public void setViewportIndex(int index) {
		checkWidget();
		if (index < 0 || index > _numberOfPeriods - 1)
			SWT.error(SWT.ERROR_INVALID_RANGE);
		
		_viewportIndex = index;
		// cause widgets to refresh
	}
	
	public int getViewportIndex() {
		checkWidget();
		return _viewportIndex;
	}
	
	public void setViewportSize(int size) {
		checkWidget();
		if (size <= 0 || _numberOfPeriods < _viewportIndex + size)
			SWT.error(SWT.ERROR_INVALID_RANGE);
		
		_viewportSize = size;
		// cause widgets to refresh
	}
	
	public int getViewportSize() {
		checkWidget();
		return _viewportSize;
	}
	
	public void setDateFormat(LocalDateFormat format) {
		checkWidget();
		_format = format;
	}
	
	public LocalDateFormat getDateFormat() {
		checkWidget();
		return _format;
	}

	public int getAttributeColumnCount() {
		checkWidget();
		return _attributeColumnCount;
	}

	public int getColumnCount() {
		checkWidget();
		// TODO Auto-generated method stub
		return 0;
	}

	public TimeSeriesTableColumn getColumn(int index) {
		checkWidget();
		// TODO Auto-generated method stub
		return null;
	}

	public int getItemCount() {
		checkWidget();
		return _itemCount;
	}

	public void setItemCount(int count) {
		checkWidget();
		final int c = Math.max(0, count);
		if (c == _itemCount)
			return;
		
		if (c > _itemCount) {
			for (int i=_itemCount; i<c; i++) {
				new TimeSeriesTableItem(this, SWT.NONE, i);
			}
			return;
		}
		
		for (int i=c; i<_itemCount; i++) {
			final TimeSeriesTableItem item = _items[i];
			if (item != null && !item.isDisposed())
				item.release();
			_items[i] = null;
		}
		
		final int length = Math.max(4, (c + 3) / 4 * 4);
		final TimeSeriesTableItem[] newItems = new TimeSeriesTableItem[length];
		System.arraycopy(_items, 0, newItems, 0, c);
		_items = newItems;
		_itemCount = c;
	}

	public TimeSeriesTableItem[] getItems() {
		checkWidget();
		final TimeSeriesTableItem[] items = new TimeSeriesTableItem[_itemCount];
		System.arraycopy(_items, 0, items, 0, items.length);
		return items;
	}

	public TimeSeriesTableItem getItem(int index) {
		checkWidget();
		if (index < 0 || index >= _itemCount)
			SWT.error(SWT.ERROR_INVALID_RANGE);
		return _items[index];
	}

	public int indexOf(TimeSeriesTableItem item) {
		checkWidget();
		if (item == null)
			SWT.error(SWT.ERROR_NULL_ARGUMENT);
		
		if (_lastIndexOf >= 1 && _lastIndexOf < _itemCount - 1) {
			if (_items[_lastIndexOf] == item) return _lastIndexOf;
			if (_items[_lastIndexOf + 1] == item) return ++_lastIndexOf;
			if (_items[_lastIndexOf - 1] == item) return --_lastIndexOf;
		}
		
		if (_lastIndexOf < _itemCount / 2) {
			for (int i=0; i<_itemCount; i++) {
				if (_items[i] == item) {
					_lastIndexOf = i;
					return i;
				}
			}
		} else {
			for (int i=_itemCount-1; i>=0; i--) {
				if (_items[i] == item) {
					_lastIndexOf = i;
					return i;
				}
			}
		}
		
		return -1;
	}

	public TimeSeriesTableItem getItem(Point point) {
		checkWidget();
		// TODO Auto-generated method stub
		return null;
	}

	public TimeSeriesTableItem[] getSelection() {
		checkWidget();
		// TODO Auto-generated method stub
		return null;
	}

	public int[] getSelectionIndices() {
		checkWidget();
		// TODO Auto-generated method stub
		return null;
	}

	public void setSelection(TimeSeriesTableItem[] actualItems) {
		checkWidget();
		// TODO Auto-generated method stub
		
	}

	public void setSelection(int[] indices) {
		checkWidget();
		// TODO Auto-generated method stub
		
	}

	public void select(int[] indices) {
		checkWidget();
		// TODO Auto-generated method stub
		
	}

	public void deselectAll() {
		checkWidget();
		// TODO Auto-generated method stub
		
	}

	public void clear(int index) {
		checkWidget();
		// TODO Auto-generated method stub
		
	}

	public void clearAll() {
		checkWidget();
		// TODO Auto-generated method stub
		
	}

	public void remove(int[] indices) {
		checkWidget();
		if (indices == null) SWT.error(SWT.ERROR_NULL_ARGUMENT);
		if (indices.length == 0) return;
		
		final int[] working = new int[indices.length];
		System.arraycopy(indices, 0, working, 0, working.length);
		Arrays.sort(working);
		
		final int start = working[0];
		final int end = working[working.length - 1];
		if (start < 0 || start > end || end >= _itemCount)
			SWT.error(SWT.ERROR_INVALID_RANGE);
		
		int last = -1;
		for (int i=working.length-1; i>0; i--) {
			final int index = working[i];
			if (index != last) {
				internalRemove(index);
				last = index;
			}
		}
	}

	public void remove(int start, int end) {
		checkWidget();
		if (start < 0 || start > end || end >= _itemCount)
			SWT.error(SWT.ERROR_INVALID_RANGE);
		
		for (int i=end; i>start; i--) {
			internalRemove(i);
		}
	}

	public void removeAll() {
		checkWidget();
		if (_itemCount <= 0) return;
		
		for (int i=0; i<_itemCount; i++) {
			final TimeSeriesTableItem item = _items[i];
			if (item != null) {
				item.release();
			}
			
			_items[i] = null;
		}
		
		_itemCount = 0;
		_model.removeAllRows();
	}

	public void showItem(TimeSeriesTableItem item) {
		checkWidget();
		// TODO Auto-generated method stub
		
	}

	public void showSelection() {
		checkWidget();
		// TODO Auto-generated method stub
		
	}
	
	void createItem(TimeSeriesTableColumn item, int index) {
		if (index < 0 || index > _attributeColumnCount)
			SWT.error(SWT.ERROR_INVALID_RANGE);
		
		if (_attributeColumnCount == _attributeColumns.length) {
			final TimeSeriesTableColumn[] newColumns = new TimeSeriesTableColumn[_attributeColumns.length + 4];
			System.arraycopy(_attributeColumns, 0, newColumns, 0, _attributeColumns.length);
			_attributeColumns = newColumns;
		}
		
		final Column column = _model.createColumn(MultiTableModel.HEADER_COLUMN, index);
		item.register(column);
		
		System.arraycopy(_attributeColumns, index, _attributeColumns, index+1, _attributeColumnCount++-index);
		_attributeColumns[index] = item;
	}

	void createItem(TimeSeriesTableItem item, int index) {
		if (index < 0 || index > _itemCount)
			SWT.error(SWT.ERROR_INVALID_RANGE);
		
		if (_itemCount == _items.length) {
			final int size = _items.length / 2;
			final TimeSeriesTableItem[] newItems = new TimeSeriesTableItem[_items.length + size];
			System.arraycopy(_items, 0, newItems, 0, _items.length);
			_items = newItems;
		}
		
		final Row row = _model.createRow(index);
		item.register(row);
		
		System.arraycopy(_items, index, _items, index+1, _itemCount++-index);
		_items[index] = item;
	}
	
	private void internalRemove(int index) {
		final TimeSeriesTableItem item = _items[index];
		if (item != null) {
			item.release();
		}
		
		System.arraycopy(_items, index+1, _items, index, --_itemCount-index);
		_items[_itemCount] = null;
		_model.removeRow(index);
	}
}
