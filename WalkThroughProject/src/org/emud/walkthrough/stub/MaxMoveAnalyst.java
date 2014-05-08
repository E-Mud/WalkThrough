package org.emud.walkthrough.stub;

import org.emud.walkthrough.analysis.Analyst;
import org.emud.walkthrough.analysis.AccelerometerData;
import org.emud.walkthrough.model.Result;

public class MaxMoveAnalyst implements Analyst {
	private AccelerometerData max;
	
	public MaxMoveAnalyst(){
		max = new AccelerometerData(new double[]{0,0,0}, 0);
	}

	@Override
	public void analyzeNewData(AccelerometerData accelerometerData) {
		double absValueMax = getAbsoluteValue(max.getData());
		double absValueNew = getAbsoluteValue(accelerometerData.getData());
		
		if(absValueNew > absValueMax)
			max = accelerometerData;
	}

	@Override
	public Result getResult() {
		double maxMove = getAbsoluteValue(max.getData());
		
		return new ResultMaxMove(Double.valueOf(maxMove));
	}

	private double getAbsoluteValue(double[] ds) {
		double value = 0;
		
		for(int i=0; i<3; i++)
			value += ds[i]*ds[i];
		
		value = Math.sqrt(value);
		
		return value;
	}

}
