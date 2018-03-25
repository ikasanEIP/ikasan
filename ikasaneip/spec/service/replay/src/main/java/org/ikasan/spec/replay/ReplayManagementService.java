package org.ikasan.spec.replay;

import java.util.Date;
import java.util.List;


public interface ReplayManagementService<EVENT, AUDIT, AUDIT_EVENT>
{
    /**
     * Get all replay events for a given date range.
     *
     * @param moduleNames
     * @param flowNames
     * @param eventId
     * @param payloadContent
     * @param fromDate
     * @param toDate
     * @param resultSize
     * @return
	 */
	public List<EVENT> getReplayEvents(List<String> moduleNames, List<String> flowNames,
                                       String eventId, String payloadContent, Date fromDate, Date toDate, int resultSize);

	/**
     * Get a list of ReplayAudit depending upon search criteria.
     *
     * @param moduleNames
     * @param flowNames
     * @param eventId
     * @param user
     * @param startDate
     * @param endDate
     * @param resultSize
     * @return
	 */
    public List<AUDIT> getReplayAudits(List<String> moduleNames, List<String> flowNames,
			String eventId, String user, Date startDate, Date endDate);
    
    /**
     * Get a AUDIT by its id.
     * 
     * @param id
     * @return
     */
    public AUDIT getReplayAuditById(Long id);
    
    /**
     * Get a List of AUDIT_EVENT by their AUDIT id.
     * 
     * @param id
     * @return
     */
    public List<AUDIT_EVENT> getReplayAuditEventsByAuditId(Long id);
    
    /**
     * Get number of AUDIT_EVENT by their AUDIT id.
     * 
     * @param id
     * @return
     */
    public Long getNumberReplayAuditEventsByAuditId(Long id);

	/**
	 * Get the replay event by id.
	 *
	 * @param id
	 * @return
     */
	public EVENT getReplayEventById(Long id);
}
