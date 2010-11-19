package net.karlmartens.ui.widget;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;

public final class GridChooser extends Composite {

	private final Table _available;
	private final Table _selected;
	
	private int _columnCount;
	private int _itemCount;
	private GridChooserColumn[] _columns;
	private GridChooserItem[] _items;

	public GridChooser(Composite parent) {
		super(parent, SWT.NONE);
		
		_columnCount = 0;
		_columns = new GridChooserColumn[0];
		
		_itemCount = 0;
		_items = new GridChooserItem[0];
		
		setLayout(new FormLayout());
		
		final int style = SWT.FULL_SELECTION | SWT.MULTI | SWT.BORDER;
		_available = new Table(this, style);
		_selected = new Table(this, style);
	
		final FormData availableFormData = new FormData();
		availableFormData.top = new FormAttachment(0, 100, 10);
		availableFormData.bottom = new FormAttachment(100, 100, -10);
		availableFormData.left = new FormAttachment(0, 100, 10);
		availableFormData.right = new FormAttachment(50, 100, -10);
		
		final FormData selectedFormData = new FormData();
		selectedFormData.top = new FormAttachment(0, 100, 10);
		selectedFormData.bottom = new FormAttachment(100, 100, -10);
		selectedFormData.left = new FormAttachment(_available, 10);
		selectedFormData.right = new FormAttachment(100, 100, -10);
		
		_available.setLayoutData(availableFormData);
		_selected.setLayoutData(selectedFormData);
	}

	public void setHeaderVisible(boolean show) {
		_available.setHeaderVisible(show);
		_selected.setHeaderVisible(show);
	}
	
	public int getColumnCount() {
		return _columnCount;
	}

	public int getItemCount() {
		return _itemCount;
	}

	void createItem(GridChooserColumn item, int index) {
		if (index < 0 || index > _columnCount)
			SWT.error(SWT.ERROR_INVALID_RANGE);
		
		if (index == _columns.length) {
			final GridChooserColumn[] newColumns = new GridChooserColumn[_columns.length + 4];
			System.arraycopy(_columns, 0, newColumns, 0, _columns.length);
			_columns = newColumns;
		}
		
		final TableColumn[] columns = new TableColumn[] { //
				new TableColumn(_available, item.getStyle(), index), //
				new TableColumn(_selected, item.getStyle(), index) //
		};
		item.registerWidgets(columns);
		
		System.arraycopy(_columns, index, _columns, index+1, _columnCount++-index);
		_columns[index] = item;
	}

	void createItem(GridChooserItem item, int index) {
		if (index < 0 || index > _itemCount)
			SWT.error(SWT.ERROR_INVALID_RANGE);
		
		if (index == _items.length) {
			final GridChooserItem[] newItems = new GridChooserItem[_items.length + 4];
			System.arraycopy(_items, 0, newItems, 0, _items.length);
			_items = newItems;
		}
		
		item.registerUnselectedWidget(new TableItem(_available, item.getStyle(), index));
		item.registerSelectedWidget(new TableItem(_selected, item.getStyle(), index));
		
		System.arraycopy(_items, index, _items, index+1, _itemCount++-index);
		_items[index] = item;
	}
	
}
