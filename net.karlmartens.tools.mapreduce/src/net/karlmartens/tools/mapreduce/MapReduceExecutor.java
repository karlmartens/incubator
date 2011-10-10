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
import net.karlmartens.platform.util.PairKeyComparator;

public final class MapReduceExecutor<K, V, K1, V1, V2> implements
		Callable<Iterable<Pair<K1, V2>>> {

	private final MapReduceJob<K, V, K1, V1, V2> _job;

	public MapReduceExecutor(MapReduceJob<K, V, K1, V1, V2> job) {
		_job = job;
	}

	@Override
	public Collection<Pair<K1, V2>> call() throws Exception {

		final int threads = Runtime.getRuntime().availableProcessors();
		final ExecutorService executor = Executors.newFixedThreadPool(threads);

		try {
			final Collection<Pair<K1, V1>> mapResults = performMap(executor);
			Collection<Pair<K1, V1>>[] buckets = performPartition(mapResults);
			return performReduce(executor, buckets);
		} finally {
			executor.shutdown();
		}
	}

	public static <K, V, K1, V1, V2> Collection<Pair<K1, V2>> run(
			MapReduceJob<K, V, K1, V1, V2> job) throws Exception {
		return new MapReduceExecutor<K, V, K1, V1, V2>(job).call();
	}

	private Collection<Pair<K1, V1>> performMap(ExecutorService executor) {

		final LinkedList<Future<Collection<Pair<K1, V1>>>> items = new LinkedList<Future<Collection<Pair<K1, V1>>>>();

		final Iterator<Pair<K, V>> it = _job.getInput();
		while (it.hasNext()) {
			final Pair<K, V> item = it.next();
			final Mapper<K, V, K1, V1> mapper = _job.newMappper();
			items.add(executor.submit(new MapTask(mapper, item)));
		}

		final ArrayList<Pair<K1, V1>> results = new ArrayList<Pair<K1, V1>>(items.size());
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

		return results;
	}

	private Collection<Pair<K1, V1>>[] performPartition(Collection<Pair<K1, V1>> items) {
		final int count = items.size() / 8;
		@SuppressWarnings("unchecked")
		final Collection<Pair<K1, V1>>[] buckets = new Collection[count];
		for (int i = 0; i < buckets.length; i++) {
			buckets[i] = new ArrayList<Pair<K1, V1>>();
		}
		
		Partitioner<K1> partitioner = _job.getPartitioner();
		for (Pair<K1, V1> item : items) {
			final int partition = partitioner.getPartition(item.a(), count);
			buckets[partition].add(item);
		}
		
		return buckets;
	}

	private Collection<Pair<K1, V2>> performReduce(ExecutorService executor,
			Collection<Pair<K1, V1>>[] items) {
		final ArrayList<Future<Collection<Pair<K1, V2>>>> futures = new ArrayList<Future<Collection<Pair<K1, V2>>>>();
		for (Collection<Pair<K1, V1>> item : items) {
			if (items.length <= 0)
				continue;
			
			final Reducer<K1, V1, V2> reducer = _job.newReducer();
			futures.add(executor.submit(new ReduceTask(reducer, _job.getComparator(), item)));
		}

		final LinkedList<Pair<K1, V2>> results = new LinkedList<Pair<K1, V2>>();
		for (Future<Collection<Pair<K1, V2>>> f : futures) {
			try {
				final Collection<Pair<K1, V2>> r = f.get();
				results.addAll(r);
			} catch (ExecutionException e) {
				throw new RuntimeException(e);
			} catch (InterruptedException e) {
				throw new RuntimeException(e);
			}
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

	private final class ReduceTask implements
			Callable<Collection<Pair<K1, V2>>> {

		private final Reducer<K1, V1, V2> _reducer;
		private final Comparator<K1> _comparator;
		private final Pair<K1, V1>[] _items;

		@SuppressWarnings("unchecked")
		public ReduceTask(Reducer<K1, V1, V2> reducer,
				Comparator<K1> comparator, Collection<Pair<K1, V1>> item) {
			_reducer = reducer;
			_comparator = comparator;
			_items = item.toArray(new Pair[0]);
		}

		@Override
		public Collection<Pair<K1, V2>> call() throws Exception {			
			Arrays.sort(_items, new PairKeyComparator<K1, V1>(_comparator));
			
			final Emitter<Pair<K1, V2>> emitter = new Emitter<Pair<K1, V2>>();
			K1 key = null;
			Collection<V1> group = new ArrayList<V1>();
			for (int i=0; i<_items.length; i++) {
				final Pair<K1, V1> candidate = _items[i];
				if (_comparator.compare(key, candidate.a()) != 0) {
					reduce(key, group, emitter);
					
					key = candidate.a();
					group.clear();
				}
				
				group.add(candidate.b());
			}
			
			reduce(key, group, emitter);
			
			return emitter.values();
		}

		private void reduce(K1 key, Collection<V1> values,
				Emitter<Pair<K1, V2>> emitter) {
			if (key == null)
				return;

			_reducer.reduce(key, values, emitter);
		}

	}

}
