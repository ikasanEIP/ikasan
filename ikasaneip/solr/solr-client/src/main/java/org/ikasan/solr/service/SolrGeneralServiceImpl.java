package org.ikasan.solr.service;

import org.ikasan.solr.dao.SolrGeneralDao;
import org.ikasan.solr.model.IkasanSolrDocument;
import org.ikasan.solr.model.IkasanSolrDocumentSearchResults;
import org.ikasan.spec.housekeeping.HousekeepService;
import org.ikasan.spec.solr.SolrDeleteService;
import org.ikasan.spec.solr.SolrGeneralService;
import org.ikasan.spec.solr.SolrServiceBase;

import java.util.List;
import java.util.Set;

/**
 * Created by Ikasan Development Team on 26/08/2017.
 */
public class SolrGeneralServiceImpl extends SolrServiceBase implements SolrGeneralService<IkasanSolrDocument, IkasanSolrDocumentSearchResults>, HousekeepService, SolrDeleteService
{
    private SolrGeneralDao<IkasanSolrDocumentSearchResults> solrGeneralDao;

    /**
     * Constructor
     *
     * @param solrGeneralSearchDao
     */
    public SolrGeneralServiceImpl(SolrGeneralDao<IkasanSolrDocumentSearchResults> solrGeneralSearchDao)
    {
        this.solrGeneralDao = solrGeneralSearchDao;
        if(this.solrGeneralDao == null)
        {
            throw new IllegalArgumentException("solrGeneralSearchDao cannot be null!");
        }
    }


    @Override
    public IkasanSolrDocumentSearchResults search(Set<String> moduleName, Set<String> flowNames,
                                                  String searchString, long startTime, long endTime, int resultSize)
    {
        this.solrGeneralDao.setSolrUsername(this.solrUsername);
        this.solrGeneralDao.setSolrPassword(this.solrPassword);
        return this.solrGeneralDao.search(moduleName, flowNames, searchString, startTime, endTime, resultSize);
    }

    @Override
    public IkasanSolrDocumentSearchResults search(Set<String> moduleNames, Set<String> flowNames, String searchString, long startTime
            , long endTime, int resultSize, List<String> entityTypes)
    {
        this.solrGeneralDao.setSolrUsername(this.solrUsername);
        this.solrGeneralDao.setSolrPassword(this.solrPassword);
        return this.solrGeneralDao.search(moduleNames, flowNames, searchString, startTime, endTime, resultSize, entityTypes);
    }

    @Override
    public IkasanSolrDocumentSearchResults search(String searchString, long startTime, long endTime, int resultSize, List<String> entityTypes)
    {
        this.solrGeneralDao.setSolrUsername(this.solrUsername);
        this.solrGeneralDao.setSolrPassword(this.solrPassword);
        return this.solrGeneralDao.search(searchString, startTime, endTime, resultSize, entityTypes);
    }

    @Override
    public IkasanSolrDocumentSearchResults search(String searchString, long startTime, long endTime, int offset, int resultSize, List<String> entityTypes)
    {
        this.solrGeneralDao.setSolrUsername(this.solrUsername);
        this.solrGeneralDao.setSolrPassword(this.solrPassword);
        return this.solrGeneralDao.search(searchString, startTime, endTime, offset, resultSize, entityTypes);
    }

    @Override
    public IkasanSolrDocumentSearchResults search(Set<String> moduleNames, String searchString, long startTime, long endTime, int offset, int resultSize, List<String> entityTypes) {
        this.solrGeneralDao.setSolrUsername(this.solrUsername);
        this.solrGeneralDao.setSolrPassword(this.solrPassword);
        return this.solrGeneralDao.search(moduleNames, null, null, null, searchString, startTime, endTime, offset, resultSize, entityTypes);
    }

    @Override
    public IkasanSolrDocumentSearchResults search(Set<String> moduleNames, Set<String> flowNames, Set<String> componentNames, String eventId, String searchString, long startTime, long endTime, int offset, int resultSize, List<String> entityTypes)
    {
        this.solrGeneralDao.setSolrUsername(this.solrUsername);
        this.solrGeneralDao.setSolrPassword(this.solrPassword);
        return this.solrGeneralDao.search(moduleNames, flowNames, componentNames, eventId, searchString, startTime, endTime, offset, resultSize, entityTypes);
    }

    @Override
    public void saveOrUpdate(IkasanSolrDocument document)
    {
        this.solrGeneralDao.setSolrUsername(this.solrUsername);
        this.solrGeneralDao.setSolrPassword(this.solrPassword);
        this.solrGeneralDao.saveOrUpdate(document);
    }

    @Override
    public void saveOrUpdate(List<IkasanSolrDocument> documents)
    {
        this.solrGeneralDao.setSolrUsername(this.solrUsername);
        this.solrGeneralDao.setSolrPassword(this.solrPassword);
        this.solrGeneralDao.saveOrUpdate(documents);
    }

    @Override
    public void housekeep()
    {
        this.solrGeneralDao.setSolrUsername(this.solrUsername);
        this.solrGeneralDao.setSolrPassword(this.solrPassword);
        this.solrGeneralDao.removeExpired();
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

    @Override
    public void removeById(String type, String id)
    {
        this.solrGeneralDao.setSolrUsername(this.solrUsername);
        this.solrGeneralDao.setSolrPassword(this.solrPassword);
        this.solrGeneralDao.removeById(type, id);
    }


}
