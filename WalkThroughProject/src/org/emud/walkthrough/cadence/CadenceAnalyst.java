package org.emud.walkthrough.cadence;

import org.emud.walkthrough.analysis.AccelerometerData;
import org.emud.walkthrough.analysis.Analyst;
import org.emud.walkthrough.model.Result;
import org.emud.walkthrough.pedometer.StepsCount;
import org.emud.walkthrough.pedometer.StepsCounter;

public class CadenceAnalyst implements Analyst {
	private static final double MIN_TIME = 0.25;
	private StepsCounter pedometer;
	private long nSamples;
	private int ratio;
	
	public CadenceAnalyst(){
		pedometer = new StepsCounter();
		nSamples = 0;
		ratio = -1;
	}

	@Override
	public void analyzeNewData(AccelerometerData accelerometerData) {
		pedometer.analyzeNewData(accelerometerData);
		nSamples++;
		if(ratio == -1)
			ratio = accelerometerData.getRatio();
	}

	@Override
	public Result getResult() {
		double cadence;
		double analysisTime = (nSamples * ratio) / 60000000;
		
		if(analysisTime >= MIN_TIME){
			cadence = ((StepsCount) pedometer.getResult()).getSteps()/analysisTime;
		}else{
			cadence = 0;
		}
		
		return new Cadence(cadence);
	}

}
