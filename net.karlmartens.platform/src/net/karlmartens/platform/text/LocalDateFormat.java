package net.karlmartens.platform.text;

import java.text.FieldPosition;
import java.text.Format;
import java.text.ParsePosition;

import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormatter;

public final class LocalDateFormat extends Format {
	private static final long serialVersionUID = 1L;

	private final DateTimeFormatter _formatter;

	public LocalDateFormat(DateTimeFormatter formatter) {
		_formatter = formatter;
	}

	@Override
	public StringBuffer format(Object obj, StringBuffer toAppendTo,
			FieldPosition pos) {
		if (obj instanceof LocalDate) {
			return format((LocalDate)obj, toAppendTo);
		}
		
		throw new IllegalArgumentException();
	}
	
	public StringBuffer format(LocalDate date, StringBuffer toAppendTo) {
		if (date != null) {
			_formatter.printTo(toAppendTo, date);
		}
		
		return toAppendTo;
	}
	
	public String format(LocalDate date) {
		return format(date, new StringBuffer()).toString();
	}

	@Override
	public Object parseObject(String source, ParsePosition pos) {
		throw new UnsupportedOperationException();
	}

}
