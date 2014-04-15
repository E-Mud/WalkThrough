package org.emud.walkthrough.analysis;

import java.util.ArrayList;
import java.util.Set;

import org.emud.walkthrough.analysis.DataReceiverBuilder.ReceiverType;
import org.emud.walkthrough.model.Result.ResultType;
import org.emud.walkthrough.stub.MaxMoveAnalyst;

public class AnalysisStationBuilder {
	
	private AnalysisStationBuilder(){
	}
	
	public static AnalysisStation buildStation(ReceiverType receiverType, Set<ResultType> resultsTypes){
		WalkDataReceiver dataReceiver;
		AnalysisStation analysisStation;
		ArrayList<Analyst> analystList = new ArrayList<Analyst>();
		
		dataReceiver = DataReceiverBuilder.buildReceiver(receiverType);
		
		for(ResultType resultType : resultsTypes){
			switch(resultType){
			case MAX_MOVE:
				analystList.add(new MaxMoveAnalyst()); 
				break;
			default:
				break;
			}
		}
		
		analysisStation = new AnalysisStation(dataReceiver, analystList);
		dataReceiver.addOnDataReceveidListener(analysisStation);
		
		return analysisStation;
	}

}
