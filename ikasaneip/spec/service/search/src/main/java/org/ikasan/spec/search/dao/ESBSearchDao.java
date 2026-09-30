package org.ikasan.spec.search.dao;

import java.util.List;
import java.util.Set;

/**
 * An interface defining search and retrieval operations for interacting with an ESB persistence layer.
 * Provides methods to execute various types of search queries and fetch documents based on their identifiers.
 *
 * @param <RESULTS>   The type representing the result set returned by search queries.
 * @param <DOCUMENT>  The type representing individual documents stored and retrieved by the DAO.
 */
public interface ESBSearchDao<RESULTS, DOCUMENT>
{
    /**
     * Searches for results based on the provided identifiers and sorting parameters.
     *
     * @param identifiers a set of unique strings representing the identifiers to filter the search.
     * @param offset the starting point or index of the results to be fetched.
     * @param resultSize the maximum number of results to return.
     * @param sortField the field by which the results should be sorted.
     * @param sortOrder the order to apply for sorting, typically "asc" for ascending or "desc" for descending.
     * @return a RESULTS object containing the search results based on the input parameters.
     */
    RESULTS search(Set<String> identifiers, int offset, int resultSize, String sortField, String sortOrder);

    /**
     * Performs a search operation based on the specified parameters.
     *
     * @param moduleName a set of module names to filter the search, may be empty to include all modules
     * @param flowNames a set of flow names to filter the search, may be empty to include all flows
     * @param searchString the search query string to match against
     * @param startTime the start timestamp (inclusive) to filter the search results
     * @param endTime the end timestamp (exclusive) to filter the search results
     * @param resultSize the maximum number of results to return
     * @param negateQuery if true, negates the search query
     * @param sortField the field by which the results will be sorted
     * @param sortOrder the sort order direction, e.g., "asc" for ascending, "desc" for descending
     * @return a RESULTS object containing the search results
     */
    RESULTS search(Set<String> moduleName, Set<String> flowNames, String searchString, long startTime
        , long endTime, int resultSize, boolean negateQuery, String sortField, String sortOrder);

    /**
     * Searches for results based on the given parameters.
     *
     * @param moduleName  a set of module names to filter the search.
     * @param flowNames   a set of flow names to narrow down the search scope.
     * @param searchString a string representing the search query.
     * @param startTime   the start timestamp for filtering results based on time range.
     * @param endTime     the end timestamp for filtering results based on time range.
     * @param resultSize  the maximum number of results to return.
     * @param entityTypes a list of entity types to filter the search.
     * @param negateQuery a flag to indicate if the search query should be negated.
     * @param sortField   the field by which the results should be sorted.
     * @param sortOrder   the order (ascending or descending) for sorting the results.
     * @return a RESULTS object containing the matching search results based on the provided parameters.
     */
    RESULTS search(Set<String> moduleName, Set<String> flowNames, String searchString, long startTime
        , long endTime, int resultSize, List<String> entityTypes, boolean negateQuery, String sortField, String sortOrder);

    /**
     * Executes a search operation based on the specified criteria and options.
     *
     * @param moduleName       a set of module names to filter the search results.
     * @param flowNames        a set of flow names to filter the search results.
     * @param componentNames   a set of component names to filter the search results.
     * @param eventId          the event ID to use as a filter for the search.
     * @param searchString     the query string to search for within the specified criteria.
     * @param startTime        the start time (inclusive) of the time range for filtering results, in milliseconds since epoch.
     * @param endTime          the end time (inclusive) of the time range for filtering results, in milliseconds since epoch.
     * @param offset           the starting index for paginated search results.
     * @param resultSize       the maximum number of results to return in the search.
     * @param entityTypes      a list of entity types to filter the search results.
     * @param negateQuery      a flag indicating whether to negate the search query criteria.
     * @param sortField        the field by which to sort the search results.
     * @param sortOrder        the order in which to sort the search results (e.g., ascending or descending).
     * @return a RESULTS object containing the search results that match the specified criteria and options.
     */
    RESULTS search(Set<String> moduleName, Set<String> flowNames, Set<String> componentNames, String eventId
        , String searchString, long startTime, long endTime, int offset, int resultSize, List<String> entityTypes, boolean negateQuery
        , String sortField, String sortOrder);


    /**
     * Executes a search query based on the specified parameters and returns the search results.
     *
     * @param searchString  The query string to be searched.
     * @param startTime     The start timestamp (inclusive) for filtering results.
     * @param endTime       The end timestamp (inclusive) for filtering results.
     * @param resultSize    The maximum number of results to return.
     * @param entityTypes   A list of entity types to filter the results by.
     * @param negateQuery   A flag to indicate whether the query should be negated.
     * @param sortField     The field by which the results should be sorted.
     * @param sortOrder     The sorting order, either "asc" for ascending or "desc" for descending.
     * @return              The search results matching the query and filters.
     */
    RESULTS search(String searchString, long startTime, long endTime, int resultSize, List<String> entityTypes, boolean negateQuery
        , String sortField, String sortOrder);

    /**
     * Searches for results based on the specified criteria.
     *
     * @param searchString   The query string used for the search.
     * @param startTime      The starting timestamp (inclusive) for filtering the results.
     * @param endTime        The ending timestamp (inclusive) for filtering the results.
     * @param offset         The starting index for paginated results.
     * @param resultSize     The maximum number of results to return.
     * @param entityTypes    A list of entity types to narrow down the search results. If empty, no filtering is applied based on entity types.
     * @param negateQuery    A flag indicating whether to invert the search query logic.
     * @param sortField      The field by which the results should be sorted.
     * @param sortOrder      The order of sorting, either ascending or descending.
     * @return A {@code RESULTS} object containing the matching results based on the search criteria.
     */
    RESULTS search(String searchString, long startTime, long endTime, int offset, int resultSize, List<String> entityTypes, boolean negateQuery
        , String sortField, String sortOrder);

    /**
     * Searches for results based on the specified harvest received time and various search parameters.
     *
     * @param moduleName               A set of module names to filter the search results.
     * @param flowNames                A set of flow names to filter the search results.
     * @param componentNames           A set of component names to filter the search results.
     * @param eventId                  The ID of the event to filter the search results.
     * @param searchString             The query string used for the search.
     * @param harvestReceivedStartTime The starting timestamp (inclusive) for filtering based on the harvest received time.
     * @param harvestReceivedEndTime   The ending timestamp (inclusive) for filtering based on the harvest received time.
     * @param offset                   The starting index for paginated results.
     * @param resultSize               The maximum number of results to return.
     * @param entityTypes              A list of entity types to narrow down the search results. If empty, no filtering is applied based on entity types.
     * @param negateQuery              A flag indicating whether to invert the search query logic.
     * @param sortField                The field by which the results should be sorted.
     * @param sortOrder                The order of sorting, either ascending or descending.
     * @return A RESULTS object containing the matching results based on the provided harvest received time and search criteria.
     */
    RESULTS searchByHarvestReceivedTime(Set<String> moduleName, Set<String> flowNames, Set<String> componentNames, String eventId
        , String searchString, long harvestReceivedStartTime, long harvestReceivedEndTime, int offset, int resultSize, List<String> entityTypes
        , boolean negateQuery, String sortField, String sortOrder);

    /**
     * Retrieves a document by its type and unique identifier.
     *
     * @param type The type of the document to be retrieved.
     * @param id   The unique identifier of the document to be retrieved.
     * @return A DOCUMENT object representing the retrieved document, or null if no document is found.
     */
    DOCUMENT findById(String type, String id);

    /**
     * Retrieves a document associated with the specified error URI.
     *
     * @param type The type of the document to be retrieved.
     * @param uri  The error URI used to identify the document.
     * @return A DOCUMENT object representing the retrieved document, or null if no document is found.
     */
    DOCUMENT findByErrorUri(String type, String uri);

}
