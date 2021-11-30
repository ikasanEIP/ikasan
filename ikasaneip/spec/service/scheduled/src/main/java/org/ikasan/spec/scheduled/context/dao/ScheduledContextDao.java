package org.ikasan.spec.scheduled.context.dao;

import org.ikasan.spec.scheduled.context.model.ScheduledContextRecord;

import java.util.List;

public interface ScheduledContextDao {

    public List<? extends ScheduledContextRecord> findAll();

    public ScheduledContextRecord findById(String id);

    void save(ScheduledContextRecord scheduledContextRecord);
}
