package org.emud.walkthrough.analysis;

import java.util.ArrayList;
import java.util.List;

import org.emud.walkthrough.model.Result;

public class AnalysisStation implements WalkDataReceiver.OnDataReceivedListener{
	private ArrayList<Analyst> analysts;
	private WalkDataReceiver walkDataReceiver;
	
	public AnalysisStation(WalkDataReceiver dr, ArrayList<Analyst> listAnalysts){
		walkDataReceiver = dr;
		analysts = new ArrayList<Analyst>(listAnalysts);
	}
	
	public void startAnalysis(){
		walkDataReceiver.startReceiving();
	}
	
	public void pauseAnalysis(){
		walkDataReceiver.pauseReceiving();
	}
	
	public void resumeAnalysis(){
		walkDataReceiver.resumeReceiving();
	}
	
	public void stopAnalysis(){
		walkDataReceiver.stopReceiving();
	}
	
	public List<Result> collectResults(){
		ArrayList<Result> results = new ArrayList<Result>();
		
		for(Analyst analyst : analysts)
			results.add(analyst.getResult());
		
		return results;
	}

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
