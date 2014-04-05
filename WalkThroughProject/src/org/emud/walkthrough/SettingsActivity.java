package org.emud.walkthrough;

import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceFragment;
import android.support.v4.app.FragmentActivity;
/**
 * A {@link PreferenceActivity} that presents a set of application settings. On
 * handset devices, settings are presented as a single list. On tablets,
 * settings are split by category, with category headers shown to the left of
 * the list of settings.
 * <p>
 * See <a href="http://developer.android.com/design/patterns/settings.html">
 * Android Design: Settings</a> for design guidelines and the <a
 * href="http://developer.android.com/guide/topics/ui/settings.html">Settings
 * API Guide</a> for more information on developing a Settings UI.
 */
public class SettingsActivity extends FragmentActivity implements OnPreferenceClickListener {


	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);


        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, new SettingsFragment())
                .commit();
	}
	
	
	public static class SettingsFragment extends PreferenceFragment {
		@Override
		public void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			addPreferencesFromResource(R.xml.pref_general);
			
			findPreference("logout_pref").setOnPreferenceClickListener((SettingsActivity) getActivity());
			findPreference("delete_pref").setOnPreferenceClickListener((SettingsActivity) getActivity());
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
			//XXX DEBUGGING
			WalkThroughApplication app = (WalkThroughApplication) getApplicationContext();
			
			app.removeUser(app.getActiveUserName());
			finish();
			
			return true;
		}
		
		return false;
	}
}
