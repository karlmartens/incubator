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

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.joda.time.LocalDate;

final class TestTimeSeriesContentProvider extends ArrayContentProvider implements TimeSeriesContentProvider {

	private final LocalDate[] _dates;
	private final int _index;
	
	public TestTimeSeriesContentProvider(LocalDate[] dates, int index) {
		_dates = dates;
		_index = index;
	}

	@Override
	public LocalDate[] getDates() {
		return _dates;
	}
	
	@Override
	public double getValue(Object element, int index) {
		final double[] series = (double[])((Object[])element)[_index];
		return series[index];
	}
}