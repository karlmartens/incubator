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

import org.eclipse.jface.viewers.ColumnViewerEditorActivationEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.TraverseEvent;
import org.eclipse.swt.events.TraverseListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Text;

public final class TextCellEditor extends org.eclipse.jface.viewers.TextCellEditor {

  private boolean _selectAll = true;

  public TextCellEditor(Composite parent) {
    super(parent);
  }

  public TextCellEditor(Composite parent, int style) {
    super(parent, style);
  }

  @Override
  public void activate(ColumnViewerEditorActivationEvent activationEvent) {
    super.activate(activationEvent);

    _selectAll = true;
    if (activationEvent.eventType == ColumnViewerEditorActivationEvent.KEY_PRESSED) {
      switch (activationEvent.character) {
        case ' ':
          break;

        default:
          final String value = new String(new char[] { activationEvent.character });
          getControl().setText(value);
          _selectAll = false;
      }
    }
  }

  @Override
  protected Control createControl(final Composite parent) {
    final Control control = super.createControl(parent);
    control.addTraverseListener(new TraverseListener() {
      @Override
      public void keyTraversed(TraverseEvent e) {
        switch (e.keyCode) {
          case SWT.ARROW_LEFT:
          case SWT.ARROW_RIGHT:
          case SWT.ARROW_UP:
          case SWT.ARROW_DOWN:
          case SWT.TAB:
            fireApplyEditorValue();
            deactivate();
            final Event event = new Event();
            event.character = e.character;
            event.keyCode = e.keyCode;
            event.stateMask = e.stateMask;
            parent.notifyListeners(SWT.KeyDown, event);
        }
      }
    });

    return control;
  }

  @Override
  protected void doSetFocus() {
    super.doSetFocus();

    if (!_selectAll) {
      final Text text = getControl();
      text.setSelection(text.getText().length());
    }
  }

  @Override
  public Text getControl() {
    return (Text) super.getControl();
  }

  @Override
  protected boolean dependsOnExternalFocusListener() {
    return getClass() != TextCellEditor.class;
  }
}
