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
package org.ikasan.sample.genericTechDrivenPriceSrc.flow;

import org.ikasan.flow.configuration.service.ConfigurationService;
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
import org.ikasan.sample.genericTechDrivenPriceSrc.tech.PriceTechImpl;
import org.ikasan.spec.component.endpoint.Consumer;
import org.ikasan.spec.component.endpoint.Producer;
import org.ikasan.spec.component.transformation.Converter;
import org.ikasan.spec.flow.Flow;
import org.ikasan.spec.flow.FlowElement;
import org.ikasan.spec.flow.FlowElementInvoker;
import org.ikasan.spec.recovery.RecoveryManager;
import org.quartz.SchedulerException;
import org.quartz.impl.StdSchedulerFactory;

/**
 * Pure Java based sample of Ikasan EIP for sourcing prices from a tech endpoint.
 * 
 * @author Ikasan Development Team
 */
public class PriceFlowFactory
{
    String flowName;
    String moduleName;
    ConfigurationService configurationService;
    FlowEventFactory flowEventFactory = new FlowEventFactory();
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
    
    public Flow createGenericTechDrivenFlow()
    {
        Producer producer = new PriceProducer();
        FlowElement producerFlowElement = new FlowElementImpl("priceProducer", producer);

        Converter priceToStringBuilderConverter = new PriceConverter();
        FlowElement<Converter> converterFlowElement = new FlowElementImpl("priceToStringBuilder", priceToStringBuilderConverter, producerFlowElement);

        Consumer consumer = new PriceConsumer(getTechImpl(), this.flowEventFactory);
        FlowElement<Consumer> consumerFlowElement = new FlowElementImpl("priceConsumer", consumer, converterFlowElement);

        // flow configuration wiring
        FlowConfiguration flowConfiguration = new DefaultFlowConfiguration(consumerFlowElement, this.configurationService);

        // iterator over each flow element
        FlowElementInvoker flowElementInvoker = new VisitingFlowElementInvoker();

        RecoveryManager recoveryManager = scheduledRecoveryManagerFactory.getRecoveryManager("flowName", "moduleName", consumer);
        
        // container for the complete flow
        return new VisitingInvokerFlow(flowName, moduleName, flowConfiguration, flowElementInvoker, recoveryManager);
    }

    /**
     * Stubbed tech implementation
     * @return PriceTechImpl
     */
    protected PriceTechImpl getTechImpl()
    {
        return new PriceTechImpl();
    }
    
}
