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

import java.util.Comparator;

import net.karlmartens.platform.datatable.DataTableColumn.Type;

/**
 * @author karl
 * 
 */
public class RangeDataTableFilter implements DataTableFilter {

  private final Type _type;
  private final int _columnIndex;
  private final Object _from;
  private final Object _to;

  public RangeDataTableFilter(Type type, int columnIndex, Object from, Object to) {
    _type = type;
    _columnIndex = columnIndex;
    _from = from;
    _to = to;
  }

  @Override
  public boolean accepts(DataTableRow row) {
    if (_from == null && _to == null)
      return true;

    final Comparator<Object> comparator = _type.comparator();
    final Object value = row.cell(_columnIndex).value();
    if (_from != null && comparator.compare(_from, value) > 0)
      return false;

    if (_to != null && comparator.compare(_to, value) <= 0)
      return false;

    return true;
  }

}
