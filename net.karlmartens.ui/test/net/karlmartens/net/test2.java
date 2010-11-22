package net.karlmartens.net;

import net.karlmartens.ui.viewer.GridChooserViewer;
import net.karlmartens.ui.viewer.GridChooserViewerColumn;

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

public final class test2 {

	public static void main(String[] args) throws Exception {
		final Shell shell = new Shell();
		shell.setLayout(new FillLayout());
		
		final GridChooserViewer viewer = new GridChooserViewer(shell);
		viewer.setContentProvider(new ArrayContentProvider());
		viewer.getGridChooser().setHeaderVisible(true);
		
		final GridChooserViewerColumn c1 = new GridChooserViewerColumn(viewer, SWT.NONE);
		c1.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				return ((String[]) element)[0];
			}
		});
		c1.getColumn().setText("Test");
		c1.getColumn().setWidth(100);
		
		final GridChooserViewerColumn c2 = new GridChooserViewerColumn(viewer, SWT.NONE);
		c2.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				return ((String[]) element)[0];
			}
		});
		c2.getColumn().setText("Test 2");
		c2.getColumn().setWidth(100);
		
		viewer.setInput(new String[][] {
				{"Andrew", "2", "3"}, //
				{"Bill", "2", "3"}, //
				{"C", "2", "3"}, //
				{"bob", "2", "3"}, //
				{"1", "2", "3"}, //
				{"11", "2", "3"}, //
				{"2", "2", "3"}, //
				{"1.1", "2", "3"} //
		});
		
		shell.open();
		
		final Display display = shell.getDisplay();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch())
				display.sleep();
		}
	}

}
