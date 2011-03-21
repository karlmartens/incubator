/**
 *  net.karlmartens.ui, is a library of UI widgets
 *  Copyright (C) 2010,2011
 *  Karl Martens
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Lesser General Public License as 
 *  published by the Free Software Foundation, either version 3 of the 
 *  License, or (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */
package net.karlmartens.ui.widget;

import net.karlmartens.ui.widget.GridChooser;
import net.karlmartens.ui.widget.GridChooserColumn;
import net.karlmartens.ui.widget.GridChooserItem;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

public final class GridChooserTest {

  public static void main(String[] args) throws Exception {
    final Shell shell = new Shell();
    shell.setLayout(new FillLayout());

    final GridChooser chooser = new GridChooser(shell);
    chooser.setHeaderVisible(true);

    GridChooserColumn gc1 = new GridChooserColumn(chooser, SWT.NONE);
    gc1.setText("Test");
    gc1.setWidth(100);

    GridChooserColumn gc2 = new GridChooserColumn(chooser, SWT.NONE);
    gc2.setText("Test2");
    gc2.setWidth(100);

    GridChooserItem gcItem1 = new GridChooserItem(chooser, SWT.NONE);
    gcItem1.setText(new String[] { "Andrew", "2", "3" });

    GridChooserItem gcItem2 = new GridChooserItem(chooser, SWT.NONE);
    gcItem2.setText(new String[] { "Bill", "2", "3" });

    GridChooserItem gcItem3 = new GridChooserItem(chooser, SWT.NONE);
    gcItem3.setText(new String[] { "C", "2", "3" });

    GridChooserItem gcItem4 = new GridChooserItem(chooser, SWT.NONE);
    gcItem4.setText(new String[] { "bob", "2", "3" });

    GridChooserItem gcItem5 = new GridChooserItem(chooser, SWT.NONE);
    gcItem5.setText(new String[] { "1", "2", "3" });

    GridChooserItem gcItem6 = new GridChooserItem(chooser, SWT.NONE);
    gcItem6.setText(new String[] { "11", "2", "3" });

    GridChooserItem gcItem7 = new GridChooserItem(chooser, SWT.NONE);
    gcItem7.setText(new String[] { "2", "2", "3" });

    GridChooserItem gcItem8 = new GridChooserItem(chooser, SWT.NONE);
    gcItem8.setText(new String[] { "1.1", "2", "3" });

    shell.open();

    final Display display = shell.getDisplay();
    while (!shell.isDisposed()) {
      if (!display.readAndDispatch())
        display.sleep();
    }
  }

}
