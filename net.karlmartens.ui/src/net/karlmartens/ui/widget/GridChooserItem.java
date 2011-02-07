package net.karlmartens.ui.widget;

import org.eclipse.swt.graphics.Rectangle;

public final class GridChooserItem extends AbstractTableItem {

	private GridChooser _parent;
	private int _selectionOrder = -1;

	public GridChooserItem(GridChooser parent, int style) {
		this(parent, style, parent.getItemCount());
	}
	
	public GridChooserItem(GridChooser parent, int style,  int rowIndex) {
		super(parent, style);
		_parent = parent;
		parent.createItem(this, rowIndex);
	}

	public GridChooser getParent() {
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
	
	public boolean setSelected(boolean selected) {
		checkWidget();
		if (selected && _selectionOrder >= 0) 
			return false;
		
		if (!selected && _selectionOrder < 0) 
			return false;
		
		if (!setSelectionOrder(selected ? _parent.getSelectionCount() : -1, true))
			return false;
		
		_parent.redraw();
		return true;
	}

	public boolean isSelected() {
		return _selectionOrder >= 0;
	}

	public int getSelectionOrder() {
		return _selectionOrder;
	}
	
	@Override
	protected int doGetColumnCount() {
		return _parent.getColumnCount();
	}
	
	boolean setSelectionOrder(int order, boolean allowDeselect) {
		final int minIndex = allowDeselect ? -1 : 0;
		final int maxIndex = _parent.getSelectionCount() - (_selectionOrder < 0 ? 0 : 1);
		final int targetOrder = Math.max(minIndex, Math.min(maxIndex, order)); 
		if (targetOrder == _selectionOrder)
			return false;
		
		final GridChooserItem[] items = _parent.getSelection();
		final int lower; 
		final int upper; 
		final int correction;
		if (targetOrder < 0) {
			lower = _selectionOrder + 1;
			upper = items.length - 1;
			correction = -1;
		} else if (_selectionOrder < 0) {
			lower = targetOrder;
			upper = items.length - 1;
			correction = 1;
		} else if (_selectionOrder < targetOrder){
			lower = _selectionOrder + 1;
			upper = Math.min(targetOrder, items.length - 1);
			correction = -1;
		} else {
			lower = targetOrder;
			upper = Math.max(0, _selectionOrder - 1);
			correction = 1;
		}
		for(int i=lower; i<=upper; i++) {
			items[i]._selectionOrder += correction;
		}

		_selectionOrder = targetOrder;
		_parent.redraw();
		return true;
	}
	
	@Override
	void clear() {
		super.clear();
		_selectionOrder = -1;
	}
	
	void release() {
		super.release();
		_parent = null;
	}
}
