package org.ikasan.solr.service;

import org.ikasan.solr.dao.SolrGeneralSearchDao;
import org.ikasan.solr.model.IkasanSolrDocument;
import org.ikasan.solr.model.IkasanSolrDocumentSearchResults;
import org.ikasan.spec.solr.SolrSearchService;
import org.ikasan.spec.solr.SolrService;

import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * Created by stewmi on 26/08/2017.
 */
public class SolrGeneralServiceImpl implements SolrSearchService<IkasanSolrDocumentSearchResults>
{
    private SolrGeneralSearchDao<IkasanSolrDocumentSearchResults> solrGeneralSearchDao;

    /**
     * Constructor
     *
     * @param solrGeneralSearchDao
     */
    public SolrGeneralServiceImpl(SolrGeneralSearchDao<IkasanSolrDocumentSearchResults> solrGeneralSearchDao)
    {
        this.solrGeneralSearchDao = solrGeneralSearchDao;
        if(this.solrGeneralSearchDao == null)
        {
            throw new IllegalArgumentException("solrGeneralSearchDao cannot be null!");
        }
    }


    @Override
    public IkasanSolrDocumentSearchResults search(String searchString, String queryFilter, long startTime, long endTime, int resultSize)
    {
        return this.solrGeneralSearchDao.search(searchString, queryFilter, startTime, endTime, resultSize);
    }

    @Override
    public String buildQuery(Set<String> moduleNames, Set<String> flowNames, Set<String> componentNames, Date fromDate, Date untilDate, String payloadContent, String eventId, String type)
    {
        return this.solrGeneralSearchDao.buildQuery(moduleNames, flowNames, componentNames, fromDate, untilDate, payloadContent, eventId, type);
    }
}
