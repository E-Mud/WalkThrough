package org.emud.walkthrough.database;

import java.util.GregorianCalendar;
import java.util.List;

import org.emud.content.observer.Subject;
import org.emud.walkthrough.model.WalkActivity;
import org.emud.walkthrough.model.Result;

import android.database.Cursor;

public interface ActivitiesDataSource {
	
	public Subject getActivitiesSubject();

	public Cursor getActivities(GregorianCalendar startDate, GregorianCalendar endDate);
	
	public Cursor getAllActivities();

	public long createNewActivity(WalkActivity act);
	
	public List<Result> getActivityResults(long activity_id);
	
	public Cursor getResults(int type, GregorianCalendar startDate, GregorianCalendar endDate);
	
	public Cursor getResults(int type);

}
