package org.ikasan.systemevent;

import com.arjuna.ats.internal.jta.transaction.arjunacore.TransactionManagerImple;
import com.arjuna.ats.jta.UserTransaction;
import jakarta.persistence.EntityManagerFactory;
import org.ikasan.spec.module.ModuleContainer;
import org.ikasan.systemevent.service.TestModuleContainer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.jpa.JpaTransactionManager;
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
public class SystemEventTestAutoConfiguration {

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
    public PlatformTransactionManager transactionManager() {
        TransactionManagerImple transactionManagerImple = new TransactionManagerImple();
        JtaTransactionManager jtaTransactionManager = new JtaTransactionManager(UserTransaction.userTransaction(), transactionManagerImple);

        return jtaTransactionManager;
    }

    @Bean
    ModuleContainer moduleContainer() {
        return new TestModuleContainer();
    }

    @Bean
    Properties platformJpaProperties() {
        Properties platformJpaProperties = new Properties();
        platformJpaProperties.put("hibernate.show_sql", false);
        platformJpaProperties.put("hibernate.hbm2ddl.auto", "none");
        platformJpaProperties.put("hibernate.transaction.jta.platform",
            "org.hibernate.engine.transaction.jta.platform.internal.JBossStandAloneJtaPlatform");

        return platformJpaProperties;
    }

    @Bean
    public JpaVendorAdapter jpaVendorAdapter() {
        HibernateJpaVendorAdapter hibernateJpaVendorAdapter
            = new HibernateJpaVendorAdapter();

        return hibernateJpaVendorAdapter;
    }
}
