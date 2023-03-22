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
package org.ikasan.module;

import org.ikasan.module.container.ModuleContainerImpl;
import org.ikasan.module.service.*;
import org.ikasan.module.startup.dao.HibernateStartupControlDao;
import org.ikasan.module.startup.dao.StartupControlDao;
import org.ikasan.spec.configuration.ConfigurationService;
import org.ikasan.spec.dashboard.DashboardRestService;
import org.ikasan.spec.harvest.HarvestingSchedulerService;
import org.ikasan.spec.housekeeping.HousekeepingSchedulerService;
import org.ikasan.spec.module.ModuleActivator;
import org.ikasan.spec.module.ModuleContainer;
import org.ikasan.spec.systemevent.SystemEventService;
import org.ikasan.wiretap.listener.JobAwareFlowEventListener;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;

import javax.annotation.Resource;
import javax.sql.DataSource;
import java.util.Map;
import java.util.Properties;

@Configuration
public class IkasanModuleAutoConfiguration implements ApplicationContextAware
{
    private ApplicationContext applicationContext;

    @Autowired
    ConfigurationService configurationService;

    @Resource Map platformHibernateProperties;
    @Autowired
    @Qualifier("ikasan.xads") DataSource ikasanxads;

    @Bean
    @DependsOn({"liquibase"})
    public ModuleInitialisationServiceImpl moduleLoader(ModuleContainer moduleContainer,
        ModuleActivator moduleActivator,
        HousekeepingSchedulerService housekeepingSchedulerService,
        HarvestingSchedulerService harvestingSchedulerService
    ) {
        return new ModuleInitialisationServiceImpl(moduleContainer, moduleActivator,
             housekeepingSchedulerService, harvestingSchedulerService);
    }

    @Bean
    public ModuleServiceImpl moduleService(ModuleContainer moduleContainer, SystemEventService systemEventService,
        StartupControlDao startupControlDao){
        return new ModuleServiceImpl(moduleContainer, systemEventService, startupControlDao);
    }

    @Bean
    public StartupControlServiceImpl startupControlService(SystemEventService systemEventService, StartupControlDao startupControlDao){
        return new StartupControlServiceImpl(systemEventService,startupControlDao);
    }



    @Bean
    @ConfigurationProperties (prefix="ikasan.module.activator.startup.type")
    public BulkStartupTypeSetupServiceConfiguration bulkStartupTypeSetupServiceConfiguration(){
        return new BulkStartupTypeSetupServiceConfiguration();
    }


    @Bean
    @ConfigurationProperties (prefix="ikasan.module.activator.wiretap")
    public WiretapTriggerSetupServiceConfiguration wiretapTriggerSetupServiceConfiguration(){
        return new WiretapTriggerSetupServiceConfiguration();
    }

    @Bean
    public BulkStartupTypeSetupService bulkStartupTypeSetupService(BulkStartupTypeSetupServiceConfiguration
                                                                           bulkStartupTypeSetupServiceConfiguration,
                                                                   StartupControlDao startupControlDao){
        return new BulkStartupTypeSetupService(bulkStartupTypeSetupServiceConfiguration,
            startupControlDao);
    }



    @Bean
    public WiretapTriggerSetupService wiretapTriggerSetupService(WiretapTriggerSetupServiceConfiguration
                                                                       wiretapTriggerSetupServiceConfiguration){
        JobAwareFlowEventListener wiretapTriggerService =
            applicationContext.getBean("wiretapFlowEventListener", JobAwareFlowEventListener.class);
        return new WiretapTriggerSetupService(wiretapTriggerSetupServiceConfiguration,
            wiretapTriggerService);
    }

    @Bean
    public ModuleActivator moduleActivator(StartupControlDao startupControlDao, DashboardRestService moduleMetadataDashboardRestService,
                                           DashboardRestService configurationMetadataDashboardRestService,
                                           BulkStartupTypeSetupService bulkStartupTypeSetupService,
                                           WiretapTriggerSetupService wiretapTriggerSetupService ) {
        return new ModuleActivatorDefaultImpl(configurationService, startupControlDao, moduleMetadataDashboardRestService,
            configurationMetadataDashboardRestService, bulkStartupTypeSetupService, wiretapTriggerSetupService);
    }

    @Bean
    public ModuleContainer moduleContainer() {
        return new ModuleContainerImpl();
    }

    @Bean
    public LocalSessionFactoryBean startupControlSessionFactory() {
        LocalSessionFactoryBean sessionFactoryBean = new LocalSessionFactoryBean();
        sessionFactoryBean.setDataSource(ikasanxads);
        sessionFactoryBean.setMappingResources("org/ikasan/module/startup/StartupControl.hbm.xml");
        Properties properties = new Properties();
        properties.putAll(platformHibernateProperties);
        sessionFactoryBean.setHibernateProperties(properties);
        return sessionFactoryBean;
    }


    @Bean
    public StartupControlDao startupControlDao() {

        HibernateStartupControlDao startupControlDao = new HibernateStartupControlDao();
        startupControlDao.setSessionFactory(startupControlSessionFactory().getObject());
        return startupControlDao;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
