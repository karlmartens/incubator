package com._3esi.platform.desktop.ui.control;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Item;

public final class TimeSeriesColumn extends Item {

	private final TimeSeriesTable _parent;

	public TimeSeriesColumn(TimeSeriesTable parent) {
		this(parent, parent.getColumnCount());
	}
	
	public TimeSeriesColumn(TimeSeriesTable parent, int index) {
		super(parent, SWT.NONE);
		_parent = parent;
		_parent.createItem(this, index);
	}
	
	@Override
	public void setText(String string) {
		super.setText(string);
		_parent.refreshColumns();
	}

}
