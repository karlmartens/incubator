package net.karlmartens.ui.viewer;

import org.eclipse.jface.viewers.ColumnLabelProvider;

class TestColumnLabelProvider extends ColumnLabelProvider {
	
	private final int _index;

	TestColumnLabelProvider(int index) {
		_index = index;
	}

	@Override
	public String getText(Object element) {
		final Object o = ((Object[]) element)[_index];
		if (o==null)
			return "";
		
		return o.toString();
	}
}