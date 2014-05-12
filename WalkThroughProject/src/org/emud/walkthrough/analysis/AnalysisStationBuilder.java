package org.emud.walkthrough.analysis;

import java.util.ArrayList;
import java.util.Set;

import org.emud.walkthrough.model.Result;
import org.emud.walkthrough.pedometer.StepsCounter;
import org.emud.walkthrough.stub.MaxMoveAnalyst;

public class AnalysisStationBuilder {
	
	private AnalysisStationBuilder(){
	}
	
	
	/**
	 * Construye un nuevo AnalysisStationBuilder según las especificaciones. 
	 * @param context Context en el que se ejecutará el análisis.
	 * @param receiverType Tipo del receptor de datos.
	 * @param resultsTypes Lista de tipos de resultados a obtener.
	 * @return AnalysisStation inicializado.
	 */
	public static AnalysisStation buildStation(WalkDataReceiver dataReceiver, int receiverType, Set<Integer> resultsTypes){
		AnalysisStation analysisStation;
		ArrayList<Analyst> analystList = new ArrayList<Analyst>();
		
		for(Integer resultType : resultsTypes){
			switch(resultType.intValue()){
			case Result.RT_MAX_MOVE:
				analystList.add(new MaxMoveAnalyst()); 
				break;
			case Result.RT_STEPS:
				analystList.add(new StepsCounter()); 
				break;
			default:
				break;
			}
		}
		
		analysisStation = new AnalysisStation(dataReceiver, analystList);
		dataReceiver.addOnDataReceveidListener(analysisStation);
		
		return analysisStation;
	}

	public static AnalysisStation buildFallingDetector(WalkDataReceiver dataReceiver, OnFallDetectedListener listener){
		AnalysisStation analysisStation;
		ArrayList<Analyst> analystList = new ArrayList<Analyst>();
		
		analystList.add(new FallingAnalyst(listener));
		analysisStation = new AnalysisStation(dataReceiver, analystList);
		dataReceiver.addOnDataReceveidListener(analysisStation);
		
		return analysisStation;
	}
}
