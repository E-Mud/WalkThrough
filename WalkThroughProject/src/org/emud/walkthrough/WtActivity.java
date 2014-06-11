package org.emud.walkthrough;

import org.emud.walkthrough.webclient.WebClient;

import com.zhealth.gnubila.android.customviews.gActivity;

public class WtActivity extends gActivity {
	private WebClient webClient;
	
	protected WebClient getWebClient(){
		if(webClient == null)
			buildWebClient();
		
		return webClient;
	}

	private void buildWebClient() {
		// TODO
	}
}
