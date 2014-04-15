package org.emud.walkthrough.stub;

import java.util.ArrayList;

import org.emud.walkthrough.analysis.WalkData;
import org.emud.walkthrough.analysis.WalkDataReceiver;

public class FalseReceiver implements WalkDataReceiver {
	private ArrayList<OnDataReceivedListener> listeners;
	
	public FalseReceiver(){
		listeners = new ArrayList<OnDataReceivedListener>();
	}

	@Override
	public void addOnDataReceveidListener(OnDataReceivedListener listener) {
		listeners.add(listener);
	}

	@Override
	public void removeOnDataReceivedListener(OnDataReceivedListener listener) {
		// TODO Auto-generated method stub

	}

	@Override
	public void startReceiving() {
		// TODO Auto-generated method stub

	}

	@Override
	public void pauseReceiving() {
		// TODO Auto-generated method stub

	}

	@Override
	public void resumeReceiving() {
		// TODO Auto-generated method stub

	}

	@Override
	public void stopReceiving() {
		// TODO Auto-generated method stub

	}
	
	public void getData(long timestamp, double[] data){
		WalkData walkData = new WalkData(data, timestamp);
		
		for(OnDataReceivedListener listener : listeners)
			listener.onDataReceveid(walkData);
	}

}
