package org.ikasan;

import com.arjuna.ats.internal.jta.transaction.arjunacore.TransactionManagerImple;
import com.arjuna.ats.jta.UserTransaction;
import liquibase.Liquibase;
import org.ikasan.spec.configuration.PlatformConfigurationService;
import org.ikasan.spec.dashboard.DashboardRestService;
import org.ikasan.spec.module.ModuleService;
import org.ikasan.spec.wiretap.WiretapDao;
import org.ikasan.wiretap.dao.HibernateWiretapDao;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.jta.JtaTransactionManager;

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
    DashboardRestService moduleMetadataDashboardRestService;

    @Mock
    PlatformConfigurationService platformConfigurationService;

    public WiretapTestAutoConfiguration() {
        MockitoAnnotations.openMocks(this);
    }

    @Bean
    WiretapDao wiretapDaoDeleteOnceHarvested() {
        return new HibernateWiretapDao(true, 1000, true);
    }

    @Bean(name = {"ikasan.xads", "ikasan.ds"})
    public DataSource ikasanXaDataSource() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName("org.h2.Driver");
        dataSource.setUrl("jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1;NON_KEYWORDS=VALUE");
        dataSource.setUsername("sa");
        dataSource.setPassword("sa");
        return dataSource;
    }

    @Bean
    public PlatformTransactionManager transactionManager() {
        TransactionManagerImple transactionManagerImple = new TransactionManagerImple();
        JtaTransactionManager jtaTransactionManager = new JtaTransactionManager(UserTransaction.userTransaction(), transactionManagerImple);

        return jtaTransactionManager;
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
    DashboardRestService moduleMetadataDashboardRestService() {
        return this.moduleMetadataDashboardRestService;
    }

    @Bean
    PlatformConfigurationService platformConfigurationService() {
        return this.platformConfigurationService;
    }

    @Bean
    Liquibase liquibase() {
        return this.liquibase;
    }

    @Bean
    public JpaVendorAdapter jpaVendorAdapter() {
        HibernateJpaVendorAdapter hibernateJpaVendorAdapter
            = new HibernateJpaVendorAdapter();

        return hibernateJpaVendorAdapter;
    }
}
