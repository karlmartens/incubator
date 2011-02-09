package net.karlmartens.ui.viewer;

import java.text.NumberFormat;
import java.text.ParseException;

import net.karlmartens.ui.widget.TimeSeriesTableItem;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.joda.time.Interval;
import org.joda.time.LocalDate;

class TimeSeriesTableValueEditingSupport extends EditingSupport {

	private final TimeSeriesTableViewer _viewer;
	private Interval _interval;

	TimeSeriesTableValueEditingSupport(
			TimeSeriesTableViewer viewer) {
		super(viewer);
		_viewer = viewer;
	}

	@Override
	protected CellEditor getCellEditor(Object element) {
		return new TextCellEditor((Composite)_viewer.getControl(), SWT.RIGHT);
	}

	@Override
	protected boolean canEdit(Object element) {
		final TimeSeriesEditingSupport editingSupport = _viewer.getEditingSupport();
		if (editingSupport == null)
			return false;
		
		return editingSupport.canEdit(element);
	}

	@Override
	protected Object getValue(Object element) {
		throw new UnsupportedOperationException();
	}

	@Override
	protected void setValue(Object element, Object value) {
		throw new UnsupportedOperationException();
	}

	@Override
	protected void initializeCellEditorValue(CellEditor cellEditor,
			ViewerCell cell) {
		_interval = null;
		cellEditor.setValue("");
		
		final TimeSeriesEditingSupport editingSupport = _viewer.getEditingSupport();
		if (editingSupport == null)
			return;
		
		final TimeSeriesContentProvider cp = (TimeSeriesContentProvider)_viewer.getContentProvider();
		if (cp == null)
			return;
		
		final int index = computePeriodIndex(cell);
		final LocalDate[] dates = cp.getDates();
		if (dates == null || dates.length < 2 || index < 0 || index >= dates.length - 1)
			return;
		
		_interval = new Interval(dates[index].toDateMidnight(), dates[index+1].toDateMidnight());
		
		final double value = cp.getValue(cell.getElement(), _interval);
		final NumberFormat format = getNumberFormat(editingSupport);
		cellEditor.setValue(format.format(value));
	}
	
	@Override
	protected void saveCellEditorValue(CellEditor cellEditor, ViewerCell cell) {
		final TimeSeriesEditingSupport editingSupport = _viewer.getEditingSupport();
		if (editingSupport == null || _interval == null)
			return;
					
		final TimeSeriesContentProvider cp = (TimeSeriesContentProvider)_viewer.getContentProvider();
		if (cp == null)
			return;

		final String source = (String)cellEditor.getValue();
		if (source == null || source.trim().length() == 0) {
			update(cell, 0.0);
			return;
		}
		
		try {
			final NumberFormat format = getNumberFormat(editingSupport);
			final Number n = format.parse(source);
			update(cell, n.doubleValue());
		} catch (ParseException e) {
			// ignore
		}
	}
	
	private void update(ViewerCell cell, double value) {
		_viewer.getEditingSupport().setValue(cell.getElement(), _interval, value);
		
		final TimeSeriesTableItem item = (TimeSeriesTableItem)cell.getItem();
		item.setValue(computePeriodIndex(cell), value);
		
		_viewer.getControl().redraw();
	}
	
	private int computePeriodIndex(ViewerCell cell) {
		return cell.getColumnIndex() - _viewer.getControl().getColumnCount();
	}

	private NumberFormat getNumberFormat(TimeSeriesEditingSupport editingSupport) {
		NumberFormat format = editingSupport.getNumberFormat();
		if (format != null)
			return format;
		
		return NumberFormat.getNumberInstance();
	}
}