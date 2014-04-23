package org.emud.walkthrough;

import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.ContactsContract.Contacts;
import android.content.Intent;
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
	
	private static final String SERVICE_ON_KEY = "on";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_falling_detection);
		long contactId;
		
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
	}

	@Override
	public void onClick(View view) {
		if(view.getId() == onButton.getId()){
			if(on){
				turnServiceOff();
				onButton.setBackgroundColor(Color.rgb(0, 170, 0));
				onButton.setText(R.string.fallingdetection_turnon);
			}else{
				turnServiceOn();
				onButton.setBackgroundColor(Color.rgb(170, 0, 0));
				onButton.setText(R.string.fallingdetection_turnoff);
			}
			
			on = !on;
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
				long contactID = cursor.getLong(cursor.getColumnIndex(Contacts._ID));
				Editor edit = PreferenceManager.getDefaultSharedPreferences(this).edit();
				
				((TextView) findViewById(R.id.falling_detection_emergencycontactname)).setText((cursor.getString(cursor.getColumnIndex(Contacts.DISPLAY_NAME))));
				
				edit.putLong(DEFAULT_CONTACT_ID, contactID);
				edit.commit();
			}
			
			cursor.close();
		}

	}

	private void turnServiceOn() {
		// TODO Auto-generated method stub
		
	}

	private void turnServiceOff() {
		// TODO Auto-generated method stub
		
	}

}
