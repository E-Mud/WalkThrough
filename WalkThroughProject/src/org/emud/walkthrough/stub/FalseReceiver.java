package org.emud.walkthrough.stub;

import java.util.ArrayList;

import org.emud.walkthrough.analysis.AccelerometerData;
import org.emud.walkthrough.analysis.WalkDataReceiver;

public class FalseReceiver extends WalkDataReceiver {
	
	public FalseReceiver(){
		super(null);
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
		AccelerometerData accelerometerData = new AccelerometerData(data, timestamp);
		
		for(OnDataReceivedListener listener : listeners)
			listener.onDataReceveid(accelerometerData);
	}

}
