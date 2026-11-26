package org.ikasan.spec.scheduled.instance.dao;

import org.ikasan.spec.scheduled.instance.model.*;
import org.ikasan.spec.search.SearchResults;

import java.util.List;

public interface SchedulerJobInstanceDao {

    /**
     * Get a scheduled job instance record by id.
     *
     * @param id
     * @return
     */
    SchedulerJobInstanceRecord findById(String id);


    /**
     * Saves a list of scheduler job instance records.
     *
     * @param scheduledContextInstanceRecord the list of {@link SchedulerJobInstanceRecord} objects to be saved
     */
    void save(List<SchedulerJobInstanceRecord> scheduledContextInstanceRecord);

    /**
     * Save a scheduled job instance record.
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
    SearchResults<SchedulerJobInstanceRecord> getSchedulerJobInstancesByContextName(String contextName
        , int limit, int offset, String sortField, String sortDirection);


    /**
     * Checks if a job plan instance contains any repeating jobs.
     *
     * @param contextInstanceId the ID of the context instance
     * @return true if the job plan instance contains repeating jobs, otherwise false
     */
    boolean doesJobPlanInstanceContainRepeatingJobs(String contextInstanceId);


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

    /**
     * Get a list of scheduler context instance aggregate jobs statuses. This method takes into account jobs that appear
     * in multiple child job plans.
     *
     * @param contextInstanceIds
     * @return
     */
    List<ContextInstanceAggregateJobStatus> getJobStatusCountForContextInstancesConsiderNonTargetedDuplication(List<String> contextInstanceIds);

    /**
     * Delete all scheduler job instances for a context instance.
     *
     * @param contextInstanceId
     */
    void deleteSchedulerJobInstances(String contextInstanceId);

    /**
     * Find the scheduler job instance identified by its context instance, job name and child context name.
     * <p>
     * A job name is only unique within a child context, as the same job can appear in several child job
     * plans. These three values together identify at most one job instance.
     *
     * @param contestInstanceId the id of the context instance the job instance belongs to
     * @param jobName the name of the job
     * @param childContextName the name of the child context (job plan) containing the job
     * @return the matching scheduler job instance record, or <code>null</code> if no job instance matches
     */
    SchedulerJobInstanceRecord findByContextIdJobNameChildContextName(String contestInstanceId, String jobName, String childContextName);
}
