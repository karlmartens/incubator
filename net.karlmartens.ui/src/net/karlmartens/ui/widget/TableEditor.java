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
import org.eclipse.swt.custom.ControlEditor;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;

public final class TableEditor extends ControlEditor {

  private static final int TIMEOUT = 100;
  
  private Table _control;
  private ControlListener _columnListener;
  private Runnable _timer;
  private TableItem _item;
  private int _column;

  public TableEditor(Table control) {
    super(control);
    _control = control;
    
    _columnListener = new ControlListener() {
      @Override
      public void controlResized(ControlEvent e) {
        layout();
      }

      @Override
      public void controlMoved(ControlEvent e) {
        layout();
      }
    };

    _timer = new Runnable() {
      public void run() {
        layout();
      }
    };

    final GC gc = new GC(control);
    gc.setFont(control.getFont());
    minimumHeight = gc.getFontMetrics().getHeight();
    gc.dispose();
    grabVertical = false;
  }
  
  @Override
  public void setEditor(Control editor) {
    if (editor != null && _item != null) {
      editor.setParent(_control.getTableComposite());
      editor.setFont( _item.getFont(_column));
    }
    super.setEditor(editor);
    resize();
  }

  public void setEditor(Control editor, TableItem item, int columnIndex) {
    setItem(item);
    setColumn(columnIndex);
    setEditor(editor);
  }

  @Override
  public void layout() {
    if (_control == null || _control.isDisposed())
      return;

    if (_item == null || _item.isDisposed())
      return;

    final int columnCount = _control.getColumnCount();
    if (columnCount == 0 && _column != 0)
      return;

    if (columnCount > 0 && (_column < 0 || _column >= columnCount))
      return;

    final Control editor = getEditor();
    if (editor == null || editor.isDisposed())
      return;

    final boolean hadFocus = editor.isVisible() && editor.isFocusControl();

    editor.setBounds(computeCellBounds());

    if (hadFocus) {
      if (editor == null || editor.isDisposed())
        return;
      editor.setFocus();
    }
  }

  @Override
  public void dispose() {
    if (_control != null && !_control.isDisposed()) {
      if (_column > -1 && _column < (_control.getColumnCount())) {
        final TableColumn column = _control.getColumn(_column);
        column.removeControlListener(_columnListener);
      }
    }

    _control = null;
    _columnListener = null;
    _timer = null;
    _item = null;
    _column = -1;
    super.dispose();
  }
  
  private void setItem(TableItem item) {
    _item = item;
    resize();
  }

  private void setColumn(int column) {
    final int columnCount = _control.getColumnCount();
    if (columnCount == 0) {
      _column = column == 0 ? 0 : -1;
      resize();
      return;
    }

    if (_column > -1 && _column < columnCount) {
      final TableColumn tableColumn = _control.getColumn(_column);
      tableColumn.removeControlListener(_columnListener);
      _column = -1;
    }

    if (column < -1 || column >= columnCount)
      return;

    _column = column;
    final TableColumn tableColumn = _control.getColumn(_column);
    tableColumn.addControlListener(_columnListener);
    resize();
  }

  private void resize() {
    layout();

    if (_control != null) {
      final Display display = _control.getDisplay();
      display.timerExec(TIMEOUT, _timer);
    }
  }

  private Rectangle computeCellBounds() {
    if (_item == null || _column <= -1 || _item.isDisposed())
      return new Rectangle(0, 0, 0, 0);

    final Rectangle cell = _item.getBounds(_column);

    // Remove space taken by image from editor
    final Rectangle image = _item.getImageBounds(_column);
    cell.x = image.x + image.width + 1;
    cell.width -= image.width + 2;

    cell.y += 3;
    cell.height -= 6;

    // Convert from global widget coordinates to table relative coordinates
    final Composite parent = getEditor().getParent();
    final Rectangle table = parent.getBounds();
    cell.x -= table.x;
    cell.y -= table.y;

    // Reduce cell width to compensate for trimming
    final Rectangle area = parent.getClientArea();
    if (cell.x < area.x + area.width) {
      if (cell.x + cell.width > area.x + area.width) {
        cell.width = area.x + area.width - cell.x;
      }
    }

    final Rectangle editor = new Rectangle(cell.x, cell.y, minimumWidth, minimumHeight);

    if (grabHorizontal) {
      editor.width = Math.max(cell.width, minimumWidth);
    }

    if (grabVertical) {
      editor.height = Math.max(cell.height, minimumHeight);
    }

    if (horizontalAlignment == SWT.RIGHT) {
      editor.x += cell.width - editor.width;
    } else if (horizontalAlignment == SWT.LEFT) {
      // nothing to do
    } else {
      editor.x += (cell.width - editor.width) / 2;
    }

    if (verticalAlignment == SWT.TOP) {
      // nothing to do
    } else if (verticalAlignment == SWT.BOTTOM) {
      editor.y += cell.height - editor.height;
    } else {
      editor.y += (cell.height - editor.height) / 2;
    }
    return editor;
  }

}
