package org.ikasan.spec.scheduled.instance.dao;

import org.ikasan.spec.scheduled.instance.model.ScheduledContextInstanceAuditAggregateRecord;
import org.ikasan.spec.scheduled.instance.model.ScheduledContextInstanceAuditAggregateSearchFilter;
import org.ikasan.spec.search.SearchResults;

import java.util.List;
import java.util.Map;

public interface ScheduledContextInstanceAuditAggregateDao {


    /**
     * Saves the given ScheduledContextInstanceAuditAggregateRecord.
     *
     * @param scheduledContextInstanceAuditAggregateRecord The ScheduledContextInstanceAuditAggregateRecord to be saved.
     */
    void save(ScheduledContextInstanceAuditAggregateRecord scheduledContextInstanceAuditAggregateRecord);


    /**
     * Retrieves all ScheduledContextInstanceAuditAggregateRecord objects that match the given criteria.
     *
     * @param limit            The maximum number of records to return.
     * @param offset           The number of records to skip before starting to return records.
     * @param sortField        The field to use for sorting the records.
     * @param sortDirection    The direction of sorting (ascending or descending).
     * @return A SearchResults object containing the matching ScheduledContextInstanceAuditAggregateRecord objects,
     *         the total number of results, and the response time of the query.
     */
    SearchResults<ScheduledContextInstanceAuditAggregateRecord> findAll(int limit, int offset, String sortField, String sortDirection);


    /**
     * Searches for ScheduledContextInstanceAuditAggregateRecord objects that match the given filter criteria.
     *
     * @param filter            The filter criteria to be applied during the search.
     * @param limit             The maximum number of records to be returned in the search result.
     * @param offset            The offset of the first record to be returned in the search result.
     * @param sortField         The field to be used for sorting the search result.
     * @param sortDirection     The direction of sorting (ascending or descending).
     * @return A SearchResults object that contains the matching ScheduledContextInstanceAuditAggregateRecord objects,
     *                          the total number of results, and the response time of the query.
     */
    SearchResults<ScheduledContextInstanceAuditAggregateRecord> findScheduledContextInstanceAuditAggregateRecordsByFilter(ScheduledContextInstanceAuditAggregateSearchFilter filter
        , int limit, int offset, String sortField, String sortDirection);

    /**
     * Retrieves the count of repeating job statuses for the given context instance IDs.
     *
     * @param contextInstanceIds The list of context instance IDs.
     * @return A map that contains the count of repeating job statuses for each context instance ID. The outer map's key is the context instance ID,
     *         and the value is an inner map that contains the count of each job status as key-value pairs. The inner map's key is the job status,
     *         and the value is the count of that job status.
     */
    Map<String, Map<String, Integer>> getRepeatingJobStatusCounts(List<String> contextInstanceIds);
}
