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

import org.eclipse.jface.viewers.IElementComparer;

final class ElementHashtableEntry {

  private final Object _element;
  private final IElementComparer _comparer;

  ElementHashtableEntry(Object element, IElementComparer comparer) {
    if (element == null)
      throw new NullPointerException();

    _element = element;
    _comparer = comparer;
  }

  public Object getElement() {
    return _element;
  }

  @Override
  public int hashCode() {
    if (_comparer != null)
      return _comparer.hashCode(_element);

    return _element.hashCode();
  }

  @Override
  public boolean equals(Object obj) {
    if (_comparer != null)
      return _comparer.equals(obj);

    return _element.equals(obj);
  }
}
