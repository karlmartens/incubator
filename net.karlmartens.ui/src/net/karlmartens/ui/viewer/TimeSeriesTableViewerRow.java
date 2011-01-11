package net.karlmartens.ui.viewer;

import net.karlmartens.ui.widget.TimeSeriesTable;
import net.karlmartens.ui.widget.TimeSeriesTableItem;

import org.eclipse.jface.viewers.TreePath;
import org.eclipse.jface.viewers.ViewerRow;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;

public class TimeSeriesTableViewerRow extends ViewerRow {

	private TimeSeriesTableItem _item;

	public TimeSeriesTableViewerRow(TimeSeriesTableItem item) {
		setItem(item);
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
		return _item.getParent().getColumnCount();
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
		final TimeSeriesTable table = _item.getParent();
		final int index = table.indexOf(_item);
		
		final TimeSeriesTableItem item;
		if (ViewerRow.ABOVE == direction) {
			if (index >= table.getItemCount() - 1) {
				return null;
			}

			item = table.getItem(index + 1);
		} else if (ViewerRow.BELOW == direction) {
			if (index <= 0) {
				return null;
			} 
			
			item = table.getItem(index + 1);
		} else {
			throw new IllegalArgumentException();
		}
		
		if (item == null)
			return null;
		
		return new TimeSeriesTableViewerRow(item);
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

	void setItem(TimeSeriesTableItem item) {
		_item = item;
	}

}
