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
 *   net.karlmartens.ui, is a library of UI widgets
 */
package net.karlmartens.ui.action;

import net.karlmartens.ui.widget.Table;

import org.eclipse.jface.action.Action;

/**
 * @author karl
 * 
 */
public final class SortColumnAction extends Action {

  private final Table _table;
  private final int _columnIndex;
  private final int _sortDirection;

  public SortColumnAction(Table table, int columnIndex, int sortDirection,
      String text) {
    _table = table;
    _columnIndex = columnIndex;
    _sortDirection = sortDirection;
    setText(text);
  }

  @Override
  public void run() {
    _table.sort(_columnIndex, _sortDirection);
  }

}
