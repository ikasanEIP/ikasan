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

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.ikasan.error.reporting.service.ErrorReportingServiceDefaultImpl;
import org.ikasan.exceptionResolver.ExceptionResolver;
import org.ikasan.exclusion.service.ExclusionServiceFactory;
import org.ikasan.exclusion.service.IsExclusionServiceAware;
import org.ikasan.flow.visitorPattern.*;
import org.ikasan.recovery.RecoveryManagerFactory;
import org.ikasan.spec.component.endpoint.Consumer;
import org.ikasan.spec.configuration.ConfigurationService;
import org.ikasan.spec.error.reporting.ErrorReportingService;
import org.ikasan.spec.error.reporting.ErrorReportingServiceFactory;
import org.ikasan.spec.error.reporting.IsErrorReportingServiceAware;
import org.ikasan.spec.exclusion.ExclusionService;
import org.ikasan.spec.flow.Flow;
import org.ikasan.spec.flow.FlowConfiguration;
import org.ikasan.spec.flow.FlowElement;
import org.ikasan.spec.flow.FlowEventListener;
import org.ikasan.spec.monitor.Monitor;
import org.ikasan.spec.monitor.MonitorSubject;
import org.ikasan.spec.recovery.RecoveryManager;
import org.ikasan.spec.resubmission.ResubmissionService;
import org.ikasan.spec.serialiser.SerialiserFactory;
import org.springframework.aop.framework.Advised;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.ikasan.spec.flow.FlowEventListener;

/**
 * Spring based FactoryBean for the creation of Ikasan Flows.
 * @author Ikasan Development Team
 * 
 */
public class FlowFactory implements FactoryBean<Flow>, ApplicationContextAware
{
    /** logger */
    private static Logger logger = Logger.getLogger(FlowFactory.class);
    
    /** name of the flow's module owner */
    String moduleName;

    /** name of the flow being instantiated */
    String name;

    /** optional module description */
    String description;

    /** default flow event listener which can be overridden */
    @Resource
    FlowEventListener flowEventListener;

    /** recovery manager factory for getting a default recoveryManager instance */
    @Resource
    RecoveryManagerFactory recoveryManagerFactory;

    /** exclusionService factory for getting a default exclusionService instance */
    @Resource
    ExclusionServiceFactory exclusionServiceFactory;

    /** exclusionService factory for getting a default exclusionService instance */
    @Resource
    ErrorReportingServiceFactory errorReportingServiceFactory;

    /** allow override of recovery manager instance */
    RecoveryManager recoveryManager;

    /** exclusion service */
    ExclusionService exclusionService;

    /** error reporting service */
    ErrorReportingService errorReportingService;

    /** exception resolver to be registered with recovery manager */
    ExceptionResolver exceptionResolver;

    /** default configuration service which can be overridden */
    @Resource
    ConfigurationService configurationService;
    
    @Resource
    SerialiserFactory ikasanSerialiserFactory;

    /** flow monitor */
    Monitor monitor;

    /** consumer is the only flow element we need a handle on */
    FlowElement<Consumer> consumer;

    /** head flow element of the exclusion flow */
    FlowElement<?> exclusionFlowHeadElement;
    
    /** handle to the re-submission service */
    ResubmissionService resubmissionService;

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
    public void setMonitor(Monitor monitor)
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

    /*
     * (non-Javadoc)
     * @see org.springframework.beans.factory.FactoryBean#getObject()
     */
    public Flow getObject()
    {
        FlowConfiguration flowConfiguration = new DefaultFlowConfiguration(consumer, configurationService, resubmissionService);

        if(exclusionService == null)
        {
            if(this.exclusionServiceFactory == null)
            {
                throw new IllegalArgumentException("exclusionServiceFactory cannot be 'null'");
            }

            exclusionService = this.exclusionServiceFactory.getExclusionService(moduleName, name);
        }

        if(errorReportingService == null)
        {
            errorReportingService = this.errorReportingServiceFactory.getErrorReportingService();
        }

        ErrorReportingService proxiedTarget = this.getTargetObject(errorReportingService, ErrorReportingService.class);
        if(proxiedTarget instanceof ErrorReportingServiceDefaultImpl)
        {
            ((ErrorReportingServiceDefaultImpl)proxiedTarget).setModuleName(moduleName);
            ((ErrorReportingServiceDefaultImpl)proxiedTarget).setFlowName(name);
        }

        if(recoveryManager == null)
        {
            recoveryManager = recoveryManagerFactory.getRecoveryManager(name,  moduleName,  consumer.getFlowComponent(), exclusionService, errorReportingService);
        }

        if(exceptionResolver != null)
        {
            recoveryManager.setResolver(exceptionResolver);
        }

        ExclusionFlowConfiguration exclusionFlowConfiguration = null;
        if(exclusionFlowHeadElement != null)
        {
            exclusionFlowConfiguration = new DefaultExclusionFlowConfiguration(this.exclusionFlowHeadElement, configurationService, resubmissionService);
        }

        Flow flow = new VisitingInvokerFlow(name, moduleName, flowConfiguration, exclusionFlowConfiguration, recoveryManager, exclusionService, ikasanSerialiserFactory);
        flow.setFlowListener(flowEventListener);

        // pass handle to the error reporting service if flow needs to be aware of this
        if(flow instanceof IsErrorReportingServiceAware)
        {
            ((IsErrorReportingServiceAware)flow).setErrorReportingService(errorReportingService);
        }

        // pass handle to the exclusion service to the excluded event flow if this is needed
        injectExclusionService(exclusionFlowConfiguration, exclusionService);

        if(monitor != null && flow instanceof MonitorSubject)
        {
            if(monitor.getEnvironment() == null)
            {
                monitor.setEnvironment("Undefined Environment");
            }

            if(monitor.getModuleName() == null)
            {
                monitor.setModuleName(moduleName);
            }
            
            if(monitor.getFlowName() == null)
            {
                monitor.setFlowName(name);
            }

            ((MonitorSubject)flow).setMonitor(monitor);
        }
        
        logger.info("Instantiated flow - name[" + name + "] module[" + moduleName
            + "] with ExclusionService[" + exclusionService.getClass().getSimpleName()
            + "] with ErrorReportingService[" + errorReportingService.getClass().getSimpleName()
            + "] with RecoveryManager[" + recoveryManager.getClass().getSimpleName()
            + "]; ExceptionResolver[" + ((exceptionResolver != null) ? exceptionResolver.getClass().getSimpleName() : "none")
            + "]; Monitor[" + ((monitor != null && flow instanceof MonitorSubject) ? monitor.getClass().getSimpleName() : "none")
            + "]");
        
        return flow;
    }

    private void injectExclusionService(ExclusionFlowConfiguration flow, ExclusionService exclusionService) {

        // The exclusion flow is optionally configured so may be null
        if (flow == null) {
            return;
        }

        for (FlowElement flowElement : flow.getFlowElements()) {
            if (flowElement.getFlowComponent() instanceof IsExclusionServiceAware) {
                ((IsExclusionServiceAware)flowElement.getFlowComponent()).setExclusionService(exclusionService);
            }
        }
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.beans.factory.FactoryBean#getObjectType()
     */
    public Class<Flow> getObjectType()
    {
        return Flow.class;
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.beans.factory.FactoryBean#isSingleton()
     */
    public boolean isSingleton()
    {
        return false;
    }

    /* (non-Javadoc)
     * @see org.springframework.context.ApplicationContextAware#setApplicationContext(org.springframework.context.ApplicationContext)
     */
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException
    {
        this.configurationService = applicationContext.getBean(ConfigurationService.class);
        this.recoveryManagerFactory = applicationContext.getBean(RecoveryManagerFactory.class);
        this.exclusionServiceFactory = applicationContext.getBean(ExclusionServiceFactory.class);
        this.errorReportingServiceFactory = applicationContext.getBean(ErrorReportingServiceFactory.class);
        this.errorReportingService = applicationContext.getBean(ErrorReportingService.class);
        this.flowEventListener = applicationContext.getBean(FlowEventListener.class);
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
        catch(Exception e)
        {
            throw new RuntimeException(e);
        }
    }

}