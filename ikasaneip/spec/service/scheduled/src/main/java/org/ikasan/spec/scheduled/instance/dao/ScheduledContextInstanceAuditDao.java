package org.ikasan.spec.scheduled.instance.dao;

import org.ikasan.spec.scheduled.instance.model.ScheduledContextInstanceRecord;
import org.ikasan.spec.search.SearchResults;

public interface ScheduledContextInstanceAuditDao {

    String SCHEDULED_CONTEXT_INSTANCE_AUDIT_TYPE = "scheduledContextInstanceAudit";
    String SCHEDULED_CONTEXT_INSTANCE_AUDIT_ID = "scheduledContextInstanceAuditId";

    /**
     * Save a scheduled context instance audit record.
     *
     * @param scheduledContextInstanceRecord
     */
    void save(ScheduledContextInstanceRecord scheduledContextInstanceRecord);

    /**
     * Find a context instance audit record.
     *
     * @param id
     * @return
     */
    ScheduledContextInstanceRecord findById(String id);

}
