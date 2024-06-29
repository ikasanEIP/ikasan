package org.ikasan.spec.scheduled.job.dao;

import org.ikasan.spec.scheduled.job.model.SchedulerJobRecord;
import org.ikasan.spec.scheduled.job.model.SchedulerJobSearchFilter;
import org.ikasan.spec.search.SearchResults;

public interface SchedulerJobDao<T extends SchedulerJobRecord> {

    /**
     * Retrieves all items from the database that match the specified limit and offset.
     *
     * @param limit  The maximum number of items to retrieve.
     * @param offset The number of items to skip before starting to retrieve.
     * @param <T>    The type of items to retrieve.
     * @return A {@link SearchResults} object containing the matching items, total number of results, and query response time.
     */
    SearchResults<? extends T> findAll(int limit, int offset);

    /**
     * Finds items from the {@link SearchResults} that match the specified contextId, limit, and offset.
     *
     * @param contextId The ID of the context to search for.
     * @param limit     The maximum number of items to retrieve.
     * @param offset    The number of items to skip before starting to retrieve.
     * @param <T>       The type of the SchedulerJobRecord.
     * @return A {@link SearchResults} object containing the matching items.
     */
    SearchResults<? extends T> findByContext(String contextId, int limit, int offset);

    /**
     * Finds items from the database that match the specified agent, limit, and offset.
     *
     * @param agent  The name of the agent to search for.
     * @param limit  The maximum number of items to retrieve.
     * @param offset The number of items to skip before starting to retrieve.
     * @param <T>    The type of items to retrieve.
     * @return A {@link SearchResults} object containing the matching items, total number of results, and query response time.
     */
    SearchResults<? extends T> findByAgent(String agent, int limit, int offset);

    /**
     * Finds items from the database that match the specified filter, limit, offset, sort column, and sort direction.
     *
     * @param filter         The {@link SchedulerJobSearchFilter} object containing the search criteria.
     * @param limit          The maximum number of items to retrieve.
     * @param offset         The number of items to skip before starting to retrieve.
     * @param sortColumn     The column to sort the results by.
     * @param sortDirection  The direction of the sorting (either "ASC" or "DESC").
     * @param <T>            The type of items to retrieve.
     * @return A {@link SearchResults} object containing a list of matching items, the total number of results, and the query response time.
     */
    SearchResults<? extends T> findByFilter(SchedulerJobSearchFilter filter, int limit, int offset, String sortColumn, String sortDirection);

    /**
     * Retrieves an item by its ID.
     *
     * @param id The ID of the item to retrieve.
     * @param <T> The type of item to retrieve.
     * @return The item with the specified ID, or null if no item is found.
     */
    T findById(String id);

    /**
     * Retrieves an item from the database that matches the specified context ID and job name.
     *
     * @param contextId The ID of the context to search for.
     * @param jobName   The name of the job to search for.
     * @param <T>       The type of item to retrieve.
     * @return The item that matches the context ID and job name, or null if no item is found.
     */
    T findByContextIdAndJobName(String contextId, String jobName);

    /**
     * Deletes the specified record from the database.
     *
     * @param record The record to be deleted.
     * @param <T>    The type of record to be deleted.
     */
    void delete(T record);

    /**
     * Deletes all records from the database that match the specified context name.
     *
     * @param contextName The name of the context to delete records for.
     */
    void deleteByContextName(String contextName);

    /**
     * Deletes records from the database that match the specified agent name.
     *
     * @param agentName The name of the agent to delete records for.
     */
    void deleteByAgentName(String agentName);

    /**
     * Saves the given record to the database.
     *
     * @param record The record to be saved.
     * @param <T>    The type of record to be saved.
     */
    void save(T record);
}
