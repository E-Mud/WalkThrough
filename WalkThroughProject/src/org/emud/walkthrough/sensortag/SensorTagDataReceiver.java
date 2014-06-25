package org.emud.walkthrough.sensortag;

import org.emud.walkthrough.analysis.AccelerometerData;
import org.emud.walkthrough.analysis.WalkDataReceiver;
import org.emud.walkthrough.sensortag.SensorTag.NotificationListener;

public class SensorTagDataReceiver extends WalkDataReceiver implements
		NotificationListener {
	private static final int SAMPLE_PERIOD = 220;
	private volatile boolean running = false;
	private SensorTag firstSensorTag, secondSensorTag;
	
	public SensorTagDataReceiver(SensorTag firstSensor, SensorTag secondSensor){
		firstSensorTag = firstSensor;
		firstSensorTag.setNotificationListener(this);
		secondSensorTag = secondSensor;
		secondSensorTag.setNotificationListener(this);
	}
	
	@Override
	public void onNotificationReceived(SensorTag sensorTag, byte[] values) {
		if(!running)
			return;
		
		double[] dValues = new double[3];

		dValues[0] = values[0];
		dValues[1] = values[1];
		dValues[2] = values[2];
		
		AccelerometerData data = new AccelerometerData(dValues, 0, SAMPLE_PERIOD);
		
		if(sensorTag == firstSensorTag){
			data.setLocation(AccelerometerData.LOCATION_RIGHT_ANKLE);
		}else{
			data.setLocation(AccelerometerData.LOCATION_LEFT_ANKLE);			
		}
		
		for(WalkDataReceiver.OnDataReceivedListener listener : listeners)
			listener.onDataReceveid(data);
	}

	@Override
	public void startReceiving() {
		firstSensorTag.enableSensor();
		secondSensorTag.enableSensor();
		firstSensorTag.setPeriod(SAMPLE_PERIOD);
		secondSensorTag.setPeriod(SAMPLE_PERIOD);
		firstSensorTag.setNotificationsEnabled(true);
		secondSensorTag.setNotificationsEnabled(true);
		
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
		firstSensorTag.setNotificationsEnabled(false);
		secondSensorTag.setNotificationsEnabled(false);
		firstSensorTag.close();
		secondSensorTag.close();
	}

}
