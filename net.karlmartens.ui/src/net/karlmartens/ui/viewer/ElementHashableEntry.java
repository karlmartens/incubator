package net.karlmartens.ui.viewer;

import org.eclipse.jface.viewers.IElementComparer;

final class ElementHashableEntry {

	private final Object _element;
	private final IElementComparer _comparer;

	ElementHashableEntry(Object element, IElementComparer comparer) {
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
