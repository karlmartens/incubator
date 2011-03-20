/**
 *  net.karlmartens.ui, is a library of UI widgets
 *  Copyright (C) 2011
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
package net.karlmartens.ui.viewer;

import static net.karlmartens.ui.widget.ClipboardStrategy.OPERATION_COPY;
import static net.karlmartens.ui.widget.ClipboardStrategy.OPERATION_CUT;
import static net.karlmartens.ui.widget.ClipboardStrategy.OPERATION_PASTE;

import java.text.DecimalFormat;

import net.karlmartens.platform.text.LocalDateFormat;
import net.karlmartens.platform.util.NumberStringComparator;
import net.karlmartens.ui.widget.TimeSeriesTable;
import net.karlmartens.ui.widget.TimeSeriesTable.ScrollDataMode;

import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;

public final class TimeSeriesTableViewerTest {

	public static void main(String[] args) throws Exception {
		final Shell shell = new Shell();
		shell.setLayout(new FillLayout());
		final Display display = shell.getDisplay();

		final LocalDate[] dates = generateDates();

		final TimeSeriesTableViewer viewer = new TimeSeriesTableViewer(shell);
		viewer.setContentProvider(new TestTimeSeriesContentProvider(dates, 3));
		viewer.setLabelProvider(new TestColumnLabelProvider(0));
		viewer.setComparator(new ViewerComparator(new NumberStringComparator()));
		viewer.setEditingSupport(new TestTimeSeriesEditingSupport(
				new DecimalFormat("#,##0.0000"), 3));

		final TimeSeriesTable table = viewer.getControl();
		table.setHeaderVisible(true);
		table.setBackground(display.getSystemColor(SWT.COLOR_WHITE));
		table.setFont(new Font(display, "Arial", 10, SWT.NORMAL));
		table.setDateFormat(new LocalDateFormat(DateTimeFormat
				.forPattern("MMM yyyy")));
		table.setNumberFormat(new DecimalFormat("#,##0.00"));
		table.setScrollDataMode(ScrollDataMode.SELECTED_ROWS);

		viewer.addSelectionChangedListener(new ISelectionChangedListener() {
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				System.out.println("Selection changed event");
			}
		});

		final TimeSeriesTableViewerColumn c1 = new TimeSeriesTableViewerColumn(
				viewer, SWT.NONE);
		c1.setLabelProvider(new TestColumnLabelProvider(0));
		c1.setEditingSupport(new TestTextEditingSupport(viewer, 0));
		c1.getColumn().setText("Test");
		c1.getColumn().setWidth(75);

		final TimeSeriesTableViewerColumn c2 = new TimeSeriesTableViewerColumn(
				viewer, SWT.CHECK);
		c2.setLabelProvider(new TestColumnLabelProvider(1));
		c2.setEditingSupport(new TestBooleanEditingSupport(viewer, 1));
		c2.getColumn().setText("Test 2");
		c2.getColumn().setWidth(60);

		new ViewerClipboardManager(viewer, OPERATION_COPY | OPERATION_CUT
				| OPERATION_PASTE);

		final int seriesLength = dates.length;
		final Object[][] input = new Object[500][];
		for (int i = 0; i < input.length; i++) {
			input[i] = new Object[] { "Item " + Integer.toString(i),
					Boolean.valueOf(i % 3 == 0), "stuff",
					generateSeries(seriesLength) };
		}
		viewer.setInput(input);

		shell.open();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch())
				display.sleep();
		}
	}

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
