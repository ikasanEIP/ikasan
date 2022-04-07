package org.ikasan.spec.scheduled.instance.service;

import java.util.List;

import org.ikasan.spec.scheduled.instance.model.InstanceStatus;
import org.ikasan.spec.scheduled.instance.model.ScheduledContextInstanceAuditRecord;
import org.ikasan.spec.scheduled.instance.model.ScheduledContextInstanceRecord;
import org.ikasan.spec.search.SearchResults;

public interface ScheduledContextInstanceService {

    /**
     * Get a scheduled context instance record by id.
     *
     * @param id
     * @return
     */
    ScheduledContextInstanceRecord findById(String id);

    /**
     * Save a scheduled context instance record.
     *
     * @param scheduledContextInstanceRecord
     */
    void save(ScheduledContextInstanceRecord scheduledContextInstanceRecord);

    /**
     * Get a scheduled context instance record by statuses.
     *
     * @param instanceStatuses
     * @return
     */
    SearchResults<ScheduledContextInstanceRecord> getScheduledContextInstancesByStatus(List<InstanceStatus> instanceStatuses);

    /**
     * Save a scheduled context instance audit record.
     *
     * @param scheduledContextInstanceAuditRecord
     */
    void saveAudit(ScheduledContextInstanceAuditRecord scheduledContextInstanceAuditRecord);

    /**
     * Get scheduled context instance audit records.
     *
     * @param limit
     * @param offset
     * @return
     */
    SearchResults<ScheduledContextInstanceAuditRecord> findAllAuditRecords(int limit, int offset);

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
