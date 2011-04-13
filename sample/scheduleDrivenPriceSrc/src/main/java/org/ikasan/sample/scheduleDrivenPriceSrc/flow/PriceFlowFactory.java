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
package org.ikasan.sample.scheduleDrivenPriceSrc.flow;

import org.ikasan.consumer.quartz.ScheduledConsumer;
import org.ikasan.consumer.quartz.ScheduledConsumerConfiguration;
import org.ikasan.consumer.quartz.ScheduledConsumerJobFactory;
import org.ikasan.flow.configuration.service.ConfigurationService;
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
import org.quartz.SchedulerException;
import org.quartz.impl.StdSchedulerFactory;

/**
 * 
 * @author Ikasan Development Team
 */
public class PriceFlowFactory
{
    String flowName;
    String moduleName;
    ConfigurationService configurationService;
    EventFactory<FlowEvent<?,?>> flowEventFactory = new FlowEventFactory();
    ScheduledRecoveryManagerFactory scheduledRecoveryManagerFactory;

    public PriceFlowFactory(String flowName, String moduleName, ConfigurationService configurationService) 
    {
        this.flowName = flowName;
        this.moduleName = moduleName;
        this.configurationService = configurationService;
        
        try
        {
            this.scheduledRecoveryManagerFactory  = 
                new ScheduledRecoveryManagerFactory(StdSchedulerFactory.getDefaultScheduler());
        }
        catch (SchedulerException e)
        {
            throw new RuntimeException(e);
        }

    }
    
    public Flow createScheduleDrivenFlow()
    {
        ScheduledConsumerJobFactory scheduledConsumerJobFactory = ScheduledConsumerJobFactory.getInstance();
        
        Producer producer = new PayloadProducer();
        FlowElement producerFlowElement = new FlowElementImpl("payloadProducer", producer);

        Converter priceToStringBuilderConverter = new ScheduleEventConverter();
        FlowElement<Converter> converterFlowElement = new FlowElementImpl("priceToStringBuilder", priceToStringBuilderConverter, producerFlowElement);

        Consumer consumer;
        try
        {
            consumer = new ScheduledConsumer(StdSchedulerFactory.getDefaultScheduler(), this.flowEventFactory);
        }
        catch (SchedulerException e)
        {
            throw new RuntimeException(e);
        }
        ScheduledConsumerConfiguration scheduledConsumerConfiguration = new ScheduledConsumerConfiguration();
        scheduledConsumerConfiguration.setJobName(flowName);
        scheduledConsumerConfiguration.setJobName(moduleName);
        scheduledConsumerConfiguration.setCronExpression("0/5 * * * * ?");
        ((ConfiguredResource)consumer).setConfiguration(scheduledConsumerConfiguration);
        ((ConfiguredResource)consumer).setConfiguredResourceId("scheduleDrivenConsumer");

        FlowElement<Consumer> consumerFlowElement = new FlowElementImpl("scheduleDrivenConsumer", consumer, converterFlowElement);

        // flow configuration wiring
        FlowConfiguration flowConfiguration = new DefaultFlowConfiguration(consumerFlowElement, this.configurationService);

        // iterator over each flow element
        FlowElementInvoker flowElementInvoker = new VisitingFlowElementInvoker();

        RecoveryManager recoveryManager = scheduledRecoveryManagerFactory.getRecoveryManager(flowName, moduleName, consumer);
        
        // container for the complete flow
        return new VisitingInvokerFlow(flowName, moduleName, flowConfiguration, flowElementInvoker, recoveryManager);
    }

}
