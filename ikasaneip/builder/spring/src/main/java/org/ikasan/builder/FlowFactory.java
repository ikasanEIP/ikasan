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
package org.ikasan.builder;

import org.ikasan.exceptionResolver.ExceptionResolver;
import org.ikasan.exclusion.service.ExclusionServiceFactory;
import org.ikasan.flow.configuration.FlowComponentInvokerSetupServiceConfiguration;
import org.ikasan.flow.configuration.FlowPersistentConfiguration;
import org.ikasan.flow.event.ResubmissionEventFactoryImpl;
import org.ikasan.history.listener.MessageHistoryContextListener;
import org.ikasan.spec.recovery.RecoveryManagerFactory;
import org.ikasan.spec.component.endpoint.Consumer;
import org.ikasan.spec.configuration.ConfigurationService;
import org.ikasan.spec.error.reporting.ErrorReportingService;
import org.ikasan.spec.error.reporting.ErrorReportingServiceFactory;
import org.ikasan.spec.exclusion.ExclusionService;
import org.ikasan.spec.exclusion.IsExclusionServiceAware;
import org.ikasan.spec.flow.*;
import org.ikasan.spec.history.MessageHistoryService;
import org.ikasan.spec.monitor.FlowMonitor;
import org.ikasan.spec.recovery.RecoveryManager;
import org.ikasan.spec.replay.ReplayRecordService;
import org.ikasan.spec.resubmission.ResubmissionEventFactory;
import org.ikasan.spec.resubmission.ResubmissionService;
import org.ikasan.spec.serialiser.SerialiserFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.framework.Advised;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.util.List;
import java.util.Map;

/**
 * Spring based FactoryBean for the creation of Ikasan Flows.
 * @author Ikasan Development Team
 * 
 */
public class FlowFactory implements FactoryBean<Flow>, ApplicationContextAware
{
 	/** logger */
    private static Logger logger = LoggerFactory.getLogger(FlowFactory.class);

    BuilderFactory builderFactory;

    /** name of the flow's module owner */
    String moduleName;

    /** name of the flow being instantiated */
    String name;

    /** optional module description */
    String description;

    /** default flow event listener which can be overridden */
    FlowEventListener flowEventListener;

    /** recovery manager factory for getting a default recoveryManager instance */
    RecoveryManagerFactory recoveryManagerFactory;

    /** exclusionService factory for getting a default exclusionService instance */
    ExclusionServiceFactory exclusionServiceFactory;

    /** exclusionService factory for getting a default exclusionService instance */
    ErrorReportingServiceFactory errorReportingServiceFactory;

    /** resubmission event factory */
    ResubmissionEventFactory resubmissionEventFactory = new ResubmissionEventFactoryImpl();

    /** allow override of recovery manager instance */
    RecoveryManager recoveryManager;

    /** exclusion service */
    ExclusionService exclusionService;

    /** error reporting service */
    ErrorReportingService errorReportingService;

    /** exception resolver to be registered with recovery manager */
    ExceptionResolver exceptionResolver;

    /** default configuration service which can be overridden */
    ConfigurationService configurationService;

    SerialiserFactory ikasanSerialiserFactory;

    /** List of FlowInvocationListener */
    List<FlowInvocationContextListener> flowInvocationContextListeners;

    /** flow monitor */
    FlowMonitor monitor;

    /** consumer is the only flow element we need a handle on */
    FlowElement<Consumer> consumer;

    /** head flow element of the exclusion flow */
    FlowElement<?> exclusionFlowHeadElement;

    /** handle to the re-submission service */
    ResubmissionService resubmissionService;
    
    /** the replayRecordService **/
    ReplayRecordService replayRecordService;
    
    /** persistent flow configuration */
    FlowPersistentConfiguration flowPersistentConfiguration;

    /** the message history service **/
    MessageHistoryService messageHistoryService;

    /** the flow configuration map to allow for externalised configurations **/
    Map<String, FlowPersistentConfiguration> flowConfigurations;

    /** the flow configuration map to allow for externalised configurations **/
    FlowComponentInvokerSetupServiceConfiguration flowComponentInvokerSetupServiceConfiguration;

	/**
     * Setter for moduleName
     * @param moduleName
     */
    public void setModuleName(String moduleName)
    {
        this.moduleName = moduleName;
    }

    /**
     * Setter for name
     * @param name
     */
    public void setName(String name)
    {
        this.name = name;
    }

    /**
     * Allow override of default flowEventListener
     * @param flowEventListener
     */
    public void setFlowEventListener(FlowEventListener flowEventListener)
    {
        this.flowEventListener = flowEventListener;
    }

    /**
     * Allow override of default recoveryManager
     * @param recoveryManager
     */
    public void setRecoveryManager(RecoveryManager recoveryManager)
    {
        this.recoveryManager = recoveryManager;
    }

    /**
     * Allow override of default exclusionService
     * @param exclusionService
     */
    public void setExclusionService(ExclusionService exclusionService)
    {
        this.exclusionService = exclusionService;
    }

    /**
     * Allow override of default errorReportingService
     * @param errorReportingService
     */
    public void setErrorReportingService(ErrorReportingService errorReportingService)
    {
        this.errorReportingService = errorReportingService;
    }

    /**
     * Allow override of default configurationService
     * @param configurationService
     */
    public void setConfigurationService(ConfigurationService configurationService)
    {
        this.configurationService = configurationService;
    }

    /**
     * Setter for description
     * @param description
     */
    public void setDescription(String description)
    {
        this.description = description;
    }

    /**
     * Setter for consumer flow element
     * @param consumer
     */
    public void setConsumer(FlowElement<Consumer> consumer)
    {
        this.consumer = consumer;
    }

    /**
     * Setter for exclusion flow head flow element
     * @param exclusionFlowHeadElement
     */
    public void setExclusionFlowHeadElement(FlowElement<?> exclusionFlowHeadElement)
    {
        this.exclusionFlowHeadElement = exclusionFlowHeadElement;
    }

    /**
     * Setter for monitor
     * @param monitor
     */
    public void setMonitor(FlowMonitor monitor)
    {
        this.monitor = monitor;
    }

    /**
     * Setter for exception resolver to be registered with the recovery manager.
     * @param exceptionResolver
     */
    public void setExceptionResolver(ExceptionResolver exceptionResolver)
    {
        this.exceptionResolver = exceptionResolver;
    }

    /**
     * @param ikasanSerialiserFactory the ikasanSerialiserFactory to set
     */
    public void setIkasanSerialiserFactory(SerialiserFactory ikasanSerialiserFactory)
    {
        this.ikasanSerialiserFactory = ikasanSerialiserFactory;
    }

    /**
     * @param resubmissionService the resubmissionService to set
     */
    public void setResubmissionService(ResubmissionService resubmissionService)
    {
        this.resubmissionService = resubmissionService;
    }
    
    /**
   	 * @param flowPersistentConfiguration the flowPersistentConfiguration to set
   	 */
   	public void setFlowPersistentConfiguration(FlowPersistentConfiguration flowPersistentConfiguration) 
   	{
   		this.flowPersistentConfiguration = flowPersistentConfiguration;
   	}

    /**
     * set the List of FlowInvocationListener
     * @param flowInvocationContextListeners the list of listeners
     */
    public void setFlowInvocationContextListeners(List<FlowInvocationContextListener> flowInvocationContextListeners)
    {
        this.flowInvocationContextListeners = flowInvocationContextListeners;
    }

    public void setResubmissionEventFactory(ResubmissionEventFactory resubmissionEventFactory)
    {
        this.resubmissionEventFactory = resubmissionEventFactory;
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.beans.factory.FactoryBean#getObject()
     */
    @Override
    public Flow getObject()
    {
        return builderFactory.getFlowBuilder(moduleName, name)
                .withConfigurationService(configurationService)
                .withResubmissionService(resubmissionService)
                .withReplayRecordService(replayRecordService)
                .withExclusionService(exclusionService)
                .withExclusionServiceFactory(exclusionServiceFactory)
                .withErrorReportingService(errorReportingService)
                .withErrorReportingServiceFactory(errorReportingServiceFactory)
                .withRecoveryManager(recoveryManager)
                .withRecoveryManagerFactory(recoveryManagerFactory)
                .withExceptionResolver(exceptionResolver)
                .withExclusionFlowHeadElement(exclusionFlowHeadElement)
                .withFlowListener(flowEventListener)
                .withMessageHistoryService(messageHistoryService)
                .withMonitor(monitor)
                .withSerialiserFactory(ikasanSerialiserFactory)
                .withFlowInvocationContextListeners(flowInvocationContextListeners)
                .withFlowConfigurations(this.flowConfigurations)
                .withFlowComponentInvokerConfiguration(this.flowComponentInvokerSetupServiceConfiguration)
                ._build(consumer);
    }

    /**
     * Determine if a message history context listener is already set.
     * @return
     */
    private boolean messageHistoryContextListenerExist()
    {
        if(this.flowInvocationContextListeners == null)
        {
            return false;
        }

        for(FlowInvocationContextListener listener: this.flowInvocationContextListeners)
        {
            if(listener instanceof MessageHistoryContextListener)
            {
                return true;
            }
        }

        return false;
    }

    /**
     * Helper method to set the exclusion service on the relevant components.
     * 
     * @param flowElements
     * @param exclusionService
     */
    private void injectExclusionService(List<FlowElement<?>> flowElements, ExclusionService exclusionService) 
    {

        for (final FlowElement flowElement : flowElements) 
        {
            if (flowElement.getFlowComponent() instanceof IsExclusionServiceAware) 
            {
                ((IsExclusionServiceAware)flowElement.getFlowComponent()).setExclusionService(exclusionService);
            }
        }
    }
    

    /*
     * (non-Javadoc)
     * @see org.springframework.beans.factory.FactoryBean#getObjectType()
     */
    @Override
    public Class<Flow> getObjectType()
    {
        return Flow.class;
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.beans.factory.FactoryBean#isSingleton()
     */
    @Override
    public boolean isSingleton()
    {
        return false;
    }

    /* (non-Javadoc)
     * @see org.springframework.context.ApplicationContextAware#setApplicationContext(org.springframework.context.ApplicationContext)
     */
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException
    {
        this.builderFactory = applicationContext.getBean(BuilderFactory.class);
        this.configurationService = applicationContext.getBean(ConfigurationService.class);
        this.recoveryManagerFactory = applicationContext.getBean(RecoveryManagerFactory.class);
        this.exclusionServiceFactory = applicationContext.getBean(ExclusionServiceFactory.class);
        this.errorReportingServiceFactory = applicationContext.getBean(ErrorReportingServiceFactory.class);
        this.errorReportingService = applicationContext.getBean(ErrorReportingService.class);
        this.flowEventListener = applicationContext.getBean(FlowEventListener.class);
        this.ikasanSerialiserFactory = applicationContext.getBean(SerialiserFactory.class);
        this.replayRecordService = applicationContext.getBean(ReplayRecordService.class);
        this.messageHistoryService = applicationContext.getBean(MessageHistoryService.class);
        this.flowConfigurations = applicationContext.getBean("flowConfigurations", Map.class);
        this.flowComponentInvokerSetupServiceConfiguration = applicationContext.getBean("flowComponentInvokerConfigurations"
            , FlowComponentInvokerSetupServiceConfiguration.class);
    }

    /**
     * Get the target object of a proxy wrapped class
     * @param proxy
     * @param targetClass
     * @param <T>
     * @return
     */
    protected <T> T getTargetObject(Object proxy, Class<T> targetClass)
    {
        try
        {
            if(AopUtils.isJdkDynamicProxy(proxy))
            {
                return (T) ((Advised)proxy).getTargetSource().getTarget();
            }

            return (T) proxy;
        }
        catch(final Exception e)
        {
            throw new RuntimeException(e);
        }
    }

}