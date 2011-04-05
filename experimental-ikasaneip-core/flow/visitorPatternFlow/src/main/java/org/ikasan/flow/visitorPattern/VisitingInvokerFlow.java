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
package org.ikasan.flow.visitorPattern;

import java.util.Collections;
import java.util.List;

import org.apache.log4j.Logger;
import org.ikasan.spec.component.endpoint.Consumer;
import org.ikasan.spec.configuration.ConfiguredResource;
import org.ikasan.spec.configuration.DynamicConfiguredResource;
import org.ikasan.spec.flow.Flow;
import org.ikasan.spec.flow.FlowElement;
import org.ikasan.spec.flow.FlowElementInvoker;
import org.ikasan.spec.flow.FlowEvent;
import org.ikasan.spec.flow.FlowInvocationContext;
import org.ikasan.spec.management.ManagedResource;
import org.ikasan.spec.monitor.Monitor;
import org.ikasan.spec.monitor.MonitorListener;
import org.ikasan.spec.recoveryManager.RecoveryManager;

/**
 * Default implementation of a Flow
 * 
 * @author Ikasan Development Team
 */
public class VisitingInvokerFlow implements Flow, EventListener<FlowEvent<?>>, MonitorListener
{
    /** logger instance */
    private static Logger logger = Logger.getLogger(VisitingInvokerFlow.class);
    
    /** Name of this flow */
    private String name;

    /** Name of the module within which this flow exists */
    private String moduleName;

    /** flow element invoker implementation */
    FlowElementInvoker flowElementInvoker;
    
    /** flow configuration implementation */
    FlowConfiguration flowConfiguration;

    /** flow monitor implementation */
    Monitor monitor;

    /** flow recovery manager implementation */
    RecoveryManager<FlowEvent<?>> recoveryManager;
    
    /**
     * Constructor
     * @param name
     * @param moduleName
     * @param flowConfiguration
     * @param flowElementInvoker
     * @param recoveryManager
     */
    public VisitingInvokerFlow(String name, String moduleName, FlowConfiguration flowConfiguration, 
            FlowElementInvoker flowElementInvoker, RecoveryManager<FlowEvent<?>> recoveryManager)
    {
        this.name = name;
        if(name == null)
        {
            throw new IllegalArgumentException("name cannot be 'null'");
        }
        
        this.moduleName = moduleName;
        if(moduleName == null)
        {
            throw new IllegalArgumentException("moduleName cannot be 'null'");
        }
        
        this.flowConfiguration = flowConfiguration;
        if(flowConfiguration == null)
        {
            throw new IllegalArgumentException("flowConfiguration cannot be 'null'");
        }
        
        this.flowElementInvoker = flowElementInvoker;
        if(flowElementInvoker == null)
        {
            throw new IllegalArgumentException("flowElementInvoker cannot be 'null'");
        }
        
        this.recoveryManager = recoveryManager;
        if(recoveryManager == null)
        {
            throw new IllegalArgumentException("recoveryManager cannot be 'null'");
        }
    }

    /**
     * Get this flow name
     * return name
     */
    public String getName()
    {
        return this.name;
    }

    /**
     * Get this module name
     * String moduleName
     */
    public String getModuleName()
    {
        return this.moduleName;
    }

    /**
     * Start this flow
     */
    public void start()
    {
        try
        {
            // configure resources that are marked as configurable
            for(FlowElement<ConfiguredResource> flowElement:flowConfiguration.getConfiguredResourceFlowElements())
            {
                flowConfiguration.configureFlowElement(flowElement);
            }

            // start managed resources (from right to left)
            List<FlowElement<ManagedResource>> flowElements = flowConfiguration.getManagedResourceFlowElements();
            Collections.reverse(flowElements);
            for(FlowElement<ManagedResource> flowElement:flowElements)
            {
                try
                {
                    flowElement.getFlowComponent().startManagedResource();
                }
                catch(RuntimeException e)
                {
                    logger.warn("Failed to start component [" 
                            + flowElement.getComponentName() + "] " + e.getMessage(), e);
                }
            }
            
            // finally start the consumer
            FlowElement<Consumer> consumerFlowElement = flowConfiguration.getConsumerFlowElement();
            Consumer<EventListener<FlowEvent<?>>> consumer = consumerFlowElement.getFlowComponent();
            consumer.setListener( (EventListener<FlowEvent<?>>)this );

            try
            {
                consumer.start();
            }
            catch(RuntimeException e)
            {
                this.recoveryManager.recover(consumerFlowElement.getComponentName(), e);
            }
        }
        finally
        {
            this.notifyMonitor();
        }
    }

    /**
     * Stop this flow
     */
    public void stop()
    {
        try
        {
            // stop any active recovery
            if(this.recoveryManager.isRecovering())
            {
                this.recoveryManager.cancelRecovery();
            }

            // stop consumer and remove the listener
            Consumer<?> consumer = flowConfiguration.getConsumerFlowElement().getFlowComponent();
            consumer.setListener(null);
            consumer.stop();

            // stop all managed resources (left to right)
            for(FlowElement<ManagedResource> flowElement:flowConfiguration.getManagedResourceFlowElements())
            {
                flowElement.getFlowComponent().stopManagedResource();
            }
        }
        finally
        {
            this.notifyMonitor();
        }
        
    }

    /**
     * Invoke the flow with a flow event
     */
    public void invoke(FlowEvent<?> event)
    {
        FlowInvocationContext flowInvocationContext = newFlowInvocationContext();

        try
        {
            // TODO - what to do about module and flow names ?
            for(FlowElement<DynamicConfiguredResource> flowElement:this.flowConfiguration.getDynamicConfiguredResourceFlowElements())
            {
                this.flowConfiguration.configureFlowElement(flowElement);
            }
        
            flowElementInvoker.invoke(flowInvocationContext, event, flowConfiguration.getLeadFlowElement());
            if(this.recoveryManager.isRecovering())
            {
                this.recoveryManager.cancelRecovery();
            }
        }
        catch(Throwable throwable)
        {
            this.recoveryManager.recover(flowInvocationContext.getLastComponentName(), throwable, event);
        }
        finally
        {
            this.notifyMonitor();
        }
    }

    /**
     * Invoke this flow with an exception.
     */
    public void invoke(Throwable throwable)
    {
        // TODO - do something with this...
        this.recoveryManager.recover(this.flowConfiguration.getConsumerFlowElement().getComponentName(), throwable);
    }
    
    /**
     * Notification to all registered <code>MonitorListener</code> of the current state of the <code>Initiator</code>
     */
    protected void notifyMonitor()
    {
        if(this.monitor != null)
        {
            this.monitor.notifyMonitor(this.getState());
        }
    }

    /**
     * Set the flow monitor
     * @param Monitor
     */
    public void setMonitor(Monitor monitor)
    {
        this.monitor = monitor;
        this.notifyMonitor();
    }

    private String getState()
    {
        if(this.recoveryManager.isRecovering())
        {
            return "recovering";
        }
        else if(this.flowConfiguration.getConsumerFlowElement().getFlowComponent().isRunning())
        {
            return "running";
        }
        else if(this.recoveryManager.isUnrecoverable())
        {
            return "stoppedInError";
        }

        return "stopped";
    }
    
    /**
     * Factory method for creating a flow invocation context.
     * @return FlowInvocationContext
     */
    protected FlowInvocationContext newFlowInvocationContext()
    {
        return new DefaultFlowInvocationContext();
    }
}
