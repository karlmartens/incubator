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
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.ResourceBundle;

import net.karlmartens.ui.Activator;
import net.karlmartens.ui.widget.ClipboardStrategy;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Shell;

import au.com.bytecode.opencsv.CSVReader;
import au.com.bytecode.opencsv.CSVWriter;

final class ViewerClipboardManager extends CellSelectionModifier {

  private final TimeSeriesTableViewer _viewer;
  private final int _operations;
  private final ClipboardStrategy _clipboardStrategy;

  ViewerClipboardManager(TimeSeriesTableViewer viewer, int operations) {
    super(viewer);
    _viewer = viewer;
    _operations = operations;
    _clipboardStrategy = new ClipboardStrategy();
    hookControl(viewer.getControl());
  }

  private void hookControl(Control control) {
    control.addKeyListener(_listener);
    control.addDisposeListener(_listener);
  }

  private void handleDispose() {
    final Control control = _viewer.getControl();
    control.removeDisposeListener(_listener);
    control.removeKeyListener(_listener);
  }

  private boolean copy() {
    final boolean[] result = new boolean[1];
    BusyIndicator.showWhile(_viewer.getControl().getDisplay(), new Runnable() {
      @Override
      public void run() {
        final Point[] cells = _viewer.getControl().getCellSelections();
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

        final Clipboard cb = new Clipboard(_viewer.getControl().getDisplay());
        cb.setContents(new String[] { sw.toString() }, new Transfer[] { TextTransfer.getInstance() });
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
        final Point[] cells = _viewer.getControl().getCellSelections();
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
          final int width = Math.min(dataRect.width, _viewer.doGetColumnCount() - anchor.x);
          final int height = Math.min(dataRect.height, _viewer.doGetItemCount() - anchor.y);
          targetRect = new Rectangle(anchor.x, anchor.y, width, height);
        } else if (dataRect.width == 1 && dataRect.height == 1) {
          // Fill
          final int width = Math.min(length, _viewer.doGetColumnCount() - anchor.x);
          final int height = Math.min(cells.length / length, _viewer.doGetItemCount() - anchor.y);
          targetRect = new Rectangle(anchor.x, anchor.y, width, height);

          final String[][] newData = new String[height][width];
          for (String[] r : newData) {
            Arrays.fill(r, data[0][0]);
          }
          data = newData;
        } else {
          // Paste into region
          final int width = Math.min(length, _viewer.doGetColumnCount() - anchor.x);
          final int height = Math.min(cells.length / length, _viewer.doGetItemCount() - anchor.y);
          targetRect = new Rectangle(anchor.x, anchor.y, width, height);
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
        final Point[] cells = _viewer.getControl().getCellSelections();
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
    final String text = (String) clipboard.getContents(TextTransfer.getInstance());
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

  private static Point[] computeCells(Rectangle region) {
    final Point[] pts = new Point[region.width * region.height];
    int i = 0;
    for (int y = region.y; y < region.y + region.height; y++) {
      for (int x = region.x; x < region.x + region.width; x++) {
        pts[i++] = new Point(x, y);
      }
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

  private static void showUnsupportedDialog(Shell shell) {
    final ResourceBundle bundle = ResourceBundle.getBundle("net.karlmartens.ui.locale.messages");
    final String title = bundle.getString("ViewerClipboardManager.Error.Unsupported.Title");
    final String message = bundle.getString("ViewerClipboardManager.Error.MultiSelection.Message");
    final IStatus status = new Status(IStatus.INFO, Activator.PLUGIN_ID, message);
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

  private final Listener _listener = new Listener();

  private class Listener extends KeyAdapter implements DisposeListener {

    @Override
    public void widgetDisposed(DisposeEvent e) {
      handleDispose();
    }

    @Override
    public void keyPressed(KeyEvent e) {
      final Event event = new Event();
      event.stateMask = e.stateMask;
      event.keyCode = e.keyCode;
      if (!_clipboardStrategy.isClipboardEvent(event))
        return;

      switch (_clipboardStrategy.getOperation(event)) {
        case OPERATION_COPY:
          if ((_operations & OPERATION_COPY) > 0)
            copy();
          break;

        case OPERATION_PASTE:
          if ((_operations & OPERATION_COPY) > 0)
            paste();
          break;

        case OPERATION_CUT:
          if ((_operations & OPERATION_COPY) > 0)
            cut();
          break;
      }
    }
  }
}