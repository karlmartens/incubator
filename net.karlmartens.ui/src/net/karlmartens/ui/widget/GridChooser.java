package net.karlmartens.ui.widget;

import static net.karlmartens.ui.Images.ARROW_DOWN;
import static net.karlmartens.ui.Images.ARROW_LEFT;
import static net.karlmartens.ui.Images.ARROW_RIGHT;
import static net.karlmartens.ui.Images.ARROW_UP;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import net.karlmartens.ui.viewer.ItemViewerComparator;

import org.eclipse.jface.util.LocalSelectionTransfer;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ITableColorProvider;
import org.eclipse.jface.viewers.ITableFontProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DragSourceEvent;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.TableDragSourceEffect;
import org.eclipse.swt.dnd.TableDropTargetEffect;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;

public final class GridChooser extends Composite {

	private final TableViewer _available;
	private final TableViewer _selected;
	private final Button _left;
	private final Button _right;
	private final Button _up;
	private final Button _down;
	
	private Image[] _images;
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
		
		_images = new Image[] {
				ARROW_LEFT.createImage(),
				ARROW_RIGHT.createImage(),
				ARROW_UP.createImage(),
				ARROW_DOWN.createImage()
		};
		
		setLayout(new FormLayout());
		
		final int style = SWT.FULL_SELECTION | SWT.MULTI | SWT.BORDER;
		_available = new TableViewer(this, style);
		_available.setContentProvider(ArrayContentProvider.getInstance());
		_available.setLabelProvider(new TableLabelProviderImpl());
		_available.addFilter(new ItemSelectionFilter(false));
		_available.addSelectionChangedListener(_selectionChangedListener);
		_available.setComparator(new ItemViewerComparator());
		new DragSourceListenerImpl(_available);
		new SelectedDropTargetListener(_available, false);
		
		final Composite centerPart = new Composite(this, SWT.NONE);
		centerPart.setLayout(new RowLayout(SWT.VERTICAL));
		
		_left = createButton(centerPart, _images[0], _changeSelectionListener);
		_right = createButton(centerPart, _images[1], _changeSelectionListener);
		_up = createButton(centerPart, _images[2], _changeSelectionListener);
		_down = createButton(centerPart, _images[3], _changeSelectionListener);
		
		_selected = new TableViewer(this, style);
		_selected.setContentProvider(ArrayContentProvider.getInstance());
		_selected.setLabelProvider(new TableLabelProviderImpl());
		_selected.addFilter(new ItemSelectionFilter(true));
		_selected.addSelectionChangedListener(_selectionChangedListener);
		_selected.setComparator(new SelectionOrderViewerComparator());
		new DragSourceListenerImpl(_selected);
		new SelectedDropTargetListener(_selected, true);
		
		final FormData availableFormData = new FormData();
		availableFormData.top = new FormAttachment(0, 100, 10);
		availableFormData.bottom = new FormAttachment(100, 100, -10);
		availableFormData.left = new FormAttachment(0, 100, 10);
		availableFormData.right = new FormAttachment(centerPart, -10);
		
		final FormData centerFormData = new FormData();
		centerFormData.top = new FormAttachment(_available.getControl(), 0, SWT.CENTER);
		centerFormData.left = new FormAttachment(50, 100, -10);
		
		final FormData selectedFormData = new FormData();
		selectedFormData.top = new FormAttachment(_available.getControl(), 0, SWT.TOP);
		selectedFormData.bottom = new FormAttachment(_available.getControl(), 0, SWT.BOTTOM);
		selectedFormData.left = new FormAttachment(centerPart, 10);
		selectedFormData.right = new FormAttachment(100, 100, -10);
		
		_available.getControl().setLayoutData(availableFormData);
		centerPart.setLayoutData(centerFormData);
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
	
	public GridChooserItem[] getSelection() {
		final GridChooserItem[] selected = new GridChooserItem[_itemCount];
		int i=0;
		for (int j=0; j<_itemCount;j++) {
			final GridChooserItem item = _items[j];
			if (!item.isSelected())
				continue;
			selected[i++] = item;
		}
		
		final GridChooserItem[] result = new GridChooserItem[i];
		System.arraycopy(selected, 0, result, 0, i);
		Arrays.sort(result, new GridChooserItemSelectionOrderComparator());
		return result;
	}
	
	public int getSelectionCount() {
		return getSelection().length;
	}
	
	@Override
	public void redraw() {
		_available.refresh();
		_selected.refresh();
		updateButtons();
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
	
	private Button createButton(Composite parent, Image image, SelectionListener listener) {
		final Button button = new Button(parent, SWT.PUSH);
		button.setImage(image);
		button.setEnabled(false);
		button.addSelectionListener(listener);
		return button;
	}
	
	private void updateButtons() {
		final Table selected = _selected.getTable();
		final int itemCount = selected.getItemCount();
		boolean firstSelected = false;
		boolean lastSelected = false;
		for (int idx : selected.getSelectionIndices()) {
			firstSelected |= idx == 0;
			lastSelected |= idx == (itemCount - 1);
		}
		
		final boolean hasSelection = !_selected.getSelection().isEmpty(); 
		_left.setEnabled(hasSelection);
		_up.setEnabled(hasSelection && !firstSelected);
		_down.setEnabled(hasSelection && !lastSelected);
		_right.setEnabled(!_available.getSelection().isEmpty());
	}

	private void updateSelection(TableViewer viewer, boolean selected) {
		final Table table = viewer.getTable();
		final int originalLastItemIndex = table.getItemCount() - 1;
		final int[] originalIndicies = table.getSelectionIndices();
		
		@SuppressWarnings("unchecked")
		final List<GridChooserItem> selection = ((StructuredSelection)viewer.getSelection()).toList();
		for (GridChooserItem item : selection) {
			item.setSelected(selected);
		}
		
		final int newItemCount = table.getItemCount();
		if (newItemCount <= 0)
			return;

		int i=0;
		for (int j=0; j<originalIndicies.length; j++) {
			final int index = originalIndicies[j];
			if (index == originalLastItemIndex) {
				originalIndicies[i++] = newItemCount - 1;
			} else if (index < newItemCount) {
				i++;
			}
		}
		
		final int[] newIndicies = new int[i];
		System.arraycopy(originalIndicies, 0, newIndicies, 0, i);
		table.setSelection(newIndicies);
		redraw();
	}
	
	private void updateSelection(TableViewer viewer, int movement) {
		@SuppressWarnings("unchecked")
		final List<GridChooserItem> selection = ((StructuredSelection)viewer.getSelection()).toList();
		if (movement > 0) {
			Collections.reverse(selection);
		}
		
		for (GridChooserItem item : selection) {
			item.setSelectionOrder(item.getSelectionOrder() + movement, false);
		}
	}
	
	private DisposeListener _disposeListener = new DisposeListener() {
		@Override
		public void widgetDisposed(DisposeEvent e) {
			for (GridChooserItem item : _items) {
				if (item == null)
					continue;
				
				item.release();
			}
			_items = new GridChooserItem[0];
			_itemCount = 0;
			
			for (GridChooserColumn column : _columns) {
				if (column == null)
					continue;
				
				column.release();
			}
			_columns = new GridChooserColumn[0];
			_columnCount = 0;
			
			for (Image image : _images) {
				if (image == null)
					continue;
				
				image.dispose();
			}
			_images = new Image[0];
		}
	};
	
	private ISelectionChangedListener _selectionChangedListener = new ISelectionChangedListener() {
		@Override
		public void selectionChanged(SelectionChangedEvent event) {
			updateButtons();
		}
	}; 
	
	private SelectionListener _changeSelectionListener = new SelectionListener() {
		@Override
		public void widgetSelected(SelectionEvent e) {
			handle(e);
		}
		
		@Override
		public void widgetDefaultSelected(SelectionEvent e) {
			handle(e);
		}
		
		private void handle(SelectionEvent e) {
			if (_left.equals(e.widget)) {
				updateSelection(_selected, false);
				return;
			} 
				
			if (_right.equals(e.widget)) {
				updateSelection(_available, true);
				return;
			}
			
			if (_up.equals(e.widget)) {
				updateSelection(_selected, -1);
				return;
			}
			
			if (_down.equals(e.widget)) {
				updateSelection(_selected, 1);
				return;
			}
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

	private static class SelectionOrderViewerComparator extends ViewerComparator {
		
		private final Comparator<GridChooserItem> _comparator = new GridChooserItemSelectionOrderComparator();
		
		@Override
		public int compare(Viewer viewer, Object e1, Object e2) {
			if (e1 instanceof GridChooserItem && e2 instanceof GridChooserItem) {
				return _comparator.compare((GridChooserItem)e1, (GridChooserItem)e2);
			}
			
			return super.compare(viewer, e1, e2);
		}
	}
	
	private static class DragSourceListenerImpl extends TableDragSourceEffect {
		private final TableViewer _viewer;

		public DragSourceListenerImpl(TableViewer viewer) {
			super(viewer.getTable());
			_viewer = viewer;
			_viewer.addDragSupport(DND.DROP_MOVE, new Transfer[] {LocalSelectionTransfer.getTransfer()}, this);
		}
		
		@Override
		public void dragStart(DragSourceEvent event) {
			if (_viewer.getSelection().isEmpty())
				event.doit = false;

			super.dragStart(event);
		}

		@Override
		public void dragSetData(DragSourceEvent event) {
			final LocalSelectionTransfer transfer = LocalSelectionTransfer.getTransfer();
			if (transfer.isSupportedType(event.dataType)) {
				transfer.setSelection(_viewer.getSelection());
			}
		}
	}
	
	private static class SelectedDropTargetListener extends TableDropTargetEffect {
		private final boolean _selected;
		
		public SelectedDropTargetListener(TableViewer viewer, boolean selected) {
			super(viewer.getTable());
			viewer.addDropSupport(DND.DROP_MOVE, new Transfer[] {LocalSelectionTransfer.getTransfer()}, this);
			_selected = selected;
		}
		
		@Override
		public void dragEnter(DropTargetEvent event) {
			checkOperation(event);			
			super.dragEnter(event);
		}
		
		@Override
		public void dragOperationChanged(DropTargetEvent event) {
			checkOperation(event);
			super.dragOperationChanged(event);
		}
		
		private void checkOperation(DropTargetEvent event) {
			if (event.detail == DND.DROP_DEFAULT) {
				if ((event.operations & DND.DROP_MOVE) != 0) {
					event.detail = DND.DROP_MOVE;
				} else {
					event.detail = DND.DROP_NONE;
				}
			}
		}

		@Override
		public void drop(DropTargetEvent event) {
			final LocalSelectionTransfer transfer = LocalSelectionTransfer.getTransfer();
			if (transfer.isSupportedType(event.currentDataType)) {
				@SuppressWarnings("unchecked")
				final List<GridChooserItem> selected = new ArrayList<GridChooserItem>(((StructuredSelection)transfer.getSelection()).toList());
				if (!_selected || event.item == null) {
					for (GridChooserItem item : selected) {
						item.setSelected(_selected);
					}
					return;
				}
				
				final GridChooserItem targetItem = (GridChooserItem)((TableItem)event.item).getData();
				final int targetIndex = targetItem.getSelectionOrder();
				final int minIndex = minimumSelectionOrder(selected);
				if (minIndex >= targetIndex) {
					Collections.reverse(selected);
				}
				for (GridChooserItem item : selected) {
					item.setSelectionOrder(targetIndex, false);
				}
			}
		}

		private int minimumSelectionOrder(List<GridChooserItem> items) {
			int idx = items.size();
			for (GridChooserItem item : items) {
				if (item.getSelectionOrder() < idx) {
					idx = item.getSelectionOrder();
				}
			}
			return idx;
		}
	}
}
