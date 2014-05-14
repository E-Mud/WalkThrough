package org.emud.walkthrough.analysisservice;

import org.emud.walkthrough.analysis.AnalysisStationBuilder;
import org.emud.walkthrough.analysis.Analyst;
import org.emud.walkthrough.model.Result;
import org.emud.walkthrough.pedometer.StepsCounter;
import org.emud.walkthrough.stub.MaxMoveAnalyst;

public class WAnalysisStationBuilder extends AnalysisStationBuilder {

	@Override
	public Analyst buildAnalyst(int resultType, int receiverType) {
		switch(resultType){
		case Result.RT_MAX_MOVE:
			return new MaxMoveAnalyst();
		case Result.RT_STEPS:
			return new StepsCounter();
		default:
			return null;
		}
	}
}
