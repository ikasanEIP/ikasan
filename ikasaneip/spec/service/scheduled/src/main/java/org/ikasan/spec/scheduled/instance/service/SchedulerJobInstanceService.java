package org.ikasan.spec.scheduled.instance.service;

import org.ikasan.spec.scheduled.instance.model.*;
import org.ikasan.spec.scheduled.instance.service.exception.SchedulerJobInstanceInitialisationException;
import org.ikasan.spec.search.SearchResults;

import java.util.List;

public interface SchedulerJobInstanceService {

    /**
     * Get a scheduled context instance record by id.
     *
     * @param id
     * @return
     */
    SchedulerJobInstanceRecord findById(String id);

    /**
     * Find by context id, job name, and child context name. There can only be one job with these coordinates.
     *
     * @param uuid
     * @param jobName
     * @param childContextName
     * @return
     */
    SchedulerJobInstanceRecord findByContextIdJobNameChildContextName(String uuid, String jobName, String childContextName);

    /**
     * Save a scheduled context instance record.
     *
     * @param scheduledContextInstanceRecord
     */
    void save(SchedulerJobInstanceRecord scheduledContextInstanceRecord);

    /**
     * Method to update an existing schedulerJobInstance.
     *
     * @param schedulerJobInstance
     */
    void update(SchedulerJobInstance schedulerJobInstance);

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
     * Initialise scheduler job instances for a given context instances. This method will initialise the instances and persist them before returning the new instances.
     *
     * @param contextInstance
     * @return
     */
    List<SchedulerJobInstance> initialiseSchedulerJobInstancesForContext(ContextInstance contextInstance) throws SchedulerJobInstanceInitialisationException;

    /**
     * Get a list of scheduler context instance aggregate jobs statuses.
     *
     * @param contextInstanceIds
     * @return
     */
    List<ContextInstanceAggregateJobStatus> getJobStatusCountForContextInstances(List<String> contextInstanceIds);
}
