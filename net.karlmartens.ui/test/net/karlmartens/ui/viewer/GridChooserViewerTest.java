/**
 *   Copyright 2010,2011 Karl Martens
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

import net.karlmartens.platform.util.NullSafe;
import net.karlmartens.platform.util.NumberStringComparator;
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
    private TextCellEditor _cellEditor;

    public TextEditingSupport(GridChooserViewer viewer, int index) {
      super(viewer);
      _viewer = viewer;
      _index = index;
    }

    @Override
    protected CellEditor getCellEditor(Object element) {
      if (_cellEditor == null) {
        _cellEditor = new TextCellEditor(_viewer.getControl());
      }
      return _cellEditor;
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
