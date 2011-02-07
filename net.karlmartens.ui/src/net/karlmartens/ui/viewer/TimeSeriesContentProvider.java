package net.karlmartens.ui.viewer;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.joda.time.Interval;
import org.joda.time.LocalDate;

public interface TimeSeriesContentProvider extends IStructuredContentProvider {
	
	LocalDate[] getDates();
	
	Double getValue(Object element, Interval interval);

}
