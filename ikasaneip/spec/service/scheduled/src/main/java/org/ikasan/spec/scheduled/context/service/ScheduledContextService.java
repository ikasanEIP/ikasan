package org.ikasan.spec.scheduled.context.service;

import org.ikasan.spec.scheduled.context.model.ScheduledContextRecord;

import java.util.List;

public interface ScheduledContextService {

    List<? extends ScheduledContextRecord> findAll();

    ScheduledContextRecord findById(String id);

    void save(ScheduledContextRecord scheduledContextRecord);
}
