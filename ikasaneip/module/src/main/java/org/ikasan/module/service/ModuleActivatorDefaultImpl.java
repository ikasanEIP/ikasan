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

import org.ikasan.module.ConfiguredModuleConfiguration;
import org.ikasan.module.FlowFactoryCapable;
import org.ikasan.spec.configuration.ConfigurationService;
import org.ikasan.spec.configuration.ConfiguredResource;
import org.slf4j.Logger; import org.slf4j.LoggerFactory;
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
    private static final Logger logger = LoggerFactory.getLogger(ModuleActivatorDefaultImpl.class);

    /** handle to the configuration service */
    private ConfigurationService configurationService;

    /** startup flow control DAO */
    private StartupControlDao startupControlDao;

    /**
     * Constructor
     * @param startupControlDao
     */
    public ModuleActivatorDefaultImpl(ConfigurationService configurationService, StartupControlDao startupControlDao)
    {
        this.configurationService = configurationService;
        if(configurationService == null)
        {
            throw new IllegalArgumentException("configurationService cannot be 'null'");
        }

        this.startupControlDao = startupControlDao;
        if(startupControlDao == null)
        {
            throw new IllegalArgumentException("startupControlDao cannot be 'null'");
        }
    }

    /**
     * Provisions configuration for this module.
     *
     * @param module
     */
    public void provision(Module<Flow> module)
    {
        // load module configuration
        if(module instanceof ConfiguredResource)
        {
            ConfiguredResource<ConfiguredModuleConfiguration> configuredModule = (ConfiguredResource)module;
            configurationService.configure(configuredModule);

            if(module instanceof FlowFactoryCapable)
            {
                ConfiguredModuleConfiguration configuration = configuredModule.getConfiguration();
                if(configuration.getFlowNames() != null)
                {
                    for(String flowname:configuration.getFlowNames())
                    {
                        module.getFlows().add( ((FlowFactoryCapable)module).getFlowFactory().newInstance(module.getName(), flowname) );
                    }
                }
            }
        }
    }

    /* (non-Javadoc)
     * @see org.ikasan.module.service.ModuleActivation#activate(org.ikasan.spec.module.Module)
     */
    public void activate(Module<Flow> module)
    {
        // start flows
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

    /**
     * Deprovisions configuration of this module.
     * @param module
     */
    public void deprovision(Module<Flow> module)
    {
        // stop flows
        for(Flow flow:module.getFlows())
        {
            // stop flow and associated components
            flow.stop();
        }

        // remove any flows created from configuration as part of activation
        if(module instanceof ConfiguredResource)
        {
            ConfiguredResource<ConfiguredModuleConfiguration> configuredModule = (ConfiguredResource)module;
            if(module instanceof FlowFactoryCapable)
            {
                ConfiguredModuleConfiguration configuration = configuredModule.getConfiguration();
                if(configuration.getFlowNames() != null)
                {
                    for(String flowname:configuration.getFlowNames())
                    {
                        module.getFlows().remove(flowname);
                    }
                }
            }
        }
    }

    /* (non-Javadoc)
     * @see org.ikasan.module.service.ModuleActivation#deactivate(org.ikasan.spec.module.Module)
     */
    public void deactivate(Module<Flow> module)
    {
        // stop flows
        for(Flow flow:module.getFlows())
        {
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
