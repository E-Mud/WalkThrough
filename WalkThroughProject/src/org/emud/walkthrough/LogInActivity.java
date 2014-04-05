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
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.Toast;
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
			//XXX DEBUGGING
			WalkThroughApplication app = (WalkThroughApplication) getApplicationContext();
			app.logActiveUser();
			Intent intent = new Intent();
			intent.setClass(this, SettingsActivity.class);
			startActivity(intent);
			/*
			//XXX DEBUGING
			((WalkThroughApplication) getApplicationContext()).unsetActiveUser();
			
			Intent intent = new Intent();
			intent.setClass(this, RegisterActivity.class);
			startActivityForResult(intent, 0);
			*/
			break;
		}
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data){
		if(resultCode == RESULT_OK){
			Bundle extra = data.getExtras();
			setUpNewUser(extra.getString("username"), extra.getString("password"));
			
			////TODO Pa la siguiente
			palasiguiente();
		}
	}

	//XXX DEBUGING
	private void palasiguiente() {
		WalkThroughApplication app = (WalkThroughApplication) getApplicationContext();
		String username, password;
		
		username = app.getActiveUserName();
		password = app.getActiveUserPassword();
		Toast toast = Toast.makeText(this, "PA LA SIGUIENTE "+ username + " " + password, Toast.LENGTH_LONG);
		toast.show();
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
			correctLogIn = webClient.logIn(username, password);
		} catch (ConnectionFailedException e) {
			showCustomDialog(CONNECTION_FAILED_DIALOG);
		}
		
		if(correctLogIn){
			if(!app.containsRegisteredUser(username)){
				setUpNewUser(username, password);
			}else{
				app.setActiveUser(username, password);
			}
			palasiguiente();
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
			webClient.logIn(username, password);
			user = webClient.getProfile();
		} catch (ConnectionFailedException e) {
			showCustomDialog(CONNECTION_FAILED_DIALOG);
		} catch (UnauthorizedException e) {
			showCustomDialog(CONNECTION_FAILED_DIALOG);
		}
		
		userDataSource = app.getUserDataSource();
		userDataSource.createProfile(user);
		
		//XXX DEBUGGING
		logUser(user);
		user = userDataSource.getProfile();
		logUser(user);
	}
	

	//XXX DEBUGING
	@Override
	public void onDestroy(){
		((WalkThroughApplication) getApplicationContext()).close();
		super.onDestroy();
	}
	
	//XXX DEBUGGING
	private void logUser(User user){
		Log.v("XXXXX un", user.getUsername());
		Log.v("XXXXX n", user.getName());
		Log.v("XXXXX ln", user.getLastname());
		Log.v("XXXXX g", ""+ user.getGender());
		Log.v("XXXXX h", ""+user.getHeight());
		Log.v("XXXXX w", ""+user.getWeight());
		Log.v("XXXXX b", ""+user.getBorndate());
	}

}
