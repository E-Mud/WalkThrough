package org.emud.walkthrough.speedometer;

import org.emud.walkthrough.analysis.AccelerometerData;
import org.emud.walkthrough.analysis.Analyst;
import org.emud.walkthrough.model.Result;

public class Speedometer implements Analyst {
	private double speedSum;
	private int nSamples;
	
	public Speedometer(){
		speedSum = 0.0d;
		nSamples = 0;
	}

	@Override
	public void analyzeNewData(AccelerometerData accelerometerData) {
		double forwardAcceleration = accelerometerData.getData()[0];
		
		if(nSamples >= 5){
			speedSum += 0.05*forwardAcceleration;
		}
		
		nSamples++;
	}

	@Override
	public Result getResult() {
		return new Speed(speedSum/(double)nSamples);
	}

}
