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
