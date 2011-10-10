package net.karlmartens.tools.mapreduce;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.BreakIterator;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.Locale;
import java.util.NoSuchElementException;
import java.util.TreeSet;

import net.karlmartens.platform.util.Pair;
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

		final Collection<Pair<String, Integer>> actuals = new TreeSet<Pair<String, Integer>>(new PairKeyComparator<String, Integer>(new StringComparator(false)));
		actuals.addAll(MapReduceExecutor.run(job));

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
	public void testLargeMapReduceWordCount() throws Exception {
		Iterable<Pair<String, String>> input = new FileInputReader("pg2600.in.txt");
		final MapReduceJob<String, String, String, Integer, Integer> job = MapReduceJob
				.of(WordMapper.class, //
						SumReducer.class, //
						new StringComparator(false), //
						input);

		final Collection<Pair<String, Integer>> actuals = new TreeSet<Pair<String, Integer>>(new PairKeyComparator<String, Integer>(new StringComparator(false)));
		actuals.addAll(MapReduceExecutor.run(job));

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

	private static void summarize(Iterable<Pair<String, Integer>> items, TestSummarizer summarizer) {
		for (Pair<String, Integer> item : items) {
			summarizer.actual("%1$s %2$d", item.a(), item.b());
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
}
