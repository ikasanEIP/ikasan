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
package org.ikasan.sample.scheduleDrivenPriceSrc.integrationTest;

import javax.annotation.Resource;

import org.ikasan.consumer.quartz.ScheduledConsumer;
import org.ikasan.consumer.quartz.ScheduledConsumerConfiguration;
import org.ikasan.consumer.quartz.ScheduledConsumerJobFactory;
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
import org.ikasan.sample.scheduleDrivenPriceSrc.component.converter.ScheduleEventConverter;
import org.ikasan.sample.scheduleDrivenPriceSrc.component.endpoint.PayloadProducer;
import org.ikasan.spec.component.endpoint.Consumer;
import org.ikasan.spec.component.endpoint.Producer;
import org.ikasan.spec.component.transformation.Converter;
import org.ikasan.spec.configuration.ConfiguredResource;
import org.ikasan.spec.event.EventFactory;
import org.ikasan.spec.flow.Flow;
import org.ikasan.spec.flow.FlowElement;
import org.ikasan.spec.flow.FlowElementInvoker;
import org.ikasan.spec.flow.FlowEvent;
import org.ikasan.spec.recovery.RecoveryManager;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.quartz.SchedulerException;
import org.quartz.impl.StdSchedulerFactory;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * 
 * @author Ikasan Development Team
 */
@RunWith(SpringJUnit4ClassRunner.class)
//specifies the Spring configuration to load for this test fixture
@ContextConfiguration(locations={
      "/hsqldb-config.xml"})
      
public class ScheduleDrivenPriceSrcFlowSample
{
    @Resource ConfigurationDao staticConfigurationDao;
    
    @Resource ConfigurationDao dynamicConfigurationDao;
    
    FlowEventFactory flowEventFactory = new FlowEventFactory();
    ScheduledRecoveryManagerFactory scheduledRecoveryManagerFactory;
    
    protected EventFactory<FlowEvent<?,?>> getEventFactory()
    {
        return new FlowEventFactory();
    }
    
    protected ConfigurationService getConfigurationService()
    {
        return new ConfiguredResourceConfigurationService(staticConfigurationDao, dynamicConfigurationDao);
    }
    
    @Before
    public void setup() throws SchedulerException
    {
        this.scheduledRecoveryManagerFactory  = 
            new ScheduledRecoveryManagerFactory(StdSchedulerFactory.getDefaultScheduler());
        
        ConfigurationService cr = this.getConfigurationService();
        cr.
    }

    @Test
    public void test_flow_consumer_translator_producer() throws SchedulerException
    {
        ScheduledConsumerJobFactory scheduledConsumerJobFactory = ScheduledConsumerJobFactory.getInstance();
        
        Producer producer = new PayloadProducer();
        FlowElement producerFlowElement = new FlowElementImpl("payloadProducer", producer);

        Converter priceToStringBuilderConverter = new ScheduleEventConverter();
        FlowElement<Converter> converterFlowElement = new FlowElementImpl("priceToStringBuilder", priceToStringBuilderConverter, producerFlowElement);

        Consumer consumer = new ScheduledConsumer(StdSchedulerFactory.getDefaultScheduler(), getEventFactory());
        ScheduledConsumerConfiguration scheduledConsumerConfiguration = new ScheduledConsumerConfiguration();
        scheduledConsumerConfiguration.setJobName("priceSrcFlow");
        scheduledConsumerConfiguration.setJobName("priceSrcModule");
        scheduledConsumerConfiguration.setCronExpression("0/5 * * * * ?");
        ((ConfiguredResource)consumer).setConfiguration(scheduledConsumerConfiguration);
        ((ConfiguredResource)consumer).setConfiguredResourceId("scheduleDrivenConsumer");

        FlowElement<Consumer> consumerFlowElement = new FlowElementImpl("scheduleDrivenConsumer", consumer, converterFlowElement);

        // flow configuration wiring
        FlowConfiguration flowConfiguration = new DefaultFlowConfiguration(consumerFlowElement, getConfigurationService());

        // iterator over each flow element
        FlowElementInvoker flowElementInvoker = new VisitingFlowElementInvoker();

        RecoveryManager recoveryManager = scheduledRecoveryManagerFactory.getRecoveryManager("flowName", "moduleName", consumer);
        
        // container for the complete flow
        Flow flow = new VisitingInvokerFlow("flowName", "moduleName", 
            flowConfiguration, flowElementInvoker, recoveryManager);
        
        flow.start();

        flow.stop();
    }

}
