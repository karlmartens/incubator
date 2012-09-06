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

import net.karlmartens.platform.util.Filter;
import net.karlmartens.ui.widget.TableColumn;
import net.karlmartens.ui.widget.TableItem;

import org.eclipse.jface.action.Action;

/**
 * @author karl
 * 
 */
public final class FilterColumnAction extends Action {

  private final TableColumn _column;
  private final Filter<TableItem> _filter;

  public FilterColumnAction(TableColumn column, String text,
      Filter<TableItem> filter) {
    _column = column;
    _filter = filter;
    setText(text);
    setChecked(filter.equals(column.getFilter()));
  }

  @Override
  public void run() {
    if (Filter.<TableItem> all() == _filter
        || _filter.equals(_column.getFilter())) {
      _column.setFilter(null);
      return;
    }

    _column.setFilter(_filter);
  }

}
