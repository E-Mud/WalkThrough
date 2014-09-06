package org.emud.walkthrough.analysis;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
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
		List<Analyst> analystList;
		List<Filter> filterList;

		filterList = buildFilterList(receiverType, resultsTypes);
		analystList = buildAnalystList(receiverType, resultsTypes);
		
		if(filterList == null){
			analysisStation = new AnalysisStation(dataReceiver, analystList);
		}else{
			analysisStation = new AnalysisStation(dataReceiver, analystList, filterList);
		}
		
		dataReceiver.addOnDataReceivedListener(analysisStation);
		
		return analysisStation;
	}
	
	protected List<Analyst> buildAnalystList(int receiverType, Set<Integer> resultsTypes){
		ArrayList<Analyst> analystList = new ArrayList<Analyst>();
		
		for(Integer resultType : resultsTypes){
			Analyst analyst = buildAnalyst(resultType, receiverType);
			if(analyst != null)
				analystList.add(analyst);
		}
		
		return analystList;
	}
	
	protected List<Filter> buildFilterList(int receiverType, Set<Integer> resultsTypes){
		return null;
	}
	
	public abstract Analyst buildAnalyst(int resultType, int receiverType);
}
