package com._3esi.platform.desktop.ui.control;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Frame;
import java.awt.Panel;

import javax.swing.JRootPane;

import org.eclipse.swt.SWT;
import org.eclipse.swt.awt.SWT_AWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;

import com._3esi.platform.desktop.ui.Task;
import com._3esi.platform.desktop.ui.UiThreadUtil;
import com.jidesoft.filter.FilterFactoryManager;
import com.jidesoft.grid.CellEditorManager;
import com.jidesoft.grid.CellRendererManager;
import com.jidesoft.grid.DefaultMultiTableModel;
import com.jidesoft.grid.JideTable;
import com.jidesoft.grid.TableScrollPane;
import com.jidesoft.plaf.LookAndFeelFactory;

public final class TimeSeriesTable extends Composite {

	private final DefaultMultiTableModel _model;
	private TableScrollPane _scrollPane;
	
	private TimeSeriesColumn[] _columns = new TimeSeriesColumn[4];
	private int _columnCount = 0;
	
	public TimeSeriesTable(Composite parent) {
		super(parent, SWT.EMBEDDED | SWT.NO_BACKGROUND);

		_model = new DefaultMultiTableModel();

		setLayout(new FillLayout());
		//setBackground(Display.getDefault().getSystemColor(SWT.COLOR_WHITE));
		final Frame frame = SWT_AWT.new_Frame(this);
		
		initSwingEnvironment();
		UiThreadUtil.performAsyncOnSwing(new Task() {
			@Override
			public void run() {
				CellRendererManager.initDefaultRenderer();
				CellEditorManager.initDefaultEditor();

				FilterFactoryManager filterManager = new FilterFactoryManager();
                filterManager.registerDefaultFilterFactories();
		
                final Panel panel = new Panel(new BorderLayout());
                final JRootPane root = new JRootPane();
                panel.add(root);
                                
                _scrollPane = new TableScrollPane(_model);
                _scrollPane.setAutoscrolls(true);
                _scrollPane.setAllowMultiSelectionInDifferentTable(true);
                _scrollPane.setCellSelectionEnabled(true);
                _scrollPane.setColumnSelectionAllowed(true);
                _scrollPane.setRowSelectionAllowed(true);
                _scrollPane.setHorizontalScrollBarCoversWholeWidth(true);
                _scrollPane.setVerticalScrollBarCoversWholeHeight(true);
                _scrollPane.setKeepCornerVisible(false);
                _scrollPane.setNonContiguousCellSelectionAllowed(true);
                _scrollPane.setWheelScrollingEnabled(true);
                
                final JideTable mainTable = (JideTable)_scrollPane.getMainTable();
                mainTable.getTableHeader().setReorderingAllowed(false);
                mainTable.setClickCountToStart(2);
                
                final Container contentPane = root.getContentPane();
                contentPane.add(_scrollPane, BorderLayout.CENTER);
                frame.add(panel);
			}
		});
	}
	
	public int getColumnCount() {
		checkWidget();
		return _columnCount;
	}

	void createItem(TimeSeriesColumn item, int index) {
        if (index < 0 || index > _columnCount)
            SWT.error(SWT.ERROR_INVALID_RANGE);

        if (index == _columns.length) {
            final TimeSeriesColumn[] newColumns = new TimeSeriesColumn[_columns.length + 4];
            System.arraycopy(_columns, 0, newColumns, 0, _columns.length);
            _columns = newColumns;
        }
        
		System.arraycopy(_columns, index, _columns, index + 1, _columnCount++ - index);
        _columns[index] = item;

        refreshColumns();
	}

    void refreshColumns() {
        final String[] columnNames = new String[_columnCount];
        for (int i=0; i<_columnCount; i++) {
        	columnNames[i] = _columns[i].getText();
        }
        _model.setColumnIdentifiers(columnNames);
        
        if (_scrollPane != null) {
	        UiThreadUtil.performAsyncOnSwing(new Task() {
				@Override
				public void run() {
					_scrollPane.refreshColumns();
				}
	        });
        }
	}

	private void initSwingEnvironment() {
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
}
