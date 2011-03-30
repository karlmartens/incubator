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
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Item;
import org.eclipse.swt.widgets.TypedListener;

public final class TimeSeriesTableColumn extends Item {

  private final TimeSeriesTable _parent;
  private int _width = 0;
  private boolean _moveable = true;
  private boolean _visible = true;

  /**
   * <p>
   * <dl>
   * <dt><b>Styles:</b></dt>
   * <dd>CHECK, LEFT, RIGHT, CENTER</dd>
   * <dt><b>Events:</b><//dt>
   * <dd>Resize, Selection, Move</dd>
   * </dl>
   * </p>
   * <p>
   * Note: Only one of the styles LEFT, RIGHT or CENTER can be specified.
   * </p>
   * 
   */
  public TimeSeriesTableColumn(TimeSeriesTable table, int style) {
    this(table, style, table.getColumnCount());
  }

  public TimeSeriesTableColumn(TimeSeriesTable table, int style, int index) {
    super(table, style);
    table.createItem(this, index);
    _parent = table;
  }

  /**
   * Constructor used internally to create a period value column.
   * 
   * @param table
   */
  TimeSeriesTableColumn(TimeSeriesTable table) {
    super(table, SWT.NONE);
    _parent = table;
  }

  public void setWidth(int width) {
    _width = width;
    _parent.redraw();
  }

  public int getWidth() {
    return _width;
  }

  public void setMoveable(boolean moveable) {
    _moveable = moveable;
  }

  public boolean isMoveable() {
    return _moveable;
  }

  public void setVisible(boolean visible) {
    _visible = visible;
    _parent.redraw();
  }

  public boolean isVisible() {
    return _visible;
  }

  TimeSeriesTable getParent() {
    return _parent;
  }

  public void addControlListener(ControlListener listener) {
    final TypedListener tListener = new TypedListener(listener);
    addListener(SWT.Resize, tListener);
    addListener(SWT.Move, tListener);
  }

  public void removeControlListener(ControlListener listener) {
    final TypedListener tListener = new TypedListener(listener);
    removeListener(SWT.Resize, tListener);
    removeListener(SWT.Move, tListener);
  }

  public void addSelectionListener(SelectionListener listener) {
    final TypedListener tListener = new TypedListener(listener);
    addListener(SWT.Selection, tListener);
    addListener(SWT.DefaultSelection, tListener);
  }

  public void removeSelectionListener(SelectionListener listener) {
    final TypedListener tListener = new TypedListener(listener);
    removeListener(SWT.Selection, tListener);
    removeListener(SWT.DefaultSelection, tListener);
  }
}
