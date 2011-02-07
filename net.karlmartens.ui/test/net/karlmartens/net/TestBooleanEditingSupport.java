package net.karlmartens.net;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.CheckboxCellEditor;
import org.eclipse.jface.viewers.ColumnViewer;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.swt.widgets.Composite;

final class TestBooleanEditingSupport extends EditingSupport {

	private final ColumnViewer _viewer;
	private final int _index;

	public TestBooleanEditingSupport(ColumnViewer viewer, int index) {
		super(viewer);
		_viewer = viewer;
		_index = index;
	}

	@Override
	protected CellEditor getCellEditor(Object element) {
		return new CheckboxCellEditor((Composite)_viewer.getControl());
	}

	@Override
	protected boolean canEdit(Object element) {
		return true;
	}

	@Override
	protected Object getValue(Object element) {
		final String[] data = (String[])element;
		return Boolean.valueOf(data[_index]);
	}

	@Override
	protected void setValue(Object element, Object value) {
		final String[] data = (String[])element;
		data[_index] = value.toString();
	}
}