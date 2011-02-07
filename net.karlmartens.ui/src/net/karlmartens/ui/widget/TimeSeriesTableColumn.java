package net.karlmartens.ui.widget;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.widgets.Item;
import org.eclipse.swt.widgets.TypedListener;

public final class TimeSeriesTableColumn extends Item {

	private int _width = 0;

	/**
	 * <p><dl>
     * <dt><b>Styles:</b></dt>
     * <dd>CHECK</dd>
     * </dl></p>
     * 
	 */
	public TimeSeriesTableColumn(TimeSeriesTable table, int style) {
		this(table, style, table.getColumnCount());
	}
	
	public TimeSeriesTableColumn(TimeSeriesTable table, int style, int index) {
		super(table, style);
		table.createItem(this, index);
	}

	public void setWidth(int width) {
		_width = width;
	}

	public int getWidth() {
		return _width;
	}
	
	public void addControlListener(ControlListener listener) {
		final TypedListener tListener = new TypedListener(listener);
		addListener(SWT.Resize, tListener);
		addListener(SWT.Move, tListener);
	}

	public void removeControlListener(ControlListener listener) {
		final TypedListener tListener = new TypedListener(listener);
		removeListener(SWT.Resize, tListener);
		removeListener(SWT.Move, tListener);
	}
}
