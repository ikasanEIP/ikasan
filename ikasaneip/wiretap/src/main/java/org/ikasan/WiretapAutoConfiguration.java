package org.ikasan;

import org.ikasan.history.dao.HibernateMessageHistoryDao;
import org.ikasan.history.dao.MessageHistoryDao;
import org.ikasan.history.service.MessageHistoryServiceImpl;
import org.ikasan.spec.configuration.PlatformConfigurationService;
import org.ikasan.spec.dashboard.DashboardRestService;
import org.ikasan.spec.history.MessageHistoryService;
import org.ikasan.spec.module.ModuleService;
import org.ikasan.spec.wiretap.WiretapDao;
import org.ikasan.spec.wiretap.WiretapSerialiser;
import org.ikasan.spec.wiretap.WiretapService;
import org.ikasan.trigger.dao.HibernateTriggerDao;
import org.ikasan.trigger.dao.TriggerDao;
import org.ikasan.trigger.service.FlowEventJob;
import org.ikasan.trigger.service.LoggingEventJob;
import org.ikasan.trigger.service.WiretapEventJob;
import org.ikasan.wiretap.dao.HibernateWiretapDao;
import org.ikasan.wiretap.listener.JobAwareFlowEventListener;
import org.ikasan.wiretap.model.WiretapEventFactory;
import org.ikasan.wiretap.model.WiretapEventFactoryDefaultImpl;
import org.ikasan.wiretap.serialiser.WiretapSerialiserService;
import org.ikasan.wiretap.service.WiretapServiceImpl;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

@Configuration
public class WiretapAutoConfiguration {

    @Value("${wiretap.housekeeping.batch.size:1000}")
    private int wiretapHouseKeepingBatchSize;

    @Bean
    @DependsOn("liquibase")
    JobAwareFlowEventListener wiretapFlowEventListener(Map<String, FlowEventJob> flowEventJobs, TriggerDao triggerDao,
                                                       @Qualifier("moduleMetadataDashboardRestService") DashboardRestService moduleMetadataDashboardRestService, ModuleService moduleService) {
        return new JobAwareFlowEventListener(flowEventJobs, triggerDao, moduleService,moduleMetadataDashboardRestService);
    }

    @Bean
    WiretapService wiretapService(WiretapDao wiretapDao, ModuleService moduleService, WiretapEventFactory wiretapEventFactory) {
        return new WiretapServiceImpl(wiretapDao, moduleService, wiretapEventFactory);
    }

    @Bean
    WiretapSerialiser wiretapSerialiser() {
        return new WiretapSerialiserService(new HashMap<>());
    }

    @Bean
    MessageHistoryService messageHistoryService(MessageHistoryDao messageHistoryDao, WiretapSerialiser wiretapSerialiser
        , PlatformConfigurationService platformConfigurationService) {
        MessageHistoryServiceImpl messageHistoryService = new MessageHistoryServiceImpl(messageHistoryDao, wiretapSerialiser);
        messageHistoryService.setPlatformConfigurationService(platformConfigurationService);

        return messageHistoryService;
    }

    @Bean
    WiretapDao wiretapDao() {
        return new HibernateWiretapDao(true, wiretapHouseKeepingBatchSize);
    }

    @Bean
    TriggerDao triggerDao() {
        return new HibernateTriggerDao();
    }

    @Bean
    MessageHistoryDao messageHistoryDao() {
        return new HibernateMessageHistoryDao();
    }

    @Bean
    HashMap jobsMap(LoggingEventJob loggingJob, WiretapEventJob wiretapJob) {
        return new HashMap(Map.of(loggingJob, loggingJob, wiretapJob, wiretapJob));
    }

    @Bean
    LoggingEventJob loggingJob() {
        return new LoggingEventJob();
    }

    @Bean
    WiretapEventJob wiretapJob(WiretapService wiretapService) {
        return new WiretapEventJob(wiretapService);
    }

    @Bean
    WiretapEventFactory wiretapEventFactory(WiretapSerialiser wiretapSerialiser) {
        return new WiretapEventFactoryDefaultImpl(wiretapSerialiser);
    }
    @Bean
    public LocalContainerEntityManagerFactoryBean wiretapEntityManager(@Qualifier("ikasan.xads")DataSource dataSource
        , JpaVendorAdapter jpaVendorAdapter, @Qualifier("platformJpaProperties")Properties platformJpaProperties) {
        LocalContainerEntityManagerFactoryBean localContainerEntityManagerFactoryBean
            = new LocalContainerEntityManagerFactoryBean();
        localContainerEntityManagerFactoryBean.setDataSource(dataSource);
        localContainerEntityManagerFactoryBean.setJpaVendorAdapter(jpaVendorAdapter);
        localContainerEntityManagerFactoryBean.setJpaProperties(platformJpaProperties);
        localContainerEntityManagerFactoryBean.setPersistenceUnitName("wiretap");
        localContainerEntityManagerFactoryBean.setPersistenceXmlLocation("classpath:wiretap-persistence.xml");

        return localContainerEntityManagerFactoryBean;
    }
}
