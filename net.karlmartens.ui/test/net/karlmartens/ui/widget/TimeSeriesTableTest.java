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

import java.text.DecimalFormat;

import net.karlmartens.platform.text.LocalDateFormat;
import net.karlmartens.ui.widget.TimeSeriesTable.ScrollDataMode;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;

public class TimeSeriesTableTest {

  private static final LocalDate DEFAULT_DATE = new LocalDate(2000, 1, 1);

  public static void main(String[] args) throws Exception {
    final Shell shell = new Shell();
    final Display display = shell.getDisplay();
    shell.setLayout(new FillLayout());

    final LocalDate[] periods = new LocalDate[60];
    for (int i = 0; i < periods.length; i++) {
      periods[i] = DEFAULT_DATE.plusMonths(i);
    }

    final TimeSeriesTable table = new TimeSeriesTable(shell);
    table.setHeaderVisible(true);
    table.setScrollDataMode(ScrollDataMode.SELECTED_ROWS);
    table.setPeriods(periods);
    table.setNumberFormat(new DecimalFormat("#,##0.00"));
    table.setDateFormat(new LocalDateFormat(DateTimeFormat.forPattern("MMM yyyy")));
    table.setBackground(display.getSystemColor(SWT.COLOR_WHITE));
    table.setFont(new Font(display, "Arial", 10, SWT.NONE));

    final TimeSeriesTableColumn[] columns = { new TimeSeriesTableColumn(table, SWT.NONE), //
        new TimeSeriesTableColumn(table, SWT.CHECK), //
    };

    columns[0].setText("Target");
    columns[0].setWidth(75);

    columns[1].setText("Enabled");
    columns[1].setWidth(60);

    final TimeSeriesTableItem[] items = { new TimeSeriesTableItem(table), //
        new TimeSeriesTableItem(table), //
        new TimeSeriesTableItem(table), //
        new TimeSeriesTableItem(table), //
        new TimeSeriesTableItem(table), //
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

    for (int i = 0; i < items.length; i++) {
      final double[] data = new double[periods.length];
      for (int j = 0; j < data.length; j++) {
        data[j] = Math.random() * 10000;
      }
      items[i].setValue(data);
    }

    table.scrollColumnTo(new LocalDate(2000, 2, 1));

    shell.open();
    while (!shell.isDisposed()) {
      if (!display.readAndDispatch())
        display.sleep();
    }
  }
}
