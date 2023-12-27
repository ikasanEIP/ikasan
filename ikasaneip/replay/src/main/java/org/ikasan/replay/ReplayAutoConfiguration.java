package org.ikasan.replay;

import org.ikasan.replay.dao.HibernateReplayDao;
import org.ikasan.replay.service.ReplayManagementServiceImpl;
import org.ikasan.replay.service.ReplayRecordServiceImpl;
import org.ikasan.replay.service.ReplayServiceImpl;
import org.ikasan.spec.replay.*;
import org.ikasan.spec.serialiser.SerialiserFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;

import javax.sql.DataSource;
import java.util.Properties;

@Configuration
public class ReplayAutoConfiguration {

    @Bean(name = "replayManagementService")
    public ReplayManagementService replayManagementService(ReplayDao replayDao, ReplayAuditDao replayAuditDao) {
        return new ReplayManagementServiceImpl(replayDao, replayAuditDao);
    }

    @Bean
    public ReplayService replayService(ReplayAuditDao replayAuditDao) {
        return new ReplayServiceImpl(replayAuditDao);
    }

    @Bean
    public ReplayRecordService replayRecordService(ReplayDao replayDao, SerialiserFactory serialiserFactory) {
        return new ReplayRecordServiceImpl(serialiserFactory, replayDao);
    }

    @Bean
    public ReplayDao replayDao() {
        return new HibernateReplayDao();
    }

    @Bean
    public LocalContainerEntityManagerFactoryBean xaReplayEntityManager(@Qualifier("ikasan.xads")DataSource dataSource
        , JpaVendorAdapter jpaVendorAdapter, @Qualifier("platformJpaProperties")Properties platformJpaProperties) {
        LocalContainerEntityManagerFactoryBean localContainerEntityManagerFactoryBean
            = new LocalContainerEntityManagerFactoryBean();
        localContainerEntityManagerFactoryBean.setDataSource(dataSource);
        localContainerEntityManagerFactoryBean.setJpaVendorAdapter(jpaVendorAdapter);
        localContainerEntityManagerFactoryBean.setJpaProperties(platformJpaProperties);
        localContainerEntityManagerFactoryBean.setPersistenceUnitName("xa-replay");
        localContainerEntityManagerFactoryBean.setPersistenceXmlLocation("classpath:replay-persistence.xml");

        return localContainerEntityManagerFactoryBean;
    }

    @Bean
    public LocalContainerEntityManagerFactoryBean replayServiceEntityManager(@Qualifier("ikasan.ds")DataSource dataSource
        , JpaVendorAdapter jpaVendorAdapter, @Qualifier("platformJpaProperties")Properties platformJpaProperties) {
        LocalContainerEntityManagerFactoryBean localContainerEntityManagerFactoryBean
            = new LocalContainerEntityManagerFactoryBean();
        localContainerEntityManagerFactoryBean.setDataSource(dataSource);
        localContainerEntityManagerFactoryBean.setJpaVendorAdapter(jpaVendorAdapter);
        localContainerEntityManagerFactoryBean.setJpaProperties(platformJpaProperties);
        localContainerEntityManagerFactoryBean.setPersistenceUnitName("replay");
        localContainerEntityManagerFactoryBean.setPersistenceXmlLocation("classpath:replay-persistence.xml");

        return localContainerEntityManagerFactoryBean;
    }
}
