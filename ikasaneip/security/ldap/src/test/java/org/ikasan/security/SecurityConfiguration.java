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
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.sql.DataSource;

@Configuration
public class SecurityConfiguration
{
    @Autowired
    @Qualifier("ikasan.ds")
    DataSource ikasands;

    @Autowired
    @Qualifier("ikasan.xads")
    DataSource ikasanxads;

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
    public AuthenticationService authenticationService(){

        AuthenticationProviderFactory authenticationProviderFactory = new AuthenticationProviderFactoryImpl(userService(),securityService());
        return new AuthenticationServiceImpl(authenticationProviderFactory,securityService());
    }

    @Bean
    public AuthenticationProvider ikasanAuthenticationProvider(){

        return new CustomAuthenticationProvider(authenticationService());

    }

    @Bean
    public SecurityDao xaSecurityDao(){
        HibernateSecurityDao securityDao = new HibernateSecurityDao();
        return securityDao;
    }

    @Bean
    public UserDao xaUserDao(){
        HibernateUserDao userDao = new HibernateUserDao();
        return userDao;
    }

    @Bean
    public SecurityService xaSecurityService()
    {
        return new SecurityServiceImpl(xaSecurityDao());
    }

    @Bean
    public UserService xaUserService()
    {
        return new UserServiceImpl(xaUserDao(), xaSecurityService(), passwordEncoder());
    }


    @Bean
    public AuthenticationProviderFactory xaAuthenticationProviderFactory(){

        return new AuthenticationProviderFactoryImpl(xaUserService(),xaSecurityService());
    }

    @Bean
    public AuthenticationService xaAuthenticationService(){

        return new AuthenticationServiceImpl(xaAuthenticationProviderFactory(),xaSecurityService());
    }

    @Bean
    public AuthenticationProvider xaIkasanAuthenticationProvider(){

        return new CustomAuthenticationProvider(xaAuthenticationService());

    }

}
