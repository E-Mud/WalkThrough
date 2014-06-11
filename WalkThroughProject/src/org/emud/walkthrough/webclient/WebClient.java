package org.emud.walkthrough.webclient;

import org.emud.walkthrough.database.ActivitiesDataSource;
import org.emud.walkthrough.database.UserDataSource;
import org.emud.walkthrough.model.User;
import org.emud.walkthrough.model.WalkActivity;

public interface WebClient extends ActivitiesDataSource, UserDataSource{
	
	public boolean init();
	
	public boolean isReady();
	
	public boolean isUserLoggedIn();
	
	public boolean isConnected();
	
	public int registerNewUser(String userName, String password, double weight)
			throws ConnectionFailedException, UsedNicknameException;

	public boolean logInUser(String username, String password) throws ConnectionFailedException;

	public User getWebProfile() throws ConnectionFailedException, UnauthorizedException;
	
	public boolean insertWalkActivity(WalkActivity activity);
}
