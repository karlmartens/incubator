package net.karlmartens.net;

import org.eclipse.jface.viewers.ColumnLabelProvider;

class TestColumnLabelProvider extends ColumnLabelProvider {
	
	private final int _index;

	TestColumnLabelProvider(int index) {
		_index = index;
	}

	@Override
	public String getText(Object element) {
		return ((String[]) element)[_index];
	}
}