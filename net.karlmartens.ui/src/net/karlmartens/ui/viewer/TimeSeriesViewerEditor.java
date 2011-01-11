package net.karlmartens.ui.viewer;

import org.eclipse.jface.viewers.CellEditor.LayoutData;
import org.eclipse.jface.viewers.ColumnViewer;
import org.eclipse.jface.viewers.ColumnViewerEditor;
import org.eclipse.jface.viewers.ColumnViewerEditorActivationEvent;
import org.eclipse.jface.viewers.ColumnViewerEditorActivationStrategy;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Item;

public class TimeSeriesViewerEditor extends ColumnViewerEditor {

	TimeSeriesViewerEditor(ColumnViewer viewer,
			ColumnViewerEditorActivationStrategy editorActivationStrategy,
			int feature) {
		super(viewer, editorActivationStrategy, feature);
	}

	@Override
	protected void setEditor(Control w, Item item, int fColumnNumber) {
		System.out.println("setEditor");
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void setLayoutData(LayoutData layoutData) {
		System.out.println("setLayoutData");
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void updateFocusCell(ViewerCell focusCell,
			ColumnViewerEditorActivationEvent event) {
		System.out.println("setLayoutData");
		// TODO Auto-generated method stub
		
	}

}
