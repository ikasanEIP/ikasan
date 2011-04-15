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
package org.ikasan.framework.module.service;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.log4j.Logger;
import org.ikasan.framework.flow.initiator.dao.InitiatorStartupControlDao;
import org.ikasan.framework.initiator.InitiatorStartupControl;
import org.ikasan.framework.initiator.InitiatorStartupControl.StartupType;
import org.ikasan.framework.module.Module;
import org.ikasan.framework.module.container.ModuleContainer;
import org.ikasan.framework.security.model.Authority;
import org.ikasan.framework.security.service.UserService;
import org.ikasan.spec.flow.Flow;
import org.springframework.beans.BeansException;
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
        InitializingBean
{
    /** logger instance */
    private final static Logger logger = Logger.getLogger(ModuleInitialisationServiceImpl.class);

    /** Runtime container for holding modules */
    private ModuleContainer moduleContainer;
    
    /**
     * Data Access object for retrieving any existing stop/start information
     */
    private InitiatorStartupControlDao initiatorStartupControlDao;

    /**
     * platform level application context to be used to parent each of the module's contexts
     */
    private ApplicationContext platformContext;

    /** UserService provides access to users and authorities */
    private UserService userService;

    /**
     * Constructor
     * 
     * @param moduleContainer - The pre built module container
     * @param userService - The user service
     * @param initiatorStartupControlDao - DAO for supplying InitiatorStartupControl instances
     */
    public ModuleInitialisationServiceImpl(ModuleContainer moduleContainer, UserService userService, InitiatorStartupControlDao initiatorStartupControlDao)
    {
        super();
        this.moduleContainer = moduleContainer;
        this.userService = userService;
        this.initiatorStartupControlDao = initiatorStartupControlDao;
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

    /*
     * (non-Javadoc)
     * 
     * @see org.springframework.beans.factory.InitializingBean#afterPropertiesSet()
     */
    @SuppressWarnings("unchecked")
    public void afterPropertiesSet() throws Exception
    {
        // Get a listing of all the module loader files that may contain loadable modules
        ApplicationContext moduleLoadingContext = new ClassPathXmlApplicationContext("moduleLoading.xml");
        List<String> moduleLoaderFiles = (List<String>) moduleLoadingContext.getBean("moduleLoader-config-files");
        // For each module loader file that needs to be loaded
        for (String moduleLoaderFile : moduleLoaderFiles)
        {
            loadModule(moduleLoaderFile);
        }
    }

    /**
     * Load the module
     * 
     * @param moduleLoaderFile - The module loader file
     */
    @SuppressWarnings("unchecked")
    private void loadModule(String moduleLoaderFile)
    {
        logger.info("loading module from file["+moduleLoaderFile+"]");
        ApplicationContext applicationContext = new ClassPathXmlApplicationContext(new String[] { moduleLoaderFile },
            this.platformContext);
        Map<String, Module> moduleBeans = applicationContext.getBeansOfType(Module.class);
        for (Module module : moduleBeans.values())
        {
            this.initialiseModuleSecurity(module);
            
            //start the module's flow if configured to
            for (Entry<String, Flow> flowEntry: module.getFlows().entrySet())
            {
                InitiatorStartupControl initiatorStartupControl = this.initiatorStartupControlDao.getInitiatorStartupControl(module.getName(), flowEntry.getKey());
                if (StartupType.AUTOMATIC.equals(initiatorStartupControl.getStartupType()))
                {
                    flowEntry.getValue().start();
                }
            }
            
            this.moduleContainer.add(module);
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
