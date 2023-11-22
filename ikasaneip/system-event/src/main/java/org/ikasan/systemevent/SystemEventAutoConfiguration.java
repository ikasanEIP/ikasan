package org.ikasan.systemevent;

import org.ikasan.spec.module.ModuleContainer;
import org.ikasan.spec.systemevent.SystemEventDao;
import org.ikasan.spec.systemevent.SystemEventService;
import org.ikasan.systemevent.dao.HibernateSystemEventDao;
import org.ikasan.systemevent.service.SystemEventServiceImpl;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;

import javax.sql.DataSource;
import java.util.Properties;

@Configuration
public class SystemEventAutoConfiguration {

    @Value("${system.event.expiry.minutes:10080}")
    private long systemEventExpiryMinutes;

    @Value("${system.event.housekeeping.batch.size:100}")
    private int systemEventHouseKeepingBatchSize;

    @Value("${system.event.transaction.batch.size:1000}")
    private int systemEventTransactionBatchSize;

    @Bean
    public SystemEventService systemEventService(SystemEventDao systemEventDao, ModuleContainer moduleContainer) {
        return new SystemEventServiceImpl(systemEventDao, systemEventExpiryMinutes, moduleContainer);
    }

    @Bean
    public SystemEventDao<?> systemEventDao() {
        return new HibernateSystemEventDao(true
            , systemEventHouseKeepingBatchSize, systemEventTransactionBatchSize);
    }

    @Bean
    public LocalContainerEntityManagerFactoryBean systemEventEntityManager(@Qualifier("ikasan.xads")DataSource dataSource
        , JpaVendorAdapter jpaVendorAdapter, Properties platformJpaProperties) {
        LocalContainerEntityManagerFactoryBean localContainerEntityManagerFactoryBean
            = new LocalContainerEntityManagerFactoryBean();
        localContainerEntityManagerFactoryBean.setDataSource(dataSource);
        localContainerEntityManagerFactoryBean.setJpaVendorAdapter(jpaVendorAdapter);
        localContainerEntityManagerFactoryBean.setJpaProperties(platformJpaProperties);
        localContainerEntityManagerFactoryBean.setPersistenceUnitName("system-event");
        localContainerEntityManagerFactoryBean.setPersistenceXmlLocation("classpath:systemevent-persistence.xml");

        return localContainerEntityManagerFactoryBean;
    }

    @Bean
    public JpaVendorAdapter jpaVendorAdapter() {
        HibernateJpaVendorAdapter hibernateJpaVendorAdapter
            = new HibernateJpaVendorAdapter();

        return hibernateJpaVendorAdapter;
    }
}
