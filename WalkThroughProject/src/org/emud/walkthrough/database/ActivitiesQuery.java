package org.emud.walkthrough.database;

import java.util.List;

import org.emud.content.Query;
import org.emud.walkthrough.DateFilter;
import org.emud.walkthrough.model.WalkActivity;


public class ActivitiesQuery implements Query<List<WalkActivity> > {
	private DateFilter dateFilter;
	private ActivitiesDataSource dataSource;
	
	
	public ActivitiesQuery(ActivitiesDataSource source, DateFilter filter){
		dataSource = source;
		dateFilter = filter;
	}
	
	@Override
	public List<WalkActivity> execute() {
		return dataSource.getActivities(dateFilter.getFromDate(), dateFilter.getToDate());
	}

}
