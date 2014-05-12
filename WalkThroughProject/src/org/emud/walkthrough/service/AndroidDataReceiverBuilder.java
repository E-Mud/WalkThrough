package org.emud.walkthrough.service;

import org.emud.walkthrough.analysis.DataReceiverBuilder;
import org.emud.walkthrough.analysis.WalkDataReceiver;

import android.content.Context;

public class AndroidDataReceiverBuilder implements DataReceiverBuilder{
	private Context context;
	
	public AndroidDataReceiverBuilder(Context con){
		context = con;
	}
	
	@Override
	public WalkDataReceiver buildReceiver(int type){
		WalkDataReceiver receiver = null;

		switch(type){
		case WalkDataReceiver.SINGLE_ACCELEROMETER:
			receiver = new LinearAccelerometerReceiver(context);
			break;
		case WalkDataReceiver.TWO_ACCELEROMETERS:
			//TODO
			break;
		default:
			break;
		}
		
		return receiver;
	}
}
