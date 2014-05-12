package org.emud.walkthrough.pedometer;

import org.emud.walkthrough.analysis.AccelerometerData;
import org.emud.walkthrough.analysis.Analyst;
import org.emud.walkthrough.model.Result;

public class StepsCounter implements Analyst {
	private int sign, peaksCount;
	private double currentPeakValue, currentValue;
	
	public StepsCounter(){
		sign = peaksCount = 0;
		currentValue = currentPeakValue = -1;
	}

	@Override
	public void analyzeNewData(AccelerometerData accelerometerData) {
		currentValue = accelerometerData.getData()[0];
		checkSign();
		

		if(sign == 1 && currentValue > currentPeakValue)
			currentPeakValue = currentValue;

	}

	private void checkSign() {
		switch(sign){
		case 0:
			if(currentValue != 0)
				sign = currentValue > 0 ? 1 : -1;
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
