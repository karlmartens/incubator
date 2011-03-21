/**
 *  net.karlmartens.ui, is a library of UI widgets
 *  Copyright (C) 2010,2011
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

import net.karlmartens.platform.util.NullSafe;
import net.karlmartens.platform.util.NumberStringComparator;
import net.karlmartens.ui.viewer.GridChooserViewer;
import net.karlmartens.ui.viewer.GridChooserViewerColumn;
import net.karlmartens.ui.viewer.GridChooserViewerEditor;
import net.karlmartens.ui.widget.GridChooserColumnWeightedResize;

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.CellEditor;
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
    final Display display = shell.getDisplay();
    shell.setLayout(new FillLayout());

    final GridChooserViewer viewer = new GridChooserViewer(shell);
    viewer.setContentProvider(new ArrayContentProvider());
    viewer.setLabelProvider(new TestColumnLabelProvider(0));
    viewer.setComparator(new ViewerComparator(new NumberStringComparator()));

    viewer.getControl().setHeaderVisible(true);

    viewer.addSelectionChangedListener(new ISelectionChangedListener() {
      @Override
      public void selectionChanged(SelectionChangedEvent event) {
        System.out.println("Selection changed event");
      }
    });

    final ColumnViewerEditorActivationStrategy activationStrategy = new ColumnViewerEditorActivationStrategy(viewer) {
      @Override
      protected boolean isEditorActivationEvent(ColumnViewerEditorActivationEvent event) {
        return event.eventType == ColumnViewerEditorActivationEvent.TRAVERSAL
            || event.eventType == ColumnViewerEditorActivationEvent.MOUSE_DOUBLE_CLICK_SELECTION
            || (event.eventType == ColumnViewerEditorActivationEvent.KEY_PRESSED && event.keyCode == SWT.CR)
            || event.eventType == ColumnViewerEditorActivationEvent.PROGRAMMATIC;
      }
    };

    GridChooserViewerEditor.create(viewer, activationStrategy, ColumnViewerEditor.TABBING_HORIZONTAL | ColumnViewerEditor.TABBING_MOVE_TO_ROW_NEIGHBOR
        | ColumnViewerEditor.TABBING_VERTICAL | ColumnViewerEditor.KEYBOARD_ACTIVATION);

    final GridChooserViewerColumn c1 = new GridChooserViewerColumn(viewer, SWT.NONE);
    c1.setLabelProvider(new TestColumnLabelProvider(0));
    c1.getColumn().setText("Test");
    c1.getColumn().setMoveable(true);

    final GridChooserViewerColumn c2 = new GridChooserViewerColumn(viewer, SWT.NONE);
    c2.setLabelProvider(new TestColumnLabelProvider(1));
    c2.setEditingSupport(new TextEditingSupport(viewer, 1));
    c2.getColumn().setText("Test 2");
    c2.getColumn().setMoveable(true);

    new GridChooserColumnWeightedResize(viewer.getControl(), new int[] { 1, 1 });

    viewer.setInput(new String[][] { { "Andrew", "2", "3" }, //
        { "Bill", "2", "3" }, //
        { "C", "2", "3" }, //
        { "bob", "2", "3" }, //
        { "1", "2", "3" }, //
        { "11", "2", "3" }, //
        { "2", "2", "3" }, //
        { "1.1", "2", "3" } //
        });

    shell.open();
    while (!shell.isDisposed()) {
      if (!display.readAndDispatch())
        display.sleep();
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
      return new TextCellEditor(_viewer.getControl());
    }

    @Override
    protected boolean canEdit(Object element) {
      return true;
    }

    @Override
    protected Object getValue(Object element) {
      final String[] data = (String[]) element;
      return data[_index];
    }

    @Override
    protected void setValue(Object element, Object value) {
      final String[] data = (String[]) element;
      data[_index] = NullSafe.toString(value);
    }
  }
}
