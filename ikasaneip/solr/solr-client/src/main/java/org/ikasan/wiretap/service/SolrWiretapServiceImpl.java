package org.ikasan.wiretap.service;

import org.ikasan.spec.flow.FlowEvent;
import org.ikasan.spec.housekeeping.HousekeepService;
import org.ikasan.spec.module.ModuleService;
import org.ikasan.spec.persistence.BatchInsert;
import org.ikasan.spec.search.PagedSearchResult;
import org.ikasan.spec.solr.SolrService;
import org.ikasan.spec.solr.SolrServiceBase;
import org.ikasan.spec.wiretap.WiretapEvent;
import org.ikasan.spec.wiretap.WiretapService;
import org.ikasan.wiretap.dao.SolrWiretapDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * Created by amajewski on 23/09/2017.
 */
public class SolrWiretapServiceImpl extends SolrServiceBase implements HousekeepService, SolrService<WiretapEvent>, BatchInsert<WiretapEvent>
{

    /** Logger for this class */
    private static Logger logger = LoggerFactory.getLogger(SolrWiretapServiceImpl.class);

    /**
     * Data access object for the persistence of <code>WiretapFlowEvent</code>
     */
    private SolrWiretapDao wiretapDao;

    /**
     * Container for modules
     */
    private ModuleService moduleService;

    /**
     * Constructor
     *
     * @param wiretapDao
     * @param moduleService
     */
    public SolrWiretapServiceImpl(SolrWiretapDao wiretapDao, ModuleService moduleService) {
        this.wiretapDao = wiretapDao;
        if (wiretapDao == null) {
            throw new IllegalArgumentException("wiretapDao cannot be 'null'");
        }

        this.moduleService = moduleService;
        if(moduleService == null)
        {
            throw new IllegalArgumentException("moduleService cannot be 'null'");
        }
    }

    /**
     * Constructor
     *
     * @param wiretapDao
     */
    public SolrWiretapServiceImpl(SolrWiretapDao wiretapDao) {
        this.wiretapDao = wiretapDao;
        if (wiretapDao == null) {
            throw new IllegalArgumentException("wiretapDao cannot be 'null'");
        }

    }

    @Override
    public void save(WiretapEvent wiretapEvent)
    {
        wiretapDao.setSolrUsername(this.solrUsername);
        wiretapDao.setSolrPassword(this.solrPassword);
        wiretapDao.save(wiretapEvent);
    }

    @Override
    public void save(List<WiretapEvent> save)
    {
        wiretapDao.setSolrUsername(this.solrUsername);
        wiretapDao.setSolrPassword(this.solrPassword);
        wiretapDao.save(save);
    }

    @Override
    public boolean housekeepablesExist()
    {
        return true;
    }

    @Override
    public void setHousekeepingBatchSize(Integer housekeepingBatchSize) {

    }

    @Override
    public void setTransactionBatchSize(Integer transactionBatchSize) {

    }

    /**
     * (non-Javadoc)
     *
     * @see org.ikasan.spec.housekeeping.HousekeepService#housekeep()
     */

    @Override
    public void housekeep()
    {
        logger.info("persistence housekeep called");
        long startTime = System.currentTimeMillis();
        wiretapDao.setSolrUsername(this.solrUsername);
        wiretapDao.setSolrPassword(this.solrPassword);
        wiretapDao.removeExpired();
        long endTime = System.currentTimeMillis();
        logger.info("persistence housekeep completed in [" + (endTime - startTime) + " ms]");
    }

    @Override
    public void insert(List<WiretapEvent> entities)
    {
        wiretapDao.setSolrUsername(this.solrUsername);
        wiretapDao.setSolrPassword(this.solrPassword);
        this.save(entities);
    }
}
