package org.ikasan.spec.scheduled.job.dao;

import org.ikasan.spec.scheduled.job.model.InternalEventDrivenJobRecord;
import org.ikasan.spec.search.SearchResults;

import java.util.List;

public interface InternalEventDrivenJobDao<T extends InternalEventDrivenJobRecord> {

    /**
     * Fina all InternalEventDrivenJobRecord with limit and offset for paging.
     *
     * @param limit
     * @param offset
     * @return
     */
    SearchResults<? extends T> findAll(int limit, int offset);

    /**
     * Find by context name with limit and offset for paging.
     *
     * @param contextName
     * @param limit
     * @param offset
     * @return
     */
    SearchResults<? extends T> findByContext(String contextName, int limit, int offset);

    /**
     * Find InternalEventDrivenJobRecord by its id.
     * @param id
     * @return
     */
    T findById(String id);

    /**
     * Save InternalEventDrivenJobRecord.
     *
     * @param record
     */
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
     * Set a InternalEventDrivenJobRecord to hold. If targetResidingContextOnly is set
     * on the InternalEventDrivenJob the childContextNames contain the specific child
     * contexts that the job will be held in.
     *
     * @param jobRecord
     * @param childContextNames
     * @param actor
     */
    void hold(T jobRecord, List<String> childContextNames, String actor);

    /**
     * Set a InternalEventDrivenJobRecord to enabled. Enabled is skipped == false.
     *
     * @param jobRecord
     * @param actor
     */
    void enable(T jobRecord, String actor);

    /**
     * Set a InternalEventDrivenJobRecord to release. Release is held == false.
     *
     * @param jobRecord
     * @param actor
     */
    void release(T jobRecord, String actor);

    /**
     * Release all jobs that are held.
     *
     * @param jobRecords
     * @param actor
     */
    void releaseAll(List<T> jobRecords, String actor);

    /**
     * Enable all jobs that are skipped.
     *
     * @param jobRecords
     * @param actor
     */
    void enableAll(List<T> jobRecords, String actor);
}
