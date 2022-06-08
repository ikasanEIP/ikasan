package org.ikasan.spec.scheduled.instance.dao;

import org.ikasan.spec.scheduled.instance.model.ScheduledContextInstanceRecord;
import org.ikasan.spec.search.SearchResults;

public interface ScheduledContextInstanceAuditDao {

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
