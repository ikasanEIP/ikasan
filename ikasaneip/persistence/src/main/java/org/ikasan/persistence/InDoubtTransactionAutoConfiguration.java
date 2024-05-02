package org.ikasan.persistence;

import org.ikasan.persistence.dao.GeneralDatabaseDaoImpl;
import org.ikasan.persistence.service.GeneralDatabaseServiceImpl;
import org.ikasan.spec.persistence.dao.GeneralDatabaseDao;
import org.ikasan.spec.persistence.service.GeneralDatabaseService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;

import javax.sql.DataSource;
import java.util.Properties;

@Configuration
public class InDoubtTransactionAutoConfiguration {

    @Bean
    @Bean
    public LocalContainerEntityManagerFactoryBean systemEventEntityManager(@Qualifier("ikasan.ds")DataSource dataSource
        , JpaVendorAdapter jpaVendorAdapter, @Qualifier("platformJpaProperties") Properties platformJpaProperties) {
        LocalContainerEntityManagerFactoryBean localContainerEntityManagerFactoryBean
            = new LocalContainerEntityManagerFactoryBean();
        localContainerEntityManagerFactoryBean.setDataSource(dataSource);
        localContainerEntityManagerFactoryBean.setJpaVendorAdapter(jpaVendorAdapter);
        localContainerEntityManagerFactoryBean.setJpaProperties(platformJpaProperties);
        localContainerEntityManagerFactoryBean.setPersistenceUnitName("persistence");
        localContainerEntityManagerFactoryBean.setPersistenceXmlLocation("classpath:persistence-persistence.xml");

        return localContainerEntityManagerFactoryBean;
    }
}
