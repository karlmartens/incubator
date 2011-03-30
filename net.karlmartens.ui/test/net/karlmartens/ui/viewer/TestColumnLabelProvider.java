/**
 *   Copyright 2010,2011 Karl Martens
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
package net.karlmartens.ui.viewer;

import org.eclipse.jface.viewers.ColumnLabelProvider;

class TestColumnLabelProvider extends ColumnLabelProvider {

  private final int _index;

  TestColumnLabelProvider(int index) {
    _index = index;
  }

  @Override
  public String getText(Object element) {
    final Object o = ((Object[]) element)[_index];
    if (o == null)
      return "";

    return o.toString();
  }
}