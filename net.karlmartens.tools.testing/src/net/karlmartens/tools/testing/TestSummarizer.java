package net.karlmartens.tools.testing;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.ComparisonFailure;

public final class TestSummarizer {

	private final List<String> _expected = new ArrayList<String>();
	private final List<String> _actual = new ArrayList<String>();

	public TestSummarizer expected(String... lines) {
		_expected .addAll(Arrays.asList(lines));
		return this;
	}

	public TestSummarizer actual(String pattern, Object... args) {
		_actual.add(String.format(pattern, args));
		return this;
	}

	public void check() {
		if (_expected.size() != _actual.size())
			createAssertionError();
		
		for (int i=0; i<_expected.size(); i++) {
			final String expected = _expected.get(i);
			final String actual = _actual.get(i);
			if (!equals(expected, actual)) {
				createAssertionError();
			}
		}
	}

	private void createAssertionError() {
		throw new ComparisonFailure(null, format(_expected), format(_actual));
	}

	private static String format(List<String> lines) {
		final StringBuilder builder = new StringBuilder();
		for (String line : lines) {
			if (builder.length() > 0) {
				builder.append(", //\n");
			}
			
			final String detail = line//
					.replace("\"", "\\\"") //
					.replace("\r", "\\r") //
					.replace("\n", "\\n");
			
			builder.append(String.format("\"%1$s\"", detail));
		}
		return builder.toString();
	}

	private static boolean equals(String expected, String actual) {
		if (expected == actual)
			return true;
		
		if (expected == null || actual == null)
			return false;
		
		return expected.equals(actual);
	}

}
