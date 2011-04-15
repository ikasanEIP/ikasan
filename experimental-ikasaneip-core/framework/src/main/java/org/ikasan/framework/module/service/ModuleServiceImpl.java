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

import org.apache.log4j.Logger;
import org.ikasan.framework.module.Module;
import org.ikasan.framework.module.container.ModuleContainer;
import org.ikasan.framework.systemevent.service.SystemEventService;
import org.ikasan.spec.flow.Flow;

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
     * Logger instance
     */
    private Logger logger = Logger.getLogger(ModuleServiceImpl.class);
  
    
    /**
     * runtime conatiner for holding modules
     */
    private ModuleContainer moduleContainer; 

    /**
     * Constructor
     * 
     * @param moduleContainer
     * @param systemEventService 
     */
    public ModuleServiceImpl(ModuleContainer moduleContainer, SystemEventService systemEventService)
    {
        super();
        this.moduleContainer = moduleContainer;
        this.systemEventService = systemEventService;

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
    	this.logger.info("stopInitiator : "+moduleName+"."+flowName+" requested by ["+actor+"]");
        Flow flow = this.resolveInitiator(moduleName, flowName);
        
        //log the request
        this.systemEventService.logSystemEvent(moduleName+"."+flowName, INITIATOR_STOP_REQUEST_SYSTEM_EVENT_ACTION,  actor);
        
        //now stop the Initiator
        flow.stop();
    }

    
    /* (non-Javadoc)
     * @see org.ikasan.framework.module.service.ModuleService#startInitiator(java.lang.String, java.lang.String, java.lang.String)
     */
    public void startFlow(String moduleName, String flowName, String actor)
    {
    	this.logger.info("startInitiator : "+moduleName+"."+flowName+" requested by ["+actor+"]");
    	Flow flow = this.resolveInitiator(moduleName, flowName);

        //log the request
        this.systemEventService.logSystemEvent(moduleName+"."+flowName, INITIATOR_START_REQUEST_SYSTEM_EVENT_ACTION,  actor);

        //now start the Initiator
        flow.start();
    }

    private Flow resolveInitiator(String moduleName, String flowName)
    {
        Module module = this.getModule(moduleName);
        if (module == null)
        {
            throw new IllegalArgumentException("no such Module ["+moduleName+"]");
        }

        return  module.getFlows().get(flowName);
    }

}
