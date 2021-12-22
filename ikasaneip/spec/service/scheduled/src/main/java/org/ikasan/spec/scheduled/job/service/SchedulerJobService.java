package org.ikasan.spec.scheduled.job.service;

import org.ikasan.spec.scheduled.job.model.FileEventDrivenJobRecord;
import org.ikasan.spec.scheduled.job.model.InternalEventDrivenJobRecord;
import org.ikasan.spec.scheduled.job.model.QuartzScheduleDrivenJobRecord;

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
}
