package org.emud.walkthrough;

import java.util.Calendar;
import java.util.GregorianCalendar;

import org.emud.walkthrough.dialogfragment.AlertDialogFragment;
import org.emud.walkthrough.dialogfragment.DatePickerDialogFragment;
import org.emud.walkthrough.dialogfragment.GenderPickerDialogFragment;
import org.emud.walkthrough.webclient.ConnectionFailedException;
import org.emud.walkthrough.webclient.UsedNicknameException;
import org.emud.walkthrough.webclient.WebClient;

import android.os.Bundle;
import android.app.Activity;
import android.app.DatePickerDialog.OnDateSetListener;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.DatePicker;
import android.widget.TextView;

public class RegisterActivity extends FragmentActivity implements OnClickListener, OnDateSetListener, DialogInterface.OnClickListener {
	private static final int DATE_PICKER_DIALOG = 0,
			SEX_PICKER_DIALOG = 1,
			USED_NICKNAME_DIALOG = 2,
			PASSWORDS_NOT_EQUAL_DIALOG = 3,
			CONNECTION_FAILED_DIALOG = 4;
	
	private GregorianCalendar bornDate;
	private int gender;
	private TextView bornDateView, genderView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_register);
		
		bornDate = (GregorianCalendar) GregorianCalendar.getInstance();
		bornDateView = ((TextView)findViewById(R.id.register_borndate));
		bornDateView.setText(""+bornDate.get(Calendar.DAY_OF_MONTH)+"/"+bornDate.get(Calendar.MONTH)+"/"+bornDate.get(Calendar.YEAR));
		bornDateView.setOnClickListener(this);
		
		gender = 0;
		genderView = ((TextView)findViewById(R.id.register_gender));
		genderView.setText(getResources().getStringArray(R.array.genderpicker_stringarray)[gender]);
		genderView.setOnClickListener(this);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.register, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item){
		String password = ((TextView)findViewById(R.id.register_password1)).getText().toString();
		if(!password.equals(((TextView)findViewById(R.id.register_password2)).getText().toString())){
			showCustomDialog(PASSWORDS_NOT_EQUAL_DIALOG);
			return true;
		}
		
		WebClient client = ((WalkThroughApplication) getApplicationContext()).getDefaultWebClient();
		
		if(!client.checkConnection()){
			showCustomDialog(CONNECTION_FAILED_DIALOG);
			return true;
		}
		
		String nickname = ((TextView)findViewById(R.id.register_nickname)).getText().toString();
		String name = ((TextView)findViewById(R.id.register_name)).getText().toString();
		String lastName = ((TextView)findViewById(R.id.register_lastname)).getText().toString();
		int height = Integer.valueOf(((TextView)findViewById(R.id.register_height)).getText().toString()).intValue();
		double weight = Double.valueOf(((TextView)findViewById(R.id.register_weight)).getText().toString()).intValue();
		
		int id = 0; 
		try {
			id = client.registerNewUser(nickname, password, name, lastName, bornDate, gender, height, weight);
		} catch (ConnectionFailedException e) {
			showCustomDialog(CONNECTION_FAILED_DIALOG);
			return true;
		} catch (UsedNicknameException e) {
			showCustomDialog(USED_NICKNAME_DIALOG);
			return true;
		}
		
		Intent intent = new Intent();
		intent.putExtra("id", id);
		intent.putExtra("password", password);
		intent.putExtra("username", nickname);
		setResult(Activity.RESULT_OK, intent);
		
		finish();
		
		return true;
	}
	
	private void showCustomDialog(int dialogId){
		DialogFragment dialogFragment;
		switch(dialogId){
		case DATE_PICKER_DIALOG:
			dialogFragment = DatePickerDialogFragment.newInstance(bornDate.get(Calendar.DAY_OF_MONTH), bornDate.get(Calendar.MONTH), bornDate.get(Calendar.YEAR));
			dialogFragment.show(getSupportFragmentManager(), "datePickerDialog");
			break;
		case SEX_PICKER_DIALOG:
			dialogFragment = GenderPickerDialogFragment.newInstance(gender);
			dialogFragment.show(getSupportFragmentManager(), "genderPickerDialog");
			break;
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
		}
	}

	@Override
	public void onClick(View view) {
		if(view == bornDateView){
			showCustomDialog(DATE_PICKER_DIALOG);
		}else{
			showCustomDialog(SEX_PICKER_DIALOG);
		}
	}

	@Override
	public void onDateSet(DatePicker view, int year, int monthOfYear,
			int dayOfMonth) {
		bornDate.set(Calendar.DAY_OF_MONTH, dayOfMonth);
		bornDate.set(Calendar.MONTH, monthOfYear);
		bornDate.set(Calendar.YEAR, year);
		bornDateView.setText(""+dayOfMonth+"/"+monthOfYear+"/"+year);
	}

	@Override
	public void onClick(DialogInterface dialog, int which) {
		gender = which;
		genderView.setText(getResources().getStringArray(R.array.genderpicker_stringarray)[gender]);
		dialog.dismiss();
	}

}
