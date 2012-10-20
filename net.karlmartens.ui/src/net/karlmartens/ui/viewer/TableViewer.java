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

import java.util.Arrays;

import net.karlmartens.ui.widget.Table;
import net.karlmartens.ui.widget.TableColumn;
import net.karlmartens.ui.widget.TableItem;

import org.eclipse.jface.viewers.AbstractTableViewer;
import org.eclipse.jface.viewers.ColumnViewerEditor;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Item;
import org.eclipse.swt.widgets.Widget;

public class TableViewer extends AbstractTableViewer {

  private final Table _control;
  private TableViewerRow _cachedRow;

  public TableViewer(Composite parent) {
    this(new Table(parent));
  }

  public TableViewer(Composite parent, int style) {
    this(new Table(parent, style));
  }

  public TableViewer(Table control) {
    _control = control;
    hookControl(control);
  }

  @Override
  protected final TableViewerRow internalCreateNewRowPart(int style,
      int rowIndex) {
    final TableItem item;
    if (rowIndex >= 0) {
      item = new TableItem(_control, rowIndex);
    } else {
      item = new TableItem(_control);
    }

    return getViewerRowFromItem(item);
  }

  @Override
  protected final int doIndexOf(Item item) {
    return _control.indexOf((TableItem) item);
  }

  @Override
  protected final int doGetItemCount() {
    return _control.getItemCount();
  }

  @Override
  protected final void doSetItemCount(int count) {
    _control.setItemCount(count);
  }

  @Override
  protected final Item[] doGetItems() {
    return _control.getItems();
  }

  @Override
  protected final TableColumn doGetColumn(int index) {
    return _control.getColumn(index);
  }

  @Override
  protected final TableItem doGetItem(int index) {
    return _control.getItem(index);
  }

  @Override
  protected final TableItem[] doGetSelection() {
    return _control.getSelection();
  }

  @Override
  protected final int[] doGetSelectionIndices() {
    return _control.getSelectionIndices();
  }

  @Override
  protected final void doClearAll() {
    _control.clearAll();
  }

  @Override
  protected final void doResetItem(Item item) {
    final TableItem tableItem = (TableItem) item;
    final int columnCount = Math.max(1, _control.getColumnCount());
    for (int i = 0; i < columnCount; i++) {
      tableItem.setText(i, "");
    }
  }

  @Override
  protected final void doRemove(int start, int end) {
    _control.remove(start, end);
  }

  @Override
  protected final void doRemoveAll() {
    _control.removeAll();
  }

  @Override
  protected final void doRemove(int[] indices) {
    _control.remove(indices);
  }

  @Override
  protected final void doShowItem(Item item) {
    _control.showItem((TableItem) item);
  }

  @Override
  protected final void doDeselectAll() {
    _control.deselectAll();
  }

  @Override
  protected final void doSetSelection(Item[] items) {
    final TableItem[] tableItems = new TableItem[items.length];
    System.arraycopy(items, 0, tableItems, 0, tableItems.length);
    _control.setSelection(tableItems);
  }

  @Override
  protected final void doShowSelection() {
    _control.showSelection();
  }

  @Override
  protected final void doSetSelection(int[] indices) {
    _control.setSelection(indices);
  }

  @Override
  protected final void doClear(int index) {
    _control.clear(index);
  }

  @Override
  protected final void doSelect(int[] indices) {
    _control.select(indices);
  }

  @Override
  protected final ColumnViewerEditor createViewerEditor() {
    return new TableViewerEditor(
        this, //
        new TableEditorActivationStrategy(this),
        ColumnViewerEditor.TABBING_HORIZONTAL
            | ColumnViewerEditor.TABBING_MOVE_TO_ROW_NEIGHBOR
            | ColumnViewerEditor.TABBING_VERTICAL
            | ColumnViewerEditor.KEYBOARD_ACTIVATION);
  }

  @Override
  protected final TableViewerRow getViewerRowFromItem(Widget item) {
    if (_cachedRow == null) {
      _cachedRow = new TableViewerRow((TableItem) item);
    } else {
      _cachedRow.setItem((TableItem) item);
    }

    return _cachedRow;
  }

  @Override
  protected final TableItem getItemAt(Point point) {
    return _control.getItem(point);
  }

  @Override
  protected final int doGetColumnCount() {
    return _control.getColumnCount();
  }

  @Override
  public final Table getControl() {
    return _control;
  }

  protected final Point[] doGetCellSelections() {
    return _control.getCellSelections();
  }

  protected final Point doGetFocusCell() {
    return _control.getFocusCell();
  }

  protected final void doSetFocusCell(Point cell, boolean multi) {
    _control.setFocusCell(cell, multi);
  }

  @Override
  protected final void preservingSelection(Runnable updateCode) {
    final Point[] selection = _control.getCellSelections();
    try {
      updateCode.run();
    } finally {

      final int rowCount = doGetItemCount();
      final int columnCount = doGetColumnCount();

      final Point[] s = new Point[selection.length];
      int index = 0;
      for (int i = 0; i < selection.length; i++) {
        final Point pt = selection[i];
        if (pt.x >= 0 && pt.x < columnCount && pt.y >= 0 && pt.y < rowCount) {
          s[index++] = pt;
        }
      }

      final Point[] newSelection = Arrays.copyOf(s, index);
      _control.setCellSelections(newSelection);
    }
  }
}
