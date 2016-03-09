package org.ikasan.spec.replay;

import java.sql.Date;
import java.util.List;

public interface ReplayManagementService<EVENT>
{
	/**
	 * Get all replay events for a given date range.
	 * 
	 * @param fromDate
	 * @param toDate
	 * @return
	 */
	public List<EVENT> getReplayEvents(Date fromDate, Date toDate);
}
