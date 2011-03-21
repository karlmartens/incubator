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

import org.eclipse.jface.viewers.ColumnViewerEditorActivationEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.TraverseEvent;
import org.eclipse.swt.events.TraverseListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Text;

public final class TextCellEditor extends
		org.eclipse.jface.viewers.TextCellEditor {

	private boolean _selectAll = true;

	public TextCellEditor(Composite parent) {
		super(parent);
	}

	public TextCellEditor(Composite parent, int style) {
		super(parent, style);
	}

	@Override
	public void activate(ColumnViewerEditorActivationEvent activationEvent) {
		super.activate(activationEvent);

		_selectAll = true;
		if (activationEvent.eventType == ColumnViewerEditorActivationEvent.KEY_PRESSED) {
			switch (activationEvent.character) {
			case ' ':
				break;

			default:
				final String value = new String(
						new char[] { activationEvent.character });
				getControl().setText(value);
				_selectAll = false;
			}
		}
	}

	@Override
	protected Control createControl(final Composite parent) {
		final Control control = super.createControl(parent);
		control.addTraverseListener(new TraverseListener() {
			@Override
			public void keyTraversed(TraverseEvent e) {
				switch (e.keyCode) {
				case SWT.ARROW_LEFT:
				case SWT.ARROW_RIGHT:
				case SWT.ARROW_UP:
				case SWT.ARROW_DOWN:
				case SWT.TAB:
					fireApplyEditorValue();
					deactivate();
					final Event event = new Event();
					event.character = e.character;
					event.keyCode = e.keyCode;
					event.stateMask = e.stateMask;
					parent.notifyListeners(SWT.KeyDown, event);
				}
			}
		});

		return control;
	}

	@Override
	protected void doSetFocus() {
		super.doSetFocus();

		if (!_selectAll) {
			final Text text = getControl();
			text.setSelection(text.getText().length());
		}
	}

	@Override
	public Text getControl() {
		return (Text) super.getControl();
	}

	@Override
	protected boolean dependsOnExternalFocusListener() {
		return getClass() != TextCellEditor.class;
	}
}
