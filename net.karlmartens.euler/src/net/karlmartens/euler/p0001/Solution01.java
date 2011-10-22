package net.karlmartens.euler.p0001;

import java.util.Arrays;


public final class Solution01 {

	public static void main(String[] args) {
		final int[] factors = new int[] { 3, 5};
		
		final boolean[] include = new boolean[1000];
		Arrays.fill(include, false);

		for (int f : factors) {
			for (int i=0; i<include.length; i+=f) {
				include[i] = true;
			}
		}
		
		int total = 0;
		for (int i=0; i<include.length; i++) {
			if (include[i]) {
				total += i;
			}
		}
		
		System.out.println(total);
	}

}
