package org.ikasan.spec.scheduled.job.dao;

import org.ikasan.spec.scheduled.job.model.GlobalEventJobRecord;
import org.ikasan.spec.search.SearchResults;

import java.util.List;

public interface GlobalEventJobDao <T extends GlobalEventJobRecord> {

    /**
     * Finds all records of type T within the specified limit and offset.
     *
     * @param limit The maximum number of records to retrieve.
     * @param offset The number of records to skip from the beginning of the result set.
     * @return A {@link SearchResults} object containing a list of records and additional information about the query.
     */
    SearchResults<? extends T> findAll(int limit, int offset);

    /**
     * Finds all records of type T within the specified limit and offset based on the given contextId.
     *
     * @param contextId The ID of the context to filter the records by.
     * @param limit The maximum number of records to retrieve.
     * @param offset The number of records to skip from the beginning of the result set.
     * @return A {@link SearchResults} object containing a list of records and additional information about the query.
     */
    SearchResults<? extends T> findByContext(String contextId, int limit, int offset);

    /**
     * Finds a record of type T by its ID.
     *
     * @param id The ID of the record to find.
     * @return The record of type T with the specified ID, or null if not found.
     */
    T findById(String id);

    /**
     * Saves a record of type T.
     *
     * @param record The record to be saved.
     * @param <T>    The type of record to save.
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
     * Set a InternalEventDrivenJobRecord to enabled. Enabled is skipped == false.
     *
     * @param jobRecord
     * @param actor
     */
    void enable(T jobRecord, String actor);
}
