package net.karlmartens.euler.p0010;

import java.util.BitSet;

public class Solution01 {

	/**
	 * <p>
	 * The sum of the primes below 10 is 2 + 3 + 5 + 7 = 17.
	 * </p>
	 * 
	 * <p>
	 * Find the sum of all the primes below two million.
	 * </p>
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		final BitSet primes = generatePrimes(2000001);
		long sum = 0;
		for (int i=primes.nextSetBit(0); i>=0; i = primes.nextSetBit(i+1)) {
			sum += i;
		}
		System.out.println(sum);
	}

	private static BitSet generatePrimes(int toIndex) {
		final BitSet primes = new BitSet();
		primes.set(2, toIndex);
		int n = 0;
		for(;;) {
			n = primes.nextSetBit(n+1);
			final int first = n + n;
			if (first >= toIndex)
				return primes;
			
			for (int i=first; i<toIndex; i+=n) {
				primes.clear(i);
			}
		}
	}

}
