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
package net.karlmartens.ui.widget;

import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Item;
import org.eclipse.swt.widgets.TableColumn;

public final class GridChooserColumn extends Item {

  private TableColumn[] _columns;

  public GridChooserColumn(GridChooser parent, int style) {
    this(parent, style, parent.getColumnCount());
  }

  public GridChooserColumn(GridChooser parent, int style, int index) {
    super(parent, style);
    parent.createItem(this, index);
  }

  @Override
  public void setText(String text) {
    for (TableColumn widget : _columns) {
      widget.setText(text);
    }
  }

  @Override
  public void setImage(Image image) {
    for (TableColumn widget : _columns) {
      widget.setImage(image);
    }
  }

  public void setWidth(int width) {
    for (TableColumn widget : _columns) {
      widget.setWidth(width);
    }
  }

  public void setMoveable(boolean moveable) {
    for (TableColumn widget : _columns) {
      widget.setMoveable(moveable);
    }
  }

  @Override
  public String getText() {
    return _columns[0].getText();
  }

  @Override
  public Image getImage() {
    return _columns[0].getImage();
  }

  public int getWidth() {
    return _columns[0].getWidth();
  }

  public boolean isMoveable() {
    return _columns[0].getMoveable();
  }

  void registerWidgets(TableColumn[] widgets) {
    _columns = widgets;
  }

  public void release() {
    _columns = null;
  }

  public void addControlListener(ControlListener listener) {
    for (TableColumn column : _columns) {
      column.addControlListener(listener);
    }
  }

  public void removeControlListener(ControlListener listener) {
    for (TableColumn column : _columns) {
      column.removeControlListener(listener);
    }
  }
}
