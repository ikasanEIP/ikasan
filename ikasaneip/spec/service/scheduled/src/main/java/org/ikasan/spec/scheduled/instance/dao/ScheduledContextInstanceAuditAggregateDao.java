package org.ikasan.spec.scheduled.instance.dao;

import org.ikasan.spec.scheduled.instance.model.ScheduledContextInstanceAuditAggregateRecord;
import org.ikasan.spec.scheduled.instance.model.ScheduledContextInstanceAuditAggregateSearchFilter;
import org.ikasan.spec.search.SearchResults;

public interface ScheduledContextInstanceAuditAggregateDao {

    /**
     * Save a scheduled context instance audit record.
     *
     * @param scheduledContextInstanceAuditAggregateRecord
     */
    void save(ScheduledContextInstanceAuditAggregateRecord scheduledContextInstanceAuditAggregateRecord);

    /**
     * Get scheduled context instance audit records.
     *
     * @param limit
     * @param offset
     * @param sortField
     * @param sortDirection
     * @return
     */
    SearchResults<ScheduledContextInstanceAuditAggregateRecord> findAll(int limit, int offset, String sortField, String sortDirection);

    /**
     * Get all scheduled context instance audit records based on the filter.
     *
     * @param filter
     * @param limit
     * @param offset
     * @param sortField
     * @param sortDirection
     * @return
     */
    SearchResults<ScheduledContextInstanceAuditAggregateRecord> findScheduledContextInstanceAuditAggregateRecordsByFilter(ScheduledContextInstanceAuditAggregateSearchFilter filter
        , int limit, int offset, String sortField, String sortDirection);
}
