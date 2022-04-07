package org.ikasan.spec.scheduled.instance.dao;

import org.ikasan.spec.scheduled.instance.model.ScheduledContextInstanceAuditRecord;
import org.ikasan.spec.search.SearchResults;

public interface ScheduledContextInstanceAuditDao {

    /**
     * Save a scheduled context instance audit record.
     *
     * @param scheduledContextInstanceAuditRecord
     */
    void save(ScheduledContextInstanceAuditRecord scheduledContextInstanceAuditRecord);

    /**
     * Get scheduled context instance audit records.
     *
     * @param limit
     * @param offset
     * @return
     */
    SearchResults<ScheduledContextInstanceAuditRecord> findAll(int limit, int offset);

    /**
     * Get all scheduled context instance audit records for a context id.
     *
     * @param contextId
     * @param limit
     * @param offset
     * @return
     */
    SearchResults<ScheduledContextInstanceAuditRecord> findAllAuditRecordsByContextId(String contextId, int limit, int offset);
}
