package org.emud.walkthrough.cadence;

import org.emud.walkthrough.analysis.AccelerometerData;
import org.emud.walkthrough.analysis.Analyst;
import org.emud.walkthrough.analysisservice.GaitCycle;
import org.emud.walkthrough.model.Result;
import org.emud.walkthrough.pedometer.Steps;
import org.emud.walkthrough.pedometer.Pedometer;

public class CadenceAnalyst implements Analyst {
	private static final double MIN_TIME = 0.25;
	private Pedometer pedometer;
	private long nSamples;
	private int ratio;
	
	public CadenceAnalyst(GaitCycle gaitCycle){
		pedometer = new Pedometer(gaitCycle);
		nSamples = 0;
		ratio = -1;
	}

	@Override
	public void analyzeNewData(AccelerometerData accelerometerData) {
		pedometer.analyzeNewData(accelerometerData);
		if(accelerometerData.getLocation() != AccelerometerData.LOCATION_LEFT_ANKLE)
			nSamples++;
		if(ratio == -1)
			ratio = accelerometerData.getRatio();
	}

	@Override
	public Result getResult() {
		double cadence;
		double analysisTime = (nSamples * ratio) / 60000.0;

		if(analysisTime >= MIN_TIME){
			double samplesPerMin = 60000/(double)ratio,
					stepsPerSample = ((Steps) pedometer.getResult()).getSteps()/(double)nSamples;
			cadence = samplesPerMin * stepsPerSample;
			return new Cadence(cadence);
		}else{
			return null;
		}		
	}

}
