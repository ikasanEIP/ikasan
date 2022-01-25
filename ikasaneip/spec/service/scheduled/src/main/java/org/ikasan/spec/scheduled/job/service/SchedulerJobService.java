package org.ikasan.spec.scheduled.job.service;

import org.ikasan.spec.scheduled.job.model.*;

public interface SchedulerJobService {

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
}
