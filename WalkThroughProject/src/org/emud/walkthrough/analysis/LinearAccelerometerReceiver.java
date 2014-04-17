package org.emud.walkthrough.analysis;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;


public class LinearAccelerometerReceiver extends WalkDataReceiver implements SensorEventListener{
	private SensorManager sensorManager;
	private Sensor accelerometer;
	private double[] gravity;
	
	public LinearAccelerometerReceiver(Context context){
		super(context);
		gravity = new double[3];
	}

	@Override
	public void startReceiving() {
		sensorManager = (SensorManager) getContext().getSystemService(Context.SENSOR_SERVICE);
	    accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
	    android.util.Log.e("WTF???", "" + (accelerometer == null));
	    resumeReceiving();
	}

	@Override
	public void pauseReceiving() {
		sensorManager.unregisterListener(this);
	}

	@Override
	public void resumeReceiving() {
		sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
	}

	@Override
	public void stopReceiving() {
		pauseReceiving();
	}

	@Override
	public void onAccuracyChanged(Sensor arg0, int arg1) {
		// TODO Auto-generated method stub
	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		final float alpha = 0.9f;
		int n = event.values.length;
		double[] data = new double[n];
		

		gravity[0] = alpha * gravity[0] + (1 - alpha) * event.values[0];
		gravity[1] = alpha * gravity[1] + (1 - alpha) * event.values[1];
		gravity[2] = alpha * gravity[2] + (1 - alpha) * event.values[2];

		data[0] = event.values[0] - gravity[0];
		data[1] = event.values[1] - gravity[1];
		data[2] = event.values[2] - gravity[2];
		
		/*for(int i=0; i<n; i++)
			data[i] = event.values[i];*/
		
		WalkData walkData = new WalkData(data, event.timestamp);
		
		for(OnDataReceivedListener listener : listeners)
			listener.onDataReceveid(walkData);
	}

}