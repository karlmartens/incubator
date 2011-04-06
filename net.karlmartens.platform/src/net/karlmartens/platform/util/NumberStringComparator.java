/**
 *   Copyright 2010,2011 Karl Martens
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
package net.karlmartens.platform.util;

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

  public NumberStringComparator(NumberFormat format, Comparator<String> base, double tolerance) {
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
