package org.ikasan.security;

import org.ikasan.security.dao.HibernateSecurityDao;
import org.ikasan.security.dao.HibernateUserDao;
import org.ikasan.security.dao.SecurityDao;
import org.ikasan.security.dao.UserDao;
import org.ikasan.security.service.*;
import org.ikasan.security.service.authentication.AuthenticationProviderFactory;
import org.ikasan.security.service.authentication.AuthenticationProviderFactoryImpl;
import org.ikasan.security.service.authentication.CustomAuthenticationProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.annotation.Resource;
import javax.sql.DataSource;
import java.util.Map;
import java.util.Properties;

@Configuration
public class SecurityConfiguration
{
    @Autowired
    @Qualifier("ikasan.ds")
    DataSource ikasands;

    @Autowired
    @Qualifier("ikasan.xads")
    DataSource ikasanxads;

    @Resource
    Map platformHibernateProperties;

    @Bean public PasswordEncoder passwordEncoder()
    {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    @Bean
    public SecurityDao securityDao(){
        HibernateSecurityDao securityDao = new HibernateSecurityDao();
        securityDao.setSessionFactory(xaSecuritySessionFactory().getObject());
        return securityDao;
    }

    @Bean
    public UserDao userDao(){
        HibernateUserDao userDao = new HibernateUserDao();
        userDao.setSessionFactory(xaSecuritySessionFactory().getObject());
        return userDao;
    }

    @Bean
    public SecurityService securityService()
    {
        return new SecurityServiceImpl(securityDao());
    }

    @Bean
    public UserService userService(Environment environment)
    {
        return new UserServiceImpl(userDao(), securityService(), passwordEncoder(), environment);
    }


    @Bean
    public LocalSessionFactoryBean securitySessionFactory(
    )
    {
        LocalSessionFactoryBean sessionFactoryBean = new LocalSessionFactoryBean();
        sessionFactoryBean.setDataSource(ikasands);
        sessionFactoryBean.setMappingResources("/org/ikasan/security/model/Principal.hbm.xml",
            "/org/ikasan/security/model/Role.hbm.xml", "/org/ikasan/security/model/Policy.hbm.xml",
            "/org/ikasan/security/model/User.hbm.xml", "/org/ikasan/security/model/Authority.hbm.xml",
            "/org/ikasan/security/model/AuthenticationMethod.hbm.xml", "/org/ikasan/security/model/PolicyLink.hbm.xml",
            "/org/ikasan/security/model/PolicyLinkType.hbm.xml", "/org/ikasan/security/model/RoleModule.hbm.xml",
            "/org/ikasan/security/model/RoleJobPlan.hbm.xml");
        Properties properties = new Properties();
        properties.putAll(platformHibernateProperties);
        sessionFactoryBean.setHibernateProperties(properties);

        return sessionFactoryBean;
    }

    @Bean
    public AuthenticationService authenticationService(Environment environment){

        AuthenticationProviderFactory authenticationProviderFactory = new AuthenticationProviderFactoryImpl(userService(environment),securityService());
        return new AuthenticationServiceImpl(authenticationProviderFactory,securityService());
    }

    @Bean
    public AuthenticationProvider ikasanAuthenticationProvider(Environment environment){

        return new CustomAuthenticationProvider(authenticationService(environment));

    }

    @Bean
    public SecurityDao xaSecurityDao(){
        HibernateSecurityDao securityDao = new HibernateSecurityDao();
        securityDao.setSessionFactory(xaSecuritySessionFactory().getObject());
        return securityDao;
    }

    @Bean
    public UserDao xaUserDao(){
        HibernateUserDao userDao = new HibernateUserDao();
        userDao.setSessionFactory(xaSecuritySessionFactory().getObject());
        return userDao;
    }

    @Bean
    public SecurityService xaSecurityService()
    {
        return new SecurityServiceImpl(xaSecurityDao());
    }

    @Bean
    public UserService xaUserService(Environment environment)
    {
        return new UserServiceImpl(xaUserDao(), xaSecurityService(), passwordEncoder(), environment);
    }


    @Bean
    public LocalSessionFactoryBean xaSecuritySessionFactory()
    {
        LocalSessionFactoryBean sessionFactoryBean = new LocalSessionFactoryBean();
        sessionFactoryBean.setDataSource(ikasanxads);
        sessionFactoryBean.setMappingResources("/org/ikasan/security/model/Principal.hbm.xml",
            "/org/ikasan/security/model/Role.hbm.xml", "/org/ikasan/security/model/Policy.hbm.xml",
            "/org/ikasan/security/model/User.hbm.xml", "/org/ikasan/security/model/Authority.hbm.xml",
            "/org/ikasan/security/model/AuthenticationMethod.hbm.xml", "/org/ikasan/security/model/PolicyLink.hbm.xml",
            "/org/ikasan/security/model/PolicyLinkType.hbm.xml", "/org/ikasan/security/model/RoleModule.hbm.xml",
            "/org/ikasan/security/model/RoleJobPlan.hbm.xml");
        Properties properties = new Properties();
        properties.putAll(platformHibernateProperties);
        sessionFactoryBean.setHibernateProperties(properties);

        return sessionFactoryBean;
    }

    @Bean
    public AuthenticationProviderFactory xaAuthenticationProviderFactory(Environment environment){

        return new AuthenticationProviderFactoryImpl(xaUserService(environment),xaSecurityService());
    }

    @Bean
    public AuthenticationService xaAuthenticationService(Environment environment){

        return new AuthenticationServiceImpl(xaAuthenticationProviderFactory(environment),xaSecurityService());
    }

    @Bean
    public AuthenticationProvider xaIkasanAuthenticationProvider(Environment environment){

        return new CustomAuthenticationProvider(xaAuthenticationService(environment));

    }

}
