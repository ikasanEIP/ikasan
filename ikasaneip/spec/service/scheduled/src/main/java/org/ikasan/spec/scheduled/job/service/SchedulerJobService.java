package org.ikasan.spec.scheduled.job.service;

import org.ikasan.spec.scheduled.job.model.*;
import org.ikasan.spec.search.SearchResults;

import java.util.ArrayList;
import java.util.List;

public interface SchedulerJobService<T extends SchedulerJobRecord> {

    SearchResults<? extends T> findByAgent(String agent, int limit, int offset);

    void delete(T record);

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

    public void saveFileEventDrivenJobRecords(List<FileEventDrivenJobRecord> fileEventDrivenJobRecords);

    public void saveInternalEventDrivenJobRecords(List<InternalEventDrivenJobRecord> internalEventDrivenJobRecord);

    public void saveQuartzScheduledJobRecords(List<QuartzScheduleDrivenJobRecord> quartzScheduleDrivenJobRecord);

    public void saveInternalEventDrivenJobs(List<InternalEventDrivenJob> quartzScheduleDrivenJobs);

    public void saveQuartzScheduledJobs(List<QuartzScheduleDrivenJob> quartzScheduleDrivenJobs);

    public void saveFileEventDrivenJobs(List<FileEventDrivenJob> quartzScheduleDrivenJobs);
}
