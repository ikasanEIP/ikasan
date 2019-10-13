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
import org.ikasan.spec.module.StartupControl;
import org.ikasan.spec.module.StartupControlService;
import org.ikasan.spec.module.StartupType;
import org.ikasan.systemevent.service.SystemEventService;

/**
 * 
 * @author Ikasan Development Team
 *
 */
public class StartupControlServiceImpl implements StartupControlService
{

	/**
     * service to log significant system happenings
     */
    private SystemEventService systemEventService;

    /**
     * constant for logging a request to change initiator startup type
     */
	public static final String INITIATOR_SET_STARTUP_TYPE_EVENT_ACTION = "Flow StartupType set to: ";

    /**
     * Logger instance
     */
    private static Logger logger = LoggerFactory.getLogger(StartupControlServiceImpl.class);
  

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
    public StartupControlServiceImpl(SystemEventService systemEventService, StartupControlDao startupControlDao)
    {
        super();

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

}
