/**
 *  net.karlmartens.ui, is a library of UI widgets
 *  Copyright (C) 2011
 *  Karl Martens
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Lesser General Public License as 
 *  published by the Free Software Foundation, either version 3 of the 
 *  License, or (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */
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

class TimeSeriesTableValueEditingSupport extends EditingSupport {

	private final TimeSeriesTableViewer _viewer;

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
		cellEditor.setValue("");
		
		final TimeSeriesEditingSupport editingSupport = _viewer.getEditingSupport();
		if (editingSupport == null)
			return;
		
		final TimeSeriesContentProvider cp = (TimeSeriesContentProvider)_viewer.getContentProvider();
		if (cp == null)
			return;
		
		final double value = cp.getValue(cell.getElement(), computePeriodIndex(cell));
		final NumberFormat format = getNumberFormat(editingSupport);
		cellEditor.setValue(format.format(value));
	}

	@Override
	protected void saveCellEditorValue(CellEditor cellEditor, ViewerCell cell) {
		final TimeSeriesEditingSupport editingSupport = _viewer.getEditingSupport();
		if (editingSupport == null)
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
		_viewer.getEditingSupport().setValue(cell.getElement(), computePeriodIndex(cell), value);
		
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