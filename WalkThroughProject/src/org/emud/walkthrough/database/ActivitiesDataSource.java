package org.emud.walkthrough.database;

import java.util.GregorianCalendar;

import org.emud.content.observer.Subject;
import org.emud.walkthrough.model.WalkActivity;

import android.database.Cursor;

public interface ActivitiesDataSource {
	
	public Subject getActivitiesSubject();

	public Cursor getActivities(GregorianCalendar startDate, GregorianCalendar endDate);
	
	public Cursor getAllActivities();

	public long createNewActivity(WalkActivity act);

}
