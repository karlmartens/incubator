package net.karlmartens.ui.widget;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Frame;
import java.awt.Panel;
import java.util.Arrays;

import javax.swing.CellEditor;
import javax.swing.JRootPane;

import net.karlmartens.platform.text.LocalDateFormat;
import net.karlmartens.platform.util.Periodicity;
import net.karlmartens.platform.util.UiThreadUtil;
import net.karlmartens.platform.util.UiThreadUtil.Task;
import net.karlmartens.platform.util.UiThreadUtil.TaskWithResult;
import net.karlmartens.ui.widget.TimeSeriesTableModel.Column;
import net.karlmartens.ui.widget.TimeSeriesTableModel.Row;

import org.eclipse.swt.SWT;
import org.eclipse.swt.awt.SWT_AWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Composite;
import org.joda.time.LocalDate;

import com.jidesoft.filter.FilterFactoryManager;
import com.jidesoft.grid.BooleanCheckBoxCellEditor;
import com.jidesoft.grid.BooleanCheckBoxCellRenderer;
import com.jidesoft.grid.CellEditorFactory;
import com.jidesoft.grid.CellEditorManager;
import com.jidesoft.grid.CellRendererManager;
import com.jidesoft.grid.MultiTableModel;
import com.jidesoft.grid.TableModelWrapperUtils;
import com.jidesoft.plaf.LookAndFeelFactory;


public final class TimeSeriesTable extends Composite {
	
	private final TimeSeriesTableEventAdapter _tableListener;
	private final TableScrollPane _scrollPane;
	private final SparklineScrollBar _navigationBar;

	private final TimeSeriesTableModel _model = new TimeSeriesTableModel();
	
	private Periodicity _periodicity = Periodicity.MONTHLY;
	private LocalDate _start = new LocalDate().withDayOfMonth(1);
	private int _numberOfPeriods = 60;
	private int _viewportIndex = 0;
	private int _viewportSize = 12;
	private LocalDateFormat _format;
	
	private TimeSeriesTableColumn[] _attributeColumns = new TimeSeriesTableColumn[4];
	private int _attributeColumnCount = 0;
	private TimeSeriesTableItem[] _items = new TimeSeriesTableItem[4];
	private int _itemCount = 0;

	public TimeSeriesTable(Composite parent) {
		super(parent, SWT.NONE);
		setLayout(new FormLayout());
	
		initSwingEnvironment();
		
		final Composite tableComposite = new Composite(this, SWT.EMBEDDED | SWT.NO_BACKGROUND);
		tableComposite.setLayout(new FillLayout());
		final Frame frame = SWT_AWT.new_Frame(tableComposite);
		
		_scrollPane = UiThreadUtil.performSyncOnSwing(new TaskWithResult<TableScrollPane>() {
			@Override
			public TableScrollPane run() {
                initializeJideFramework();
                final Panel panel = new Panel(new BorderLayout());
                final JRootPane root = new JRootPane();
                panel.add(root);
                final Container contentPane = root.getContentPane();
                final TableScrollPane scrollPane = new TableScrollPane(_model);
                contentPane.add(scrollPane, BorderLayout.CENTER);
                frame.add(panel);
                return scrollPane;
			}
		});
		
		_navigationBar = new SparklineScrollBar(this);
		
		final FormData tableData = new FormData();
		tableData.top = new FormAttachment(0, 100, 0);
		tableData.bottom = new FormAttachment(_navigationBar, -5, SWT.TOP);
		tableData.left = new FormAttachment(_navigationBar, 0, SWT.LEFT);
		tableData.right = new FormAttachment(_navigationBar, 0, SWT.RIGHT);
		
		final FormData navigationData = new FormData();
		navigationData.bottom = new FormAttachment(100, 100, 0);
		navigationData.left = new FormAttachment(0, 100, 0);
		navigationData.right = new FormAttachment(100, 100, 0);
		navigationData.height = 40;
		
		tableComposite.setLayoutData(tableData);
		_navigationBar.setLayoutData(navigationData);
		
		_tableListener = new TimeSeriesTableEventAdapter(this, _scrollPane);
	}
	
	public void setPeriodicity(Periodicity periodicity) {
		checkWidget();
		if (periodicity == null)
			SWT.error(SWT.ERROR_NULL_ARGUMENT);
		
		_periodicity = periodicity;
		// TODO cause widgets to refresh
	}
	
	public Periodicity getPeriodicity() {
		checkWidget();
		return _periodicity;
	}
	
	public void setStart(LocalDate date) {
		checkWidget();
		if (date == null)
			SWT.error(SWT.ERROR_NULL_ARGUMENT);
		
		_start = date;
		// TODO cause widgets to refresh
	}
	
	public LocalDate getStart() {
		checkWidget();
		return _start;
	}
	
	public void setNumberOfPeriods(int size) {
		checkWidget();
		if (size <= 0)
			SWT.error(SWT.ERROR_INVALID_RANGE);
		
		_numberOfPeriods = size;
		// TODO cause widgets to refresh
	}
	
	public int getNumberOfPeriods() {
		checkWidget();
		return _numberOfPeriods;
	}
	
	public void setViewportIndex(int index) {
		checkWidget();
		if (index < 0 || index > _numberOfPeriods - 1)
			SWT.error(SWT.ERROR_INVALID_RANGE);
		
		_viewportIndex = index;
		// cause widgets to refresh
	}
	
	public int getViewportIndex() {
		checkWidget();
		return _viewportIndex;
	}
	
	public void setViewportSize(int size) {
		checkWidget();
		if (size <= 0 || _numberOfPeriods < _viewportIndex + size)
			SWT.error(SWT.ERROR_INVALID_RANGE);
		
		_viewportSize = size;
		// cause widgets to refresh
	}
	
	public int getViewportSize() {
		checkWidget();
		return _viewportSize;
	}
	
	public void setDateFormat(LocalDateFormat format) {
		checkWidget();
		_format = format;
	}
	
	public LocalDateFormat getDateFormat() {
		checkWidget();
		return _format;
	}

	public int getAttributeColumnCount() {
		checkWidget();
		return _attributeColumnCount;
	}

	public int getColumnCount() {
		checkWidget();
		return _model.getColumnCount();
	}

	public TimeSeriesTableColumn getColumn(int index) {
		checkWidget();
		// TODO Auto-generated method stub
		if (index < 0 || index >= _attributeColumnCount)
			SWT.error(SWT.ERROR_INVALID_RANGE);
		
		return _attributeColumns[index];
	}

	public int getItemCount() {
		checkWidget();
		return _itemCount;
	}

	public void setItemCount(int count) {
		checkWidget();
		final int c = Math.max(0, count);
		if (c == _itemCount)
			return;
		
		if (c > _itemCount) {
			for (int i=_itemCount; i<c; i++) {
				new TimeSeriesTableItem(this, SWT.NONE, i);
			}
			return;
		}
		
		for (int i=c; i<_itemCount; i++) {
			final TimeSeriesTableItem item = _items[i];
			if (item != null && !item.isDisposed())
				item.release();
			_items[i] = null;
		}
		
		final int length = Math.max(4, (c + 3) / 4 * 4);
		final TimeSeriesTableItem[] newItems = new TimeSeriesTableItem[length];
		System.arraycopy(_items, 0, newItems, 0, c);
		_items = newItems;
		_itemCount = c;
	}

	public TimeSeriesTableItem[] getItems() {
		checkWidget();
		final TimeSeriesTableItem[] items = new TimeSeriesTableItem[_itemCount];
		System.arraycopy(_items, 0, items, 0, items.length);
		return items;
	}

	public TimeSeriesTableItem getItem(int index) {
		checkWidget();
		if (index < 0 || index >= _itemCount)
			SWT.error(SWT.ERROR_INVALID_RANGE);
		return _items[index];
	}

	public int indexOf(TimeSeriesTableItem item) {
		checkWidget();
		if (item == null)
			SWT.error(SWT.ERROR_NULL_ARGUMENT);

		// TODO think about this
		return _model.indexOf(item.getRow());
	}

	public TimeSeriesTableItem getItem(Point point) {
		checkWidget();
		
		final int index = _scrollPane.getRowAt(point.x, point.y);
		if (index <= 0)
			return null;
		return _items[index];
	}

	public TimeSeriesTableItem[] getSelection() {
		checkWidget();
		
		final int[] selectedIndices = getSelectionIndices(); 
		final TimeSeriesTableItem[] selected = new TimeSeriesTableItem[selectedIndices.length];
		int i=0;
		for (int index : selectedIndices) {
			selected[i++] = _items[index];
		}
		
		return selected;
	}

	public int[] getSelectionIndices() {
		checkWidget();
		return _tableListener.getSelectionIndices();
	}

	public void setSelection(TimeSeriesTableItem[] items) {
		checkWidget();
		if (items == null)
			SWT.error(SWT.ERROR_NULL_ARGUMENT);
		
		final int[] selected = new int[items.length];
		for(int i=0; i<items.length; i++) {
			selected[i] = indexOf(items[i]);
		}
		
		setSelection(selected);
	}

	public void setSelection(int[] indices) {
		checkWidget();
		
		deselectAll();
		select(indices);
	}

	public void select(int[] indices) {
		checkWidget();
		if (indices == null)
			SWT.error(SWT.ERROR_NULL_ARGUMENT);
		
		final int[] idxs = new int[indices.length];
		System.arraycopy(indices, 0, idxs, 0, idxs.length);
		Arrays.sort(idxs);
		
		final int[] selected = getSelectionIndices();
		
		UiThreadUtil.performAsyncOnSwing(new Task() {
			@Override
			public void run() {				
				int previous = -1;
				int selectedIndex = 0;
				for (int index : idxs) {
					if (previous == index)
						continue;
					
					while(selectedIndex < selected.length && index < selected[selectedIndex])
						selectedIndex++;
					
					if (selectedIndex < selected.length && index == selected[selectedIndex])
						continue;
					
					final int rowIndex = TableModelWrapperUtils.getRowAt(_scrollPane.getModel(), index);
					_scrollPane.getMainTable().changeSelection(rowIndex, 0, true, false);
					previous = index;
				}
			}});
	}

	public void deselectAll() {
		checkWidget();
		UiThreadUtil.performAsyncOnSwing(new Task() {
			@Override
			public void run() {
				_scrollPane.getRowHeaderTable().clearSelection();
				_scrollPane.getMainTable().clearSelection();
				}
			});
	}

	public void clear(int index) {
		checkWidget();
		if (index < 0 || index >= _itemCount)
			SWT.error(SWT.ERROR_INVALID_RANGE);
		_items[index].clear();
	}

	public void clearAll() {
		checkWidget();
		for (int i=0; i<_itemCount; i++) {
			_items[i].clear();
		}
	}

	public void remove(int[] indices) {
		checkWidget();
		if (indices == null) SWT.error(SWT.ERROR_NULL_ARGUMENT);
		if (indices.length == 0) return;
		
		final int[] working = new int[indices.length];
		System.arraycopy(indices, 0, working, 0, working.length);
		Arrays.sort(working);
		
		final int start = working[0];
		final int end = working[working.length - 1];
		if (start < 0 || start > end || end >= _itemCount)
			SWT.error(SWT.ERROR_INVALID_RANGE);
		
		int last = -1;
		for (int i=working.length-1; i>0; i--) {
			final int index = working[i];
			if (index != last) {
				internalRemove(index);
				last = index;
			}
		}
	}

	public void remove(int start, int end) {
		checkWidget();
		if (start < 0 || start > end || end >= _itemCount)
			SWT.error(SWT.ERROR_INVALID_RANGE);
		
		for (int i=end; i>start; i--) {
			internalRemove(i);
		}
	}

	public void removeAll() {
		checkWidget();
		if (_itemCount <= 0) return;
		
		for (int i=0; i<_itemCount; i++) {
			final TimeSeriesTableItem item = _items[i];
			if (item != null) {
				item.release();
			}
			
			_items[i] = null;
		}
		
		_itemCount = 0;
		UiThreadUtil.performAsyncOnSwing(new Task() {
			@Override
			public void run() {
				_model.removeAllRows();
			}
		});
	}

	public void showItem(TimeSeriesTableItem item) {
		checkWidget();
		final int index = indexOf(item);
		UiThreadUtil.performAsyncOnSwing(new Task() {
			@Override
			public void run() {
				_scrollPane.scrollToRow(index);
			}});
	}

	public void showSelection() {
		checkWidget();
		
		final TimeSeriesTableItem[] items = getSelection();
		if (items.length <= 0)
			return;
		
		showItem(items[0]);
	}
	
	void createItem(final TimeSeriesTableColumn item, final int index) {
		if (index < 0 || index > _attributeColumnCount)
			SWT.error(SWT.ERROR_INVALID_RANGE);
		
		if (_attributeColumnCount == _attributeColumns.length) {
			final TimeSeriesTableColumn[] newColumns = new TimeSeriesTableColumn[_attributeColumns.length + 4];
			System.arraycopy(_attributeColumns, 0, newColumns, 0, _attributeColumns.length);
			_attributeColumns = newColumns;
		}
		
		System.arraycopy(_attributeColumns, index, _attributeColumns, index+1, _attributeColumnCount++-index);
		_attributeColumns[index] = item;
		
		final Column column = _model.createColumn(MultiTableModel.HEADER_COLUMN, index);
		item.register(column);
	}

	void createItem(TimeSeriesTableItem item, int index) {
		if (index < 0 || index > _itemCount)
			SWT.error(SWT.ERROR_INVALID_RANGE);
		
		if (_itemCount == _items.length) {
			final int size = _items.length / 2;
			final TimeSeriesTableItem[] newItems = new TimeSeriesTableItem[_items.length + size];
			System.arraycopy(_items, 0, newItems, 0, _items.length);
			_items = newItems;
		}
		
		System.arraycopy(_items, index, _items, index+1, _itemCount++-index);
		_items[index] = item;
		
		final Row row = _model.createRow(index);
		item.register(row);
	}

	Rectangle getBounds(TimeSeriesTableItem item, int columnIndex) {
		// TODO implement
		final int actualColumnIndex = TableModelWrapperUtils.getColumnAt(_model, columnIndex);
		final int rowIndex = indexOf(item);
		final int actualRowIndex = TableModelWrapperUtils.getRowAt(_model, rowIndex);
		
		return new Rectangle(0, 0, 0, 0);
	}
	
	Rectangle getBounds(TimeSeriesTableItem item) {
		// TODO implement
		return new Rectangle(0, 0, 0, 0);
	}
	
	private void internalRemove(final int index) {
		final TimeSeriesTableItem item = _items[index];
		if (item != null) {
			item.release();
		}
		
		System.arraycopy(_items, index+1, _items, index, --_itemCount-index);
		_items[_itemCount] = null;
		
		UiThreadUtil.performAsyncOnSwing(new Task() {
			@Override
			public void run() {
				_model.removeRow(index);
			}
		});
	}

	private static void initSwingEnvironment() {
        UiThreadUtil.performAsyncOnSwing(new Task() {
            @Override
            public void run() {
                LookAndFeelFactory.installDefaultLookAndFeelAndExtension();
                try {
                    System.setProperty("sun.awt.noerasebackground", "true"); //$NON-NLS-1$ //$NON-NLS-2$
                } catch (NoSuchMethodError error) {
                    // do nothing
                }
            }
        });
    }

	private static void initializeJideFramework() {
        CellRendererManager.initDefaultRenderer();
        CellRendererManager.registerRenderer(Boolean.class,
            new BooleanCheckBoxCellRenderer());

        CellEditorManager.initDefaultEditor();
        CellEditorManager.registerEditor(Boolean.class, new CellEditorFactory() {
            @Override
            public CellEditor create() {
                return new BooleanCheckBoxCellEditor();
            }
        });

        FilterFactoryManager filterManager = new FilterFactoryManager();
        filterManager.registerDefaultFilterFactories();
	};
}
