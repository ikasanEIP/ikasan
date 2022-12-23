package org.ikasan.spec.scheduled.job.dao;

import org.ikasan.spec.scheduled.job.model.GlobalEventJobRecord;
import org.ikasan.spec.search.SearchResults;

public interface GlobalEventJobDao <T extends GlobalEventJobRecord> {

    SearchResults<? extends T> findAll(int limit, int offset);

    SearchResults<? extends T> findByContext(String contextId, int limit, int offset);

    T findById(String id);

    void save(T record);
}
