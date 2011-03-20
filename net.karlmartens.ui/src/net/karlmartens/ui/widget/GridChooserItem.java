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
package net.karlmartens.ui.widget;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Rectangle;

public final class GridChooserItem extends AbstractTableItem {

	private GridChooser _parent;
	private int _selectionOrder = -1;

	public GridChooserItem(GridChooser parent, int style) {
		this(parent, style, parent.getItemCount());
	}

	public GridChooserItem(GridChooser parent, int style, int rowIndex) {
		super(parent, style);
		_parent = parent;
		parent.createItem(this, rowIndex);
	}

	@Override
	public Color getBackground() {
		checkWidget();
		if (_background != null)
			return _background;

		if (isSelected())
			return _parent.getSelectedComposite().getBackground();

		return _parent.getAvailableComposite().getBackground();
	}

	public GridChooser getParent() {
		return _parent;
	}

	public Rectangle getBounds(int index) {
		checkWidget();
		return _parent.getBounds(this, index);
	}

	public Rectangle getBounds() {
		checkWidget();
		return _parent.getBounds(this);
	}

	public Rectangle getImageBounds(int index) {
		checkWidget();
		return _parent.getImageBounds(this, index);
	}

	public boolean setSelected(boolean selected) {
		checkWidget();
		if (selected && _selectionOrder >= 0)
			return false;

		if (!selected && _selectionOrder < 0)
			return false;

		if (!setSelectionOrder(selected ? _parent.getSelectionCount() : -1,
				true))
			return false;

		_parent.redraw();
		return true;
	}

	public boolean isSelected() {
		return _selectionOrder >= 0;
	}

	public int getSelectionOrder() {
		return _selectionOrder;
	}

	@Override
	protected int doGetColumnCount() {
		return _parent.getColumnCount();
	}

	boolean setSelectionOrder(int order, boolean allowDeselect) {
		final int minIndex = allowDeselect ? -1 : 0;
		final int maxIndex = _parent.getSelectionCount()
				- (_selectionOrder < 0 ? 0 : 1);
		final int targetOrder = Math.max(minIndex, Math.min(maxIndex, order));
		if (targetOrder == _selectionOrder)
			return false;

		final GridChooserItem[] items = _parent.getSelection();
		final int lower;
		final int upper;
		final int correction;
		if (targetOrder < 0) {
			lower = _selectionOrder + 1;
			upper = items.length - 1;
			correction = -1;
		} else if (_selectionOrder < 0) {
			lower = targetOrder;
			upper = items.length - 1;
			correction = 1;
		} else if (_selectionOrder < targetOrder) {
			lower = _selectionOrder + 1;
			upper = Math.min(targetOrder, items.length - 1);
			correction = -1;
		} else {
			lower = targetOrder;
			upper = Math.max(0, _selectionOrder - 1);
			correction = 1;
		}
		for (int i = lower; i <= upper; i++) {
			items[i]._selectionOrder += correction;
		}

		_selectionOrder = targetOrder;
		_parent.redraw();
		return true;
	}

	@Override
	void clear() {
		super.clear();
		_selectionOrder = -1;
	}

	void release() {
		super.release();
		_parent = null;
	}
}
