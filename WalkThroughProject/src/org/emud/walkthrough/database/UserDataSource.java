package org.emud.walkthrough.database;

import java.util.Map;

import org.emud.walkthrough.model.User;

public interface UserDataSource {

	public void createProfile(User user);
	
	public User getProfile();
	
	public void updateProfile(String name, String lastName, int gender, int height, double weight);
	
	public String[] getColumns();
}
