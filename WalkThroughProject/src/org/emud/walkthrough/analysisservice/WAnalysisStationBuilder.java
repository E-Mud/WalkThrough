package org.emud.walkthrough.analysisservice;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.emud.walkthrough.analysis.Analyst;
import org.emud.walkthrough.analysis.Filter;
import org.emud.walkthrough.analysis.StationBuilder;
import org.emud.walkthrough.cadence.CadenceAnalyst;
import org.emud.walkthrough.pedometer.Pedometer;
import org.emud.walkthrough.regularity.RegularityAnalyst;
import org.emud.walkthrough.resulttype.ResultType;
import org.emud.walkthrough.speedometer.Speedometer;


public class WAnalysisStationBuilder extends StationBuilder {
	private GaitCycle gaitCycle;
	
	public WAnalysisStationBuilder(int receiverType){
		gaitCycle = new GaitCycle(receiverType);
	}

	@Override
	protected List<Filter> buildFilterList(int resultTypeInt, Set<Integer> receiverType){
		ArrayList<Filter> list = new ArrayList<Filter>();
		list.add(gaitCycle);
		
		return list;
	}
	
	@Override
	public Analyst buildAnalyst(int resultTypeInt, int receiverType) {
		ResultType resultType = ResultType.valueOf(resultTypeInt);
		switch(resultType){
		case RT_REGULARITY:
			return new RegularityAnalyst();
		case RT_STEPS:
			return new Pedometer(gaitCycle);
		case RT_SPEED:
			return new Speedometer();
		case RT_CADENCE:
			return new CadenceAnalyst(gaitCycle);
		default:
			return null;
		}
	}
}
