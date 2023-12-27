package org.ikasan.filter;

import org.ikasan.filter.duplicate.dao.FilteredMessageDao;
import org.ikasan.filter.duplicate.dao.HibernateFilteredMessageDaoImpl;
import org.ikasan.filter.duplicate.service.DefaultDuplicateFilterService;
import org.ikasan.filter.duplicate.service.DefaultEntityAgeFilterService;
import org.ikasan.filter.duplicate.service.DefaultManagementFilterService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;

import javax.sql.DataSource;
import java.util.Properties;

@Configuration
public class FilterAutoConfiguration {

    @Bean
    public DefaultManagementFilterService managementFilterService(FilteredMessageDao duplicateFilterDao) {
        return new DefaultManagementFilterService(duplicateFilterDao);
    }

    @Bean
    public DefaultDuplicateFilterService duplicateFilterService(FilteredMessageDao duplicateFilterDao) {
        return new DefaultDuplicateFilterService(duplicateFilterDao);
    }

    @Bean
    public DefaultEntityAgeFilterService entityAgeFilterService(FilteredMessageDao duplicateFilterDao) {
        return new DefaultEntityAgeFilterService(duplicateFilterDao);
    }

    @Bean
    public FilteredMessageDao duplicateFilterDao() {
        return new HibernateFilteredMessageDaoImpl();
    }

    @Bean
    public LocalContainerEntityManagerFactoryBean xaFilterEntityManager(@Qualifier("ikasan.xads")DataSource dataSource
        , JpaVendorAdapter jpaVendorAdapter, @Qualifier("platformJpaProperties")Properties platformJpaProperties) {
        LocalContainerEntityManagerFactoryBean localContainerEntityManagerFactoryBean
            = new LocalContainerEntityManagerFactoryBean();
        localContainerEntityManagerFactoryBean.setDataSource(dataSource);
        localContainerEntityManagerFactoryBean.setJpaVendorAdapter(jpaVendorAdapter);
        localContainerEntityManagerFactoryBean.setJpaProperties(platformJpaProperties);
        localContainerEntityManagerFactoryBean.setPersistenceUnitName("filter");
        localContainerEntityManagerFactoryBean.setPersistenceXmlLocation("classpath:filter-persistence.xml");

        return localContainerEntityManagerFactoryBean;
    }
}
