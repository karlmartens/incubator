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
package net.karlmartens.platform.util;

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
