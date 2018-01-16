package org.ikasan.solr.dao;

import java.util.List;
import java.util.Set;

/**
 * Created by Ikasan Development Team on 04/08/2017.
 */
public interface SolrGeneralDao<RESULTS>
{
    /**
     * Perform general search against ikasan solr index.
     *
     * @param moduleName
     * @param flowNames
     * @param searchString
     * @param startTime
     * @param endTime
     * @param resultSize
     * @return RESULTS
     */
    public RESULTS search(Set<String> moduleName, Set<String> flowNames, String searchString, long startTime, long endTime, int resultSize);


    /**
     * Perform general search against ikasan solr index.
     *
     * @param moduleName
     * @param flowNames
     * @param searchString
     * @param startTime
     * @param endTime
     * @param resultSize
     * @param entityTypes
     * @return RESULTS
     */
    public RESULTS search(Set<String> moduleName, Set<String> flowNames, String searchString, long startTime, long endTime, int resultSize, List<String> entityTypes);

    /**
     * Set the solr username
     *
     * @param solrUsername
     */
    public void setSolrUsername(String solrUsername);


    /**
     * Set the solr password
     *
     * @param solrPassword
     */
    public void setSolrPassword(String solrPassword);

    /**
     * Method to remove expired records from the solr index.
     */
    public void removeExpired();
}
