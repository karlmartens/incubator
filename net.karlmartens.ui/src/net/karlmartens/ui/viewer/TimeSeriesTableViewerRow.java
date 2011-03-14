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

import net.karlmartens.ui.widget.TimeSeriesTable;
import net.karlmartens.ui.widget.TimeSeriesTableItem;

import org.eclipse.jface.viewers.TreePath;
import org.eclipse.jface.viewers.ViewerRow;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;

final class TimeSeriesTableViewerRow extends ViewerRow {

	private TimeSeriesTableItem _item;

	public TimeSeriesTableViewerRow(TimeSeriesTableItem item) {
		_item = item;
	}
	
	public void setItem(TimeSeriesTableItem item) {
		_item = item;
	}

	@Override
	public Rectangle getBounds(int columnIndex) {
		return _item.getBounds(columnIndex);
	}

	@Override
	public Rectangle getBounds() {
		return _item.getBounds();
	}

	@Override
	public TimeSeriesTableItem getItem() {
		return _item;
	}

	@Override
	public int getColumnCount() {
		return _item.getParent().getColumnCount() + _item.getParent().getPeriodCount();
	}

	@Override
	public Image getImage(int columnIndex) {
		return _item.getImage(columnIndex);
	}

	@Override
	public void setImage(int columnIndex, Image image) {
		_item.setImage(columnIndex, image);
	}

	@Override
	public String getText(int columnIndex) {
		return _item.getText(columnIndex);
	}

	@Override
	public void setText(int columnIndex, String text) {
		_item.setText(columnIndex, text);
	}

	@Override
	public Color getBackground(int columnIndex) {
		return _item.getBackground(columnIndex);
	}

	@Override
	public void setBackground(int columnIndex, Color color) {
		_item.setBackground(columnIndex, color);
	}

	@Override
	public Color getForeground(int columnIndex) {
		return _item.getForeground(columnIndex);
	}

	@Override
	public void setForeground(int columnIndex, Color color) {
		_item.setForeground(columnIndex, color);
	}

	@Override
	public Font getFont(int columnIndex) {
		return _item.getFont(columnIndex);
	}

	@Override
	public void setFont(int columnIndex, Font font) {
		_item.setFont(columnIndex, font);
	}

	@Override
	public TimeSeriesTable getControl() {
		return _item.getParent();
	}

	@Override
	public ViewerRow getNeighbor(int direction, boolean sameLevel) {
		final TimeSeriesTableItem item;
		if (ViewerRow.ABOVE == direction) {
			 item = getNeighbor(-1);
		} else if (ViewerRow.BELOW == direction) {
			item = getNeighbor(1);
		} else {
			throw new IllegalArgumentException();
		}
		
		if (item == null)
			return null;
		
		return new TimeSeriesTableViewerRow(item);
	}

	private TimeSeriesTableItem getNeighbor(int delta) {
		final TimeSeriesTable table = _item.getParent();
		final int index = table.indexOf(_item) + delta;
		if (index < 0 || index >= table.getItemCount())
			return null;
		
		return table.getItem(index);
	}

	@Override
	public TreePath getTreePath() {
		return new TreePath(new Object[] {_item.getData()});
	}

	@Override
	public Object clone() {
		return new TimeSeriesTableViewerRow(_item);
	}

	@Override
	public Object getElement() {
		return _item.getData();
	}
	
	@Override
	protected boolean isColumnVisible(int columnIndex) {
		return true;
	}

	@Override
	protected boolean scrollCellIntoView(int columnIndex) {
		final TimeSeriesTable parent = _item.getParent();
		parent.showItem(_item);
		if (parent.getPeriodCount() > 0) {
			parent.showColumn(columnIndex);
		}
		
		return true;
	}
}
