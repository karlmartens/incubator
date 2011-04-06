/**
 *   Copyright 2011 Karl Martens
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *       
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 *
 *   net.karlmartens.platform, is a library of shared basic utility classes
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
