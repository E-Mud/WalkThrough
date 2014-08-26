package org.emud.walkthrough.analysisservice;

import org.emud.walkthrough.analysis.StationBuilder;
import org.emud.walkthrough.analysis.Analyst;
import org.emud.walkthrough.cadence.CadenceAnalyst;
import org.emud.walkthrough.pedometer.Pedometer;
import org.emud.walkthrough.regularity.RegularityAnalyst;
import org.emud.walkthrough.resulttype.ResultType;
import org.emud.walkthrough.speedometer.Speedometer;

public class WAnalysisStationBuilder extends StationBuilder {

	@Override
	public Analyst buildAnalyst(int resultTypeInt, int receiverType) {
		ResultType resultType = ResultType.valueOf(resultTypeInt);
		switch(resultType){
		case RT_REGULARITY:
			return new RegularityAnalyst();
		case RT_STEPS:
			return new Pedometer();
		case RT_SPEED:
			return new Speedometer();
		case RT_CADENCE:
			return new CadenceAnalyst();
		default:
			return null;
		}
	}
}
