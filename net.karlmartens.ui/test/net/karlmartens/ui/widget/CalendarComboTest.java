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
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

public final class CalendarComboTest {

  public static void main(String[] args) throws Exception {
    new CalendarComboTest().run();
  }

  private final Display _display;
  private final Shell _shell;
  private final CalendarCombo _control;

  public CalendarComboTest() {
    _display = Display.getDefault();
    _shell = new Shell(_display);
    _shell.setLayout(new GridLayout(1, false));

    _control = new CalendarCombo(_shell, SWT.NONE);
    _control.setLayoutData(GridDataFactory.swtDefaults().create());
  }

  private void run() {
    _shell.open();
    _shell.layout();
    while (!_shell.isDisposed()) {
      if (!_display.readAndDispatch())
        _display.sleep();
    }
  }

}
