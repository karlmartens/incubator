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

import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Item;
import org.eclipse.swt.widgets.TableColumn;

public final class GridChooserColumn extends Item {

	private TableColumn[] _columns;

	public GridChooserColumn(GridChooser parent, int style) {
		this(parent, style, parent.getColumnCount());
	}
	
	public GridChooserColumn(GridChooser parent, int style, int index) {
		super(parent, style);
		parent.createItem(this, index);
	}
	
	@Override
	public void setText(String text) {
		for (TableColumn widget : _columns) {
			widget.setText(text);
		}
	}
	
	@Override
	public void setImage(Image image) {
		for (TableColumn widget : _columns) {
			widget.setImage(image);
		}
	}
	
	public void setWidth(int width) {
		for (TableColumn widget : _columns) {
			widget.setWidth(width);
		}
	}
	
	public void setMoveable(boolean moveable) {
		for (TableColumn widget : _columns) {
			widget.setMoveable(moveable);
		}
	}
	
	@Override
	public String getText() {
		return _columns[0].getText();
	}
	
	@Override
	public Image getImage() {
		return _columns[0].getImage();
	}

	public int getWidth() {
		return _columns[0].getWidth() ;
	}
	
	public boolean isMoveable() {
		return _columns[0].getMoveable();
	}

	void registerWidgets(TableColumn[] widgets) {
		_columns = widgets;
	}

	public void release() {
		_columns = null;
	}

	public void addControlListener(ControlListener listener) {
		for (TableColumn column : _columns) {
			column.addControlListener(listener);
		}
	}
	
	public void removeControlListener(ControlListener listener) {
		for (TableColumn column : _columns) {
			column.removeControlListener(listener);
		}
	}
}
