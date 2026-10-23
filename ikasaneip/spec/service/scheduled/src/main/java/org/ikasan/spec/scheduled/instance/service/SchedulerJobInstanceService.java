package org.ikasan.spec.scheduled.instance.service;

import org.ikasan.spec.scheduled.context.model.ContextTemplate;
import org.ikasan.spec.scheduled.instance.model.*;
import org.ikasan.spec.scheduled.instance.service.exception.SchedulerJobInstanceInitialisationException;
import org.ikasan.spec.search.SearchResults;

import java.util.List;
import java.util.Map;

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
     * Save a List of scheduled context instance records.
     *
     * @param scheduledContextInstanceRecords
     */
    void save(List<SchedulerJobInstanceRecord> scheduledContextInstanceRecords);

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
    SearchResults<SchedulerJobInstanceRecord> getSchedulerJobInstancesByContextInstanceId(String contextInstanceId
        , int limit, int offset, String sortField, String sortDirection);

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
    SearchResults<SchedulerJobInstanceRecord> getSchedulerJobInstancesByContextName(String contextName, int limit
        , int offset, String sortField, String sortDirection);


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
    SearchResults<SchedulerJobInstanceRecord> getScheduledContextInstancesByFilter(SchedulerJobInstanceSearchFilter filter
        , int limit, int offset, String sortField, String sortDirection);



    /**
     * Initialise scheduler job instances for a given context template, context instance, and parameters.
     *
     * @param contextTemplate The context template to use for initialisation
     * @param contextInstance The context instance to initialise job instances for
     * @param parameters The initialisation parameters
     * @return A list of initialised scheduler job instances
     * @throws SchedulerJobInstanceInitialisationException If an error occurs during initialisation
     */
    List<SchedulerJobInstance> initialiseSchedulerJobInstancesForContext(ContextTemplate contextTemplate, ContextInstance contextInstance
        , SchedulerJobInstancesInitialisationParameters parameters) throws SchedulerJobInstanceInitialisationException;

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
     * Helper method to get all command execution jobs associated with an context instance keyed on job identifier.
     *
     * @param contextInstanceId the id of the context instance that we want the jobs for.
     *
     * @return Map&lt;String, InternalEventDrivenJobInstance&gt; containing the command execution jobs
     * keyed on their identifier.
     */
    Map<String, InternalEventDrivenJobInstance> getCommandExecutionJobsForContextInstance(String contextInstanceId);

    /**
     * Helper method to get all command execution jobs associated with an context instance keyed on job identifier
     * and child context name.
     *
     * The results are key on job.getIdentifier() + "-" + jon.getChildContextName()
     *
     * @param contextInstanceId the id of the context instance that we want the jobs for.
     *
     * @return Map&lt;String, InternalEventDrivenJobInstance&gt; containing the command execution jobs
     * keyed on their identifier.
     */
    Map<String, InternalEventDrivenJobInstance> getCommandExecutionJobsForContextInstanceChildContext(String contextInstanceId);

    /**
     * Places all scheduler job instances within the specified context and child context on hold.
     *
     * @param contextInstance the context instance containing the jobs to be held
     * @param childContextName the name of the child context within the main context instance where jobs should be held
     * @return a list of {@link SchedulerJobInstanceRecord} representing the jobs that have been successfully placed on hold
     */
    List<SchedulerJobInstanceRecord> holdJobsWithinContext(ContextInstance contextInstance, String childContextName);

    /**
     * Retrieves a list of job instances within the specified context that are eligible to be released.
     *
     * @param contextInstance the context instance for which the jobs should be retrieved
     * @param childContextName the name of the child context within the main context instance
     * @return a list of {@link SchedulerJobInstanceRecord} representing the jobs to be released within the given context
     */
    List<SchedulerJobInstanceRecord> getJobsToReleaseWithinContext(ContextInstance contextInstance, String childContextName);

    /**
     * Disables all scheduler job instances within the specified context and child context.
     *
     * @param contextInstance the context instance containing the jobs to be disabled
     * @param childContextName the name of the child context within the main context instance where jobs should be disabled
     * @return a list of {@link SchedulerJobInstanceRecord} representing the jobs that have been successfully disabled
     */
    List<SchedulerJobInstanceRecord> disableJobsWithinContext(ContextInstance contextInstance, String childContextName);

    /**
     * Retrieves a list of job instances within the specified context that are eligible to be enabled.
     *
     * @param contextInstance the context instance containing the jobs to be enabled
     * @param childContextName the name of the child context within the main context instance where jobs should be enabled
     * @return a list of {@link SchedulerJobInstanceRecord} representing the jobs to be enabled within the given context
     */
    List<SchedulerJobInstanceRecord> getJobsToEnableWithinContext(ContextInstance contextInstance, String childContextName);

    /**
     * Deletes all scheduler job instances associated with the specified context instance.
     *
     * @param contextInstanceId the unique identifier of the context instance whose scheduler job instances are to be deleted
     */
    void deleteSchedulerJobInstances(String contextInstanceId);
}
