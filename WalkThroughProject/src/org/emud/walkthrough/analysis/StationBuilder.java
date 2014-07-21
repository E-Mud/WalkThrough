package org.emud.walkthrough.analysis;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import org.emud.walkthrough.fallingdetection.FallingAnalyst;
import org.emud.walkthrough.fallingdetection.OnFallDetectedListener;

public abstract class StationBuilder {
	/**
	 * Construye un nuevo StationBuilder según las especificaciones.
	 * @param context Context en el que se ejecutará el análisis.
	 * @param receiverType Tipo del receptor de datos.
	 * @param resultType Tipo de resultado a obtener.
	 * @return AnalysisStation inicializado.
	 */
	public AnalysisStation buildStation(WalkDataReceiver dataReceiver, int receiverType, int resultType){
		Set<Integer> set = new HashSet<Integer>();
		set.add(Integer.valueOf(resultType));
		
		return buildStation(dataReceiver, receiverType, set);
	}
	
	/**
	 * Construye un nuevo StationBuilder según las especificaciones. 
	 * @param context Context en el que se ejecutará el análisis.
	 * @param receiverType Tipo del receptor de datos.
	 * @param resultsTypes Lista de tipos de resultados a obtener.
	 * @return AnalysisStation inicializado.
	 */
	public AnalysisStation buildStation(WalkDataReceiver dataReceiver, int receiverType, Set<Integer> resultsTypes){
		AnalysisStation analysisStation;
		ArrayList<Analyst> analystList = new ArrayList<Analyst>();
		
		for(Integer resultType : resultsTypes){
			Analyst analyst = buildAnalyst(resultType, receiverType);
			if(analyst != null)
				analystList.add(analyst);
		}
		
		analysisStation = new AnalysisStation(dataReceiver, analystList);
		dataReceiver.addOnDataReceivedListener(analysisStation);
		
		return analysisStation;
	}

	public AnalysisStation buildFallingDetector(WalkDataReceiver dataReceiver, OnFallDetectedListener listener){
		AnalysisStation analysisStation;
		ArrayList<Analyst> analystList = new ArrayList<Analyst>();
		
		analystList.add(new FallingAnalyst(listener));
		analysisStation = new AnalysisStation(dataReceiver, analystList);
		dataReceiver.addOnDataReceivedListener(analysisStation);
		
		return analysisStation;
	}
	
	public abstract Analyst buildAnalyst(int resultType, int receiverType);
}
