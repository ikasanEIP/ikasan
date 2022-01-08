package org.ikasan.spec.scheduled.job.service;

import org.ikasan.spec.scheduled.job.model.InternalEventDrivenJobRecord;
import org.ikasan.spec.search.SearchResults;

public interface InternalEventDrivenJobService {

    SearchResults<InternalEventDrivenJobRecord> findAll(int limit, int offset);

    SearchResults<InternalEventDrivenJobRecord> findByContext(String contextId, int limit, int offset);

    InternalEventDrivenJobRecord findById(String id);

    void save(InternalEventDrivenJobRecord record);
}
