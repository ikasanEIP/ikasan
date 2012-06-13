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

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.log4j.Logger;
import org.ikasan.spec.flow.Flow;
import org.ikasan.spec.module.Module;
import org.ikasan.spec.module.ModuleContainer;
import org.ikasan.spec.module.ModuleInitialisationService;
import org.quartz.Scheduler;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.support.ClassPathXmlApplicationContext;

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
    
    /** loader configuration */
    private String loaderConfiguration;

    /**
     * Data Access object for retrieving any existing stop/start information
     */
//    private StartupControlDao startupControlDao;

    /**
     * platform level application context to be used to parent each of the module's contexts
     */
    private ApplicationContext platformContext;

    /** UserService provides access to users and authorities */
//    private UserService userService;

    /**
     * Constructor
     * 
     * @param moduleContainer - The pre built module container
     * @param userService - The user service
     * @param initiatorStartupControlDao - DAO for supplying InitiatorStartupControl instances
     */
//    public ModuleInitialisationServiceImpl(ModuleContainer moduleContainer, UserService userService, StartupControlDao startupControlDao)
    public ModuleInitialisationServiceImpl(ModuleContainer moduleContainer)
    {
        super();
        this.moduleContainer = moduleContainer;
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
        List<String> loaderResources = loaderContext.getBean(List.class);
        String[] loaderResourcesArray = new String[loaderResources.size()];
        loaderResources.toArray(loaderResourcesArray);
        ApplicationContext applicationContext = new ClassPathXmlApplicationContext(loaderResourcesArray, platformContext);

        // load all modules in this context
        // TODO - should multiple modules share the same application context ?
        Map<String, Module> moduleBeans = applicationContext.getBeansOfType(Module.class);
        for (Module<Flow> module : moduleBeans.values())
        {
            for(Flow flow:module.getFlows())
            {
                flow.start();
            }

            this.moduleContainer.add(module);
        }
        
    }

    /**
     * Callback fom the container to gracefully stop flows and modules.
     */
    public void destroy() throws Exception
    {
        // shutdown all modules
        for(Module<Flow> module:this.moduleContainer.getModules())
        {
            // TODO - do we need to reverse the order of shudown?
            for(Flow flow:module.getFlows())
            {
                flow.stop();
            }
        }

        // TODO - find a more generic way of managing this for platform resources
        Scheduler scheduler = this.platformContext.getBean(Scheduler.class);
        if(scheduler != null)
        {
            scheduler.shutdown();
        }
    }

//    /**
//     * Load the module
//     * 
//     * @param moduleLoaderFile - The module loader file
//     */
//    @SuppressWarnings("unchecked")
//    private void loadModule(String moduleLoaderFile)
//    {
//        logger.info("loading module from file["+moduleLoaderFile+"]");
//        ApplicationContext applicationContext = new ClassPathXmlApplicationContext(new String[] { moduleLoaderFile },
//            this.platformContext);
//        Map<String, Module> moduleBeans = applicationContext.getBeansOfType(Module.class);
//        for (Module<Flow> module : moduleBeans.values())
//        {
////            this.initialiseModuleSecurity(module);
//            
//            //start the module's flow if configured to
//            for (Flow flow: module.getFlows())
//            {
////                InitiatorStartupControl initiatorStartupControl = this.initiatorStartupControlDao.getInitiatorStartupControl(module.getName(), flowEntry.getKey());
////                if (StartupType.AUTOMATIC.equals(startupControl.getStartupType()))
////                {
////                    flowEntry.getValue().start();
////                }
//                flow.start();
//            }
//            
//        }
//    }

//    /**
//     * Creates the authorities for the module if they do not already exist
//     * 
//     * @param module - The module to secure
//     */
//    private void initialiseModuleSecurity(Module module)
//    {
//        List<Authority> existingAuthorities = this.userService.getAuthorities();
//        Authority moduleUserAuthority = new Authority("USER_" + module.getName(), "Allows user access to the "
//                + module.getName() + " module. This is typically assigned to business users");
//        if (!existingAuthorities.contains(moduleUserAuthority))
//        {
//            logger.info("module user authority does not exist for module [" + module.getName() + "], creating...");
//            this.userService.createAuthority(moduleUserAuthority);
//        }
//        Authority moduleAdminAuthority = new Authority("ADMIN_" + module.getName(),
//            "Allows administrator access to the " + module.getName()
//                    + " module. This is typically assigned to business administrators");
//        if (!existingAuthorities.contains(moduleAdminAuthority))
//        {
//            logger.info("module admin authority does not exist for module [" + module.getName() + "], creating...");
//            this.userService.createAuthority(moduleAdminAuthority);
//        }
//    }
}
