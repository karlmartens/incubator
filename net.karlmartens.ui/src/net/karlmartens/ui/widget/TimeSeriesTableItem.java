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
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;

public final class TimeSeriesTableItem extends AbstractTableItem {

  private TimeSeriesTable _parent;
  private double[] _values;

  public TimeSeriesTableItem(TimeSeriesTable parent) {
    this(parent, parent.getItemCount());
  }

  public TimeSeriesTableItem(TimeSeriesTable parent, int index) {
    super(parent, SWT.NONE);
    _parent = parent;
    parent.createItem(this, index);
  }

  public TimeSeriesTable getParent() {
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
  public String getText(int index) {
    if (index < _parent.getColumnCount())
      return super.getText(index);

    final double value = getValue(index - _parent.getColumnCount());
    if (value == 0.0)
      return "";

    return _parent.getNumberFormat().format(value);
  }

  public double getValue(int index) {
    checkWidget();
    if (_values == null || index < 0 || index >= _values.length)
      return 0.0;

    return _values[index];
  }

  public void setValue(double[] values) {
    checkWidget();
    if (values == null)
      SWT.error(SWT.ERROR_NULL_ARGUMENT);

    for (int i = 0; i < values.length; i++) {
      setValue(i, values[i]);
    }
  }

  public void setValue(int index, double value) {
    checkWidget();

    final int count = Math.max(1, _parent.getPeriodCount());
    if (index < 0 || index >= count)
      return;

    if (_values == null) {
      _values = new double[count];
    }

    if (_values[index] == value)
      return;

    _values[index] = value;
    _parent.redraw();
  }

  @Override
  public Color getBackground(int index) {
    if (index < _parent.getColumnCount())
      return super.getBackground(index);

    return getBackground();
  }

  @Override
  public Font getFont(int index) {
    if (index < _parent.getColumnCount())
      return super.getFont(index);

    return getFont();
  }

  @Override
  public Color getForeground(int index) {
    if (index < _parent.getColumnCount())
      return super.getForeground(index);

    return getForeground();
  }

  @Override
  public Image getImage(int index) {
    if (index < _parent.getColumnCount())
      return super.getImage(index);

    return null;
  }

  @Override
  protected int doGetColumnCount() {
    return _parent.getColumnCount();
  }

  @Override
  void clear() {
    super.clear();
    _values = null;
  }

  @Override
  void release() {
    super.release();
    _parent = null;
  }
}
