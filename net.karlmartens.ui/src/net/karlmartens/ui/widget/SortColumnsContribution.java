/**
 *   Copyright 2011 Karl Martens
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

import net.karlmartens.ui.Images;
import net.karlmartens.ui.Messages;
import net.karlmartens.ui.action.SortColumnAction;

import org.eclipse.jface.action.ActionContributionItem;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.Separator;
import org.eclipse.ui.actions.CompoundContributionItem;

final class SortColumnsContribution extends CompoundContributionItem {

  private final TableColumnManager _manager;
  private final Table _table;

  private int _columnIndex = -1;

  public SortColumnsContribution(TableColumnManager manager, Table table) {
    _manager = manager;
    _table = table;
  }

  public void setColumnIndex(int columnIndex) {
    _columnIndex = columnIndex;
  }

  @Override
  protected IContributionItem[] getContributionItems() {
    if (!_manager.isSortEnabled() || _columnIndex < 0
        || _columnIndex >= _table.getColumnCount())
      return new IContributionItem[0];

    final IContributionItem[] items = new IContributionItem[3];
    items[0] = new ActionContributionItem(new SortColumnAction(_table,
        _columnIndex, Table.SORT_ASCENDING, Messages.SORT_ASCENDING.string(),
        Images.SORT_ASCEND));
    items[1] = new ActionContributionItem(new SortColumnAction(_table,
        _columnIndex, Table.SORT_DESCENDING, Messages.SORT_DESCENDING.string(),
        Images.SORT_DESCEND));
    items[2] = new Separator();

    return items;
  }
}
