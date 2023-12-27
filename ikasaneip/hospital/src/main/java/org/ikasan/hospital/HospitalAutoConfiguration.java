package org.ikasan.hospital;

import org.ikasan.hospital.dao.HibernateHospitalDao;
import org.ikasan.hospital.dao.HospitalDao;
import org.ikasan.hospital.service.HospitalManagementServiceImpl;
import org.ikasan.hospital.service.HospitalServiceImpl;
import org.ikasan.spec.exclusion.ExclusionManagementService;
import org.ikasan.spec.hospital.service.HospitalManagementService;
import org.ikasan.spec.hospital.service.HospitalService;
import org.ikasan.spec.module.ModuleContainer;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;

import javax.sql.DataSource;
import java.util.Properties;

@Configuration
public class HospitalAutoConfiguration {

    @Bean
    public HospitalManagementService hospitalManagementService(HospitalDao hospitalDao) {
        return new HospitalManagementServiceImpl(hospitalDao);
    }

    @Bean
    public HospitalService hospitalService(ModuleContainer moduleContainer, HospitalDao hospitalDao
        , ExclusionManagementService exclusionManagementService) {
        return new HospitalServiceImpl(moduleContainer, hospitalDao, exclusionManagementService);
    }

    @Bean
    public HospitalDao hospitalDao() {
        return new HibernateHospitalDao();
    }

    @Bean
    public LocalContainerEntityManagerFactoryBean xaHospitalEntityManager(@Qualifier("ikasan.xads")DataSource dataSource
        , JpaVendorAdapter jpaVendorAdapter, @Qualifier("platformJpaProperties")Properties platformJpaProperties) {
        LocalContainerEntityManagerFactoryBean localContainerEntityManagerFactoryBean
            = new LocalContainerEntityManagerFactoryBean();
        localContainerEntityManagerFactoryBean.setDataSource(dataSource);
        localContainerEntityManagerFactoryBean.setJpaVendorAdapter(jpaVendorAdapter);
        localContainerEntityManagerFactoryBean.setJpaProperties(platformJpaProperties);
        localContainerEntityManagerFactoryBean.setPersistenceUnitName("xa-hospital");
        localContainerEntityManagerFactoryBean.setPersistenceXmlLocation("classpath:hospital-persistence.xml");

        return localContainerEntityManagerFactoryBean;
    }

    @Bean
    public LocalContainerEntityManagerFactoryBean hospitalServiceEntityManager(@Qualifier("ikasan.ds")DataSource dataSource
        , JpaVendorAdapter jpaVendorAdapter, @Qualifier("platformJpaProperties")Properties platformJpaProperties) {
        LocalContainerEntityManagerFactoryBean localContainerEntityManagerFactoryBean
            = new LocalContainerEntityManagerFactoryBean();
        localContainerEntityManagerFactoryBean.setDataSource(dataSource);
        localContainerEntityManagerFactoryBean.setJpaVendorAdapter(jpaVendorAdapter);
        localContainerEntityManagerFactoryBean.setJpaProperties(platformJpaProperties);
        localContainerEntityManagerFactoryBean.setPersistenceUnitName("hospital");
        localContainerEntityManagerFactoryBean.setPersistenceXmlLocation("classpath:hospital-persistence.xml");

        return localContainerEntityManagerFactoryBean;
    }
}
