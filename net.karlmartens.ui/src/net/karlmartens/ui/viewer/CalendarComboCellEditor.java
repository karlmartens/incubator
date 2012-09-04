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

import net.karlmartens.ui.widget.DateCombo;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColumnViewerEditorActivationEvent;
import org.eclipse.swt.SWT;
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

public class CalendarComboCellEditor extends CellEditor {

  public static final int DROP_DOWN_ON_MOUSE_ACTIVATION = 1;
  public static final int DROP_DOWN_ON_KEY_ACTIVATION = 1 << 1;
  public static final int DROP_DOWN_ON_PROGRAMMATIC_ACTIVATION = 1 << 2;
  public static final int DROP_DOWN_ON_TRAVERSE_ACTIVATION = 1 << 3;

  private static final int DEFAULT_STYLE = SWT.NONE;

  private boolean _selectAll = true;
  private String _selection;

  private DateCombo _control;

  public CalendarComboCellEditor() {
    setStyle(DEFAULT_STYLE);
  }

  public CalendarComboCellEditor(Composite parent) {
    this(parent, DEFAULT_STYLE);
  }

  public CalendarComboCellEditor(Composite parent, int style) {
    super(parent, style);
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

    String text = null;
    if (activationEvent.eventType == ColumnViewerEditorActivationEvent.KEY_PRESSED) {
      switch (activationEvent.character) {
        case ' ':
          text = _selection;
          _selectAll = true;
          break;

        case SWT.BS:
        case SWT.DEL:
          text = "";
          _selectAll = false;
          break;

        default:
          text = String.valueOf(activationEvent.character);
          _selectAll = false;
      }
    }

    if (text != null)
      getControl().setText(text);
  }

  protected Control createControl(Composite parent) {
    _control = new DateCombo(parent, getStyle());
    _control.setFont(parent.getFont());

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
        _selection = _control.getText();
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
        CalendarComboCellEditor.this.focusLost();
      }
    });

    return _control;
  }

  protected Object doGetValue() {
    return _selection;
  }

  protected void doSetFocus() {
    _control.setFocus();

    if (!_selectAll) {
      final DateCombo combo = getControl();
      combo.setTextSelection(combo.getText().length());
    }
  }

  protected void doSetValue(Object value) {
    Assert.isTrue(_control != null && (value instanceof String));
    _selection = (String) value;
    _control.setText(_selection);
  }

  void applyEditorValueAndDeactivate() {
    // must set the selection before getting value
    _selection = _control.getText();
    final Object newValue = doGetValue();
    markDirty();
    boolean isValid = isCorrect(newValue);
    setValueValid(isValid);

    if (!isValid) {
      setErrorMessage(MessageFormat.format(getErrorMessage(),
          new Object[] { _selection }));
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
        if (!getControl().isCalendarVisible()) {
          applyEditorValueAndDeactivate();
          notifyParentAndFocus(event);
        }
        break;

      case SWT.TAB:
      case SWT.CR:
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

  @Override
  public DateCombo getControl() {
    return (DateCombo) super.getControl();
  }

  @Override
  protected boolean dependsOnExternalFocusListener() {
    return getClass() != CalendarComboCellEditor.class;

  }
}
