package net.karlmartens.euler.p0007;

import java.util.BitSet;

public class Solution01 {

	/**
	 * <p>
	 * By listing the first six prime numbers: 2, 3, 5, 7, 11, and 13, we can
	 * see that the 6th prime is 13.
	 * </p>
	 * 
	 * <p>
	 * What is the 10 001st prime number?
	 * </p>
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		final BitSet primes = new BitSet();
		primes.set(0);
		primes.set(1);

		final int bound = 110000;
		int count = 0;
		int prime = 0;
		for (;;) {
			prime = primes.nextClearBit(prime + 1);
			if (prime >= bound) {
				throw new RuntimeException("Out of Bounds Exception");
			}
			count++;
			if (count == 10001)
				break;

			final int first = prime + prime;
			for (int i = first; i < bound; i += prime) {
				primes.set(i);
			}
		}

		System.out.println(prime);
	}

}
