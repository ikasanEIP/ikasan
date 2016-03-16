package org.ikasan.spec.replay;

import java.util.Date;
import java.util.List;

public interface ReplayManagementService<EVENT, AUDIT>
{
	/**
	 *  Get all replay events for a given date range.
	 *  
	 * @param moduleNames
	 * @param flowNames
	 * @param payloadContent
	 * @param eventId
	 * @param fromDate
	 * @param toDate
	 * @return
	 */
	public List<EVENT> getReplayEvents(List<String> moduleNames, List<String> flowNames,
			String payloadContent, String eventId, Date fromDate, Date toDate);
	
	/**
     * Get a list of audits depending upon search criteria.
     * 
     * @param user
     * @param startDate
     * @param endDate
     * @return
     */
    public List<AUDIT> getReplayAudits(String user, Date startDate, Date endDate);
}
