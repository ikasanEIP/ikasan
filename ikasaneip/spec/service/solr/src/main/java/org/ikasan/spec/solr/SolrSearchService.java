package org.ikasan.spec.solr;

import java.util.List;
import java.util.Set;

/**
 * Created by Ikasan Development on 27/08/2017.
 */
public interface SolrSearchService<RESULTS>
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
     * @return
     */
    public RESULTS search(Set<String> moduleNames, Set<String> flowNames, String searchString, long startTime, long endTime, int resultSize);

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
     * @return
     */
    public RESULTS search(Set<String> moduleNames, Set<String> flowNames, String searchString, long startTime, long endTime, int resultSize, List<String> entityTypes);

    /**
     * Perform general search against ikasan solr index.
     *
     * @param searchString
     * @param startTime
     * @param endTime
     * @param resultSize
     * @param entityTypes
     * @return RESULTS
     */
    public RESULTS search(String searchString, long startTime, long endTime, int resultSize, List<String> entityTypes);

    /**
     * Perform general search against ikasan solr index.
     *
     * @param searchString
     * @param startTime
     * @param endTime
     * @param offset
     * @param resultSize
     * @param entityTypes
     * @return RESULTS
     */
    public RESULTS search(String searchString, long startTime, long endTime, int offset, int resultSize, List<String> entityTypes);

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
     * @return
     */
    public RESULTS search(Set<String> moduleNames, Set<String> flowNames, Set<String> componentNames, String eventId, String searchString, long startTime, long endTime, int offset, int resultSize, List<String> entityTypes);

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
}
