package org.ikasan.spec.scheduled.job.dao;

import org.ikasan.spec.scheduled.job.model.GlobalEventJobRecord;
import org.ikasan.spec.search.SearchResults;

import java.util.List;

public interface GlobalEventJobDao <T extends GlobalEventJobRecord> {

    SearchResults<? extends T> findAll(int limit, int offset);

    SearchResults<? extends T> findByContext(String contextId, int limit, int offset);

    T findById(String id);

    void save(T record);

    /**
     * Set a InternalEventDrivenJobRecord to skip. If targetResidingContextOnly is set
     * on the InternalEventDrivenJob the childContextNames contain the specific child
     * contexts that the job will be skipped in.
     *
     * @param jobRecord
     * @param childContextNames
     * @param actor
     */
    void skip(T jobRecord, List<String> childContextNames, String actor);

    /**
     * Set a InternalEventDrivenJobRecord to enabled. Enabled is skipped == false.
     *
     * @param jobRecord
     * @param actor
     */
    void enable(T jobRecord, String actor);
}
