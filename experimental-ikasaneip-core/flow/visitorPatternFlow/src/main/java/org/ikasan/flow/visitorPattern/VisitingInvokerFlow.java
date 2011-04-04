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
import org.ikasan.exceptionHandler.action.ExceptionAction;
import org.ikasan.monitor.Monitor;
import org.ikasan.monitor.MonitorListener;
import org.ikasan.spec.component.endpoint.Consumer;
import org.ikasan.spec.configuration.ConfiguredResource;
import org.ikasan.spec.configuration.DynamicConfiguredResource;
import org.ikasan.spec.flow.Flow;
import org.ikasan.spec.flow.FlowElement;
import org.ikasan.spec.flow.FlowElementInvoker;
import org.ikasan.spec.flow.FlowEvent;
import org.ikasan.spec.flow.FlowInvocationContext;
import org.ikasan.spec.management.ManagedResource;
import org.ikasan.spec.recoveryManager.RecoveryManager;

/**
 * Default implementation of a Flow
 * 
 * @author Ikasan Development Team
 */
public class VisitingInvokerFlow implements Flow, EventListener<FlowEvent<?>>, MonitorListener
{
    private static Logger logger = Logger.getLogger(VisitingInvokerFlow.class);
    
    /** Exception action is an implied rollback message */
    public static final String EXCEPTION_ACTION_IMPLIED_ROLLBACK = "Exception Action implied rollback";
    
    /** Exception action is an implied rollback message */
    public static final String UNSUPPORTED_EXCLUDE_ENCONTERED = "Unsupported EXCLUDE action encountered";

    /** Name of this flow */
    private String name;

    /** Name of the module within which this flow exists */
    private String moduleName;

    FlowElementInvoker flowElementInvoker;
    FlowConfiguration flowConfiguration;
    Monitor monitor;
    RecoveryManager<FlowEvent<?>> recoveryManager;
    
    /** Flag indicating a stoppage in error */
    protected boolean error = false;

    /** Flag indicating that the initiator has received a stop call */
    protected boolean stopping = false;
    
    /**
     * Constructor
     * 
     * @param name - name of this flow
     * @param moduleName - name of the module containing this flow
     * @param headElement - first element in the flow
     * @param visitingInvoker - invoker for this flow
     */
    public VisitingInvokerFlow(String name, String moduleName, FlowConfiguration flowConfiguration, 
            FlowElementInvoker flowElementInvoker, RecoveryManager recoveryManager)
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

    public String getName()
    {
        return name;
    }

    public void setModuleName(String moduleName)
    {
        this.moduleName = moduleName;
    }

    public String getModuleName()
    {
        return moduleName;
    }

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
                throw new RuntimeException("Failed to start consumer component [" 
                        + flowConfiguration.getConsumerFlowElement().getComponentName() + "] " + e.getMessage(), e);
            }
        }
        finally
        {
            this.notifyMonitor();
        }
    }

    public void stop()
    {
        this.stopping = true;
        
        try
        {
//            // stop any active recovery
//            if(this.recoveryManager.isRecovering())
//            {
//                this.recoveryManager.cancelRecovery();
//            }

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

    public void invoke(FlowEvent<?> event)
    {
        FlowInvocationContext flowInvocationContext = new DefaultFlowInvocationContext();
        ExceptionAction exceptionAction = null;

        try
        {
            // TODO - what to do about module and flow names ?
            for(FlowElement<DynamicConfiguredResource> flowElement:this.flowConfiguration.getDynamicConfiguredResourceFlowElements())
            {
                this.flowConfiguration.configureFlowElement(flowElement);
            }
        
            flowElementInvoker.invoke(flowInvocationContext, event, flowConfiguration.getLeadFlowElement());
        }
        catch(Throwable throwable)
        {
            handleRecovery(flowInvocationContext.getLastComponentName(), throwable, event);
        }
        finally
        {
            this.notifyMonitor();
        }
    }

    private void handleRecovery(String lastComponentName, Throwable throwable, FlowEvent event)
    {
        try
        {
            this.recoveryManager.recover(lastComponentName, throwable, event);
        }
        finally
        {
            if(recoveryManager.isRecovering())
            {
                // recoveryManager is in an active recovery cycle
                stopping = false;
                error = true;
            }
            else if(this.flowConfiguration.getConsumerFlowElement().getFlowComponent().isRunning())
            {
                // recoveryManager has no need to recover - all is well
                stopping = false;
                error = false;
            }
            else
            {
                // stop the flow as the outcome is unrecoverable
                stopping = true;
                error = true;
            }
        }
        
    }
    
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
            // TODO 
            this.monitor.notifyMonitor("TO DO");
        }
    }

    public void setMonitor(Monitor monitor)
    {
        this.monitor = monitor;
    }

    private String getState()
    {
        if(stopping)
        {
            if(error)
            {
                return "stoppedInError";
            }

            return "stopped";
        }
        else
        {
            if(error)
            {
                return "recovering";
            }

            return "running";
        }
    }
}
