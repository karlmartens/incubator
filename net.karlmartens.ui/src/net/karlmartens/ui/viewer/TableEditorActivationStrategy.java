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

import net.karlmartens.ui.widget.CellNavigationStrategy;
import net.karlmartens.ui.widget.TableColumn;
import net.karlmartens.ui.widget.TableItem;

import org.eclipse.jface.viewers.ColumnViewerEditorActivationEvent;
import org.eclipse.jface.viewers.ColumnViewerEditorActivationStrategy;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Event;

import de.kupzog.ktable.renderers.CheckableCellRenderer;

final class TableEditorActivationStrategy extends ColumnViewerEditorActivationStrategy {

  private final CellNavigationStrategy _navigationStrategy = new CellNavigationStrategy();
  private final TableViewer _viewer;

  public TableEditorActivationStrategy(TableViewer viewer) {
    super(viewer);
    _viewer = viewer;
  }

  @Override
  protected boolean isEditorActivationEvent(ColumnViewerEditorActivationEvent event) {
    if (event.eventType != ColumnViewerEditorActivationEvent.MOUSE_CLICK_SELECTION
        && event.eventType != ColumnViewerEditorActivationEvent.MOUSE_DOUBLE_CLICK_SELECTION
        && event.eventType != ColumnViewerEditorActivationEvent.KEY_PRESSED && event.eventType != ColumnViewerEditorActivationEvent.PROGRAMMATIC)
      return false;

    if (event.eventType == ColumnViewerEditorActivationEvent.MOUSE_CLICK_SELECTION)
      return isCheckActivationEvent(event);

    if (event.eventType == ColumnViewerEditorActivationEvent.KEY_PRESSED) {
      final Event e = new Event();
      e.stateMask = event.stateMask;
      e.keyCode = event.keyCode;
      if (isNonPrintable(e) || _navigationStrategy.isNavigationEvent(e) || _navigationStrategy.isExpandEvent(e) || _navigationStrategy.isSelectAllEvent(e)) {
        return false;
      }
    }

    return true;
  }

  private boolean isNonPrintable(Event e) {
    switch (e.keyCode) {
      case SWT.SHIFT:
      case SWT.COMMAND:
      case SWT.CONTROL:
      case SWT.ALT:
      case SWT.CAPS_LOCK:
      case SWT.NUM_LOCK:
      case SWT.SCROLL_LOCK:
      case SWT.ESC:
        return true;
    }

    return false;
  }

  private boolean isCheckActivationEvent(ColumnViewerEditorActivationEvent event) {
    if (event.eventType != ColumnViewerEditorActivationEvent.MOUSE_CLICK_SELECTION)
      return false;

    final Object source = event.getSource();
    if (!(source instanceof ViewerCell))
      return false;

    final ViewerCell cell = (ViewerCell) source;
    final int columnIdx = cell.getColumnIndex();
    final TableColumn column = _viewer.getControl().getColumn(columnIdx);
    final int style = column.getStyle();
    if ((style & SWT.CHECK) == 0)
      return false;

    final MouseEvent mEvent = (MouseEvent) event.sourceEvent;
    final Point eventPt = new Point(mEvent.x, mEvent.y);
    final TableItem item = _viewer.getItemAt(eventPt);

    final Rectangle cellBounds = item.getBounds(columnIdx);
    final Rectangle targetBounds = CheckableCellRenderer.IMAGE_CHECKED.getBounds();

    final Rectangle activationBounds = new Rectangle(cellBounds.x, cellBounds.y, targetBounds.width, targetBounds.height);
    activationBounds.y += Math.max((cellBounds.height - targetBounds.height) / 2, 0);

    if ((style & SWT.RIGHT) > 0) {
      activationBounds.x += Math.max(cellBounds.width - targetBounds.width, 0);
    } else if ((style & SWT.LEFT) > 0) {
      // nothing to do
    } else {
      // default is centered
      activationBounds.x += Math.max((cellBounds.width - targetBounds.width) / 2, 0);
    }

    return activationBounds.contains(eventPt);
  }
}
