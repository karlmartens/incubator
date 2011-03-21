/**
 *  net.karlmartens.platform, is a library of shared basic utility classes
 *  Copyright (C) 2010,2011
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

}
