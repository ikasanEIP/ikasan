package org.ikasan.spec.scheduled.job.dao;

import org.ikasan.spec.scheduled.job.model.QuartzScheduleDrivenJobRecord;
import org.ikasan.spec.search.SearchResults;

import java.util.List;

public interface QuartzScheduleDrivenJobDao<T extends QuartzScheduleDrivenJobRecord> {

    SearchResults<? extends T> findAll(int limit, int offset);

    SearchResults<? extends T> findByContext(String contextId, int limit, int offset);

    T findById(String id);

    void save(T record);
}
