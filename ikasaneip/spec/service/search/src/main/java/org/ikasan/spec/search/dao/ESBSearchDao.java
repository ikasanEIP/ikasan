package org.ikasan.spec.search.dao;

import java.util.List;
import java.util.Set;

/**
 * Created by Ikasan Development Team on 04/08/2017.
 */
public interface ESBSearchDao<RESULTS, DOCUMENT>
{
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
