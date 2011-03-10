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
package net.karlmartens.ui.widget;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.widgets.Item;
import org.eclipse.swt.widgets.TypedListener;

public final class TimeSeriesTableColumn extends Item {

	private final TimeSeriesTable _parent;
	private int _width = 0;
	private boolean _moveable = true;
	private boolean _visible = true;

	/**
	 * <p><dl>
     * <dt><b>Styles:</b></dt>
     * <dd>CHECK</dd>
     * </dl></p>
     * 
	 */
	public TimeSeriesTableColumn(TimeSeriesTable table, int style) {
		this(table, style, table.getColumnCount());
	}
	
	public TimeSeriesTableColumn(TimeSeriesTable table, int style, int index) {
		super(table, style);
		table.createItem(this, index);
		_parent = table;
	}
	
	TimeSeriesTableColumn(TimeSeriesTable table) {
		super(table, SWT.NONE);
		_parent = table;
	}

	public void setWidth(int width) {
		_width = width;
		_parent.redraw();
	}

	public int getWidth() {
		return _width;
	}
	
	public void setMoveable(boolean moveable) {
		_moveable = moveable;
	}
	
	public boolean isMoveable() {
		return _moveable;
	}

	public void setVisible(boolean visible) {
		_visible  = visible;
		_parent.redraw();
	}
	
	public boolean isVisible() {
		return _visible;
	}
	
	TimeSeriesTable getParent() {
		return _parent;
	}
	
	public void addControlListener(ControlListener listener) {
		final TypedListener tListener = new TypedListener(listener);
		addListener(SWT.Resize, tListener);
		addListener(SWT.Move, tListener);
	}

	public void removeControlListener(ControlListener listener) {
		final TypedListener tListener = new TypedListener(listener);
		removeListener(SWT.Resize, tListener);
		removeListener(SWT.Move, tListener);
	}
}
