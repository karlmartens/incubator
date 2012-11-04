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
import net.karlmartens.ui.action.ToggleColumnVisibiltyAction;

import org.eclipse.jface.action.ActionContributionItem;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.ui.actions.CompoundContributionItem;

final class VisibleColumnsContribution extends CompoundContributionItem {

  private final Table _table;

  public VisibleColumnsContribution(Table table) {
    _table = table;
  }

  public void dispose() {
    if (_menuManager == null)
      return;

    _menuManager.dispose();
    _menuManager = null;
  }

  @Override
  protected IContributionItem[] getContributionItems() {
    final IMenuManager menu = getMenuManager();
    menu.removeAll();

    final IContributionItem[] items = new IContributionItem[_table
        .getColumnCount()];
    for (int i = 0; i < items.length; i++) {
      final TableColumn column = _table.getColumn(i);
      if (!column.isHideable())
        continue;

      menu.add(new ActionContributionItem(new ToggleColumnVisibiltyAction(
          column)));
    }

    menu.update();

    if (menu.isEmpty())
      return new IContributionItem[0];

    return new IContributionItem[] { menu };
  }

  private MenuManager _menuManager;

  private MenuManager getMenuManager() {
    if (_menuManager == null) {
      _menuManager = new MenuManager(Messages.SHOW_HIDE_COLUMN.string(),
          Images.SHOW_HIDE_COLUMN, null);
    }

    return _menuManager;
  }

}
