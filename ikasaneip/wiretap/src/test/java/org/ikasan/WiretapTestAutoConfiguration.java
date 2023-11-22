package org.ikasan;

import jakarta.persistence.EntityManagerFactory;
import liquibase.Liquibase;
import org.ikasan.spec.configuration.PlatformConfigurationService;
import org.ikasan.spec.dashboard.DashboardRestService;
import org.ikasan.spec.module.ModuleService;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.util.Properties;

@Configuration
@EnableTransactionManagement
@ImportResource("/transactions.xml")
public class WiretapTestAutoConfiguration {

    @Mock
    ModuleService moduleService;

    @Mock
    Liquibase liquibase;

    @Mock
    DashboardRestService dashboardRestService;

    @Mock
    PlatformConfigurationService platformConfigurationService;

    public WiretapTestAutoConfiguration() {
        MockitoAnnotations.openMocks(this);
    }

    @Bean(name = {"ikasan.xads", "ikasan.ds"})
    public DataSource ikasanXaDataSource() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName("org.h2.Driver");
        dataSource.setUrl("jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1");
        dataSource.setUsername("sa");
        dataSource.setPassword("sa");
        return dataSource;
    }

    @Bean
    public PlatformTransactionManager transactionManager(EntityManagerFactory entityManagerFactory) {
        JpaTransactionManager transactionManager = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(entityManagerFactory);
        return transactionManager;
    }

    @Bean
    Properties platformJpaProperties() {
        Properties platformJpaProperties = new Properties();
        platformJpaProperties.put("hibernate.show_sql", false);
        platformJpaProperties.put("hibernate.hbm2ddl.auto", "create-drop");

        return platformJpaProperties;
    }

    @Bean
    ModuleService moduleService() {
        return this.moduleService;
    }

    @Bean
    DashboardRestService dashboardRestService() {
        return this.dashboardRestService;
    }

    @Bean
    PlatformConfigurationService platformConfigurationService() {
        return this.platformConfigurationService;
    }

    @Bean
    Liquibase liquibase() {
        return this.liquibase;
    }
}
