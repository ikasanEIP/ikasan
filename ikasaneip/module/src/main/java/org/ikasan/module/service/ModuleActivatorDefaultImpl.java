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
import org.ikasan.module.startup.dao.StartupControlDao;
import org.ikasan.spec.flow.Flow;
import org.ikasan.spec.flow.FlowConfiguration;
import org.ikasan.spec.management.ManagedService;
import org.ikasan.spec.module.Module;
import org.ikasan.spec.module.ModuleActivator;
import org.ikasan.spec.module.StartupControl;
import org.ikasan.spec.module.StartupType;

import java.util.List;

/**
 * Simple implementation of the default activation of a module.
 * @author Ikasan Development Team
 *
 */
public class ModuleActivatorDefaultImpl implements ModuleActivator<Flow>
{
    /** logger instance */
    private final static Logger logger = Logger.getLogger(ModuleActivatorDefaultImpl.class);

    /** startup flow control DAO */
    private StartupControlDao startupControlDao;

    /**
     * Constructor
     * @param startupControlDao
     */
    public ModuleActivatorDefaultImpl(StartupControlDao startupControlDao)
    {
        this.startupControlDao = startupControlDao;
        if(startupControlDao == null)
        {
            throw new IllegalArgumentException("startupControlDao cannot be 'null'");
        }
    }
    
    /* (non-Javadoc)
     * @see org.ikasan.module.service.ModuleActivation#activate(org.ikasan.spec.module.Module)
     */
    public void activate(Module<Flow> module)
    {
        for(Flow flow:module.getFlows())
        {
            StartupControl flowStartupControl = this.startupControlDao.getStartupControl(module.getName(), flow.getName());
            if(StartupType.AUTOMATIC.equals(flowStartupControl.getStartupType()))
            {
                try
                {
                    flow.start();
                }
                catch(RuntimeException e)
                {
                    logger.warn("Module [" + module.getName() + "] Flow ["+ flow.getName() + "] failed to start!", e);
                }
            }
            else 
            {
                logger.info("Module [" + module.getName() + "] Flow ["+ flow.getName() + "] startup is set to [" + flowStartupControl.getStartupType().name() + "]. Not automatically started!");
            }
        }
    }

    /* (non-Javadoc)
     * @see org.ikasan.module.service.ModuleActivation#deactivate(org.ikasan.spec.module.Module)
     */
    public void deactivate(Module<Flow> module)
    {
        for(Flow flow:module.getFlows())
        {
            // stop flow and associated components
            flow.stop();

            // destroy any managed services used by the flow
            FlowConfiguration flowConfiguration = flow.getFlowConfiguration();
            List<ManagedService> managedServices = flowConfiguration.getManagedServices();
            for(ManagedService managedService:managedServices)
            {
                managedService.destroy();
            }
        }

    }
}
