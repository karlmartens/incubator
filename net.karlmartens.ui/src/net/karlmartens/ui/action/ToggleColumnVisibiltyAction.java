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
package net.karlmartens.ui.action;

import net.karlmartens.ui.widget.TimeSeriesTableColumn;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;

public class ToggleColumnVisibiltyAction extends Action implements IAction {

	private final TimeSeriesTableColumn _column;

	public ToggleColumnVisibiltyAction(TimeSeriesTableColumn column) {
		super(column.getText(), IAction.AS_CHECK_BOX);
		setChecked(column.isVisible());
		
		_column = column;
	}
	
	@Override
	public void run() {
		_column.setVisible(isChecked());
	}
}
