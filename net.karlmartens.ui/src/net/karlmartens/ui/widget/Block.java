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
package net.karlmartens.ui.widget;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Layout;

final class Block extends Composite {

  private final Label _label;

  public Block(Composite parent) {
    super(parent, SWT.NONE);

    final GridLayout layout = new GridLayout(1, false);
    layout.horizontalSpacing = 0;
    layout.marginHeight = 0;
    layout.marginWidth = 0;
    layout.verticalSpacing = 0;

    super.setLayout(layout);

    _label = createLabel();

    final PassthoughEventListener listener = new PassthoughEventListener(this);
    listener.addSource(_label);
  }

  @Override
  public void setLayout(Layout layout) {
    checkWidget();
    // Ingore
  }

  @Override
  public void setBackground(Color color) {
    super.setBackground(color);
    _label.setBackground(color);
  }

  @Override
  public void setForeground(Color color) {
    super.setForeground(color);
    _label.setForeground(color);
  }

  @Override
  public void setFont(Font font) {
    super.setFont(font);
    _label.setFont(font);
  }

  public void setAlignment(int alignment) {
    checkWidget();
    _label.setAlignment(alignment);
  }

  public void setEnabled(boolean enabled) {
    checkWidget();
    _label.setEnabled(enabled);
  }

  public void setText(String string) {
    checkWidget();
    _label.setText(string);
  }

  private Label createLabel() {
    final Label label = new Label(this, SWT.NONE);
    label.setLayoutData(GridDataFactory//
        .swtDefaults()//
        .align(SWT.CENTER, SWT.CENTER)//
        .grab(true, true)//
        .create());
    label.setForeground(getForeground());
    label.setBackground(getBackground());
    label.setFont(getFont());
    label.setAlignment(SWT.CENTER);
    return label;
  }

}