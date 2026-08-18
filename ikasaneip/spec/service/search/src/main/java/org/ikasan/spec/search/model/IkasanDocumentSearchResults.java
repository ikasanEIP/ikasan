package org.ikasan.spec.search.model;

import java.util.List;

/**
 * Interface for search results containing Ikasan documents.
 * This interface provides access to the list of results, total count, and query performance metrics.
 */
public interface IkasanDocumentSearchResults {

    /**
     * Get the list of documents in this result set.
     *
     * @return the list of documents
     */
    List<IkasanESBDocument> getResultList();

    /**
     * Get the total number of results that match the search criteria.
     * This may be larger than the size of the result list if pagination is used.
     *
     * @return the total number of results
     */
    long getTotalNumberOfResults();

    /**
     * Get the time taken to execute the query.
     *
     * @return the query response time in milliseconds
     */
    long getQueryResponseTime();
}
