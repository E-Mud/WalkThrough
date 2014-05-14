package org.emud.walkthrough.analysisservice;

import org.emud.walkthrough.analysis.AnalysisStationBuilder;
import org.emud.walkthrough.analysis.Analyst;
import org.emud.walkthrough.pedometer.StepsCounter;
import org.emud.walkthrough.resulttype.ResultType;
import org.emud.walkthrough.speedometer.Speedometer;
import org.emud.walkthrough.stub.MaxMoveAnalyst;

public class WAnalysisStationBuilder extends AnalysisStationBuilder {

	@Override
	public Analyst buildAnalyst(int resultTypeInt, int receiverType) {
		ResultType resultType = ResultType.valueOf(resultTypeInt);
		switch(resultType){
		case RT_MAX_MOVE:
			return new MaxMoveAnalyst();
		case RT_STEPS:
			return new StepsCounter();
		case RT_SPEED:
			return new Speedometer();
		default:
			return null;
		}
	}
}
