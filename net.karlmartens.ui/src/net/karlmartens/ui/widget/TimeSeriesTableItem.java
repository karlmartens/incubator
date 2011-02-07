package net.karlmartens.ui.widget;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Rectangle;

public final class TimeSeriesTableItem extends AbstractTableItem {
	
	private TimeSeriesTable _parent;
	private double[] _values;

	public TimeSeriesTableItem(TimeSeriesTable parent) {
		this(parent, parent.getItemCount());
	}
	
	public TimeSeriesTableItem(TimeSeriesTable parent, int index) {
		super(parent, SWT.NONE);
		_parent = parent;
		parent.createItem(this, index);
	}

	public TimeSeriesTable getParent() {
		checkWidget();
		return _parent;
	}
	
	public Rectangle getBounds(int index) {
		checkWidget();
		return _parent.getBounds(this, index);
	}
	
	public Rectangle getBounds() {
		checkWidget();
		return _parent.getBounds(this);
	}

	public Rectangle getImageBounds(int index) {
		checkWidget();
		return _parent.getImageBounds(this, index);
	}

	public double getValue(int index) {
		checkWidget();
		if (_values == null || index < 0 || index >= _values.length)
			return 0.0;
		
		return _values[index];
	}
	
	public void setValue(double[] values) {
		checkWidget();
		if (values == null)
			SWT.error(SWT.ERROR_NULL_ARGUMENT);
		
		for (int i=0; i<values.length; i++) {
			setValue(i, values[i]);
		}		
	}

	public void setValue(int index, double value) {
		checkWidget();

		final int count = Math.max(1, _parent.getPeriodCount());
		if (index < 0 || index >= count)
			return;
		
		if (_values == null) {
			_values = new double[count];
		}

		if (_values[index] == value)
			return;
		
		_values[index] = value;
		_parent.redraw();
	}
	
	@Override
	protected int doGetColumnCount() {
		return _parent.getColumnCount();
	}
	
	@Override
	void clear() {
		super.clear();
		_values = null;
	}
	
	@Override
	void release() {
		super.release();
		_parent = null;
	}
}
