package net.karlmartens.euler.p0006;

public class Solution01 {

	/**
	 * <p>
	 * The sum of the squares of the first ten natural numbers is,
	 * </p>
	 * 
	 * <p style="text-indent: 50px">
	 * 12 + 22 + ... + 102 = 385
	 * </p>
	 * 
	 * <p>
	 * The square of the sum of the first ten natural numbers is,
	 * </p>
	 * <p style="text-indent: 50px">
	 * (1 + 2 + ... + 10)2 = 552 = 3025
	 * </p>
	 * 
	 * <p>
	 * Hence the difference between the sum of the squares of the first ten
	 * natural numbers and the square of the sum is 3025 385 = 2640.
	 * </p>
	 * 
	 * <p>
	 * Find the difference between the sum of the squares of the first one
	 * hundred natural numbers and the square of the sum.
	 * <p>
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		final int n = 100;
		final int squareOfSums = (int) Math.pow(n / 2 * (1 + n), 2);
		final int sumOfSquares = n * (2 * n + 1) * (n + 1) / 6;
		final int result = squareOfSums - sumOfSquares;
		System.out.println(result);
	}

}
