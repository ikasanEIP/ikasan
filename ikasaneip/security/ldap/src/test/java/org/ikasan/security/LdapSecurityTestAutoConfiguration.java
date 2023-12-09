package org.ikasan.security;

import jakarta.persistence.EntityManagerFactory;
import org.ikasan.security.dao.HibernateSecurityDao;
import org.ikasan.security.dao.HibernateUserDao;
import org.ikasan.security.dao.SecurityDao;
import org.ikasan.security.dao.UserDao;
import org.ikasan.security.service.*;
import org.ikasan.security.service.authentication.AuthenticationProviderFactory;
import org.ikasan.security.service.authentication.AuthenticationProviderFactoryImpl;
import org.ikasan.security.service.authentication.CustomAuthenticationProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.util.Properties;

@Configuration
@EnableTransactionManagement
@ImportResource( locations={
    "/mock-components.xml",
    "/test-transaction.xml",
    "/substitute-components.xml",
})
public class LdapSecurityTestAutoConfiguration {
    @Value("${ikasan.dashboard.extract.enabled:false}")
    boolean preventLocalAuthentication;

    @Bean(name = {"ikasan.xads", "ikasan.ds"})
    public DataSource ikasanDataSource() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName("org.h2.Driver");
        dataSource.setUrl("jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1");
        dataSource.setUsername("sa");
        dataSource.setPassword("sa");
        return dataSource;
    }

    @Bean
    public PlatformTransactionManager transactionManager(EntityManagerFactory securityEntityManager) {
        JpaTransactionManager transactionManager = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(securityEntityManager);
        return transactionManager;
    }

    @Bean
    Properties platformJpaProperties() {
        Properties platformJpaProperties = new Properties();
        platformJpaProperties.put("hibernate.show_sql", false);
        platformJpaProperties.put("hibernate.hbm2ddl.auto", "create-drop");

        return platformJpaProperties;
    }

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
        return new UserServiceImpl(userDao(), securityService(), passwordEncoder(), this.preventLocalAuthentication);
    }

    @Bean
    public AuthenticationService authenticationService(){

        AuthenticationProviderFactory authenticationProviderFactory = new AuthenticationProviderFactoryImpl(userService(),securityService());
        return new AuthenticationServiceImpl(authenticationProviderFactory,securityService());
    }

    @Bean AuthenticationProviderFactory authenticationProviderFactory() {
        return new AuthenticationProviderFactoryImpl(userService(),securityService());
    }

    @Bean
    public AuthenticationProvider ikasanAuthenticationProvider(){

        return new CustomAuthenticationProvider(authenticationService());

    }
}
