package net.karlmartens.euler.p0001;

import java.util.Arrays;
import java.util.Collection;

import net.karlmartens.platform.util.ComparableComparator;
import net.karlmartens.platform.util.Pair;
import net.karlmartens.tools.mapreduce.Emitter;
import net.karlmartens.tools.mapreduce.MapReduceExecutor;
import net.karlmartens.tools.mapreduce.MapReduceJob;
import net.karlmartens.tools.mapreduce.Mapper;
import net.karlmartens.tools.mapreduce.Reducer;

public final class Solution02 {
	
	@SuppressWarnings("unchecked")
	public static void main(String[] args) throws Exception {
		final Collection<Pair<Integer, Integer>> sums = MapReduceExecutor.run(MapReduceJob.of( //
				Series.class, //
				SumInt.class, //
				new ComparableComparator<Integer>(), //
				Arrays.asList(//
						Pair.of(3, 1000), //
						Pair.of(5, 1000), //
						Pair.of(15, 1000)) //
				));
		
		final Collection<Pair<String, Integer>> answers = MapReduceExecutor.run(MapReduceJob.of(//
				Translate.class, //
				SumString.class, //
				new ComparableComparator<String>(), //
				sums));
		
		for (Pair<String, Integer> a : answers) {
			System.out.println(a.b());
		}
	}
	
	public static class Series implements Mapper<Integer, Integer, Integer, Integer> {
		@Override
		public void map(Integer key, Integer value,
				Emitter<Pair<Integer, Integer>> emitter) {
			for (int i=0; i<value.intValue(); i+=key.intValue()) {
				emitter.emit(Pair.of(key, i));
			}
		}
	}
	
	public static class Translate implements Mapper<Integer, Integer, String, Integer> {
		@Override
		public void map(Integer key, Integer value,
				Emitter<Pair<String, Integer>> emitter) {
			Integer result = value;
			if (key.intValue() == 15) {
				result *= -1;
			}
			emitter.emit(Pair.of("answer", result));
		}		
	}
	
	private static class Sum<K> implements Reducer<K, Integer, Integer> {
		@Override
		public final void reduce(K key, Iterable<Integer> values,
				Emitter<Pair<K, Integer>> emitter) {
			int total = 0;
			for (Integer v : values) {
				total += v.intValue();
			}
			emitter.emit(Pair.of(key, total));
		}
	}
	
	public static class SumInt extends Sum<Integer> {
		// Nothing to do
	}
	
	public static class SumString extends Sum<String> {
		// Nothing to do
	}
}
