package net.karlmartens.net;

import net.karlmartens.platform.util.NumberStringComparator;
import net.karlmartens.ui.viewer.TimeSeriesTableViewer;
import net.karlmartens.ui.viewer.TimeSeriesTableViewerColumn;

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

public class TimeSeriesTableViewerTest {

	public static void main(String[] args) throws Exception {
		final Shell shell = new Shell();
		shell.setLayout(new FillLayout());
		
		final TimeSeriesTableViewer viewer = new TimeSeriesTableViewer(shell);
		viewer.setContentProvider(new ArrayContentProvider());
		viewer.setLabelProvider(new TestColumnLabelProvider(0));
		viewer.setComparator(new ViewerComparator(new NumberStringComparator()));
		
		final TimeSeriesTableViewerColumn c1 = new TimeSeriesTableViewerColumn(viewer, SWT.NONE);
		c1.setLabelProvider(new TestColumnLabelProvider(0));
		c1.getColumn().setText("Test");
		
		final TimeSeriesTableViewerColumn c2 = new TimeSeriesTableViewerColumn(viewer, SWT.NONE);
		c2.setLabelProvider(new TestColumnLabelProvider(1));
		c2.getColumn().setText("Test 2");
		
		viewer.setInput(new String[][] {//
				{"Andrew", "true"}, //
				{"Bill", "true"}, //
				{"C", "false"}, //
				{"bob", "true"}, //
				{"1", "true"}, //
				{"11", "false"}, //
				{"2", "false"}, //
				{"1.1", "true"} //
		});
		
		shell.open();
		final Display display = shell.getDisplay();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch())
				display.sleep();
		}
	}
	
}
