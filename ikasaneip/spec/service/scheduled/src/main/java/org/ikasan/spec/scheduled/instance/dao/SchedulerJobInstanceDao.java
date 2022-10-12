package org.ikasan.spec.scheduled.instance.dao;

import org.ikasan.spec.scheduled.instance.model.*;
import org.ikasan.spec.search.SearchResults;

import java.util.List;

public interface SchedulerJobInstanceDao {

    /**
     * Get a scheduled context instance record by id.
     *
     * @param id
     * @return
     */
    SchedulerJobInstanceRecord findById(String id);

    /**
     * Save a scheduled context instance record.
     *
     * @param scheduledContextInstanceRecord
     */
    void save(SchedulerJobInstanceRecord scheduledContextInstanceRecord);

    /**
     * Get scheduler job instances by context instance id
     *
     * @param contextInstanceId
     * @param limit
     * @param offset
     * @param sortField
     * @param sortDirection
     * @return
     */
    SearchResults<SchedulerJobInstanceRecord> getSchedulerJobInstancesByContextInstanceId(String contextInstanceId, int limit, int offset, String sortField, String sortDirection);

    /**
     * Get scheduler job instances by context name
     *
     * @param contextName
     * @param limit
     * @param offset
     * @param sortField
     * @param sortDirection
     * @return
     */
    SearchResults<SchedulerJobInstanceRecord> getSchedulerJobInstancesByContextName(String contextName, int limit, int offset, String sortField, String sortDirection);


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
    SearchResults<SchedulerJobInstanceRecord> getScheduledContextInstancesByFilter(SchedulerJobInstanceSearchFilter filter, int limit, int offset, String sortField, String sortDirection);

    /**
     * Get a list of scheduler context instance aggregate jobs statuses.
     *
     * @param contextInstanceIds
     * @return
     */
    List<ContextInstanceAggregateJobStatus> getJobStatusCountForContextInstances(List<String> contextInstanceIds);
}
