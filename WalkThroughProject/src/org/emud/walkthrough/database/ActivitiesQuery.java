package org.emud.walkthrough.database;


import org.emud.content.Query;
import org.emud.walkthrough.DateFilter;

import android.database.Cursor;

public class ActivitiesQuery implements Query<Cursor> {
	private DateFilter dateFilter;
	private ActivitiesDataSource dataSource;
	
	
	public ActivitiesQuery(ActivitiesDataSource source, DateFilter filter){
		dataSource = source;
		dateFilter = filter;
	}
	
	
	
	@Override
	public Cursor execute() {
		return dataSource.getActivities(dateFilter.getFromDate(), dateFilter.getToDate());
	}

}
