package org.emud.walkthrough;

import org.emud.walkthrough.fallingdetection.FallingDetectionService;

import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.preference.PreferenceManager;
import android.provider.ContactsContract.Contacts;
import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import android.widget.ToggleButton;

public class FallingDetectionActivity extends FragmentActivity implements OnClickListener {
	public static final String DEFAULT_CONTACT_ID = "defContactId";
	private ToggleButton onButton;
	private boolean bound;
	private long contactId;
	private ServiceConnection connection;
    private Messenger service;
    private Handler handler;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_falling_detection);
		
		onButton = (ToggleButton) findViewById(R.id.falling_detection_onoff);
		onButton.setOnClickListener(this);
		
		findViewById(R.id.falling_detection_changecontact).setOnClickListener(this);
		
		contactId = PreferenceManager.getDefaultSharedPreferences(this).getLong(DEFAULT_CONTACT_ID, -1);
		
		if(contactId >= 0){
            Cursor cursor;
            
            cursor = getContentResolver().query(Contacts.CONTENT_URI, new String[] {Contacts.DISPLAY_NAME},
            		Contacts._ID + " = " + contactId, null, null);
            cursor.moveToFirst();
            
			((TextView) findViewById(R.id.falling_detection_emergencycontactname)).setText((cursor.getString(cursor.getColumnIndex(Contacts.DISPLAY_NAME))));
            
            cursor.close();
        }
	
		bound = false;
		
		connection = new ServiceConnection() {
			public void onServiceConnected(ComponentName className, IBinder binder) {
				if(binder == null){
					log("not ServiceConnected");
					startFallingService();
				}else{
					log("ServiceConnected");
		            service = new Messenger(binder);
		            bound = true;
				}
	        }
			public void onServiceDisconnected(ComponentName className) {
	            service = null;
	            bound = false;
	        }
	    };

		startFallingService();
	}
	
	private void bindFallingService(){
		bindService(new Intent(this, FallingDetectionService.class), connection, 0);
	}
	

	private void log(String string) {
		android.util.Log.d("FDA", string);
	}
	
	@SuppressLint("HandlerLeak")
	private void startFallingService() {
		log("startFallingService");
    	Intent intentService = new Intent(this, FallingDetectionService.class);
    	handler = new Handler(){
    		@Override
            public void handleMessage(Message msg) {
                if(msg.what == ServiceMessageHandler.MSG_START){
            		log("bindFallingService from msg");
            		onButton.setChecked(msg.arg1 == FallingDetectionService.SERVICE_ON);
                	bindFallingService();
                }
            }
    	};
    	Messenger messenger = new Messenger(handler);
    	intentService.putExtra(FallingDetectionService.START_MESSENGER, messenger);
		startService(intentService);
	}


	@Override
	public void onPause(){
		super.onPause();
		
		if(bound){
			unbindService(connection);
			bound = false;
		}
	}
	
	@Override
	public void onDestroy(){
		if(bound)
			unbindService(connection);
		
		if(!onButton.isChecked()){
			Intent intent = new Intent(this, FallingDetectionService.class);
			stopService(intent);
		}
	
		super.onDestroy();
	}

	@Override
	public void onClick(View view) {
		if(view.getId() == onButton.getId()){
			if(onButton.isChecked()){
				turnServiceOn();
			}else{
				turnServiceOff();
			}
		}else{
			Intent intent = new Intent(Intent.ACTION_PICK, Contacts.CONTENT_URI);
			startActivityForResult(intent, 0);
		}
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK) {
			Uri contactUri = data.getData();
			String[] projection = {Contacts._ID, Contacts.DISPLAY_NAME, Contacts.HAS_PHONE_NUMBER};

			Cursor cursor = getContentResolver()
					.query(contactUri, projection, null, null, null);
			cursor.moveToFirst();

			boolean hasNumber = cursor.getInt(cursor.getColumnIndex(Contacts.HAS_PHONE_NUMBER)) > 0;

			if(hasNumber){
				contactId = cursor.getLong(cursor.getColumnIndex(Contacts._ID));
				Editor edit = PreferenceManager.getDefaultSharedPreferences(this).edit();
				
				((TextView) findViewById(R.id.falling_detection_emergencycontactname)).setText((cursor.getString(cursor.getColumnIndex(Contacts.DISPLAY_NAME))));
				
				edit.putLong(DEFAULT_CONTACT_ID, contactId);
				edit.commit();
			}
			
			cursor.close();
		}

	}

	private void turnServiceOn() {
		if(contactId != -1){
			try {
				Message msg = Message.obtain(null, ServiceMessageHandler.MSG_START, null);
				Bundle bundle = new Bundle();
				bundle.putLong(FallingDetectionService.CONTACT_ID_KEY, contactId);
				msg.setData(bundle);
				service.send(msg);
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		}else{
			//DIALOG
			onButton.setChecked(false);
		}
	}

	private void turnServiceOff() {
		try {
			service.send(Message.obtain(null, ServiceMessageHandler.MSG_STOP, null));
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}

}
