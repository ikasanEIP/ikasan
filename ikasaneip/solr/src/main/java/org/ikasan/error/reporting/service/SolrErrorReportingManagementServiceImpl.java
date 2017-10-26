package org.ikasan.error.reporting.service;

import org.ikasan.error.reporting.model.SolrErrorOccurrence;
import org.ikasan.spec.error.reporting.ErrorOccurrence;
import org.ikasan.spec.error.reporting.ErrorReportingServiceDao;
import org.ikasan.spec.solr.SolrService;

/**
 * Created by Ikasan Development Team on 23/09/2017.
 */
public class SolrErrorReportingManagementServiceImpl implements SolrService<ErrorOccurrence> {

    private ErrorReportingServiceDao errorReportingServiceDao;

    /**
     * Constructor
     *
     * @param errorReportingServiceDao
     */
    public SolrErrorReportingManagementServiceImpl(ErrorReportingServiceDao errorReportingServiceDao) {
        super();
        this.errorReportingServiceDao = errorReportingServiceDao;
        if (this.errorReportingServiceDao == null) {
            throw new IllegalArgumentException("errorManagementDao cannot be null!");
        }
    }
    @Override
    public void save(ErrorOccurrence entity)
    {
        this.errorReportingServiceDao.save(entity);
    }

}
