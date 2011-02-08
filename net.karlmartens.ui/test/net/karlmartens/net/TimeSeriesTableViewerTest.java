package net.karlmartens.net;

import java.text.DecimalFormat;
import java.util.BitSet;

import net.karlmartens.platform.text.LocalDateFormat;
import net.karlmartens.platform.util.NumberStringComparator;
import net.karlmartens.ui.viewer.TimeSeriesContentProvider;
import net.karlmartens.ui.viewer.TimeSeriesTableViewer;
import net.karlmartens.ui.viewer.TimeSeriesTableViewerColumn;
import net.karlmartens.ui.viewer.TimeSeriesTableViewerEditor;
import net.karlmartens.ui.widget.TimeSeriesTable;
import net.karlmartens.ui.widget.TimeSeriesTable.ScrollDataMode;

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnViewerEditor;
import org.eclipse.jface.viewers.ColumnViewerEditorActivationEvent;
import org.eclipse.jface.viewers.ColumnViewerEditorActivationStrategy;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.joda.time.Interval;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;

public final class TimeSeriesTableViewerTest {
	private static BitSet TRAVERSAL_KEYS = new BitSet();
	
	static {
		TRAVERSAL_KEYS.set(SWT.HOME);
		TRAVERSAL_KEYS.set(SWT.END);
		TRAVERSAL_KEYS.set(SWT.ARROW_LEFT);
		TRAVERSAL_KEYS.set(SWT.ARROW_RIGHT);
		TRAVERSAL_KEYS.set(SWT.ARROW_DOWN);
		TRAVERSAL_KEYS.set(SWT.ARROW_UP);
		TRAVERSAL_KEYS.set(SWT.PAGE_UP);
		TRAVERSAL_KEYS.set(SWT.PAGE_DOWN);
		TRAVERSAL_KEYS.set(SWT.TAB);		
		TRAVERSAL_KEYS.set(SWT.SHIFT);		
		TRAVERSAL_KEYS.set(SWT.CONTROL);		
		TRAVERSAL_KEYS.set(SWT.ALT);		
		TRAVERSAL_KEYS.set(SWT.COMMAND);		
	}

	public static void main(String[] args) throws Exception {
		final Shell shell = new Shell();
		shell.setLayout(new FillLayout());
		final Display display = shell.getDisplay();
		
		final TimeSeriesTableViewer viewer = new TimeSeriesTableViewer(shell);
		viewer.setContentProvider(new TestTimeSeriesContentProvider());
		viewer.setLabelProvider(new TestColumnLabelProvider(0));
		viewer.setComparator(new ViewerComparator(new NumberStringComparator()));
		//viewer.setEditingSupport();
		
		final TimeSeriesTable table = viewer.getControl();
		table.setHeaderVisible(true);
		table.setBackground(display.getSystemColor(SWT.COLOR_WHITE));
		table.setFont(new Font(display, "Arial", 10, SWT.NORMAL));
		table.setDateFormat(new LocalDateFormat(DateTimeFormat.forPattern("MMM yyyy")));
		table.setNumberFormat(new DecimalFormat("#,##0.00"));
		table.setScrollDataMode(ScrollDataMode.SELECTED_ROWS);
		
		viewer.addSelectionChangedListener(new ISelectionChangedListener() {
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				System.out.println("Selection changed event");
			}
		});
		
		final ColumnViewerEditorActivationStrategy activationStrategy = new ColumnViewerEditorActivationStrategy(viewer) {
			@Override
			protected boolean isEditorActivationEvent(
					ColumnViewerEditorActivationEvent event) {
				return event.eventType == ColumnViewerEditorActivationEvent.TRAVERSAL
					|| event.eventType == ColumnViewerEditorActivationEvent.MOUSE_DOUBLE_CLICK_SELECTION
					|| (event.eventType == ColumnViewerEditorActivationEvent.KEY_PRESSED && (!TRAVERSAL_KEYS.get(event.keyCode)))
					|| event.eventType == ColumnViewerEditorActivationEvent.PROGRAMMATIC;
			}
		};
		
		TimeSeriesTableViewerEditor.create(viewer, activationStrategy, 
					ColumnViewerEditor.TABBING_HORIZONTAL | 
					ColumnViewerEditor.TABBING_MOVE_TO_ROW_NEIGHBOR | 
					ColumnViewerEditor.TABBING_VERTICAL | 
					ColumnViewerEditor.KEYBOARD_ACTIVATION);
		
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
		
		viewer.setInput(new String[][] {
				{"Rigs", "true", "3"}, //
				{"Capital", "true", "3"}, //
				{"Oil", "true", "3"}, //
				{"Water", "true", "3"}, //
				{"Custom", "true", "3"}, //
		});
		
		shell.open();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch())
				display.sleep();
		}
	}
	
	private static class TestTimeSeriesContentProvider extends ArrayContentProvider implements TimeSeriesContentProvider {

		private final LocalDate[] _dates;
		
		public TestTimeSeriesContentProvider() {
			final LocalDate initialDate = new LocalDate(2011, 1, 1);
			_dates = new LocalDate[12*15];
			for (int i=0; i<_dates.length; i++) {
				_dates[i] = initialDate.plusMonths(i);
			}
		}
		
		@Override
		public LocalDate[] getDates() {
			return _dates;
		}
		
		@Override
		public Double getValue(Object element, Interval interval) {
			return Math.random() * 100000;
		}
	}
}
