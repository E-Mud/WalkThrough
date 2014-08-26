package org.emud.walkthrough;

import org.emud.walkthrough.model.User;
import org.emud.walkthrough.model.WalkActivity;

public interface WebClient{
	
	public boolean init();
	
	public boolean isReady();
	
	public boolean isUserLoggedIn();
	
	public boolean isConnected();
	
	public int registerNewUser(String userName, String password, double weight)
			throws ConnectionFailedException, UsedNicknameException;

	public User logInUser(String username, String password) throws ConnectionFailedException;

	public void logOutUser();

	public User getWebProfile();
	
	public boolean deleteUserProfile() throws ConnectionFailedException;
	
	public int insertWalkActivity(WalkActivity activity);

	public void close();
}
