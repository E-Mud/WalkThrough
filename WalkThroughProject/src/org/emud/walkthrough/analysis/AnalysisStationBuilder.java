package org.emud.walkthrough.analysis;

import java.util.ArrayList;
import java.util.Set;

import org.emud.walkthrough.model.Result;
import org.emud.walkthrough.stub.MaxMoveAnalyst;

import android.content.Context;

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

	public static AnalysisStation buildFallingDetector(Context context, OnFallDetectedListener listener){
		WalkDataReceiver dataReceiver;
		AnalysisStation analysisStation;
		ArrayList<Analyst> analystList = new ArrayList<Analyst>();
		
		dataReceiver = DataReceiverBuilder.buildReceiver(context, WalkDataReceiver.SINGLE_ACCELEROMETER);
		analystList.add(new FallingAnalyst(listener));
		analysisStation = new AnalysisStation(dataReceiver, analystList);
		dataReceiver.addOnDataReceveidListener(analysisStation);
		
		return analysisStation;
	}
}
