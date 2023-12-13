package org.ikasan.configurationService;

import org.ikasan.configurationService.dao.ConfigurationDao;
import org.ikasan.configurationService.dao.ConfigurationDaoHibernateImpl;
import org.ikasan.configurationService.metadata.JsonConfigurationMetaDataExtractor;
import org.ikasan.configurationService.metadata.JsonConfigurationMetaDataProvider;
import org.ikasan.configurationService.service.ConfiguredResourceConfigurationService;
import org.ikasan.configurationService.service.PlatformConfigurationServiceImpl;
import org.ikasan.spec.configuration.ConfigurationManagement;
import org.ikasan.spec.configuration.PlatformConfigurationService;
import org.ikasan.spec.metadata.ConfigurationMetaDataExtractor;
import org.ikasan.spec.metadata.ConfigurationMetaDataProvider;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;

import javax.sql.DataSource;
import java.util.Properties;

@Configuration
public class ConfigurationServiceAutoConfiguration {
    @Bean
    public PlatformConfigurationService platformConfigurationService(ConfigurationManagement configurationService) {
        return new PlatformConfigurationServiceImpl(configurationService);
    }

    @Bean
    public ConfiguredResourceConfigurationService configurationService(ConfigurationDao configurationServiceDao, ConfigurationDao xaConfigurationServiceDao) {
        return new ConfiguredResourceConfigurationService(configurationServiceDao, xaConfigurationServiceDao);
    }

    @Bean
    public ConfigurationDao configurationServiceDao() {
        return new ConfigurationDaoHibernateImpl();
    }

    @Bean
    public ConfigurationDao xaConfigurationServiceDao() {
        return new ConfigurationDaoHibernateImpl();
    }

    @Bean
    public ConfigurationMetaDataProvider configurationMetaDataProvider(ConfigurationManagement configurationService) {
        return new JsonConfigurationMetaDataProvider(configurationService);
    }

    @Bean
    public ConfigurationMetaDataExtractor configurationMetaDataExtractor(ConfigurationManagement configurationService) {
        return new JsonConfigurationMetaDataExtractor(configurationService);
    }

    @Bean
    public LocalContainerEntityManagerFactoryBean xaConfigurationServiceEntityManager(@Qualifier("ikasan.xads")DataSource dataSource
        , JpaVendorAdapter jpaVendorAdapter, Properties platformJpaProperties) {
        LocalContainerEntityManagerFactoryBean localContainerEntityManagerFactoryBean
            = new LocalContainerEntityManagerFactoryBean();
        localContainerEntityManagerFactoryBean.setDataSource(dataSource);
        localContainerEntityManagerFactoryBean.setJpaVendorAdapter(jpaVendorAdapter);
        localContainerEntityManagerFactoryBean.setJpaProperties(platformJpaProperties);
        localContainerEntityManagerFactoryBean.setPersistenceUnitName("xa-configuration-service");
        localContainerEntityManagerFactoryBean.setPersistenceXmlLocation("classpath:configuration-service-persistence.xml");

        return localContainerEntityManagerFactoryBean;
    }

    @Bean
    public LocalContainerEntityManagerFactoryBean configurationServiceEntityManager(@Qualifier("ikasan.ds")DataSource dataSource
        , JpaVendorAdapter jpaVendorAdapter, Properties platformJpaProperties) {
        LocalContainerEntityManagerFactoryBean localContainerEntityManagerFactoryBean
            = new LocalContainerEntityManagerFactoryBean();
        localContainerEntityManagerFactoryBean.setDataSource(dataSource);
        localContainerEntityManagerFactoryBean.setJpaVendorAdapter(jpaVendorAdapter);
        localContainerEntityManagerFactoryBean.setJpaProperties(platformJpaProperties);
        localContainerEntityManagerFactoryBean.setPersistenceUnitName("configuration-service");
        localContainerEntityManagerFactoryBean.setPersistenceXmlLocation("classpath:configuration-service-persistence.xml");

        return localContainerEntityManagerFactoryBean;
    }

    @Bean
    public JpaVendorAdapter jpaVendorAdapter() {
        HibernateJpaVendorAdapter hibernateJpaVendorAdapter
            = new HibernateJpaVendorAdapter();

        return hibernateJpaVendorAdapter;
    }
}
