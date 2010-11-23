package net.karlmartens.platform.util;

public final class NullSafe {
	
	private NullSafe() {
		// Utility class
	}

	public static String toString(Object value) {
		if (value == null)
			return null;

		return value.toString();
	}

}
