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

import java.util.HashMap;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class DataTableRow {

  private final Map<String, Object> _properties = new HashMap<String, Object>();

  private DataTableCell[] _cells = new DataTableCell[0];

  public DataTableRow(int cellCount) {
    size(cellCount);
  }

  public void insertCell(int columnIndex) {
    final int originalLength = _cells.length;
    if (columnIndex < 0 || columnIndex > originalLength)
      throw new IllegalArgumentException();

    size(originalLength + 1);

    final DataTableCell a = _cells[originalLength];
    System.arraycopy(_cells, columnIndex, _cells, columnIndex + 1,
        originalLength - columnIndex);
    _cells[columnIndex] = a;
  }

  public DataTableCell cell(int index) {
    return _cells[index];
  }

  public DataTableRow cell(int index, DataTableCell value) {
    _cells[index] = value;
    return this;
  }

  public Map<String, Object> properties() {
    return _properties;
  }

  public Object property(String name) {
    return _properties.get(name);
  }

  public DataTableRow setProperty(String name, Object value) {
    _properties.put(name, value);
    return this;
  }

  public DataTableRow size(int columnCount) {
    if (columnCount < 0)
      throw new IllegalArgumentException();

    for (int i = columnCount; i < _cells.length; i++)
      _cells[i].release();

    final DataTableCell[] newCells = new DataTableCell[columnCount];
    final int len = Math.min(_cells.length, columnCount);
    System.arraycopy(_cells, 0, newCells, 0, len);

    for (int i = len; i < columnCount; i++)
      newCells[i] = new DataTableCell();

    _cells = newCells;
    return this;
  }

  public void removeCells(int columnIndex, int numberOfColumns) {
    if (columnIndex < 0 || columnIndex > _cells.length)
      throw new IllegalArgumentException();

    if (numberOfColumns < 1)
      throw new IllegalArgumentException();

    final int lastCel = Math.min(_cells.length, columnIndex + numberOfColumns);
    for (int cell = columnIndex; cell < lastCel; cell++) {
      _cells[cell].release();
      _cells[cell] = null;
    }

    final int cellCount = Math.max(0, _cells.length - numberOfColumns);
    System.arraycopy(_cells, columnIndex + numberOfColumns, _cells,
        columnIndex, cellCount - columnIndex);
  }

  void release() {
    _properties.clear();
    _cells = new DataTableCell[0];
  }

  public JsonElement toJson() {
    final JsonArray arr = new JsonArray();
    for (int i = 0; i < _cells.length; i++)
      arr.add(_cells[i].toJson());

    final JsonObject json = new JsonObject();
    json.add("c", arr);

    return json;
  }

  @Override
  public String toString() {
    return new Gson().toJson(toJson());
  }

}