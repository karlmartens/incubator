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

import java.util.Arrays;

import net.karlmartens.ui.action.ToggleColumnVisibiltyAction;

import org.eclipse.jface.action.ActionContributionItem;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.ui.actions.CompoundContributionItem;

final class VisibleColumnsContribution extends CompoundContributionItem {

  private final Table _table;

  public VisibleColumnsContribution(Table table) {
    _table = table;
  }

  @Override
  protected IContributionItem[] getContributionItems() {
    final IContributionItem[] items = new IContributionItem[_table.getColumnCount()];
    int index = 0;
    for (int i = 0; i < items.length; i++) {
      final TableColumn column = _table.getColumn(i);
      if (!column.isHideable())
        continue;
      
      items[index++] = new ActionContributionItem(new ToggleColumnVisibiltyAction(column));
    }
    
    return Arrays.copyOf(items, index);
  }

}
