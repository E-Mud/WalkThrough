package org.emud.walkthrough.analysis;

import java.util.ArrayList;
import java.util.Set;

import org.emud.walkthrough.model.Result;
import org.emud.walkthrough.stub.MaxMoveAnalyst;

import android.content.Context;

public class AnalysisStationBuilder {
	
	private AnalysisStationBuilder(){
	}
	
	public static AnalysisStation buildStation(Context context, int receiverType, Set<Integer> resultsTypes){
		WalkDataReceiver dataReceiver;
		AnalysisStation analysisStation;
		ArrayList<Analyst> analystList = new ArrayList<Analyst>();
		
		dataReceiver = DataReceiverBuilder.buildReceiver(context, receiverType);
		
		for(Integer resultType : resultsTypes){
			switch(resultType.intValue()){
			case Result.RT_MAX_MOVE:
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
