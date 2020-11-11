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
public class SolrErrorReportingManagementServiceImpl extends SolrServiceBase implements SolrService<ErrorOccurrence>, BatchInsert<ErrorOccurrence>
{

    private SolrErrorReportingServiceDao errorReportingServiceDao;

    /**
     * Constructor
     *
     * @param errorReportingServiceDao
     */
    public SolrErrorReportingManagementServiceImpl(SolrErrorReportingServiceDao errorReportingServiceDao) {
        super();
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

//    @Override
//    public ErrorOccurrence find(String uri)
//    {
//        this.errorReportingServiceDao.setSolrUsername(this.solrUsername);
//        this.errorReportingServiceDao.setSolrPassword(this.solrPassword);
//        return this.errorReportingServiceDao.find(uri);
//    }
//
//    @Override
//    public Map<String, ErrorOccurrence> find(List<String> uris)
//    {
//        this.errorReportingServiceDao.setSolrUsername(this.solrUsername);
//        this.errorReportingServiceDao.setSolrPassword(this.solrPassword);
//        return this.errorReportingServiceDao.find(uris);
//    }
//
//    @Override
//    public List<ErrorOccurrence> find(List<String> moduleName, List<String> flowName, List<String> flowElementname, Date startDate, Date endDate, int size)
//    {
//        this.errorReportingServiceDao.setSolrUsername(this.solrUsername);
//        this.errorReportingServiceDao.setSolrPassword(this.solrPassword);
//        return this.find(moduleName, flowName, flowElementname, startDate, endDate, size);
//    }
//
//    @Override
//    public List<ErrorOccurrence> find(List<String> moduleName, List<String> flowName, List<String> flowElementname, Date startDate, Date endDate, String action, String exceptionClass, int size)
//    {
//        this.errorReportingServiceDao.setSolrUsername(this.solrUsername);
//        this.errorReportingServiceDao.setSolrPassword(this.solrPassword);
//        return this.find(moduleName, flowName, flowElementname, startDate, endDate, action, exceptionClass, size);
//    }
//
//    @Override public PagedSearchResult<ErrorOccurrence> find(int pageNo, int pageSize, String orderBy,
//        boolean orderAscending, String moduleName, String flowName, String componentName, Date fromDate, Date untilDate)
//    {
//        throw new UnsupportedOperationException();
//    }
//
//    @Override
//    public String notify(String flowElementName, ErrorOccurrence<byte[]> errorOccurrence, Throwable throwable)
//    {
//        throw new UnsupportedOperationException();
//    }
//
//    @Override
//    public String notify(String flowElementName, ErrorOccurrence<byte[]> errorOccurrence, Throwable throwable, String action)
//    {
//        throw new UnsupportedOperationException();
//    }
//
//    @Override
//    public String notify(String flowElementName, Throwable throwable)
//    {
//        throw new UnsupportedOperationException();
//    }
//
//    @Override
//    public String notify(String flowElementName, Throwable throwable, String action)
//    {
//        throw new UnsupportedOperationException();
//    }
//
//    @Override
//    public void setTimeToLive(Long timeToLive)
//    {
//        throw new UnsupportedOperationException();
//    }
//
//    @Override
//    public void housekeep()
//    {
//        throw new UnsupportedOperationException();
//    }
//
//    @Override
//    public Long rowCount(List<String> moduleName, List<String> flowName, List<String> flowElementname, Date startDate, Date endDate)
//    {
//        throw new UnsupportedOperationException();
//    }

    @Override
    public void insert(List<ErrorOccurrence> entities)
    {
        this.save(entities);
    }
}
