/* 
 * $Id$
 * $URL$
 *
 * ====================================================================
 * Ikasan Enterprise Integration Platform
 * Copyright (c) 2003-2008 Mizuho International plc. and individual contributors as indicated
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the 
 * Free Software Foundation Europe e.V. Talstrasse 110, 40217 Dusseldorf, Germany 
 * or see the FSF site: http://www.fsfeurope.org/.
 * ====================================================================
 */
package org.ikasan.framework.module.service;

import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.ikasan.framework.flow.initiator.dao.InitiatorStartupControlDao;
import org.ikasan.framework.initiator.Initiator;
import org.ikasan.framework.initiator.InitiatorStartupControl;
import org.ikasan.framework.initiator.InitiatorStartupControl.StartupType;
import org.ikasan.framework.module.Module;
import org.ikasan.framework.module.container.ModuleContainer;
import org.ikasan.framework.security.model.Authority;
import org.ikasan.framework.security.service.UserService;
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
    private Logger logger = Logger.getLogger(ModuleInitialisationServiceImpl.class);

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
            platformContext);
        Map<String, Module> moduleBeans = applicationContext.getBeansOfType(Module.class);
        for (Module module : moduleBeans.values())
        {
            initialiseModuleSecurity(module);
            
            //start the module's initiators if configured to
            for (Initiator initiator : module.getInitiators()){
                InitiatorStartupControl initiatorStartupControl = initiatorStartupControlDao.getInitiatorStartupControl(module.getName(), initiator.getName());
                if (StartupType.AUTOMATIC.equals(initiatorStartupControl.getStartupType())){
                    initiator.start();
                }
            }
            
            moduleContainer.add(module);
        }
    }

    /**
     * Creates the authorities for the module if they do not already exist
     * 
     * @param module - The module to secure
     */
    private void initialiseModuleSecurity(Module module)
    {
        List<Authority> existingAuthorities = userService.getAuthorities();
        Authority moduleUserAuthority = new Authority("USER_" + module.getName(), "Allows user access to the "
                + module.getName() + " module. This is typically assigned to business users");
        if (!existingAuthorities.contains(moduleUserAuthority))
        {
            logger.info("module user authority does not exist for module [" + module.getName() + "], creating...");
            userService.createAuthority(moduleUserAuthority);
        }
        Authority moduleAdminAuthority = new Authority("ADMIN_" + module.getName(),
            "Allows administrator access to the " + module.getName()
                    + " module. This is typically assigned to business administrators");
        if (!existingAuthorities.contains(moduleAdminAuthority))
        {
            logger.info("module admin authority does not exist for module [" + module.getName() + "], creating...");
            userService.createAuthority(moduleAdminAuthority);
        }
    }
}
