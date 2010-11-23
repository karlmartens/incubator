package net.karlmartens.ui.widget;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ControlEditor;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;


public final class GridChooserEditor extends ControlEditor {
	private static final int TIMEOUT = 1500;
	
	private GridChooser _chooser;
	private ControlListener _columnListener;
	private Runnable _timer;
	private GridChooserItem _item;
	private int _column;

	public GridChooserEditor(GridChooser chooser) {
		super(chooser);
		_chooser = chooser;
		
		_columnListener = new ControlListener() {
			@Override
			public void controlResized(ControlEvent e) {
				layout();
			}
			
			@Override
			public void controlMoved(ControlEvent e) {
				layout();
			}
		};
		
		_timer = new Runnable () {
			public void run() {
				layout ();
			}
		};
		
		grabVertical=true;
	}
	
	public GridChooserItem getItem() {
		return _item;
	}
	
	private void setItem(GridChooserItem item) {
		_item = item;
		resize();
	}
	
	private void setColumn(int column) {
		final int columnCount = _chooser.getColumnCount();
		
		if (columnCount == 0) {
			_column = column == 0 ? 0 : -1;
			resize();
			return;
		}
		
		if (_column > -1 && _column < columnCount) {
			final GridChooserColumn chooserColumn = _chooser.getColumn(_column);
			chooserColumn.removeControlListener(_columnListener);
			_column = -1;
		}
		
	    if (column < -1 || column >= columnCount)
	    	return;
	    
	    _column = column;
	    final GridChooserColumn chooserColumn = _chooser.getColumn(_column);
	    chooserColumn.addControlListener(_columnListener);
	    resize();
	}
	
	@Override
	public void dispose() {
		if (_chooser != null && !_chooser.isDisposed()) {
			if (_column > -1 && _column < _chooser.getColumnCount()) {
				final GridChooserColumn column = _chooser.getColumn(_column);
				column.removeControlListener(_columnListener);
			}
		}

		_chooser = null;
		_columnListener = null;
		_timer = null;
		_item = null;
		_column = -1;
		super.dispose();
	}
	
	@Override
	public void setEditor(Control editor) {
		final GridChooserItem item = getItem();
		if (editor != null && item != null) {
			if (getItem().isSelected()) {
				editor.setParent(_chooser.getSelectedComposite());
			} else {
				editor.setParent(_chooser.getAvailableComposite());
			}
		}
		super.setEditor(editor);
		resize();
	}

	public void setEditor(Control editor, GridChooserItem item, int columnIndex) {		
		setItem(item);
		setColumn(columnIndex);
		setEditor(editor);
	}
	
	@Override
	public void layout() {
		if (_chooser == null || _chooser.isDisposed())
			return;
		
		if (_item == null || _item.isDisposed()) 
			return;
		
		final int columnCount = _chooser.getColumnCount();
		if (columnCount == 0 && _column != 0) 
			return;
		
		if (columnCount > 0 && (_column < 0 || _column >= columnCount))
			return;
		
		final Control editor = getEditor();
		if (editor == null || editor.isDisposed()) return;
		
		final boolean hadFocus = editor.isVisible() && editor.isFocusControl();
		
		editor.setBounds (computeCellBounds());
		
		if (hadFocus) {
			if (editor == null || editor.isDisposed()) return;
			editor.setFocus ();
		}
	}

	private void resize() {
		layout();
		
		if (_chooser != null) {
			final Display display = _chooser.getDisplay();
			display.timerExec(-1, _timer);
			display.timerExec(TIMEOUT, _timer);
		}
	}
	
	private Rectangle computeCellBounds() {
		if (_item == null || _column <= -1 || _item.isDisposed())
			return new Rectangle(0, 0, 0, 0);

		final Rectangle cell = _item.getBounds(_column);

		// Remove space taken by image from editor
		final Rectangle image = _item.getImageBounds(_column);
		cell.x = image.x + image.width;
		cell.width -= image.width;

		// Convert from global widget coordinates to table relative coordinates
		final Composite parent = getEditor().getParent();
		final Rectangle table = parent.getBounds();
		cell.x -= table.x;
		cell.y -= table.y;
		
		// Reduce cell width to compensate for trimming
		final Rectangle area = parent.getClientArea();
		if (cell.x < area.x + area.width) {
			if (cell.x + cell.width > area.x + area.width) {
				cell.width = area.x + area.width - cell.x;
			}
		}
		
		final Rectangle editor = new Rectangle(cell.x, cell.y, minimumWidth, minimumHeight);
		
		if (grabHorizontal) {
			editor.width = Math.max(cell.width, minimumWidth);
		}
		
		if (grabVertical) {
			editor.height = Math.max(cell.height, minimumHeight);
		}

		if (horizontalAlignment == SWT.RIGHT) {
			editor.x += cell.width - editor.width;
		} else if (horizontalAlignment == SWT.LEFT) {
			// nothing to do
		} else {
			editor.x += (cell.width - editor.width) / 2;
		}
		
		return editor;
	}
}
