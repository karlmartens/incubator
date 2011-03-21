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

import java.util.BitSet;

public final class ArraySupport {

  public static int[] minus(int[] first, int[] second) {
    final BitSet set = new BitSet();
    for (int i : first)
      set.set(i);

    for (int i : second)
      set.flip(i);

    return toArray(set);
  }

  public static void swap(long[] arr, int firstIndex, int secondIndex) {
    final long t = arr[firstIndex];
    arr[firstIndex] = arr[secondIndex];
    arr[secondIndex] = t;
  }

  public static void swap(int[] arr, int firstIndex, int secondIndex) {
    final int t = arr[firstIndex];
    arr[firstIndex] = arr[secondIndex];
    arr[secondIndex] = t;
  }

  public static void swap(short[] arr, int firstIndex, int secondIndex) {
    final short t = arr[firstIndex];
    arr[firstIndex] = arr[secondIndex];
    arr[secondIndex] = t;
  }

  public static void swap(char[] arr, int firstIndex, int secondIndex) {
    final char t = arr[firstIndex];
    arr[firstIndex] = arr[secondIndex];
    arr[secondIndex] = t;
  }

  public static void swap(byte[] arr, int firstIndex, int secondIndex) {
    final byte t = arr[firstIndex];
    arr[firstIndex] = arr[secondIndex];
    arr[secondIndex] = t;
  }

  public static void swap(double[] arr, int firstIndex, int secondIndex) {
    final double t = arr[firstIndex];
    arr[firstIndex] = arr[secondIndex];
    arr[secondIndex] = t;
  }

  public static void swap(float[] arr, int firstIndex, int secondIndex) {
    final float t = arr[firstIndex];
    arr[firstIndex] = arr[secondIndex];
    arr[secondIndex] = t;
  }

  public static void swap(boolean[] arr, int firstIndex, int secondIndex) {
    final boolean t = arr[firstIndex];
    arr[firstIndex] = arr[secondIndex];
    arr[secondIndex] = t;
  }

  public static void swap(Object[] arr, int firstIndex, int secondIndex) {
    final Object t = arr[firstIndex];
    arr[firstIndex] = arr[secondIndex];
    arr[secondIndex] = t;
  }

  public static int[] toArray(BitSet set) {
    final int[] a = new int[set.cardinality()];
    int index = 0;
    for (int i = set.nextSetBit(0); i >= 0; i = set.nextSetBit(i + 1)) {
      a[index++] = i;
    }
    return a;
  }
}
