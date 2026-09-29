package org.ikasan.spec.search.dao;

import java.util.List;
import java.util.Set;

/**
 * Created by Ikasan Development Team on 04/08/2017.
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
     * Perform general search against ikasan persistence.
     *
     * @param moduleName
     * @param flowNames
     * @param searchString
     * @param startTime
     * @param endTime
     * @param resultSize
     * @param negateQuery
     * @param sortField
     * @param sortOrder
     * @return RESULTS
     */
    RESULTS search(Set<String> moduleName, Set<String> flowNames, String searchString, long startTime
        , long endTime, int resultSize, boolean negateQuery, String sortField, String sortOrder);


    /**
     * Perform general search against ikasan persistence.
     *
     * @param moduleName
     * @param flowNames
     * @param searchString
     * @param startTime
     * @param endTime
     * @param resultSize
     * @param entityTypes
     * @param negateQuery
     * @param sortField
     * @param sortOrder
     * @return RESULTS
     */
    RESULTS search(Set<String> moduleName, Set<String> flowNames, String searchString, long startTime
        , long endTime, int resultSize, List<String> entityTypes, boolean negateQuery, String sortField, String sortOrder);

    /**
     * Perform general search against ikasan persistence.
     *
     * @param moduleName
     * @param flowNames
     * @param componentNames
     * @param eventId
     * @param searchString
     * @param startTime
     * @param endTime
     * @param offset
     * @param resultSize
     * @param entityTypes
     * @param negateQuery
     * @param sortField
     * @param sortOrder
     * @return
     */
    RESULTS search(Set<String> moduleName, Set<String> flowNames, Set<String> componentNames, String eventId
        , String searchString, long startTime, long endTime, int offset, int resultSize, List<String> entityTypes, boolean negateQuery
        , String sortField, String sortOrder);



    /**
     * Perform general search against ikasan persistence.
     *
     * @param searchString
     * @param startTime
     * @param endTime
     * @param resultSize
     * @param entityTypes
     * @param negateQuery
     * @param sortField
     * @param sortOrder
     * @return RESULTS
     */
    RESULTS search(String searchString, long startTime, long endTime, int resultSize, List<String> entityTypes, boolean negateQuery
        , String sortField, String sortOrder);

    /**
     * Perform general search against ikasan persistence.
     *
     * @param searchString
     * @param startTime
     * @param endTime
     * @param offset
     * @param resultSize
     * @param entityTypes
     * @param negateQuery
     * @param sortField
     * @param sortOrder
     * @return RESULTS
     */
    RESULTS search(String searchString, long startTime, long endTime, int offset, int resultSize, List<String> entityTypes, boolean negateQuery
        , String sortField, String sortOrder);

    /**
     * Method to find a document in the persistence by type and id.
     *
     * @param type
     * @param id
     */
    DOCUMENT findById(String type, String id);

    /**
     * Method to find a document in the persistence by type and id.
     *
     * @param type
     * @param uri
     */
    DOCUMENT findByErrorUri(String type, String uri);

}
