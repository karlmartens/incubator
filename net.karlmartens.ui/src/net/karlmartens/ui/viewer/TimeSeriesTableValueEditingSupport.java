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
package net.karlmartens.ui.viewer;

import java.text.NumberFormat;
import java.text.ParseException;

import net.karlmartens.ui.widget.TimeSeriesTableItem;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

class TimeSeriesTableValueEditingSupport extends EditingSupport {

  private final TimeSeriesTableViewer _viewer;
  private TextCellEditor _cellEditor;

  TimeSeriesTableValueEditingSupport(TimeSeriesTableViewer viewer) {
    super(viewer);
    _viewer = viewer;
  }

  @Override
  protected CellEditor getCellEditor(Object element) {
    if (_cellEditor == null) {
      _cellEditor = new TextCellEditor((Composite) _viewer.getControl(), SWT.RIGHT);
    }
    return _cellEditor;
  }

  @Override
  protected boolean canEdit(Object element) {
    final TimeSeriesEditingSupport editingSupport = _viewer.getEditingSupport();
    if (editingSupport == null)
      return false;

    return editingSupport.canEdit(element);
  }

  @Override
  protected Object getValue(Object element) {
    throw new UnsupportedOperationException();
  }

  @Override
  protected void setValue(Object element, Object value) {
    throw new UnsupportedOperationException();
  }

  @Override
  protected void initializeCellEditorValue(CellEditor cellEditor, ViewerCell cell) {
    cellEditor.setValue("");

    final TimeSeriesEditingSupport editingSupport = _viewer.getEditingSupport();
    if (editingSupport == null)
      return;

    final TimeSeriesContentProvider cp = (TimeSeriesContentProvider) _viewer.getContentProvider();
    if (cp == null)
      return;

    final double value = cp.getValue(cell.getElement(), computePeriodIndex(cell));
    final NumberFormat format = getNumberFormat(editingSupport);
    cellEditor.setValue(format.format(value));
  }

  @Override
  protected void saveCellEditorValue(CellEditor cellEditor, ViewerCell cell) {
    final TimeSeriesEditingSupport editingSupport = _viewer.getEditingSupport();
    if (editingSupport == null)
      return;

    final TimeSeriesContentProvider cp = (TimeSeriesContentProvider) _viewer.getContentProvider();
    if (cp == null)
      return;

    final String source = (String) cellEditor.getValue();
    if (source == null || source.trim().length() == 0) {
      update(cell, 0.0);
      return;
    }

    try {
      final NumberFormat format = getNumberFormat(editingSupport);
      final Number n = format.parse(source);
      update(cell, n.doubleValue());
    } catch (ParseException e) {
      // ignore
    }
  }

  private void update(ViewerCell cell, double value) {
    _viewer.getEditingSupport().setValue(cell.getElement(), computePeriodIndex(cell), value);

    final TimeSeriesTableItem item = (TimeSeriesTableItem) cell.getItem();
    item.setValue(computePeriodIndex(cell), value);

    _viewer.getControl().redraw();
  }

  private int computePeriodIndex(ViewerCell cell) {
    return cell.getColumnIndex() - _viewer.getControl().getColumnCount();
  }

  private NumberFormat getNumberFormat(TimeSeriesEditingSupport editingSupport) {
    NumberFormat format = editingSupport.getNumberFormat();
    if (format != null)
      return format;

    return NumberFormat.getNumberInstance();
  }
}