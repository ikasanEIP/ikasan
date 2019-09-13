package org.ikasan.dashboard.beans;

import org.ikasan.dashboard.ui.util.SystemEventLogger;
import org.ikasan.security.dao.HibernateSecurityDao;
import org.ikasan.security.dao.HibernateUserDao;
import org.ikasan.security.dao.SecurityDao;
import org.ikasan.security.dao.UserDao;
import org.ikasan.security.service.*;
import org.ikasan.security.service.authentication.AuthenticationProviderFactory;
import org.ikasan.security.service.authentication.AuthenticationProviderFactoryImpl;
import org.ikasan.security.service.authentication.CustomAuthenticationProvider;
import org.ikasan.systemevent.dao.HibernateSystemEventDao;
import org.ikasan.systemevent.dao.SystemEventDao;
import org.ikasan.systemevent.service.SystemEventService;
import org.ikasan.systemevent.service.SystemEventServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.annotation.Resource;
import javax.sql.DataSource;
import java.util.Map;
import java.util.Properties;

@Configuration
@ImportResource( {
    "classpath:datasource-conf.xml",
    "classpath:transaction-conf.xml"
} )
public class IkasanSecurityConfiguration
{
    @Autowired
    @Qualifier("ikasan.ds")
    private DataSource ikasands;

    @Resource
    private Map platformHibernateProperties;

    @Bean public PasswordEncoder passwordEncoder()
    {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    @Bean
    public SecurityDao securityDao(){
        HibernateSecurityDao securityDao = new HibernateSecurityDao();
        securityDao.setSessionFactory(securitySessionFactory().getObject());
        return securityDao;
    }

    @Bean
    public UserDao userDao(){
        HibernateUserDao userDao = new HibernateUserDao();
        userDao.setSessionFactory(securitySessionFactory().getObject());
        return userDao;
    }

    @Bean
    public SecurityService securityService()
    {
        return new SecurityServiceImpl(securityDao());
    }

    @Bean
    public UserService userService()
    {
        return new UserServiceImpl(userDao(), securityService(), passwordEncoder());
    }

    @Bean
    SystemEventLogger systemEventLogger(SystemEventService systemEventService)
    {
        return new SystemEventLogger(systemEventService);
    }

    @Bean
    public SystemEventService systemEventService()
    {
        return new SystemEventServiceImpl(systemEventDao(), 10800L);
    }

    private SystemEventDao systemEventDao(){
        HibernateSystemEventDao systemEventDao = new HibernateSystemEventDao();
        systemEventDao.setSessionFactory(securitySessionFactory().getObject());
        return systemEventDao;
    }

    @Bean
    public LocalSessionFactoryBean securitySessionFactory(
    )
    {
        LocalSessionFactoryBean sessionFactoryBean = new LocalSessionFactoryBean();
        sessionFactoryBean.setDataSource(ikasands);
        sessionFactoryBean.setMappingResources(
            "/org/ikasan/security/model/Principal.hbm.xml",
            "/org/ikasan/security/model/PrincipalLite.hbm.xml",
            "/org/ikasan/security/model/Role.hbm.xml",
            "/org/ikasan/security/model/Policy.hbm.xml",
            "/org/ikasan/security/model/User.hbm.xml",
            "/org/ikasan/security/model/UserLite.hbm.xml",
            "/org/ikasan/security/model/Authority.hbm.xml",
            "/org/ikasan/security/model/AuthenticationMethod.hbm.xml",
            "/org/ikasan/security/model/PolicyLink.hbm.xml",
            "/org/ikasan/security/model/PolicyLinkType.hbm.xml",
            "/org/ikasan/systemevent/model/SystemEvent.hbm.xml");
        Properties properties = new Properties();
        properties.putAll(platformHibernateProperties);
        sessionFactoryBean.setHibernateProperties(properties);

        return sessionFactoryBean;
    }

    @Bean
    public AuthenticationService authenticationService(){

        AuthenticationProviderFactory authenticationProviderFactory = new AuthenticationProviderFactoryImpl(userService(),securityService());
        return new AuthenticationServiceImpl(authenticationProviderFactory,securityService());
    }

    @Bean
    public AuthenticationProvider ikasanAuthenticationProvider(){

        return new CustomAuthenticationProvider(authenticationService());

    }

    @Bean
    public LdapService ldapService()
    {
        return new LdapServiceImpl(securityDao(), userDao(), passwordEncoder());
    }

    @Bean
    public AuthenticationProviderFactory authenticationProviderFactory()
    {
        return new AuthenticationProviderFactoryImpl(userService(), securityService());
    }
}
