package org.ikasan.solr.dao;

import org.ikasan.solr.model.IkasanSolrDocument;

import java.util.List;

/**
 * Created by Ikasan Development Team on 04/08/2017.
 */
public interface SolrGeneralSearchDao<RESULTS>
{
    /**
     * Perform general search against ikasan solr index.
     *
     * @param searchString
     * @param resultSize
     * @return
     */
    public RESULTS search(String searchString, long startTime, long endTime, int resultSize);
}
