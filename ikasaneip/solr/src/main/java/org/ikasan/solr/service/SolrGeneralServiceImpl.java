package org.ikasan.solr.service;

import org.ikasan.solr.dao.SolrGeneralSearchDao;
import org.ikasan.solr.model.IkasanSolrDocumentSearchResults;
import org.ikasan.spec.solr.SolrSearchService;

import java.util.List;
import java.util.Set;

/**
 * Created by Ikasan Development Team on 26/08/2017.
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
    public IkasanSolrDocumentSearchResults search(Set<String> moduleName, Set<String> flowNames,
                                                  String searchString, long startTime, long endTime, int resultSize)
    {
        return this.solrGeneralSearchDao.search(moduleName, flowNames, searchString, startTime, endTime, resultSize);
    }

    @Override
    public IkasanSolrDocumentSearchResults search(Set<String> moduleNames, Set<String> flowNames, String searchString, long startTime
            , long endTime, int resultSize, List<String> entityTypes)
    {
        return this.solrGeneralSearchDao.search(moduleNames, flowNames, searchString, startTime, endTime, resultSize, entityTypes);
    }
}
