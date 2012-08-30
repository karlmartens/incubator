/**
 *   Copyright 2012 Karl Martens
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

import net.karlmartens.platform.util.ReflectSupport;

import org.eclipse.jface.viewers.ColumnViewerEditorActivationEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;

public class ComboBoxCellEditor extends org.eclipse.jface.viewers.ComboBoxCellEditor {

  public ComboBoxCellEditor(Composite parent, String[] items) {
    super(parent, items);
  }

  public ComboBoxCellEditor(Composite parent, String[] items, int style) {
    super(parent, items, style);
  }

  @Override
  public void activate(ColumnViewerEditorActivationEvent activationEvent) {
    super.activate(activationEvent);

    int index = -1;
    if (activationEvent.eventType == ColumnViewerEditorActivationEvent.KEY_PRESSED) {
      switch (activationEvent.character) {
        case ' ':
          break;

        case SWT.BS:
        case SWT.DEL:
          index = indexOf("");
          break;

        default:
          index = indexStartingWith(String.valueOf(activationEvent.character));
      }
    }

    if (index >= 0)
      getControl().select(index);
  }

  private int indexStartingWith(String prefix) {
    final String target = prefix.toLowerCase();
    final String[] items = getItems();
    for (int i = 0; i < items.length; i++) {
      if (items[i].toLowerCase().startsWith(target)) {
        return i;
      }
    }

    return -1;
  }

  private int indexOf(String value) {
    final String[] items = getItems();
    for (int i = 0; i < items.length; i++) {
      if ("".equals(items[i])) {
        return i;
      }
    }

    return -1;
  }

  protected Control createControl(final Composite parent) {
    final CCombo control = (CCombo) super.createControl(parent);

    control.addKeyListener(new KeyAdapter() {
      // hook key pressed - see PR 14201
      public void keyPressed(KeyEvent e) {
        switch (e.keyCode) {
          case SWT.ARROW_LEFT:
          case SWT.ARROW_RIGHT:
            applyAndDeactivate();
            notifyParentAndFocus(e);
            break;
            
          case SWT.TAB:
            notifyParentAndFocus(e);
            break;
        }
      }

      private void notifyParentAndFocus(KeyEvent e) {
        final Event event = new Event();
        event.character = e.character;
        event.keyCode = e.keyCode;
        event.stateMask = e.stateMask;
        parent.notifyListeners(SWT.KeyDown, event);
        parent.setFocus();
      }
    });

    return control;
  }

  @Override
  public CCombo getControl() {
    return (CCombo) super.getControl();
  }

  @Override
  protected boolean dependsOnExternalFocusListener() {
    return getClass() != ComboBoxCellEditor.class;
  }

  private void applyAndDeactivate() {
    ReflectSupport.invoke("applyEditorValueAndDeactivate", this, new Class[] {}, new Object[] {});
  }
}
