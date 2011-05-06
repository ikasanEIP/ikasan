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
package org.ikasan.sample.genericTechDrivenPriceSrc.integrationTest;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.ikasan.consumer.quartz.ScheduledConsumerConfiguration;
import org.ikasan.flow.configuration.dao.ConfigurationDao;
import org.ikasan.flow.configuration.service.ConfiguredResourceConfigurationService;
import org.ikasan.flow.event.DefaultReplicationFactory;
import org.ikasan.flow.event.FlowEventFactory;
import org.ikasan.recovery.RecoveryManagerFactory;
import org.ikasan.sample.genericTechDrivenPriceSrc.flow.PriceFlowFactory;
import org.ikasan.sample.genericTechDrivenPriceSrc.integrationTest.comparator.ConsumerEventComparator;
import org.ikasan.sample.genericTechDrivenPriceSrc.integrationTest.comparator.ConverterEventComparator;
import org.ikasan.sample.genericTechDrivenPriceSrc.integrationTest.comparator.ProducerEventComparator;
import org.ikasan.sample.genericTechDrivenPriceSrc.tech.PriceTechImpl;
import org.ikasan.sample.genericTechDrivenPriceSrc.tech.PriceTechMessage;
import org.ikasan.scheduler.SchedulerFactory;
import org.ikasan.spec.component.endpoint.Consumer;
import org.ikasan.spec.configuration.ConfigurationManagement;
import org.ikasan.spec.configuration.ConfigurationService;
import org.ikasan.spec.event.ReplicationFactory;
import org.ikasan.spec.flow.Flow;
import org.ikasan.testharness.flow.FlowObserver;
import org.ikasan.testharness.flow.FlowTestHarness;
import org.ikasan.testharness.flow.FlowTestHarnessImpl;
import org.ikasan.testharness.flow.expectation.model.ConsumerComponent;
import org.ikasan.testharness.flow.expectation.model.ConverterComponent;
import org.ikasan.testharness.flow.expectation.model.ProducerComponent;
import org.ikasan.testharness.flow.expectation.service.OrderedExpectation;
import org.ikasan.testharness.flow.listener.FlowEventListenerSubject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.quartz.SchedulerException;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Pure Java based sample of Ikasan EIP for sourcing prices from a tech endpoint.
 * 
 * @author Ikasan Development Team
 */
@RunWith(SpringJUnit4ClassRunner.class)
//specifies the Spring configuration to load for this test fixture
@ContextConfiguration(locations={
        "/configuration-dao-config.xml", 
        "/hsqldb-config.xml"})

public class PriceFlowSampleTest
{
    /** Spring DI resource */
    @Resource ConfigurationDao staticConfigurationDao;
    
    /** Spring DI resource */
    @Resource ConfigurationDao dynamicConfigurationDao;
    
    /** flow event factory */
    FlowEventFactory flowEventFactory = new FlowEventFactory();

    /** recovery manager factory */
    RecoveryManagerFactory recoveryManagerFactory;
    
    /** configuration service */
    ConfigurationService configurationService;
    
    /** configuration management for the scheduled consumer */
    ConfigurationManagement<Consumer,ScheduledConsumerConfiguration> configurationManagement;
    
    /** Consumer specific event comparator */
    ConsumerEventComparator consumerEventComparator;
    
    /** Converter specific event comparator */
    ConverterEventComparator converterEventComparator;
    
    /** Producer specific event comparator */
    ProducerEventComparator producerEventComparator;
    
    /** Captures the actual components invoked and events created within the flow */
    FlowEventListenerSubject flowEventListenerSubject;

    ReplicationFactory replicationFactory;
    
    @Before
    public void setup() throws SchedulerException
    {
        this.recoveryManagerFactory  = new RecoveryManagerFactory(SchedulerFactory.getInstance().getScheduler());
        this.configurationService = new ConfiguredResourceConfigurationService(staticConfigurationDao, dynamicConfigurationDao);
        this.configurationManagement = (ConfigurationManagement<Consumer,ScheduledConsumerConfiguration>)configurationService;
        
        // event listener subject
        flowEventListenerSubject = new FlowEventListenerSubject( DefaultReplicationFactory.getInstance() );
        
        // create the test comparators
        this.consumerEventComparator = new ConsumerEventComparator();
        this.converterEventComparator = new ConverterEventComparator();
        this.producerEventComparator = new ProducerEventComparator();
    }

    @Test
    public void test_flow_consumer_translator_producer() throws SchedulerException
    {
        final PriceTechMessage priceTechConsumerMessage = new PriceTechMessage("abc", 10, 10);
        final StringBuilder priceTechConverterMessage = new StringBuilder("identifier = abc bid = 10 spread = 10 at ");
        final StringBuilder priceTechProducerMessage = new StringBuilder("identifier = abc bid = 10 spread = 10 at ");

        // 
        // setup expectations
        FlowTestHarness flowTestHarness = new FlowTestHarnessImpl(new OrderedExpectation()
        {
            {
                expectation(new ConsumerComponent("priceConsumer"), "consumer sourcing prices");
                expectation(flowEventFactory.newEvent("abc", priceTechConsumerMessage),  consumerEventComparator, "Raw message from the consumer");

                expectation(new ConverterComponent("priceConverter"), "converter for price object into stringBuffer");
                expectation(flowEventFactory.newEvent("abc", priceTechConverterMessage),  converterEventComparator, "Converted message from the converter");

                expectation(new ProducerComponent("priceProducer"), "producer logging the prices");
                expectation(flowEventFactory.newEvent("abc", priceTechProducerMessage),  producerEventComparator, "Logged message from the producer");
            }}
        );
        flowEventListenerSubject.addObserver((FlowObserver)flowTestHarness);
        
        PriceFlowFactory priceFlowFactory = 
            new PriceFlowFactory("flowName", "moduleName", this.configurationService, flowEventFactory, recoveryManagerFactory);

        // set a listener to record the events for the test harness
        priceFlowFactory.setFlowEventListener(flowEventListenerSubject);

        List<PriceTechMessage> priceTechMessages = new ArrayList<PriceTechMessage>();
        priceTechMessages.add(priceTechConsumerMessage);
        Flow priceFlow = priceFlowFactory.createGenericTechDrivenFlow(new PriceTechImpl(priceTechMessages));

        priceFlow.start();
        
        try
        {
            Thread.sleep(100);
        }
        catch(InterruptedException e)
        {
            // dont care
        }
        
        priceFlow.stop();
        
        // run flow assertions
        flowTestHarness.assertIsSatisfied();
    }

}
