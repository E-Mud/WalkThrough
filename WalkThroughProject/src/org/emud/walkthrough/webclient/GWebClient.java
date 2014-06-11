package org.emud.walkthrough.webclient;

import java.util.GregorianCalendar;
import java.util.List;

import org.emud.content.observer.Subject;
import org.emud.walkthrough.model.Result;
import org.emud.walkthrough.model.User;
import org.emud.walkthrough.model.WalkActivity;

import static org.emud.walkthrough.webclient.GWebServiceConstants.*;

import com.zhealth.gnubila.android.gService;
import com.zhealth.gnubila.android.utils.gData;

public class GWebClient implements WebClient {
	private gService service;
	private boolean serviceUserLoggedIn, userLoggedIn;
	
	public GWebClient(gService service){
		this.service = service;
		serviceUserLoggedIn = userLoggedIn = false;
	}
	
	@Override
	public boolean init(){
		boolean initialized = service.init(PROJECT_ID, APP_NAME, SERVER_URL);
		
		if(initialized){
			serviceUserLoggedIn = service.login(PROJECT_ID, SERVER_USER_NAME, SERVER_USER_PASSWORD);
		}else{
			return false;
		}
		
		return serviceUserLoggedIn;
	}
	
	@Override
	public boolean isReady(){
		return service.isStarted(PROJECT_ID) && serviceUserLoggedIn;
	}
	
	@Override
	public boolean isUserLoggedIn(){
		return service.isStarted(PROJECT_ID) && serviceUserLoggedIn && userLoggedIn;
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
	public boolean isConnected() {
		return service.isOnline();
	}

	@Override
	public int registerNewUser(String userName, String password, double legLength)
			throws ConnectionFailedException, UsedNicknameException {
		//TODO
		if(!isReady())
			return -1;
		
		String[] atts = new String[]{"D.y", "name", "password", "legLength"};
		String[] values = new String[]{"wtuser", userName, password, ""+legLength};
		gData data = service.insertQuery(PROJECT_ID, atts, values);
		android.util.Log.d("GWC", "registered data:" + data.getJSON());
		int webId = Integer.parseInt(data.getValue(0, "D.x"));
		android.util.Log.d("GWC", "registered id: " + webId);
		return webId;
	}

	@Override
	public boolean logInUser(String username, String password)
			throws ConnectionFailedException {
		// TODO Auto-generated method stub
		userLoggedIn = true;
		return userLoggedIn;
	}

	@Override
	public User getWebProfile() throws ConnectionFailedException,
			UnauthorizedException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean insertWalkActivity(WalkActivity activity) {
		// TODO Auto-generated method stub
		return false;
	}

}
