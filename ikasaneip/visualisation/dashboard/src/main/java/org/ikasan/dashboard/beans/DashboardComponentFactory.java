package org.ikasan.dashboard.beans;

import org.ikasan.business.stream.metadata.dao.SolrBusinessStreamMetadataDao;
import org.ikasan.business.stream.metadata.service.SolrBusinessStreamMetaDataServiceImpl;
import org.ikasan.configuration.metadata.dao.SolrComponentConfigurationMetadataDao;
import org.ikasan.configuration.metadata.service.SolrComponentConfigurationMetadataServiceImpl;
import org.ikasan.dashboard.cache.FlowStateCache;
import org.ikasan.dashboard.ui.util.DashboardCacheAdapter;
import org.ikasan.error.reporting.dao.SolrErrorReportingServiceDao;
import org.ikasan.error.reporting.service.SolrErrorReportingManagementServiceImpl;
import org.ikasan.exclusion.dao.SolrExclusionEventDao;
import org.ikasan.exclusion.service.SolrExclusionServiceImpl;
import org.ikasan.hospital.dao.SolrHospitalDao;
import org.ikasan.hospital.service.SolrHospitalServiceImpl;
import org.ikasan.module.metadata.dao.SolrModuleMetadataDao;
import org.ikasan.module.metadata.service.SolrModuleMetadataServiceImpl;
import org.ikasan.replay.dao.SolrReplayAuditDao;
import org.ikasan.replay.dao.SolrReplayDao;
import org.ikasan.replay.service.SolrReplayAuditServiceImpl;
import org.ikasan.replay.service.SolrReplayServiceImpl;
import org.ikasan.rest.client.ModuleControlRestServiceImpl;
import org.ikasan.solr.dao.SolrGeneralDaoImpl;
import org.ikasan.solr.service.SolrGeneralServiceImpl;
import org.ikasan.spec.cache.FlowStateCacheAdapter;
import org.ikasan.spec.exclusion.ExclusionManagementService;
import org.ikasan.spec.hospital.service.HospitalAuditService;
import org.ikasan.spec.metadata.BusinessStreamMetaDataService;
import org.ikasan.spec.metadata.ModuleMetaDataProvider;
import org.ikasan.spec.persistence.BatchInsert;
import org.ikasan.spec.replay.ReplayEvent;
import org.ikasan.spec.replay.ReplayManagementService;
import org.ikasan.spec.wiretap.WiretapEvent;
import org.ikasan.spec.wiretap.WiretapService;
import org.ikasan.systemevent.dao.SolrSystemEventDao;
import org.ikasan.systemevent.service.SolrSystemEventServiceImpl;
import org.ikasan.topology.metadata.JsonFlowMetaDataProvider;
import org.ikasan.topology.metadata.JsonModuleMetaDataProvider;
import org.ikasan.wiretap.dao.SolrWiretapDao;
import org.ikasan.wiretap.service.SolrWiretapServiceImpl;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;

@Configuration
public class DashboardComponentFactory
{
    @Value("${solr.url}")
    private String solrUrl;

    @Value("${solr.username}")
    private String solrUsername;

    @Value("${solr.password}")
    private String solrPassword;

    @Resource
    private ModuleControlRestServiceImpl moduleControlRestService;


    @Bean
    public SolrGeneralServiceImpl solrSearchService()
    {
        SolrGeneralDaoImpl dao = new SolrGeneralDaoImpl();
        dao.initStandalone(solrUrl, 30);
        SolrGeneralServiceImpl service = new SolrGeneralServiceImpl(dao);
        service.setSolrUsername(solrUsername);
        service.setSolrPassword(solrPassword);

        return service;
    }

    @Bean("wiretapEventBatchInsert")
    public BatchInsert<WiretapEvent> solrWiretapService()
    {
        SolrWiretapDao dao = new SolrWiretapDao();
        dao.initStandalone(solrUrl, 30);
        SolrWiretapServiceImpl service = new SolrWiretapServiceImpl(dao);
        service.setSolrUsername(solrUsername);
        service.setSolrPassword(solrPassword);

        return service;
    }

    @Bean("errorOccurrenceBatchInsert")
    public SolrErrorReportingManagementServiceImpl solrErrorReportingService()
    {
        SolrErrorReportingServiceDao dao = new SolrErrorReportingServiceDao();
        dao.initStandalone(solrUrl, 30);
        SolrErrorReportingManagementServiceImpl service = new SolrErrorReportingManagementServiceImpl(dao);
        service.setSolrUsername(solrUsername);
        service.setSolrPassword(solrPassword);

        return service;
    }

    @Bean("exclusionEventBatchInsert")
    public ExclusionManagementService solrExclusionService()
    {
        SolrExclusionEventDao dao = new SolrExclusionEventDao();
        dao.initStandalone(solrUrl, 30);
        SolrExclusionServiceImpl service = new SolrExclusionServiceImpl(dao);
        service.setSolrUsername(solrUsername);
        service.setSolrPassword(solrPassword);

        return service;
    }

    @Bean("replayEventBatchInsert")
    public BatchInsert<ReplayEvent> solrReplayService()
    {
        SolrReplayDao dao = new SolrReplayDao();
        dao.initStandalone(solrUrl, 30);

        SolrReplayServiceImpl service = new SolrReplayServiceImpl(dao, dao);
        service.setSolrUsername(solrUsername);
        service.setSolrPassword(solrPassword);

        return service;
    }

    @Bean
    public BatchInsert replayAuditService()
    {
        return createSolrReplayAuditServiceImpl();
    }

    private SolrReplayAuditServiceImpl createSolrReplayAuditServiceImpl()
    {
        SolrReplayAuditDao dao = new SolrReplayAuditDao();
        dao.initStandalone(solrUrl, 30);

        SolrReplayAuditServiceImpl service = new SolrReplayAuditServiceImpl(dao);
        service.setSolrUsername(solrUsername);
        service.setSolrPassword(solrPassword);

        return service;
    }

    @Bean
    public HospitalAuditService hospitalAuditService()
    {
        SolrHospitalDao dao = new SolrHospitalDao();
        dao.initStandalone(solrUrl, 30);

        SolrHospitalServiceImpl service = new SolrHospitalServiceImpl(dao);
        service.setSolrUsername(solrUsername);
        service.setSolrPassword(solrPassword);

        return service;
    }

    @Bean
    public BatchInsert moduleMetadataBatchInsert()
    {
        return this.createSolrModuleMetadataServiceImpl();
    }

    @Bean
    public SolrModuleMetadataServiceImpl moduleMetadataService()
    {
        return this.createSolrModuleMetadataServiceImpl();
    }

    private SolrModuleMetadataServiceImpl createSolrModuleMetadataServiceImpl()
    {
        SolrModuleMetadataDao dao = new SolrModuleMetadataDao();
        dao.initStandalone(solrUrl, 30);

        SolrModuleMetadataServiceImpl service = new SolrModuleMetadataServiceImpl(dao);
        service.setSolrUsername(solrUsername);
        service.setSolrPassword(solrPassword);

        return service;
    }

    @Bean
    public BusinessStreamMetaDataService businessStreamMetaDataService()
    {
        SolrBusinessStreamMetadataDao dao = new SolrBusinessStreamMetadataDao();
        dao.initStandalone(solrUrl, 30);

        SolrBusinessStreamMetaDataServiceImpl service = new SolrBusinessStreamMetaDataServiceImpl(dao);
        service.setSolrUsername(solrUsername);
        service.setSolrPassword(solrPassword);

        return service;
    }

    @Bean
    public BatchInsert systemEventBatchInsert()
    {
        return this.createSolrSystemEventServiceImpl();
    }


    private SolrSystemEventServiceImpl createSolrSystemEventServiceImpl()
    {
        SolrSystemEventDao dao = new SolrSystemEventDao();
        dao.initStandalone(solrUrl, 30);

        SolrSystemEventServiceImpl service = new SolrSystemEventServiceImpl(dao);
        service.setSolrUsername(solrUsername);
        service.setSolrPassword(solrPassword);

        return service;
    }

    @Bean
    public SolrComponentConfigurationMetadataServiceImpl configurationMetadataService()
    {
        return createSolrComponentConfigurationMetadataServiceImpl();
    }

    @Bean
    public BatchInsert configurationMetadataBatchInsert()
    {
        return createSolrComponentConfigurationMetadataServiceImpl();
    }

    private SolrComponentConfigurationMetadataServiceImpl createSolrComponentConfigurationMetadataServiceImpl()
    {
        SolrComponentConfigurationMetadataDao dao = new SolrComponentConfigurationMetadataDao();
        dao.initStandalone(solrUrl, 30);

        SolrComponentConfigurationMetadataServiceImpl service = new SolrComponentConfigurationMetadataServiceImpl(dao);
        service.setSolrUsername(solrUsername);
        service.setSolrPassword(solrPassword);

        return service;
    }

    @Bean
    public FlowStateCacheAdapter dashboardCacheAdapter()
    {
        return new DashboardCacheAdapter();
    }

    @Bean
    public FlowStateCache flowStateCache()
    {
        FlowStateCache flowStateCache = FlowStateCache.instance();
        flowStateCache.setModuleControlRestService(this.moduleControlRestService);
        return flowStateCache;
    }

    @Bean
    public ModuleMetaDataProvider<String> moduleMetaDataProvider() {
        return new JsonModuleMetaDataProvider(new JsonFlowMetaDataProvider());
    }
}
