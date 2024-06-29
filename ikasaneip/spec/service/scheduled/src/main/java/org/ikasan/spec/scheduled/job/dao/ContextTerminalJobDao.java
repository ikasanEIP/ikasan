package org.ikasan.spec.scheduled.job.dao;

import org.ikasan.spec.scheduled.job.model.ContextTerminalJobRecord;
import org.ikasan.spec.search.SearchResults;

public interface ContextTerminalJobDao<T extends ContextTerminalJobRecord> {

    /**
     * Retrieves a list of search results for a specified limit and offset.
     *
     * @param limit The maximum number of results to retrieve.
     * @param offset The starting index of the results to retrieve.
     * @return A list of search results with a total count and query response time.
     */
    SearchResults<? extends T> findAll(int limit, int offset);

    /**
     * Retrieves a list of search results for a specified limit and offset within a given context.
     *
     * @param contextId The ID of the context.
     * @param limit The maximum number of results to retrieve.
     * @param offset The starting index of the results to retrieve.
     * @return A {@link SearchResults} object containing the search results within the specified context.
     */
    SearchResults<? extends T> findByContext(String contextId, int limit, int offset);

    /**
     * Retrieves the object with the specified ID.
     *
     * @param id The ID of the object to retrieve.
     * @return The object with the specified ID.
     */
    T findById(String id);

    /**
     * Persists the given record.
     *
     * @param record The record to be saved.
     */
    void save(T record);
}
