package net.karlmartens.euler.p0001;

import java.util.Arrays;

public final class Solution01 {

	/**
	 * <p>
	 * If we list all the natural numbers below 10 that are multiples of 3 or 5,
	 * we get 3,5,6 and 9. The sum of these multiples is 23.
	 * </p>
	 * 
	 * <p>
	 * Find the sum of all the multiples of 3 or 5 below 1000.
	 * </p>
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		final int[] factors = new int[] { 3, 5 };

		final boolean[] include = new boolean[1000];
		Arrays.fill(include, false);

		for (int f : factors) {
			for (int i = 0; i < include.length; i += f) {
				include[i] = true;
			}
		}

		int total = 0;
		for (int i = 0; i < include.length; i++) {
			if (include[i]) {
				total += i;
			}
		}

		System.out.println(total);
	}

}
