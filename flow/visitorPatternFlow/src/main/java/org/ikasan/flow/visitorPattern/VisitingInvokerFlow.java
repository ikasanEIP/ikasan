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
import org.ikasan.spec.event.EventListener;
import org.ikasan.spec.flow.Flow;
import org.ikasan.spec.flow.FlowElement;
import org.ikasan.spec.flow.FlowElementInvoker;
import org.ikasan.spec.flow.FlowEvent;
import org.ikasan.spec.flow.FlowInvocationContext;
import org.ikasan.spec.management.ManagedResource;
import org.ikasan.spec.monitor.Monitor;
import org.ikasan.spec.monitor.MonitorListener;
import org.ikasan.spec.recovery.RecoveryManager;

/**
 * Default implementation of a Flow
 * 
 * @author Ikasan Development Team
 */
public class VisitingInvokerFlow implements Flow, EventListener<FlowEvent<?>>, MonitorListener
{
    /** logger instance */
    private static Logger logger = Logger.getLogger(VisitingInvokerFlow.class);
    
    /** running state string constant */
    private static String RUNNING = "running";
    
    /** stopped state string constant */
    private static String STOPPED = "stopped";
    
    /** recovering state string constant */
    private static String RECOVERING = "recovering";
    
    /** stoppedInError state string constant */
    private static String STOPPED_IN_ERROR = "stoppedInError";
    
    /** Name of this flow */
    private String name;

    /** Name of the module within which this flow exists */
    private String moduleName;

    /** flow element invoker implementation */
    private FlowElementInvoker flowElementInvoker;
    
    /** flow configuration implementation */
    private FlowConfiguration flowConfiguration;

    /** flow monitor implementation */
    private Monitor monitor;

    /** stateful recovery manager implementation */
    private RecoveryManager<FlowEvent<?>> recoveryManager;
    
    /** startup failure flag */
    private boolean flowInitialisationFailure = false;
    
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
     * Initialise the flow elements for startup
     */
    protected void initialiseFlow()
    {
        this.flowInitialisationFailure = false;
        
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
        }
        catch(RuntimeException e)
        {
            this.flowInitialisationFailure = true;
            throw e;
        }
    }
    
    /**
     * Start this flow
     */
    public void start()
    {
        FlowElement<Consumer> consumerFlowElement = flowConfiguration.getConsumerFlowElement();

        String currentState = this.getState();
        if(currentState.equals(RECOVERING) || currentState.equals(RUNNING))
        {
            logger.info("consumer [" + consumerFlowElement.getComponentName() 
                + "] already in a " + currentState + " state. Ignoring start request.");
            return;
        }
        
        try
        {
            // initialise the flow
            initialiseFlow();
            
            // start the consumer
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
        catch(RuntimeException e)
        {
            logger.error(e);
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
                this.recoveryManager.cancel();
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
        FlowInvocationContext flowInvocationContext = getFlowInvocationContext();

        try
        {
            for(FlowElement<DynamicConfiguredResource> flowElement:this.flowConfiguration.getDynamicConfiguredResourceFlowElements())
            {
                try
                {
                    this.flowConfiguration.configureFlowElement(flowElement);
                }
                catch(RuntimeException e)
                {
                    flowInvocationContext.addInvokedComponentName(flowElement.getComponentName());
                    throw e;
                }
            }
        
            flowElementInvoker.invoke(flowInvocationContext, event, flowConfiguration.getLeadFlowElement());
            if(this.recoveryManager.isRecovering())
            {
                this.recoveryManager.cancel();
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
     * Invoke the recover manager to act on the passed exception.
     * @param throwable
     */
    public void invoke(Throwable throwable)
    {
        try
        {
            this.recoveryManager.recover(getFlowInvocationContext().getLastComponentName(), throwable);
        }
        finally
        {
            this.notifyMonitor();
        }
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

    /**
     * Resolve the state of this flow into a string representation
     * @return
     */
    protected String getState()
    {
        if(this.recoveryManager.isRecovering())
        {
            return RECOVERING;
        }
        else if(this.flowConfiguration.getConsumerFlowElement().getFlowComponent().isRunning())
        {
            return RUNNING;
        }
        else if(this.flowInitialisationFailure || this.recoveryManager.isUnrecoverable())
        {
            return STOPPED_IN_ERROR;
        }

        return STOPPED;
    }
    
    /**
     * Factory method for creating a flow invocation context.
     * @return FlowInvocationContext
     */
    protected FlowInvocationContext getFlowInvocationContext()
    {
        FlowInvocationContext context = new DefaultFlowInvocationContext(this.name, this.moduleName);
        
        // consumer is added as default first invoked component
        context.addInvokedComponentName(this.flowConfiguration.getConsumerFlowElement().getComponentName());
        return context;
    }
}
