package net.karlmartens.platform.util;

import java.util.BitSet;

public final class ArraySupport {

	public static int[] minus(int[] first, int[] second) {
		final BitSet set = new BitSet();
		for (int i : first)
			set.set(i);
		
		for (int i : second)
			set.flip(i);
		
		final int[] result = new int[set.cardinality()];
		int i=0;
		for (int value=set.nextSetBit(0); value>=0; value=set.nextSetBit(value+1))
			result[i++] = value;
		
		return result;
	}
	
}
