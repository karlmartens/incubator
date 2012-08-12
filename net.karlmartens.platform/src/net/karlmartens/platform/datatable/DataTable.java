/**
 *   Copyright 2012 Karl Martens
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
 *   net.karlmartens.platform, is a library of shared basic utility classes
 */

package net.karlmartens.platform.datatable;

import static net.karlmartens.platform.util.NullSafe.max;
import static net.karlmartens.platform.util.NullSafe.min;

import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

import net.karlmartens.platform.datatable.DataTableColumn.Role;
import net.karlmartens.platform.datatable.DataTableColumn.Type;
import net.karlmartens.platform.util.ArrayIndexComparator;
import net.karlmartens.platform.util.Range;

import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
import org.joda.time.LocalTime;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class DataTable {

  private final Map<String, Object> _properties = new HashMap<String, Object>();

  private int _columnCount = 0;
  private DataTableColumn[] _columns = new DataTableColumn[0];
  private int _rowCount = 0;
  private DataTableRow[] _rows = new DataTableRow[0];

  public int addColumn(Type type) {
    return addColumn(new DataTableColumn(type));
  }

  public int addColumn(Type type, String label) {
    return addColumn(new DataTableColumn(type).label(label));
  }

  public int addColumn(Type type, String label, String id) {
    return addColumn(new DataTableColumn(type).label(label).id(id));
  }

  public int addColumn(DataTableColumn column) {
    final int index = _columnCount;
    insertColumn(index, column);
    return index;
  }

  public int addRow(Object... values) {
    final int index = _rowCount;
    final DataTableRow row = new DataTableRow(_columnCount);
    for (int i = 0; i < Math.min(_columnCount, values.length); i++) {
      final Object value = values[i];
      if (value instanceof DataTableCell) {
        row.cell(i, (DataTableCell) value);
      } else if (value instanceof String) {
        row.cell(i).value((String) value);
      } else if (value instanceof Number) {
        row.cell(i).value((Number) value);
      } else if (value instanceof Boolean) {
        row.cell(i).value((Boolean) value);
      } else if (value instanceof LocalDate) {
        row.cell(i).value((LocalDate) value);
      } else if (value instanceof LocalDateTime) {
        row.cell(i).value((LocalDateTime) value);
      } else if (value instanceof LocalTime) {
        row.cell(i).value((LocalTime) value);
      } else {
        throw new UnsupportedOperationException();
      }
    }
    insertRows(index, row);
    return index;
  }

  public void addRows(int numberOfRows) {
    final DataTableRow[] newRows = new DataTableRow[numberOfRows];
    for (int i = 0; i < numberOfRows; i++)
      newRows[i] = new DataTableRow(_columnCount);
    addRows(newRows);
  }

  public void addRows(DataTableRow... rows) {
    insertRows(_rowCount, rows);
  }

  public String getColumnId(int columnIndex) {
    checkColumnIndex(columnIndex);
    return _columns[columnIndex].id();
  }

  public String getColumnLabel(int columnIndex) {
    checkColumnIndex(columnIndex);
    return _columns[columnIndex].label();
  }

  public String getColumnPattern(int columnIndex) {
    checkColumnIndex(columnIndex);
    return _columns[columnIndex].pattern();
  }

  public Map<String, Object> getColumnProperties(int columnIndex) {
    checkColumnIndex(columnIndex);
    return _columns[columnIndex].properties();
  }

  public Object getColumnProperty(int columnIndex, String name) {
    checkColumnIndex(columnIndex);
    return _columns[columnIndex].property(name);
  }

  public Range getColumnRange(int columnIndex) {
    final Type type = getColumnType(columnIndex);
    final Comparator<Object> comparator = type.comparator();

    Object min = null;
    Object max = null;
    for (int i = 0; i < _rowCount; i++) {
      final DataTableRow row = _rows[i];
      final DataTableCell cell = row.cell(columnIndex);
      final Object value = cell.value();
      if (value == null)
        continue;

      if (min == null) {
        min = value;
        max = value;
        continue;
      }

      min = min(min, value, comparator);
      max = max(max, value, comparator);
    }

    return new Range(min, max);
  }

  public Role getColumnRole(int columnIndex) {
    checkColumnIndex(columnIndex);
    return _columns[columnIndex].role();
  }

  public Type getColumnType(int columnIndex) {
    checkColumnIndex(columnIndex);
    return _columns[columnIndex].type();
  }

  public int[] getFilteredRows(DataTableFilter... filters) {
    final int[] accepted = new int[_rowCount];
    int index = 0;
    for (int row = 0; row < _rowCount; row++) {
      if (accepts(filters, _rows[row]))
        accepted[index++] = row;
    }

    return Arrays.copyOf(accepted, index);
  }

  public String getFormattedValue(int rowIndex, int columnIndex) {
    checkRowIndex(rowIndex);
    checkColumnIndex(columnIndex);

    final DataTableCell cell = _rows[rowIndex].cell(columnIndex);
    final String formatted = cell.formattedValue();
    if (formatted != null)
      return formatted;

    final Object value = cell.value();
    if (value == null)
      return "";

    final Type type = getColumnType(columnIndex);
    final String pattern = getColumnPattern(columnIndex);
    if (pattern == null)
      return type.format(value);

    return type.format(pattern, value);
  }

  public int getNumberOfColumns() {
    return _columnCount;
  }

  public int getNumberOfRows() {
    return _rowCount;
  }

  public Map<String, Object> getProperties(int rowIndex, int columnIndex) {
    checkRowIndex(rowIndex);
    checkColumnIndex(columnIndex);
    return _rows[rowIndex].cell(columnIndex).properties();
  }

  public Object getProperty(int rowIndex, int columnIndex, String name) {
    checkRowIndex(rowIndex);
    checkColumnIndex(columnIndex);
    return _rows[rowIndex].cell(columnIndex).property(name);
  }

  public Map<String, Object> getRowProperties(int rowIndex) {
    checkRowIndex(rowIndex);
    return _rows[rowIndex].properties();
  }

  public Object getRowProperty(int rowIndex, String name) {
    checkRowIndex(rowIndex);
    return _rows[rowIndex].property(name);
  }

  public int[] getSortedRows(int... columnIndices) {
    final DataTableSort[] sort = toSortArray(columnIndices);
    return getSortedRows(sort);
  }

  public int[] getSortedRows(DataTableSort... sort) {
    final Integer[] indices = new Integer[_rowCount];
    for (int i = 0; i < indices.length; i++)
      indices[i] = i;

    final DataTableRow[] rows = Arrays.copyOf(_rows, _rowCount);
    Arrays.sort(indices, new ArrayIndexComparator<DataTableRow>(rows,
        new DataTableRowComparator(sort)));

    final int[] sorted = new int[indices.length];
    for (int i = 0; i < sorted.length; i++)
      sorted[i] = indices[i].intValue();

    return sorted;
  }

  public Map<String, Object> getTableProperties() {
    return _properties;
  }

  public Object getTableProperty(String name) {
    return _properties.get(name);
  }

  public Object getValue(int rowIndex, int columnIndex) {
    checkRowIndex(rowIndex);
    checkColumnIndex(columnIndex);
    return _rows[rowIndex].cell(columnIndex).value();
  }

  public void insertColumn(int columnIndex, Type type) {
    insertColumn(columnIndex, new DataTableColumn(type));
  }

  public void insertColumn(int columnIndex, Type type, String label) {
    insertColumn(columnIndex, new DataTableColumn(type).label(label));
  }

  public void insertColumn(int columnIndex, Type type, String label, String id) {
    insertColumn(columnIndex, new DataTableColumn(type).label(label).id(id));
  }

  public void insertColumn(int columnIndex, DataTableColumn column) {
    if (columnIndex < 0 || columnIndex > _columnCount)
      throw new IndexOutOfBoundsException();

    if (_columnCount == _columns.length) {
      final DataTableColumn[] newColumns = new DataTableColumn[_columnCount + 4];
      System.arraycopy(_columns, 0, newColumns, 0, _columnCount);
      _columns = newColumns;
    }

    System.arraycopy(_columns, columnIndex, _columns, columnIndex + 1,
        _columnCount++ - columnIndex);
    _columns[columnIndex] = column;

    for (int row = 0; row < _rowCount; row++)
      _rows[row].insertCell(columnIndex);
  }

  public void insertRows(int rowIndex, int numberOfRows) {
    final DataTableRow[] rows = new DataTableRow[numberOfRows];
    for (int i = 0; i < rows.length; i++)
      rows[i] = new DataTableRow(_columnCount);

    insertRows(rowIndex, rows);
  }

  public void insertRows(int rowIndex, DataTableRow... rows) {
    if (rowIndex < 0 || rowIndex > _rowCount)
      throw new IndexOutOfBoundsException();

    if ((_rowCount + rows.length) >= _rows.length) {
      final int newLength = Math.max(_rowCount + rows.length,
          _rows.length * 3 / 2);
      final DataTableRow[] newRows = new DataTableRow[newLength];
      System.arraycopy(_rows, 0, newRows, 0, _rowCount);
      _rows = newRows;
    }

    for (DataTableRow row : rows)
      row.size(_columnCount);

    System.arraycopy(_rows, rowIndex, _rows, rowIndex + rows.length, _rowCount
        - rowIndex);
    System.arraycopy(rows, 0, _rows, rowIndex, rows.length);
    _rowCount += rows.length;
  }

  public void removeColumn(int columnIndex) {
    removeColumns(columnIndex, 1);
  }

  public void removeColumns(int columnIndex, int numberOfColumns) {
    checkColumnIndex(columnIndex);
    if (numberOfColumns < 1)
      throw new IllegalArgumentException();

    final int lastCol = Math.min(_columnCount, columnIndex + numberOfColumns);
    for (int col = columnIndex; col < lastCol; col++) {
      _columns[col].release();
      _columns[col] = null;
    }

    _columnCount = Math.max(0, _columnCount - numberOfColumns);
    System.arraycopy(_columns, columnIndex + numberOfColumns, _columns,
        columnIndex, _columnCount - columnIndex);

    for (int row = 0; row < _rowCount; row++)
      _rows[row].removeCells(columnIndex, numberOfColumns);
  }

  public void removeRow(int rowIndex) {
    removeRows(rowIndex, 1);
  }

  public void removeRows(int rowIndex, int numberOfRows) {
    checkRowIndex(rowIndex);
    if (numberOfRows < 1)
      throw new IllegalArgumentException();

    final int lastRow = Math.min(_rowCount, rowIndex + numberOfRows);
    for (int row = rowIndex; row < lastRow; row++) {
      _rows[row].release();
      _rows[row] = null;
    }

    _rowCount = Math.max(0, _rowCount - numberOfRows);
    System.arraycopy(_rows, rowIndex + numberOfRows, _rows, rowIndex, _rowCount
        - rowIndex);
  }

  public void setCell(int rowIndex, int columnIndex, String value,
      String formattedValue, Map<String, Object> properties) {
    checkRowIndex(rowIndex);
    checkColumnIndex(columnIndex);
    final DataTableCell cell = _rows[rowIndex].cell(columnIndex);
    cell.value(value);
    cell.formattedValue(formattedValue);
    cell.properties().putAll(properties);
  }

  public void setCell(int rowIndex, int columnIndex, Number value,
      String formattedValue, Map<String, Object> properties) {
    checkRowIndex(rowIndex);
    checkColumnIndex(columnIndex);
    final DataTableCell cell = _rows[rowIndex].cell(columnIndex);
    cell.value(value);
    cell.formattedValue(formattedValue);
    cell.properties().putAll(properties);
  }

  public void setCell(int rowIndex, int columnIndex, Boolean value,
      String formattedValue, Map<String, Object> properties) {
    checkRowIndex(rowIndex);
    checkColumnIndex(columnIndex);
    final DataTableCell cell = _rows[rowIndex].cell(columnIndex);
    cell.value(value);
    cell.formattedValue(formattedValue);
    cell.properties().putAll(properties);
  }

  public void setCell(int rowIndex, int columnIndex, LocalDate value,
      String formattedValue, Map<String, Object> properties) {
    checkRowIndex(rowIndex);
    checkColumnIndex(columnIndex);
    final DataTableCell cell = _rows[rowIndex].cell(columnIndex);
    cell.value(value);
    cell.formattedValue(formattedValue);
    cell.properties().putAll(properties);
  }

  public void setCell(int rowIndex, int columnIndex, LocalDateTime value,
      String formattedValue, Map<String, Object> properties) {
    checkRowIndex(rowIndex);
    checkColumnIndex(columnIndex);
    final DataTableCell cell = _rows[rowIndex].cell(columnIndex);
    cell.value(value);
    cell.formattedValue(formattedValue);
    cell.properties().putAll(properties);
  }

  public void setCell(int rowIndex, int columnIndex, LocalTime value,
      String formattedValue, Map<String, Object> properties) {
    checkRowIndex(rowIndex);
    checkColumnIndex(columnIndex);
    final DataTableCell cell = _rows[rowIndex].cell(columnIndex);
    cell.value(value);
    cell.formattedValue(formattedValue);
    cell.properties().putAll(properties);
  }

  public void setColumnLabel(int columnIndex, String label) {
    checkColumnIndex(columnIndex);
    final DataTableColumn column = _columns[columnIndex];
    column.label(label);
  }

  public void setColumnProperty(int columnIndex, String name, Object value) {
    checkColumnIndex(columnIndex);
    final DataTableColumn column = _columns[columnIndex];
    column.setProperty(name, value);
  }

  public void setColumnProperties(int columnIndex,
      Map<String, Object> properties) {
    checkColumnIndex(columnIndex);
    final DataTableColumn column = _columns[columnIndex];
    column.properties().putAll(properties);
  }

  public void setFormattedValue(int rowIndex, int columnIndex,
      String formattedValue) {
    checkRowIndex(rowIndex);
    checkColumnIndex(columnIndex);
    final DataTableCell cell = _rows[rowIndex].cell(columnIndex);
    cell.formattedValue(formattedValue);
  }

  public void setProperty(int rowIndex, int columnIndex, String name,
      Object value) {
    checkRowIndex(rowIndex);
    checkColumnIndex(columnIndex);
    final DataTableCell cell = _rows[rowIndex].cell(columnIndex);
    cell.setProperty(name, value);
  }

  public void setProperties(int rowIndex, int columnIndex,
      Map<String, Object> properties) {
    checkRowIndex(rowIndex);
    checkColumnIndex(columnIndex);
    final DataTableCell cell = _rows[rowIndex].cell(columnIndex);
    cell.properties().putAll(properties);
  }

  public void setRowProperty(int rowIndex, String name, Object value) {
    checkRowIndex(rowIndex);
    _rows[rowIndex].setProperty(name, value);
  }

  public void setRowProperties(int rowIndex, Map<String, Object> properties) {
    checkRowIndex(rowIndex);
    _rows[rowIndex].properties().putAll(properties);
  }

  public void setTableProperty(String name, Object value) {
    _properties.put(name, value);
  }

  public void setTableProperties(Map<String, Object> properties) {
    _properties.putAll(properties);
  }

  public void setValue(int rowIndex, int columnIndex, String value) {
    checkRowIndex(rowIndex);
    checkColumnIndex(columnIndex);
    final DataTableCell cell = _rows[rowIndex].cell(columnIndex);
    cell.value(value);
  }

  public void setValue(int rowIndex, int columnIndex, Number value) {
    checkRowIndex(rowIndex);
    checkColumnIndex(columnIndex);
    final DataTableCell cell = _rows[rowIndex].cell(columnIndex);
    cell.value(value);
  }

  public void setValue(int rowIndex, int columnIndex, Boolean value) {
    checkRowIndex(rowIndex);
    checkColumnIndex(columnIndex);
    final DataTableCell cell = _rows[rowIndex].cell(columnIndex);
    cell.value(value);
  }

  public void setValue(int rowIndex, int columnIndex, LocalDate value) {
    checkRowIndex(rowIndex);
    checkColumnIndex(columnIndex);
    final DataTableCell cell = _rows[rowIndex].cell(columnIndex);
    cell.value(value);
  }

  public void setValue(int rowIndex, int columnIndex, LocalDateTime value) {
    checkRowIndex(rowIndex);
    checkColumnIndex(columnIndex);
    final DataTableCell cell = _rows[rowIndex].cell(columnIndex);
    cell.value(value);
  }

  public void setValue(int rowIndex, int columnIndex, LocalTime value) {
    checkRowIndex(rowIndex);
    checkColumnIndex(columnIndex);
    final DataTableCell cell = _rows[rowIndex].cell(columnIndex);
    cell.value(value);
  }

  public void sort(int... columnIndices) {
    final DataTableSort[] sort = toSortArray(columnIndices);
    sort(sort);
  }

  public void sort(DataTableSort... sort) {
    final int[] indices = getSortedRows(sort);
    final DataTableRow[] newRows = new DataTableRow[_rows.length];
    for (int i = 0; i < indices.length; i++)
      newRows[i] = _rows[indices[i]];

    _rows = newRows;
  }

  public JsonElement toJson() {
    final JsonArray cols = new JsonArray();
    for (int i = 0; i < _columnCount; i++)
      cols.add(_columns[i].toJson());

    final JsonArray rows = new JsonArray();
    for (int i = 0; i < _rowCount; i++)
      rows.add(_rows[i].toJson());

    final JsonObject json = new JsonObject();
    json.add("cols", cols);
    json.add("rows", rows);
    return json;
  }

  @Override
  public String toString() {
    return new Gson().toJson(toJson());
  }

  private void checkColumnIndex(int columnIndex) {
    if (columnIndex < 0 || columnIndex >= _columnCount)
      throw new IndexOutOfBoundsException();
  }

  private void checkRowIndex(int rowIndex) {
    if (rowIndex < 0 || rowIndex >= _rowCount)
      throw new IndexOutOfBoundsException();
  }

  private static boolean accepts(DataTableFilter[] filters, DataTableRow row) {
    for (DataTableFilter filter : filters) {
      if (!filter.accepts(row))
        return false;
    }

    return true;
  }

  private static DataTableSort[] toSortArray(int[] columnIndices) {
    final DataTableSort[] sort = new DataTableSort[columnIndices.length];
    for (int i = 0; i < sort.length; i++)
      sort[i] = new DataTableSort(columnIndices[i], false);
    return sort;
  }

  private final class DataTableRowComparator implements
      Comparator<DataTableRow> {

    private final DataTableSort[] _sort;

    public DataTableRowComparator(DataTableSort[] sort) {
      if (sort == null)
        throw new NullPointerException();

      if (sort.length <= 0)
        throw new IllegalArgumentException();

      _sort = sort;
    }

    @Override
    public int compare(DataTableRow o1, DataTableRow o2) {
      for (int i = 0; i < _sort.length; i++) {
        final DataTableSort sort = _sort[i];

        final int columnIndex = sort.columnIndex();
        final Comparator<Object> comparator = getColumnType(columnIndex)
            .comparator();
        final Object v1 = o1.cell(columnIndex).value();
        final Object v2 = o2.cell(columnIndex).value();

        final int c = comparator.compare(v1, v2);
        if (c == 0)
          continue;

        if (sort.decending())
          return -c;

        return c;
      }

      return 0;
    }
  }
}
