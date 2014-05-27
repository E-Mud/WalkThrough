package org.emud.walkthrough.sensortag;

import org.emud.walkthrough.analysis.AccelerometerData;
import org.emud.walkthrough.analysis.WalkDataReceiver;
import org.emud.walkthrough.sensortag.SensorTag.NotificationListener;

public class SensorTagDataReceiver extends WalkDataReceiver implements
		NotificationListener {
	private static final int SAMPLE_PERIOD = 220;
	private volatile boolean running = false;
	private SensorTag sensorTag;
	
	public SensorTagDataReceiver(SensorTag sensor){
		sensorTag = sensor;
		sensorTag.setNotificationListener(this);
	}
	
	@Override
	public void onNotificationReceived(byte[] values) {
		if(!running)
			return;
		
		double[] dValues = new double[3];

		dValues[0] = values[0];
		dValues[1] = values[1];
		dValues[2] = values[2];
		
		AccelerometerData data = new AccelerometerData(dValues, 0, SAMPLE_PERIOD);
		
		for(WalkDataReceiver.OnDataReceivedListener listener : listeners)
			listener.onDataReceveid(data);
	}

	@Override
	public void startReceiving() {
		sensorTag.enableSensor();
		sensorTag.setPeriod(SAMPLE_PERIOD);
		sensorTag.setNotificationsEnabled(true);
		
		running = true;
	}

	@Override
	public void pauseReceiving() {
		running = false;
	}

	@Override
	public void resumeReceiving() {
		running = true;
	}

	@Override
	public void stopReceiving() {
		running = false;
		sensorTag.setNotificationsEnabled(false);
		sensorTag.close();
	}

}
