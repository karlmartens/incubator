package net.karlmartens.euler.p0004;

import java.util.ArrayList;
import java.util.Collection;

import net.karlmartens.platform.util.ComparableComparator;
import net.karlmartens.platform.util.Pair;
import net.karlmartens.tools.mapreduce.Emitter;
import net.karlmartens.tools.mapreduce.MapReduceExecutor;
import net.karlmartens.tools.mapreduce.MapReduceJob;
import net.karlmartens.tools.mapreduce.Mapper;
import net.karlmartens.tools.mapreduce.Reducer;

public final class Solution01 {

	/**
	 * <p>
	 * A palindromic number reads the same both ways. The largest palindrome
	 * made from the product of two 2-digit numbers is 9009 = 91 99.
	 * </p>
	 * 
	 * <p>
	 * Find the largest palindrome made from the product of two 3-digit numbers.
	 * </p>
	 * 
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		final ArrayList<Pair<Integer, Integer>> input = new ArrayList<Pair<Integer, Integer>>();
		for (int i = 1; i < 10; i++) {
			input.add(Pair.of(100 * i, 100 * (i + 1)));
		}

		final Collection<Pair<String, Integer>> results = MapReduceExecutor
				.run(MapReduceJob.of(//
						MapperImpl.class, //
						ReducerImpl.class, //
						new ComparableComparator<String>(), input));

		for (Pair<String, Integer> r : results) {
			final Integer value = r.b();
			System.out.println(String.format("%1$3d", value));
		}
	}

	public static class MapperImpl implements
			Mapper<Integer, Integer, String, Integer> {
		@Override
		public void map(Integer key, Integer value,
				Emitter<Pair<String, Integer>> emitter) {
			for (int i = key.intValue(); i < 1000; i++) {
				for (int j = key.intValue(); j < value.intValue(); j++) {
					final int candidate = i * j;
					final String cStr = Integer.toString(candidate);
					final String rStr = new StringBuilder(cStr).reverse()
							.toString();
					if (cStr.equals(rStr)) {
						emitter.emit(Pair.of("answer", candidate));
					}
				}
			}
		}
	}

	public static class ReducerImpl implements
			Reducer<String, Integer, Integer> {
		@Override
		public void reduce(String key, Iterable<Integer> values,
				Emitter<Pair<String, Integer>> emitter) {
			int max = -1;
			for (Integer value : values) {
				final int candidate = value.intValue();
				if (max < candidate) {
					max = candidate;
				}
			}

			if (max >= 0) {
				emitter.emit(Pair.of(key, max));
			}
		}
	}

}
