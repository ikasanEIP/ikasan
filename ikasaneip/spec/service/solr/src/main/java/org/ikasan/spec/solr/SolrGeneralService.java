package org.ikasan.spec.solr;

import java.io.IOException;
import java.util.List;
import java.util.Set;

/**
 * Created by Ikasan Development on 27/08/2017.
 */
public interface SolrGeneralService<ENTITY, RESULTS>
{
    /**
     * Perform general search against ikasan solr index.
     *
     * @param moduleNames
     * @param flowNames
     * @param searchString
     * @param startTime
     * @param endTime
     * @param resultSize
     * @param negateQuery
     * @param sortField
     * @param sortOrder
     * @return
     */
    RESULTS search(Set<String> moduleNames, Set<String> flowNames, String searchString, long startTime
        , long endTime, int resultSize, boolean negateQuery, String sortField, String sortOrder) throws IOException;

    /**
     * Perform general search against ikasan solr index.
     *
     * @param moduleNames
     * @param flowNames
     * @param searchString
     * @param startTime
     * @param endTime
     * @param resultSize
     * @param entityTypes
     * @param negateQuery
     * @param sortField
     * @param sortOrder
     * @return
     */
    RESULTS search(Set<String> moduleNames, Set<String> flowNames, String searchString, long startTime
        , long endTime, int resultSize, List<String> entityTypes, boolean negateQuery, String sortField, String sortOrder);

    /**
     * Perform general search against ikasan solr index.
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
    RESULTS search(String searchString, long startTime, long endTime, int resultSize, List<String> entityTypes, boolean negateQuery, String sortField, String sortOrder);

    /**
     * Perform general search against ikasan solr index.
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
    RESULTS search(String searchString, long startTime, long endTime, int offset, int resultSize, List<String> entityTypes, boolean negateQuery, String sortField, String sortOrder);

    /**
     * Perform general search against ikasan solr index.
     *
     * @param moduleNames
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
    RESULTS search(Set<String> moduleNames, String searchString, long startTime, long endTime, int offset, int resultSize
        , List<String> entityTypes, boolean negateQuery, String sortField, String sortOrder);

    /**
     * Perform general search against ikasan solr index.
     *
     * @param moduleNames
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
    RESULTS search(Set<String> moduleNames, Set<String> flowNames, Set<String> componentNames, String eventId
        , String searchString, long startTime, long endTime, int offset, int resultSize, List<String> entityTypes, boolean negateQuery, String sortField, String sortOrder);

    /**
     * Method to find a document in the solr index by type and id.
     *
     * @param type
     * @param id
     */
    ENTITY findById(String type, String id);

    /**
     * Method to find a document in the solr index by type and error uri.
     *
     * @param type
     * @param uri
     */
    ENTITY findByErrorUri(String type, String uri);

    /**
     * Save or update an ENTITY
     *
     * @param entity
     */
    void saveOrUpdate(ENTITY entity);

    /**
     * Save or update a list of ENTITY
     *
     * @param entity
     */
    void saveOrUpdate(List<ENTITY> entity);

    /**
     * Backs up the Solr index to a specified location with a specified number of backups to keep.
     *
     * @param backupLocationPath The path where the backup of the index should be stored
     * @param numberOfBackupsToKeep The number of backup copies of the index to keep
     */
    void backupIndex(String backupLocationPath, int numberOfBackupsToKeep);

    /**
     * Set the solr username
     *
     * @param solrUsername
     */
    void setSolrUsername(String solrUsername);


    /**
     * Set the solr password
     *
     * @param solrPassword
     */
    void setSolrPassword(String solrPassword);
}
