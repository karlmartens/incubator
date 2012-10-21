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

import java.util.Set;

import net.karlmartens.platform.util.Filter;
import net.karlmartens.ui.widget.TableColumn;
import net.karlmartens.ui.widget.TableItem;

import org.eclipse.jface.action.Action;

/**
 * @author karl
 * 
 */
public final class FilterColumnValueAction extends Action {

  private final TableColumn _column;
  private final Set<String> _accepted;

  public FilterColumnValueAction(TableColumn column, String text,
      Set<String> accepted) {
    _column = column;
    _accepted = accepted;
    setText(text);
    setChecked(accepted.contains(text));
  }

  @Override
  public void run() {
    final String text = getText();
    if (_accepted.contains(text)) {
      _accepted.remove(text);
    } else {
      _accepted.add(text);
    }

    _column.setFilter(new Filter<TableItem>() {
      @Override
      public boolean accepts(TableItem candidate) {
        if (_accepted.isEmpty())
          return true;

        final int index = _column.table().indexOf(_column);
        final String candidateText = candidate.getText(index);
        return _accepted.contains(candidateText);
      }
    });
  }

}
