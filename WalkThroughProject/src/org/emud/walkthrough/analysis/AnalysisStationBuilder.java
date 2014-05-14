package org.emud.walkthrough.analysis;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import org.emud.walkthrough.fallingdetection.FallingAnalyst;
import org.emud.walkthrough.fallingdetection.OnFallDetectedListener;

public abstract class AnalysisStationBuilder {
	/**
	 * Construye un nuevo AnalysisStationBuilder según las especificaciones.
	 * @param context Context en el que se ejecutará el análisis.
	 * @param receiverType Tipo del receptor de datos.
	 * @param resultType Tipo de resultado a obtener.
	 * @return AnalysisStation inicializado.
	 */
	public AnalysisStation buildStation(DataReceiverBuilder dataReceiverBuilder, int receiverType, int resultType){
		Set<Integer> set = new HashSet<Integer>();
		set.add(Integer.valueOf(resultType));
		
		return buildStation(dataReceiverBuilder, receiverType, set);
	}
	
	/**
	 * Construye un nuevo AnalysisStationBuilder según las especificaciones. 
	 * @param context Context en el que se ejecutará el análisis.
	 * @param receiverType Tipo del receptor de datos.
	 * @param resultsTypes Lista de tipos de resultados a obtener.
	 * @return AnalysisStation inicializado.
	 */
	public AnalysisStation buildStation(DataReceiverBuilder dataReceiverBuilder, int receiverType, Set<Integer> resultsTypes){
		AnalysisStation analysisStation;
		ArrayList<Analyst> analystList = new ArrayList<Analyst>();
		WalkDataReceiver dataReceiver;
		
		for(Integer resultType : resultsTypes){
			Analyst analyst = buildAnalyst(resultType, receiverType);
			if(analyst != null)
				analystList.add(analyst);
		}
		
		dataReceiver = dataReceiverBuilder.buildReceiver(receiverType);
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
