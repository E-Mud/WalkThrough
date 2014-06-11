package org.emud.walkthrough.database;

import org.emud.content.observer.Subject;
import org.emud.walkthrough.model.User;

public interface UserDataSource {
	
	public Subject getUserSubject();

	public void createProfile(User user);
	
	public User getProfile();
	
	public void updateProfile(String name, String lastName, int gender, int height, double weight);
}
