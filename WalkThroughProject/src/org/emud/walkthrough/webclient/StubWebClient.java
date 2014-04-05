package org.emud.walkthrough.webclient;

import java.util.GregorianCalendar;

import org.emud.walkthrough.model.User;

public class StubWebClient implements WebClient{

	@Override
	public boolean checkConnection(){
		//TODO
		return true;
	}
	
	@Override
	public int registerNewUser(String nickname, String password, String name, String lastName,
			GregorianCalendar borndate, int sex, int height, double weight)
			throws ConnectionFailedException, UsedNicknameException{
		//TODO
		return 0;
	}

	@Override
	public String getAuthToken(String userName, String password)
			throws ConnectionFailedException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setAuthToken(String authToken) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean logIn(String username, String password)
			throws ConnectionFailedException {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public User getProfile() throws ConnectionFailedException,
			UnauthorizedException {
		// TODO Auto-generated method stub
		User user = new User();
		user.setLastname("Alvarez");
		user.setName("Antonio");
		user.setUsername("AA");
		user.setBorndate((GregorianCalendar)GregorianCalendar.getInstance());
		return user;
	}
}
