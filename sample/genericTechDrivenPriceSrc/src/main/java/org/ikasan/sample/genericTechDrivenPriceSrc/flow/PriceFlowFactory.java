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
