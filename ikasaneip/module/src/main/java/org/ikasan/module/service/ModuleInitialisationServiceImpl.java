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

import org.apache.log4j.Logger;
import org.ikasan.security.model.Authority;
import org.ikasan.security.service.UserService;
import org.ikasan.spec.flow.Flow;
import org.ikasan.spec.module.Module;
import org.ikasan.spec.module.ModuleActivator;
import org.ikasan.spec.module.ModuleContainer;
import org.ikasan.spec.module.ModuleInitialisationService;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Module Initialisation Service default implementation
 * 
 * @author Ikasan Development Team
 */
public class ModuleInitialisationServiceImpl implements ModuleInitialisationService, ApplicationContextAware,
        InitializingBean, DisposableBean
{
    /** logger instance */
    private final static Logger logger = Logger.getLogger(ModuleInitialisationServiceImpl.class);

    /** Runtime container for holding modules */
    private ModuleContainer moduleContainer;
    
    /** module activation mechanism */
    private ModuleActivator moduleActivator;

    /** loader configuration */
    private String loaderConfiguration;

    /**
     * platform level application context to be used to parent each of the module's contexts
     */
    private ApplicationContext platformContext;

    /** UserService provides access to users and authorities */
    private UserService userService;

    /** Container for Spring application contexts loaded internally by this class */
    private List<AbstractApplicationContext> innerContexts;

    /**
     * Constructor
     * @param moduleContainer
     * @param moduleActivator
     * @param userService
     */
    public ModuleInitialisationServiceImpl(ModuleContainer moduleContainer, ModuleActivator moduleActivator, UserService userService)
    {
        super();
        this.moduleContainer = moduleContainer;
        if(moduleContainer == null)
        {
            throw new IllegalArgumentException("moduleContainer cannot be 'null'");
        }

        this.moduleActivator = moduleActivator;
        if(moduleActivator == null)
        {
            throw new IllegalArgumentException("moduleActivator cannot be 'null'");
        }

        this.userService = userService;
        if(userService == null)
        {
            throw new IllegalArgumentException("userService cannot be 'null'");
        }
        innerContexts = new LinkedList<>();
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

    public void setLoaderConfiguration(String loaderConfiguration)
    {
        this.loaderConfiguration = loaderConfiguration;
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see org.springframework.beans.factory.InitializingBean#afterPropertiesSet()
     */
    @SuppressWarnings("unchecked")
    public void afterPropertiesSet() throws Exception
    {
        // Load the configurations defined by the loader conf and instantiate a context merged with the platform context
        ApplicationContext loaderContext = new ClassPathXmlApplicationContext(this.loaderConfiguration);
        Map<String,List> loaderResources = loaderContext.getBeansOfType(List.class);

        for(List<String> loaderResource : loaderResources.values())
        {
            String[] resourcesArray = new String[loaderResource.size()];
            loaderResource.toArray(resourcesArray);

            AbstractApplicationContext applicationContext = new ClassPathXmlApplicationContext(resourcesArray, platformContext);
            innerContexts.add(applicationContext);
    
            // check for moduleActivator overrides and use the first one found
            try
            {
                Map<String,ModuleActivator> activators = applicationContext.getBeansOfType(ModuleActivator.class);
                if(activators != null && activators.size() > 0)
                {
                    for(ModuleActivator activator : activators.values())
                    {
                        this.moduleActivator = activator;
                        logger.info("Overridding default moduleActivator with [" + this.moduleActivator.getClass().getName() + "]");
                        break;  // just use the first one we find
                    }
                }
            }
            catch(NoSuchBeanDefinitionException e)
            {
                // nothing of issue here, move on
            }
            
            // load all modules in this context
            // TODO - should multiple modules share the same application context ?
            Map<String, Module> moduleBeans = applicationContext.getBeansOfType(Module.class);
            for (Module<Flow> module : moduleBeans.values())
            {
                try {
                    this.initialiseModuleSecurity(module);
                    this.moduleContainer.add(module);
                    this.moduleActivator.activate(module);
                } catch (RuntimeException re){
                    logger.error("There was a problem initialising module", re);
                }
            }
        }
    }

    /**
     * Callback from the container to gracefully stop flows and modules, and stop the inner loaded contexts
     */
    public void destroy() throws Exception
    {
        // shutdown all modules cleanly
        for(Module<Flow> module:this.moduleContainer.getModules())
        {
            this.moduleActivator.deactivate(module);
        }

        // TODO - find a more generic way of managing this for platform resources
        shutdownSchedulers(platformContext);
        // close our inner loaded contexts
        for (AbstractApplicationContext context : innerContexts)
        {
            logger.debug("closing and destroying inner context: " + context.getDisplayName());
            shutdownSchedulers(context);
            context.close();
        }
        innerContexts.clear();
    }

    private void shutdownSchedulers(ApplicationContext context)
    {
        Map<String,Scheduler> schedulers = context.getBeansOfType(Scheduler.class);
        if(schedulers != null)
        {
            for(Map.Entry<String, Scheduler> entry : schedulers.entrySet())
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
    }

    /**
     * Creates the authorities for the module if they do not already exist
     * 
     * @param module - The module to secure
     */
    private void initialiseModuleSecurity(Module module)
    {
        List<Authority> existingAuthorities = this.userService.getAuthorities();
        Authority moduleUserAuthority = new Authority("USER_" + module.getName(), "Allows user access to the "
                + module.getName() + " module. This is typically assigned to business users");
        if (!existingAuthorities.contains(moduleUserAuthority))
        {
            logger.info("module user authority does not exist for module [" + module.getName() + "], creating...");
            this.userService.createAuthority(moduleUserAuthority);
        }
        Authority moduleAdminAuthority = new Authority("ADMIN_" + module.getName(),
            "Allows administrator access to the " + module.getName()
                    + " module. This is typically assigned to business administrators");
        if (!existingAuthorities.contains(moduleAdminAuthority))
        {
            logger.info("module admin authority does not exist for module [" + module.getName() + "], creating...");
            this.userService.createAuthority(moduleAdminAuthority);
        }
    }
}
