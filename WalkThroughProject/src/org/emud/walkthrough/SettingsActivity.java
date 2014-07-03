package org.emud.walkthrough;

import org.emud.walkthrough.dialogfragment.AlertDialogFragment;
import org.emud.walkthrough.dialogfragment.ConfirmDeleteDialogFragment;
import org.emud.walkthrough.dialogfragment.ProgressDialogFragment;
import org.emud.walkthrough.dialogfragment.ConfirmDeleteDialogFragment.OnConfirmListener;
import org.emud.walkthrough.webclient.ConnectionFailedException;
import org.emud.walkthrough.webclient.UsedNicknameException;
import org.emud.walkthrough.webclient.WebClient;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceFragment;
import android.provider.ContactsContract.Contacts;
import android.support.v4.app.DialogFragment;

public class SettingsActivity extends WtFragmentActivity implements OnPreferenceClickListener, OnConfirmListener {
	private static final int INVALID_CONTACT_DIALOG = 0,
			CONFIRM_DELETE_DIALOG = 1,
			CONNECTION_FAILED_DIALOG = 2,
			PROGRESS_DIALOG = 3;

	private SettingsFragment settingsFragment;
	private boolean resumingWithBadResult = false;

	private DialogFragment progressDialogFragment;
	
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);

		settingsFragment = new SettingsFragment();
		
        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, settingsFragment)
                .commit();
        
        WalkThroughApplication app = (WalkThroughApplication) getApplicationContext();
        long contactId = app.getEmergencyContact();
        
		settingsFragment.setScreenPref(app.getScreenPref());
        
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
		private boolean screen;
		
		@Override
		public void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			addPreferencesFromResource(R.xml.pref_general);
			
			findPreference("logout_pref").setOnPreferenceClickListener((SettingsActivity) getActivity());
			findPreference("delete_pref").setOnPreferenceClickListener((SettingsActivity) getActivity());
			findPreference("emergencycontact_pref").setOnPreferenceClickListener((SettingsActivity) getActivity());
			CheckBoxPreference screenPreference = (CheckBoxPreference) findPreference("screen_pref");
			screenPreference.setChecked(screen);
			screenPreference.setOnPreferenceClickListener((SettingsActivity) getActivity());
		}

		@Override
		public void onResume(){
			super.onResume();
			
			if(contactName != null){
				Preference contactPreference = findPreference("emergencycontact_pref");
				contactPreference.setSummary(contactName);
			}
		}
		
		public void setScreenPref(boolean screenPref) {
			screen = screenPref;
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
		
		if(key.equals("screen_pref")){
			((WalkThroughApplication) getApplicationContext()).setScreenPref(((CheckBoxPreference) pref).isChecked());
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
			
			fragment.setConfirmListener(this);
			fragment.show(getSupportFragmentManager(), "confirmDeleteDialog");
			break;
		case CONNECTION_FAILED_DIALOG:
			dialogFragment = AlertDialogFragment.newInstance(R.string.cfd_title, R.string.cfd_message);
			dialogFragment.show(getSupportFragmentManager(), "connectionFailedDialog");
			break;
		case PROGRESS_DIALOG:
			dialogFragment = ProgressDialogFragment.newInstance(R.string.delete_progress_message);
			dialogFragment.show(getSupportFragmentManager(), "progressDialog");
			progressDialogFragment = dialogFragment;
			break;
		}
	}

	@Override
	public void buttonClicked() {
		final WebClient webClient = getWebClient(); 
		
		showCustomDialog(PROGRESS_DIALOG);
		
		new AsyncTask<Void, Void, Boolean>(){
			private Exception exceptionThrowed = null;
			
			@Override
			protected Boolean doInBackground(Void... params) {
				boolean result;
				try {
					result = webClient.deleteUserProfile();
				} catch (Exception e) {
					exceptionThrowed = e;
					return false;
				}
				
				return result;
			}
			
			@Override
			protected void onPostExecute(Boolean result) {
				if(progressDialogFragment != null){
					progressDialogFragment.dismiss();
					progressDialogFragment = null;
				}

				if(exceptionThrowed != null){
					if(exceptionThrowed instanceof ConnectionFailedException)
						showCustomDialog(CONNECTION_FAILED_DIALOG);
				}else{
					userDeleted(result);
				}
		     }
		}.execute();
	}
	
	private void userDeleted(boolean res){
		if(res){
			WalkThroughApplication app = (WalkThroughApplication) getApplicationContext();
			app.removeUser(app.getActiveUserName());
			finish();
		}
	}
}
