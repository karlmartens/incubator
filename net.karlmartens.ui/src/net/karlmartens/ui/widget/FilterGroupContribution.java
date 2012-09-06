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
package net.karlmartens.ui.widget;

import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.ResourceBundle;
import java.util.Set;

import net.karlmartens.platform.util.Filter;
import net.karlmartens.platform.util.NullSafe;
import net.karlmartens.platform.util.NumberStringComparator;
import net.karlmartens.ui.action.FilterColumnAction;

import org.eclipse.jface.action.ActionContributionItem;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.ui.actions.CompoundContributionItem;

/**
 * @author karl
 * 
 */
final class FilterGroupContribution extends CompoundContributionItem {

  private final Table _table;
  private final String _menuText;
  private final String _allText;
  private final String _topTextText;

  private int _columnIndex;

  public FilterGroupContribution(Table table) {
    _table = table;

    final ResourceBundle bundle = ResourceBundle
        .getBundle("net.karlmartens.ui.locale.messages");
    _menuText = bundle.getString("FilterColumns.TEXT");
    _allText = bundle.getString("Filter.All.TEXT");
    _topTextText = bundle.getString("Filter.TopTen.TEXT");
  }

  public void setColumnIndex(int columnIndex) {
    _columnIndex = columnIndex;
  }

  @Override
  protected IContributionItem[] getContributionItems() {
    if (_columnIndex < 0 || _columnIndex >= _table.getColumnCount())
      return new IContributionItem[0];
    final TableColumn column = _table.getColumn(_columnIndex);
    if (!column.isFilterable())
      return new IContributionItem[0];

    final IMenuManager menu = new MenuManager(_menuText);

    final IAction allAction = new FilterColumnAction(column, _allText,
        Filter.<TableItem> all());
    menu.add(new ActionContributionItem(allAction));

    final TableItem[] items = _table.getItems();
    Arrays.sort(items, new TableItemSorter(_columnIndex));

    final int from = Math.max(items.length - 10, 0);
    final int to = from + Math.min(10, items.length);
    final TableItem[] topTen = Arrays.copyOfRange(items, from, to);
    final IAction topTenFilter = new FilterColumnAction(column, _topTextText,
        Filter.accepting(topTen));
    menu.add(new ActionContributionItem(topTenFilter));

    menu.add(new Separator());

    String lastValue = null;
    final Set<TableItem> acceptedItems = new HashSet<TableItem>();
    for (TableItem item : items) {
      final String value = item.getText(_columnIndex);
      if (!NullSafe.equals(lastValue, value)) {
        if (!acceptedItems.isEmpty()) {
          final TableItem[] arr = acceptedItems.toArray(new TableItem[0]);
          final IAction action = new FilterColumnAction(column, lastValue,
              Filter.accepting(arr));
          menu.add(new ActionContributionItem(action));
        }

        lastValue = value;
        acceptedItems.clear();
      }

      acceptedItems.add(item);
    }

    if (!acceptedItems.isEmpty()) {
      final TableItem[] arr = acceptedItems.toArray(new TableItem[0]);
      final IAction action = new FilterColumnAction(column, lastValue,
          Filter.accepting(arr));
      menu.add(new ActionContributionItem(action));
    }

    menu.update();

    return new IContributionItem[] { menu };
  }

  private static class TableItemSorter implements Comparator<TableItem> {

    private final Comparator<String> _comparator = new NumberStringComparator();
    private int _index;

    public TableItemSorter(int columnIndex) {
      _index = columnIndex;
    }

    @Override
    public int compare(TableItem o1, TableItem o2) {
      if (o1 == o2)
        return 0;

      if (o1 == null)
        return -1;

      if (o2 == null)
        return 1;

      final String s1 = o1.getText(_index);
      final String s2 = o2.getText(_index);
      return _comparator.compare(s1, s2);
    }
  }
}
