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
package org.ikasan.monitor;

import org.slf4j.Logger; import org.slf4j.LoggerFactory;
import org.ikasan.spec.configuration.ConfiguredResource;
import org.ikasan.spec.monitor.Monitor;
import org.ikasan.spec.management.ManagedService;
import org.ikasan.spec.monitor.Notifier;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;

/**
 * Ikasan default monitor implementation.
 *
 * @author Ikasan Development Team
 */
public class DefaultMonitorImpl<T> implements Monitor<T>, ConfiguredResource<MonitorConfiguration>
{
    /** logger instance */
    private static Logger logger = LoggerFactory.getLogger(DefaultMonitorImpl.class);

    // executor service for thread dispatching
    private final ExecutorService executorService;

    /** configuration instance for this monitor */
    private MonitorConfiguration configuration;

    /** configured resource identifier */
    private String configuredResourceId;

    /** environment label */
    private String environment;

    /** module name */
    private String moduleName;
    
    /** flow name */
    private String flowName;

    /** has the state changed */
    private Map<String,T> states = new ConcurrentHashMap<>();

    /** list of notifiers to be informed */
    protected List<Notifier> notifiers = new ArrayList<>();

    /**
     * Constructor
     * @param executorService
     */
    public DefaultMonitorImpl(ExecutorService executorService)
    {
        this.executorService = executorService;
        if(executorService == null)
        {
            throw new IllegalArgumentException("executorService cannot be 'null'");
        }
    }

    @Override
    public String getEnvironment() {
        return environment;
    }

    @Override
    public void setEnvironment(String environment)
    {
        this.environment = environment;
    }

    @Override
    public void invoke(final T status)
    {
        if(configuration.isActive())
        {
            if(this.notifiers == null || this.notifiers.size() == 0)
            {
                logger.warn("Monitor [" + configuration.getMonitorName() + "] has no registered notifiers");
                return;
            }

            if (executorService == null || executorService.isShutdown())
            {
                logger.warn("Cannot invoke Monitor after destroy has been called - executorService is null or shutdown");
                return;
            }
            
            String monitorName = "Module[" + moduleName + "] Flow[" + flowName + "]";
            		
            boolean stateChanged = hasStateChanged(environment + monitorName, status);

            for(final Notifier notifier:notifiers)
            {
                if( !notifier.isNotifyStateChangesOnly() || (notifier.isNotifyStateChangesOnly() && stateChanged) )
                {
                    executorService.execute(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            try
                            {
                                notifier.invoke(environment, moduleName, flowName, status);
                            }
                            catch(RuntimeException e)
                            {
                                logger.warn("Failed to invoke notifier[" + notifier.getClass().getName() + "]", e);
                            }
                            }
                    });
                }
            }
        }

    }

    /**
     * Has the reported state for this key changed since the last reported state
     * @param stateKey
     * @param status
     * @return
     */
    protected boolean hasStateChanged(String stateKey, T status)
    {
        T lastState = states.get(stateKey);
        if(lastState == null || !(lastState.equals(status)) )
        {
            states.put(stateKey, status);
            return true;
        }

        return false;
    }

    @Override
    public void destroy()
    {
        if (executorService != null)
        {
            logger.info("Monitor shutting down executorService");
            executorService.shutdown();
        }
    }

    @Override
    public void setNotifiers(List<Notifier> notifiers)
    {
        this.notifiers = notifiers;
    }

    @Override
    public List<Notifier> getNotifiers()
    {
        return this.notifiers;
    }

    @Override
    public MonitorConfiguration getConfiguration()
    {
        return configuration;
    }

    @Override
    public void setConfiguration(MonitorConfiguration configuration)
    {
        this.configuration = configuration;
    }

    @Override
    public String getConfiguredResourceId()
    {
        return this.configuredResourceId;
    }

    @Override
    public void setConfiguredResourceId(String configuredResourceId)
    {
        this.configuredResourceId = configuredResourceId;
    }

	/**
	 * @return the moduleName
	 */
	public String getModuleName()
	{
		return moduleName;
	}

	/**
	 * @param moduleName the moduleName to set
	 */
	public void setModuleName(String moduleName)
	{
		this.moduleName = moduleName;
	}

	/**
	 * @return the flowName
	 */
	public String getFlowName()
	{
		return flowName;
	}

	/**
	 * @param flowName the flowName to set
	 */
	public void setFlowName(String flowName)
	{
		this.flowName = flowName;
	}
}
