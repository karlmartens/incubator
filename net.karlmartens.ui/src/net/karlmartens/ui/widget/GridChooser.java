package net.karlmartens.ui.widget;

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ITableColorProvider;
import org.eclipse.jface.viewers.ITableFontProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;

public final class GridChooser extends Composite {

	private final TableViewer _available;
	private final TableViewer _selected;
	
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
		_available = new TableViewer(this, style);
		_available.setContentProvider(ArrayContentProvider.getInstance());
		_available.setLabelProvider(new TableLabelProviderImpl());
		_available.addFilter(new ItemSelectionFilter(false));
		
		_selected = new TableViewer(this, style);
		_selected.setContentProvider(ArrayContentProvider.getInstance());
		_selected.setLabelProvider(new TableLabelProviderImpl());
		_selected.addFilter(new ItemSelectionFilter(true));
		
		final FormData availableFormData = new FormData();
		availableFormData.top = new FormAttachment(0, 100, 10);
		availableFormData.bottom = new FormAttachment(100, 100, -10);
		availableFormData.left = new FormAttachment(0, 100, 10);
		availableFormData.right = new FormAttachment(50, 100, -10);
		
		final FormData selectedFormData = new FormData();
		selectedFormData.top = new FormAttachment(0, 100, 10);
		selectedFormData.bottom = new FormAttachment(100, 100, -10);
		selectedFormData.left = new FormAttachment(_available.getControl(), 10);
		selectedFormData.right = new FormAttachment(100, 100, -10);
		
		_available.getControl().setLayoutData(availableFormData);
		_selected.getControl().setLayoutData(selectedFormData);
		
		addDisposeListener(_disposeListener);
	}

	public void setHeaderVisible(boolean show) {
		checkWidget();
		_available.getTable().setHeaderVisible(show);
		_selected.getTable().setHeaderVisible(show);
	}
	
	public int getColumnCount() {
		checkWidget();
		return _columnCount;
	}

	public int getItemCount() {
		checkWidget();
		return _itemCount;
	}
	
	public GridChooserItem[] getItems() {
		checkWidget();
		final GridChooserItem[] items = new GridChooserItem[_itemCount];
		System.arraycopy(_items, 0, items, 0, _itemCount);
		return items; 
	}
	
	@Override
	public void redraw() {
		_available.refresh();
		_selected.refresh();
		super.redraw();
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
				new TableColumn(_available.getTable(), item.getStyle(), index), //
				new TableColumn(_selected.getTable(), item.getStyle(), index) //
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
		
		System.arraycopy(_items, index, _items, index+1, _itemCount++-index);
		_items[index] = item;

		_available.setInput(getItems());
		_selected.setInput(getItems());
	}
	
	private DisposeListener _disposeListener = new DisposeListener() {
		@Override
		public void widgetDisposed(DisposeEvent e) {
			for (GridChooserItem item : _items) {
				item.release();
			}
			_items = new GridChooserItem[0];
			_itemCount = 0;
			
			for (GridChooserColumn column : _columns) {
				column.release();
			}
			_columns = new GridChooserColumn[0];
			_columnCount = 0;
		}
	};
	
	private static class TableLabelProviderImpl extends LabelProvider implements ITableLabelProvider, ITableColorProvider, ITableFontProvider {

		@Override
		public Image getColumnImage(Object element, int columnIndex) {
			if (element instanceof GridChooserItem) {
				return ((GridChooserItem)element).getImage(columnIndex);
			}
			
			return null;
		}

		@Override
		public String getColumnText(Object element, int columnIndex) {
			if (element instanceof GridChooserItem) {
				return ((GridChooserItem)element).getText(columnIndex);
			}

			return "";
		}

		@Override
		public Color getForeground(Object element, int columnIndex) {
			if (element instanceof GridChooserItem) {
				return ((GridChooserItem)element).getForeground(columnIndex);
			}
			
			return null;
		}

		@Override
		public Color getBackground(Object element, int columnIndex) {
			if (element instanceof GridChooserItem) {
				return ((GridChooserItem)element).getBackground(columnIndex);
			}
			
			return null;
		}

		@Override
		public Font getFont(Object element, int columnIndex) {
			if (element instanceof GridChooserItem) {
				return ((GridChooserItem)element).getFont(columnIndex);
			}
			
			return null;
		}
		
	}

	private static class ItemSelectionFilter extends ViewerFilter {
		
		private final boolean _selected;
		
		public ItemSelectionFilter(boolean selected) {
			_selected = selected;
		}

		@Override
		public boolean select(Viewer viewer, Object parentElement,
				Object element) {
			if (element instanceof GridChooserItem) {
				return ((GridChooserItem)element).isSelected() == _selected;
			}
			
			return false;
		}
		
	}
}
