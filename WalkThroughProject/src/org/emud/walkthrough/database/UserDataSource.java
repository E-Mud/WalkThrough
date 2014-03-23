package org.emud.walkthrough.database;

import java.util.GregorianCalendar;
import java.util.Map;

public interface UserDataSource {
	
	public void createProfile(String nickname, int wsId, String name, String lastName,
			GregorianCalendar borndate, int gender, int height, double weight);
	
	public Map<String, Object> getProfile();
	
	public void updateProfile(String name, String lastName, int gender, int height, double weight);
	
	public String[] getColumns();
}
