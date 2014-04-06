package org.emud.walkthrough;


import java.util.HashSet;
import java.util.Set;

import org.emud.walkthrough.database.DataSource;
import org.emud.walkthrough.database.UserDataSource;
import org.emud.walkthrough.stub.StubWebClient;
import org.emud.walkthrough.webclient.WebClient;

import android.app.Application;
import android.content.SharedPreferences;
import android.util.Log;

public class WalkThroughApplication extends Application {
	private WebClient defaultWebClient;
	private DataSource dataSource;
	private String activeUser;
	
	@Override
	public void onCreate(){
		super.onCreate();
		activeUser = getSharedPreferences("WalkThroughPreferences", MODE_PRIVATE)
				.getString("activeUserName", null);
	}
	
	public boolean setActiveUser(String user, String password){
		Set<String> registeredUsers = getSharedPreferences("WalkThroughPreferences", MODE_PRIVATE)
				.getStringSet("registeredUsers", null);
		if(registeredUsers != null && registeredUsers.contains(user)){
			activeUser = user;
			SharedPreferences.Editor editor = getSharedPreferences("WalkThroughPreferences", MODE_PRIVATE).edit();
			editor.putString("activeUserName", activeUser);
			editor.putString("activeUserPassword", password);
			editor.commit();
			return true;
		}else{
			return false;
		}
	}
	
	public void unsetActiveUser(){
		if(activeUser != null){
			activeUser = null;
			SharedPreferences.Editor editor = getSharedPreferences("WalkThroughPreferences", MODE_PRIVATE).edit();
			editor.remove("activeUserName");
			editor.remove("activeUserPassword");
			editor.commit();
			closeDataSource();
		}
	}

	public String getActiveUserName(){
		return activeUser;
	}
	
	public String getActiveUserPassword(){
		return getSharedPreferences("WalkThroughPreferences", MODE_PRIVATE)
		.getString("activeUserPassword", null);
	}
	
	public UserDataSource getUserDataSource(){
		if(activeUser != null){
			if(dataSource == null)
				dataSource = new DataSource(this, activeUser);
			return dataSource;
		}else{
			return null;
		}
	}
	
	public boolean containsRegisteredUser(String username) {
		SharedPreferences registeredPref = getSharedPreferences("WalkThroughPreferences", MODE_PRIVATE);
		Set<String> registeredUsers = registeredPref.getStringSet("registeredUsers", null);
		
		return registeredUsers.contains(username);
	}
	
	public boolean addUser(String user){
		SharedPreferences registeredPref = getSharedPreferences("WalkThroughPreferences", MODE_PRIVATE);
		Set<String> registeredUsers = registeredPref.getStringSet("registeredUsers", null);
		boolean added;
		
		if(registeredUsers != null){
			added = registeredUsers.add(user);
		}else{
			added = true;
			registeredUsers = new HashSet<String>();
			registeredUsers.add(user);
		}
		
		if(added){
			SharedPreferences.Editor editor = registeredPref.edit();
			editor.putStringSet("registeredUsers", registeredUsers);
			editor.commit();			
		}
		
		return added;
	}
	
	public void removeUser(String user){
		SharedPreferences registeredPref = getSharedPreferences("WalkThroughPreferences", MODE_PRIVATE);
		Set<String> registeredUsers = registeredPref.getStringSet("registeredUsers", null);
		if(registeredUsers != null){
			boolean removed = registeredUsers.remove(user);
			if(removed){
				SharedPreferences.Editor editor = registeredPref.edit();
				editor.putStringSet("registeredUsers", registeredUsers);
				editor.commit();
				removeUserDatabase(user);
				if(activeUser != null && activeUser.equals(user))
					unsetActiveUser();
			}
		}
	}	
	
	private void removeUserDatabase(String user) {
		String databaseName = DataSource.buildDatabaseName(user);
		super.deleteDatabase(databaseName);
	}
	

	public void close() {
		closeDataSource();
	}
	
	private void closeDataSource() {
		if(dataSource != null){
			dataSource.closeDatabase();
			dataSource = null;
		}
	}

	public WebClient getDefaultWebClient(){
		return defaultWebClient == null ? defaultWebClient = new StubWebClient() : defaultWebClient;
	}
	
	public long getEmergencyContact(){
		if(activeUser != null){
			SharedPreferences userPrefs = getSharedPreferences(activeUser + "Preferences", MODE_PRIVATE);
			
			return userPrefs.getLong("emergencyContactID", -1);
		}else{
			return -1;
		}
	}
	
	public void setEmergencyContact(long contactID) {
		if(activeUser != null){
			SharedPreferences userPrefs = getSharedPreferences(activeUser + "Preferences", MODE_PRIVATE);
			SharedPreferences.Editor editor = userPrefs.edit();
			
			editor.putLong("emergencyContactID", contactID);
			editor.commit();			
		}
	}
	
	
	//XXX DEBUGING
	public void logUsers(){
		SharedPreferences registeredPref = getSharedPreferences("WalkThroughPreferences", MODE_PRIVATE);
		Set<String> registeredUsers = registeredPref.getStringSet("registeredUsers", null);
		if(registeredUsers != null){
			for(String username : registeredUsers)
				Log.v("XXXXXXXXXXXXXXXXXXXXXX", username);
		}else{
			Log.v("XXXXXXXXXXXXXXXXXXXXXX", "Vacio");
		}
	}
	
	//XXX DEBUGING
	public void logActiveUser(){
		Log.v("XXXXXXX user", getSharedPreferences("WalkThroughPreferences", MODE_PRIVATE)
		.getString("activeUserName", "NONE"));
		Log.v("XXXXXXX pass", getSharedPreferences("WalkThroughPreferences", MODE_PRIVATE)
		.getString("activeUserPassword", "NONE"));
	}
}
