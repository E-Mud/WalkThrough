package org.emud.walkthrough.length;

import java.util.ArrayList;

import org.emud.walkthrough.analysis.AccelerometerData;
import org.emud.walkthrough.analysis.Analyst;
import org.emud.walkthrough.analysisservice.GaitCycle;
import org.emud.walkthrough.model.Result;
import java.lang.Math;

public class MeanLengthAnalyst implements Analyst {
	private double userLegLength;
	private GaitCycle gaitCycle;
	private ArrayList<AccelerometerData> samples;
	private ArrayList<Integer> cyclesIndex;
	
	public MeanLengthAnalyst(GaitCycle cycle){
		this(cycle, 0);
	}
	
	public MeanLengthAnalyst(GaitCycle cycle, double legLength){
		userLegLength = legLength;
		gaitCycle = cycle;
		samples = new ArrayList<AccelerometerData>();
		cyclesIndex = new ArrayList<Integer>();
	}

	@Override
	public void analyzeNewData(AccelerometerData accelerometerData) {
		if(accelerometerData.getLocation() == AccelerometerData.LOCATION_LEFT_ANKLE)
			return;
		
		samples.add(accelerometerData);
		
		if(gaitCycle.cycleCompleted(accelerometerData.getLocation()))
			cyclesIndex.add(samples.size()-1);
	}

	@Override
	public Result getResult() {
		double meanLength = calculateLength();
		
		return new Length(meanLength);
	}
	
	private double[] integration(double[] ar){
		int n = ar.length;
		double[] res = new double[n];
		
		res[0] = 0.005*ar[0];
		
		for(int i=1; i<n; i++){
			res[i] = res[i-1] + 0.005*ar[i];
		}
		
		return res;
	}
	
	private double calculateLength(){
		if(samples.isEmpty())
			return 0;
		
		int n = samples.size(), m = cyclesIndex.size();
		double[] accArray = new double[n];
		double meanLength = 0;
		
		if(samples.get(0).getLocation() == AccelerometerData.LOCATION_TRUNK){
			for(int i=0; i<n; i++){
				accArray[i] = samples.get(i).getData()[1];
			}
			
			accArray = integration(integration(accArray));
			
			for(int i=1; i<m-1; i++){
				double h = calculateDifference(accArray, cyclesIndex.get(i), cyclesIndex.get(i+1));
				meanLength += 2*Math.sqrt(2*h*userLegLength - h*h);
			}
			
			return meanLength/(double)m;
		}else{
			for(int i=0; i<n; i++)
				accArray[i] = samples.get(i).getData()[0];
			
			accArray = integration(accArray);
			return accArray[n-1]/(double)m;
		}
	}

	private double calculateDifference(double[] data, int start, int end){
		double max = 0, min = 100;
		
		for(int i=start; i<end; i++){
			if(data[i] > max)
				max = data[i];
			if(data[i] < min)
				min = data[i];
		}
		
		return max-min;
	}
}
