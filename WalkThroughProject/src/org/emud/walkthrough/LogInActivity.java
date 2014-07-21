package org.emud.walkthrough;

import org.emud.walkthrough.database.UserDataSource;
import org.emud.walkthrough.dialogfragment.AlertDialogFragment;
import org.emud.walkthrough.dialogfragment.ProgressDialogFragment;
import org.emud.walkthrough.model.User;
import org.emud.walkthrough.webclient.ConnectionFailedException;
import org.emud.walkthrough.webclient.WebClient;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;

public class LogInActivity extends WtFragmentActivity implements OnClickListener {
	private static final int CONNECTION_FAILED_DIALOG = 0, WRONG_DATA_DIALOG = 1, PROGRESS_DIALOG = 2;
	private boolean closingOnFinish;
	private EditText usernameEditText, passwordEditText;
	private DialogFragment progressDialogFragment;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		
		closingOnFinish = true;
		
		usernameEditText = (EditText)findViewById(R.id.login_username);
		passwordEditText = (EditText)findViewById(R.id.login_password);
		
		findViewById(R.id.login_login).setOnClickListener(this);
		findViewById(R.id.login_register).setOnClickListener(this);
	}
	
	@Override
	public void onDestroy(){
		if(closingOnFinish){
			closingOnFinish = false;
			((WalkThroughApplication) getApplicationContext()).onClose();
		}
		super.onDestroy();
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
			User user = new User(extra.getInt("id"), extra.getString("username"), extra.getDouble("leglength"));
			setUpNewUser(user, extra.getString("password"));
			
			goToMainActivity();
		}
	}

	private void goToMainActivity() {
		Intent intent = new Intent();
		intent.setClass(this, MainActivity.class);
		startActivity(intent);
		closingOnFinish = false;
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
		case PROGRESS_DIALOG:
			dialogFragment = ProgressDialogFragment.newInstance(R.string.login_progress_message);
			dialogFragment.show(getSupportFragmentManager(), "progressDialog");
			progressDialogFragment = dialogFragment;
			break;
		}
	}
	
	private void logIn(){
		final WebClient webClient = getWebClient();
		String username, password;

		username = usernameEditText.getText().toString();
		password = passwordEditText.getText().toString();
		
		showCustomDialog(PROGRESS_DIALOG);
		
		new AsyncTask<String, Void, User>(){
			private Exception exceptionThrowed = null;
			
			@Override
			protected User doInBackground(String... params) {
				User user = null;
				try {
					user = webClient.logInUser(params[0], params[1]);
				} catch (ConnectionFailedException e) {
					exceptionThrowed = e;
				}
				
				return user;
			}
			
			@Override
			protected void onPostExecute(User user){
				if(progressDialogFragment != null){
					progressDialogFragment.dismiss();
					progressDialogFragment = null;
				}
				
				if(exceptionThrowed == null){
					onLoggedIn(user);
				}else{
					showCustomDialog(CONNECTION_FAILED_DIALOG);
				}
			}
			
		}.execute(username, password);
		
	}
	
	private void onLoggedIn(User user){
		WalkThroughApplication app = (WalkThroughApplication) getApplicationContext();
		String password = passwordEditText.getText().toString();
		
		if(user != null){
			if(app.containsRegisteredUser(user.getUsername())){
				app.setActiveUser(user.getUsername(), password);
			}else{
				setUpNewUser(user, password);
			}
			goToMainActivity();
		}else{
			showCustomDialog(WRONG_DATA_DIALOG);
		}
	}

	private void setUpNewUser(User user, String password) {
		WalkThroughApplication app = (WalkThroughApplication) getApplicationContext();
		UserDataSource userDataSource;
		String username = user.getUsername();
		
		app.addUser(username);
		app.setActiveUser(username, password);
		
		userDataSource = app.getUserDataSource();
		userDataSource.createProfile(user);
	}
}
