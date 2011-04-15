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
import org.ikasan.flow.configuration.service.ConfigurationManagement;
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
import org.ikasan.spec.flow.Flow;
import org.ikasan.spec.flow.FlowElement;
import org.ikasan.spec.flow.FlowElementInvoker;
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
    /** Spring DI resource */
    @Resource ConfigurationDao staticConfigurationDao;
    
    /** Spring DI resource */
    @Resource ConfigurationDao dynamicConfigurationDao;
    
    /** flow event factory */
    FlowEventFactory flowEventFactory = new FlowEventFactory();
    
    /** configuration service */
    ConfigurationService configurationService;
    
    /** configuration management for the scheduled consumer */
    ConfigurationManagement<Consumer,ScheduledConsumerConfiguration> configurationManagement;
    
    /** recovery manager */
    ScheduledRecoveryManagerFactory scheduledRecoveryManagerFactory;
    
    @Before
    public void setup() throws SchedulerException
    {
        this.scheduledRecoveryManagerFactory  = 
            new ScheduledRecoveryManagerFactory(StdSchedulerFactory.getDefaultScheduler());
        
        configurationService = new ConfiguredResourceConfigurationService(staticConfigurationDao, dynamicConfigurationDao);;
        configurationManagement = (ConfigurationManagement<Consumer,ScheduledConsumerConfiguration>)configurationService;
    }

    @Test
    public void test_flow_consumer_translator_producer() throws SchedulerException
    {
        ScheduledConsumerJobFactory scheduledConsumerJobFactory = ScheduledConsumerJobFactory.getInstance();
        
        Producer producer = new PayloadProducer();
        FlowElement producerFlowElement = new FlowElementImpl("payloadProducer", producer);

        Converter priceToStringBuilderConverter = new ScheduleEventConverter();
        FlowElement<Converter> converterFlowElement = new FlowElementImpl("priceToStringBuilder", priceToStringBuilderConverter, producerFlowElement);

        // get the consumer
        
        Consumer consumer = createConsumer();
        FlowElement<Consumer> consumerFlowElement = new FlowElementImpl("scheduleDrivenConsumer", consumer, converterFlowElement);
        

        // flow configuration wiring
        FlowConfiguration flowConfiguration = new DefaultFlowConfiguration(consumerFlowElement, configurationService);

        // iterator over each flow element
        FlowElementInvoker flowElementInvoker = new VisitingFlowElementInvoker();

        RecoveryManager recoveryManager = scheduledRecoveryManagerFactory.getRecoveryManager("flowName", "moduleName", consumer);
        
        // container for the complete flow
        Flow flow = new VisitingInvokerFlow("flowName", "moduleName", 
            flowConfiguration, flowElementInvoker, recoveryManager);
        
        flow.start();

        flow.stop();
    }

    protected Consumer createConsumer()
    {
        try
        {
            // create consumer component
            Consumer consumer = new ScheduledConsumer(StdSchedulerFactory.getDefaultScheduler(), flowEventFactory);
            
            // create configuration and configure
            ScheduledConsumerConfiguration configuration = configurationManagement.createConfiguration(consumer);
            configuration.setJobName("priceSrcFlow");
            configuration.setJobName("priceSrcModule");
            configuration.setCronExpression("0/5 * * * * ?");
            configurationManagement.saveConfiguration(configuration);
            
            return consumer;
        }
        catch (SchedulerException e)
        {
            throw new RuntimeException(e);
        }

    }
}
