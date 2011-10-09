/**
 *  
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
package net.karlmartens.tools.mapreduce;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import net.karlmartens.platform.util.Pair;

public final class MapReduceExecutor<K, V, K1, V1, V2> implements Callable<Iterable<Pair<K1, V2>>> {

  private final MapReduceJob<K, V, K1, V1, V2> _job;

  public MapReduceExecutor(MapReduceJob<K, V, K1, V1, V2> job) {
    _job = job;
  }

  @Override
  public Iterable<Pair<K1, V2>> call() throws Exception {

    final int threads = Runtime.getRuntime().availableProcessors();
    final ExecutorService executor = Executors.newFixedThreadPool(threads);

    final Pair<K1, V1>[] mapResults = performMap(executor);
    final Collection<Pair<K1, Collection<V1>>> cResults = collect(mapResults);
    return performReduce(executor, cResults);
  }

  @SuppressWarnings("unchecked")
  private Pair<K1, V1>[] performMap(ExecutorService executor) {

    final LinkedList<Future<Collection<Pair<K1, V1>>>> items = new LinkedList<Future<Collection<Pair<K1, V1>>>>();

    final Iterator<Pair<K, V>> it = _job.getInput();
    while (it.hasNext()) {
      final Pair<K, V> item = it.next();
      final Mapper<K, V, K1, V1> mapper = _job.newMappper();
      items.add(executor.submit(new MapTask(mapper, item)));
    }

    final LinkedList<Pair<K1, V1>> results = new LinkedList<Pair<K1, V1>>();
    for (Future<Collection<Pair<K1, V1>>> item : items) {
      try {
        final Collection<Pair<K1, V1>> i = item.get();
        results.addAll(i);
      } catch (ExecutionException e) {
        throw new RuntimeException(e);
      } catch (InterruptedException e) {
        throw new RuntimeException(e);
      }
    }

    return (Pair<K1, V1>[]) results.toArray(new Pair[0]);
  }

  private Collection<Pair<K1, Collection<V1>>> collect(ExecutorService executor, Pair<K1, V1>[] items) {
    sort(executor, items);

    final Comparator<K1> comparator = _job.getComparator();

    K1 key;
    int from = 0;
    int to = 0;
    while (from < items.length) {
      if (key == null || (to < items.length && comparator.compare(key, items[to].a()) == 0)) {
        to++;
        continue;
      }

      Arrays.copyOfRange(items, from, to);
    }
  }

  private Iterable<Pair<K1, V2>> performReduce(ExecutorService executor, Collection<Pair<K1, Collection<V1>>> items) {
    final ArrayList<Future<Collection<Pair<K1, V2>>>> futures = new ArrayList<Future<Collection<Pair<K1, V2>>>>(items.size());
    for (Pair<K1, Collection<V1>> item : items) {
      final Reducer<K1, V1, V2> reducer = _job.newReducer();
      futures.add(executor.submit(new ReduceTask(reducer, item)));
    }

    final LinkedList<Pair<K1, V2>> results = new LinkedList<Pair<K1, V2>>();
    for (Future<Collection<Pair<K1, V2>>> f : futures) {
      final Collection<Pair<K1, V2>> r = f.get();
      results.addAll(r);
    }
    return results;
  }

  private final class MapTask implements Callable<Collection<Pair<K1, V1>>> {

    private final Mapper<K, V, K1, V1> _mapper;
    private final Pair<K, V> _item;

    public MapTask(Mapper<K, V, K1, V1> mapper, Pair<K, V> item) {
      _mapper = mapper;
      _item = item;
    }

    @Override
    public Collection<Pair<K1, V1>> call() throws Exception {
      final Emitter<Pair<K1, V1>> emitter = new Emitter<Pair<K1, V1>>();
      _mapper.map(_item.a(), _item.b(), emitter);
      return emitter.values();
    }

  }
}
