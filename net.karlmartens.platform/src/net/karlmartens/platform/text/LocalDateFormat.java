/**
 *  net.karlmartens.platform, is a library of shared basic utility classes
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
  public StringBuffer format(Object obj, StringBuffer toAppendTo, FieldPosition pos) {
    if (obj instanceof LocalDate) {
      return format((LocalDate) obj, toAppendTo);
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
