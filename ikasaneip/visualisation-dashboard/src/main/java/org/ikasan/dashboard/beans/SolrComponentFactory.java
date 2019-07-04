package org.ikasan.dashboard.beans;

import org.ikasan.error.reporting.dao.SolrErrorReportingServiceDao;
import org.ikasan.error.reporting.service.SolrErrorReportingManagementServiceImpl;
import org.ikasan.exclusion.dao.SolrExclusionEventDao;
import org.ikasan.exclusion.service.SolrExclusionServiceImpl;
import org.ikasan.spec.error.reporting.ErrorReportingService;
import org.ikasan.spec.exclusion.ExclusionManagementService;
import org.ikasan.spec.wiretap.WiretapService;
import org.ikasan.wiretap.dao.SolrWiretapDao;
import org.ikasan.wiretap.service.SolrWiretapServiceImpl;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
public class SolrComponentFactory
{
    @Value("${solr.url}")
    private String solrUrl;

    @Value("${solr.username}")
    private String solrUsername;

    @Value("${solr.password}")
    private String solrPassword;

    @Bean
    public WiretapService solrWiretapService()
    {
        SolrWiretapDao dao = new SolrWiretapDao();
        dao.initStandalone(solrUrl, 30);
        SolrWiretapServiceImpl service = new SolrWiretapServiceImpl(dao);
        service.setSolrUsername(solrUsername);
        service.setSolrPassword(solrPassword);

        return service;
    }

    @Bean
    public ErrorReportingService solrErrorReportingService()
    {
        SolrErrorReportingServiceDao dao = new SolrErrorReportingServiceDao();
        dao.initStandalone(solrUrl, 30);
        SolrErrorReportingManagementServiceImpl service = new SolrErrorReportingManagementServiceImpl(dao);
        service.setSolrUsername(solrUsername);
        service.setSolrPassword(solrPassword);

        return service;
    }

    @Bean
    public ExclusionManagementService solrExclusionService()
    {
        SolrExclusionEventDao dao = new SolrExclusionEventDao();
        dao.initStandalone(solrUrl, 30);
        SolrExclusionServiceImpl service = new SolrExclusionServiceImpl(dao);
        service.setSolrUsername(solrUsername);
        service.setSolrPassword(solrPassword);

        return service;
    }
}
