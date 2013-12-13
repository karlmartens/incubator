package net.karlmartens.tools.mapreduce;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.BreakIterator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.Locale;
import java.util.NoSuchElementException;
import java.util.TreeSet;

import net.karlmartens.platform.function.Pair;
import net.karlmartens.platform.util.ArraySupport;
import net.karlmartens.platform.util.PairKeyComparator;
import net.karlmartens.platform.util.StringComparator;
import net.karlmartens.tools.testing.TestSummarizer;

import org.junit.Test;

public final class MapReduceTest {

	@Test
	public void testMapReduceWordCount() throws Exception {
		@SuppressWarnings("unchecked")
		final Iterable<Pair<String, String>> input = Arrays.asList(//
				Pair.of("document", "A few words of wisdom."), //
				Pair.of("document", "A few more words of wisdom."));

		final MapReduceJob<String, String, String, Integer, Integer> job = MapReduceJob
				.of(WordMapper.class, //
						SumReducer.class, //
						new StringComparator(true), //
						input);

		Collection<Pair<String, Integer>> a = new TreeSet<Pair<String, Integer>>(new PairKeyComparator<String, Integer>(new StringComparator(false)));
		a.addAll(MapReduceExecutor.run(job));

		final Collection<Pair<?,?>> actuals = new ArrayList<Pair<?,?>>();
		actuals.addAll(a);

		final TestSummarizer summarizer = new TestSummarizer()//
				.expected(//
						"a 2", //
						"few 2", //
						"more 1", //
						"of 2", //
						"wisdom 2", //
						"words 2");
		summarize(actuals, summarizer);
		summarizer.check();
	}

	@Test
	public void testMapReduceAverageWordCount() throws Exception {
		@SuppressWarnings("unchecked")
		final Iterable<Pair<String, String>> input = Arrays.asList(//
				Pair.of("document", "A few words of wisdom."), //
				Pair.of("document", "A few more words of wisdom."));

		final MapReduceJob<String, String, String, Integer, Integer> job = MapReduceJob
				.of(WordMapper.class, //
						SumReducer.class, //
						new StringComparator(true), //
						input);

		final MapReduceJob<String, Integer, String, Integer, Double> job2 = MapReduceJob
				.of(NoneMapper.class, //
						AverageReducer.class, //
						new StringComparator(true), //
						new KeyedInputReader("document", MapReduceExecutor.run(job)));
		
		final Collection<Pair<?,?>> actuals = new ArrayList<Pair<?,?>>();
		actuals.addAll(MapReduceExecutor.run(job2));
		
		final TestSummarizer summarizer = new TestSummarizer()//
				.expected("document       0.55");
		summarize(actuals, summarizer);
		summarizer.check();
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testLargeMapReduceWordCount() throws Exception {
		Iterable<Pair<String, String>> input = new FileInputReader("pg2600.in.txt");
		final MapReduceJob<String, String, String, Integer, Integer> job = MapReduceJob
				.of(WordMapper.class, //
						SumReducer.class, //
						new StringComparator(false), //
						input);

		final Pair<String, Integer>[] actuals = MapReduceExecutor.run(job).toArray(new Pair[0]);
		ArraySupport.sort(actuals, new PairKeyComparator<String, Integer>(new StringComparator(false)));

		final TestSummarizer summarizer = new TestSummarizer();
		readExpected("pg2600.out.txt", summarizer);
		summarize(actuals, summarizer);
		summarizer.check();
	}
	
	private static void readExpected(String resource, TestSummarizer summarizer) {
		final BufferedReader in = new BufferedReader(new InputStreamReader(MapReduceTest.class.getResourceAsStream(resource)));
		try {
			String line = null;
			while ((line = in.readLine()) != null) {
				summarizer.expected(line);
			}
		} catch (IOException e)  { 
			throw new RuntimeException(e);
		} finally {
			try {
				in.close();
			} catch (IOException e) {
				// Ignore
			}
		}
	}

	private static void summarize(Pair<?, ?>[] items, TestSummarizer summarizer) {
		summarize(Arrays.asList(items), summarizer);
	}

	private static void summarize(Iterable<Pair<?, ?>> items, TestSummarizer summarizer) {
		for (Pair<?, ?> item : items) {
			final String format = new StringBuilder() //
				.append(getFormat(1, item.a())) //
				.append(" ") //
				.append(getFormat(2, item.b())) //
				.toString();
			
			summarizer.actual(format, item.a(), item.b());
		}
	}
	
	private static String getFormat(int index, Object o) {
		final String format;
		if (o instanceof Double) {
			format = "10.2f";
		} else if (o instanceof Integer) {
			format ="d";
		} else {
			format = "s";
		}

		return new StringBuilder() //
			.append("%") //
			.append(index) //
			.append("$") //
			.append(format) //
			.toString();
	}

	public static class NoneMapper implements Mapper<String, Integer, String, Integer> {

		@Override
		public void map(String key, Integer value,
				Emitter<Pair<String, Integer>> emitter) {
			emitter.emit(Pair.of(key, value));
		}
		
	}

	public static class WordMapper implements
			Mapper<String, String, String, Integer> {

		@Override
		public void map(String key, String value,
				Emitter<Pair<String, Integer>> emitter) {
			final BreakIterator it = BreakIterator
					.getWordInstance(Locale.CANADA);
			it.setText(value);

			int from = it.first();
			int to = it.next();
			while (to != BreakIterator.DONE) {
				final String word = value.substring(from, to);
				if (Character.isLetterOrDigit(word.charAt(0))) {
					emitter.emit(Pair.of(word.toLowerCase(), Integer.valueOf(1)));
				}
				from = to;
				to = it.next();
			}
		}

	}

	private static class SumReducer implements
			Reducer<String, Integer, Integer> {

		@Override
		public void reduce(String key, Iterable<Integer> values,
				Emitter<Pair<String, Integer>> emitter) {
			int total = 0;
			for (Integer v : values) {
				total += v.intValue();
			}
			emitter.emit(Pair.of(key, total));
		}

	}
	
	private static class AverageReducer implements Reducer<String, Integer, Double> {

		@Override
		public void reduce(String key, Iterable<Integer> values,
				Emitter<Pair<String, Double>> emitter) {
			int uWords = 0;
			int tWords = 0;
			for (Integer v : values) {
				uWords++;
				tWords += v.intValue();
			}
			emitter.emit(Pair.of(key, (double)uWords / (double)tWords));
		}
		
	}
	
	private static class FileInputReader implements Iterable<Pair<String, String>> {
		
		private final String _resource;

		public FileInputReader(String filename) {
			_resource = filename;
		}

		@Override
		public Iterator<Pair<String, String>> iterator() {
			final BufferedReader in = new BufferedReader(new InputStreamReader(MapReduceTest.class.getResourceAsStream(_resource)));
			return new Iterator<Pair<String,String>>() {
				private String _next = null;
				private boolean _closed = false;
				
				@Override
				public boolean hasNext() {
					if (_next == null)
						forward();
					
					return _next != null;
				}

				@Override
				public Pair<String, String> next() {
					if (!hasNext())
						throw new NoSuchElementException();
					
					try {
						return Pair.of(_resource, _next);
					} finally {
						_next = null;
					}
				}

				@Override
				public void remove() {
					 throw new UnsupportedOperationException();
				}

				private void forward() {
					if (!_closed ) {
						try {
							_next = in.readLine();
							if (_next == null) {
								close();
							}
						} catch (IOException e) {
							close();
						}
					}
				}

				private void close() {
					try {
						in.close();
					} catch (IOException e) {
						// Ignore
					} finally {
						_closed = true;
					}
				}
			};
		}
		
	}
	
	private static class KeyedInputReader implements Iterable<Pair<String, Integer>> {

		private final String _document;
		private final Collection<Pair<String, Integer>> _data;

		public KeyedInputReader(String document,
				Collection<Pair<String, Integer>> data) {
			_document = document;
			_data = data;
		}

		@Override
		public Iterator<Pair<String, Integer>> iterator() {
			final Iterator<Pair<String, Integer>> it = _data.iterator();
			return new Iterator<Pair<String,Integer>>() {

				@Override
				public boolean hasNext() {
					return it.hasNext();
				}

				@Override
				public Pair<String, Integer> next() {
					final Pair<String, Integer> orig = it.next();
					return Pair.of(_document, orig.b());
				}

				@Override
				public void remove() {
					throw new UnsupportedOperationException();
				}
			};
		}
		
	}
}
