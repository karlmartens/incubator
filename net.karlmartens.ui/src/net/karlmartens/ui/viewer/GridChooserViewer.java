package net.karlmartens.ui.viewer;

import java.util.HashSet;
import java.util.Set;

import net.karlmartens.ui.widget.GridChooser;
import net.karlmartens.ui.widget.GridChooserItem;

import org.eclipse.jface.viewers.AbstractTableViewer;
import org.eclipse.jface.viewers.ColumnViewerEditor;
import org.eclipse.jface.viewers.ColumnViewerEditorActivationStrategy;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.ViewerRow;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Item;
import org.eclipse.swt.widgets.Widget;

public final class GridChooserViewer extends AbstractTableViewer {

	private final GridChooser _chooser;
	private GridChooserViewerRow _cachedRow;
	
	public GridChooserViewer(Composite parent) {
		this(new GridChooser(parent));
	}
	
	public GridChooserViewer(GridChooser chooser) {
		_chooser = chooser;
		hookControl(chooser);
	}

	@Override
	public Control getControl() {
		return _chooser;
	}
	
	public GridChooser getGridChooser() {
		return _chooser;
	}
	
	public void refresh(final Object element, final boolean updateLabels, boolean reveal) {
		if (checkBusy())
			return;
		
		if (isCellEditorActive()) {
			cancelEditing();
		}
		
		preservingSelection(new Runnable() {
			@Override
			public void run() {
				internalRefresh(element, updateLabels);
			}
		});
		
		if (reveal) {
			reveal(element);
		}
	}
	
	public void refresh(boolean updateLabels, boolean reveal) {
		refresh(getRoot(), updateLabels, reveal);
	}
	
	public void remove(Object[] elements) {
		if (checkBusy())
			return;
		
		if (elements.length == 0)
			return;
		
		final Set<ElementHashtableEntry> elementsToRemove = new HashSet<ElementHashtableEntry>();
		for (Object element : elements) {
			elementsToRemove.add(new ElementHashtableEntry(element, getComparer()));
		}

		boolean deselectedItems = false;
		for (GridChooserItem item : _chooser.getSelection()) {
			final Object o = item.getData();
			if (o == null)
				continue;
			
			if (elementsToRemove.contains(new ElementHashtableEntry(o, getComparer()))) {
				deselectedItems = true;
				break;
			}
		}
		
		super.remove(elements);
		
		if (deselectedItems) {
			final ISelection selection = getSelection();
			updateSelection(selection);
			firePostSelectionChanged(new SelectionChangedEvent(this, selection));
		}
	}
	
	@Override
	protected ViewerRow internalCreateNewRowPart(int style, int rowIndex) {
		final GridChooserItem item;
		if (rowIndex >= 0) {
			item = new GridChooserItem(_chooser, style, rowIndex);
		} else {
			item = new GridChooserItem(_chooser, style);
		}

		return getViewerRowFromItem(item);
	}

	@Override
	protected int doIndexOf(Item item) {
		return _chooser.indexOf((GridChooserItem)item);
	}

	@Override
	protected int doGetItemCount() {
		return _chooser.getItemCount();
	}

	@Override
	protected void doSetItemCount(int count) {
		_chooser.setItemCount(count);
	}

	@Override
	protected Item[] doGetItems() {
		return _chooser.getItems();
	}

	@Override
	protected Widget doGetColumn(int index) {
		return _chooser.getColumn(index);
	}

	@Override
	protected Item doGetItem(int index) {
		return _chooser.getItem(index);
	}

	@Override
	protected Item[] doGetSelection() {
		return _chooser.getSelection();
	}

	@Override
	protected int[] doGetSelectionIndices() {
		return _chooser.getSelectionIndices();
	}

	@Override
	protected void doClearAll() {
		_chooser.clearAll();
	}

	@Override
	protected void doResetItem(Item item) {
		final GridChooserItem chooserItem = (GridChooserItem)item;
		final int columnCount = Math.max(1, _chooser.getColumnCount());
		for (int i=0; i<columnCount; i++) {
			chooserItem.setText(i, "");
			if (chooserItem.getImage(i) != null) {
				chooserItem.setImage(i, null);
			}
		}
	}

	@Override
	protected void doRemove(int start, int end) {
		_chooser.remove(start, end);
	}

	@Override
	protected void doRemoveAll() {
		_chooser.removeAll();
	}

	@Override
	protected void doRemove(int[] indices) {
		_chooser.remove(indices);
	}

	@Override
	protected void doShowItem(Item item) {
		_chooser.showItem((GridChooserItem)item);
	}

	@Override
	protected void doDeselectAll() {
		_chooser.deselectAll();
	}

	@Override
	protected void doSetSelection(Item[] items) {
		final GridChooserItem[] chooserItems = new GridChooserItem[items.length];
		System.arraycopy(items, 0, chooserItems, 0, chooserItems.length);
		_chooser.setSelection(chooserItems);
	}

	@Override
	protected void doShowSelection() {
		_chooser.showSelection();
	}

	@Override
	protected void doSetSelection(int[] indices) {
		_chooser.setSelection(indices);
	}

	@Override
	protected void doClear(int index) {
		_chooser.clear(index);
	}

	@Override
	protected void doSelect(int[] indices) {
		_chooser.select(indices);
	}

	@Override
	protected ColumnViewerEditor createViewerEditor() {
		return new GridChooserViewerEditor(this, 
				new ColumnViewerEditorActivationStrategy(this), 
				ColumnViewerEditor.DEFAULT);
	}

	@Override
	protected ViewerRow getViewerRowFromItem(Widget item) {
		if (_cachedRow == null) {
			_cachedRow = new GridChooserViewerRow((GridChooserItem)item);
		} else {
			_cachedRow.setItem((GridChooserItem)item);
		}

		return _cachedRow;
	}

	@Override
	protected Item getItemAt(Point point) {
		final GridChooserItem[] selection = _chooser.getSelection();
		if (selection.length == 1) {
			final int columnCount = _chooser.getColumnCount();
			for (int i=0; i<columnCount; i++) {
				if (selection[0].getBounds(i).contains(point)) {
					return selection[0];
				}
			}
		}
		return _chooser.getItem(point);
	}

	@Override
	protected int doGetColumnCount() {
		return _chooser.getColumnCount();
	}
}
