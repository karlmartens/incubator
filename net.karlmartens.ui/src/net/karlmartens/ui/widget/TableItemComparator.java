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

import java.util.Comparator;

final class TableItemComparator implements Comparator<TableItem> {

  private final Comparator<String> _comparator;
  private final int _index;
  private final int _direction;

  public TableItemComparator(Comparator<String> comparator, int index, int direction) {
    _comparator = comparator;
    _index = index;
    _direction = direction;
  }

  @Override
  public int compare(TableItem o1, TableItem o2) {
    if (o1 == null) {
      if (o2 == null) {
        return 0;
      }

      return -1;
    }

    if (o2 == null)
      return 1;

    final String s1 = o1.getText(_index);
    final String s2 = o2.getText(_index);
    return _direction * _comparator.compare(s1, s2);
  }

}
