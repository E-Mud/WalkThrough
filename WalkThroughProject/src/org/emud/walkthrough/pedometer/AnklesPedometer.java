package org.emud.walkthrough.pedometer;

import org.emud.walkthrough.analysis.AccelerometerData;
import org.emud.walkthrough.analysis.Analyst;
import org.emud.walkthrough.analysisservice.GaitCycle;
import org.emud.walkthrough.model.Result;

public class AnklesPedometer implements Analyst {
	private Pedometer rightAnkle, leftAnkle;
	
	public AnklesPedometer(GaitCycle gaitCycle){
		rightAnkle = new Pedometer(gaitCycle);
		leftAnkle = new Pedometer(gaitCycle);
	}

	@Override
	public void analyzeNewData(AccelerometerData accelerometerData) {
		if(accelerometerData.getLocation() == AccelerometerData.LOCATION_RIGHT_ANKLE){
			rightAnkle.analyzeNewData(accelerometerData);
		}else{
			if(accelerometerData.getLocation() == AccelerometerData.LOCATION_LEFT_ANKLE)
				leftAnkle.analyzeNewData(accelerometerData);
		}
	}

	@Override
	public Result getResult() {
		int leftAnkleSteps = ((Steps) leftAnkle.getResult()).getSteps(),
				rightAnkleSteps = ((Steps) rightAnkle.getResult()).getSteps();
		
		return new Steps(rightAnkleSteps + leftAnkleSteps);
	}

}
