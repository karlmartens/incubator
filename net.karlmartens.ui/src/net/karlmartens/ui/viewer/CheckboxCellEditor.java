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

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColumnViewerEditorActivationEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

/**
 * @author karl
 * 
 */
public final class CheckboxCellEditor extends CellEditor {

  private static final int DEFAULT_STYLE = SWT.NONE;

  private boolean _value = false;

  public CheckboxCellEditor() {
    setStyle(DEFAULT_STYLE);
  }

  public CheckboxCellEditor(Composite parent) {
    this(parent, DEFAULT_STYLE);
  }

  public CheckboxCellEditor(Composite parent, int style) {
    super(parent, style);
  }

  public void activate() {
    _value = !_value;
    fireApplyEditorValue();
  }

  protected Control createControl(Composite parent) {
    return null;
  }

  protected Object doGetValue() {
    return Boolean.toString(_value);
  }

  protected void doSetFocus() {
    // Nothing to do
  }

  protected void doSetValue(Object value) {
    Assert.isTrue(value instanceof String);
    _value = Boolean.valueOf((String) value).booleanValue();
  }

  public void activate(ColumnViewerEditorActivationEvent activationEvent) {
    if (activationEvent.eventType == ColumnViewerEditorActivationEvent.TRAVERSAL)
      return;

    super.activate(activationEvent);
  }
}
