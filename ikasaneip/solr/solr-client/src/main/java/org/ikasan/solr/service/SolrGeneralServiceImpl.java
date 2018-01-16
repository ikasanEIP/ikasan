package org.ikasan.solr.service;

import org.ikasan.solr.dao.SolrGeneralDao;
import org.ikasan.solr.model.IkasanSolrDocumentSearchResults;
import org.ikasan.spec.housekeeping.HousekeepService;
import org.ikasan.spec.solr.SolrSearchService;
import org.ikasan.spec.solr.SolrServiceBase;

import java.util.List;
import java.util.Set;

/**
 * Created by Ikasan Development Team on 26/08/2017.
 */
public class SolrGeneralServiceImpl extends SolrServiceBase implements SolrSearchService<IkasanSolrDocumentSearchResults>, HousekeepService
{
    private SolrGeneralDao<IkasanSolrDocumentSearchResults> solrGeneralSearchDao;

    /**
     * Constructor
     *
     * @param solrGeneralSearchDao
     */
    public SolrGeneralServiceImpl(SolrGeneralDao<IkasanSolrDocumentSearchResults> solrGeneralSearchDao)
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
        this.solrGeneralSearchDao.setSolrUsername(this.solrUsername);
        this.solrGeneralSearchDao.setSolrPassword(this.solrPassword);
        return this.solrGeneralSearchDao.search(moduleName, flowNames, searchString, startTime, endTime, resultSize);
    }

    @Override
    public IkasanSolrDocumentSearchResults search(Set<String> moduleNames, Set<String> flowNames, String searchString, long startTime
            , long endTime, int resultSize, List<String> entityTypes)
    {
        this.solrGeneralSearchDao.setSolrUsername(this.solrUsername);
        this.solrGeneralSearchDao.setSolrPassword(this.solrPassword);
        return this.solrGeneralSearchDao.search(moduleNames, flowNames, searchString, startTime, endTime, resultSize, entityTypes);
    }

    @Override
    public void housekeep()
    {
        this.solrGeneralSearchDao.setSolrUsername(this.solrUsername);
        this.solrGeneralSearchDao.setSolrPassword(this.solrPassword);
        this.solrGeneralSearchDao.removeExpired();
    }

    @Override
    public boolean housekeepablesExist()
    {
        return true;
    }

    @Override
    public void setHousekeepingBatchSize(Integer housekeepingBatchSize)
    {
        // not relevant for solr housekeeping
    }

    @Override
    public void setTransactionBatchSize(Integer transactionBatchSize)
    {
        // not relevant for solr housekeeping
    }
}
