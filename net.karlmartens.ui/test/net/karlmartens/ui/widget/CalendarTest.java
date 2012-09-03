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
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

public final class CalendarTest {

  public static void main(String[] args) throws Exception {
    new CalendarTest().run();
  }

  private final Display _display;
  private final Shell _shell;
  private final Calendar _control;

  public CalendarTest() {
    final GridLayout layout = new GridLayout(1, false);
    layout.marginHeight = 0;
    layout.marginWidth = 0;
    layout.verticalSpacing = 0;

    _display = Display.getDefault();
    _shell = new Shell(_display);
    _shell.setLayout(layout);

    _control = new Calendar(_shell, SWT.NONE);
    _control.setLayoutData(GridDataFactory.swtDefaults().create());
    _control.setTitleFont(new Font(_display, "Arial", 12, SWT.BOLD));
    _control.setFont(new Font(_display, "Arial", 10, SWT.NORMAL));
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
