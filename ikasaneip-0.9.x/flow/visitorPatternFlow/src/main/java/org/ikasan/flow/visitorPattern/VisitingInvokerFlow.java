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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.ikasan.flow.event.FlowEventFactory;
import org.ikasan.spec.component.endpoint.Consumer;
import org.ikasan.spec.configuration.ConfiguredResource;
import org.ikasan.spec.configuration.DynamicConfiguredResource;
import org.ikasan.spec.event.EventFactory;
import org.ikasan.spec.event.EventListener;
import org.ikasan.spec.flow.Flow;
import org.ikasan.spec.flow.FlowElement;
import org.ikasan.spec.flow.FlowElementInvoker;
import org.ikasan.spec.flow.FlowEvent;
import org.ikasan.spec.flow.FlowEventListener;
import org.ikasan.spec.flow.FlowInvocationContext;
import org.ikasan.spec.management.ManagedResource;
import org.ikasan.spec.management.ManagedResourceRecoveryManager;
import org.ikasan.spec.monitor.Monitor;
import org.ikasan.spec.monitor.MonitorListener;
import org.ikasan.spec.recovery.RecoveryManager;
import org.ikasan.spec.event.EventFactory;

/**
 * Default implementation of a Flow
 * 
 * @author Ikasan Development Team
 */
public class VisitingInvokerFlow implements Flow, EventListener<FlowEvent<?,?>>, MonitorListener
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
    
    /** paused state string constant */
    private static String PAUSED = "paused";
    
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
    private RecoveryManager<FlowEvent<?,?>> recoveryManager;
    
    /** startup failure flag */
    private boolean flowInitialisationFailure = false;
    
    /** has the consumer been paused */
    private boolean consumerPaused = false;

    /** default managed resource recovery manager factory */
    private ManagedResourceRecoveryManagerFactory managedResourceRecoveryManagerFactory = new ManagedResourceRecoveryManagerFactory();

    /** default event factory */
    private EventFactory eventFactory = new FlowEventFactory();

    /**
     * Constructor
     * @param name the flow name
     * @param moduleName name of module this flow exists for
     * @param flowConfiguration
     * @param flowElementInvoker
     * @param recoveryManager
     */
    public VisitingInvokerFlow(String name, String moduleName, FlowConfiguration flowConfiguration, 
            FlowElementInvoker flowElementInvoker, RecoveryManager<FlowEvent<?,?>> recoveryManager)
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
     * Allow override of the managed resource recovery manager within this class.
     * Mainly for testability.
     * @param managedResourceRecoveryManagerFactory
     */
    public void setManagedResourceRecoveryManagerFactory (ManagedResourceRecoveryManagerFactory managedResourceRecoveryManagerFactory)
    {
        this.managedResourceRecoveryManagerFactory = managedResourceRecoveryManagerFactory;
    }
    
    /**
     * Start this flow
     */
    public void start()
    {
        this.flowInitialisationFailure = false;

        try
        {
            if(isRunning())
            {
                logger.info("flow [" + name + "] module [" 
                    + moduleName 
                    + "] is already running. Ignoring start request.");
                return;
            }

            try
            {
		        this.recoveryManager.initialise();

                // configure resources that are marked as configurable
                for(FlowElement<ConfiguredResource> flowElement:this.flowConfiguration.getConfiguredResourceFlowElements())
                {
                    // set the default configured resource id if none previously set.
                    if(flowElement.getFlowComponent().getConfiguredResourceId() == null)
                    {
                        flowElement.getFlowComponent().setConfiguredResourceId(this.moduleName + this.name + flowElement.getComponentName());
                    }
                        
                    this.flowConfiguration.configureFlowElement(flowElement);
                }
            }
            catch(RuntimeException e)
            {
                this.flowInitialisationFailure = true;
                throw e;
            }
            
            try
            {
                startManagedResources();
            }
            catch(RuntimeException e)
            {
                this.flowInitialisationFailure = true;
                this.stopManagedResources();
                throw e;
            }
            
            startConsumer();
            logger.info("Started Flow[" + this.name + "] in Module[" + this.moduleName + "]");
        }
        finally
        {
            this.notifyMonitor();
        }
    }

    public void pause()
    {
        try
        {
            stopConsumer();
            this.consumerPaused = true;
            logger.info("Paused Flow[" + this.name + "] in Module[" + this.moduleName + "]");
        }
        finally
        {
            this.notifyMonitor();
        }
    }
    
    public void resume()
    {
        try
        {
            if(isRunning())
            {
                logger.info("flow [" + name + "] module [" 
                    + moduleName 
                    + "] is already running. Ignoring resume request.");
                return;
            }

            startConsumer();
            logger.info("Resumed Flow[" + this.name + "] in Module[" + this.moduleName + "]");
        }
        finally
        {
            this.notifyMonitor();
        }
    }
    
    protected boolean isRunning()
    {
        String currentState = this.getState();
        if(currentState.equals(RECOVERING) || currentState.equals(RUNNING))
        {
            return true;
        }

        return false;
    }
    
    /**
     * Start the consumer component.
     */
    protected void startConsumer()
    {
        this.consumerPaused = false;
        FlowElement<Consumer> consumerFlowElement = this.flowConfiguration.getConsumerFlowElement();

        // start the consumer
        Consumer<EventListener<FlowEvent<?,?>>,EventFactory> consumer = consumerFlowElement.getFlowComponent();
        consumer.setListener( (EventListener<FlowEvent<?,?>>)this );

        // if event factory has not been set on the consumer then set the default
        if(consumer.getEventFactory() == null)
        {
            consumer.setEventFactory(eventFactory);
        }

        try
        {
            consumer.start();
        }
        catch(RuntimeException e)
        {
            this.recoveryManager.recover(consumerFlowElement.getComponentName(), e);
        }
    }
    
    /**
     * Stop the consumer component.
     */
    protected void stopConsumer()
    {
        // stop any active recovery
        if(this.recoveryManager.isRecovering())
        {
            this.recoveryManager.cancel();
        }

        // stop consumer and remove the listener
        Consumer<?,?> consumer = this.flowConfiguration.getConsumerFlowElement().getFlowComponent();
        consumer.setListener(null);
        consumer.stop();
    }
    
    /**
     * Stop all managed resources from left to right.
     */
    protected void stopManagedResources()
    {
        for(FlowElement<ManagedResource> flowElement:this.flowConfiguration.getManagedResourceFlowElements())
        {
            flowElement.getFlowComponent().stopManagedResource();
        }
    }
    
    /**
     * Start the components marked as including Managed Resources.
     * These component are started from right to left in the flow.
     */
    protected void startManagedResources()
    {
        List<FlowElement<ManagedResource>> flowElements = this.flowConfiguration.getManagedResourceFlowElements();
        this.recoveryManager.setManagedResources(flowElements);
        for(int index=flowElements.size()-1; index >= 0; index--)
        {
            FlowElement<ManagedResource> flowElement = flowElements.get(index);
            try
            {
                ManagedResource managedResource = flowElement.getFlowComponent();
                managedResource.setManagedResourceRecoveryManager( managedResourceRecoveryManagerFactory.getManagedResourceRecoveryManager(flowElement.getComponentName()) );
                managedResource.startManagedResource();
                logger.info("Started managed component [" 
                    + flowElement.getComponentName() + "]");
            }
            catch(RuntimeException e)
            {
                if(flowElement.getFlowComponent().isCriticalOnStartup())
                {
                    // log issues as these may get resolved by the recovery manager 
                    logger.warn("Failed to start critical component [" 
                            + flowElement.getComponentName() + "] " + e.getMessage(), e);
                    throw e;
                }
                else
                {
                    // just log any issues as these may get resolved by the recovery manager 
                    logger.warn("Failed to start managed component [" 
                            + flowElement.getComponentName() + "] " + e.getMessage(), e);
                }
            }
        }
    }
    
    /**
     * Stop this flow
     */
    public void stop()
    {
        try
        {
            this.consumerPaused = false;
            stopConsumer();
            stopManagedResources();
            logger.info("Stopped Flow[" + this.name + "] in Module[" + this.moduleName + "]");
        }
        finally
        {
            this.notifyMonitor();
        }
        
    }

    /**
     * Invoke the flow with a flow event
     */
    public void invoke(FlowEvent<?,?> event)
    {
        FlowInvocationContext flowInvocationContext = createFlowInvocationContext();

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
        
            this.flowElementInvoker.invoke(moduleName, name, flowInvocationContext, event, this.flowConfiguration.getConsumerFlowElement());
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
            this.recoveryManager.recover(this.flowConfiguration.getConsumerFlowElement().getComponentName(), throwable);
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
            try
            {
                this.monitor.notifyMonitor(this.getState());
            }
            catch(RuntimeException e)
            {
                // don't let the failure of the monitor interfere with
                // the operation of the business flow
                logger.error("Failed to notify the registered monitor", e);
            }
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
    public String getState()
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
        else if(this.consumerPaused)
        {
            return PAUSED;
        }

        return STOPPED;
    }

    /**
     * Factory method for creating a flow invocation context.
     * @return FlowInvocationContext
     */
    protected FlowInvocationContext createFlowInvocationContext()
    {
        return new DefaultFlowInvocationContext();
    }

    /* (non-Javadoc)
     * @see org.ikasan.spec.flow.Flow#getFlowElements()
     */
    public List<FlowElement<?>> getFlowElements()
    {
        return this.flowConfiguration.getFlowElements();
    }

    /**
     * Return the flow element matching this name.
     * @return flowElement
     */
    public FlowElement<?> getFlowElement(String name)
    {
        for(FlowElement flowElement:this.flowConfiguration.getFlowElements())
        {
            if(flowElement.getComponentName().equals(name))
            {
                return flowElement;
            }
        }

        return null;
    }

    /**
     * Set the flow event listener
     * @param flowEventListener
     */
	public void setFlowListener(FlowEventListener flowEventListener) 
	{
		this.flowElementInvoker.setFlowEventListener(flowEventListener);
	}

    /**
     * Managed Resource Recovery Manager factory used to create MR recovery manager 
     * instances per named managed resource.
     * @author Ikasan Development Team
     *
     */
    protected class ManagedResourceRecoveryManagerFactory
    {
        // cache of managed resource recovery managers
        private Map<String,ManagedResourceRecoveryManager> managedResourceRecoveryManagers = new HashMap<String,ManagedResourceRecoveryManager>();
        
        /**
         * Get the named managed resource recovery manager
         * @param name
         * @return ManagedResourceRecoveryManager
         */
        public ManagedResourceRecoveryManager getManagedResourceRecoveryManager(String name)
        {
            ManagedResourceRecoveryManager managedResourceRecoveryManager = managedResourceRecoveryManagers.get(name);
            if(managedResourceRecoveryManager == null)
            {
                managedResourceRecoveryManager = new ManagedResourceRecoveryManagerImpl(name);
                managedResourceRecoveryManagers.put(name, managedResourceRecoveryManager);
            }
            
            return managedResourceRecoveryManager;
        }

        /**
         * Managed Resource Recovery Manager implementation
         * @author Ikasan Development Team
         *
         */
        protected class ManagedResourceRecoveryManagerImpl implements ManagedResourceRecoveryManager
        {
            /** name of this managed resource recovery manager */
            private String name;
            
            /**
             * Constructor
             * @param name
             */
            public ManagedResourceRecoveryManagerImpl(String name)
            {
                this.name = name;
                if(name == null)
                {
                    throw new IllegalArgumentException("name cannot be 'null'");
                }
            }
            
            /*
             * (non-Javadoc)
             * @see org.ikasan.spec.management.ManagedResourceRecoveryManager#recover(java.lang.Throwable)
             */
            public void recover(Throwable throwable)
            {
                try
                {
                    recoveryManager.recover(name, throwable);
                }
                finally
                {
                    notifyMonitor();
                }
            }

            /*
             * (non-Javadoc)
             * @see org.ikasan.spec.management.ManagedResourceRecoveryManager#isRecovering()
             */
            public boolean isRecovering()
            {
                return recoveryManager.isRecovering();
            }

            /*
             * (non-Javadoc)
             * @see org.ikasan.spec.management.ManagedResourceRecoveryManager#cancel()
             */
            public void cancel()
            {
                try
                {
                    recoveryManager.cancel();
                }
                finally
                {
                    notifyMonitor();
                }
            }
        }
    }

}
