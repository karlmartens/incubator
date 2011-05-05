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
package net.karlmartens.platform.util;

import java.util.BitSet;

public final class ArraySupport {

  public static long max(long[] arr) {
    if (arr.length == 0)
      throw new IllegalArgumentException();

    long max = arr[0];
    for (int i = 1; i < arr.length; i++) {
      max = Math.max(max, arr[i]);
    }

    return max;
  }

  public static int max(int[] arr) {
    if (arr.length == 0)
      throw new IllegalArgumentException();

    int max = arr[0];
    for (int i = 1; i < arr.length; i++) {
      max = Math.max(max, arr[i]);
    }

    return max;
  }

  public static double max(double[] arr) {
    if (arr.length == 0)
      throw new IllegalArgumentException();

    double max = arr[0];
    for (int i = 1; i < arr.length; i++) {
      max = Math.max(max, arr[i]);
    }

    return max;
  }

  public static float max(float[] arr) {
    if (arr.length == 0)
      throw new IllegalArgumentException();

    float max = arr[0];
    for (int i = 1; i < arr.length; i++) {
      max = Math.max(max, arr[i]);
    }

    return max;
  }

  public static long min(long[] arr) {
    if (arr.length == 0)
      throw new IllegalArgumentException();

    long min = arr[0];
    for (int i = 0; i < arr.length; i++) {
      min = Math.min(min, arr[i]);
    }

    return min;
  }

  public static int min(int[] arr) {
    if (arr.length == 0)
      throw new IllegalArgumentException();

    int min = arr[0];
    for (int i = 0; i < arr.length; i++) {
      min = Math.min(min, arr[i]);
    }

    return min;
  }

  public static double min(double[] arr) {
    if (arr.length == 0)
      throw new IllegalArgumentException();

    double min = arr[0];
    for (int i = 0; i < arr.length; i++) {
      min = Math.min(min, arr[i]);
    }

    return min;
  }

  public static float min(float[] arr) {
    if (arr.length == 0)
      throw new IllegalArgumentException();

    float min = arr[0];
    for (int i = 0; i < arr.length; i++) {
      min = Math.min(min, arr[i]);
    }

    return min;
  }

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
