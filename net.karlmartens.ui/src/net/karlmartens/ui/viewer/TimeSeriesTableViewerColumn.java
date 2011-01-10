package net.karlmartens.ui.viewer;

import net.karlmartens.ui.widget.TimeSeriesTable;
import net.karlmartens.ui.widget.TimeSeriesTableColumn;

import org.eclipse.jface.viewers.ViewerColumn;

public final class TimeSeriesTableViewerColumn extends ViewerColumn {

	private final TimeSeriesTableColumn _column;

	public TimeSeriesTableViewerColumn(TimeSeriesTableViewer viewer, int style) {
		this(viewer, style, -1);
	}

	public TimeSeriesTableViewerColumn(TimeSeriesTableViewer viewer, int style, int index) {
		this(viewer, createColumn(viewer.getControl(), style, index));
	}
	
	private TimeSeriesTableViewerColumn(TimeSeriesTableViewer viewer, TimeSeriesTableColumn column) {
		super(viewer, column);
		_column = column;
	}
	
	public TimeSeriesTableColumn getColumn() {
		return _column;
	}
	
	private static TimeSeriesTableColumn createColumn(TimeSeriesTable table, int style, int index) {
		if (index >= 0)
			return new TimeSeriesTableColumn(table, style, index);
		
		return new TimeSeriesTableColumn(table, style);
	}
}
