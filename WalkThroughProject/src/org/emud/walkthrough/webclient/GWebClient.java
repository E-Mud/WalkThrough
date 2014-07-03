package org.emud.walkthrough.webclient;

import static org.emud.walkthrough.webclient.GWebServiceConstants.APP_NAME;
import static org.emud.walkthrough.webclient.GWebServiceConstants.PROJECT_ID;
import static org.emud.walkthrough.webclient.GWebServiceConstants.SERVER_URL;
import static org.emud.walkthrough.webclient.GWebServiceConstants.SERVER_USER_NAME;
import static org.emud.walkthrough.webclient.GWebServiceConstants.SERVER_USER_PASSWORD;

import org.emud.walkthrough.model.User;
import org.emud.walkthrough.model.WalkActivity;

import com.zhealth.gnubila.android.gService;
import com.zhealth.gnubila.android.utils.gConstants;
import com.zhealth.gnubila.android.utils.gData;

public class GWebClient implements WebClient {
	private gService service;
	private boolean serviceUserLoggedIn;
	private User userLoggedIn;
	
	
	public GWebClient(gService service, User user){
		this.service = service;
		serviceUserLoggedIn = false;
		userLoggedIn = user;
	}
	
	@Override
	public boolean init(){
		if(isReady())
			return true;
		
		log("initializing");
		
		boolean initialized = service.init(PROJECT_ID, APP_NAME, SERVER_URL);
		
		log("initialized: " + initialized);
		
		if(initialized){
			log("logging service user");
			serviceUserLoggedIn = service.login(PROJECT_ID, SERVER_USER_NAME, SERVER_USER_PASSWORD);
			log("service user logged: " + serviceUserLoggedIn);
		}else{
			return false;
		}
		
		return serviceUserLoggedIn;
	}
	
	private void log(String string) {
		android.util.Log.d("GWC", string);
	}

	@Override
	public boolean isReady(){
		return service.isStarted(PROJECT_ID) && serviceUserLoggedIn;
	}
	
	@Override
	public boolean isUserLoggedIn(){
		return service.isStarted(PROJECT_ID) && serviceUserLoggedIn && (userLoggedIn != null);
	}

	@Override
	public boolean isConnected() {
		return service.isOnline();
	}

	@Override
	public int registerNewUser(String username, String password, double legLength)
			throws ConnectionFailedException, UsedNicknameException {
		log("registering new user");
		checkReady();
		
		gData data;
		
		data = service.getGQuery("walkthrough", new String[]{"where=D.y=wtuser+username="+username,
				"fields=D.k",
				"D.dataformat=xml"});
		
		if(data.getCount() > 0 && data.getValue(0, gConstants.TAG_DK) != null)
			throw new UsedNicknameException();

		log("valid username " + username);

		String[] atts, values;
		int webId;
		
		atts = new String[]{"D.y", "username", "leglength"};
		values = new String[]{"wtuser", username, ""+legLength};

		log("registering user");
		
		data = service.insertQuery("walkthrough", atts, values);		
		webId = Integer.parseInt(data.getValue(0, gConstants.TAG_DK));
		
		log("user registered: " + webId);
		
		return webId;
	}

	private void checkReady() throws ConnectionFailedException{
		if(!isReady()){
			if(!init())
				throw new ConnectionFailedException();
		}
		
		if(!isConnected())
			throw new ConnectionFailedException();
	}

	@Override
	public User logInUser(String username, String password)
			throws ConnectionFailedException {
		checkReady();
		
		gData data = service.getGQuery("walkthrough", new String[]{"where=D.y=wtuser+username="+username,
				"fields=D.k,username,leglength",
				"D.dataformat=xml"});
		
		if(data.getCount() == 0 || data.getValue(0, gConstants.TAG_DK) == null)
			return null;
		
		userLoggedIn = new User(Integer.parseInt(data.getValue(0, gConstants.TAG_DK)),
				data.getValue(0, "username"),
				Double.parseDouble(data.getValue(0, "leglength")));
		
		return userLoggedIn;
	}
	
	@Override
	public void logOutUser(){
		userLoggedIn = null;
	}

	@Override
	public User getWebProfile() {
		return userLoggedIn;
		/*
		checkReady();
		
		if(userLoggedInDk < 0)
			return null;
		
		gData data = service.getGQuery("walkthrough", new String[]{"where=D.y=wtuser+D.k="+userLoggedInDk,
				"fields=D.k,username,leglength",
				"D.dataformat=xml"});
		
		if(data.getCount() == 0 || data.getValue(0, "username") == null)
			return null;
		
		User user = new User(userLoggedInDk,
				data.getValue(0, "username"),
				Double.parseDouble(data.getValue(0, "leglength")));
		
		return user;*/
	}

	@Override
	public boolean deleteUserProfile() throws ConnectionFailedException {
		checkReady();
		
		if(userLoggedIn == null)
			return false;
		
		gData data = service.removeQueryByID(PROJECT_ID, ""+userLoggedIn.getWebServiceId());
		return data.getValue(0, gConstants.TAG_DK) != null;
	}

	
	@Override
	public boolean insertWalkActivity(WalkActivity activity) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void close() {
		serviceUserLoggedIn = false;
		userLoggedIn = null;
		service.logOut(PROJECT_ID);
		service.stop();
	}
}
