package com.ikasan.sample.spring.boot.builderpattern;

import com.ikasan.sample.person.PersonMessageProvider;
import com.ikasan.sample.person.dao.PersonHibernateImpl;
import com.ikasan.sample.person.service.PersonServiceImpl;
import org.h2.jdbcx.JdbcDataSource;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;

import javax.sql.DataSource;
import java.util.Properties;

@Configuration
public class PersonDbConfiguration {

    @Value("${person.db.url}")
    private String url;
    @Value("${person.db.username}")
    private String username;
    @Value("${person.db.password}")
    private String password;

    @Value("${person.db.hbm2ddl.auto}")
    String hibernateHbm2ddlAuto;

    @Value("${person.db.show-sql}")
    String hibernateShowSql;

    @Bean
    public PersonMessageProvider personMessageProvider(PersonServiceImpl personService) {
        return new PersonMessageProvider(personService);
    }

    @Bean
    public PersonServiceImpl personService(PersonHibernateImpl xaPersonDao) {
        return new PersonServiceImpl(xaPersonDao);
    }

    @Bean
    @DependsOn("personEntityManager")
    public PersonHibernateImpl xaPersonDao() {
        return new PersonHibernateImpl();
    }

    @Bean(name = "personDataSource")
    DataSource personDataSource() {
        JdbcDataSource dataSource = new JdbcDataSource();
        dataSource.setURL(this.url);
        dataSource.setUser(this.username);
        dataSource.setPassword(this.password);

        return dataSource;
    }

    @Bean(name = "personJpaProperties")
    Properties personJpaProperties() {
        Properties platformJpaProperties = new Properties();
        platformJpaProperties.put("hibernate.show_sql", this.hibernateShowSql);
        platformJpaProperties.put("hibernate.hbm2ddl.auto", this.hibernateHbm2ddlAuto);
        platformJpaProperties.put("hibernate.transaction.jta.platform",
            "org.hibernate.engine.transaction.jta.platform.internal.JBossStandAloneJtaPlatform");

        return platformJpaProperties;
    }

    @Bean(name = "personEntityManager")
    public LocalContainerEntityManagerFactoryBean personEntityManager(@Qualifier("personDataSource") DataSource dataSource
        , JpaVendorAdapter jpaVendorAdapter, @Qualifier("personJpaProperties") Properties platformJpaProperties) {
        LocalContainerEntityManagerFactoryBean localContainerEntityManagerFactoryBean
            = new LocalContainerEntityManagerFactoryBean();
        localContainerEntityManagerFactoryBean.setDataSource(dataSource);
        localContainerEntityManagerFactoryBean.setJpaVendorAdapter(jpaVendorAdapter);
        localContainerEntityManagerFactoryBean.setJpaProperties(platformJpaProperties);
        localContainerEntityManagerFactoryBean.setPersistenceUnitName("person");
        localContainerEntityManagerFactoryBean.setPersistenceXmlLocation("classpath:person-persistence.xml");

        return localContainerEntityManagerFactoryBean;
    }
}
