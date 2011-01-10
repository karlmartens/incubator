package net.karlmartens.ui.widget;

import java.util.ArrayList;
import java.util.List;

import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;

import com.jidesoft.converter.ConverterContext;
import com.jidesoft.grid.EditorContext;
import com.jidesoft.grid.MultiTableModel;

final class TimeSeriesTableModel implements MultiTableModel {
	private static final long serialVersionUID = 1L;
	
	private List<TableModelListener> _listeners = new ArrayList<TableModelListener>();
	private Column[] _columns = new Column[4];
	private int _columnCount = 0;
	private Row[] _rows = new Row[4];
	private int _rowCount = 0;

	@Override
	public String getColumnName(int columnIndex) {
		return getColumn(columnIndex).getName();
	}
	
	@Override
	public int getColumnCount() {
		return _columnCount;
	}
	
	@Override
	public int getColumnType(int columnIndex) {
		return getColumn(columnIndex).getType();
	}
	
	@Override
	public Class<?> getColumnClass(int columnIndex) {
		return String.class;
	}
	
	@Override
	public int getTableIndex(int columnIndex) {
		final Column column = getColumn(columnIndex);
		if (MultiTableModel.HEADER_COLUMN == column.getType()) 
			return 0;
		
		if (MultiTableModel.FOOTER_COLUMN == column.getType())
			return 2;
	
		return 1;
	}
	
	@Override
	public int getRowCount() {
		return _rowCount;
	}
	
	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		final Row row = getRow(rowIndex);
		return row.getValue(columnIndex);
	}
	
	@Override
	public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
		// TODO implement
	}
	
	@Override
	public Class<?> getCellClassAt(int rowIndex, int columnIndex) {
		return String.class;
	}
	
	public ConverterContext getConverterContextAt(int rowIndex, int columnIndex) {
		return null;
	}
	
	public EditorContext getEditorContextAt(int rowIndex, int columnIndex) {
		return null;
	}
	
	public boolean isCellEditable(int rowIndex, int columnIndex) {
		return false;
	}
	
	public void addTableModelListener(TableModelListener listener) {
		_listeners.add(listener);
	}
	
	public void removeTableModelListener(TableModelListener listener) {
		_listeners.remove(listener);
	}

	Column createColumn(int type, int index) {
		final Column c = new Column(type);
		addColumn(c, index);
		return c;
	}

	public Row createRow(int index) {
		final Row r = new Row();
		addRow(r, index);
		return r;
	}

	private void addColumn(Column column, int index) {
		if (index < 0 || index > _columnCount)
			throw new IllegalArgumentException();
		
		if (_columnCount == _columns.length) {
			final Column[] newColumns = new Column[_columns.length + 4];
			System.arraycopy(_columns, 0, newColumns, 0, _columns.length);
			_columns = newColumns;
		}
		
		System.arraycopy(_columns, index, _columns, index+1, _columnCount++-index);
		_columns[index] = column;
		fireTableStructureChanged();
	}

	private void addRow(Row row, int index) {
		if (index < 0 || index > _rowCount) 
			throw new IllegalArgumentException();
		
		if (_rowCount == _rows.length) {
			final int size = _rows.length / 2;
			final Row[] newRows = new Row[_columns.length + size];
			System.arraycopy(_rows, 0, newRows, 0, _rows.length);
			_rows = newRows;
		}
		
		System.arraycopy(_rows, index, _rows, index+1, _rowCount++-index);
		_rows[index] = row;
		fireTableRowsUpdated(index, _rowCount - 1);
	}

	private Column getColumn(int index) {
		if (index >= _columnCount)
			throw new IndexOutOfBoundsException();
		
		return _columns[index];
	}
	
	private Row getRow(int index) {
		if (index >= _rowCount)
			throw new IndexOutOfBoundsException();
		
		return _rows[index];
	}
	
	private int indexOf(Column column) {
		for (int i=0; i<_columnCount; i++) {
			if (_columns[i] == column) {
				return i;
			}
		}
		
		return -1;
	}
	
	
	private void fireTableStructureChanged() {
		fire(new TableModelEvent(this, TableModelEvent.HEADER_ROW));
	}
	
	private void fireTableRowsUpdated(int first, int last) {
		fire(new TableModelEvent(this, first, last));
	}
	
	private void fireTableCellUpdated(int row, int column) {
		fire(new TableModelEvent(this, row, Integer.MAX_VALUE, column));
	}

	private void fire(TableModelEvent event) {
		for (TableModelListener listener : _listeners) {
			listener.tableChanged(event);
		}
	}

	class Column {
		private final int _type;
		private String _name;
		
		private Column(int type) {
			_type = type;
		}
		
		String getName() {
			return _name;
		}
		
		void setName(String name) {
			_name = name;
			fireTableCellUpdated(TableModelEvent.HEADER_ROW, indexOf(this));
		}

		private int getType() {
			return _type;
		}
	}
	
	class Row {
		
		private String[] _values;

		public String getValue(int index) {
			return _values[index];
		}
		
		public void setValue(String value, int index) {
			_values[index] = value;
		}
		
		public void setValues(String[] values) {
			_values = values;
		}
		
	}
}