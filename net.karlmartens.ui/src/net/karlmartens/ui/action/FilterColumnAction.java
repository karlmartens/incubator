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
import net.karlmartens.ui.widget.Table;
import net.karlmartens.ui.widget.TableItem;

import org.eclipse.jface.action.Action;

/**
 * @author karl
 * 
 */
public final class FilterColumnAction extends Action {

  private final Table _table;
  private final Filter<TableItem> _filter;

  public FilterColumnAction(Table table, String text, Filter<TableItem> filter) {
    _table = table;
    _filter = filter;
    setText(text);
  }

  @Override
  public void run() {
    if (Filter.<TableItem> all() == _filter) {
      _table.setFilter(null);
      return;
    }

    _table.setFilter(_filter);
  }

}
