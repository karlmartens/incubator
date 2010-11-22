package net.karlmartens.net;

import net.karlmartens.ui.viewer.GridChooserViewer;
import net.karlmartens.ui.widget.GridChooser;
import net.karlmartens.ui.widget.GridChooserColumn;
import net.karlmartens.ui.widget.GridChooserItem;

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
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
		
		final GridChooserViewerColumn c1 = new GridChooserViewerColumn(viewer);
		c1.setLabelProvider(new LabelProvider() {
			@Override
			public String getText(Object element) {
				return ((String[]) element)[0];
			}
		});
		c1.getColumn().setText("Test");
		c1.getColumn().setWidth(100);
		
		final GridChooserViewerColumn c2 = new GridChooserViewerColumn(viewer);
		c2.setLabelProvider(new LabelProvider() {
			@Override
			public String getText(Object element) {
				return ((String[]) element)[1];
			}
		});
		c2.getColumn().setText("Test 2");
		c2.getColumn().setWidth(100);
		
		shell.open();
		
		final Display display = shell.getDisplay();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch())
				display.sleep();
		}
	}

}
