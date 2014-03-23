package org.emud.walkthrough.webclient;

import java.util.GregorianCalendar;

public interface WebClient {
	public boolean checkConnection();
	
	public int registerNewUser(String userName, String password, String name, String lastName,
			GregorianCalendar borndate, int sex, int height, double weight)
			throws ConnectionFailedException, UsedNicknameException;
	
	public String getAuthToken(String userName, String password) throws ConnectionFailedException;
	
	public void setAuthToken(String authToken);
}
