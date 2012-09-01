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

import java.text.MessageFormat;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColumnViewerEditorActivationEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.TraverseEvent;
import org.eclipse.swt.events.TraverseListener;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;

public class ComboBoxCellEditor extends CellEditor {

  public static final int DROP_DOWN_ON_MOUSE_ACTIVATION = 1;
  public static final int DROP_DOWN_ON_KEY_ACTIVATION = 1 << 1;
  public static final int DROP_DOWN_ON_PROGRAMMATIC_ACTIVATION = 1 << 2;
  public static final int DROP_DOWN_ON_TRAVERSE_ACTIVATION = 1 << 3;

  private static final int DEFAULT_STYLE = SWT.NONE;

  private String[] _items;
  private int _selection;
  private int _activationStyle = SWT.NONE;

  private CCombo _control;

  public ComboBoxCellEditor() {
    setStyle(DEFAULT_STYLE);
  }

  public ComboBoxCellEditor(Composite parent, String[] items) {
    this(parent, items, DEFAULT_STYLE);
  }

  public ComboBoxCellEditor(Composite parent, String[] items, int style) {
    super(parent, style);
    setItems(items);
  }

  public void setActivationStyle(int activationStyle) {
    _activationStyle = activationStyle;
  }

  public String[] getItems() {
    return _items;
  }

  public void setItems(String[] items) {
    Assert.isNotNull(items);
    _items = items;
    populateComboBoxItems();
  }

  public LayoutData getLayoutData() {
    final LayoutData layoutData = super.getLayoutData();
    if ((_control == null) || _control.isDisposed()) {
      layoutData.minimumWidth = 60;
    } else {
      // make the comboBox 10 characters wide
      GC gc = new GC(_control);
      layoutData.minimumWidth = (gc.getFontMetrics().getAverageCharWidth() * 10) + 10;
      gc.dispose();
    }
    return layoutData;
  }

  @Override
  public void activate(ColumnViewerEditorActivationEvent activationEvent) {
    super.activate(activationEvent);

    int index = -1;
    if (activationEvent.eventType == ColumnViewerEditorActivationEvent.KEY_PRESSED) {
      switch (activationEvent.character) {
        case ' ':
          index = _selection;
          break;

        case SWT.BS:
        case SWT.DEL:
          index = -1;
          break;

        default:
          index = indexStartingWith(String.valueOf(activationEvent.character));
      }
    }

    getControl().select(index);

    if (_activationStyle != SWT.NONE) {
      boolean dropDown = false;
      if ((activationEvent.eventType == ColumnViewerEditorActivationEvent.MOUSE_CLICK_SELECTION || activationEvent.eventType == ColumnViewerEditorActivationEvent.MOUSE_DOUBLE_CLICK_SELECTION)
          && (_activationStyle & DROP_DOWN_ON_MOUSE_ACTIVATION) != 0) {
        dropDown = true;
      } else if (activationEvent.eventType == ColumnViewerEditorActivationEvent.KEY_PRESSED
          && (_activationStyle & DROP_DOWN_ON_KEY_ACTIVATION) != 0) {
        dropDown = true;
      } else if (activationEvent.eventType == ColumnViewerEditorActivationEvent.PROGRAMMATIC
          && (_activationStyle & DROP_DOWN_ON_PROGRAMMATIC_ACTIVATION) != 0) {
        dropDown = true;
      } else if (activationEvent.eventType == ColumnViewerEditorActivationEvent.TRAVERSAL
          && (_activationStyle & DROP_DOWN_ON_TRAVERSE_ACTIVATION) != 0) {
        dropDown = true;
      }

      if (dropDown) {
        getControl().getDisplay().asyncExec(new Runnable() {
          public void run() {
            ((CCombo) getControl()).setListVisible(true);
          }
        });
      }
    }
  }

  protected Control createControl(Composite parent) {
    _control = new CCombo(parent, getStyle());
    _control.setFont(parent.getFont());

    populateComboBoxItems();

    _control.addKeyListener(new KeyAdapter() {
      // hook key pressed - see PR 14201
      public void keyPressed(KeyEvent e) {
        keyReleaseOccured(e);
      }
    });

    _control.addSelectionListener(new SelectionAdapter() {
      public void widgetDefaultSelected(SelectionEvent event) {
        applyEditorValueAndDeactivate();
      }

      public void widgetSelected(SelectionEvent event) {
        _selection = _control.getSelectionIndex();
      }
    });

    _control.addTraverseListener(new TraverseListener() {
      public void keyTraversed(TraverseEvent e) {
        if (e.detail == SWT.TRAVERSE_ESCAPE || e.detail == SWT.TRAVERSE_RETURN) {
          e.doit = false;
        }
      }
    });

    _control.addFocusListener(new FocusAdapter() {
      public void focusLost(FocusEvent e) {
        ComboBoxCellEditor.this.focusLost();
      }
    });

    return _control;
  }

  protected Object doGetValue() {
    if (_selection == -1)
      return "";

    return _items[_selection];
  }

  protected void doSetFocus() {
    _control.setFocus();
  }

  protected void doSetValue(Object value) {
    Assert.isTrue(_control != null && (value instanceof String));
    _selection = indexOf((String) value);
    _control.select(_selection);
  }

  private void populateComboBoxItems() {
    if (_control == null || _items == null)
      return;

    _control.removeAll();
    for (int i = 0; i < _items.length; i++) {
      _control.add(_items[i], i);
    }

    setValueValid(true);
    _selection = -1;
  }

  void applyEditorValueAndDeactivate() {
    // must set the selection before getting value
    _selection = _control.getSelectionIndex();
    final Object newValue = doGetValue();
    markDirty();
    boolean isValid = isCorrect(newValue);
    setValueValid(isValid);

    if (!isValid) {
      // Only format if the 'index' is valid
      if (_items.length > 0 && _selection >= 0 && _selection < _items.length) {
        // try to insert the current value into the error message.
        setErrorMessage(MessageFormat.format(getErrorMessage(),
            new Object[] { _items[_selection] }));
      } else {
        // Since we don't have a valid index, assume we're using an
        // 'edit'
        // combo so format using its text value
        setErrorMessage(MessageFormat.format(getErrorMessage(),
            new Object[] { _control.getText() }));
      }
    }

    fireApplyEditorValue();
    deactivate();
  }

  protected void focusLost() {
    if (isActivated()) {
      applyEditorValueAndDeactivate();
    }
  }

  protected void keyReleaseOccured(KeyEvent event) {
    switch (event.keyCode) {
      case SWT.ESC:
        fireCancelEditor();
        break;

      case SWT.ARROW_LEFT:
      case SWT.ARROW_RIGHT:
        applyEditorValueAndDeactivate();
        notifyParentAndFocus(event);
        break;

      case SWT.TAB:
        applyEditorValueAndDeactivate();
        notifyParentAndFocus(event);
        break;
    }
  }

  private void notifyParentAndFocus(KeyEvent e) {
    if (_control == null || _control.isDisposed())
      return;

    final Event event = new Event();
    event.character = e.character;
    event.keyCode = e.keyCode;
    event.stateMask = e.stateMask;

    final Composite parent = _control.getParent();
    parent.notifyListeners(SWT.KeyDown, event);
    parent.setFocus();
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
    for (int i = 0; i < _items.length; i++) {
      if (value.equals(_items[i])) {
        return i;
      }
    }

    return -1;
  }

  @Override
  public CCombo getControl() {
    return (CCombo) super.getControl();
  }
}
