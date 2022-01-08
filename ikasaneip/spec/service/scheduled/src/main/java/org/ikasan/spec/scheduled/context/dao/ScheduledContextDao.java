package org.ikasan.spec.scheduled.context.dao;

import org.ikasan.spec.scheduled.context.model.ScheduledContextRecord;
import org.ikasan.spec.search.SearchResults;

import java.util.List;

public interface ScheduledContextDao {

    SearchResults<ScheduledContextRecord> findAll();

    ScheduledContextRecord findById(String id);

    ScheduledContextRecord findByName(String name);

    void save(ScheduledContextRecord scheduledContextRecord);
}
