package org.emud.walkthrough.analysisservice;

import org.emud.walkthrough.analysis.WalkDataReceiver;

import android.content.Context;

public class AndroidDataReceiverBuilder{
	private Context context;
	
	public AndroidDataReceiverBuilder(Context con){
		context = con;
	}
	
	public WalkDataReceiver buildReceiver(int type){
		WalkDataReceiver receiver = null;

		switch(type){
		case AnalysisService.SINGLE_ACCELEROMETER:
			receiver = new LinearAccelerometerReceiver(context);
			break;
		case AnalysisService.TWO_ACCELEROMETERS:
			//TODO
			break;
		default:
			break;
		}
		
		return receiver;
	}
}
