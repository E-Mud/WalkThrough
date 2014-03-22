package org.emud.walkthrough;

import org.emud.walkthrough.webclient.WebClient;

import android.app.Application;

public class WalkThroughApplication extends Application {
	private WebClient defaultWebClient;
	
	public WebClient getDefaultWebClient(){
		return defaultWebClient == null ? defaultWebClient = new WebClient() : defaultWebClient;
	}
}
