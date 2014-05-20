package org.emud.walkthrough.analysisservice;

import org.emud.walkthrough.analysis.AccelerometerData;
import org.emud.walkthrough.analysis.WalkDataReceiver;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;


/**
 * Receptor de datos para el acelerómetro del teléfono movil.
 * @author alberto
 *
 */
public class LinearAccelerometerReceiver extends WalkDataReceiver implements SensorEventListener{
	private static final int RATIO = 220000;
	private SensorManager sensorManager;
	private Sensor accelerometer;
	private double[] gravity;
	private Context context;
	
	public LinearAccelerometerReceiver(Context cont){
		context = cont;
		gravity = new double[3];
	}

	@Override
	public void startReceiving() {
		sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
	    accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
	    resumeReceiving();
	}

	@Override
	public void pauseReceiving() {
		sensorManager.unregisterListener(this);
	}

	@Override
	public void resumeReceiving() {
		sensorManager.registerListener(this, accelerometer, RATIO);
	}

	@Override
	public void stopReceiving() {
		pauseReceiving();
	}

	@Override
	public void onAccuracyChanged(Sensor arg0, int arg1) {
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
		
		double aux = data[2];
		data[2] = data[0];
		data[0] = -aux;
		
		AccelerometerData accelerometerData = new AccelerometerData(data, event.timestamp, RATIO);
		
		for(OnDataReceivedListener listener : listeners)
			listener.onDataReceveid(accelerometerData);
	}

}
