/**
 *   Copyright 2011 Karl Martens
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

import static net.karlmartens.ui.widget.ClipboardStrategy.OPERATION_COPY;
import static net.karlmartens.ui.widget.ClipboardStrategy.OPERATION_CUT;
import static net.karlmartens.ui.widget.ClipboardStrategy.OPERATION_PASTE;
import net.karlmartens.platform.util.NumberStringComparator;
import net.karlmartens.ui.widget.Table;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

public final class TableViewerTest {

  public static void main(String[] args) throws Exception {
    final Object[][] input = new Object[5][];
    for (int i = 0; i < input.length; i++) {
      input[i] = new Object[302];
      input[i][0] = "Item " + Integer.toString(i);
      input[i][1] = Boolean.valueOf(i % 3 == 0);
      
      for (int j=2; j<input[i].length; j++) {
        input[i][j] = Integer.toString(Double.valueOf(Math.random() * 10000).intValue());
      }
    }

    final Shell shell = new Shell();
    shell.setLayout(new GridLayout(1, false));
    
    final Display display = shell.getDisplay();
    
    final TableViewer viewer = new TableViewer(shell);
    viewer.setContentProvider(new ArrayContentProvider());
    viewer.setComparator(new ViewerComparator(new NumberStringComparator()));
    viewer.addDeleteCellSelectionSupport();
    viewer.addClipboardSupport(OPERATION_COPY | OPERATION_CUT | OPERATION_PASTE);
  
    final TableViewerColumn c1 = new TableViewerColumn(viewer, SWT.NONE);
    c1.setLabelProvider(new TestColumnLabelProvider(0));
    c1.setEditingSupport(new TestTextEditingSupport(viewer, 0, SWT.LEFT));
    c1.getColumn().setText("Test");
    c1.getColumn().setWidth(75);

    final TableViewerColumn c2 = new TableViewerColumn(viewer, SWT.CHECK);
    c2.setLabelProvider(new TestColumnLabelProvider(1));
    c2.setEditingSupport(new TestBooleanEditingSupport(viewer, 1));
    c2.getColumn().setText("Test 2");
    c2.getColumn().setWidth(60);
    
    for (int i=2; i<302; i++ ) {
      final TableViewerColumn c = new TableViewerColumn(viewer, SWT.RIGHT);
      c.setLabelProvider(new TestColumnLabelProvider(i));
      c.setEditingSupport(new TestTextEditingSupport(viewer, i, SWT.RIGHT));
      c.getColumn().setText("Test " + Integer.toString(i+1));
      c.getColumn().setWidth(40);
    }
    
    final Table table = viewer.getControl();
    table.setLayoutData(GridDataFactory.fillDefaults().create());
    table.setHeaderVisible(true);
    table.setBackground(display.getSystemColor(SWT.COLOR_WHITE));
    table.setFont(new Font(display, "Arial", 8, SWT.NORMAL));
    table.addColumnSortSupport();
    table.setFixedColumnCount(2);

    viewer.setInput(input);

    shell.open();
    while (!shell.isDisposed()) {
      if (!display.readAndDispatch()) {
        display.sleep();
      }
    }
  }
  
}
