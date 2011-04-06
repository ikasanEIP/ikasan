/*
 * $Id$
 * $URL$
 *
 * ====================================================================
 *
 * Copyright (c) 2000-20010 by Mizuho International plc.
 * All Rights Reserved.
 *
 * ====================================================================
 *
 */
package org.ikasan.sample.priceSrc.flow;

import java.util.ArrayList;
import java.util.List;

import org.hamcrest.core.IsInstanceOf;
import org.ikasan.exceptionHandler.ExceptionGroup;
import org.ikasan.exceptionHandler.ExceptionHandler;
import org.ikasan.exceptionHandler.MatchingExceptionHandler;
import org.ikasan.exceptionHandler.action.ExceptionAction;
import org.ikasan.exceptionHandler.action.RetryAction;
import org.ikasan.exceptionHandler.action.StopAction;
import org.ikasan.exceptionHandler.matcher.MatcherBasedExceptionGroup;
import org.ikasan.flow.configuration.dao.ConfigurationDao;
import org.ikasan.flow.configuration.dao.ConfigurationHibernateImpl;
import org.ikasan.flow.configuration.service.ConfiguredResourceConfigurationService;
import org.ikasan.flow.event.FlowEventFactory;
import org.ikasan.flow.visitorPattern.DefaultFlowConfiguration;
import org.ikasan.flow.visitorPattern.FlowConfiguration;
import org.ikasan.flow.visitorPattern.FlowElementImpl;
import org.ikasan.flow.visitorPattern.VisitingFlowElementInvoker;
import org.ikasan.flow.visitorPattern.VisitingInvokerFlow;
import org.ikasan.recoveryManager.ScheduledRecoveryManager;
import org.ikasan.sample.priceSrc.component.convertor.PriceConverter;
import org.ikasan.sample.priceSrc.component.endpoint.PriceConsumer;
import org.ikasan.sample.priceSrc.component.endpoint.PriceProducer;
import org.ikasan.sample.priceSrc.tech.PriceTechImpl;
import org.ikasan.spec.component.endpoint.Consumer;
import org.ikasan.spec.component.endpoint.Producer;
import org.ikasan.spec.component.transformation.Converter;
import org.ikasan.spec.configuration.service.ConfigurationService;
import org.ikasan.spec.flow.Flow;
import org.ikasan.spec.flow.FlowElement;
import org.ikasan.spec.flow.FlowElementInvoker;
import org.ikasan.spec.recoveryManager.RecoveryManager;
import org.junit.Test;
import org.quartz.SchedulerException;
import org.quartz.impl.StdSchedulerFactory;

/**
 * Pure Java based sample of Ikasan EIP for sourcing prices from a tech endpoint.
 * 
 * @author Ikasan Development Team
 */
public class PriceFlowSample
{
    protected PriceTechImpl getTechImpl()
    {
        return new PriceTechImpl();
    }
    
    protected FlowEventFactory getEventFactory()
    {
        return new FlowEventFactory();
    }
    
    protected ConfigurationService getConfigurationService()
    {
        ConfigurationDao configurationDao = new ConfigurationHibernateImpl();
        return new ConfiguredResourceConfigurationService(configurationDao, configurationDao);
    }
    
    protected ExceptionHandler getExceptionHandler()
    {
        // flow exception handler
        int delay = 1000;
        int retries = 10;
        ExceptionAction stopAction = StopAction.instance();
          
        IsInstanceOf instanceOfException = new org.hamcrest.core.IsInstanceOf(Exception.class);
        MatcherBasedExceptionGroup matcher = new MatcherBasedExceptionGroup(instanceOfException, stopAction);
          
        List<ExceptionGroup> matchers = new ArrayList<ExceptionGroup>();
        matchers.add(matcher);
          
        ExceptionHandler exceptionHandler = new MatchingExceptionHandler(matchers);

        return exceptionHandler;
    }
    
    protected RecoveryManager getRecoveryManager(FlowElement<Consumer> consumerFlowElement) throws SchedulerException
    {
        return new ScheduledRecoveryManager(consumerFlowElement.getFlowComponent(), consumerFlowElement.getComponentName(),
            getExceptionHandler(), StdSchedulerFactory.getDefaultScheduler());
    }
    
    @Test
    public void test_flow_consumer_translator_producer() throws SchedulerException
    {
        Producer producer = new PriceProducer();
        FlowElement producerFlowElement = new FlowElementImpl("priceProducer", producer);

        Converter priceToStringBuilderConverter = new PriceConverter();
        FlowElement<Converter> converterFlowElement = new FlowElementImpl("priceToStringBuilder", priceToStringBuilderConverter, producerFlowElement);

        Consumer consumer = new PriceConsumer(getTechImpl(), getEventFactory());
        FlowElement<Consumer> consumerFlowElement = new FlowElementImpl("priceConsumer", consumer, converterFlowElement);

        // flow configuration wiring
        FlowConfiguration flowConfiguration = new DefaultFlowConfiguration(consumerFlowElement, getConfigurationService());

        // iterator over each flow element
        FlowElementInvoker flowElementInvoker = new VisitingFlowElementInvoker();

        // container for the complete flow
        Flow flow = new VisitingInvokerFlow("flowName", "moduleName", 
            flowConfiguration, flowElementInvoker, getRecoveryManager(consumerFlowElement));
        
        flow.start();

        flow.stop();
    }

}
