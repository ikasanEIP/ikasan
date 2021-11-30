package org.ikasan.spec.scheduled.context.dao;

import org.ikasan.spec.scheduled.context.model.ScheduledContextInstanceRecord;

public interface ScheduledContextInstanceDao {
    ScheduledContextInstanceRecord findById(String id);

    void save(ScheduledContextInstanceRecord scheduledContextInstanceRecord);
}
