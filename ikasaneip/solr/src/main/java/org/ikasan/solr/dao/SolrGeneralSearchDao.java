package org.ikasan.solr.dao;

import java.util.List;
import java.util.Set;

/**
 * Created by Ikasan Development Team on 04/08/2017.
 */
public interface SolrGeneralSearchDao<RESULTS>
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
     * @return
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
     * @return
     */
    public RESULTS search(Set<String> moduleName, Set<String> flowNames, String searchString, long startTime, long endTime, int resultSize, List<String> entityTypes);
}
