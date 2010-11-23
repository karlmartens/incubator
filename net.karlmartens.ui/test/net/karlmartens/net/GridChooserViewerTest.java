package net.karlmartens.net;

import net.karlmartens.platform.util.NullSafe;
import net.karlmartens.platform.util.NumberStringComparator;
import net.karlmartens.ui.viewer.GridChooserViewer;
import net.karlmartens.ui.viewer.GridChooserViewerColumn;
import net.karlmartens.ui.viewer.GridChooserViewerEditor;

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ColumnViewerEditor;
import org.eclipse.jface.viewers.ColumnViewerEditorActivationEvent;
import org.eclipse.jface.viewers.ColumnViewerEditorActivationStrategy;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

public final class GridChooserViewerTest {

	public static void main(String[] args) throws Exception {
		final Shell shell = new Shell();
		shell.setLayout(new FillLayout());
		
		final GridChooserViewer viewer = new GridChooserViewer(shell);
		viewer.setContentProvider(new ArrayContentProvider());
		viewer.setLabelProvider(new ColumnLabelProviderImpl(0));
		viewer.setComparator(new ViewerComparator(new NumberStringComparator()));
		viewer.getGridChooser().setHeaderVisible(true);
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
					|| (event.eventType == ColumnViewerEditorActivationEvent.KEY_PRESSED && event.keyCode == SWT.CR)
					|| event.eventType == ColumnViewerEditorActivationEvent.PROGRAMMATIC;
			}
		};
		
		GridChooserViewerEditor.create(viewer, activationStrategy, 
					ColumnViewerEditor.TABBING_HORIZONTAL | 
					ColumnViewerEditor.TABBING_MOVE_TO_ROW_NEIGHBOR | 
					ColumnViewerEditor.TABBING_VERTICAL | 
					ColumnViewerEditor.KEYBOARD_ACTIVATION);
		
		final GridChooserViewerColumn c1 = new GridChooserViewerColumn(viewer, SWT.NONE);
		c1.setLabelProvider(new ColumnLabelProviderImpl(0));
		c1.getColumn().setText("Test");
		c1.getColumn().setWidth(100);
		
		final GridChooserViewerColumn c2 = new GridChooserViewerColumn(viewer, SWT.NONE);
		c2.setLabelProvider(new ColumnLabelProviderImpl(1));
		c2.setEditingSupport(new TextEditingSupport(viewer, 1));
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
	
	private static class ColumnLabelProviderImpl extends ColumnLabelProvider {
		
		private final int _index;

		private ColumnLabelProviderImpl(int index) {
			_index = index;
		}
	
		@Override
		public String getText(Object element) {
			return ((String[]) element)[_index];
		}
	}
	
	private static class TextEditingSupport extends EditingSupport {

		private final GridChooserViewer _viewer;
		private final int _index;

		public TextEditingSupport(GridChooserViewer viewer, int index) {
			super(viewer);
			_viewer = viewer;
			_index = index;
		}

		@Override
		protected CellEditor getCellEditor(Object element) {
			return new TextCellEditor(_viewer.getGridChooser());
		}

		@Override
		protected boolean canEdit(Object element) {
			return true;
		}

		@Override
		protected Object getValue(Object element) {
			final String[] data = (String[])element;
			return data[_index];
		}

		@Override
		protected void setValue(Object element, Object value) {
			final String[] data = (String[])element;
			data[_index] = NullSafe.toString(value);
		}
	}
}
