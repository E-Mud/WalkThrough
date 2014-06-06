package org.emud.walkthrough.pedometer;

import java.util.Arrays;

public class MedianFilter {
	private double[] data;
	private int size;
	
	public MedianFilter(int size){
		this.size = size;
		data = new double[size];
	}
	
	public double applyFilter(double newValue){
		double[] sortedData;
		
		for(int i=0; i<size-1; i++)
			data[i] = data[i+1];
		data[size-1] = newValue;
		
		sortedData = Arrays.copyOf(data, size);
		Arrays.sort(sortedData);
		
		return sortedData[size/2];
	}
}
