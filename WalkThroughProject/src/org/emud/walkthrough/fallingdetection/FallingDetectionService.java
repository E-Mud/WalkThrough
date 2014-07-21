package org.emud.walkthrough.fallingdetection;

import org.emud.walkthrough.R;
import org.emud.walkthrough.ServiceMessageHandler;
import org.emud.walkthrough.ServiceMessageHandler.OnMessageReceivedListener;
import org.emud.walkthrough.analysis.AnalysisStation;
import org.emud.walkthrough.analysis.StationBuilder;
import org.emud.walkthrough.analysis.WalkDataReceiver;
import org.emud.walkthrough.analysisservice.LinearAccelerometerReceiver;

import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract.Data;
import android.provider.ContactsContract.RawContacts;
import android.support.v4.app.NotificationCompat;
import android.telephony.SmsManager;

public class FallingDetectionService extends Service implements OnMessageReceivedListener, OnFallDetectedListener {
	public static final int SERVICE_OFF = 0, SERVICE_ON = 1;
	public static final String CONTACT_ID_KEY = "emergencyContactID", START_MESSENGER = "startResponseMessenger";
	private AnalysisStation station;
	private Messenger messenger;
	private long emergencyContactID;
	private int currentState;
	
	@Override
	public void onCreate(){
		StationBuilder stationBuilder = new FallingStationBuilder(this);
		int receiverType = WalkDataReceiver.SINGLE_ACCELEROMETER;
		WalkDataReceiver receiver = new LinearAccelerometerReceiver(this);
		
		station = stationBuilder.buildStation(receiver, receiverType, 0);
		messenger = new Messenger(new ServiceMessageHandler(this));		
		currentState = SERVICE_OFF;
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId){
		android.util.Log.d("FDS", "onStartCommand");

		Messenger responseMessenger = intent.getParcelableExtra(START_MESSENGER);
		Message msg = Message.obtain(null, ServiceMessageHandler.MSG_START, currentState, 0);
		try {
			responseMessenger.send(msg);
		} catch (RemoteException e) {
			e.printStackTrace();
		}
		
		return START_STICKY;
	}
	

    @Override
    public IBinder onBind(Intent intent) {
		android.util.Log.d("FDS", "onBind");
        if(messenger != null){
    		android.util.Log.d("FDS", "onBind not null");
        	return messenger.getBinder();
        }else{
    		android.util.Log.d("FDS", "onBind null");
        	return null;
        }
    }

    /*@Override
    public boolean onUnbind(Intent intent){
    	if(currentState == SERVICE_OFF)
    		stopSelf();
    	
    	return false;
    }*/
    
	@Override
	public void onStartMessage(Message msg) {
		emergencyContactID = msg.getData().getLong(CONTACT_ID_KEY);
		currentState = SERVICE_ON;
		station.startAnalysis();
	}

	private void log(String string) {
		android.util.Log.d("FlngService", string);
	}


	@Override
	public void onPauseMessage(Message msg) {
	}

	@Override
	public void onResumeMessage(Message msg) {
	}

	@Override
	public void onStopMessage(Message msg) {
		currentState = SERVICE_OFF;
		station.stopAnalysis();
	}

	@Override
	public void onStateMessage(Message msg) {
		Message msgResponse = Message.obtain(null, ServiceMessageHandler.MSG_STATE, 0, 0);
		Bundle bundle = new Bundle();
		
		bundle.putInt(ServiceMessageHandler.STATE_KEY, currentState);
		msgResponse.setData(bundle);
		
		try {
			msg.replyTo.send(msgResponse);
		} catch (RemoteException e) {
			e.printStackTrace();
		}		
	}
	
	@Override
	public void fallDetected() {
		sendSMS();
		notificateUser();
	}


	private void notificateUser() {
		NotificationCompat.Builder mBuilder =
		        new NotificationCompat.Builder(this)
		        .setSmallIcon(R.drawable.ic_notification_icon)
		        .setContentTitle(getResources().getString(R.string.falling_notificationtitle))
		        .setContentText(getResources().getString(R.string.falling_notificationtext));

		NotificationManager mNotificationManager =
		    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		
		mNotificationManager.notify(0, mBuilder.build());
	}

	private void sendSMS(){
		String phoneNumber = getPhoneNumber();
		String fallingSmsText = getResources().getString(R.string.falling_smstext);
		
		SmsManager smsManager = SmsManager.getDefault();
		log(phoneNumber);
		smsManager.sendTextMessage(phoneNumber, null, fallingSmsText, null, null);
	}

	private String getPhoneNumber() {
		String phone;

	    Cursor cursor = getContentResolver().query(Data.CONTENT_URI,
	            new String[] {Phone.NUMBER},
	            RawContacts.CONTACT_ID + " = " + emergencyContactID  + " AND "
	                    + Data.MIMETYPE + "='" + Phone.CONTENT_ITEM_TYPE + "'", null, null);

	    boolean empty = cursor.moveToFirst(); 
	    log("empty: " + !empty);
	    phone = cursor.getString(cursor.getColumnIndex(Phone.NUMBER));
	    log("phone number: " + (phone == null));
	    
	    do{
	    	log("phone numbers: " + cursor.getString(cursor.getColumnIndex(Phone.NUMBER)));
	    }while(cursor.moveToNext());
	    cursor.close();
	    
	    return phone;
	}

}
