package org.emud.walkthrough.analysis;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;

import org.emud.walkthrough.model.Result;

public class AnalysisStation implements WalkDataReceiver.OnDataReceivedListener{
	private static enum AnalysisState{PREPARED,
		RUNNING,
		PAUSED,
		STOPPED};
		
	private ArrayList<Analyst> analysts;
	private WalkDataReceiver walkDataReceiver;
	private Dispatcher dispatcher;
	private LinkedBlockingQueue<AccelerometerData> dataQueue;
	private AnalysisState state;
	private List<Filter> filters;
	
	/**
	 * Constructor de AnalysisStation. Se recomienda usar la clase estatica StationBuilder en su lugar.
	 * @param dr Receptor de datos.
	 * @param analystList Lista de Analyst que realizarán los análisis a partir de los datos recibidos.
	 */
	public AnalysisStation(WalkDataReceiver dr, List<Analyst> analystList){
		walkDataReceiver = dr;
		analysts = new ArrayList<Analyst>(analystList);
		dataQueue = new LinkedBlockingQueue<AccelerometerData>();
		filters = new ArrayList<Filter>();
		state = AnalysisState.PREPARED;
	}
	
	/**
	 * Constructor de AnalysisStation. Se recomienda usar la clase estatica StationBuilder en su lugar.
	 * @param dr Receptor de datos.
	 * @param analystList Lista de Analyst que realizarán los análisis a partir de los datos recibidos.
	 */
	public AnalysisStation(WalkDataReceiver dr, List<Analyst> analystList, List<Filter> filterList){
		walkDataReceiver = dr;
		analysts = new ArrayList<Analyst>(analystList);
		dataQueue = new LinkedBlockingQueue<AccelerometerData>();
		filters = new ArrayList<Filter>(filterList);
		state = AnalysisState.PREPARED;
	}
	
	private void dispatch(AccelerometerData accData) {
		AccelerometerData data = accData;
		
		for(Filter filter : filters){
			data = filter.filter(data);
			if(data == null)
				return;
		}
		
		for(Analyst analyst : analysts)
			analyst.analyzeNewData(data);
	}
	
	/**
	 * Comenzar el análisis.
	 * 
	 */
	public void startAnalysis(){
		if(state == AnalysisState.PREPARED){
			dispatcher = new Dispatcher(this, dataQueue);
			walkDataReceiver.startReceiving();
			state = AnalysisState.RUNNING;
			dispatcher.start();
		}
	}
	
	/**
	 * Pausar el análisis.
	 */
	public void pauseAnalysis(){
		if(state == AnalysisState.RUNNING){
			walkDataReceiver.pauseReceiving();
			state = AnalysisState.PAUSED;
		}
	}
	
	/**
	 * Reanudar el análisis.
	 */
	public void resumeAnalysis(){
		if(state == AnalysisState.PREPARED){
			startAnalysis();
		}else{
			if(state == AnalysisState.PAUSED){
				walkDataReceiver.resumeReceiving();
				state = AnalysisState.RUNNING;
			}
		}
	}
	
	/**
	 * Parar definitivamente el análisis.
	 */
	public void stopAnalysis(){
		if(state == AnalysisState.RUNNING || state == AnalysisState.PAUSED){
			walkDataReceiver.stopReceiving();
			dispatcher.interrupt();
			state = AnalysisState.STOPPED;
		}
	}
	
	/**
	 * Devuelve los resultados de los análisis. No es necesario que el análisis esté pausado o detenido pero se recomienda llamara a está función en uno de esos estados para evitar interferencias con el análisis.
	 * @return Lista con los resultados obtenidos hasta el momento.
	 */
	public List<Result> collectResults(){
		ArrayList<Result> results = new ArrayList<Result>();
		
		for(Analyst analyst : analysts){
			Result result = analyst.getResult();
			if(result != null)
				results.add(result);
		}
		
		return results;
	}

	/**
	 * @see org.emud.walkthrough.analysis.WalkDataReceiver.OnDataReceivedListener#onDataReceveid(org.emud.walkthrough.analysis.AccelerometerData)
	 */
	@Override
	public void onDataReceveid(AccelerometerData accelerometerData) {
		if(state != AnalysisState.RUNNING)
			return;
		try {
			dataQueue.put(accelerometerData);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	private static class Dispatcher extends Thread{
		private boolean exitCorrect;
		private LinkedBlockingQueue<AccelerometerData> dataQueue;
		private AnalysisStation analysisStation;
		
		public Dispatcher(AnalysisStation station, LinkedBlockingQueue<AccelerometerData> queue){
			analysisStation = station;
			dataQueue = queue;
		}
		
		@Override
		public void interrupt(){
			exitCorrect = true;
			super.interrupt();
		}
		
		@Override
		public void run() {
			AccelerometerData data;
			exitCorrect=false;
			try{
				while(true){
					data = dataQueue.take();
					analysisStation.dispatch(data);
				}
			}catch(InterruptedException e){
				if(!exitCorrect)
					e.printStackTrace();					
			}
		}		
	}
}
