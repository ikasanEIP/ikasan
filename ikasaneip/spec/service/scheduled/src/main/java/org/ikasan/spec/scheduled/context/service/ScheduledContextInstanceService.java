package org.ikasan.spec.scheduled.context.service;

import org.ikasan.spec.scheduled.context.model.ScheduledContextInstanceRecord;

public interface ScheduledContextInstanceService {
    ScheduledContextInstanceRecord findById(String id);

    void save(ScheduledContextInstanceRecord scheduledContextInstanceRecord);
}
