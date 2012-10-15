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

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.ResourceBundle;

import net.karlmartens.ui.Activator;
import net.karlmartens.ui.UiUtil;
import net.karlmartens.ui.widget.ClipboardStrategy;
import net.karlmartens.ui.widget.Table;
import net.karlmartens.ui.widget.TableColumn;
import net.karlmartens.ui.widget.TableItem;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler2;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchCommandConstants;
import org.eclipse.ui.IWorkbenchPartSite;
import org.eclipse.ui.handlers.IHandlerActivation;
import org.eclipse.ui.handlers.IHandlerService;

import au.com.bytecode.opencsv.CSVReader;
import au.com.bytecode.opencsv.CSVWriter;

public final class TableViewerClipboardManager extends CellSelectionModifier {

  private final TableViewer _viewer;
  private final int _operations;

  public TableViewerClipboardManager(IWorkbenchPartSite site,
      TableViewer viewer, int operations) {
    super(viewer);
    _viewer = viewer;
    _operations = operations;

    final IHandlerService service = (IHandlerService) site
        .getService(IHandlerService.class);
    activateHandlers(service);
  }

  private boolean isOperationEnabled(int operation) {
    return (_operations & operation) != 0;
  }

  public TableViewerClipboardManager(TableViewer viewer, int operations) {
    super(viewer);
    _viewer = viewer;
    _operations = operations;
    hookControl(viewer.getControl());
  }

  private void activateHandlers(final IHandlerService service) {
    final List<IHandlerActivation> activations = new ArrayList<IHandlerActivation>();

    activations.add(service.activateHandler(
        IWorkbenchCommandConstants.EDIT_COPY, new AbstractHandler() {
          @Override
          public Object execute(ExecutionEvent event)
              throws ExecutionException {
            if (isOperationEnabled(OPERATION_COPY))
              copy();
            
            return null;
          }

          @Override
          public void setEnabled(Object evaluationContext) {
            setBaseEnabled(UiUtil.hasFocus(_viewer.getControl(), true));
          }
        }));

    activations.add(service.activateHandler(
        IWorkbenchCommandConstants.EDIT_CUT, new AbstractHandler() {
          @Override
          public Object execute(ExecutionEvent event)
              throws ExecutionException {
            if (isOperationEnabled(OPERATION_CUT))
              cut();
            
            return null;
          }

          @Override
          public void setEnabled(Object evaluationContext) {
            setBaseEnabled(UiUtil.hasFocus(_viewer.getControl(), true));
          }
        }));

      activations.add(service.activateHandler(
          IWorkbenchCommandConstants.EDIT_PASTE, new AbstractHandler() {
            @Override
            public Object execute(ExecutionEvent event)
                throws ExecutionException {
              if (isOperationEnabled(OPERATION_PASTE))
                paste();
              
              return null;
            }

            @Override
            public void setEnabled(Object evaluationContext) {
              setBaseEnabled(UiUtil.hasFocus(_viewer.getControl(), true));
            }
          }));

    final Table t = _viewer.getControl();

    new Listener() {

      {
        t.addListener(SWT.Dispose, this);
        t.addListener(SWT.FocusIn, this);
        t.addListener(SWT.FocusOut, this);
      }

      @Override
      public void handleEvent(Event event) {
        if (event.type == SWT.Dispose) {
          t.removeListener(SWT.Dispose, this);
          t.removeListener(SWT.FocusIn, this);
          t.removeListener(SWT.FocusOut, this);

          for (IHandlerActivation activation : activations) {
            try {
              service.deactivateHandler(activation);
            } catch (Throwable t) {
              // Ignore
            }
          }
          return;
        }

        if (event.type == SWT.FocusIn || event.type == SWT.FocusOut) {
          for (IHandlerActivation activation : activations) {
            ((IHandler2) activation.getHandler()).setEnabled(null);
          }
          return;
        }
      }
    };
  }

  private void hookControl(final Control control) {
    final ClipboardStrategy clipboardStrategy = new ClipboardStrategy();

    final Listener listener = new Listener() {
      @Override
      public void handleEvent(Event event) {
        if (!clipboardStrategy.isClipboardEvent(event))
          return;
        
        if (!hasFocus(control))
          return;

        switch (clipboardStrategy.getOperation(event)) {
          case OPERATION_COPY:
            if (isOperationEnabled(OPERATION_COPY)) {
              if (copy())
                UiUtil.consume(event);
            }
            break;

          case OPERATION_PASTE:
            if (isOperationEnabled(OPERATION_PASTE)) {
              if (paste())
                UiUtil.consume(event);
            }
            break;

          case OPERATION_CUT:
            if (isOperationEnabled(OPERATION_CUT)) {
              if (cut())
                UiUtil.consume(event);
            }
            break;
        }
      }

      private boolean hasFocus(Control control) {
        final Display display = control.getDisplay();
        Control focus = display.getFocusControl();
        while (focus != null) {
          if (focus == control)
            return true;
          
          focus = focus.getParent();
        }

        return false;
      }
    };

    final Display display = control.getDisplay();
    display.addFilter(SWT.KeyDown, listener);

    control.addDisposeListener(new DisposeListener() {
      @Override
      public void widgetDisposed(DisposeEvent e) {
        if (display.isDisposed())
          return;

        display.removeFilter(SWT.KeyDown, listener);
      }
    });
  }

  private boolean copy() {
    final boolean[] result = new boolean[1];
    BusyIndicator.showWhile(_viewer.getControl().getDisplay(), new Runnable() {
      @Override
      public void run() {
        final Point[] cells = _viewer.doGetCellSelections();
        Arrays.sort(cells, _comparator);
        final int length = computeRegion(cells);
        if (length <= 0) {
          result[0] = length == 0;
          return;
        }

        final String[] values = getValues(cells);

        final StringWriter sw = new StringWriter();
        final CSVWriter writer = new CSVWriter(sw, '\t');
        for (int i = 0; i < cells.length / length; i++) {
          final String[] row = new String[length];
          System.arraycopy(values, i * length, row, 0, row.length);
          writer.writeNext(row);
        }

        try {
          writer.close();
        } catch (IOException e) {
          throw new RuntimeException(e);
        }

        final StringBuffer buffer = sw.getBuffer();
        buffer.setLength(buffer.length() - CSVWriter.DEFAULT_LINE_END.length());

        final Clipboard cb = new Clipboard(_viewer.getControl().getDisplay());
        cb.setContents(new String[] { sw.toString() },
            new Transfer[] { TextTransfer.getInstance() });
        cb.dispose();
        result[0] = true;
      }
    });

    if (!result[0]) {
      showUnsupportedDialog(_viewer.getControl().getShell());
    }
    return result[0];
  }

  private boolean paste() {
    final boolean[] result = new boolean[1];
    BusyIndicator.showWhile(_viewer.getControl().getDisplay(), new Runnable() {
      @Override
      public void run() {
        final Point[] cells = _viewer.doGetCellSelections();
        Arrays.sort(cells, _comparator);
        final int length = computeRegion(cells);
        if (length <= 0) {
          result[0] = length == 0;
          return;
        }

        String[][] data = readFromClipboard();
        if (data.length == 0) {
          result[0] = true;
          return;
        }

        final Rectangle dataRect = new Rectangle(0, 0, 0, data.length);
        for (String[] dataRow : data) {
          dataRect.width = Math.max(dataRect.width, dataRow.length);
        }

        final Point anchor = cells[0];
        final Rectangle targetRect;
        if (cells.length == 1) {
          // Paste top-left anchor
          targetRect = computeViewerAvailableCellBlock(new Rectangle(anchor.x,
              anchor.y, dataRect.width, dataRect.height));

        } else if (dataRect.width == 1 || dataRect.height == 1) {
          // Fill
          targetRect = computeViewerAvailableCellBlock(new Rectangle(anchor.x,
              anchor.y, length, cells.length / length));

          final String[][] newData = new String[targetRect.height][targetRect.width];
          if (dataRect.width == 1) {
            if (dataRect.height == 1) {
              for (String[] r : newData) {
                Arrays.fill(r, data[0][0]);
              }
            } else if (dataRect.height == targetRect.height) {
              for (int i = 0; i < targetRect.height; i++) {
                Arrays.fill(newData[i], data[i][0]);
              }
            }
          } else if (dataRect.width == targetRect.width) {
            for (int x = 0; x < targetRect.width; x++) {
              for (int y = 0; y < targetRect.height; y++) {
                newData[y][x] = data[0][x];
              }
            }
          }

          data = newData;
        } else {
          // Paste into region
          targetRect = computeViewerAvailableCellBlock(new Rectangle(anchor.x,
              anchor.y, length, cells.length / length));
        }

        final Point[] targetCells = computeCells(targetRect);
        if (!isEditable(targetCells)) {
          result[0] = false;
          return;
        }

        final String[] values = new String[targetRect.width * targetRect.height];
        Arrays.fill(values, "");
        copy(data, values, new Point(targetRect.width, targetRect.height));
        setValues(targetCells, values);
        _viewer.refresh();
        result[0] = true;
      }
    });

    if (!result[0]) {
      showUnsupportedDialog(_viewer.getControl().getShell());
    }
    return result[0];
  }

  private boolean cut() {
    if (!copy())
      return false;

    final boolean[] result = new boolean[1];
    BusyIndicator.showWhile(_viewer.getControl().getDisplay(), new Runnable() {
      @Override
      public void run() {
        final Point[] cells = _viewer.doGetCellSelections();
        if (cells == null || cells.length == 0) {
          result[0] = true;
          return;
        }

        final String[] values = new String[cells.length];
        Arrays.fill(values, "");
        setValues(cells, values);
        _viewer.refresh();
        result[0] = true;
      }
    });

    return result[0];
  }

  private String[][] readFromClipboard() {
    final Clipboard clipboard = new Clipboard(_viewer.getControl().getDisplay());
    final String text = (String) clipboard.getContents(TextTransfer
        .getInstance());
    if (text == null || text.isEmpty())
      return new String[0][0];

    final CSVReader reader = new CSVReader(new StringReader(text), '\t');
    try {
      final List<String[]> rows = reader.readAll();
      return rows.toArray(new String[][] {});
    } catch (IOException e) {
      throw new RuntimeException(e);
    } finally {
      try {
        reader.close();
      } catch (IOException e) {
        // ignore
      }
    }
  }

  private static int computeRegion(Point[] cells) {
    if (cells == null || cells.length == 0)
      return 0;

    final Point origin = cells[0];
    int length = 0;
    for (Point cell : cells) {
      if (cell.y != origin.y)
        break;

      length++;
    }

    if ((cells.length % length) != 0)
      return -1;

    for (int i = length; i < cells.length; i++) {
      final int cIdx = i % length;
      if (cIdx != 0 && cells[i].y != cells[i - 1].y)
        return -1;

      if (cells[cIdx].x != cells[i].x)
        return -1;
    }

    return length;
  }

  private Point[] computeCells(Rectangle region) {
    final Table t = _viewer.getControl();

    final Point pt = new Point(0, 0);
    final Point[] pts = new Point[region.width * region.height];
    int i = 0;
    for (int y = region.y; y < t.getItemCount() && pt.y < region.height; y++) {
      final TableItem row = t.getItem(y);
      if (!row.isVisible())
        continue;

      pt.x = 0;
      for (int x = region.x; x < t.getColumnCount() && pt.x < region.width; x++) {
        final TableColumn column = t.getColumn(x);
        if (!column.isVisible())
          continue;

        pts[i++] = new Point(x, y);
        pt.x++;
      }

      pt.y++;
    }

    return pts;
  }

  private static void copy(String[][] source, String[] dest, Point dimensions) {
    for (int i = 0; i < Math.min(dimensions.y, source.length); i++) {
      final String[] sourceRow = source[i];
      for (int j = 0; j < Math.min(dimensions.x, sourceRow.length); j++) {
        dest[i * dimensions.x + j] = sourceRow[j];
      }
    }
  }

  private Rectangle computeViewerAvailableCellBlock(Rectangle block) {
    final Table t = _viewer.getControl();

    final Rectangle available = new Rectangle(block.x, block.y, 0, 0);
    for (int x = available.x; x < t.getColumnCount(); x++) {
      if (available.width >= block.width)
        break;

      if (t.getColumn(x).isVisible()) {
        available.width++;
      } else if (available.x == x) {
        available.x++;
      }
    }

    for (int y = available.y; y < t.getItemCount(); y++) {
      if (available.height >= block.height)
        break;

      if (t.getItem(y).isVisible()) {
        available.height++;
      } else if (available.y == y) {
        available.y++;
      }
    }

    return available;
  }

  private static void showUnsupportedDialog(Shell shell) {
    final ResourceBundle bundle = ResourceBundle
        .getBundle("net.karlmartens.ui.locale.messages");
    final String title = bundle
        .getString("ViewerClipboardManager.Error.Unsupported.Title");
    final String message = bundle
        .getString("ViewerClipboardManager.Error.MultiSelection.Message");
    final IStatus status = new Status(IStatus.INFO, Activator.PLUGIN_ID,
        message);
    ErrorDialog.openError(shell, title, null, status);
  }

  private final Comparator<Point> _comparator = new Comparator<Point>() {
    @Override
    public int compare(Point o1, Point o2) {
      if (o1 == o2)
        return 0;

      if (o1 == null)
        return -1;

      if (o2 == null)
        return 1;

      int i = o1.y - o2.y;
      if (i != 0)
        return i;

      return o1.x - o2.x;
    }
  };
}