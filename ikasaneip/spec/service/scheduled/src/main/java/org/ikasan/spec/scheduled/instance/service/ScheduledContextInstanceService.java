package org.ikasan.spec.scheduled.instance.service;

import java.util.List;

import org.ikasan.spec.scheduled.instance.model.*;
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
     * Delete context instance record by id.
     *
     * @param id
     */
    void deleteById(String id);

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
     * @param scheduledContextInstanceAuditAggregateRecord
     * @param previousContextInstance
     * @param updatedContextInstance
     */
    void saveAudit(ScheduledContextInstanceAuditAggregateRecord scheduledContextInstanceAuditAggregateRecord, ContextInstance previousContextInstance,
                   ContextInstance updatedContextInstance);

    /**
     * Find a context instance audit record by id.
     *
     * @param id
     * @return
     */
    ScheduledContextInstanceRecord findAuditRecordById(String id);

    /**
     * Get scheduled context instance audit records.
     *
     * @param limit
     * @param offset
     * @param sortField
     * @param sortDirection
     * @return
     */
    SearchResults<ScheduledContextInstanceAuditAggregateRecord> findAllAuditRecords(int limit, int offset, String sortField, String sortDirection);

    /**
     * Get all scheduled context instance audit records for a context id.
     *
     * @param filter
     * @param limit
     * @param offset
     * @param sortField
     * @param sortDirection
     * @return
     */
    SearchResults<ScheduledContextInstanceAuditAggregateRecord> findAllAuditRecordsByFilter(ScheduledContextInstanceAuditAggregateSearchFilter filter
        , int limit, int offset, String sortField, String sortDirection);

    /**
     * Get scheduled context instance records by statuses with limit and offset.
     *
     * @param instanceStatuses
     * @param limit
     * @param offset
     * @return
     */
    SearchResults<ScheduledContextInstanceRecord> getScheduledContextInstancesByStatus(List<InstanceStatus> instanceStatuses, int limit, int offset);

    /**
     * Get scheduled context instance records by context name with limit and offset.
     *
     * @param contextName
     * @param limit
     * @param offset
     * @return
     */
    SearchResults<ScheduledContextInstanceRecord> getScheduledContextInstancesByContextName(String contextName, int limit, int offset, String sortField, String sortDirection);

    /**
     * Get scheduled context instance records by context name with limit and offset, within a give time window.
     *
     * @param contextName
     * @param startTimestamp
     * @param endTimestamp
     * @param limit
     * @param offset
     * @param sortField
     * @param sortDirection
     * @return
     */
    SearchResults<ScheduledContextInstanceRecord> getScheduledContextInstancesByContextName(String contextName, long startTimestamp, long endTimestamp, int limit, int offset, String sortField, String sortDirection);

    /**
     * Get scheduled context instance records by filter with limit and offset.
     *
     * @param filter
     * @param limit
     * @param offset
     * @param sortField
     * @param sortDirection
     * @return
     */
    SearchResults<ScheduledContextInstanceRecord> getScheduledContextInstancesByFilter(ContextInstanceSearchFilter filter, int limit, int offset, String sortField, String sortDirection);

}
