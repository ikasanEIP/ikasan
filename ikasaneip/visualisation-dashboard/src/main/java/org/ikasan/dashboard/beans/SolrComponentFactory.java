package org.ikasan.dashboard.beans;

import org.apache.solr.client.solrj.embedded.EmbeddedSolrServer;
import org.ikasan.configuration.metadata.dao.SolrComponentConfigurationMetadataDao;
import org.ikasan.configuration.metadata.service.SolrComponentConfigurationMetadataServiceImpl;
import org.ikasan.error.reporting.dao.SolrErrorReportingServiceDao;
import org.ikasan.error.reporting.service.SolrErrorReportingManagementServiceImpl;
import org.ikasan.exclusion.dao.SolrExclusionEventDao;
import org.ikasan.exclusion.service.SolrExclusionServiceImpl;
import org.ikasan.module.metadata.dao.SolrModuleMetadataDao;
import org.ikasan.module.metadata.service.SolrModuleMetadataServiceImpl;
import org.ikasan.replay.dao.SolrReplayDao;
import org.ikasan.replay.service.SolrReplayServiceImpl;
import org.ikasan.spec.error.reporting.ErrorReportingService;
import org.ikasan.spec.exclusion.ExclusionManagementService;
import org.ikasan.spec.replay.ReplayManagementService;
import org.ikasan.spec.wiretap.WiretapService;
import org.ikasan.wiretap.dao.SolrWiretapDao;
import org.ikasan.wiretap.service.SolrWiretapServiceImpl;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
public class SolrComponentFactory
{
    @Value("${solr.url}")
    private String solrUrl;

    @Value("${solr.username}")
    private String solrUsername;

    @Value("${solr.password}")
    private String solrPassword;

    @Resource
    private EmbeddedSolrServer embeddedSolrServer;

    @Bean("wiretapEventBatchInsert")
    public WiretapService solrWiretapService()
    {
        SolrWiretapDao dao = new SolrWiretapDao();
        if(embeddedSolrServer != null)
        {
            dao.setSolrClient(embeddedSolrServer);
        }
        else
        {
            dao.initStandalone(solrUrl, 30);
        }
        SolrWiretapServiceImpl service = new SolrWiretapServiceImpl(dao);
        service.setSolrUsername(solrUsername);
        service.setSolrPassword(solrPassword);

        return service;
    }

    @Bean("errorOccurrenceBatchInsert")
    public ErrorReportingService solrErrorReportingService()
    {
        SolrErrorReportingServiceDao dao = new SolrErrorReportingServiceDao();
        if(embeddedSolrServer != null)
        {
            dao.setSolrClient(embeddedSolrServer);
        }
        else
        {
            dao.initStandalone(solrUrl, 30);
        }
        SolrErrorReportingManagementServiceImpl service = new SolrErrorReportingManagementServiceImpl(dao);
        service.setSolrUsername(solrUsername);
        service.setSolrPassword(solrPassword);

        return service;
    }

    @Bean("exclusionEventBatchInsert")
    public ExclusionManagementService solrExclusionService()
    {
        SolrExclusionEventDao dao = new SolrExclusionEventDao();
        if(embeddedSolrServer != null)
        {
            dao.setSolrClient(embeddedSolrServer);
        }
        else
        {
            dao.initStandalone(solrUrl, 30);
        }
        SolrExclusionServiceImpl service = new SolrExclusionServiceImpl(dao);
        service.setSolrUsername(solrUsername);
        service.setSolrPassword(solrPassword);

        return service;
    }

    @Bean("replayEventBatchInsert")
    public ReplayManagementService solrReplayService()
    {
        SolrReplayDao dao = new SolrReplayDao();
        if(embeddedSolrServer != null)
        {
            dao.setSolrClient(embeddedSolrServer);
        }
        else
        {
            dao.initStandalone(solrUrl, 30);
        }

        SolrReplayServiceImpl service = new SolrReplayServiceImpl(dao, dao);
        service.setSolrUsername(solrUsername);
        service.setSolrPassword(solrPassword);

        return service;
    }

    @Bean("moduleMetadataBatchInsert")
    public SolrModuleMetadataServiceImpl moduleMetadataService()
    {
        SolrModuleMetadataDao dao = new SolrModuleMetadataDao();
        if(embeddedSolrServer != null)
        {
            dao.setSolrClient(embeddedSolrServer);
        }
        else
        {
            dao.initStandalone(solrUrl, 30);
        }

        SolrModuleMetadataServiceImpl service = new SolrModuleMetadataServiceImpl(dao);
        service.setSolrUsername(solrUsername);
        service.setSolrPassword(solrPassword);

        return service;
    }

    @Bean("configurationMetadataBatchInsert")
    public SolrComponentConfigurationMetadataServiceImpl configurationMetadataService()
    {
        SolrComponentConfigurationMetadataDao dao = new SolrComponentConfigurationMetadataDao();
        if(embeddedSolrServer != null)
        {
            dao.setSolrClient(embeddedSolrServer);
        }
        else
        {
            dao.initStandalone(solrUrl, 30);
        }

        SolrComponentConfigurationMetadataServiceImpl service = new SolrComponentConfigurationMetadataServiceImpl(dao);
        service.setSolrUsername(solrUsername);
        service.setSolrPassword(solrPassword);

        return service;
    }

}
