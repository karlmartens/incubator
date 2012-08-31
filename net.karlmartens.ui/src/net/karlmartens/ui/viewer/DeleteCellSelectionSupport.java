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

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Control;

public final class DeleteCellSelectionSupport extends CellSelectionModifier {

  private final TableViewer _viewer;

  public DeleteCellSelectionSupport(TableViewer viewer) {
    super(viewer);
    _viewer = viewer;
    new Listener();
  }

  private void performDelete() {
    final Point[] selection = _viewer.doGetCellSelections();
    if (selection == null || selection.length == 0)
      return;

    if (!isEditable(selection)) {
      return;
    }

    final String[] values = new String[selection.length];
    Arrays.fill(values, "");
    setValues(selection, values);
    _viewer.refresh();
  }

  private final class Listener implements KeyListener, DisposeListener {

    private Listener() {
      final Control control = _viewer.getControl();
      control.addKeyListener(this);
      control.addDisposeListener(this);
    }

    @Override
    public void widgetDisposed(DisposeEvent e) {
      final Control control = _viewer.getControl();
      control.removeKeyListener(this);
      control.removeDisposeListener(this);
    }

    @Override
    public void keyPressed(KeyEvent e) {
      switch (e.keyCode) {
        case SWT.DEL:
        case SWT.BS:
          performDelete();
      }
    }

    @Override
    public void keyReleased(KeyEvent e) {
      // Ignore
    }
  }
}
