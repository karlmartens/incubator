package net.karlmartens.ui.viewer;

import java.util.Arrays;

import net.karlmartens.ui.viewer.TimeSeriesContentProvider;

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.joda.time.Interval;
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
	public double getValue(Object element, Interval interval) {
		final double[] series = (double[])((Object[])element)[_index];

		final int index = Arrays.binarySearch(_dates, interval.getStart().toLocalDate());
		return series[index];
	}
}