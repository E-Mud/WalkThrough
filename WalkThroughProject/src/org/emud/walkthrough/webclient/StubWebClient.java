package org.emud.walkthrough.webclient;

import java.util.GregorianCalendar;

import org.emud.walkthrough.model.User;

public class StubWebClient implements WebClient{
	private User user;
	
	@Override
	public boolean checkConnection(){
		//TODO
		return true;
	}
	
	@Override
	public int registerNewUser(String nickname, String password, String name, String lastName,
			GregorianCalendar borndate, int gender, int height, double weight)
			throws ConnectionFailedException, UsedNicknameException{
		//TODO
		user = new User();
		user.setUsername(nickname);
		user.setName(name);
		user.setLastname(lastName);
		user.setBorndate((GregorianCalendar)GregorianCalendar.getInstance());
		user.setGender(gender);
		user.setHeight(height);
		user.setWeight(weight);
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
		return user;
	}
}
