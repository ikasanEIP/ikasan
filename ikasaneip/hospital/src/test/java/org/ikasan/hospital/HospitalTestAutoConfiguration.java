package org.ikasan.hospital;

import com.arjuna.ats.internal.jta.transaction.arjunacore.TransactionManagerImple;
import com.arjuna.ats.jta.UserTransaction;
import org.ikasan.spec.exclusion.ExclusionManagementService;
import org.ikasan.spec.module.ModuleContainer;
import org.jmock.Mockery;
import org.jmock.imposters.ByteBuddyClassImposteriser;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.jta.JtaTransactionManager;

import javax.sql.DataSource;
import java.util.Properties;

@Configuration
@EnableTransactionManagement
@ImportResource({"/test-transaction.xml"})
public class HospitalTestAutoConfiguration
{
    private Mockery mockery = new Mockery()
    {{
        setImposteriser(ByteBuddyClassImposteriser.INSTANCE);
    }};

    @Bean
    public Mockery mockery() {
        return this.mockery;
    }


    @Bean
    public ModuleContainer moduleContainer() {
        return mockery.mock(ModuleContainer.class);
    }

    @Bean
    public ExclusionManagementService exclusionManagementService() {
        return mockery.mock(ExclusionManagementService.class);
    }

    @Bean(name = {"ikasan.xads", "ikasan.ds"})
    public DataSource ikasanDataSource() {
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
        platformJpaProperties.put("hibernate.transaction.jta.platform",
            "org.hibernate.engine.transaction.jta.platform.internal.JBossStandAloneJtaPlatform");

        return platformJpaProperties;
    }
}
