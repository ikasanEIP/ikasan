package org.ikasan.connector.basefiletransfer;

import org.ikasan.connector.base.command.HibernateTransactionalResourceCommandDAO;
import org.ikasan.connector.basefiletransfer.outbound.persistence.HibernateBaseFileTransferDaoImpl;
import org.ikasan.connector.util.chunking.model.dao.HibernateFileChunkDao;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
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
    public HibernateTransactionalResourceCommandDAO transactionalResourceCommandDAO() {
        return new HibernateTransactionalResourceCommandDAO();
    }

    @Bean
    public HibernateFileChunkDao fileChunkDao() {
        return new HibernateFileChunkDao();
    }

    @Bean
    public HibernateBaseFileTransferDaoImpl baseFileTransferDao() {
        return new HibernateBaseFileTransferDaoImpl();
    }

    @Bean
    public LocalContainerEntityManagerFactoryBean baseFileTransferEntityManager(@Qualifier("ikasan.xads")DataSource dataSource
        , JpaVendorAdapter jpaVendorAdapter, Properties platformJpaProperties) {
        LocalContainerEntityManagerFactoryBean localContainerEntityManagerFactoryBean
            = new LocalContainerEntityManagerFactoryBean();
        localContainerEntityManagerFactoryBean.setDataSource(dataSource);
        localContainerEntityManagerFactoryBean.setJpaVendorAdapter(jpaVendorAdapter);
        localContainerEntityManagerFactoryBean.setJpaProperties(platformJpaProperties);
        localContainerEntityManagerFactoryBean.setPersistenceUnitName("file-transfer");
        localContainerEntityManagerFactoryBean.setPersistenceXmlLocation("classpath:file-transfer-persistence.xml");

        return localContainerEntityManagerFactoryBean;
    }

    @Bean
    public JpaVendorAdapter jpaVendorAdapter() {
        HibernateJpaVendorAdapter hibernateJpaVendorAdapter
            = new HibernateJpaVendorAdapter();

        return hibernateJpaVendorAdapter;
    }
}
