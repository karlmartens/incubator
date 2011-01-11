package net.karlmartens.net;

import net.karlmartens.platform.util.NumberStringComparator;
import net.karlmartens.ui.viewer.TimeSeriesTableViewer;
import net.karlmartens.ui.viewer.TimeSeriesTableViewerColumn;

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.CheckboxCellEditor;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import com.jidesoft.utils.Lm;

public class TimeSeriesTableViewerTest {

	public static void main(String[] args) throws Exception {
		Lm.verifyLicense("3esi", "esi.manage", //$NON-NLS-1$//$NON-NLS-2$
        ".kk4DMgzlNhCN9eJ.H.:oZQYOhx0At23");

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
		c2.setEditingSupport(new BooleanEditingSupport(viewer, 1));
		c2.getColumn().setText("Test 2");
		
		viewer.addSelectionChangedListener(new ISelectionChangedListener() {
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				final Object[] selected = ((IStructuredSelection)viewer.getSelection()).toArray();
				if (selected.length == 0)
					System.out.println("NULL");
				
				for (Object o : selected) {
					final Object[] row = (Object[])o;
					System.out.println(row[0].toString());
				}
			}
		});
		
		viewer.setInput(new Object[][] {//
				{"Andrew", true}, //
				{"Bill", true}, //
				{"C", false}, //
				{"bob", true}, //
				{"1", true}, //
				{"11", false}, //
				{"2", false}, //
				{"1.1", true} //
		});
		
		shell.open();
		final Display display = shell.getDisplay();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch())
				display.sleep();
		}
	}
	
	private static class BooleanEditingSupport extends EditingSupport {

		private final TimeSeriesTableViewer _viewer;
		private final int _index;

		public BooleanEditingSupport(TimeSeriesTableViewer viewer, int index) {
			super(viewer);
			_viewer = viewer;
			_index = index;
		}

		@Override
		protected CellEditor getCellEditor(Object element) {
			return new CheckboxCellEditor(_viewer.getControl());
		}

		@Override
		protected boolean canEdit(Object element) {
			return true;
		}

		@Override
		protected Object getValue(Object element) {
			final Object[] data = (Object[])element;
			return data[_index];
		}

		@Override
		protected void setValue(Object element, Object value) {
			final Object[] data = (Object[])element;
			data[_index] = value;
		}
	}
}
