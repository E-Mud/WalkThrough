package org.emud.walkthrough.analysis;


import android.content.Context;

public class DataReceiverBuilder {
	
	private DataReceiverBuilder(){
	}
	
	/**
	 * 
	 * @param context
	 * @param type
	 * @return
	 */
	public static WalkDataReceiver buildReceiver(Context context, int type){
		WalkDataReceiver receiver = null;
		//TODO
		switch(type){
		case WalkDataReceiver.SINGLE_ACCELEROMETER:
			receiver = new LinearAccelerometerReceiver(context);
			break;
		case WalkDataReceiver.TWO_ACCELEROMETERS:
			break;
		default:
			break;
		}
		
		return receiver;
	}
}
