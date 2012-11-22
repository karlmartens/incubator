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

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

public class SwitchTest {

  public static void main(String[] args) throws Exception {
    final Shell shell = new Shell();
    final Display display = shell.getDisplay();
    shell.setLayout(new GridLayout(1, false));

    final Switch control = new Switch(shell);
    control.setLayoutData(GridDataFactory.swtDefaults().create());
    control.setActiveText("Shown");
    control.setActiveBackground(new Color(display, 135, 209, 243));
    control.setInactiveBackground(new Color(display, 198, 198, 198));
    control.setInactiveText("Hidden");
    control.setFont(new Font(display, "Arial", 10, SWT.BOLD));
    control.setEnabled(true);
    control.setSelection(true);

    final Switch control1 = new Switch(shell);
    control1.setLayoutData(GridDataFactory.swtDefaults().create());
    control1.setActiveText("Shown");
    control1.setActiveBackground(new Color(display, 135, 209, 243));
    control1.setInactiveBackground(new Color(display, 198, 198, 198));
    control1.setInactiveText("Hidden");
    control1.setFont(new Font(display, "Arial", 10, SWT.BOLD));
    control1.setEnabled(false);
    control1.setSelection(true);

    final Switch control2 = new Switch(shell);
    control2.setLayoutData(GridDataFactory.swtDefaults().create());
    control2.setActiveText("Shown");
    control2.setActiveBackground(new Color(display, 135, 209, 243));
    control2.setInactiveBackground(new Color(display, 198, 198, 198));
    control2.setInactiveText("Hidden");
    control2.setFont(new Font(display, "Arial", 10, SWT.BOLD));
    control2.setEnabled(false);
    control2.setSelection(false);

    shell.open();
    while (!shell.isDisposed()) {
      if (!display.readAndDispatch())
        display.sleep();
    }
  }
}
