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

import net.karlmartens.ui.widget.TableColumn;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;

public class ToggleColumnVisibiltyAction extends Action implements IAction {

  private final TableColumn _column;

  public ToggleColumnVisibiltyAction(TableColumn column) {
    super(column.getText(), IAction.AS_CHECK_BOX);
    setChecked(column.isVisible());

    _column = column;
  }

  @Override
  public void run() {
    _column.setVisible(isChecked());
  }
}
