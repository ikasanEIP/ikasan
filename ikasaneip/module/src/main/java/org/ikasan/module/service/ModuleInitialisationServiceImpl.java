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
package org.ikasan.module.service;

import org.ikasan.scheduler.SchedulerFactory;
import org.ikasan.spec.flow.Flow;
import org.ikasan.spec.flow.FlowConfiguration;
import org.ikasan.spec.harvest.HarvestingSchedulerService;
import org.ikasan.spec.housekeeping.HousekeepingSchedulerService;
import org.ikasan.spec.management.ManagedService;
import org.ikasan.spec.module.Module;
import org.ikasan.spec.module.ModuleActivator;
import org.ikasan.spec.module.ModuleContainer;
import org.ikasan.spec.module.ModuleInitialisationService;
import org.ikasan.spec.monitor.FlowMonitor;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanDefinitionStoreException;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


/**
 * Module Initialisation Service default implementation
 *
 * @author Ikasan Development Team
 */
public class ModuleInitialisationServiceImpl
        implements ModuleInitialisationService, ApplicationContextAware, InitializingBean, DisposableBean
{
    /**
     * logger instance
     */
    private static final Logger logger = LoggerFactory.getLogger(ModuleInitialisationServiceImpl.class);

    /**
     * Runtime container for holding modules
     */
    private ModuleContainer moduleContainer;

    /**
     * module activation mechanism
     */
    private ModuleActivator moduleActivator;

    /**
     * platform level application context to be used to parent each of the module's contexts
     */
    private ApplicationContext platformContext;

    /**
     * HousekeepingSchedulerService provides access to starting off house keeping processes
     */
    private HousekeepingSchedulerService housekeepingSchedulerService;
   /**
     * HarvestingSchedulerService provides access to starting off harvesting processes
     */
    private HarvestingSchedulerService harvestingSchedulerService;

    /**
     * Constructor
     *
     * @param moduleContainer
     * @param moduleActivator
     * @param housekeepingSchedulerService
     * @param harvestingSchedulerService
     */
    public ModuleInitialisationServiceImpl(ModuleContainer moduleContainer, ModuleActivator moduleActivator,
        HousekeepingSchedulerService housekeepingSchedulerService,
        HarvestingSchedulerService harvestingSchedulerService)
    {
        super();
        this.moduleContainer = moduleContainer;
        if (moduleContainer == null)
        {
            throw new IllegalArgumentException("moduleContainer cannot be 'null'");
        }
        this.moduleActivator = moduleActivator;
        if (moduleActivator == null)
        {
            throw new IllegalArgumentException("moduleActivator cannot be 'null'");
        }
        this.housekeepingSchedulerService = housekeepingSchedulerService;
        if (housekeepingSchedulerService == null)
        {
            throw new IllegalArgumentException("housekeepingSchedulerService cannot be 'null'");
        }
        this.harvestingSchedulerService = harvestingSchedulerService;
        if (harvestingSchedulerService == null)
        {
            throw new IllegalArgumentException("harvestingSchedulerService cannot be 'null'");
        }
    }

    /*
     * (non-Javadoc)
     *
     * @seeorg.springframework.context.ApplicationContextAware#setApplicationContext(org.springframework.context.
     * ApplicationContext)
     */
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException
    {
        this.platformContext = applicationContext;
    }

    public void register(Module module)
    {
        initialise(module);
    }

    public void register(List<Module> modules)
    {
        for (Module<Flow> module : modules)
        {
            initialise(module);
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see org.springframework.beans.factory.InitializingBean#afterPropertiesSet()
     */
    @SuppressWarnings("unchecked") public void afterPropertiesSet() throws Exception
    {
        try
        {
            loadModuleFromContext(platformContext);

        }
        catch (BeanDefinitionStoreException e)
        {
            if (e.getMessage().contains("IOException parsing XML document from class path resource ["))
            {
                throw new MissingConfigFileException("Failed loading one of config files. See exception details.", e);
            }
            else if (e.getMessage().startsWith("Invalid bean definition with name ") && e.getMessage()
                .contains("Could not resolve placeholder"))
            {
                throw new MissingPropertiesException("Unable to resolve properties. See exception details.", e);
            }
            else if (e.getMessage().startsWith("Invalid bean definition with name. "))
            {
                throw new MissingBeanConfigurationException("Unable to configure bean. See exception details.", e);
            }
        }
    }

    private void loadModuleFromContext(ApplicationContext context)
    {
        // check for moduleActivator overrides and use the first one found
        try
        {
            Map<String, ModuleActivator> activators = context.getBeansOfType(ModuleActivator.class);
            if (activators != null && activators.size() > 0)
            {
                for (ModuleActivator activator : activators.values())
                {
                    this.moduleActivator = activator;
                    logger.info("Overridding default moduleActivator with [" + this.moduleActivator.getClass().getName()
                            + "]");
                    break;  // just use the first one we find
                }
            }
        }
        catch (NoSuchBeanDefinitionException e)
        {
            if (e.getMessage().startsWith("Invalid bean definition with name ") && e.getMessage()
                .contains("Could not resolve placeholder"))
            {
                throw new MissingPropertiesException("Unable to resolve properties. See exception details.", e);
            }
            else if (e.getMessage().startsWith("Invalid bean definition with name. "))
            {
                throw new MissingBeanConfigurationException("Unable to configure bean. See exception details.", e);
            }
        }
        // load all modules in this context
        // TODO - should multiple modules share the same application context ?
        Map<String, Module> moduleBeans = context.getBeansOfType(Module.class);
        if (!moduleBeans.isEmpty())
        {
            initialise(moduleBeans);
        }
    }

    private void initialise(Map<String, Module> moduleBeans)
    {
        for (Module<Flow> module : moduleBeans.values())
        {
            initialise(module);
        }
    }

    private void initialise(Module module)
    {
        try
        {
            this.moduleContainer.add(module);
            this.moduleActivator.activate(module);

            this.housekeepingSchedulerService.registerJobs();
            this.harvestingSchedulerService.registerJobs();
        }
        catch (RuntimeException re)
        {
            logger.error("There was a problem initialising module", re);
        }
    }

    /**
     * Callback from the container to gracefully stop flows and modules, and stop the inner loaded contexts
     */
    public void destroy() throws Exception
    {
        // shutdown all modules cleanly
        List<String> modulesToRemove = new ArrayList<>();
        for (Module<Flow> module : this.moduleContainer.getModules())
        {
            // stop flows
            this.moduleActivator.deactivate(module);

            // destroy
            for(Flow flow:module.getFlows())
            {
                // destroy any managed services used by the flow
                FlowConfiguration flowConfiguration = flow.getFlowConfiguration();
                List<ManagedService> managedServices = flowConfiguration.getManagedServices();
                for(ManagedService managedService:managedServices)
                {
                    managedService.destroy();
                }
            }

            modulesToRemove.add(module.getName());
        }

        // remove all modules from container
        for (String moduleToRemove : modulesToRemove)
        {
            moduleContainer.remove(moduleToRemove);
        }
        shutdownSchedulers(platformContext);
        shutdownMonitors(platformContext);
    }

    private void shutdownSchedulers(ApplicationContext context)
    {
        Map<String, Scheduler> schedulers = context.getBeansOfType(Scheduler.class);
        if (schedulers != null)
        {
            for (Map.Entry<String, Scheduler> entry : schedulers.entrySet())
            {
                logger.info("Shutting down Quartz scheduler with bean name: " + entry.getKey());
                try
                {
                    entry.getValue().shutdown();
                }
                catch (SchedulerException e)
                {
                    logger.warn("Exception shutting down Quartz scheduler. Will continue shutdown", e);
                }
            }
        }
        // We calling close on scheduler factory in order to force creation of new scheduler
        // when spring context is being reloaded
        SchedulerFactory.close();
    }

    private void shutdownMonitors(ApplicationContext context)
    {
        Map<String, FlowMonitor> monitors = context.getBeansOfType(FlowMonitor.class);
        if (monitors != null)
        {
            for (Map.Entry<String, FlowMonitor> entry : monitors.entrySet())
            {
                logger.info("Shutting down Monitor with bean name: " + entry.getKey());
                entry.getValue().destroy();
            }
        }
    }
}
