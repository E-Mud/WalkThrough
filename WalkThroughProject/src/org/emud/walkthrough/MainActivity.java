package org.emud.walkthrough;

import org.emud.walkthrough.fragment.AutoUpdateListFragment;

import android.os.Bundle;
import android.content.Intent;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.ListFragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;

public class MainActivity extends FragmentActivity implements OnClickListener {
	private static final int NEW_ACTIVITY_CONTENT = 0,
			FALLING_DETECTION_CONTENT = 1,
			MY_ACTIVITIES_CONTENT = 2,
			MY_RESULTS_CONTENT = 3;
	
	private int currentContent;
	private ActionBarDrawerToggle drawerToggle;
	private DrawerLayout drawerLayout;
	
	private ListFragment myActivitiesListFragment;
	
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
		int newContent;
		
		switch(view.getId()){
		case R.id.drawer_newactivity_item:
			newContent = NEW_ACTIVITY_CONTENT;
			toast = Toast.makeText(this, "Nueva actividad", Toast.LENGTH_LONG);
			toast.show();
			break;
		case R.id.drawer_fallingdetection_item:
			newContent = FALLING_DETECTION_CONTENT;
			toast = Toast.makeText(this, "Deteccion de caidas", Toast.LENGTH_LONG);
			toast.show();
			break;
		case R.id.drawer_myactivities_item:
			newContent = MY_ACTIVITIES_CONTENT;
			toast = Toast.makeText(this, "Mis actividades", Toast.LENGTH_LONG);
			toast.show();
			break;
		case R.id.drawer_myresults_item:
			newContent = MY_RESULTS_CONTENT;
			toast = Toast.makeText(this, "Mis resultados", Toast.LENGTH_LONG);
			toast.show();
			break;
			default: return;
		}
		
		if(newContent != currentContent){
			currentContent = newContent;
			setContent(currentContent);
		}
		drawerLayout.closeDrawer(GravityCompat.START);
	}
	
	private void setContent(int which){
		int newTitle;
		Fragment contentFragment;
		FragmentTransaction fragmentTransaction;
		
		switch(which){
		case NEW_ACTIVITY_CONTENT:
			newTitle = R.string.newactivity_title;
			contentFragment = new DummyFragment();
			break;
		case FALLING_DETECTION_CONTENT:
			newTitle = R.string.fallingdetection_title;
			contentFragment = new DummyFragment();
			break;
		case MY_ACTIVITIES_CONTENT:
			newTitle = R.string.myactivities_title;
			if(myActivitiesListFragment == null){
				myActivitiesListFragment = new AutoUpdateListFragment();
				//myActivitiesListFragment.setEmptyText(getResources().getString(R.string.myactivitieslist_empty));
				//TODO set adapter
				//TODO start loader
			}
			contentFragment = myActivitiesListFragment;
			break;
		case MY_RESULTS_CONTENT:
			newTitle = R.string.myresults_title;
			contentFragment = new DummyFragment();
			break;
		default: return;
		}
		
		fragmentTransaction = getSupportFragmentManager().beginTransaction();
		fragmentTransaction.replace(R.id.main_content_frame, contentFragment);
		fragmentTransaction.commit();
		setTitle(newTitle);
	}
	
	public static class DummyFragment extends Fragment{		
		@Override
		public android.view.View onCreateView(android.view.LayoutInflater inflater, android.view.ViewGroup container, Bundle savedInstanceState){
			return inflater.inflate(android.R.layout.simple_list_item_1, container, false);
		}
	}

}
