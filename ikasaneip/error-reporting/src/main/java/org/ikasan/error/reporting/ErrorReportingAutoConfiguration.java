package org.ikasan.error.reporting;

import org.ikasan.error.reporting.dao.ErrorManagementDao;
import org.ikasan.error.reporting.dao.HibernateErrorManagementDao;
import org.ikasan.error.reporting.dao.HibernateErrorReportingServiceDao;
import org.ikasan.error.reporting.service.ErrorReportingManagementServiceImpl;
import org.ikasan.error.reporting.service.ErrorReportingServiceFactoryDefaultImpl;
import org.ikasan.spec.error.reporting.ErrorReportingManagementService;
import org.ikasan.spec.error.reporting.ErrorReportingService;
import org.ikasan.spec.error.reporting.ErrorReportingServiceDao;
import org.ikasan.spec.error.reporting.ErrorReportingServiceFactory;
import org.ikasan.spec.serialiser.SerialiserFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;

import javax.sql.DataSource;
import java.util.Properties;

@Configuration
public class ErrorReportingAutoConfiguration
{
    @Bean
    @Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    public ErrorReportingService errorReportingService(ErrorReportingServiceFactory errorReportingServiceFactory) {
        return errorReportingServiceFactory.getErrorReportingService();
    }

    @Bean
    public ErrorReportingServiceFactory errorReportingServiceFactory(SerialiserFactory serialiserFactory
        , ErrorReportingServiceDao errorReportingServiceDao) {
        return new ErrorReportingServiceFactoryDefaultImpl(serialiserFactory, errorReportingServiceDao);
    }

    @Bean
    public ErrorReportingManagementService errorReportingManagementService(ErrorReportingServiceDao errorReportingServiceDao
        , ErrorManagementDao errorManagementDao) {
        return new ErrorReportingManagementServiceImpl(errorManagementDao, errorReportingServiceDao);
    }

    @Bean
    public ErrorReportingServiceDao errorReportingServiceDao() {
        return new HibernateErrorReportingServiceDao();
    }

    @Bean
    public ErrorManagementDao errorManagementDao() {
        return new HibernateErrorManagementDao();
    }

    @Bean
    public LocalContainerEntityManagerFactoryBean exclusionEntityManager(@Qualifier("ikasan.xads")DataSource dataSource
        , JpaVendorAdapter jpaVendorAdapter, Properties platformJpaProperties) {
        LocalContainerEntityManagerFactoryBean localContainerEntityManagerFactoryBean
            = new LocalContainerEntityManagerFactoryBean();
        localContainerEntityManagerFactoryBean.setDataSource(dataSource);
        localContainerEntityManagerFactoryBean.setJpaVendorAdapter(jpaVendorAdapter);
        localContainerEntityManagerFactoryBean.setJpaProperties(platformJpaProperties);
        localContainerEntityManagerFactoryBean.setPersistenceUnitName("error-reporting");
        localContainerEntityManagerFactoryBean.setPersistenceXmlLocation("classpath:error-reporting-persistence.xml");

        return localContainerEntityManagerFactoryBean;
    }

    @Bean
    public JpaVendorAdapter jpaVendorAdapter() {
        HibernateJpaVendorAdapter hibernateJpaVendorAdapter
            = new HibernateJpaVendorAdapter();

        return hibernateJpaVendorAdapter;
    }
}
