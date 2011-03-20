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
package net.karlmartens.ui.viewer;

import org.eclipse.jface.viewers.IElementComparer;

final class ElementHashtableEntry {

	private final Object _element;
	private final IElementComparer _comparer;

	ElementHashtableEntry(Object element, IElementComparer comparer) {
		if (element == null)
			throw new NullPointerException();

		_element = element;
		_comparer = comparer;
	}

	public Object getElement() {
		return _element;
	}

	@Override
	public int hashCode() {
		if (_comparer != null)
			return _comparer.hashCode(_element);

		return _element.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (_comparer != null)
			return _comparer.equals(obj);

		return _element.equals(obj);
	}
}
