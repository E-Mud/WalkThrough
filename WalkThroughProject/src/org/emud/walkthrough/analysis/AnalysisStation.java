package org.emud.walkthrough.analysis;

import java.util.ArrayList;
import java.util.List;

import org.emud.walkthrough.model.Result;

public class AnalysisStation implements WalkDataReceiver.OnDataReceivedListener{
	private ArrayList<Analyst> analysts;
	private WalkDataReceiver walkDataReceiver;
	
	/**
	 * Constructor de AnalysisStation. Se recomienda usar la clase estatica AnalysisStationBuilder en su lugar.
	 * @param dr Receptor de datos.
	 * @param listAnalysts Lista de Analyst que realizarán los análisis a partir de los datos recibidos.
	 */
	public AnalysisStation(WalkDataReceiver dr, ArrayList<Analyst> listAnalysts){
		walkDataReceiver = dr;
		analysts = new ArrayList<Analyst>(listAnalysts);
	}
	
	/**
	 * Comenzar el análisis.
	 * 
	 */
	public void startAnalysis(){
		walkDataReceiver.startReceiving();
	}
	
	/**
	 * Pausar el análisis.
	 */
	public void pauseAnalysis(){
		walkDataReceiver.pauseReceiving();
	}
	
	/**
	 * Reanudar el análisis.
	 */
	public void resumeAnalysis(){
		walkDataReceiver.resumeReceiving();
	}
	
	/**
	 * Parar definitivamente el análisis.
	 */
	public void stopAnalysis(){
		walkDataReceiver.stopReceiving();
	}
	
	/**
	 * Devuelve los resultados de los análisis. No es necesario que el análisis esté pausado o detenido pero se recomienda llamara a está función en uno de esos estados para evitar interferencias con el análisis.
	 * @return Lista con los resultados obtenidos hasta el momento.
	 */
	public List<Result> collectResults(){
		ArrayList<Result> results = new ArrayList<Result>();
		
		for(Analyst analyst : analysts)
			results.add(analyst.getResult());
		
		return results;
	}

	/**
	 * @see org.emud.walkthrough.analysis.WalkDataReceiver.OnDataReceivedListener#onDataReceveid(org.emud.walkthrough.analysis.WalkData)
	 */
	@Override
	public void onDataReceveid(WalkData walkData) {
		for(Analyst analyst : analysts)
			analyst.analyzeNewData(walkData);
	}
	
	//XXX TESTING
	public WalkDataReceiver getDataReceiver(){
		return walkDataReceiver;
	}
	
	//XXX TESTING
	public List<Analyst> getAnalystList(){
		return analysts;
	}
}
