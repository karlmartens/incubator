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

import net.karlmartens.ui.widget.Table;
import net.karlmartens.ui.widget.TableEditor;
import net.karlmartens.ui.widget.TableItem;

import org.eclipse.jface.viewers.CellEditor.LayoutData;
import org.eclipse.jface.viewers.CellLabelProvider;
import org.eclipse.jface.viewers.ColumnViewerEditor;
import org.eclipse.jface.viewers.ColumnViewerEditorActivationEvent;
import org.eclipse.jface.viewers.ColumnViewerEditorActivationListener;
import org.eclipse.jface.viewers.ColumnViewerEditorActivationStrategy;
import org.eclipse.jface.viewers.ColumnViewerEditorDeactivationEvent;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Item;

public final class TableViewerEditor extends ColumnViewerEditor {

  private final TableViewer _viewer;
  private final TableEditor _editor;
  private final ViewerCellSelectionManager _selectionManager;

  public TableViewerEditor(TableViewer viewer, ColumnViewerEditorActivationStrategy editorActivationStrategy, int feature) {
    super(viewer, editorActivationStrategy, feature);
    _viewer = viewer;
    _editor = new TableEditor(viewer.getControl());
    _selectionManager = new ViewerCellSelectionManager(viewer);
    addEditorActivationListener(_listener);
  }

  @Override
  protected void setEditor(Control w, Item item, int columnIndex) {
    final TableItem tableItem = (TableItem) item;
    _editor.setEditor(w, tableItem, columnIndex);
  }

  @Override
  protected void setLayoutData(LayoutData layoutData) {
    _editor.grabHorizontal = layoutData.grabHorizontal;
    _editor.horizontalAlignment = layoutData.horizontalAlignment;
    _editor.minimumWidth = layoutData.minimumWidth;
    _editor.verticalAlignment = SWT.CENTER;

    if (layoutData.minimumHeight != SWT.DEFAULT) {
      _editor.minimumHeight = layoutData.minimumHeight;
    }
  }
  
  private TableViewerRow _cachedRow;

  @Override
  public ViewerCell getFocusCell() {
    final Point p = _selectionManager.getFocusCell();
    if (p == null)
      return null;

    final TableItem item = _viewer.getControl().getItem(p.y);
    if (_cachedRow == null) {
      _cachedRow = new TableViewerRow(item);
    } else {
      _cachedRow.setItem(item);
    }
    return _cachedRow.getCell(p.x);
  }

  @Override
  protected void updateFocusCell(ViewerCell focusCell, ColumnViewerEditorActivationEvent event) {
    if (event.eventType == ColumnViewerEditorActivationEvent.PROGRAMMATIC || event.eventType == ColumnViewerEditorActivationEvent.TRAVERSAL) {
      final Table table = _viewer.getControl();
      final int row = table.indexOf((TableItem) focusCell.getItem());
      final int col = focusCell.getColumnIndex();
      _selectionManager.setFocusCell(new Point(col, row));
    }
  }

  private final ColumnViewerEditorActivationListener _listener = new ColumnViewerEditorActivationListener() {
    @Override
    public void beforeEditorActivated(ColumnViewerEditorActivationEvent event) {
      _viewer.getControl().addSelectionListener(_selectionListener);
    }

    @Override
    public void afterEditorActivated(ColumnViewerEditorActivationEvent event) {
      // nothing to do
    }

    @Override
    public void beforeEditorDeactivated(ColumnViewerEditorDeactivationEvent event) {
      // Nothing to do
    }

    @Override
    public void afterEditorDeactivated(ColumnViewerEditorDeactivationEvent event) {
      final Table table = _viewer.getControl();
      table.removeSelectionListener(_selectionListener);

      final ViewerCell cell = (ViewerCell) event.getSource();
      final CellLabelProvider labelProvider = _viewer.getLabelProvider(cell.getColumnIndex());
      if (labelProvider != null)
        labelProvider.update(cell);

      table.redraw();
    }
  };

  private SelectionListener _selectionListener = new SelectionAdapter() {
    @Override
    public void widgetSelected(SelectionEvent e) {
      final Control control = _editor.getEditor();
      if (control == null || control.isDisposed())
        return;

      control.notifyListeners(SWT.FocusOut, new Event());
    }
  };

}
