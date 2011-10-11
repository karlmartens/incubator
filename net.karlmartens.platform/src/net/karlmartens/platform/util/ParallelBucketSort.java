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

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public final class ParallelBucketSort {
  
  private ParallelBucketSort() {
    // Utility class
  }
  
  public static <T extends Comparable<T>> void sort(T[] arr) {
    sort(arr, new ComparableComparator<T>());
  }

  public static <T> void sort(T[] arr, Comparator<T> comparator) {
    final Collection<T>[] buckets = partition(arr, comparator);
    
    final int threads = Runtime.getRuntime().availableProcessors();
    final ExecutorService service = Executors.newFixedThreadPool(threads);
    
    final List<Future<T[]>> results = new ArrayList<Future<T[]>>(buckets.length);
    for (Collection<T> items : buckets) {
      if (items.size() <= 0)
        continue;
      
      results.add(service.submit(new Worker<T>(items, comparator)));
    }
    
    try {
      int index = 0;
      for (Future<T[]> result : results) {
        final T[] v = result.get();
        System.arraycopy(v, 0, arr, index, v.length);
        index += v.length;
      }
    } catch (ExecutionException e) {
      throw new RuntimeException(e);
    } catch (InterruptedException e) {
      throw new RuntimeException(e);
    } finally {
      service.shutdown();
    }
  }
  
  private static <T> Collection<T>[] partition(T[] arr, Comparator<T> comparator) {
    final int partitions = arr.length / 16;
    @SuppressWarnings("unchecked")
    final T[] pivots = (T[]) Array.newInstance(arr.getClass().getComponentType(), partitions);
    @SuppressWarnings("unchecked")
    final Collection<T>[] buckets = new Collection[partitions+1];

    final Random random = new Random();
    for (int i=0; i<pivots.length; i++) {
      pivots[i] = arr[random.nextInt(partitions)];
      buckets[i] = new ArrayList<T>();
    }
    buckets[buckets.length -1] = new ArrayList<T>();
    
    Arrays.sort(pivots, comparator);
    
    for (int i=0; i<arr.length; i++) {
      int index = Arrays.binarySearch(pivots, arr[i], comparator);
      if (index < 0) 
        index = -(index + 1);
      
      buckets[index].add(arr[i]);
    }
    
    return buckets;
  }
  
  private static class Worker<T> implements Callable<T[]> {

    private final Collection<T> _items;
    private final Comparator<T> _comparator;

    public Worker(Collection<T> items, Comparator<T> comparator) {
      _items = items;
      _comparator = comparator;
    }

    @Override
    public T[] call() throws Exception {
      @SuppressWarnings("unchecked")
      final T[] arr = (T[]) _items.toArray();
      Arrays.sort(arr, _comparator);
      return arr;
    }
    
  }
}
