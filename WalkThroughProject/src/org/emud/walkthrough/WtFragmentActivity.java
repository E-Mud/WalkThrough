package org.emud.walkthrough;

import org.emud.walkthrough.webclient.GWebClient;
import org.emud.walkthrough.webclient.WebClient;

import com.zhealth.gnubila.android.customviews.gFragmentActivity;

public class WtFragmentActivity extends gFragmentActivity {
	private WebClient webClient;
	
	protected WebClient getWebClient(){
		if(webClient == null)
			buildWebClient();
		
		return webClient;
	}

	private void buildWebClient() {
		webClient = new GWebClient(G);
	}
}
