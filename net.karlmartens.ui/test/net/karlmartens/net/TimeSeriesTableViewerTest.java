package net.karlmartens.net;

import java.util.BitSet;

import net.karlmartens.platform.util.NumberStringComparator;
import net.karlmartens.ui.viewer.TimeSeriesTableViewer;
import net.karlmartens.ui.viewer.TimeSeriesTableViewerColumn;
import net.karlmartens.ui.viewer.TimeSeriesTableViewerEditor;

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
	}

	public static void main(String[] args) throws Exception {
		final Shell shell = new Shell();
		shell.setLayout(new FillLayout());
		final Display display = shell.getDisplay();
		
		final TimeSeriesTableViewer viewer = new TimeSeriesTableViewer(shell);
		viewer.setContentProvider(new ArrayContentProvider());
		viewer.setLabelProvider(new TestColumnLabelProvider(0));
		viewer.setComparator(new ViewerComparator(new NumberStringComparator()));
		viewer.getControl().setHeaderVisible(true);
		viewer.getControl().setBackground(display.getSystemColor(SWT.COLOR_WHITE));
		viewer.getControl().setFont(new Font(display, "Arial", 10, SWT.NORMAL));
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
}
