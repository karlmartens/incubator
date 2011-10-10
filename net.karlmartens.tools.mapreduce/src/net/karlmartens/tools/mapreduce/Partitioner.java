package net.karlmartens.tools.mapreduce;

public interface Partitioner<T> {

	int getPartition(T key, int numReducers);
	
}
