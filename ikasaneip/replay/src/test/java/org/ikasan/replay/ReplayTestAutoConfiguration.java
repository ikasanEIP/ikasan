package org.ikasan.replay;

import com.arjuna.ats.internal.jta.transaction.arjunacore.TransactionManagerImple;
import com.arjuna.ats.jta.UserTransaction;
import jakarta.jms.MapMessage;
import jakarta.jms.TextMessage;
import org.ikasan.replay.dao.HibernateReplayDao;
import org.ikasan.replay.service.ReplayManagementServiceImpl;
import org.ikasan.serialiser.converter.JmsMapMessageConverter;
import org.ikasan.serialiser.converter.JmsTextMessageConverter;
import org.ikasan.serialiser.service.SerialiserFactoryKryoImpl;
import org.ikasan.spec.module.ModuleContainer;
import org.ikasan.spec.replay.ReplayAuditDao;
import org.ikasan.spec.replay.ReplayDao;
import org.ikasan.spec.replay.ReplayManagementService;
import org.ikasan.spec.serialiser.Serialiser;
import org.ikasan.spec.serialiser.SerialiserFactory;
import org.jmock.Mockery;
import org.jmock.imposters.ByteBuddyClassImposteriser;
import org.springframework.beans.factory.annotation.Qualifier;
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
import java.util.Map;
import java.util.Properties;

@Configuration
@EnableTransactionManagement
@ImportResource({"/test-transaction.xml"})
public class ReplayTestAutoConfiguration
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
    public SerialiserFactory serialiserFactory() {
        return mockery.mock(SerialiserFactory.class);
    }

    @Bean
    public Serialiser serialiser() {
        return mockery.mock(Serialiser.class);
    }

    @Bean
    public ModuleContainer moduleContainer() {
        return mockery.mock(ModuleContainer.class);
    }

    @Bean(name = "deleteOnceHarvestedReplayManagementService")
    public ReplayManagementService deleteOnceHarvestedReplayManagementService(@Qualifier("deleteOnceHarvestedReplayDao")ReplayDao replayDao
        , ReplayAuditDao replayAuditDao) {
        return new ReplayManagementServiceImpl(replayDao, replayAuditDao);
    }

    @Bean(name = "deleteOnceHarvestedReplayDao")
    public ReplayDao deleteOnceHarvestedReplayDao() {
        return new HibernateReplayDao(true);
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

    @Bean
    public JpaVendorAdapter jpaVendorAdapter() {
        HibernateJpaVendorAdapter hibernateJpaVendorAdapter
            = new HibernateJpaVendorAdapter();

        return hibernateJpaVendorAdapter;
    }
}
