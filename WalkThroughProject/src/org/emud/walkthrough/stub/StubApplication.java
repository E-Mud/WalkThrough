package org.emud.walkthrough.stub;

import org.emud.walkthrough.WalkThroughApplication;

import android.content.SharedPreferences;
import android.util.Log;

public class StubApplication extends WalkThroughApplication {

	@Override
	public void onCreate(){
		super.onCreate();
	}
	
	public boolean setActiveUser(String user, String password){
		Log.v("STUBAPP", "setActiveUser Called");
		return true;
	}
	
	public void unsetActiveUser(){
		Log.v("STUBAPP", "unsetActiveUser Called");
	}

	public String getActiveUserName(){
		Log.v("STUBAPP", "getActiveUserName Called");
		return "user";
	}
	
	public String getActiveUserPassword(){
		Log.v("STUBAPP", "getActiveUserName Called");
		return "pass";
	}
	
	public void removeUser(String user){
		Log.v("STUBAPP", "removeUser Called");
	}
	
	public long getEmergencyContact(){
		Log.v("STUBAPP", "getEmergencyContact Called");
		SharedPreferences userPrefs = getSharedPreferences("debugUserPreferences", MODE_PRIVATE);

		return userPrefs.getLong("emergencyContactID", -1);
	}
	
	public void setEmergencyContact(long contactID) {
		Log.v("STUBAPP", "setEmergencyContact Called");
		SharedPreferences userPrefs = getSharedPreferences("debugUserPreferences", MODE_PRIVATE);
		SharedPreferences.Editor editor = userPrefs.edit();

		editor.putLong("emergencyContactID", contactID);
		editor.commit();
	}
}
