package org.ikasan.exclusion;

import jakarta.persistence.EntityManagerFactory;
import org.ikasan.serialiser.converter.JmsMapMessageConverter;
import org.ikasan.serialiser.converter.JmsTextMessageConverter;
import org.ikasan.serialiser.service.SerialiserFactoryKryoImpl;
import org.ikasan.spec.serialiser.SerialiserFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.jms.MapMessage;
import javax.jms.TextMessage;
import javax.sql.DataSource;
import java.util.Map;
import java.util.Properties;

@Configuration
@EnableTransactionManagement
@ImportResource("/test-transaction.xml")
public class ExclusionTestAutoConfiguration
{
    @Bean(name = {"ikasan.xads", "ikasan.ds"})
    public DataSource ikasanDataSource() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName("org.h2.Driver");
        dataSource.setUrl("jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1");
        dataSource.setUsername("sa");
        dataSource.setPassword("sa");
        return dataSource;
    }

    @Bean
    public SerialiserFactory ikasanSerialiserFactory() {
        return new SerialiserFactoryKryoImpl(Map.of()
            , Map.of(TextMessage.class, new JmsTextMessageConverter(), MapMessage.class, new JmsMapMessageConverter()));
    }

    @Bean
    public PlatformTransactionManager transactionManager(EntityManagerFactory moduleEntityManager) {
        JpaTransactionManager transactionManager = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(moduleEntityManager);
        return transactionManager;
    }


    @Bean
    Properties platformJpaProperties() {
        Properties platformJpaProperties = new Properties();
        platformJpaProperties.put("hibernate.show_sql", false);
        platformJpaProperties.put("hibernate.hbm2ddl.auto", "create-drop");

        return platformJpaProperties;
    }
}
