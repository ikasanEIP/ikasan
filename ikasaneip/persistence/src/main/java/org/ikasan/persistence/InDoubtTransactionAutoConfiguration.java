package org.ikasan.persistence;

import org.ikasan.persistence.dao.HibernateInDoubtTransactionDaoImpl;
import org.ikasan.persistence.service.InDoubtTransactionServiceImpl;
import org.ikasan.spec.persistence.dao.InDoubtTransactionDao;
import org.ikasan.spec.persistence.service.InDoubtTransactionService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;

import javax.sql.DataSource;
import java.util.Properties;

@Configuration
@ImportResource("/persistence-transaction.xml")
public class InDoubtTransactionAutoConfiguration {

    @Bean
    InDoubtTransactionService inDoubtTransactionService(InDoubtTransactionDao inDoubtTransactionDao) {
        return new InDoubtTransactionServiceImpl(inDoubtTransactionDao);
    }
    @Bean
    InDoubtTransactionDao inDoubtTransactionDao() {
        return new HibernateInDoubtTransactionDaoImpl();
    }

    @Bean
    public LocalContainerEntityManagerFactoryBean persistenceEntityManager(@Qualifier("ikasan.ds")DataSource dataSource
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
