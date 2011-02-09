package net.karlmartens.ui.viewer;

import java.text.NumberFormat;

import org.joda.time.Interval;

public interface TimeSeriesEditingSupport {

	NumberFormat getNumberFormat();

	boolean canEdit(Object element);

	void setValue(Object element, Interval interval, double value);

}
