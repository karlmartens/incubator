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
import net.karlmartens.ui.widget.ClipboardStrategy;

import org.eclipse.jface.viewers.ColumnViewerEditorActivationEvent;
import org.eclipse.jface.viewers.ColumnViewerEditorActivationStrategy;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Event;

final class TimeSeriesEditorActivationStrategy extends ColumnViewerEditorActivationStrategy {

  private final CellNavigationStrategy _navigationStrategy = new CellNavigationStrategy();
  private final ClipboardStrategy _clipboardStrategy = new ClipboardStrategy();

  public TimeSeriesEditorActivationStrategy(TimeSeriesTableViewer viewer) {
    super(viewer);
  }

  @Override
  protected boolean isEditorActivationEvent(ColumnViewerEditorActivationEvent event) {
    if (event.eventType != ColumnViewerEditorActivationEvent.MOUSE_DOUBLE_CLICK_SELECTION && event.eventType != ColumnViewerEditorActivationEvent.KEY_PRESSED
        && event.eventType != ColumnViewerEditorActivationEvent.PROGRAMMATIC)
      return false;

    if (event.eventType == ColumnViewerEditorActivationEvent.KEY_PRESSED) {
      final Event e = new Event();
      e.stateMask = event.stateMask;
      e.keyCode = event.keyCode;
      if (isNonPrintable(e) || _clipboardStrategy.isClipboardEvent(e) || _navigationStrategy.isNavigationEvent(e) || _navigationStrategy.isExpandEvent(e)) {
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
}
