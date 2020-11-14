package org.ikasan.error.reporting.service;

import org.ikasan.error.reporting.dao.SolrErrorReportingServiceDao;
import org.ikasan.error.reporting.model.SolrErrorOccurrence;
import org.ikasan.spec.error.reporting.ErrorOccurrence;
import org.ikasan.spec.error.reporting.ErrorReportingManagementService;
import org.ikasan.spec.error.reporting.ErrorReportingService;
import org.ikasan.spec.error.reporting.ErrorReportingServiceDao;
import org.ikasan.spec.persistence.BatchInsert;
import org.ikasan.spec.search.PagedSearchResult;
import org.ikasan.spec.solr.SolrService;
import org.ikasan.spec.solr.SolrServiceBase;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by Ikasan Development Team on 23/09/2017.
 */
public class SolrErrorReportingServiceImpl extends SolrServiceBase implements SolrService<ErrorOccurrence>, BatchInsert<ErrorOccurrence>
{

    private SolrErrorReportingServiceDao errorReportingServiceDao;

    /**
     * Constructor
     *
     * @param errorReportingServiceDao
     */
    public SolrErrorReportingServiceImpl(SolrErrorReportingServiceDao errorReportingServiceDao) {
        this.errorReportingServiceDao = errorReportingServiceDao;
        if (this.errorReportingServiceDao == null) {
            throw new IllegalArgumentException("errorManagementDao cannot be null!");
        }
    }
    @Override
    public void save(ErrorOccurrence entity)
    {
        this.errorReportingServiceDao.setSolrUsername(this.solrUsername);
        this.errorReportingServiceDao.setSolrPassword(this.solrPassword);
        this.errorReportingServiceDao.save(entity);
    }

    @Override
    public void save(List<ErrorOccurrence> entities)
    {
        this.errorReportingServiceDao.setSolrUsername(this.solrUsername);
        this.errorReportingServiceDao.setSolrPassword(this.solrPassword);
        this.errorReportingServiceDao.save(entities);
    }

    @Override
    public void insert(List<ErrorOccurrence> entities)
    {
        this.save(entities);
    }
}
