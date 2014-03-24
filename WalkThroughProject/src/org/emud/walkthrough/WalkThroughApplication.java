package org.emud.walkthrough;


import java.util.HashSet;
import java.util.Set;

import org.emud.walkthrough.database.DataSource;
import org.emud.walkthrough.database.UserDataSource;
import org.emud.walkthrough.webclient.StubWebClient;
import org.emud.walkthrough.webclient.WebClient;

import android.app.Application;
import android.content.SharedPreferences;

public class WalkThroughApplication extends Application {
	private WebClient defaultWebClient;
	private DataSource dataSource;
	private String activeUser;
	
	@Override
	public void onCreate(){
		super.onCreate();
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
		}
	}
	
	public String getActiveUserName(){
		return getSharedPreferences("WalkThroughPreferences", MODE_PRIVATE)
		.getString("activeUserName", null);
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
			}
		}
	}	
	
	private void removeUserDatabase(String user) {
		String databaseName = DataSource.buildDatabaseName(user);
		super.deleteDatabase(databaseName);
	}

	public WebClient getDefaultWebClient(){
		return defaultWebClient == null ? defaultWebClient = new StubWebClient() : defaultWebClient;
	}
}
