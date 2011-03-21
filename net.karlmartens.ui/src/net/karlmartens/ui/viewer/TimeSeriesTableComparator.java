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

import net.karlmartens.platform.util.ComparableComparator;
import net.karlmartens.platform.util.NullSafe;
import net.karlmartens.platform.util.NumberStringComparator;
import net.karlmartens.ui.widget.TimeSeriesTable;
import net.karlmartens.ui.widget.TimeSeriesTableColumn;

import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;

public final class TimeSeriesTableComparator {

	private final TimeSeriesTableViewer _viewer;
	private final Comparator _comparator = new Comparator();

	public TimeSeriesTableComparator(TimeSeriesTableViewer viewer) {
		_viewer = viewer;

		final TimeSeriesTable table = _viewer.getControl();
		for (int i = 0; i <= table.getColumnCount(); i++) {
			new Listener(table.getColumn(i));
		}
	}

	private class Listener implements SelectionListener, DisposeListener {

		private final TimeSeriesTableColumn _column;

		private Listener(TimeSeriesTableColumn column) {
			_column = column;
			hook();
		}

		private void hook() {
			_column.addSelectionListener(this);
			_column.addDisposeListener(this);
		}

		@Override
		public void widgetDisposed(DisposeEvent e) {
			_column.removeSelectionListener(this);
			_column.removeDisposeListener(this);
		}

		@Override
		public void widgetSelected(SelectionEvent e) {
			final TimeSeriesTable table = _viewer.getControl();
			final Integer previousIndex = _comparator._columnIndex;
			_comparator._columnIndex = table.indexOf(_column);

			if (NullSafe.equals(previousIndex, _comparator._columnIndex)) {
				_comparator._ascending = !_comparator._ascending;
			} else {
				_comparator._ascending = true;
			}

			_viewer.setComparator(_comparator);
			_viewer.refresh();

		}

		@Override
		public void widgetDefaultSelected(SelectionEvent e) {
			// ignore
		}
	}

	private static class Comparator extends ViewerComparator {

		private final ComparableComparator<Double> _dComparator = new ComparableComparator<Double>();
		private final java.util.Comparator<String> _comparator = new NumberStringComparator();
		private Integer _columnIndex;
		private boolean _ascending = true;

		@Override
		public int compare(Viewer viewer, Object e1, Object e2) {
			final TimeSeriesTableViewer v = (TimeSeriesTableViewer) viewer;
			if (_columnIndex != null) {
				final TimeSeriesContentProvider cp = (TimeSeriesContentProvider) v
						.getContentProvider();
				final int periodIndex = _columnIndex
						- v.getControl().getColumnCount();
				final double d1 = cp.getValue(e1, periodIndex);
				final double d2 = cp.getValue(e2, periodIndex);
				return (_ascending ? 1 : -1) * _dComparator.compare(d1, d2);
			}

			final ILabelProvider lp = (ILabelProvider) v.getLabelProvider();
			final String s1 = lp.getText(e1);
			final String s2 = lp.getText(e2);
			return (_ascending ? 1 : -1) * _comparator.compare(s1, s2);
		}
	}
}
