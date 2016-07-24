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

import org.apache.log4j.Logger;
import org.ikasan.flow.configuration.FlowPersistentConfiguration;
import org.ikasan.flow.event.FlowEventFactory;
import org.ikasan.spec.component.endpoint.Consumer;
import org.ikasan.spec.configuration.ConfiguredResource;
import org.ikasan.spec.configuration.DynamicConfiguredResource;
import org.ikasan.spec.error.reporting.ErrorReportingService;
import org.ikasan.spec.error.reporting.IsErrorReportingServiceAware;
import org.ikasan.spec.event.EventFactory;
import org.ikasan.spec.event.EventListener;
import org.ikasan.spec.event.Resubmission;
import org.ikasan.spec.exclusion.ExclusionService;
import org.ikasan.spec.flow.*;
import org.ikasan.spec.management.ManagedResource;
import org.ikasan.spec.management.ManagedResourceRecoveryManager;
import org.ikasan.spec.monitor.Monitor;
import org.ikasan.spec.monitor.MonitorSubject;
import org.ikasan.spec.monitor.Notifier;
import org.ikasan.spec.recovery.RecoveryManager;
import org.ikasan.spec.serialiser.SerialiserFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Default implementation of a Flow
 * 
 * @author Ikasan Development Team
 */
@SuppressWarnings(value={"unchecked", "javadoc"})
public class VisitingInvokerFlow implements Flow, EventListener<FlowEvent<?,?>>, MonitorSubject, IsErrorReportingServiceAware, ConfiguredResource<FlowPersistentConfiguration>
{
	/** logger instance */
    private static Logger logger = Logger.getLogger(VisitingInvokerFlow.class);

    /** thread states */
    private static Boolean ACTIVE = true;
    private static Boolean INACTIVE = false;
    
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

    /** The flow event listener */
    private FlowEventListener flowEventListener;

    /** flow configuration implementation */
    private FlowConfiguration flowConfiguration;

    /** flow monitor implementation */
    private Monitor monitor;

    /** stateful recovery manager implementation */
    private RecoveryManager<FlowEvent<?,?>, FlowInvocationContext> recoveryManager;
    
    /** startup failure flag */
    private boolean flowInitialisationFailure = false;
    
    /** has the consumer been paused */
    private boolean consumerPaused = false;

    /** default managed resource recovery manager factory */
    private ManagedResourceRecoveryManagerFactory managedResourceRecoveryManagerFactory = new ManagedResourceRecoveryManagerFactory();

    /** default event factory */
    private EventFactory eventFactory = new FlowEventFactory();

    /** Event Exclusion Service */
    private ExclusionService<FlowEvent,Object> exclusionService;

    /** flow configuration implementation */
    private ExclusionFlowConfiguration exclusionFlowConfiguration;

    /** errorReportingService handle */
    private ErrorReportingService errorReportingService;

    /** serialiserFactory handle */
    private SerialiserFactory serialiserFactory;

    /** List of listeners for the end of the FlowInvocation using the associated context */
    private List<FlowInvocationContextListener> flowInvocationContextListeners;

    /** flag to control invocation of the context listeners at runtime, defaults to true */
    protected volatile boolean invokeContextListeners = true;


    /** persistent flow configuration */
    private FlowPersistentConfiguration flowPersistentConfiguration = new FlowPersistentConfiguration();
    
    /** configured resource id */
    private String configuredResourceId;

    /** map to keep track of active threads */
    private ConcurrentHashMap<Long, Boolean> activeThreads = new ConcurrentHashMap<Long, Boolean>();

    /** we don't want to wait for ever for the flow to stop. Default 30 seconds */
    private long stopWaitTimeout = 30000;

    /**
     * Constructor
     * @param name
     * @param moduleName
     * @param flowConfiguration
     * @param recoveryManager
     * @param exclusionService
     */
    public VisitingInvokerFlow(String name, String moduleName, FlowConfiguration flowConfiguration,
                               RecoveryManager<FlowEvent<?,?>, FlowInvocationContext> recoveryManager,
                               ExclusionService exclusionService, SerialiserFactory serialiserFactory)
    {
        this(name, moduleName, flowConfiguration, null, recoveryManager, exclusionService, serialiserFactory);
    }

    /**
     * Constructor
     * @param name
     * @param moduleName
     * @param flowConfiguration
     * @param exclusionFlowConfiguration
     * @param recoveryManager
     * @param exclusionService
     */
    public VisitingInvokerFlow(String name, String moduleName, FlowConfiguration flowConfiguration, ExclusionFlowConfiguration exclusionFlowConfiguration,
                               RecoveryManager<FlowEvent<?,?>, FlowInvocationContext> recoveryManager,
                               ExclusionService exclusionService, SerialiserFactory serialiserFactory)
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

        this.exclusionFlowConfiguration = exclusionFlowConfiguration;

        this.recoveryManager = recoveryManager;
        if(recoveryManager == null)
        {
            throw new IllegalArgumentException("recoveryManager cannot be 'null'");
        }

        this.exclusionService = exclusionService;
        if(exclusionService == null)
        {
            throw new IllegalArgumentException("exclusionService cannot be 'null'");
        }
        
        this.serialiserFactory = serialiserFactory;
        if(serialiserFactory == null)
        {
            throw new IllegalArgumentException("serialiserFactory cannot be 'null'");
        }
        
        this.configuredResourceId = this.moduleName + "-" + this.name;
    }

    /**
     * Get the stop wait timeout
     * @return
     */
    public long getStopWaitTimeout()
    {
        return stopWaitTimeout;
    }

    /**
     * Set the stop wait timeout
     * @param stopWaitTimeout
     */
    public void setStopWaitTimeout(long stopWaitTimeout)
    {
        this.stopWaitTimeout = stopWaitTimeout;
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
        try
        {
            this.flowInitialisationFailure = false;

            if(isRunning())
            {
                logger.info("flow [" + name + "] module ["
                    + moduleName
                    + "] is already running. Ignoring start request.");
                return;
            }

            _start();
            startConsumer();
            logger.info("Started Flow[" + this.name + "] in Module[" + this.moduleName + "]");
        }
        finally
        {
            this.notifyMonitor();
        }
    }

    public void startPause()
    {
        try
        {
            _start();
            pause();
        }
        finally
        {
            this.notifyMonitor();
        }
    }

    protected void _start()
    {
        try
        {
            this.flowInitialisationFailure = false;

            this.recoveryManager.initialise();

            // configure any registered monitors marked as configured
            if(this.monitor != null)
            {
                if(this.monitor instanceof ConfiguredResource)
                {
                    ConfiguredResource configuredMonitor = (ConfiguredResource)monitor;
                    if( configuredMonitor.getConfiguredResourceId() == null )
                    {
                        configuredMonitor.setConfiguredResourceId(this.moduleName + this.name + "_monitor");
                    }

                    this.flowConfiguration.configure(configuredMonitor);
                }

                List<Notifier> monitorNotifiers = this.monitor.getNotifiers();
                if(monitorNotifiers == null)
                {
                    logger.warn("Flow monitor has no registered notifiers. Flow state changes will not be notified!");
                }
                else
                {
                    for(Notifier monitorNotifier : monitorNotifiers)
                    {
                        if(monitorNotifier instanceof ConfiguredResource)
                        {
                            ConfiguredResource configuredMonitorNotifier = (ConfiguredResource)monitorNotifier;
                            if( configuredMonitorNotifier.getConfiguredResourceId() == null )
                            {
                                configuredMonitorNotifier.setConfiguredResourceId(this.moduleName + this.name + "_monitor_notifier_" + monitorNotifier.getClass().getSimpleName());
                            }

                            this.flowConfiguration.configure(configuredMonitorNotifier);
                        }
                    }
                }
            }

            // configure exclusion flow resources that are marked as configurable
            if(this.exclusionFlowConfiguration != null)
            {
                configure(this.exclusionFlowConfiguration.getConfiguredResourceFlowElements());

                // register the errorReportingService with those components in the exclusion flow requiring it
                for(FlowElement<IsErrorReportingServiceAware> flowElement:this.exclusionFlowConfiguration.getErrorReportingServiceAwareFlowElements())
                {
                    IsErrorReportingServiceAware component = flowElement.getFlowComponent();
                    component.setErrorReportingService(this.errorReportingService);
                }
            }

            // configure business flow resources that are marked as configurable
            configure(this.flowConfiguration.getConfiguredResourceFlowElements());
            
            // configure the flow elements
            configureFlowElements(this.flowConfiguration.getFlowElements());
            
            // configure the flow itself
            this.flowConfiguration.configure(this);
            
            this.invokeContextListeners = this.flowPersistentConfiguration.getInvokeContextListeners();

            // register the errorReportingService with those components requiring it
            for(FlowElement<IsErrorReportingServiceAware> flowElement:this.flowConfiguration.getErrorReportingServiceAwareFlowElements())
            {
                IsErrorReportingServiceAware component = flowElement.getFlowComponent();
                component.setErrorReportingService(this.errorReportingService);
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
    }

    /**
     * Configure the given list of configured flowElements
     * @param flowElements
     */
    private void configure(List<FlowElement<ConfiguredResource>> flowElements)
    {
        for(FlowElement<ConfiguredResource> flowElement:flowElements)
        {
            // set the default configured resource id if none previously set.
            if(flowElement.getFlowComponent().getConfiguredResourceId() == null)
            {
                flowElement.getFlowComponent().setConfiguredResourceId(this.moduleName + this.name + flowElement.getComponentName());
            }

            this.flowConfiguration.configure(flowElement.getFlowComponent());
        }
    }
    
    /**
     * Configure the given list of configured flowElements
     * @param flowElements
     */
    private void configureFlowElements(List<FlowElement<?>> flowElements)
    {
        for(FlowElement<?> flowElement:flowElements)
        {
            // set the default configured resource id if none previously set.
            if(flowElement.getConfiguredResourceId() == null)
            {
                flowElement.setConfiguredResourceId(this.moduleName + this.name + flowElement.getComponentName() + "_element");
            }

            this.flowConfiguration.configure(flowElement);
        }
    }

    public void pause()
    {
        try
        {
            // stop any active recovery
            if(this.recoveryManager.isRecovering())
            {
                this.recoveryManager.cancel();
            }

            // stop consumer and remove the listener
            Consumer<?,?> consumer = this.flowConfiguration.getConsumerFlowElement().getFlowComponent();
            consumer.stop();

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

    /**
     * Is this flow in a running / recovering state
     * @return
     */
    public boolean isRunning()
    {
        String currentState = this.getState();
        return currentState.equals(RECOVERING) || currentState.equals(RUNNING);

    }

    /**
     * Is this flow in a paused state
     * @return
     */
    public boolean isPaused()
    {
        String currentState = this.getState();
        return currentState.equals(PAUSED);
    }

    @Override
    public void startContextListeners()
    {
        invokeContextListeners = true;
    }

    @Override
    public void stopContextListeners()
    {
        invokeContextListeners = false;
    }

    @Override
    public boolean areContextListenersRunning()
    {
        return invokeContextListeners;
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
        consumer.setListener(this);

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
     * Stop all managed resources from left to right.
     */
    protected void stopManagedResources()
    {
        stopManagedResourceFlowElements(this.flowConfiguration.getManagedResourceFlowElements());
        if(this.exclusionFlowConfiguration != null)
        {
            stopManagedResourceFlowElements(this.exclusionFlowConfiguration.getManagedResourceFlowElements());
        }
    }

    private void stopManagedResourceFlowElements(List<FlowElement<ManagedResource>> flowElements) {
        for(FlowElement<ManagedResource> flowElement:flowElements)
        {
            logger.info("Stopping managed component             ["
                    + flowElement.getComponentName() + "]...");
            flowElement.getFlowComponent().stopManagedResource();
            logger.info("Successfully stopped managed component ["
                    + flowElement.getComponentName() + "]");
        }
    }

    /**
     * Start the components marked as including Managed Resources.
     * These component are started from right to left in the flow.
     */
    protected void startManagedResources()
    {
        if(this.exclusionFlowConfiguration != null)
        {
            List<FlowElement<ManagedResource>> exclusionFlowElements = this.exclusionFlowConfiguration.getManagedResourceFlowElements();
            startManagedResourceFlowElements(exclusionFlowElements);
        }

        List<FlowElement<ManagedResource>> flowElements = this.flowConfiguration.getManagedResourceFlowElements();
        this.recoveryManager.setManagedResources(flowElements);
        startManagedResourceFlowElements(flowElements);
    }

    private void startManagedResourceFlowElements(List<FlowElement<ManagedResource>> flowElements)
    {
        for(int index=flowElements.size()-1; index >= 0; index--)
        {
            FlowElement<ManagedResource> flowElement = flowElements.get(index);
            try
            {
                ManagedResource managedResource = flowElement.getFlowComponent();
                managedResource.setManagedResourceRecoveryManager( managedResourceRecoveryManagerFactory.getManagedResourceRecoveryManager(flowElement.getComponentName()) );
                logger.info("Starting managed component             ["
                        + flowElement.getComponentName() + "]...");
                managedResource.startManagedResource();
                logger.info("Successfully started managed component ["
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
     * Set the current threads state
     *
     * @param state
     */
    private void setThreadState(boolean state)
    {
        this.activeThreads.put(Thread.currentThread().getId(), state);
    }

    /**
     * Determine if all threads are inactive
     *
     * @return
     * @throws InterruptedException
     */
    private boolean allThreadInactive()
    {
        long currentTimeMillis = System.currentTimeMillis();

        logger.info("Checking if threads are inactive.");

        while(true)
        {
            boolean allInactive = true;
            for(Long key: this.activeThreads.keySet())
            {
                boolean active = this.activeThreads.get(key);

                logger.debug("Thread [" + key + "] is active [" + active + "]");

                if(active)
                {
                    allInactive = false;
                }
            }

            if(allInactive)
            {
                logger.info("All threads are inactive.");
                this.activeThreads = new ConcurrentHashMap<Long, Boolean>();
                return true;
            }
            else if(System.currentTimeMillis() - currentTimeMillis > this.stopWaitTimeout)
            {
                logger.info("Timed out waiting for threads to complete.");
                this.activeThreads = new ConcurrentHashMap<Long, Boolean>();
                return true;
            }
            else
            {
                try
                {
                    Thread.sleep(500);
                    logger.debug("Sleeping waiting for threads to complete.");
                }
                catch (InterruptedException e)
                {
                    logger.error("Could not put thread to sleep when trying to determine if all threads are inactive.", e);
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

            // stop any active recovery
            if(this.recoveryManager.isRecovering())
            {
                this.recoveryManager.cancel();
            }

            // stop consumer and remove the listener
            Consumer<?,?> consumer = this.flowConfiguration.getConsumerFlowElement().getFlowComponent();
            consumer.stop();

            if(this.allThreadInactive())
            {
                consumer.setListener(null);
                stopManagedResources();
            }

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
        this.setThreadState(ACTIVE);
        FlowInvocationContext flowInvocationContext = createFlowInvocationContext();
        flowInvocationContext.startFlowInvocation();

        // keep a handle on the original assigned eventLifeId as this could change within the flow
        Object originalEventLifeIdentifier = event.getIdentifier();

        try
        {
            // TODO part of flow configuration should also disable of exclusionService

            if(this.exclusionService.isBlackListed(originalEventLifeIdentifier))
            {
                this.exclusionService.park(event, originalEventLifeIdentifier);
                if(this.exclusionFlowConfiguration != null)
                {
                    invoke(moduleName, name, flowInvocationContext, event, this.exclusionFlowConfiguration.getLeadFlowElement());
                }
                this.exclusionService.removeBlacklisted(originalEventLifeIdentifier);
            }
            else
            {
                configureDynamicConfiguredResources();
                
                // record the event so that it can be replayed if necessary.
                if(this.getFlowConfiguration().getReplayRecordService() != null && this.flowPersistentConfiguration.getIsRecording())
                {
                	this.getFlowConfiguration().getReplayRecordService().record(event, this.moduleName, 
                			this.name, this.flowPersistentConfiguration.getRecordedEventTimeToLive());
                }
                
                invoke(moduleName, name, flowInvocationContext, event, this.flowConfiguration.getConsumerFlowElement());
                updateDynamicConfiguredResources();
                if(this.recoveryManager.isRecovering())
                {
                    this.recoveryManager.cancel();
                }
            }
            flowInvocationContext.endFlowInvocation();
        }
        catch(Throwable throwable)
        {
            flowInvocationContext.endFlowInvocation();
            this.recoveryManager.recover(flowInvocationContext, throwable, event, originalEventLifeIdentifier);
        }
        finally
        {
            this.notifyFlowInvocationContextListenersEndFlow(flowInvocationContext);
            this.notifyMonitor();
            this.setThreadState(INACTIVE);
        }
    }
    
    /* (non-Javadoc)
	 * @see org.ikasan.spec.event.EventListener#invoke(org.ikasan.spec.event.Resubmission)
	 */
	@Override
	public void invoke(Resubmission<FlowEvent<?,?>> event)
	{
		FlowInvocationContext flowInvocationContext = createFlowInvocationContext();
        flowInvocationContext.startFlowInvocation();

        try
        {
            configureDynamicConfiguredResources();
            invoke(moduleName, name, flowInvocationContext, event.getEvent(), this.flowConfiguration.getConsumerFlowElement());
            updateDynamicConfiguredResources();
            if(this.recoveryManager.isRecovering())
            {
                this.recoveryManager.cancel();
            }
            flowInvocationContext.endFlowInvocation();
        }
        catch(Throwable throwable)
        {
            flowInvocationContext.endFlowInvocation();
            this.recoveryManager.recover(flowInvocationContext, throwable, event.getEvent(), event.getEvent().getIdentifier());
        }
        finally
        {
            this.notifyFlowInvocationContextListenersEndFlow(flowInvocationContext);
            this.notifyMonitor();
        }
	}

	private void configureDynamicConfiguredResources()
    {
        for(FlowElement<DynamicConfiguredResource> flowElement:this.flowConfiguration.getDynamicConfiguredResourceFlowElements())
        {
            this.flowConfiguration.configure(flowElement.getFlowComponent());
        }
    }
    
    private void updateDynamicConfiguredResources()
    {
        for(FlowElement<DynamicConfiguredResource> flowElement:this.flowConfiguration.getDynamicConfiguredResourceFlowElements())
        {
            this.flowConfiguration.update(flowElement.getFlowComponent());
        }
    }

    protected void invoke(String moduleName, String flowName, FlowInvocationContext flowInvocationContext,
                       FlowEvent flowEvent, FlowElement flowElement)
    {
        while (flowElement != null)
        {
            try
            {
               	
            	notifyFlowInvocationContextListenersSnapEvent(flowElement, flowEvent);
            	flowElementCaptureMetrics(flowElement);
                flowElement = flowElement.getFlowElementInvoker().invoke(flowEventListener, moduleName, flowName, flowInvocationContext, flowEvent, flowElement);
                
            }
            catch (ClassCastException e)
            {
                throw new RuntimeException("Unable to find method signature in module["
                        + moduleName + "] flow[" + flowName + "] on component ["
                        + flowElement.getComponentName() + "] for payload class ["
                        + flowEvent.getPayload().getClass().getName() + "]", e);
            }
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
                this.monitor.invoke(this.getState());
            }
            catch(RuntimeException e)
            {
                // don't let the failure of the monitor interfere with
                // the operation of the business flow
                logger.error("Failed to notify the registered monitor", e);
            }
        }
    }
    
    protected void flowElementCaptureMetrics(FlowElement flowElement)
    {
    	try
    	{
	    	if(flowElement.getConfiguration() != null)
	    	{
	    		flowElement.getFlowElementInvoker().setIgnoreContextInvocation(!((FlowElementConfiguration)flowElement.getConfiguration()).getCaptureMetrics());
	    	}
	    	else
	    	{
	    		flowElement.getFlowElementInvoker().setIgnoreContextInvocation(true);
	    	}
    	} 
        catch (RuntimeException e)
        {
        	flowElement.getFlowElementInvoker().setIgnoreContextInvocation(true);
            logger.warn("Unable to set the ignore context invocation on flow element, defaulting to true and continuing", e);
        }
    }

    /**
     * Notify any FlowInvocationContextListeners that the flow has completed
     */
    protected void notifyFlowInvocationContextListenersEndFlow(FlowInvocationContext flowInvocationContext)
    {    	
        if (flowInvocationContextListeners != null && this.invokeContextListeners)
        {
            for (FlowInvocationContextListener listener : flowInvocationContextListeners)
            {
                try
                {
                    listener.endFlow(flowInvocationContext);
                } 
                catch (RuntimeException e)
                {
                    logger.warn("Unable to invoke FlowInvocationContextListener, continuing", e);
                }
            }
        }

    }
    
    /**
     * Notify any FlowInvocationContextListeners that to snap an event
     */
    protected void notifyFlowInvocationContextListenersSnapEvent(FlowElement flowElement, FlowEvent flowEvent)
    {
    	if(((FlowElementConfiguration)flowElement.getConfiguration()).getSnapEvent() &&
    			((FlowElementConfiguration)flowElement.getConfiguration()).getCaptureMetrics())
    	{
	        if (flowInvocationContextListeners != null && this.invokeContextListeners)
	        {
	            for (FlowInvocationContextListener listener : flowInvocationContextListeners)
	            {
	                try
	                {
	                    listener.snapEvent(flowElement, flowEvent);
	                } 
	                catch (RuntimeException e)
	                {
	                    logger.warn("Unable to invoke FlowInvocationContextListener snap event, continuing", e);
	                }
	            }
	        }
    	}
    }


    /**
     * Set the flow monitor
     * @param monitor
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
		this.flowEventListener = flowEventListener;
	}

    @Override
    public void setErrorReportingService(ErrorReportingService errorReportingService)
    {
        this.errorReportingService = errorReportingService;
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
        private Map<String,ManagedResourceRecoveryManager> managedResourceRecoveryManagers = new HashMap<>();
        
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

	/* (non-Javadoc)
	 * @see org.ikasan.spec.flow.Flow#getConsumerFlowElement()
	 */
	@Override
	public FlowConfiguration getFlowConfiguration()
	{
		return this.flowConfiguration;
	}

	/* (non-Javadoc)
	 * @see org.ikasan.spec.flow.Flow#getSerialiserFactory()
	 */
	@Override
	public SerialiserFactory getSerialiserFactory()
	{
		return this.serialiserFactory;
	}
	
	/* (non-Javadoc)
	 * @see org.ikasan.spec.configuration.Configured#getConfiguration()
	 */
	@Override
	public FlowPersistentConfiguration getConfiguration() 
	{
		return this.flowPersistentConfiguration;
	}

	/* (non-Javadoc)
	 * @see org.ikasan.spec.configuration.Configured#setConfiguration(java.lang.Object)
	 */
	@Override
	public void setConfiguration(FlowPersistentConfiguration configuration)
	{
		this.flowPersistentConfiguration = configuration;
	}

	/* (non-Javadoc)
	 * @see org.ikasan.spec.configuration.ConfiguredResource#getConfiguredResourceId()
	 */
	@Override
	public String getConfiguredResourceId() 
	{
		return this.configuredResourceId;
	}

	/* (non-Javadoc)
	 * @see org.ikasan.spec.configuration.ConfiguredResource#setConfiguredResourceId(java.lang.String)
	 */
	@Override
	public void setConfiguredResourceId(String id) 
	{
		this.configuredResourceId = id;
	}

    @Override
    public void setFlowInvocationContextListeners(List<FlowInvocationContextListener> flowInvocationContextListeners)
    {
        this.flowInvocationContextListeners = flowInvocationContextListeners;
    }

    @Override
    public List<FlowInvocationContextListener> getFlowInvocationContextListeners()
    {
        return flowInvocationContextListeners;
    }
}
