package org.emud.walkthrough;

import org.emud.walkthrough.webclient.WebClient;

import android.os.Bundle;

import com.zhealth.gnubila.android.customviews.gFragmentActivity;

public class WtFragmentActivity extends gFragmentActivity {
	private WebClient webClient;
	
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		bind(this);
	}
	
	protected WebClient getWebClient(){
		if(webClient == null)
			buildWebClient();
		
		return webClient;
	}

	private void buildWebClient() {
		webClient = ((WalkThroughApplication) getApplicationContext()).getDefaultWebClient(G);
	}
}
