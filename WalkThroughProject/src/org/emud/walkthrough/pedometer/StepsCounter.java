package org.emud.walkthrough.pedometer;

import org.emud.walkthrough.analysis.AccelerometerData;
import org.emud.walkthrough.analysis.Analyst;
import org.emud.walkthrough.model.Result;

public class StepsCounter implements Analyst {
	private static final int FILTER_SIZE = 5;
	private MedianFilter medianFilter;
	private int nSamples, sign, peaksCount;
	private double currentPeakValue;
	
	public StepsCounter(){
		medianFilter = new MedianFilter(FILTER_SIZE);
		nSamples = 0;
	}

	@Override
	public void analyzeNewData(AccelerometerData accelerometerData) {
		double filteredAtp = medianFilter.applyFilter(accelerometerData.getData()[0]);
		
		nSamples++;
		
		if(nSamples > FILTER_SIZE/2 ){
			checkSign(filteredAtp);
			
			if(sign == 1 && filteredAtp > currentPeakValue)
				currentPeakValue = filteredAtp;
		}
	}

	private void checkSign(double currentValue) {
		switch(sign){
		case 0:
			if(currentValue < 0)
				sign = -1;
			break;
		case 1:
			if(currentValue < 0){
				sign = -1;
				peaksCount++;
				currentPeakValue = -1;
			}
			break;
		case -1:
			if(currentValue > 0)
				sign = 1;
			break;
		}
	}

	@Override
	public Result getResult() {
		return new StepsCount(peaksCount);
	}

}