package net.karlmartens.net;

import net.karlmartens.platform.util.NullSafe;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColumnViewer;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.swt.widgets.Composite;

final class TestTextEditingSupport extends EditingSupport {

	private final ColumnViewer _viewer;
	private final int _index;

	public TestTextEditingSupport(ColumnViewer viewer, int index) {
		super(viewer);
		_viewer = viewer;
		_index = index;
	}

	@Override
	protected CellEditor getCellEditor(Object element) {
		return new TextCellEditor((Composite)_viewer.getControl());
	}

	@Override
	protected boolean canEdit(Object element) {
		return true;
	}

	@Override
	protected Object getValue(Object element) {
		final Object[] data = (Object[])element;
		return (String)data[_index];
	}

	@Override
	protected void setValue(Object element, Object value) {
		final Object[] data = (Object[])element;
		data[_index] = NullSafe.toString(value);
	}
}