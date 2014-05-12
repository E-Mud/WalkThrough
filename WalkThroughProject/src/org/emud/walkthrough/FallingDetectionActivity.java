package org.emud.walkthrough;

import org.emud.walkthrough.analysis.ServiceMessageHandler;
import org.emud.walkthrough.service.FallingDetectionService;

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
import android.graphics.Color;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class FallingDetectionActivity extends FragmentActivity implements OnClickListener {
	public static final String DEFAULT_CONTACT_ID = null;
	private Button onButton;
	private boolean on;
	private boolean bound;
	private long contactId;
	private ServiceConnection connection;
    private Messenger service;
	
	private static final String SERVICE_ON_KEY = "on";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_falling_detection);
		
		onButton = (Button) findViewById(R.id.falling_detection_onoff);
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
		
		if(savedInstanceState != null){
			on = savedInstanceState.getBoolean(SERVICE_ON_KEY, false);
			setOnOffButtonAttrs();
		}
	
		bound = false;
		
		connection = new ServiceConnection() {
			public void onServiceConnected(ComponentName className, IBinder binder) {
				if(binder == null){
					startFallingService();
				}else{
		            service = new Messenger(binder);
		            bound = true;
		            on = true;
		            setOnOffButtonAttrs();
				}
	        }
			public void onServiceDisconnected(ComponentName className) {
	            service = null;
	            bound = false;
	        }
	    };
	    
	    bindFallingService();	    
	}
	
	private void bindFallingService(){
		bindService(new Intent(this, FallingDetectionService.class), connection, 0);
	}
	
    @SuppressLint("HandlerLeak")
	private void startFallingService() {
    	Intent intentService = new Intent(this, FallingDetectionService.class);
    	Handler handler = new Handler(){
    		@Override
            public void handleMessage(Message msg) {
                if(msg.what == ServiceMessageHandler.MSG_STOP){
                	bindFallingService();
                }
            }
    	};
    	Messenger messenger = new Messenger(handler);
    	intentService.putExtra(FallingDetectionService.START_MESSENGER, messenger);
		startService(intentService);
	}
	
	private void setOnOffButtonAttrs() {
		if(on){
			onButton.setBackgroundColor(Color.rgb(170, 0, 0));
			onButton.setText(R.string.fallingdetection_turnoff);
		}else{
			onButton.setBackgroundColor(Color.rgb(0, 170, 0));
			onButton.setText(R.string.fallingdetection_turnon);
		}
	}

	@Override
	public void onSaveInstanceState(Bundle saveInstanceState){
		super.onSaveInstanceState(saveInstanceState);
		
		saveInstanceState.putBoolean(SERVICE_ON_KEY, on);
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
		
		if(!on){
			Intent stopServiceIntent = new Intent(this, FallingDetectionService.class);
	        stopService(stopServiceIntent);
		}
		
		Editor edit = PreferenceManager.getDefaultSharedPreferences(this).edit();
		edit.putBoolean(SERVICE_ON_KEY, on);
		edit.commit();
		
		super.onDestroy();
	}

	@Override
	public void onClick(View view) {
		if(view.getId() == onButton.getId()){
			if(!bound){
				bindService(new Intent(this, FallingDetectionService.class), connection, 0);
				return;
			}
			if(on){
				turnServiceOff();
			}else{
				turnServiceOn();
			}
			on = !on;
			setOnOffButtonAttrs();
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
		try {
			Message msg = Message.obtain(null, ServiceMessageHandler.MSG_START, null);
			Bundle bundle = new Bundle();
			bundle.putLong(FallingDetectionService.CONTACT_ID_KEY, contactId);
			msg.setData(bundle);
			service.send(msg);
		} catch (RemoteException e) {
			e.printStackTrace();
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
