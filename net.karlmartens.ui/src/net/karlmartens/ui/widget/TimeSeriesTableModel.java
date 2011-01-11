package net.karlmartens.ui.widget;

import java.awt.Color;
import java.awt.Font;

import javax.swing.event.TableModelEvent;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;

import net.karlmartens.platform.util.UiThreadUtil;
import net.karlmartens.platform.util.UiThreadUtil.Task;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.FontData;

import com.jidesoft.converter.ConverterContext;
import com.jidesoft.grid.CellStyle;
import com.jidesoft.grid.CellStyleProvider;
import com.jidesoft.grid.EditorContext;
import com.jidesoft.grid.MultiTableModel;

final class TimeSeriesTableModel extends AbstractTableModel implements MultiTableModel, CellStyleProvider {
	private static final long serialVersionUID = 1L;

	private static final CellStyle DEFAULT_STYLE = new CellStyle();
	
	private Column[] _columns = new Column[4];
	private int _columnCount = 0;
	private Row[] _rows = new Row[4];
	private int _rowCount = 0;
	private int _lastIndexOf;
	
	@Override
	public int getColumnCount() {
		return _columnCount;
	}
	
	@Override
	public int getRowCount() {
		return _rowCount;
	}

	@Override
	public String getColumnName(int columnIndex) {
		return getColumn(columnIndex).getName();
	}

	@Override
	public int getColumnType(int columnIndex) {
		return getColumn(columnIndex).getType();
	}
	
	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		System.out.print("getValue(" + Integer.toString(rowIndex) + "," + Integer.toString(columnIndex) + ")");
		final Row row = getRow(rowIndex);
		final String value = row.getValue(columnIndex);
		System.out.println("='" + value + "'");
		return value;
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
	public Class<?> getCellClassAt(int rowIndex, int columnIndex) {
		return String.class;
	}
	
	public ConverterContext getConverterContextAt(int rowIndex, int columnIndex) {
		return null;
	}
	
	public EditorContext getEditorContextAt(int rowIndex, int columnIndex) {
		return null;
	}
	
	@Override
	public CellStyle getCellStyleAt(TableModel model, int rowIndex, int columnIndex) {
		final Row row = getRow(rowIndex);
		final CellStyle rowStyle = row.getCellStyle(columnIndex);
		if (rowStyle != null)
			return rowStyle;
		
		return DEFAULT_STYLE;
	}
	
	@Override
	public void fireTableChanged(final TableModelEvent event) {
		UiThreadUtil.performAsyncOnSwing(new Task() {		
			@Override
			public void run() {
				TimeSeriesTableModel.super.fireTableChanged(event);
			}
		});
	}

	Column createColumn(int type, int index) {
		if (index < 0 || index > _columnCount)
			throw new IllegalArgumentException();
		
		if (_columnCount == _columns.length) {
			final Column[] newColumns = new Column[_columns.length + 4];
			System.arraycopy(_columns, 0, newColumns, 0, _columns.length);
			_columns = newColumns;
		}
		
		System.arraycopy(_columns, index, _columns, index+1, _columnCount++-index);
		final Column c = new Column(type);
		_columns[index] = c;
		fireTableStructureChanged();
		return c;
	}

	Row createRow(int index) {
		if (index < 0 || index > _rowCount) 
			throw new IllegalArgumentException();
		
		if (_rowCount == _rows.length) {
			final int size = _rows.length / 2;
			final Row[] newRows = new Row[_rows.length + size];
			System.arraycopy(_rows, 0, newRows, 0, _rows.length);
			_rows = newRows;
		}
		
		System.arraycopy(_rows, index, _rows, index+1, _rowCount++-index);
		final Row r = new Row();
		_rows[index] = r;
		fireTableRowsUpdated(index, _rowCount-2);
		fireTableRowsInserted(_rowCount-1, _rowCount-1);
		return r;
	}

	void removeRow(int index) {
		System.arraycopy(_rows, index+1, _rows, index, --_rowCount-index);
		_rows[_rowCount] = null;
		fireTableRowsUpdated(index, _rowCount);
	}

	void removeAllRows() {
		final int rowCount = _rowCount;
		for (int i=0; i<rowCount; i++) {
			_rows[i] = null;
		}
		_rowCount = 0;
		fireTableRowsUpdated(0, rowCount-1);
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
	
	int indexOf(Column column) {
		for (int i=0; i<_columnCount; i++) {
			if (_columns[i] == column) {
				return i;
			}
		}
		
		return -1;
	}
	
	int indexOf(Row row) {
		if (_lastIndexOf >= 1 && _lastIndexOf < _rowCount - 1) {
			if (_rows[_lastIndexOf] == row) return _lastIndexOf;
			if (_rows[_lastIndexOf + 1] == row) return ++_lastIndexOf;
			if (_rows[_lastIndexOf - 1] == row) return --_lastIndexOf;
		}
		
		if (_lastIndexOf < _rowCount / 2) {
			for (int i=0; i<_rowCount; i++) {
				if (_rows[i] == row) {
					_lastIndexOf = i;
					return i;
				}
			}
		} else {
			for (int i=_rowCount-1; i>=0; i--) {
				if (_rows[i] == row) {
					_lastIndexOf = i;
					return i;
				}
			}
		}
		
		return -1;
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
		
		private String[] _values = new String[0];
		private Color _background;
		private Color[] _cellBackgrounds = new Color[0];
		private Color _foreground;
		private Color[] _cellForegrounds = new Color[0];
		private Font _font;
		private Font[] _cellFonts = new Font[0];

		public String getValue(int index) {
			check();
			return _values[index];
		}

		public void setValue(String value, int index) {
			check();
			System.out.println("setValue(" + value + "," + Integer.toString(index) + ")");
			_values[index] = value;
			final int rowIndex = indexOf(this);
			fireTableCellUpdated(rowIndex, index);
		}
		
		public void setValues(String[] values) {
			if (values == null)
				throw new NullPointerException();
			
			check();
			System.arraycopy(values, 0, _values, 0, Math.min(values.length, _values.length));
			final int rowIndex = indexOf(this);
			fireTableRowsUpdated(rowIndex, rowIndex);
		}
		
		public void setBackground(org.eclipse.swt.graphics.Color color) {
			check();
			_background = color == null ? null : new Color(color.getRed(), color.getGreen(), color.getBlue());
			final int rowIndex = indexOf(this);
			fireTableRowsUpdated(rowIndex, rowIndex);
		}
		
		public void setBackground(int index, org.eclipse.swt.graphics.Color color) {
			check();
			_cellBackgrounds[index] = color == null ? null : new Color(color.getRed(), color.getGreen(), color.getBlue());
			final int rowIndex = indexOf(this);
			fireTableCellUpdated(rowIndex, index);
		}
		
		public void setForeground(org.eclipse.swt.graphics.Color color) {
			check();
			_foreground = color == null ? null : new Color(color.getRed(), color.getGreen(), color.getBlue());
			final int rowIndex = indexOf(this);
			fireTableRowsUpdated(rowIndex, rowIndex);
		}
		
		public void setForeground(int index, org.eclipse.swt.graphics.Color color) {
			check();
			_cellForegrounds[index] = color == null ? null : new Color(color.getRed(), color.getGreen(), color.getBlue());
			final int rowIndex = indexOf(this);
			fireTableCellUpdated(rowIndex, index);
		}
		
		public void setFont(org.eclipse.swt.graphics.Font font) {
			check();
			_font = createFont(font);
			final int rowIndex = indexOf(this);
			fireTableRowsUpdated(rowIndex, rowIndex);
		}
		
		public void setFont(int index, org.eclipse.swt.graphics.Font font) {
			check();
			_cellFonts[index] = createFont(font);;
			final int rowIndex = indexOf(this);
			fireTableCellUpdated(rowIndex, index);
		}
		
		private Font createFont(org.eclipse.swt.graphics.Font font) {
			if (font == null)
				return null;
			
			final FontData fd = font.getFontData()[0];
			final String name = fd.getName();
			final int size = fd.getHeight();
			
			int fontStyle = 0;
			if ((fd.getStyle() & SWT.BOLD) > 0)
				fontStyle |= java.awt.Font.BOLD;
			if ((fd.getStyle() & SWT.ITALIC) > 0)
				fontStyle |= java.awt.Font.ITALIC;
			if ((fd.getStyle() & SWT.NORMAL) > 0)
				fontStyle |= java.awt.Font.PLAIN;
			
			return new Font(name, fontStyle, size);
		}

		private CellStyle getCellStyle() {
			final CellStyle style = new CellStyle();
			if (_foreground != null)
				style.setForeground(_foreground);
			
			if (_background != null)
				style.setBackground(_background);
			
			if (_font != null)
				style.setFont(_font);
			
			return style;
		}
		
		private CellStyle getCellStyle(int index) {
			check();
			final CellStyle style = getCellStyle();
			
			if (_cellForegrounds[index] != null)
				style.setForeground(_cellForegrounds[index]);
			
			if (_cellBackgrounds[index] != null)
				style.setBackground(_cellBackgrounds[index]);
			
			if (_cellFonts[index] != null)
				style.setFont(_cellFonts[index]);
			
			return style;
		}

		private void check() {
			if (_values.length != _columnCount) {
				final String[] newValues = new String[_columnCount];
				System.arraycopy(_values, 0, newValues, 0, Math.min(newValues.length, _values.length));
				_values = newValues;
			}

			if (_cellBackgrounds.length != _columnCount) {
				final Color[] newBackgrounds = new Color[_columnCount];
				System.arraycopy(_cellBackgrounds, 0, newBackgrounds, 0, Math.min(newBackgrounds.length, _cellBackgrounds.length));
				_cellBackgrounds = newBackgrounds;
			}

			if (_cellForegrounds.length != _columnCount) {
				final Color[] newForegrounds = new Color[_columnCount];
				System.arraycopy(_cellForegrounds, 0, newForegrounds, 0, Math.min(newForegrounds.length, _cellForegrounds.length));
				_cellForegrounds = newForegrounds;
			}

			if (_cellFonts.length != _columnCount) {
				final Font[] newFonts = new Font[_columnCount];
				System.arraycopy(_cellFonts, 0, newFonts, 0, Math.min(newFonts.length, _cellFonts.length));
				_cellFonts = newFonts;
			}
		}
	}
}