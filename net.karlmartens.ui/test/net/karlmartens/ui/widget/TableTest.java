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

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

public class TableTest {

  public static void main(String[] args) throws Exception {
    final Shell shell = new Shell();
    final Display display = shell.getDisplay();
    shell.setLayout(new FillLayout());

    final Table table = new Table(shell, SWT.NONE);
    table.setHeaderVisible(true);
    table.setBackground(display.getSystemColor(SWT.COLOR_WHITE));
    table.setFont(new Font(display, "Arial", 10, SWT.NONE));
    table.addColumnSortSupport();

    IMenuManager menuManager = table.getMenuManager();
    menuManager.appendToGroup(Table.GROUP_COMMAND, new Action("Test") {
      @Override
      public void run() {
        MessageDialog.openInformation(shell, "Test", "Test");
      }
    });

    final TableColumn[] columns = { //
    new TableColumn(table, SWT.NONE), //
        new TableColumn(table, SWT.CHECK), //
    };

    columns[0].setText("Target");
    columns[0].setWidth(75);

    columns[1].setText("Enabled");
    columns[1].setWidth(60);

    final TableItem[] items = { new TableItem(table), //
        new TableItem(table), //
        new TableItem(table), //
        new TableItem(table), //
        new TableItem(table), //
    };

    items[0].setText(new String[] { "Rigs", "true" });
    // items[0].setBackground(display.getSystemColor(SWT.COLOR_LIST_SELECTION));
    items[1].setText(new String[] { "Capital", "true" });
    // items[1].setBackground(1,
    // display.getSystemColor(SWT.COLOR_TITLE_INACTIVE_BACKGROUND));
    // items[1].setForeground(1, display.getSystemColor(SWT.COLOR_RED));
    items[2].setText(new String[] { "Oil", "true" });
    items[3].setText(new String[] { "Water", "true" });
    items[4].setText(new String[] { "Steel", "true" });

    shell.open();
    while (!shell.isDisposed()) {
      if (!display.readAndDispatch())
        display.sleep();
    }
  }
}
