package org.emud.walkthrough.stub;

import java.util.GregorianCalendar;
import java.util.List;

import org.emud.content.observer.Subject;
import org.emud.walkthrough.model.Result;
import org.emud.walkthrough.model.User;
import org.emud.walkthrough.model.WalkActivity;
import org.emud.walkthrough.webclient.ConnectionFailedException;
import org.emud.walkthrough.webclient.UnauthorizedException;
import org.emud.walkthrough.webclient.UsedNicknameException;
import org.emud.walkthrough.webclient.WebClient;

public class StubWebClient implements WebClient{
	private User user;
	
	@Override
	public boolean isConnected(){
		//TODO
		return true;
	}
	
	@Override
	public int registerNewUser(String nickname, String password, double legLength)
			throws ConnectionFailedException, UsedNicknameException{
		//TODO
		user = new User();
		user.setUsername(nickname);
		user.setName("name");
		user.setLastname("lastName");
		user.setBorndate((GregorianCalendar)GregorianCalendar.getInstance());
		user.setGender(0);
		user.setHeight(0);
		user.setWeight(legLength);
		return 0;
	}

	@Override
	public boolean logInUser(String username, String password)
			throws ConnectionFailedException {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public User getWebProfile() throws ConnectionFailedException,
			UnauthorizedException {
		// TODO Auto-generated method stub
		return user;
	}

	@Override
	public Subject getActivitiesSubject() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<WalkActivity> getActivities(GregorianCalendar startDate,
			GregorianCalendar endDate) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long createNewActivity(WalkActivity act) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public List<Result> getActivityResults(long activity_id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Result> getResults(int type, GregorianCalendar startDate,
			GregorianCalendar endDate) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Result> getResults(int type) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Subject getUserSubject() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void createProfile(User user) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public User getProfile() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void updateProfile(String name, String lastName, int gender,
			int height, double weight) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean insertWalkActivity(WalkActivity activity) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean init() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isReady() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isUserLoggedIn() {
		// TODO Auto-generated method stub
		return false;
	}
}
