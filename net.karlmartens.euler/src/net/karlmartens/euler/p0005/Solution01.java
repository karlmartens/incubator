package net.karlmartens.euler.p0005;

import java.util.BitSet;

public class Solution01 {

	public static void main(String[] args) {
		final int max = 20;
		final BitSet primes = generatePrimes(max+1);
		final int limit = (int)Math.sqrt(max);
		final double lg = Math.log(max);
		int result = 1;
		for (int i = primes.nextSetBit(0); i >= 0; i = primes.nextSetBit(i+1)) {
			if (i > max)
				break;
			
			if (i <= limit) {
				result *= (int)Math.pow(i, (int)(lg / Math.log(i)));
			} else {
				result *= i;
			}
		}
		
		System.out.println(result);
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
