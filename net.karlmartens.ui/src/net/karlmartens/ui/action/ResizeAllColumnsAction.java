/**
 *  net.karlmartens.ui, is a library of UI widgets
 *  Copyright (C) 2011
 *  Karl Martens
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Lesser General Public License as 
 *  published by the Free Software Foundation, either version 3 of the 
 *  License, or (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */
package net.karlmartens.ui.action;

import java.util.ResourceBundle;

import net.karlmartens.ui.widget.TimeSeriesTable;

import org.eclipse.jface.action.Action;

public final class ResizeAllColumnsAction extends Action {

  private final TimeSeriesTable _table;
  private final ResizeColumnAction _delegateAction;

  public ResizeAllColumnsAction(TimeSeriesTable table) {
    _table = table;
    _delegateAction = new ResizeColumnAction(_table, -1);

    final ResourceBundle bundle = ResourceBundle.getBundle("net.karlmartens.ui.locale.messages");
    setText(bundle.getString("ResizeAllColumnsAction.TEXT"));
  }

  @Override
  public void run() {
    for (int i = 0; i < _table.getColumnCount() + _table.getPeriodCount(); i++) {
      _delegateAction.setColumnIndex(i);
      _delegateAction.run();
    }
  }
}
