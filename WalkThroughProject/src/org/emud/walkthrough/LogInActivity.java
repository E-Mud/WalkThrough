package org.emud.walkthrough;

import org.emud.walkthrough.database.UserDataSource;
import org.emud.walkthrough.dialogfragment.AlertDialogFragment;
import org.emud.walkthrough.model.User;
import org.emud.walkthrough.webclient.ConnectionFailedException;
import org.emud.walkthrough.webclient.UnauthorizedException;
import org.emud.walkthrough.webclient.WebClient;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.content.Intent;

public class LogInActivity extends FragmentActivity implements OnClickListener {

	private static final int CONNECTION_FAILED_DIALOG = 0, WRONG_DATA_DIALOG = 1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		
		findViewById(R.id.login_login).setOnClickListener(this);
		findViewById(R.id.login_register).setOnClickListener(this);
	}

	@Override
	public void onClick(View view) {
		switch(view.getId()){
		case R.id.login_login:
			logIn();
			break;
		case R.id.login_register:
			Intent intent = new Intent();
			intent.setClass(this, RegisterActivity.class);
			startActivityForResult(intent, 0);
			
			break;
		}
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data){
		if(resultCode == RESULT_OK){
			Bundle extra = data.getExtras();
			setUpNewUser(extra.getString("username"), extra.getString("password"));
			
			goToMainActivity();
		}
	}

	private void goToMainActivity() {
		Intent intent = new Intent();
		intent.setClass(this, MainActivity.class);
		startActivity(intent);
		finish();
	}

	private void showCustomDialog(int id){
		DialogFragment dialogFragment;
		switch(id){
		case CONNECTION_FAILED_DIALOG:
			dialogFragment = AlertDialogFragment.newInstance(R.string.cfd_title, R.string.cfd_message);
			dialogFragment.show(getSupportFragmentManager(), "connectionFailedDialog");
			break;
		case WRONG_DATA_DIALOG:
			dialogFragment = AlertDialogFragment.newInstance(R.string.wdd_title, R.string.wdd_message);
			dialogFragment.show(getSupportFragmentManager(), "wrongDataDialog");
			break;
		}
	}
	
	private void logIn() {
		WalkThroughApplication app = (WalkThroughApplication) getApplicationContext();
		String username, password;
		WebClient webClient = app.getDefaultWebClient();
		boolean correctLogIn = false;
		
		username = ((EditText)findViewById(R.id.login_username)).getText().toString();
		password = ((EditText)findViewById(R.id.login_password)).getText().toString();
		
		try {
			correctLogIn = webClient.logInUser(username, password);
		} catch (ConnectionFailedException e) {
			showCustomDialog(CONNECTION_FAILED_DIALOG);
		}
		
		if(correctLogIn){
			if(!app.containsRegisteredUser(username)){
				setUpNewUser(username, password);
			}else{
				app.setActiveUser(username, password);
			}
			goToMainActivity();
		}else{
			showCustomDialog(WRONG_DATA_DIALOG);
		}
	}

	private void setUpNewUser(String username, String password) {
		WalkThroughApplication app = (WalkThroughApplication) getApplicationContext();
		UserDataSource userDataSource;
		WebClient webClient;
		User user = null;
		
		app.addUser(username);
		app.setActiveUser(username, password);
		
		webClient = app.getDefaultWebClient();
		try {
			webClient.logInUser(username, password);
			user = webClient.getWebProfile();
		} catch (ConnectionFailedException e) {
			showCustomDialog(CONNECTION_FAILED_DIALOG);
		} catch (UnauthorizedException e) {
			showCustomDialog(CONNECTION_FAILED_DIALOG);
		}
		
		userDataSource = app.getUserDataSource();
		userDataSource.createProfile(user);
	}
}
