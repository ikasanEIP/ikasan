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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.core.env.Environment;
import javax.sql.DataSource;
import java.util.Map;

@Configuration
public class SecurityConfiguration
{
    @Autowired
    @Qualifier("ikasan.ds")
    DataSource ikasands;

    @Autowired
    @Qualifier("ikasan.xads")
    DataSource ikasanxads;

    @Value("${ikasan.dashboard.extract.enabled:false}")
    boolean preventLocalAuthentication;

    @Autowired
    Map platformHibernateProperties;

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
    public UserService userService(Environment environment)
    {
        return new UserServiceImpl(userDao(), securityService(), passwordEncoder(), this.preventLocalAuthentication);
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
    public UserService xaUserService(Environment environment)
    {
        return new UserServiceImpl(xaUserDao(), xaSecurityService(), passwordEncoder(), this.preventLocalAuthentication);
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
