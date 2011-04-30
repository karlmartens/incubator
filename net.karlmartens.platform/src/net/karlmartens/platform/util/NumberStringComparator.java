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

import java.text.BreakIterator;
import java.text.Collator;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Comparator;

public final class NumberStringComparator implements Comparator<String> {

  private final NumberFormat _format;
  private final Collator _collator;
  private final double _tolerance;
  private final BreakIterator[] _bi;

  public NumberStringComparator() {
    this(NumberFormat.getInstance());
  }

  public NumberStringComparator(NumberFormat format) {
    this(format, getDefaultCollator(), 0.00001);
  }

  private static Collator getDefaultCollator() {
    final Collator c = Collator.getInstance();
    c.setStrength(Collator.PRIMARY);
    return c;
  }

  public NumberStringComparator(NumberFormat format, Collator collator, double tolerance) {
    _format = format;
    _collator = collator;
    _tolerance = tolerance;
    _bi = new BreakIterator[] { BreakIterator.getWordInstance(), BreakIterator.getWordInstance() };
  }

  @Override
  public int compare(String o1, String o2) {
    if (o1 == o2)
      return 0;

    if (o1 == null)
      return -1;

    if (o2 == null)
      return 1;

    _bi[0].setText(o1);
    _bi[1].setText(o2);

    int current1 = _bi[0].first();
    int current2 = _bi[1].first();
    for (;;) {
      if (current1 == BreakIterator.DONE) {
        if (current2 == BreakIterator.DONE) {
          return 0;
        }

        return -1;
      }

      if (current2 == BreakIterator.DONE)
        return 1;

      int next1 = _bi[0].following(current1);
      if (next1 == BreakIterator.DONE)
        next1 = o1.length();

      int next2 = _bi[1].following(current2);
      if (next2 == BreakIterator.DONE)
        next2 = o2.length();

      final String w1 = o1.substring(current1, next1);
      final String w2 = o2.substring(current2, next2);

      try {
        final double n1 = _format.parse(w1).doubleValue();
        final double n2 = _format.parse(w2).doubleValue();
        if (Math.abs(n1 - n2) > _tolerance) {
          if (n1 < n2) {
            return -1;
          }

          return 1;
        }
      } catch (ParseException e) {
        // Ignore error compare string with base comparator
      }

      final int c = _collator.compare(w1, w2);
      if (c != 0)
        return c;

      current1 = _bi[0].next();
      current2 = _bi[1].next();
    }
  }
}
