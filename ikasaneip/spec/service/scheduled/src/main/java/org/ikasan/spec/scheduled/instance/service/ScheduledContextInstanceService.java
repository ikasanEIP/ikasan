package org.ikasan.spec.scheduled.instance.service;

import org.ikasan.spec.scheduled.instance.model.ScheduledContextInstanceRecord;

public interface ScheduledContextInstanceService {
    ScheduledContextInstanceRecord findById(String id);

    void save(ScheduledContextInstanceRecord scheduledContextInstanceRecord);
}
