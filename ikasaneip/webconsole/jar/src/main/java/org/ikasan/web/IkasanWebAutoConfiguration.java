/*
 * $Id$
 * $URL$
 *
 * ====================================================================
 * Ikasan Enterprise Integration Platform
 *
 * Distributed under the Modified BSD License.
 * Copyright notice: The copyright for this software and a full listing
 * of individual contributors are as shown in the packaged copyright.txt
 * file.
 *
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *  - Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 *
 *  - Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 *  - Neither the name of the ORGANIZATION nor the names of its contributors may
 *    be used to endorse or promote products derived from this software without
 *    specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE
 * USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 * ====================================================================
 */
package org.ikasan.web;

import org.ikasan.security.dao.*;
import org.ikasan.security.service.*;
import org.ikasan.security.service.authentication.AuthenticationProviderFactory;
import org.ikasan.security.service.authentication.CustomAuthenticationProvider;
import org.ikasan.security.service.authentication.ModuleAuthenticationProviderFactoryImpl;
import org.ikasan.spec.module.ModuleService;
import org.ikasan.spec.systemevent.SystemEventService;
import org.ikasan.spec.wiretap.WiretapService;
import org.ikasan.web.controller.*;
import org.quartz.Scheduler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.core.env.Environment;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ViewResolverRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.view.InternalResourceViewResolver;
import org.springframework.web.servlet.view.JstlView;

import javax.sql.DataSource;
import java.util.Properties;

@Configuration
@EnableWebMvc
public class IkasanWebAutoConfiguration implements WebMvcConfigurer
{
    @Autowired
    @Qualifier("ikasan.ds")
    private DataSource ikasands;

    @Override
    public void configureViewResolvers(ViewResolverRegistry registry)
    {
        InternalResourceViewResolver resolver = new InternalResourceViewResolver();
        resolver.setPrefix("/WEB-INF/jsp/");
        resolver.setSuffix(".jsp");
        resolver.setViewClass(JstlView.class);
        registry.viewResolver(resolver);

        resolver = new InternalResourceViewResolver();
        resolver.setPrefix("/WEB-INF/");
        resolver.setSuffix(".jsp");
        resolver.setViewClass(JstlView.class);
        registry.viewResolver(resolver);
    }

    @Value("${ikasan.dashboard.extract.enabled:false}")
    boolean preventLocalAuthentication;

    @Bean public AdminController adminController()
    {
        return new AdminController();
    }

    @Bean public HomeController homeController()
    {
        return new HomeController();
    }

    @Bean
    @DependsOn("moduleService")
    public ModulesController modulesController(ModuleService moduleService)
    {
        return new ModulesController(moduleService);
    }

    @Bean
    @DependsOn("scheduler")
    public SchedulerController schedulerController(Scheduler scheduler)
    {
        return new SchedulerController(scheduler);
    }

    @Bean
    @DependsOn("systemEventService")
    public SystemEventLogController systemEventLogController(SystemEventService systemEventService)
    {
        return new SystemEventLogController(systemEventService);
    }

    @Bean public UsersController usersController(Environment environment, @Qualifier("userService")UserService userService
        , @Qualifier("dashboardUserService")UserService dashboardUserService)
    {
        return new UsersController(userService,dashboardUserService,environment);
    }

    @Bean
    @DependsOn({"wiretapService", "moduleService"})
    public WiretapEventsSearchFormController wiretapEventsSearchFormController(WiretapService wiretapService, ModuleService moduleService)
    {
        return new WiretapEventsSearchFormController(wiretapService, moduleService);
    }

    @Bean(name = "dashboardUserService")
    public UserService dashboardUserService(Environment environment)
    {
        return new DashboardUserServiceImpl(environment);
    }

    @Bean
    public AuthenticationProvider ikasanAuthenticationProvider(Environment environment, SecurityService securityService
        , @Qualifier("userService") UserService userService, @Qualifier("dashboardUserService") UserService dashboardUserService){

        AuthenticationProviderFactory authenticationProviderFactory = new ModuleAuthenticationProviderFactoryImpl(userService
            ,dashboardUserService,securityService, environment);
        AuthenticationService authenticationService = new AuthenticationServiceImpl(authenticationProviderFactory, securityService);
        return new CustomAuthenticationProvider(authenticationService);

    }

    @Bean
    public PasswordEncoder passwordEncoder()
    {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    @Bean
    @DependsOn("entityManagerFactory")
    public SecurityDao securityDao(){
        HibernateSecurityDao securityDao = new HibernateSecurityDao();
        return securityDao;
    }

    @Bean
    @DependsOn("entityManagerFactory")
    public UserDao userDao(){
        HibernateUserDao userDao = new HibernateUserDao();
        return userDao;
    }

    @Bean
    @DependsOn("entityManagerFactory")
    public AuthorityDao authorityDao(){
        HibernateAuthorityDao hibernateAuthorityDao = new HibernateAuthorityDao();
        return hibernateAuthorityDao;
    }

    @Bean
    public SecurityService securityService(SecurityDao securityDao)
    {
        return new SecurityServiceImpl(securityDao);
    }

    @Bean(name = "userService")
    public UserService userService(UserDao userDao, SecurityService securityService, PasswordEncoder passwordEncoder)
    {
        return new UserServiceImpl(userDao, securityService, passwordEncoder, this.preventLocalAuthentication);
    }

    @Bean(name = "entityManagerFactory") // todo work out why we need a been named entity manager factory in the context
    public LocalContainerEntityManagerFactoryBean entityManagerFactory(@Qualifier("ikasan.ds")DataSource dataSource
        , JpaVendorAdapter jpaVendorAdapter, @Qualifier("platformJpaProperties") Properties platformJpaProperties) {
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

    @Bean
    Properties platformJpaProperties() {
        Properties platformJpaProperties = new Properties();
        platformJpaProperties.put("hibernate.show_sql", false);
        platformJpaProperties.put("hibernate.hbm2ddl.auto", "none");
        platformJpaProperties.put("hibernate.transaction.jta.platform",
            "org.hibernate.engine.transaction.jta.platform.internal.JBossStandAloneJtaPlatform");

        return platformJpaProperties;
    }

}

