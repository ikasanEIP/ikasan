package org.ikasan.spec.search.service;

import java.util.List;
import java.util.Set;

/**
 * Interface defining search and retrieval operations on the Ikasan Solr index.
 *
 * @param <ENTITY>  the type representing an individual entity in the search results
 * @param <RESULTS> the type representing the collection of search results
 */
public interface ESBSearchService<ENTITY, RESULTS>
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
     * Searches for results based on the specified parameters.
     *
     * @param moduleName   the set of module names to filter the search by
     * @param flowNames    the set of flow names to filter the search by
     * @param searchString the string to search for in the data
     * @param startTime    the lower bound of the time range (inclusive) for the search
     * @param endTime      the upper bound of the time range (exclusive) for the search
     * @param resultSize   the maximum number of results to return
     * @param negateQuery  whether to negate the search query, returning results that do not match
     * @param sortField    the field by which to sort the search results
     * @param sortOrder    the order of sorting, e.g., ascending or descending
     * @return the search results matching the specified criteria
     */
    RESULTS search(Set<String> moduleName, Set<String> flowNames, String searchString, long startTime
        , long endTime, int resultSize, boolean negateQuery, String sortField, String sortOrder);

    /**
     * Executes a search query based on provided criteria and returns the results.
     *
     * @param moduleName   A set of module names to scope the search query.
     * @param flowNames    A set of flow names to refine the search query.
     * @param searchString The query string to search for.
     * @param startTime    The start timestamp (inclusive) for filtering results based on time.
     * @param endTime      The end timestamp (inclusive) for filtering results based on time.
     * @param resultSize   The maximum number of results to retrieve.
     * @param entityTypes  A list of entity types to filter the search results.
     * @param negateQuery  A flag indicating whether to negate the search query.
     * @param sortField    The field by which to sort the search results.
     * @param sortOrder    The order of sorting, either ascending or descending.
     * @return A RESULTS object containing the results of the search query.
     */
    RESULTS search(Set<String> moduleName, Set<String> flowNames, String searchString, long startTime
        , long endTime, int resultSize, List<String> entityTypes, boolean negateQuery, String sortField, String sortOrder);

    /**
     * Searches for results based on the specified criteria.
     *
     * @param moduleName      the set of module names to include in the search
     * @param flowNames       the set of flow names to include in the search
     * @param componentNames  the set of component names to include in the search
     * @param eventId         the specific event ID to search for
     * @param searchString    the string to search for within the provided context
     * @param startTime       the start time for the search range, in milliseconds since epoch
     * @param endTime         the end time for the search range, in milliseconds since epoch
     * @param offset          the offset for the search result pagination
     * @param resultSize      the maximum number of results to return
     * @param entityTypes     the list of entity types to filter the search
     * @param negateQuery     whether to negate the search query criteria
     * @param sortField       the field by which to sort the results
     * @param sortOrder       the order in which to sort the results (e.g., ascending or descending)
     * @return the search results matching the specified criteria
     */
    RESULTS search(Set<String> moduleName, Set<String> flowNames, Set<String> componentNames, String eventId
        , String searchString, long startTime, long endTime, int offset, int resultSize, List<String> entityTypes, boolean negateQuery
        , String sortField, String sortOrder);

    /**
     * Searches for results based on the provided criteria.
     *
     * @param searchString the string used to perform the search.
     * @param startTime the start time for the search range, in milliseconds since epoch.
     * @param endTime the end time for the search range, in milliseconds since epoch.
     * @param resultSize the maximum number of results to return.
     * @param entityTypes a list of entity types to filter the search results.
     * @param negateQuery a flag indicating whether to negate the search query.
     * @param sortField the field by which the results should be sorted.
     * @param sortOrder the order in which to sort the results, either ascending or descending.
     * @return the search results matching the provided criteria.
     */
    RESULTS search(String searchString, long startTime, long endTime, int resultSize, List<String> entityTypes, boolean negateQuery
        , String sortField, String sortOrder);

    /**
     * Searches for results based on the specified criteria.
     *
     * @param searchString The keyword or phrase to search for.
     * @param startTime The start of the time range for the search, represented in milliseconds since epoch.
     * @param endTime The end of the time range for the search, represented in milliseconds since epoch.
     * @param offset The starting index of the search results, used for pagination.
     * @param resultSize The maximum number of results to return.
     * @param entityTypes A list of entity types to filter the search results.
     * @param negateQuery If true, the search will exclude results matching the search criteria; otherwise, it will include them.
     * @param sortField The field by which to sort the search results.
     * @param sortOrder The order in which to sort the results, either "asc" for ascending or "desc" for descending.
     * @return A RESULTS object containing the results of the search based on the given parameters.
     */
    RESULTS search(String searchString, long startTime, long endTime, int offset, int resultSize, List<String> entityTypes, boolean negateQuery
        , String sortField, String sortOrder);


    /**
     * Searches for results based on the given parameters.
     *
     * @param moduleNames the set of module names to filter the search against
     * @param searchString the string to search for
     * @param startTime the start time of the range to filter the search, in milliseconds since epoch
     * @param endTime the end time of the range to filter the search, in milliseconds since epoch
     * @param offset the starting index of the results to return
     * @param resultSize the maximum number of results to return
     * @param entityTypes the list of entity types to include in the search
     * @param negateQuery a flag indicating whether to negate the search criteria
     * @param sortField the field by which to sort the search results
     * @param sortOrder the direction in which to sort the results (e.g., ascending or descending)
     * @return a RESULTS object containing the search results based on the specified criteria
     */
    RESULTS search(Set<String> moduleNames, String searchString, long startTime, long endTime, int offset
        , int resultSize, List<String> entityTypes, boolean negateQuery, String sortField, String sortOrder);

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
     * Finds an entity by its type and ID.
     *
     * @param type the type of the entity to search for
     * @param id the unique identifier of the entity
     * @return the entity matching the specified type and ID, or null if no match is found
     */
    ENTITY findById(String type, String id);


    /**
     * Finds an entity by its error URI and type.
     *
     * @param type the type of the entity to search for
     * @param uri the error URI associated with the entity
     * @return the entity matching the specified type and error URI, or null if no match is found
     */
    ENTITY findByErrorUri(String type, String uri);

}
