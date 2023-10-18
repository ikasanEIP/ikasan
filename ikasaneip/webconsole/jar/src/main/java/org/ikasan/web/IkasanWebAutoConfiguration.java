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

import org.ikasan.security.dao.HibernateSecurityDao;
import org.ikasan.security.dao.HibernateUserDao;
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
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.servlet.config.annotation.ViewResolverRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.view.InternalResourceViewResolver;
import org.springframework.web.servlet.view.JstlView;

import javax.annotation.Resource;
import javax.sql.DataSource;
import java.util.Map;
import java.util.Properties;

@Configuration
public class IkasanWebAutoConfiguration extends WebMvcConfigurerAdapter
{
    @Override public void configureViewResolvers(ViewResolverRegistry registry)
    {
        InternalResourceViewResolver resolver = new InternalResourceViewResolver();
        resolver.setPrefix("/WEB-INF/jsp/");
        resolver.setSuffix(".jsp");
        resolver.setViewClass(JstlView.class);
        registry.viewResolver(resolver);
    }

    @Resource ModuleService moduleService;

    @Resource Scheduler scheduler;

    @Resource SystemEventService systemEventService;

    @Resource WiretapService wiretapService;

    @Resource Map platformHibernateProperties;

    @Autowired
    @Qualifier("ikasan.ds")
    DataSource ikasands;



    @Bean public AdminController adminController()
    {
        return new AdminController();
    }

    @Bean public HomeController homeController()
    {
        return new HomeController();
    }

    @Bean public ModulesController modulesController()
    {
        return new ModulesController(moduleService);
    }

    @Bean public SchedulerController schedulerController()
    {
        return new SchedulerController(scheduler);
    }

    @Bean public SystemEventLogController systemEventLogController()
    {
        return new SystemEventLogController(systemEventService);
    }

    @Bean public UsersController usersController(Environment environment)
    {
        return new UsersController(userService(environment),dashboardUserService(environment),environment);
    }

    @Bean public WiretapEventsSearchFormController wiretapEventsSearchFormController()
    {
        return new WiretapEventsSearchFormController(wiretapService,moduleService);
    }


    @Bean public PasswordEncoder passwordEncoder()
    {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    @Bean
    public SecurityService securityService()
    {
        HibernateSecurityDao securityDao = new HibernateSecurityDao();
        securityDao.setSessionFactory(securitySessionFactory().getObject());
        return new SecurityServiceImpl(securityDao);
    }

    @Bean
    public UserService userService(Environment environment)
    {
        HibernateUserDao userDao = new HibernateUserDao();
        userDao.setSessionFactory(securitySessionFactory().getObject());
        return new UserServiceImpl(userDao, securityService(), passwordEncoder(), environment);
    }

    @Bean
    public UserService dashboardUserService(Environment environment)
    {
        return new DashboardUserServiceImpl(environment);
    }


    @Bean
    public LocalSessionFactoryBean securitySessionFactory()
    {
        LocalSessionFactoryBean sessionFactoryBean = new LocalSessionFactoryBean();
        sessionFactoryBean.setDataSource(ikasands);
        sessionFactoryBean.setMappingResources("/org/ikasan/security/model/Principal.hbm.xml",
            "/org/ikasan/security/model/Role.hbm.xml", "/org/ikasan/security/model/RoleModule.hbm.xml",
            "/org/ikasan/security/model/Policy.hbm.xml", "/org/ikasan/security/model/User.hbm.xml",
            "/org/ikasan/security/model/Authority.hbm.xml", "/org/ikasan/security/model/AuthenticationMethod.hbm.xml",
            "/org/ikasan/security/model/PolicyLink.hbm.xml", "/org/ikasan/security/model/PolicyLinkType.hbm.xml",
            "/org/ikasan/security/model/RoleJobPlan.hbm.xml");
        Properties properties = new Properties();
        properties.putAll(platformHibernateProperties);
        sessionFactoryBean.setHibernateProperties(properties);

        return sessionFactoryBean;
    }


    @Bean
    public AuthenticationProvider ikasanAuthenticationProvider(Environment environment){

        AuthenticationProviderFactory authenticationProviderFactory = new ModuleAuthenticationProviderFactoryImpl(userService(environment),dashboardUserService(environment),securityService(),environment);
        AuthenticationService authenticationService = new AuthenticationServiceImpl(authenticationProviderFactory,securityService());
        return new CustomAuthenticationProvider(authenticationService);

    }

}

