package org.ikasan.exclusion;

import org.ikasan.exclusion.dao.BlackListDaoFactory;
import org.ikasan.exclusion.dao.HibernateExclusionEventDao;
import org.ikasan.exclusion.service.ExclusionManagementServiceImpl;
import org.ikasan.exclusion.service.ExclusionSearchServiceImpl;
import org.ikasan.exclusion.service.ExclusionServiceFactory;
import org.ikasan.spec.exclusion.ExclusionEventDao;
import org.ikasan.spec.exclusion.ExclusionManagementService;
import org.ikasan.spec.exclusion.ExclusionSearchService;
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
public class ExclusionAutoConfiguration
{
    @Bean
    public ExclusionServiceFactory exclusionServiceFactory(BlackListDaoFactory blackListDaoFactory
        , ExclusionEventDao exclusionServiceExclusionEventDao, SerialiserFactory ikasanSerialiserFactory) {
        return new ExclusionServiceFactory(blackListDaoFactory, exclusionServiceExclusionEventDao, ikasanSerialiserFactory);
    }

    @Bean
    public BlackListDaoFactory blackListDaoFactory() {
        return new BlackListDaoFactory(1000);
    }

    @Bean
    public ExclusionEventDao exclusionServiceExclusionEventDao() {
        return new HibernateExclusionEventDao();
    }

    @Bean
    public ExclusionManagementService exclusionManagementService(ExclusionEventDao exclusionServiceExclusionEventDao) {
        return new ExclusionManagementServiceImpl(exclusionServiceExclusionEventDao);
    }

    @Bean
    public ExclusionSearchService exclusionSearchService(ExclusionEventDao exclusionServiceExclusionEventDao) {
        return new ExclusionSearchServiceImpl(exclusionServiceExclusionEventDao);
    }

    @Bean
    public LocalContainerEntityManagerFactoryBean exclusionEntityManager(@Qualifier("ikasan.xads")DataSource dataSource
        , JpaVendorAdapter jpaVendorAdapter, Properties platformJpaProperties) {
        LocalContainerEntityManagerFactoryBean localContainerEntityManagerFactoryBean
            = new LocalContainerEntityManagerFactoryBean();
        localContainerEntityManagerFactoryBean.setDataSource(dataSource);
        localContainerEntityManagerFactoryBean.setJpaVendorAdapter(jpaVendorAdapter);
        localContainerEntityManagerFactoryBean.setJpaProperties(platformJpaProperties);
        localContainerEntityManagerFactoryBean.setPersistenceUnitName("exclusion");
        localContainerEntityManagerFactoryBean.setPersistenceXmlLocation("classpath:exclusion-persistence.xml");

        return localContainerEntityManagerFactoryBean;
    }

    @Bean
    public JpaVendorAdapter jpaVendorAdapter() {
        HibernateJpaVendorAdapter hibernateJpaVendorAdapter
            = new HibernateJpaVendorAdapter();

        return hibernateJpaVendorAdapter;
    }
}
