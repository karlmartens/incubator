package net.karlmartens.net;

import java.text.NumberFormat;
import java.util.Arrays;

import org.joda.time.Interval;
import org.joda.time.LocalDate;

import net.karlmartens.ui.viewer.TimeSeriesEditingSupport;

final class TestTimeSeriesEditingSupport implements TimeSeriesEditingSupport {

	private final LocalDate[] _dates;
	private final int _index;
	private final NumberFormat _format;

	public TestTimeSeriesEditingSupport(NumberFormat format, LocalDate[] dates, int index) {
		_dates = dates;
		_index = index;
		_format = format;
	}

	@Override
	public NumberFormat getNumberFormat() {
		return _format;
	}

	@Override
	public boolean canEdit(Object element) {
		return true;
	}

	@Override
	public void setValue(Object element, Interval interval, double value) {
		final double[] series = (double[])((Object[])element)[_index];
		final int index = Arrays.binarySearch(_dates, interval.getStart().toLocalDate());
		series[index] = value;
	}

}
