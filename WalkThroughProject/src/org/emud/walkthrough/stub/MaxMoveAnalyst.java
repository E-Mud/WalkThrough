package org.emud.walkthrough.stub;

import java.util.ArrayList;

import org.emud.walkthrough.analysis.Analyst;
import org.emud.walkthrough.analysis.WalkData;
import org.emud.walkthrough.model.Result;

public class MaxMoveAnalyst implements Analyst {
	private WalkData max;
	
	public MaxMoveAnalyst(){
		max = new WalkData(new double[]{0,0,0}, 0);
	}

	@Override
	public void analyzeNewData(WalkData walkData) {
		double absValueMax = getAbsoluteValue(max.getData());
		double absValueNew = getAbsoluteValue(walkData.getData());
		
		if(absValueNew > absValueMax)
			max = walkData;
	}

	@Override
	public Result getResult() {
		double maxMove = getAbsoluteValue(max.getData());
		
		return new Result(Double.valueOf(maxMove));
	}

	private double getAbsoluteValue(double[] ds) {
		double value = 0;
		
		for(int i=0; i<3; i++)
			value += ds[i]*ds[i];
		
		value = Math.sqrt(value);
		
		return value;
	}

}
