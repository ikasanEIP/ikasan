package org.ikasan.security;

import org.ikasan.security.dao.*;
import org.ikasan.security.service.SecurityService;
import org.ikasan.security.service.SecurityServiceImpl;
import org.ikasan.security.service.UserService;
import org.ikasan.security.service.UserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.sql.DataSource;
import java.util.Properties;

@Configuration
public class SecurityAutoConfiguration
{
    @Autowired
    @Qualifier("ikasan.ds")
    DataSource ikasands;

    @Autowired
    @Value("${ikasan.dashboard.extract.enabled:false}")
    boolean preventLocalAuthentication;

    @Bean public PasswordEncoder passwordEncoder()
    {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    @Bean
    public SecurityDao securityDao(){
        HibernateSecurityDao securityDao = new HibernateSecurityDao();
        return securityDao;
    }

    @Bean
    public UserDao userDao(){
        HibernateUserDao userDao = new HibernateUserDao();
        return userDao;
    }

    @Bean
    public AuthorityDao authorityDao(){
        HibernateAuthorityDao hibernateAuthorityDao = new HibernateAuthorityDao();
        return hibernateAuthorityDao;
    }

    @Bean
    public SecurityService securityService()
    {
        return new SecurityServiceImpl(securityDao());
    }

    @Bean
    public UserService userService(Environment environment)
    {
        return new UserServiceImpl(userDao(), securityService(), passwordEncoder(), this.preventLocalAuthentication);
    }

    @Bean
    public LocalContainerEntityManagerFactoryBean securityEntityManager(@Qualifier("ikasan.ds")DataSource dataSource
        , JpaVendorAdapter jpaVendorAdapter, @Qualifier("platformJpaProperties")Properties platformJpaProperties) {
        LocalContainerEntityManagerFactoryBean localContainerEntityManagerFactoryBean
            = new LocalContainerEntityManagerFactoryBean();
        localContainerEntityManagerFactoryBean.setDataSource(dataSource);
        localContainerEntityManagerFactoryBean.setJpaVendorAdapter(jpaVendorAdapter);
        localContainerEntityManagerFactoryBean.setJpaProperties(platformJpaProperties);
        localContainerEntityManagerFactoryBean.setPersistenceUnitName("security");
        localContainerEntityManagerFactoryBean.setPersistenceXmlLocation("classpath:security-persistence.xml");

        return localContainerEntityManagerFactoryBean;
    }

    @Bean
    public JpaVendorAdapter jpaVendorAdapter() {
        HibernateJpaVendorAdapter hibernateJpaVendorAdapter
            = new HibernateJpaVendorAdapter();

        return hibernateJpaVendorAdapter;
    }
}
