package org.ikasan.connector.basefiletransfer;

import org.ikasan.connector.base.command.HibernateTransactionalResourceCommandDAO;
import org.ikasan.connector.base.command.TransactionalResourceCommandDAO;
import org.ikasan.connector.basefiletransfer.outbound.persistence.BaseFileTransferDao;
import org.ikasan.connector.basefiletransfer.outbound.persistence.HibernateBaseFileTransferDaoImpl;
import org.ikasan.connector.util.chunking.model.dao.FileChunkDao;
import org.ikasan.connector.util.chunking.model.dao.HibernateFileChunkDao;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.ImportResource;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;

import javax.sql.DataSource;
import java.util.Properties;

@Configuration
@ImportResource("/transaction.xml")
public class BaseFileTransferAutoConfiguration {

    @Bean
    @DependsOn("transactionalResourceCommandEntityManager")
    public TransactionalResourceCommandDAO transactionalResourceCommandDAO() {
        return new HibernateTransactionalResourceCommandDAO();
    }

    @Bean
    @DependsOn("fileChunkEntityManager")
    public FileChunkDao fileChunkDao() {
        return new HibernateFileChunkDao();
    }

    @Bean
    @DependsOn("baseFileTransferEntityManager")
    public BaseFileTransferDao baseFileTransferDao() {
        return new HibernateBaseFileTransferDaoImpl();
    }

    @Bean(name = "baseFileTransferEntityManager")
    public LocalContainerEntityManagerFactoryBean baseFileTransferEntityManager(@Qualifier("ikasan.xads")DataSource dataSource
        , JpaVendorAdapter jpaVendorAdapter, @Qualifier("platformJpaProperties")Properties platformJpaProperties) {
        LocalContainerEntityManagerFactoryBean localContainerEntityManagerFactoryBean
            = new LocalContainerEntityManagerFactoryBean();
        localContainerEntityManagerFactoryBean.setDataSource(dataSource);
        localContainerEntityManagerFactoryBean.setJpaVendorAdapter(jpaVendorAdapter);
        localContainerEntityManagerFactoryBean.setJpaProperties(platformJpaProperties);
        localContainerEntityManagerFactoryBean.setPersistenceUnitName("file-transfer");
        localContainerEntityManagerFactoryBean.setPersistenceXmlLocation("classpath:file-transfer-persistence.xml");

        return localContainerEntityManagerFactoryBean;
    }

    @Bean(name = "fileChunkEntityManager")
    public LocalContainerEntityManagerFactoryBean fileChunkEntityManager(@Qualifier("ikasan.ds")DataSource dataSource
        , JpaVendorAdapter jpaVendorAdapter, @Qualifier("platformJpaProperties")Properties platformJpaProperties) {
        LocalContainerEntityManagerFactoryBean localContainerEntityManagerFactoryBean
            = new LocalContainerEntityManagerFactoryBean();
        localContainerEntityManagerFactoryBean.setDataSource(dataSource);
        localContainerEntityManagerFactoryBean.setJpaVendorAdapter(jpaVendorAdapter);
        localContainerEntityManagerFactoryBean.setJpaProperties(platformJpaProperties);
        localContainerEntityManagerFactoryBean.setPersistenceUnitName("file-chunk");
        localContainerEntityManagerFactoryBean.setPersistenceXmlLocation("classpath:file-chunk-persistence.xml");

        return localContainerEntityManagerFactoryBean;
    }

    @Bean(name = "transactionalResourceCommandEntityManager")
    public LocalContainerEntityManagerFactoryBean transactionalResourceCommandEntityManager(@Qualifier("ikasan.ds")DataSource dataSource
        , JpaVendorAdapter jpaVendorAdapter, @Qualifier("platformJpaProperties")Properties platformJpaProperties) {
        LocalContainerEntityManagerFactoryBean localContainerEntityManagerFactoryBean
            = new LocalContainerEntityManagerFactoryBean();
        localContainerEntityManagerFactoryBean.setDataSource(dataSource);
        localContainerEntityManagerFactoryBean.setJpaVendorAdapter(jpaVendorAdapter);
        localContainerEntityManagerFactoryBean.setJpaProperties(platformJpaProperties);
        localContainerEntityManagerFactoryBean.setPersistenceUnitName("transactional-resource-command");
        localContainerEntityManagerFactoryBean.setPersistenceXmlLocation("classpath:transactional-resource-command-persistence.xml");

        return localContainerEntityManagerFactoryBean;
    }
}
