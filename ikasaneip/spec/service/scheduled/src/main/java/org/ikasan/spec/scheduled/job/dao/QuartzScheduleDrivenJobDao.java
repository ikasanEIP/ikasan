package org.ikasan.spec.scheduled.job.dao;

import org.ikasan.spec.scheduled.job.model.QuartzScheduleDrivenJobRecord;
import org.ikasan.spec.search.SearchResults;

import java.util.List;

public interface QuartzScheduleDrivenJobDao<T extends QuartzScheduleDrivenJobRecord> {

    /**
     * Retrieves a list of search results with a specified limit and offset.
     *
     * @param limit  The maximum number of results to retrieve.
     * @param offset The number of results to skip before retrieving the list.
     * @return A SearchResults object containing a list of search results.
     */
    SearchResults<? extends T> findAll(int limit, int offset);

    /**
     * Retrieves a list of search results with a specified limit and offset based on the given context ID.
     *
     * @param contextId The ID of the context that the search results will be based on.
     * @param limit The maximum number of search results to retrieve.
     * @param offset The number of search results to skip before beginning to retrieve results.
     * @return A {@link SearchResults} object containing the search results.
     */
    SearchResults<? extends T> findByContext(String contextId, int limit, int offset);

    /**
     * Retrieves an object of type T by its ID.
     *
     * @param id The unique identifier of the object.
     * @return The object of type T with the specified ID.
     */
    T findById(String id);

    /**
     * Saves a record of type T.
     *
     * @param record The record to be saved.
     */
    void save(T record);
}
