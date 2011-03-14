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
import org.eclipse.swt.widgets.Event;

public class ClipboardStrategy {

	public static final int OPERATION_NONE = 0;
	public static final int OPERATION_COPY = 1;
	public static final int OPERATION_CUT = 2;
	public static final int OPERATION_PASTE = 4;

	public boolean isClipboardEvent(Event e) {
		if ((e.stateMask & SWT.MOD1) == 0)
			return false;
		
		return 'c' == e.keyCode // 
			|| 'v' == e.keyCode //
			|| 'x' == e.keyCode;
	}

	public int getOperation(Event e) {
		if ((e.stateMask & SWT.MOD1) == 0)
			return OPERATION_NONE;

		switch(e.keyCode) {
			case 'c':
				return OPERATION_COPY;
				
			case 'v':
				return OPERATION_PASTE;
				
			case 'x':
				return OPERATION_CUT;
		}
		
		return OPERATION_NONE;
	}
	
}
