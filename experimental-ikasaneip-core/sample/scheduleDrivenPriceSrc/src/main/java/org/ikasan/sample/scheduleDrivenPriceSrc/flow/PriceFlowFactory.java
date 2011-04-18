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
package org.ikasan.sample.scheduleDrivenPriceSrc.flow;

import org.ikasan.consumer.quartz.ScheduledConsumerConfiguration;
import org.ikasan.consumer.quartz.ScheduledConsumerFactory;
import org.ikasan.flow.configuration.model.Configuration;
import org.ikasan.flow.configuration.service.ConfigurationManagement;
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
import org.ikasan.scheduler.SchedulerFactory;
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

/**
 * 
 * @author Ikasan Development Team
 */
public class PriceFlowFactory
{
    String flowName;
    String moduleName;
    ConfigurationService configurationService;
    ConfigurationManagement<Consumer<?>,Configuration> configurationManagement;
    EventFactory<FlowEvent<?,?>> flowEventFactory = new FlowEventFactory();
    ScheduledConsumerFactory scheduledConsumerFactory;
    ScheduledRecoveryManagerFactory scheduledRecoveryManagerFactory;

    public PriceFlowFactory(String flowName, String moduleName, 
            ConfigurationService configurationService, ConfigurationManagement configurationManagement) 
    {
        this.flowName = flowName;
        this.moduleName = moduleName;
        this.configurationService = configurationService;
        this.configurationManagement = configurationManagement;
        
        this.scheduledConsumerFactory  = 
            new ScheduledConsumerFactory(SchedulerFactory.getInstance().getScheduler(), flowEventFactory);
        this.scheduledRecoveryManagerFactory  = 
            new ScheduledRecoveryManagerFactory(SchedulerFactory.getInstance().getScheduler());
    }
    
    public Flow createScheduleDrivenFlow()
    {
        Producer producer = new PayloadProducer();
        FlowElement producerFlowElement = new FlowElementImpl("payloadProducer", producer);

        Converter priceToStringBuilderConverter = new ScheduleEventConverter();
        FlowElement<Converter> converterFlowElement = new FlowElementImpl("priceToStringBuilder", priceToStringBuilderConverter, producerFlowElement);

        Consumer consumer = createConsumer("scheduleDrivenConsumerId");
        FlowElement<Consumer> consumerFlowElement = new FlowElementImpl("scheduleDrivenConsumer", consumer, converterFlowElement);

        // flow configuration wiring
        FlowConfiguration flowConfiguration = new DefaultFlowConfiguration(consumerFlowElement, this.configurationService);

        // iterator over each flow element
        FlowElementInvoker flowElementInvoker = new VisitingFlowElementInvoker();

        RecoveryManager recoveryManager = scheduledRecoveryManagerFactory.getRecoveryManager(flowName, moduleName, consumer);
        
        // container for the complete flow
        return new VisitingInvokerFlow(flowName, moduleName, flowConfiguration, flowElementInvoker, recoveryManager);
    }

    protected Consumer<?> createConsumer(String configuredResourceId)
    {
        ScheduledConsumerConfiguration configuration = new ScheduledConsumerConfiguration();
        configuration.setCronExpression("0/5 * * * * ?");
        
        // create consumer component
        Consumer<?> consumer = this.scheduledConsumerFactory.getScheduledConsumer(flowName, moduleName);

        ConfiguredResource configuredResource = (ConfiguredResource)consumer;
        configuredResource.setConfiguredResourceId(configuredResourceId);
        configuredResource.setConfiguration(configuration);
        configurationManagement.saveConfiguration( configurationManagement.createConfiguration(consumer) );
  
        return consumer;
    }
}
