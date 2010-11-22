package net.karlmartens.ui.viewer;

import net.karlmartens.ui.widget.GridChooser;
import net.karlmartens.ui.widget.GridChooserColumn;

import org.eclipse.jface.viewers.ViewerColumn;

public final class GridChooserViewerColumn extends ViewerColumn {
	
	private final GridChooserColumn _column;

	public GridChooserViewerColumn(GridChooserViewer viewer, int style) {
		this(viewer, style, -1);
	}

	public GridChooserViewerColumn(GridChooserViewer viewer, int style, int index) {
		this(viewer, createColumn(viewer.getGridChooser(), style, index));
	}
	
	private GridChooserViewerColumn(GridChooserViewer viewer, GridChooserColumn column) {
		super(viewer, column);
		_column = column;
	}
	
	public GridChooserColumn getColumn() {
		return _column;
	}
	
	private static GridChooserColumn createColumn(GridChooser chooser, int style, int index) {
		if (index >= 0)
			return new GridChooserColumn(chooser, style, index);
		
		return new GridChooserColumn(chooser, style);
	}
}
