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
package org.ikasan.sample.genericTechDrivenPriceSrc.integrationTest;

import org.ikasan.flow.configuration.dao.ConfigurationDao;
import org.ikasan.flow.configuration.dao.ConfigurationHibernateImpl;
import org.ikasan.flow.configuration.service.ConfigurationService;
import org.ikasan.flow.configuration.service.ConfiguredResourceConfigurationService;
import org.ikasan.flow.event.FlowEventFactory;
import org.ikasan.flow.visitorPattern.DefaultFlowConfiguration;
import org.ikasan.flow.visitorPattern.FlowConfiguration;
import org.ikasan.flow.visitorPattern.FlowElementImpl;
import org.ikasan.flow.visitorPattern.VisitingFlowElementInvoker;
import org.ikasan.flow.visitorPattern.VisitingInvokerFlow;
import org.ikasan.recovery.ScheduledRecoveryManagerFactory;
import org.ikasan.sample.genericTechDrivenPriceSrc.component.converter.PriceConverter;
import org.ikasan.sample.genericTechDrivenPriceSrc.component.endpoint.PriceConsumer;
import org.ikasan.sample.genericTechDrivenPriceSrc.component.endpoint.PriceProducer;
import org.ikasan.sample.genericTechDrivenPriceSrc.flow.PriceFlowFactory;
import org.ikasan.sample.genericTechDrivenPriceSrc.tech.PriceTechImpl;
import org.ikasan.spec.component.endpoint.Consumer;
import org.ikasan.spec.component.endpoint.Producer;
import org.ikasan.spec.component.transformation.Converter;
import org.ikasan.spec.event.EventFactory;
import org.ikasan.spec.flow.Flow;
import org.ikasan.spec.flow.FlowElement;
import org.ikasan.spec.flow.FlowElementInvoker;
import org.ikasan.spec.flow.FlowEvent;
import org.ikasan.spec.recovery.RecoveryManager;
import org.junit.Before;
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
    FlowEventFactory flowEventFactory = new FlowEventFactory();
    ScheduledRecoveryManagerFactory scheduledRecoveryManagerFactory;
    
    protected PriceTechImpl getTechImpl()
    {
        return new PriceTechImpl();
    }
    
    protected EventFactory<FlowEvent<?,?>> getEventFactory()
    {
        return new FlowEventFactory();
    }
    
    protected ConfigurationService getConfigurationService()
    {
        ConfigurationDao configurationDao = new ConfigurationHibernateImpl();
        return new ConfiguredResourceConfigurationService(configurationDao, configurationDao);
    }
    
//    protected ExceptionHandler getExceptionHandler()
//    {
//        // flow exception handler
//        int delay = 1000;
//        int retries = 10;
//        ExceptionAction stopAction = StopAction.instance();
//          
//        IsInstanceOf instanceOfException = new org.hamcrest.core.IsInstanceOf(Exception.class);
//        MatcherBasedExceptionGroup matcher = new MatcherBasedExceptionGroup(instanceOfException, stopAction);
//          
//        List<ExceptionGroup> matchers = new ArrayList<ExceptionGroup>();
//        matchers.add(matcher);
//          
//        ExceptionHandler exceptionHandler = new MatchingExceptionHandler(matchers);
//
//        return exceptionHandler;
//    }
    
    @Before
    public void setup() throws SchedulerException
    {
        this.scheduledRecoveryManagerFactory  = 
            new ScheduledRecoveryManagerFactory(StdSchedulerFactory.getDefaultScheduler());
    }

    @Test
    public void test_flow_consumer_translator_producer() throws SchedulerException
    {
        PriceFlowFactory priceFlowFactory = new PriceFlowFactory("flowName", "moduleName", configurationService);
        Flow priceFlow = priceFlowFactory.createGenericTechDrivenFlow();
        priceFlow.start();
        priceFlow.stop();
    }

}
