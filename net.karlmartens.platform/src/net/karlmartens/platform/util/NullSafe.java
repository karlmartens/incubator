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

import java.util.Comparator;

public final class NullSafe {

  private NullSafe() {
    // Utility class
  }

  public static String toString(Object value) {
    if (value == null)
      return null;

    return value.toString();
  }

  public static boolean equals(Object o1, Object o2) {
    if (o1 == o2)
      return true;

    if (o1 == null || o2 == null)
      return false;

    return o1.equals(o2);
  }

  public static <T> T min(T a, T b, Comparator<T> comparator) {
    if (comparator.compare(a, b) <= 0)
      return a;

    return b;
  }

  public static <T extends Comparable<T>> T min(T a, T b) {
    final Comparator<T> comparator = ComparableComparator.getInstance();
    return min(a, b, comparator);
  }

  public static <T> T max(T a, T b, Comparator<T> comparator) {
    if (comparator.compare(a, b) <= 0)
      return b;

    return a;
  }

  public static <T extends Comparable<T>> T max(T a, T b) {
    final Comparator<T> comparator = ComparableComparator.getInstance();
    return max(a, b, comparator);
  }
}
