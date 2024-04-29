package org.ikasan.persistence;

import org.ikasan.persistence.dao.GeneralDatabaseDaoImpl;
import org.ikasan.persistence.service.GeneralDatabaseServiceImpl;
import org.ikasan.spec.persistence.dao.GeneralDatabaseDao;
import org.ikasan.spec.persistence.service.GeneralDatabaseService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Configuration
public class PersistenceAutoConfiguration {

    @Bean
    public GeneralDatabaseService generalDatabaseService(GeneralDatabaseDao generalDatabaseDao) {
        return new GeneralDatabaseServiceImpl(generalDatabaseDao);
    }
    @Bean
    public GeneralDatabaseDao generalDatabaseDao(@Qualifier("ikasan.ds") DataSource ikasanDataSource) {
        return new GeneralDatabaseDaoImpl(ikasanDataSource);
    }
}
