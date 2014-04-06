package org.emud.walkthrough;

import org.emud.walkthrough.dialogfragment.AlertDialogFragment;
import org.emud.walkthrough.dialogfragment.ConfirmDeleteDialogFragment;
import org.emud.walkthrough.dialogfragment.ConfirmDeleteDialogFragment.OnAcceptButtonListener;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceFragment;
import android.provider.ContactsContract.Contacts;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;

public class SettingsActivity extends FragmentActivity implements OnPreferenceClickListener, OnAcceptButtonListener {
	private static final int INVALID_CONTACT_DIALOG = 0,
			CONFIRM_DELETE_DIALOG = 1;

	private SettingsFragment settingsFragment;
	private boolean resumingWithBadResult = false;
	
	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);

		settingsFragment = new SettingsFragment();
		
        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, settingsFragment)
                .commit();
        
        WalkThroughApplication app = (WalkThroughApplication) getApplicationContext();
        long contactId = app.getEmergencyContact();
        
        if(contactId >= 0){
            Cursor cursor;
            
            cursor = getContentResolver().query(Contacts.CONTENT_URI, new String[] {Contacts.DISPLAY_NAME},
            		Contacts._ID + " = " + contactId, null, null);
            cursor.moveToFirst();
            
            settingsFragment.setContactDisplayName(cursor.getString(cursor.getColumnIndex(Contacts.DISPLAY_NAME)));
            
            cursor.close();
        }
	}
	
	
	public static class SettingsFragment extends PreferenceFragment {
		private String contactName;
		
		@Override
		public void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			addPreferencesFromResource(R.xml.pref_general);
			
			findPreference("logout_pref").setOnPreferenceClickListener((SettingsActivity) getActivity());
			findPreference("delete_pref").setOnPreferenceClickListener((SettingsActivity) getActivity());
			findPreference("emergencycontact_pref").setOnPreferenceClickListener((SettingsActivity) getActivity());
		}
		
		@Override
		public void onResume(){
			super.onResume();
			
			if(contactName != null){
				Preference contactPreference = findPreference("emergencycontact_pref");
				contactPreference.setSummary(contactName);
			}
		}
		
		public void setContactDisplayName(String name) {
			contactName = name;
			
			Preference contactPreference = findPreference("emergencycontact_pref");
			
			if(contactPreference != null)
				contactPreference.setSummary(name);
		}
	}


	@Override
	public boolean onPreferenceClick(Preference pref) {
		if(!pref.hasKey())
			return false;
		
		String key = pref.getKey();
		if(key.equals("logout_pref")){
			WalkThroughApplication app = (WalkThroughApplication) getApplicationContext();
			
			app.unsetActiveUser();
			
			//XXX DEBUGGING
			app.logActiveUser();
			
			finish();
			
			return true;
		}
		
		if(key.equals("delete_pref")){
			showCustomDialog(CONFIRM_DELETE_DIALOG);
			return true;
		}
		
		if(key.equals("emergencycontact_pref")){
			Intent intent = new Intent(Intent.ACTION_PICK, Contacts.CONTENT_URI);
			startActivityForResult(intent, 0);
		    return true;
		}
		
		return false;
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
				WalkThroughApplication app = (WalkThroughApplication) getApplicationContext();
				long contactID = cursor.getLong(cursor.getColumnIndex(Contacts._ID));
				
				settingsFragment.setContactDisplayName(cursor.getString(cursor.getColumnIndex(Contacts.DISPLAY_NAME)));
				app.setEmergencyContact(contactID);
				
			}else{
				resumingWithBadResult  = true;
			}
			
			cursor.close();
		}

	}
	
	@Override
	protected void onPostResume() {
	    super.onPostResume();
	    if (resumingWithBadResult) {
		    resumingWithBadResult = false;

			showCustomDialog(INVALID_CONTACT_DIALOG);
	    }
	}
	
	
	
	private void showCustomDialog(int id) {
		switch(id){
		case INVALID_CONTACT_DIALOG:
			DialogFragment dialogFragment;
			dialogFragment = AlertDialogFragment.newInstance(R.string.icd_title, R.string.icd_message);
			dialogFragment.show(getSupportFragmentManager(), "invalidContactDialog");
			break;
		case CONFIRM_DELETE_DIALOG:
			String userName = ((WalkThroughApplication) getApplicationContext()).getActiveUserName();
			ConfirmDeleteDialogFragment fragment = ConfirmDeleteDialogFragment.newInstance(userName);
			
			fragment.setAcceptButtonListener(this);
			fragment.show(getSupportFragmentManager(), "confirmDeleteDialog");
			break;
		}
	}

	@Override
	public void buttonClicked() {
		WalkThroughApplication app = (WalkThroughApplication) getApplicationContext();
		
		app.removeUser(app.getActiveUserName());
		finish();
	}
}
