package org.emud.walkthrough.webclient;

import java.util.GregorianCalendar;

public class WebClient {

	public static boolean checkConnection(){
		//TODO
		return true;
	}
	
	public int registerNewUser(String nickname, String password, String name, String lastName,
			GregorianCalendar borndate, int sex, int height, double weight)
			throws ConnectionFailedException, UsedNicknameException{
		//TODO
		return 0;
	}
}
