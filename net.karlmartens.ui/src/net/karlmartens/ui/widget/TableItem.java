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

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Rectangle;

public final class TableItem extends AbstractTableItem {

  private final Table _parent;

  public TableItem(Table parent) {
    this(parent, parent.getItemCount());    
  }
  
  public TableItem(Table parent, int index) {
    super(parent, SWT.NONE);
    _parent = parent;
    _parent.createItem(this, index);
  }

  public Table getParent() {
    checkWidget();
    return _parent;
  }

  public Rectangle getBounds(int index) {
    checkWidget();
    return _parent.getBounds(this, index);
  }

  public Rectangle getBounds() {
    checkWidget();
    return _parent.getBounds(this);
  }

  public Rectangle getImageBounds(int index) {
    checkWidget();
    return _parent.getImageBounds(this, index);
  }

  @Override
  protected int doGetColumnCount() {
    return _parent.getColumnCount();
  }

}
