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

import org.ikasan.framework.flow.initiator.dao.InitiatorStartupControlDao;
import org.ikasan.framework.initiator.Initiator;
import org.ikasan.framework.initiator.InitiatorStartupControl;
import org.ikasan.framework.initiator.InitiatorStartupControl.StartupType;
import org.ikasan.framework.module.Module;
import org.ikasan.framework.module.container.ModuleContainer;
import org.ikasan.framework.systemevent.service.SystemEventService;

/**
 * Default implementation of <code>ModuleService</code>
 * 
 * @author Ikasan Development Team
 *
 */
public class ModuleServiceImpl implements ModuleService
{
 
    /**
     * service to log significant system happenings
     */
    private SystemEventService systemEventService;
    
    /**
     * data access object for initiator startup control info
     */
    private InitiatorStartupControlDao initiatorStartupControlDao;
    
    /**
     * constant for logging an incomming initiator start request
     */
    public static final String INITIATOR_START_REQUEST_SYSTEM_EVENT_ACTION = "Initiator start requested";
    /**
     * constant for logging an incomming initiator stop request
     */
	public static final String INITIATOR_STOP_REQUEST_SYSTEM_EVENT_ACTION = "Initiator stop requested";
    /**
     * constant for logging a request to change initiator startup type
     */
	public static final String INITIATOR_SET_STARTUP_TYPE_EVENT_ACTION = "Initiator StartupType set to: ";

    
    
    /**
     * runtime conatiner for holding modules
     */
    private ModuleContainer moduleContainer; 
    



    /**
     * Constructor
     * 
     * @param moduleContainer
     */
    public ModuleServiceImpl(ModuleContainer moduleContainer, InitiatorStartupControlDao initiatorStartupControlDao, SystemEventService systemEventService)
    {
        super();
        this.moduleContainer=moduleContainer;
        this.initiatorStartupControlDao = initiatorStartupControlDao;
        this.systemEventService = systemEventService;

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
        
        //log the request
        systemEventService.logSystemEvent(moduleName+"."+initiatorName, INITIATOR_STOP_REQUEST_SYSTEM_EVENT_ACTION,  actor);
        
        //now stop the Initiator
        initiator.stop();
    }

    
    /* (non-Javadoc)
     * @see org.ikasan.framework.module.service.ModuleService#startInitiator(java.lang.String, java.lang.String, java.lang.String)
     */
    public void startInitiator(String moduleName, String initiatorName, String actor){
        Initiator initiator = resolveInitiator(moduleName, initiatorName);
        
        //check if its not disabled
        InitiatorStartupControl initiatorStartupControl = initiatorStartupControlDao.getInitiatorStartupControl(moduleName, initiatorName);
        if (initiatorStartupControl.isDisabled()){
        	throw new IllegalStateException("Cannot start a disabled Initiator");
        }
        
        
        
        //log the request
        systemEventService.logSystemEvent(moduleName+"."+initiatorName, INITIATOR_START_REQUEST_SYSTEM_EVENT_ACTION,  actor);
        
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


	/* (non-Javadoc)
	 * @see org.ikasan.framework.module.service.ModuleService#updateInitiatorStartupType(java.lang.String, java.lang.String, org.ikasan.framework.initiator.InitiatorStartupControl.StartupType, java.lang.String, java.lang.String)
	 */
	public void updateInitiatorStartupType(String moduleName,
			String initiatorName, StartupType startupType, String comment,
			String actor) {
        //log the request
        systemEventService.logSystemEvent(moduleName+"."+initiatorName, INITIATOR_SET_STARTUP_TYPE_EVENT_ACTION+startupType.toString(), actor);

        InitiatorStartupControl initiatorStartupControl = initiatorStartupControlDao.getInitiatorStartupControl(moduleName, initiatorName);
		initiatorStartupControl.setStartupType(startupType);
		initiatorStartupControl.setComment(comment);
		
		//save the control
		initiatorStartupControlDao.save(initiatorStartupControl);
	}

	/* (non-Javadoc)
	 * @see org.ikasan.framework.module.service.ModuleService#getInitiatorStartupControl(java.lang.String, java.lang.String)
	 */
	public InitiatorStartupControl getInitiatorStartupControl(String moduleName, String initiatorName) {
		return initiatorStartupControlDao.getInitiatorStartupControl(moduleName, initiatorName);
	}
    
    
    
    


    



}
