package org.emud.walkthrough.analysis;

import java.util.ArrayList;


public abstract class WalkDataReceiver {
	protected ArrayList<OnDataReceivedListener> listeners;
	
	/**
	 * Constructor de WalkDataReceiver.
	 * @param context Context en el que se ejecutará el receptor.
	 */
	public WalkDataReceiver(){
		listeners = new ArrayList<OnDataReceivedListener>();
	}

	/**
	 * Añadde un nuevo OnDataReceivedListener a la lista.
	 * @param listener 
	 */
	public void addOnDataReceivedListener(OnDataReceivedListener listener) {
		listeners.add(listener);
	}

	/**
	 * Elimina un OnDataReceivedListener de la lista.
	 * @param listener
	 */
	public void removeOnDataReceivedListener(OnDataReceivedListener listener) {
		listeners.remove(listener);
	}
	
	/**
	 * Comenzar a recibir datos.
	 */
	public abstract void startReceiving();
	
	/**
	 * Parar temporalmente de recibir datos.
	 */
	public abstract void pauseReceiving();
	
	/**
	 * Reanudar la recepción de datos.
	 */
	public abstract void resumeReceiving();
	
	/**
	 * Parar definitivamente la recepción de datos.
	 */
	public abstract void stopReceiving();
	
	/**
	 * Interfaz para los observadores de los datos recibidos.
	 * @author alberto
	 */
	public static interface OnDataReceivedListener{
		public void onDataReceveid(AccelerometerData accelerometerData);
	}
	
	/**
	 * @author alberto
	 *
	 */
	public static interface OnErrorReceivingListener{
		
	}

}
