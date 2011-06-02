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
package net.karlmartens.ui.viewer;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNull;
import static junit.framework.Assert.assertTrue;
import static net.karlmartens.ui.widget.ClipboardStrategy.OPERATION_COPY;
import static net.karlmartens.ui.widget.ClipboardStrategy.OPERATION_CUT;
import static net.karlmartens.ui.widget.ClipboardStrategy.OPERATION_PASTE;

import java.text.DecimalFormat;
import java.util.Arrays;

import net.karlmartens.platform.text.LocalDateFormat;
import net.karlmartens.platform.util.NumberStringComparator;
import net.karlmartens.ui.SwtTester;
import net.karlmartens.ui.SwtTester.Initializer;
import net.karlmartens.ui.SwtTester.Task;
import net.karlmartens.ui.action.ResizeAllColumnsAction;
import net.karlmartens.ui.widget.TimeSeriesTable;
import net.karlmartens.ui.widget.TimeSeriesTable.ScrollDataMode;

import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Shell;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.junit.Test;

public final class TimeSeriesTableViewerTest {

  private final LocalDate[] _dates;
  private final Object[][] _input;

  public TimeSeriesTableViewerTest() {
    _dates = generateDates();
    final int seriesLength = _dates.length;
    _input = new Object[500][];
    for (int i = 0; i < _input.length; i++) {
      _input[i] = new Object[] { "Item " + Integer.toString(i), Boolean.valueOf(i % 3 == 0), "stuff", generateSeries(seriesLength) };
    }
  }

  @Test
  public void testRemove() {
    SwtTester//
        .test(_initializer)//
        .add(new Task<TimeSeriesTableViewer>() {
          @Override
          public void run(TimeSeriesTableViewer context) {
            final TimeSeriesTable control = context.getControl();

            assertEquals(500, control.getItemCount());

            control.setCellSelections(new Point[] { new Point(5, 499) });
            context.remove((Object) _input[499]);
            assertEquals(499, control.getItemCount());
            assertNull(context.getElementAt(499));
            assertEquals(0, control.getCellSelections().length);
            for (int i = 0; i < 499; i++) {
              assertEquals("Element " + Integer.toString(i), (Object) _input[i], context.getElementAt(i));
            }

            final Point[] expectedTopSelection = new Point[] { new Point(5, 0) };
            control.setCellSelections(expectedTopSelection);
            context.remove((Object) _input[0]);
            assertEquals(498, control.getItemCount());
            for (int i = 1; i < 499; i++) {
              assertEquals("Element " + Integer.toString(i - 1), (Object) _input[i], context.getElementAt(i - 1));
            }
            assertTrue(Arrays.equals(expectedTopSelection, control.getCellSelections()));

            context.remove(new Object[] { _input[10], _input[20], _input[30] });
            assertEquals(495, control.getItemCount());
            int index = 0;
            for (int i = 1; i < 499; i++) {
              if (i == 10 || i == 20 || i == 30)
                continue;

              assertEquals("Element " + Integer.toString(index), (Object) _input[i], context.getElementAt(index));
              index++;
            }
            assertTrue(Arrays.equals(expectedTopSelection, control.getCellSelections()));

            context.setInput(null);
            assertEquals(0, control.getItemCount());
            assertEquals(0, control.getCellSelections().length);
          }
        }).run();
  }

  @Test
  public void testRefresh() {
    SwtTester//
        .test(_initializer)//
        .add(new Task<TimeSeriesTableViewer>() {
          @Override
          public void run(TimeSeriesTableViewer context) {
            final TimeSeriesTable table = context.getControl();
            new ResizeAllColumnsAction(context.getControl()).run();
            final int[] expectedWidths = getColumnWidths(table);
            context.refresh();
            final int[] actualWidths = getColumnWidths(table);
            assertTrue(Arrays.equals(expectedWidths, actualWidths));
          }
        }).run();
  }

  @Test
  public void testFocus() {
    final TimeSeriesTableViewer[] tables = new TimeSeriesTableViewer[1];
    SwtTester//
        .test(_initializer)//
        .add(new Task<TimeSeriesTableViewer>() {
          @Override
          public void run(TimeSeriesTableViewer context) {
            final TimeSeriesTable table = context.getControl();
            tables[0] = _initializer.run(table.getShell());
            assertTrue(tables[0].getControl().setFocus());

            final Event e = new Event();
            e.button = 1;
            e.x = 100;
            e.y = 100;
            table.notifyListeners(SWT.MouseDown, e);
            assertTrue(table.isFocusControl());
          }
        }).run();
  }

  @Test
  public void testRemoveFromModel() throws Exception {
    SwtTester //
        .test(_initializer) //
        .add(new Task<TimeSeriesTableViewer>() {
          @Override
          public void run(TimeSeriesTableViewer context) {
            for (int i = 2; i >= 0; i--) {
              final Object[][] input = Arrays.copyOf(_input, i);
              context.getControl().setCellSelections(new Point[] { new Point(4, i) });
              context.setInput(input);
              Display.getCurrent().readAndDispatch();
            }
          }
        }).run();
  }

  private static int[] getColumnWidths(TimeSeriesTable table) {
    final int columnCount = table.getColumnCount() + table.getPeriodCount();
    final int[] widths = new int[columnCount];
    for (int i = 0; i < columnCount; i++) {
      widths[i] = table.getColumn(i).getWidth();
    }
    return widths;
  }

  public static void main(String[] args) throws Exception {
    final TimeSeriesTableViewerTest test = new TimeSeriesTableViewerTest();

    final Shell shell = new Shell();
    shell.setLayout(new FillLayout());

    final TimeSeriesTableViewer viewer = test._initializer.run(shell);
    viewer.setComparator(new ViewerComparator(new NumberStringComparator()));

    final TimeSeriesTable table = viewer.getControl();
    table.getColumn(0).addSelectionListener(new TestSelectionListener("Period Column"));

    viewer.addSelectionChangedListener(new ISelectionChangedListener() {
      @Override
      public void selectionChanged(SelectionChangedEvent event) {
        System.out.println("Selection changed event");
      }
    });

    viewer.addClipboardSupport(OPERATION_COPY | OPERATION_CUT | OPERATION_PASTE);
    viewer.addDeleteCellSelectionSupport();

    final Display display = shell.getDisplay();
    shell.open();
    while (!shell.isDisposed()) {
      if (!display.readAndDispatch()) {
        display.sleep();
      }
    }
  }

  private final Initializer<TimeSeriesTableViewer> _initializer = new Initializer<TimeSeriesTableViewer>() {

    @Override
    public TimeSeriesTableViewer run(Shell shell) {
      final Display display = shell.getDisplay();

      final TimeSeriesTableViewer viewer = new TimeSeriesTableViewer(shell, SWT.MULTI);
      viewer.setContentProvider(new TestTimeSeriesContentProvider(_dates, 3));
      viewer.setLabelProvider(new TestColumnLabelProvider(0));
      viewer.setEditingSupport(new TestTimeSeriesEditingSupport(new DecimalFormat("#,##0.0000"), 3));

      final TimeSeriesTable table = viewer.getControl();
      table.setHeaderVisible(true);
      table.setBackground(display.getSystemColor(SWT.COLOR_WHITE));
      table.setFont(new Font(display, "Arial", 10, SWT.NORMAL));
      table.setDateFormat(new LocalDateFormat(DateTimeFormat.forPattern("MMM yyyy")));
      table.setNumberFormat(new DecimalFormat("#,##0.00"));
      table.setScrollDataMode(ScrollDataMode.SELECTED_ROWS);
      table.addColumnSortSupport();

      final TimeSeriesTableViewerColumn c1 = new TimeSeriesTableViewerColumn(viewer, SWT.NONE);
      c1.setLabelProvider(new TestColumnLabelProvider(0));
      c1.setEditingSupport(new TestTextEditingSupport(viewer, 0));
      c1.getColumn().setText("Test");
      c1.getColumn().setWidth(75);

      final TimeSeriesTableViewerColumn c2 = new TimeSeriesTableViewerColumn(viewer, SWT.CHECK);
      c2.setLabelProvider(new TestColumnLabelProvider(1));
      c2.setEditingSupport(new TestBooleanEditingSupport(viewer, 1));
      c2.getColumn().setText("Test 2");
      c2.getColumn().setWidth(60);

      viewer.setInput(_input);

      return viewer;
    }
  };

  private static LocalDate[] generateDates() {
    final LocalDate initialDate = new LocalDate(2011, 1, 1);
    final LocalDate[] dates = new LocalDate[12 * 15];
    for (int i = 0; i < dates.length; i++) {
      dates[i] = initialDate.plusMonths(i);
    }
    return dates;
  }

  private static double[] generateSeries(int length) {
    final double[] values = new double[length];
    for (int i = 0; i < values.length; i++) {
      values[i] = Math.random() * 100000;
    }
    return values;
  }
}
