package org.emud.walkthrough;

import android.os.Bundle;
import android.content.Intent;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;

public class MainActivity extends FragmentActivity implements OnClickListener {
	private ActionBarDrawerToggle drawerToggle;
	private DrawerLayout drawerLayout;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		drawerLayout = (DrawerLayout)findViewById(R.id.main_drawer_layout);
		
		findViewById(R.id.drawer_newactivity_item).setOnClickListener(this);
		findViewById(R.id.drawer_fallingdetection_item).setOnClickListener(this);
		findViewById(R.id.drawer_myactivities_item).setOnClickListener(this);
		findViewById(R.id.drawer_myresults_item).setOnClickListener(this);
		
		getActionBar().setDisplayHomeAsUpEnabled(true);
        //getActionBar().setHomeButtonEnabled(true);

        
        drawerToggle = new ActionBarDrawerToggle(
                this,                  /* host Activity */
                drawerLayout,         /* DrawerLayout object */
                R.drawable.ic_nav_drawer,  /* nav drawer image to replace 'Up' caret */
                R.string.drawer_open,  /* "open drawer" description for accessibility */
                R.string.drawer_close  /* "close drawer" description for accessibility */
                ) {
            public void onDrawerClosed(View view) {
                //getActionBar().setTitle(mTitle);
                //invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            public void onDrawerOpened(View drawerView) {
                //getActionBar().setTitle(mDrawerTitle);
                //invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }
        };
        
        drawerLayout.setDrawerListener(drawerToggle);
	}

	@Override
	public void onResume(){
		super.onResume();
			
		
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item){
		if (drawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
		
		Intent intent = new Intent();
		intent.setClass(this, SettingsActivity.class);
		startActivity(intent);
		
		return true;
	}

	@Override
	public void onClick(View view) {
		Toast toast;
		int newTitle;
		
		switch(view.getId()){
		case R.id.drawer_newactivity_item:
			newTitle = R.string.newactivity_title;
			toast = Toast.makeText(this, "Nueva actividad", Toast.LENGTH_LONG);
			toast.show();
			break;
		case R.id.drawer_fallingdetection_item:
			newTitle = R.string.fallingdetection_title;
			toast = Toast.makeText(this, "Deteccion de caidas", Toast.LENGTH_LONG);
			toast.show();
			break;
		case R.id.drawer_myactivities_item:
			newTitle = R.string.myactivities_title;
			toast = Toast.makeText(this, "Mis actividades", Toast.LENGTH_LONG);
			toast.show();
			break;
		case R.id.drawer_myresults_item:
			newTitle = R.string.myresults_title;
			toast = Toast.makeText(this, "Mis resultados", Toast.LENGTH_LONG);
			toast.show();
			break;
			default: return;
		}
		
		setTitle(newTitle);
		drawerLayout.closeDrawer(GravityCompat.START);
	}

}
