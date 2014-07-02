package org.emud.walkthrough;

import org.emud.walkthrough.dialogfragment.AlertDialogFragment;
import org.emud.walkthrough.dialogfragment.ProgressDialogFragment;
import org.emud.walkthrough.webclient.ConnectionFailedException;
import org.emud.walkthrough.webclient.UsedNicknameException;
import org.emud.walkthrough.webclient.WebClient;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;

public class RegisterActivity extends WtFragmentActivity{
	private static final int USED_NICKNAME_DIALOG = 2,
			PASSWORDS_NOT_EQUAL_DIALOG = 3,
			CONNECTION_FAILED_DIALOG = 4,
			PROGRESS_DIALOG = 5;
	
	private EditText usernameEditText, passwordEditText, leglengthEditText;

	private DialogFragment progressDialogFragment;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_register);
		
		usernameEditText = (EditText)findViewById(R.id.register_username);
		passwordEditText = (EditText)findViewById(R.id.register_password1);
		leglengthEditText = (EditText)findViewById(R.id.register_leglength);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.register, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item){		
		final String password = passwordEditText.getText().toString();
		if(!password.equals(((TextView)findViewById(R.id.register_password2)).getText().toString())){
			showCustomDialog(PASSWORDS_NOT_EQUAL_DIALOG);
			return true;
		}
		
		final String username = usernameEditText.getText().toString();
		final double legLength = Double.parseDouble(leglengthEditText.getText().toString());
		final WebClient client = getWebClient();
		
		showCustomDialog(PROGRESS_DIALOG);
		
		new AsyncTask<Void, Void, Integer>(){
			private Exception exceptionThrowed = null;
			
			@Override
			protected Integer doInBackground(Void... params) {
				int id;
				android.util.Log.d("RegAT", "diInBackground");
				try {
					id = client.registerNewUser(username, password, legLength);
				} catch (Exception e) {
					android.util.Log.d("RegAT", "catched " + e.getClass().getCanonicalName());
					e.printStackTrace();
					exceptionThrowed = e;
					return -1;
				}
				
				return id;
			}
			
			@Override
			protected void onPostExecute(Integer result) {
				if(progressDialogFragment != null){
					progressDialogFragment.dismiss();
					progressDialogFragment = null;
				}
				android.util.Log.d("RegAT", "onPostExecute: " + result);
				if(exceptionThrowed != null){
					if(exceptionThrowed instanceof ConnectionFailedException){
						showCustomDialog(CONNECTION_FAILED_DIALOG);						
					}else{
						if(exceptionThrowed instanceof UsedNicknameException)
							showCustomDialog(USED_NICKNAME_DIALOG);
					}
				}else{
					userRegistered(result);
				}
		     }
		}.execute();
		
		return true;
	}
	
	private void userRegistered(int wsId){
		String username = usernameEditText.getText().toString(),
				password = passwordEditText.getText().toString();
		double leglength = Double.parseDouble(leglengthEditText.getText().toString());
		
		Intent intent = new Intent();
		intent.putExtra("id", wsId);
		intent.putExtra("password", password);
		intent.putExtra("username", username);
		intent.putExtra("leglength", leglength);
		setResult(Activity.RESULT_OK, intent);
		
		finish();
	}
	
	private void showCustomDialog(int dialogId){
		DialogFragment dialogFragment;
		switch(dialogId){
		/*case DATE_PICKER_DIALOG:
			dialogFragment = DatePickerDialogFragment.newInstance(bornDate.get(Calendar.DAY_OF_MONTH), bornDate.get(Calendar.MONTH), bornDate.get(Calendar.YEAR));
			((DatePickerDialogFragment)dialogFragment).setOnDatePickedListener(this);
			dialogFragment.show(getSupportFragmentManager(), "datePickerDialog");
			break;
		case SEX_PICKER_DIALOG:
			dialogFragment = GenderPickerDialogFragment.newInstance(gender);
			dialogFragment.show(getSupportFragmentManager(), "genderPickerDialog");
			break;*/
		case USED_NICKNAME_DIALOG:
			dialogFragment = AlertDialogFragment.newInstance(R.string.und_title, R.string.und_message);
			dialogFragment.show(getSupportFragmentManager(), "usedNickNameDialog");
			break;
		case PASSWORDS_NOT_EQUAL_DIALOG:
			dialogFragment = AlertDialogFragment.newInstance(R.string.pned_title, R.string.pned_message);
			dialogFragment.show(getSupportFragmentManager(), "passwordsNotEqualDialog");
			break;
		case CONNECTION_FAILED_DIALOG:
			dialogFragment = AlertDialogFragment.newInstance(R.string.cfd_title, R.string.cfd_message);
			dialogFragment.show(getSupportFragmentManager(), "connectionFailedDialog");
			break;
		case PROGRESS_DIALOG:
			dialogFragment = ProgressDialogFragment.newInstance(R.string.register_progress_message);
			dialogFragment.show(getSupportFragmentManager(), "progressDialog");
			progressDialogFragment = dialogFragment;
			break;
		}
	}
/*
	@Override
	public void onClick(View view) {
		if(view == bornDateView){
			showCustomDialog(DATE_PICKER_DIALOG);
		}else{
			showCustomDialog(SEX_PICKER_DIALOG);
		}
	}

	@Override
	public void onClick(DialogInterface dialog, int which) {
		gender = which;
		genderView.setText(getResources().getStringArray(R.array.genderpicker_stringarray)[gender]);
		dialog.dismiss();
	}

	@Override
	public void datePicked(int year, int month, int day) {
		bornDate.set(Calendar.DAY_OF_MONTH, day);
		bornDate.set(Calendar.MONTH, month);
		bornDate.set(Calendar.YEAR, year);
		bornDateView.setText(""+day+"/"+month+"/"+year);
	}
*/
}
