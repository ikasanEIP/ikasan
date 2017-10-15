package org.ikasan.error.reporting.service;

import org.ikasan.error.reporting.model.ErrorOccurrence;
import org.ikasan.spec.error.reporting.ErrorReportingServiceDao;
import org.ikasan.spec.solr.SolrService;

/**
 * Created by Ikasan Development Team on 23/09/2017.
 */
public class SolrErrorReportingManagmentServiceImpl implements SolrService<ErrorOccurrence<byte[]>> {

    private ErrorReportingServiceDao errorReportingServiceDao;
    /**
     * Constructor
     *
     * @param errorReportingServiceDao
     */
    public SolrErrorReportingManagmentServiceImpl(ErrorReportingServiceDao errorReportingServiceDao) {
        super();
        this.errorReportingServiceDao = errorReportingServiceDao;
        if (this.errorReportingServiceDao == null) {
            throw new IllegalArgumentException("errorManagementDao cannot be null!");
        }
    }
    @Override
    public void save(ErrorOccurrence<byte[]> entity)
    {
        this.errorReportingServiceDao.save(entity);
    }

}
