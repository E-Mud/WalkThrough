package org.emud.walkthrough.stub;

import org.emud.walkthrough.analysis.AnalysisStationBuilder;
import org.emud.walkthrough.analysis.Analyst;
import org.emud.walkthrough.cadence.CadenceAnalyst;
import org.emud.walkthrough.pedometer.StepsCounter;
import org.emud.walkthrough.resulttype.ResultType;
import org.emud.walkthrough.speedometer.Speedometer;

import android.content.Context;

public class DebugAnalysisStationBuilder extends AnalysisStationBuilder {
	private Context context;
	
	public DebugAnalysisStationBuilder(Context con){
		context = con;
	}

	@Override
	public Analyst buildAnalyst(int resultTypeInt, int receiverType) {
		ResultType resultType = ResultType.valueOf(resultTypeInt);
		switch(resultType){
		case RT_MAX_MOVE:
			return new MaxMoveAnalyst(context);
		case RT_STEPS:
			return new StepsCounter();
		case RT_SPEED:
			return new Speedometer();
		case RT_CADENCE:
			return new CadenceAnalyst();
		default:
			return null;
		}
	}
}
