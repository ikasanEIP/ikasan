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

import org.slf4j.Logger; import org.slf4j.LoggerFactory;
import org.ikasan.module.startup.StartupControlImpl;
import org.ikasan.module.startup.dao.StartupControlDao;
import org.ikasan.spec.flow.Flow;
import org.ikasan.spec.module.*;
import org.ikasan.systemevent.service.SystemEventService;

import java.util.List;

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
     * constant for logging an incoming initiator start request
     */
    public static final String INITIATOR_START_REQUEST_SYSTEM_EVENT_ACTION = "Flow start requested";
    /**
     * constant for logging an incoming initiator pause request
     */
    public static final String INITIATOR_START_PAUSE_REQUEST_SYSTEM_EVENT_ACTION = "Flow start/pause requested";
    /**
     * constant for logging an incoming initiator pause request
     */
    public static final String INITIATOR_PAUSE_REQUEST_SYSTEM_EVENT_ACTION = "Flow pause requested";
    /**
     * constant for logging an incoming initiator resume request
     */
    public static final String INITIATOR_RESUME_REQUEST_SYSTEM_EVENT_ACTION = "Flow resume requested";
    /**
     * constant for logging an incoming initiator stop request
     */
	public static final String INITIATOR_STOP_REQUEST_SYSTEM_EVENT_ACTION = "Flow stop requested";
    /**
     * constant for logging a request to change initiator startup type
     */
	public static final String INITIATOR_SET_STARTUP_TYPE_EVENT_ACTION = "Flow StartupType set to: ";

    public static final String STOP_CONTEXT_LISTENERS_ACTION = "Flow context listeners stop requested";

    public static final String START_CONTEXT_LISTENERS_ACTION = "Flow context listeners start requested";

    /**
     * Logger instance
     */
    private static Logger logger = LoggerFactory.getLogger(ModuleServiceImpl.class);
  
    /**
     * runtime container for holding modules
     */
    private ModuleContainer moduleContainer; 

    /**
     * runtime container for holding modules
     */
    private StartupControlDao startupControlDao;

    /**
     * Constructor
     * 
     * @param moduleContainer
     * @param systemEventService 
     */
    public ModuleServiceImpl(ModuleContainer moduleContainer, SystemEventService systemEventService, StartupControlDao startupControlDao)
    {
        super();
        this.moduleContainer = moduleContainer;
        if(moduleContainer == null)
        {
            throw new IllegalArgumentException("moduleContainer cannot be 'null'");
        }

        this.systemEventService = systemEventService;
        if(systemEventService == null)
        {
            throw new IllegalArgumentException("systemEventService cannot be 'null'");
        }

        this.startupControlDao = startupControlDao;
        if(startupControlDao == null)
        {
            throw new IllegalArgumentException("startupControlDao cannot be 'null'");
        }
    }

    /* (non-Javadoc)
     * @see org.ikasan.framework.module.service.ModuleService#getModules()
     */
    public List<Module> getModules()
    {
        return this.moduleContainer.getModules();
    }

    /* (non-Javadoc)
     * @see org.ikasan.framework.module.service.ModuleService#getModule(java.lang.String)
     */
    public Module getModule(String moduleName)
    {
        //TODO throw exception if moduleName is erroneous
        return this.moduleContainer.getModule(moduleName);
    }

    /* (non-Javadoc)
     * @see org.ikasan.framework.module.service.ModuleService#stopInitiator(java.lang.String, java.lang.String, java.lang.String)
     */
    public void stopFlow(String moduleName, String flowName, String actor)
    {
        //log the request
        this.systemEventService.logSystemEvent(moduleName+"."+flowName, INITIATOR_STOP_REQUEST_SYSTEM_EVENT_ACTION,  actor);
    	this.logger.info("stopFlow : " + moduleName + "." + flowName + " requested by [" + actor + "]");
        Flow flow = this.resolveFlow(moduleName, flowName);
        if(flow == null)
        {
            // TODO - throw exception ?
            logger.error("flow name[" + flowName + "] not found in module [" + moduleName + "]");
        }
        else
        {
            flow.stop();
        }
    }

    /* (non-Javadoc)
     * @see org.ikasan.framework.module.service.ModuleService#stopInitiator(java.lang.String, java.lang.String, java.lang.String)
     */
    public void pauseFlow(String moduleName, String flowName, String actor)
    {
        //log the request
        this.systemEventService.logSystemEvent(moduleName+"."+flowName, INITIATOR_PAUSE_REQUEST_SYSTEM_EVENT_ACTION,  actor);
        this.logger.info("pauseFlow : " + moduleName + "." + flowName + " requested by [" + actor + "]");
        Flow flow = this.resolveFlow(moduleName, flowName);
        if(flow == null)
        {
            // TODO - throw exception ?
            logger.error("flow name[" + flowName + "] not found in module [" + moduleName + "]");
        }
        else
        {
            flow.pause();
        }
    }

    /* (non-Javadoc)
     * @see org.ikasan.framework.module.service.ModuleService#stopInitiator(java.lang.String, java.lang.String, java.lang.String)
     */
    public void startPauseFlow(String moduleName, String flowName, String actor)
    {
        //log the request
        this.systemEventService.logSystemEvent(moduleName+"."+flowName, INITIATOR_START_PAUSE_REQUEST_SYSTEM_EVENT_ACTION,  actor);
        this.logger.info("startPauseFlow : " + moduleName + "." + flowName + " requested by [" + actor + "]");
        Flow flow = this.resolveFlow(moduleName, flowName);
        if(flow == null)
        {
            // TODO - throw exception ?
            logger.error("flow name[" + flowName + "] not found in module [" + moduleName + "]");
        }
        else
        {
            flow.startPause();
        }
    }

    /* (non-Javadoc)
     * @see org.ikasan.framework.module.service.ModuleService#stopInitiator(java.lang.String, java.lang.String, java.lang.String)
     */
    public void resumeFlow(String moduleName, String flowName, String actor)
    {
        //log the request
        this.systemEventService.logSystemEvent(moduleName+"."+flowName, INITIATOR_RESUME_REQUEST_SYSTEM_EVENT_ACTION,  actor);
        this.logger.info("resumeFlow : " + moduleName + "." + flowName + " requested by [" + actor + "]");
        Flow flow = this.resolveFlow(moduleName, flowName);
        if(flow == null)
        {
            // TODO - throw exception ?
            logger.error("flow name[" + flowName + "] not found in module [" + moduleName + "]");
        }
        else
        {
        	StartupControl flowStartupControl = this.startupControlDao.getStartupControl(moduleName, flowName);
            if(StartupType.DISABLED.equals(flowStartupControl.getStartupType()))
            {
                throw new IllegalStateException("flow [" + flowName + "] module [" 
                    + moduleName + "] is disabled so cannot be resumed.");
            }
            
            flow.resume();
        }
    }

    /* (non-Javadoc)
     * @see org.ikasan.framework.module.service.ModuleService#startInitiator(java.lang.String, java.lang.String, java.lang.String)
     */
    public void startFlow(String moduleName, String flowName, String actor)
    {
    	//log the request
        this.systemEventService.logSystemEvent(moduleName+"."+flowName, INITIATOR_START_REQUEST_SYSTEM_EVENT_ACTION,  actor);
    	this.logger.info("startFlow : " + moduleName + "." + flowName + " requested by [" + actor + "]");
    	Flow flow = this.resolveFlow(moduleName, flowName);
    	if(flow == null)
    	{
    	    // TODO - throw exception ?
    	    logger.error("flow name[" + flowName + "] not found in module [" + moduleName + "]");
    	}
    	else
    	{
            StartupControl flowStartupControl = this.startupControlDao.getStartupControl(moduleName, flowName);
            if(StartupType.DISABLED.equals(flowStartupControl.getStartupType()))
            {
                throw new IllegalStateException("flow [" + flowName + "] module [" 
                    + moduleName + "] is disabled so cannot be started.");
            }
            flow.start();
    	}
    }

    private Flow resolveFlow(String moduleName, String flowName)
    {
        Module<Flow> module = this.getModule(moduleName);
        if (module == null)
        {
            throw new IllegalArgumentException("no such Module ["+moduleName+"]");
        }

        return module.getFlow(flowName);
    }

    /* (non-Javadoc)
     * @see org.ikasan.spec.module.ModuleService#getStartupType(java.lang.String, java.lang.String)
     */
    public StartupControl getStartupControl(String moduleName, String flowName)
    {
        StartupControl startupControl = this.startupControlDao.getStartupControl(moduleName, flowName);
        if(startupControl != null)
        {
            return startupControl;
        }
        
        return new StartupControlImpl(moduleName, flowName);
    }

    /* (non-Javadoc)
     * @see org.ikasan.spec.module.ModuleService#setStartupType(java.lang.String, java.lang.String, org.ikasan.spec.module.StartupType, java.lang.String, java.lang.String)
     */
    public void setStartupType(String moduleName, String flowName, StartupType startupType, String comment, String actor)
    {
        StartupControl startupControl = this.startupControlDao.getStartupControl(moduleName, flowName);
        if(startupControl == null)
        {
            startupControl = new StartupControlImpl(moduleName, flowName);
        }
        startupControl.setStartupType(startupType);
        startupControl.setComment(comment);
        this.startupControlDao.save(startupControl);
        
        this.systemEventService.logSystemEvent(moduleName+"."+flowName, INITIATOR_SET_STARTUP_TYPE_EVENT_ACTION + startupControl.getStartupType().name(),  actor);
    }

    @Override
    public void stopContextListeners(String moduleName, String flowName, String actor)
    {
        //log the request
        this.systemEventService.logSystemEvent(moduleName+"."+flowName, STOP_CONTEXT_LISTENERS_ACTION,  actor);
        this.logger.info("stopContextListeners : " + moduleName + "." + flowName + " requested by [" + actor + "]");
        Flow flow = this.resolveFlow(moduleName, flowName);
        if(flow == null)
        {
            // TODO - throw exception ?
            logger.error("flow name[" + flowName + "] not found in module [" + moduleName + "]");
        }
        else
        {
            flow.stopContextListeners();
        }
    }

    @Override
    public void startContextListeners(String moduleName, String flowName, String actor)
    {
        //log the request
        this.systemEventService.logSystemEvent(moduleName+"."+flowName, START_CONTEXT_LISTENERS_ACTION,  actor);
        this.logger.info("startContextListeners : " + moduleName + "." + flowName + " requested by [" + actor + "]");
        Flow flow = this.resolveFlow(moduleName, flowName);
        if(flow == null)
        {
            // TODO - throw exception ?
            logger.error("flow name[" + flowName + "] not found in module [" + moduleName + "]");
        }
        else
        {
            flow.startContextListeners();
        }
    }
}
