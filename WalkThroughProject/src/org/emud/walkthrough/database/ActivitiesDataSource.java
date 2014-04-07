package org.emud.walkthrough.database;

import java.util.GregorianCalendar;

import android.database.Cursor;

public interface ActivitiesDataSource {

	public Cursor getActivities(GregorianCalendar startDate, GregorianCalendar endDate);
	
	public Cursor getAllActivities();
}
