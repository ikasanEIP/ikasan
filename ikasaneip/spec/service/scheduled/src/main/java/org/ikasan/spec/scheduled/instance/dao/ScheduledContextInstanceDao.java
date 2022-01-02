package org.ikasan.spec.scheduled.instance.dao;

import org.ikasan.spec.scheduled.instance.model.InstanceStatus;
import org.ikasan.spec.scheduled.instance.model.ScheduledContextInstanceRecord;

import java.util.List;

public interface ScheduledContextInstanceDao {

    /**
     * Get a scheduled context instance record by id.
     *
     * @param id
     * @return
     */
    ScheduledContextInstanceRecord findById(String id);

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
    List<? extends  ScheduledContextInstanceRecord> getScheduledContextInstancesByStatus(List<InstanceStatus> instanceStatuses);
}
