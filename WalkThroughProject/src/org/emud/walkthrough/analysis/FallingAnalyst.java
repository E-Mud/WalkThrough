package org.emud.walkthrough.analysis;

import org.emud.walkthrough.model.Result;

public class FallingAnalyst implements Analyst {
	OnFallDetectedListener listener;
	
	public FallingAnalyst(OnFallDetectedListener lstner){
		listener = lstner;
	}

	@Override
	public void analyzeNewData(WalkData walkData) {
		// TODO Auto-generated method stub
	}

	@Override
	public Result getResult() {
		return null;
	}

}
