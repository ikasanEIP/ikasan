package org.ikasan.spec.scheduled.instance.dao;

import org.ikasan.spec.scheduled.instance.model.ScheduledContextInstanceRecord;

public interface ScheduledContextInstanceDao {
    ScheduledContextInstanceRecord findById(String id);

    void save(ScheduledContextInstanceRecord scheduledContextInstanceRecord);
}
