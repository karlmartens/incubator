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

import net.karlmartens.ui.widget.CellNavigationStrategy;
import net.karlmartens.ui.widget.ClipboardStrategy;

import org.eclipse.jface.viewers.ColumnViewerEditorActivationEvent;
import org.eclipse.jface.viewers.ColumnViewerEditorActivationStrategy;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Event;

final class TimeSeriesEditorActivationStrategy extends
		ColumnViewerEditorActivationStrategy {
	
	private final CellNavigationStrategy _navigationStrategy = new CellNavigationStrategy();
	private final ClipboardStrategy _clipboardStrategy = new ClipboardStrategy();

	public TimeSeriesEditorActivationStrategy(TimeSeriesTableViewer viewer) {
		super(viewer);
	}

	@Override
	protected boolean isEditorActivationEvent(
			ColumnViewerEditorActivationEvent event) {
		if (event.eventType != ColumnViewerEditorActivationEvent.MOUSE_DOUBLE_CLICK_SELECTION
				&& event.eventType != ColumnViewerEditorActivationEvent.KEY_PRESSED
				&& event.eventType != ColumnViewerEditorActivationEvent.PROGRAMMATIC)
			return false;

		if (event.eventType == ColumnViewerEditorActivationEvent.KEY_PRESSED) {
			final Event e = new Event();
			e.keyCode = event.keyCode;
			if (isNonPrintable(e) ||
					_clipboardStrategy.isClipboardEvent(e) ||
					_navigationStrategy.isNavigationEvent(e) ||
					_navigationStrategy.isExpandEvent(e)) {
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
