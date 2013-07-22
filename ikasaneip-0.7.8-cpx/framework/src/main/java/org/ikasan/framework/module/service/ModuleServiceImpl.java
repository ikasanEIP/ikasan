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

import org.ikasan.framework.flow.initiator.dao.InitiatorCommandDao;
import org.ikasan.framework.initiator.Initiator;
import org.ikasan.framework.initiator.InitiatorCommand;
import org.ikasan.framework.module.Module;
import org.ikasan.framework.module.container.ModuleContainer;

/**
 * Default implementation of <code>ModuleService</code>
 * 
 * @author Ikasan Development Team
 *
 */
public class ModuleServiceImpl implements ModuleService
{
    private InitiatorCommandDao initiatorCommandDao;
    
    
    /**
     * runtime conatiner for holding modules
     */
    private ModuleContainer moduleContainer; 
    
    /**
     * Flag determining whether or not we should keep historic InitiatorCommands
     */
    private boolean keepHistoricInitiatorCommands = false;


    /**
     * Constructor
     * 
     * @param moduleContainer
     */
    public ModuleServiceImpl(ModuleContainer moduleContainer, InitiatorCommandDao initiatorCommandDao)
    {
        super();
        this.moduleContainer=moduleContainer;
        this.initiatorCommandDao = initiatorCommandDao;

    }

    /* (non-Javadoc)
     * @see org.ikasan.framework.module.service.ModuleService#getModules()
     */
    public List<Module> getModules()
    {
        return moduleContainer.getModules();
    }

    /* (non-Javadoc)
     * @see org.ikasan.framework.module.service.ModuleService#getModule(java.lang.String)
     */
    public Module getModule(String moduleName)
    {
        //TODO throw exception if moduleName is erroneous
        return moduleContainer.getModule(moduleName);
    }
    
    /* (non-Javadoc)
     * @see org.ikasan.framework.module.service.ModuleService#stopInitiator(java.lang.String, java.lang.String, java.lang.String)
     */
    public void stopInitiator(String moduleName, String initiatorName, String actor){
        Initiator initiator = resolveInitiator(moduleName, initiatorName);
        
        //create and persist an instance of InitiatorCommand
        InitiatorCommand initiatorCommand = new InitiatorCommand(moduleName, initiatorName, "stop",actor);
        initiatorCommandDao.save(initiatorCommand,keepHistoricInitiatorCommands);
        
        //now stop the Initiator
        initiator.stop();
    }

    
    /* (non-Javadoc)
     * @see org.ikasan.framework.module.service.ModuleService#startInitiator(java.lang.String, java.lang.String, java.lang.String)
     */
    public void startInitiator(String moduleName, String initiatorName, String actor){
        Initiator initiator = resolveInitiator(moduleName, initiatorName);
        
        //create and persist an instance of InitiatorCommand
        InitiatorCommand initiatorCommand = new InitiatorCommand(moduleName, initiatorName, "start",actor);
        initiatorCommandDao.save(initiatorCommand, keepHistoricInitiatorCommands);
        
        //now start the Initiator
        initiator.start();
    }    
    
    private Initiator resolveInitiator(String moduleName, String initiatorName)
    {
        Module module = getModule(moduleName);
        if (module==null){
            throw new IllegalArgumentException("no such Module ["+moduleName+"]");
        }
        
        Initiator initiator = module.getInitiator(initiatorName);
        if (initiator==null){
            throw new IllegalArgumentException("no such Initiator ["+initiatorName+"] for Module ["+moduleName+"]");
        }
        return initiator;
    }
    
    
    
    


    



}
