package net.karlmartens.ui.widget;

import net.karlmartens.ui.widget.TimeSeriesTableModel.Row;

import org.eclipse.swt.widgets.Item;

public class TimeSeriesTableItem extends Item {

	private TimeSeriesTable _parent;
	private Row _row;

	public TimeSeriesTableItem(TimeSeriesTable parent, int style) {
		this(parent, style, parent.getItemCount());
	}
	
	public TimeSeriesTableItem(TimeSeriesTable parent, int style,  int rowIndex) {
		super(parent, style);
		_parent = parent;
		parent.createItem(this, rowIndex);
	}

	void register(Row row) {
		_row = row;
	}

	void clear() {
		super.setText("");
		super.setImage(null);
	}

	void release() {
		clear();
		_parent = null;
		_row = null;
	}

}
