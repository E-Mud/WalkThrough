package org.emud.walkthrough.webclient;

import java.util.GregorianCalendar;

import org.emud.walkthrough.model.User;

public interface WebClient {
	public boolean checkConnection();
	
	public int registerNewUser(String userName, String password, String name, String lastName,
			GregorianCalendar borndate, int gender, int height, double weight)
			throws ConnectionFailedException, UsedNicknameException;
	
	public String getAuthToken(String userName, String password) throws ConnectionFailedException;
	
	public void setAuthToken(String authToken);

	public boolean logIn(String username, String password) throws ConnectionFailedException;

	public User getProfile() throws ConnectionFailedException, UnauthorizedException;
}
