package net.karlmartens.tools.mapreduce;

public final class HashPartitioner<K1> implements Partitioner<K1> {

	@Override
	public int getPartition(K1 key, int numReducers) {
		return Math.abs(key.hashCode()) % numReducers;
	}

}
