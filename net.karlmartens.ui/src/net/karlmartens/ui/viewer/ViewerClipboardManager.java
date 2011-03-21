/**
 *  net.karlmartens.ui, is a library of UI widgets
 *  Copyright (C) 2011
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

import static net.karlmartens.ui.widget.ClipboardStrategy.OPERATION_COPY;
import static net.karlmartens.ui.widget.ClipboardStrategy.OPERATION_CUT;
import static net.karlmartens.ui.widget.ClipboardStrategy.OPERATION_PASTE;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.BitSet;
import java.util.List;

import net.karlmartens.ui.widget.ClipboardStrategy;
import net.karlmartens.ui.widget.TimeSeriesTableColumn;
import net.karlmartens.ui.widget.TimeSeriesTableItem;

import org.eclipse.jface.util.Policy;
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

import au.com.bytecode.opencsv.CSVReader;
import au.com.bytecode.opencsv.CSVWriter;

public final class ViewerClipboardManager {

  private final TimeSeriesTableViewer _viewer;
  private final int _operations;
  private final ClipboardStrategy _clipboardStrategy;
  private final EditingSupportProxy _editingSupportCache;

  public ViewerClipboardManager(TimeSeriesTableViewer viewer, int operations) {
    _viewer = viewer;
    _operations = operations;
    _editingSupportCache = new EditingSupportProxy(viewer);
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

  private void handleCopy() {
    if ((_operations & OPERATION_COPY) == 0)
      return;
    final Rectangle region = computeRegion(_viewer.getControl().getCellSelections());
    if (!isRegionValid(region))
      return;

    doCopy(region);
  }

  private void handlePaste() {
    if ((_operations & OPERATION_COPY) == 0)
      return;

    final Rectangle region = computeRegion(_viewer.getControl().getCellSelections());
    if (!isRegionValid(region))
      return;

    String[][] data = readFromClipboard();
    if (data.length == 0)
      return;

    final Rectangle dataRect = new Rectangle(0, 0, 0, data.length);
    for (String[] dataRow : data) {
      dataRect.width = Math.max(dataRect.width, dataRow.length);
    }

    final Rectangle targetRect;
    if (region.width == 1 && region.height == 1) {
      // Paste top-left anchor
      final int width = Math.min(dataRect.width, _viewer.doGetColumnCount() - region.x);
      final int height = Math.min(dataRect.height, _viewer.doGetItemCount() - region.y);
      targetRect = new Rectangle(region.x, region.y, width, height);
    } else if (dataRect.width == 1 && dataRect.height == 1) {
      // Fill
      final int width = Math.min(region.width, _viewer.doGetColumnCount() - region.x);
      final int height = Math.min(region.height, _viewer.doGetItemCount() - region.y);
      targetRect = new Rectangle(region.x, region.y, width, height);

      final String[][] newData = new String[region.height][region.width];
      for (String[] r : newData) {
        Arrays.fill(r, data[0][0]);
      }
      data = newData;
    } else {
      // Paste into region
      final int width = Math.min(region.width, _viewer.doGetColumnCount() - region.x);
      final int height = Math.min(region.height, _viewer.doGetItemCount() - region.y);
      targetRect = new Rectangle(region.x, region.y, width, height);
    }

    if (!isRegionEditable(targetRect))
      return;

    final String[] values = new String[targetRect.width * targetRect.height];
    Arrays.fill(values, "");
    copy(data, values, new Point(targetRect.width, targetRect.height));
    doSet(targetRect, values);
    _viewer.refresh();
  }

  private void handleCut() {
    if ((_operations & OPERATION_COPY) == 0)
      return;

    final Rectangle region = computeRegion(_viewer.getControl().getCellSelections());
    if (!isRegionValid(region))
      return;

    if (!isRegionEditable(region))
      return;

    doCopy(region);

    final String[] values = new String[region.width * region.height];
    Arrays.fill(values, "");
    doSet(region, values);
    _viewer.refresh();
  }

  private void copy(String[][] source, String[] dest, Point dimensions) {
    for (int i = 0; i < Math.min(dimensions.y, source.length); i++) {
      final String[] sourceRow = source[i];
      for (int j = 0; j < Math.min(dimensions.x, sourceRow.length); j++) {
        dest[i * dimensions.x + j] = sourceRow[j];
      }
    }
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

  private void doCopy(Rectangle region) {
    final StringWriter sw = new StringWriter();
    final CSVWriter writer = new CSVWriter(sw, '\t');
    for (int y = region.y; y < (region.y + region.height); y++) {
      final String[] row = new String[region.width];
      for (int i = 0; i < row.length; i++) {
        final int x = region.x + i;
        final TimeSeriesTableItem item = _viewer.doGetItem(y);
        final int columnCount = _viewer.getControl().getColumnCount();
        final String text;
        if (x < columnCount) {
          text = item.getText(x);
        } else {
          text = Double.toString(item.getValue(x - columnCount));
        }
        row[i] = text;
      }

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
  }

  private void doSet(Rectangle region, String[] values) {
    final int firstTimeValueIndex = _viewer.getControl().getColumnCount();
    final TimeSeriesEditingSupport timeValueEditingSupport = _viewer.getEditingSupport();

    for (int y = region.y; y < (region.y + region.height); y++) {
      final TimeSeriesTableItem item = _viewer.doGetItem(y);
      for (int x = region.x; x < (region.x + region.width); x++) {
        _editingSupportCache._base = getViewerColumn(x).doGetEditingSupport();
        final int index = (y - region.y) * region.width + (x - region.x);
        if (x < firstTimeValueIndex) {
          _editingSupportCache.setValue(item.getData(), values[index]);
        } else {
          final int vIndex = x - firstTimeValueIndex;
          try {
            final Double value = Double.valueOf(values[index]);
            timeValueEditingSupport.setValue(item.getData(), vIndex, value == null ? 0.0 : value.doubleValue());
          } catch (NumberFormatException e) {
            timeValueEditingSupport.setValue(item.getData(), vIndex, 0.0);
          }
        }
      }
    }
  }

  private boolean isRegionValid(Rectangle region) {
    if (region.x < 0 || region.y < 0 || region.width <= 0 || region.height <= 0)
      return false;

    return true;
  }

  private boolean isRegionEditable(Rectangle region) {
    for (int y = region.y; y < (region.y + region.height); y++) {
      final TimeSeriesTableItem item = _viewer.doGetItem(y);
      for (int x = region.x; x < (region.x + region.width); x++) {
        _editingSupportCache._base = getViewerColumn(x).doGetEditingSupport();
        if (!_editingSupportCache.canEdit(item.getData()))
          return false;
      }
    }

    return true;
  }

  private TimeSeriesTableViewerColumn getViewerColumn(int index) {
    final TimeSeriesTableColumn column = (TimeSeriesTableColumn) _viewer.doGetColumn(index);
    return (TimeSeriesTableViewerColumn) column.getData(Policy.JFACE + ".columnViewer");
  }

  private Rectangle computeRegion(Point[] cells) {
    if (cells.length <= 0)
      return new Rectangle(0, 0, 0, 0);

    final Rectangle r = new Rectangle(cells[0].x, cells[0].y, 1, 1);
    for (Point cell : cells) {
      final int dx = cell.x - r.x;
      if (dx < 0) {
        r.x += dx;
        r.width -= dx;
      }

      if (dx >= r.width) {
        r.width = dx + 1;
      }

      final int dy = cell.y - r.y;
      if (dy < 0) {
        r.y += dy;
        r.height -= dy;
      }

      if (dy >= r.height) {
        r.height = dy + 1;
      }
    }

    final BitSet set = new BitSet(r.width * r.height);
    for (Point cell : cells) {
      set.set((cell.y - r.y) * r.width + (cell.x - r.x));
    }
    if (set.cardinality() != (r.width * r.height))
      return new Rectangle(0, 0, 0, 0);

    return r;
  }

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
          handleCopy();
          break;

        case OPERATION_PASTE:
          handlePaste();
          break;

        case OPERATION_CUT:
          handleCut();
          break;
      }
    }
  }
}