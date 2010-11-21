package net.karlmartens.platform;

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Comparator;

public final class NumberStringComparator implements Comparator<String> {
	
	private final NumberFormat _format;
	private final Comparator<String> _comparator;
	private final double _tolerance;

	public NumberStringComparator() {
		this(NumberFormat.getInstance());
	}

	public NumberStringComparator(NumberFormat format) {
		this(format, new StringComparator(true), 0.00001);
	}

	public NumberStringComparator(NumberFormat format,
			Comparator<String> base, double tolerance) {
		_format = format;
		_comparator = base;
		_tolerance = tolerance;
	}

	@Override
	public int compare(String o1, String o2) {
		if (o1 == o2)
			return 0;
		
		if (o1 == null)
			return -1;
		
		if (o2 == null)
			return 1;
		
		try {
			final double n1 = _format.parse(o1).doubleValue();
			final double n2 = _format.parse(o2).doubleValue();
			if (Math.abs(n1 - n2) > _tolerance) {
				if (n1 < n2) {
					return -1;
				}
				
				return 1;
			}
		} catch (ParseException e) {
			// Ignore error compare string with base comparator
		}

		return _comparator.compare(o1, o2);
	}

}
