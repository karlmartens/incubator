package net.karlmartens.platform;

import java.util.Comparator;

public final class StringComparator implements Comparator<String> {
	
	private final boolean _ignoreCase;

	public StringComparator(boolean ignoreCase) {
		_ignoreCase = ignoreCase;
	}
	
	@Override
	public int compare(String o1, String o2) {
		if (o1 == o2)
			return 0;
		
		if (o1 == null)
			return -1;
		
		if (o2 == null)
			return 1;
		
		if (_ignoreCase) {
			return o1.compareToIgnoreCase(o2);
		}
		
		return o1.compareTo(o2);
	}

}
