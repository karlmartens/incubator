package net.karlmartens.ui.widget;

import net.karlmartens.ui.widget.TimeSeriesTableModel.Column;

import org.eclipse.swt.widgets.Item;

public final class TimeSeriesTableColumn extends Item {

	private Column _column;

	public TimeSeriesTableColumn(TimeSeriesTable parent, int style) {
		this(parent, style, parent.getAttributeColumnCount());
	}

	public TimeSeriesTableColumn(TimeSeriesTable parent, int style, int index) {
		super(parent, style, index);
		parent.createItem(this, index);
	}
	
	public void setText(String string) {
		checkWidget();
		_column.setName(string);
	}
	
	public String getText() {
		checkWidget();
		return _column.getName();
	}

	void register(Column column) {
		_column = column;		
	}

}
