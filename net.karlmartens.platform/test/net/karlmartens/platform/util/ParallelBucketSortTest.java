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

import org.junit.Test;

import net.karlmartens.tools.testing.TestSummarizer;

public final class ParallelBucketSortTest {

  @Test
  public void testLargeSort() throws Exception {
    final Integer[] arr = new Integer[10000];
    for (int i=0; i<arr.length; i++) {
      arr[i] = Integer.valueOf(i);
    }
    
    final String[] expected = format(arr);
    
    ArraySupport.shuffle(arr);
    ParallelBucketSort.sort(arr);
    
    final TestSummarizer summarizer = new TestSummarizer();
    summarizer.expected(expected);
    summarize(arr, summarizer);
    summarizer.check();
  }

  private static String[] format(Integer[] arr) {
    final String[] result = new String[arr.length];
    for (int i=0; i<arr.length; i++) {
      result[i] = String.format("%1$d", arr[i]);
    }
    return result;
  }

  private static void summarize(Integer[] arr, TestSummarizer summarizer) {
    final String[] actuals = format(arr);
    for (String a : actuals) {
      summarizer.actual("%1$s", a);
    }
  }
  
}
