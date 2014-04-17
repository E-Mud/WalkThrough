package org.emud.walkthrough.analysis;

import org.emud.walkthrough.stub.FalseReceiver;

import android.content.Context;

public class DataReceiverBuilder {
	
	private DataReceiverBuilder(){
	}
	
	public static WalkDataReceiver buildReceiver(Context context, int type){
		WalkDataReceiver receiver = null;
		//TODO
		switch(type){
		case WalkDataReceiver.SINGLE_ACCELEROMETER:
			receiver = new LinearAccelerometerReceiver(context);
			break;
		case WalkDataReceiver.TWO_ACCELEROMETERS:
			break;
		case WalkDataReceiver.GUI_RECEIVER:
			receiver = new FalseReceiver();
			break;
		default:
			break;
		}
		
		return receiver;
	}
}
