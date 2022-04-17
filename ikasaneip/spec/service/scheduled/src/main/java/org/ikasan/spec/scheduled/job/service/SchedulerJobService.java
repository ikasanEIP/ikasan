package org.ikasan.spec.scheduled.job.service;

import org.ikasan.spec.scheduled.job.model.*;
import org.ikasan.spec.search.SearchResults;

import java.util.ArrayList;
import java.util.List;

public interface SchedulerJobService<T extends SchedulerJobRecord> {

    /**
     * Find jobs that are associated with an agent.
     *
     * @param agent
     * @param limit
     * @param offset
     * @return
     */
    SearchResults<? extends T> findByAgent(String agent, int limit, int offset);

    /**
     * Find a job by context and job name.
     *
     * @param contextId
     * @param jobName
     * @return
     */
    T findByContextIdAndJobName(String contextId, String jobName);

    /**
     * Delete an individual job record.
     *
     * @param record
     */
    void delete(T record);

    /**
     * Delete all jobs associated with an agent
     *
     * @param agentName
     */
    void deleteByAgentName(String agentName);

    /**
     * Save a FileEventDrivenJobRecord
     * @param fileEventDrivenJobRecord
     */
    void saveFileEventDrivenJobRecord(FileEventDrivenJobRecord fileEventDrivenJobRecord);

    /**
     * Save a InternalEventDrivenJobRecord
     *
     * @param internalEventDrivenJobRecord
     */
    void saveInternalEventDrivenJobRecord(InternalEventDrivenJobRecord internalEventDrivenJobRecord);

    /**
     * Save a QuartzScheduleDrivenJobRecord
     *
     * @param quartzScheduleDrivenJobRecord
     */
    void saveQuartzScheduledJobRecord(QuartzScheduleDrivenJobRecord quartzScheduleDrivenJobRecord);

    /**
     * Save a FileEventDrivenJob
     *
     * @param fileEventDrivenJob
     */
    void saveFileEventDrivenJob(FileEventDrivenJob fileEventDrivenJob);

    /**
     * Save a InternalEventDrivenJob
     *
     * @param internalEventDrivenJob
     */
    void saveInternalEventDrivenJob(InternalEventDrivenJob internalEventDrivenJob);

    /**
     * Save a QuartzScheduleDrivenJob
     *
     * @param quartzScheduleDrivenJob
     */
    void saveQuartzScheduledJob(QuartzScheduleDrivenJob quartzScheduleDrivenJob);

    /**
     * Save a list of FileEventDrivenJobRecord.
     *
     * @param fileEventDrivenJobRecords
     */
    public void saveFileEventDrivenJobRecords(List<FileEventDrivenJobRecord> fileEventDrivenJobRecords);

    /**
     * Save a list of InternalEventDrivenJobRecord
     *
     * @param internalEventDrivenJobRecord
     */
    public void saveInternalEventDrivenJobRecords(List<InternalEventDrivenJobRecord> internalEventDrivenJobRecord);

    /**
     * Save a list of QuartzScheduleDrivenJobRecord.
     *
     * @param quartzScheduleDrivenJobRecord
     */
    public void saveQuartzScheduledJobRecords(List<QuartzScheduleDrivenJobRecord> quartzScheduleDrivenJobRecord);

    /**
     * Save a list of InternalEventDrivenJob.
     *
     * @param quartzScheduleDrivenJobs
     */
    public void saveInternalEventDrivenJobs(List<InternalEventDrivenJob> quartzScheduleDrivenJobs);

    /**
     * Save a list of QuartzScheduleDrivenJob.
     * @param quartzScheduleDrivenJobs
     */
    public void saveQuartzScheduledJobs(List<QuartzScheduleDrivenJob> quartzScheduleDrivenJobs);

    /**
     * Save a list of FileEventDrivenJob.
     *
     * @param quartzScheduleDrivenJobs
     */
    public void saveFileEventDrivenJobs(List<FileEventDrivenJob> quartzScheduleDrivenJobs);
}
