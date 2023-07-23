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

import org.ikasan.builder.component.Builder;
import org.ikasan.builder.invoker.Configuration;
import org.ikasan.component.endpoint.consumer.api.spec.EndpointEventProvider;
import org.ikasan.exceptionResolver.ExceptionResolver;
import org.ikasan.exclusion.service.ExclusionServiceFactory;
import org.ikasan.flow.configuration.FlowPersistentConfiguration;
import org.ikasan.flow.visitorPattern.invoker.*;
import org.ikasan.sample.MyConfiguration;
import org.ikasan.spec.component.endpoint.Broker;
import org.ikasan.spec.component.endpoint.Consumer;
import org.ikasan.spec.component.endpoint.EndpointException;
import org.ikasan.spec.component.endpoint.Producer;
import org.ikasan.spec.component.filter.Filter;
import org.ikasan.spec.component.routing.MultiRecipientRouter;
import org.ikasan.spec.component.routing.SingleRecipientRouter;
import org.ikasan.spec.component.sequencing.Sequencer;
import org.ikasan.spec.component.splitting.Splitter;
import org.ikasan.spec.component.transformation.Converter;
import org.ikasan.spec.component.transformation.Translator;
import org.ikasan.spec.configuration.Configured;
import org.ikasan.spec.configuration.ConfiguredResource;
import org.ikasan.spec.error.reporting.ErrorReportingService;
import org.ikasan.spec.event.EventFactory;
import org.ikasan.spec.exclusion.ExclusionService;
import org.ikasan.spec.flow.Flow;
import org.ikasan.spec.flow.FlowElement;
import org.ikasan.spec.flow.FlowInvocationContextListener;
import org.ikasan.spec.resubmission.ResubmissionEventFactory;
import org.ikasan.spec.resubmission.ResubmissionService;
import org.ikasan.spec.serialiser.SerialiserFactory;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.annotation.ImportResource;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.util.SocketUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * This test class supports the <code>FlowBuilder</code> class.
 *
 * @author Ikasan Development Team
 */
@SuppressWarnings("unchecked")
@org.springframework.context.annotation.Configuration
@ImportResource( {
        "classpath:filetransfer-service-conf.xml",
} )
public class FlowBuilderTest
{
    /**
     * Mockery for mocking concrete classes
     */
    private Mockery mockery = new Mockery()
    {
        {
            setImposteriser(ClassImposteriser.INSTANCE);
        }
    };

    // Endpoints  
    /** Mock Consumer */
    final Consumer consumer = mockery.mock(Consumer.class, "mockConsumer");
    /** Mock Consumer Builder */
    final Builder<Consumer> consumerBuilder = mockery.mock(Builder.class, "mockConsumerBuilder");
    /** Mock Producer */
    final Producer producer = mockery.mock(Producer.class, "mockProducer");
    /** Mock Producer Builder */
    final Builder<Producer> producerBuilder = mockery.mock(Builder.class, "mockProducerBuilder");
    /** Mock Broker */
    final Broker broker = mockery.mock(Broker.class, "mockBroker");
    /** Mock Broker Builder */
    final Builder<Broker> brokerBuilder = mockery.mock(Builder.class, "mockBrokerBuilder");

    // Transformers
    /** Mock Translator */
    final Translator translator = mockery.mock(Translator.class, "mockTranslator");
    /** Mock Translator Builder */
    final Builder<Translator> translatorBuilder = mockery.mock(Builder.class, "mockTranslatorBuilder");

    /** Mock Splitter */
    final Splitter splitter = mockery.mock(Splitter.class, "mockSplitter");
    /** Mock Splitter Builder */
    final Builder<Splitter> splitterBuilder = mockery.mock(Builder.class, "mockSplitterBuilder");

    /** Mock ConcurrentSplitter Builder */
    final Builder<Splitter> concurrentSplitterBuilder = mockery.mock(Builder.class, "mockConcurrentSplitterBuilder");

    /** Mock Converter */
    final Converter converter = mockery.mock(Converter.class, "mockConverter");
    /** Mock Converter Builder */
    final Builder<Converter> converterBuilder = mockery.mock(Builder.class, "mockConverterBuilder");

    // filters
    /** Mock Filters */
    final Filter filter = mockery.mock(Filter.class, "mockFilter");
    /** Mock Filter Builder */
    final Builder<Filter> filterBuilder = mockery.mock(Builder.class, "mockFilterBuilder");

    // Routers
    /** Mock SRR Router */
    final SingleRecipientRouter singleRecipientRouter = mockery.mock(SingleRecipientRouter.class, "mockSingleRecipientRouter");
    /** Mock SRR Router Builder */
    final Builder<SingleRecipientRouter> singleRecipientRouterBuilder = mockery.mock(Builder.class, "mockSingleRecipientRouterBuilder");
    /** Mock MRR Router */
    final MultiRecipientRouter multiRecipientRouter = mockery.mock(MultiRecipientRouter.class, "mockMultiRecipientRouter");
    /** Mock MRR Router Builder */
    final Builder<MultiRecipientRouter> multiRecipientRouterBuilder = mockery.mock(Builder.class, "mockMultiRecipientRouterBuilder");

    // Sequencers
    /** Mock Sequencer */
    final Sequencer sequencer = mockery.mock(Sequencer.class, "mockSequencingRouter");
    /** Mock Sequencer Builder */
    final Builder<Sequencer> sequencerBuilder = mockery.mock(Builder.class, "mockSequencingRouterBuilder");

    /** Context Listener */
    final FlowInvocationContextListener flowInvocationContextListener = mockery.mock(FlowInvocationContextListener.class, "flowInvocationContextListener");

    final ExclusionServiceFactory exclusionServiceFactory = mockery.mock(ExclusionServiceFactory.class, "exclusionServiceFactory");

    final ExclusionService exclusionService = mockery.mock(ExclusionService.class, "exclusionService");

    final SerialiserFactory serialiserFactory = mockery.mock(SerialiserFactory.class, "serialiserFactory");

    IkasanApplication ikasanApplication;

    @Before
    public void setup()
    {
        String[] args = { "--server.port=" + SocketUtils.findAvailableTcpPort(8000, 9000),
            "--ikasan.datasource.liquibase.change-log=classpath:db-changelog.xml",
            "--ikasan.transient.datasource.liquibase.change-log=classpath:db-changelog-transient-3.2.0.xml",
            "--server.tomcat.additional-tld-skip-patterns=xercesImpl.jar,xml-apis.jar,serializer.jar",
            "--spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration"
            + ",org.springframework.boot.autoconfigure.liquibase.LiquibaseAutoConfiguration"
            + ",org.springframework.boot.autoconfigure.quartz.QuartzAutoConfiguration"
            + ",org.springframework.boot.autoconfigure.security.servlet.SecurityFilterAutoConfiguration"
            + ",me.snowdrop.boot.narayana.autoconfigure.NarayanaConfiguration"
            + ",org.springframework.boot.autoconfigure.context.MessageSourceAutoConfiguration"
        };

        ikasanApplication = IkasanApplicationFactory.getIkasanApplication(args);
    }

    private void setupMockExpectations(){
        // expectations
        mockery.checking(new Expectations()
        {
            {
                // set event factory
                oneOf(consumer).setEventFactory(with(any(EventFactory.class)));
                atLeast(1).of(consumer).isRunning();
                will(returnValue(true));

                oneOf(exclusionServiceFactory).getExclusionService("moduleName", "flowName");
                will(returnValue(exclusionService));
            }
        });
    }


    @After
    public void teardown()
    {
        ikasanApplication.close();
    }

    /**
     * Test successful flow creation.
     *
     * NOTE: No tests to run - the fact it compiles means the method signatures are present.
     */
    @Test
    public void test_successful_method_combinations()
    {
        BuilderFactory builderFactory = ikasanApplication.getBuilderFactory();
        FlowBuilder flowBuilder = builderFactory.getFlowBuilder("testModule","testFlow");

        // get primary route builder
        FlowBuilder.PrimaryRouteBuilder primaryRouteBuilder = flowBuilder.consumer("Consumer", consumer);

        mockery.checking(new Expectations()
        {
            {
                exactly(3).of(consumerBuilder).build();
                will(returnValue(consumer));
                exactly(3).of(converterBuilder).build();
                will(returnValue(converter));
                exactly(3).of(translatorBuilder).build();
                will(returnValue(translator));
                exactly(3).of(producerBuilder).build();
                will(returnValue(producer));
                exactly(3).of(brokerBuilder).build();
                will(returnValue(broker));
                exactly(3).of(sequencerBuilder).build();
                will(returnValue(sequencer));
                exactly(3).of(singleRecipientRouterBuilder).build();
                will(returnValue(singleRecipientRouter));
                exactly(3).of(multiRecipientRouterBuilder).build();
                will(returnValue(multiRecipientRouter));
                exactly(3).of(filterBuilder).build();
                will(returnValue(filter));
                exactly(3).of(splitterBuilder).build();
                will(returnValue(splitter));
                exactly(3).of(concurrentSplitterBuilder).build();
                will(returnValue(splitter));
            }
        });

        // consumer combinations
        flowBuilder.consumer("Consumer", consumer);
        flowBuilder.consumer("Consumer", consumerBuilder);
        flowBuilder.consumer("Consumer", consumer, Configuration.consumerInvoker());
        flowBuilder.consumer("Consumer", consumerBuilder, Configuration.consumerInvoker());
        flowBuilder.consumer("Consumer", consumer, Configuration.consumerInvoker().build());
        flowBuilder.consumer("Consumer", consumerBuilder, Configuration.consumerInvoker().build());

        // converter combinations
        primaryRouteBuilder.converter("Converter", converter);
        primaryRouteBuilder.converter("Converter", converterBuilder);
        primaryRouteBuilder.converter("Converter", converter, Configuration.converterInvoker());
        primaryRouteBuilder.converter("Converter", converterBuilder, Configuration.converterInvoker());
        primaryRouteBuilder.converter("Converter", converter, Configuration.converterInvoker().build());
        primaryRouteBuilder.converter("Converter", converterBuilder, Configuration.converterInvoker().build());

        // translator combinations
        primaryRouteBuilder.translator("Translator", translator);
        primaryRouteBuilder.translator("Translator", translatorBuilder);
        primaryRouteBuilder.translator("Translator", translator, Configuration.translatorInvoker());
        primaryRouteBuilder.translator("Translator", translatorBuilder, Configuration.translatorInvoker());
        primaryRouteBuilder.translator("Translator", translator, Configuration.translatorInvoker().build());
        primaryRouteBuilder.translator("Translator", translatorBuilder, Configuration.translatorInvoker().build());

        // producer combinations
        primaryRouteBuilder.producer("Producer", producer);
        primaryRouteBuilder.producer("Producer", producerBuilder);
        primaryRouteBuilder.producer("Producer", producer, Configuration.producerInvoker());
        primaryRouteBuilder.producer("Producer", producerBuilder, Configuration.producerInvoker());
        primaryRouteBuilder.producer("Producer", producer, Configuration.producerInvoker().build());
        primaryRouteBuilder.producer("Producer", producerBuilder, Configuration.producerInvoker().build());

        // broker combinations
        primaryRouteBuilder.broker("Broker", broker);
        primaryRouteBuilder.broker("Broker", brokerBuilder);
        primaryRouteBuilder.broker("Broker", broker, Configuration.brokerInvoker());
        primaryRouteBuilder.broker("Broker", brokerBuilder, Configuration.brokerInvoker());
        primaryRouteBuilder.broker("Broker", broker, Configuration.brokerInvoker().build());
        primaryRouteBuilder.broker("Broker", brokerBuilder, Configuration.brokerInvoker().build());

        // sequencer combinations
        primaryRouteBuilder.sequencer("Sequencer", sequencer);
        primaryRouteBuilder.sequencer("Sequencer", sequencerBuilder);
        primaryRouteBuilder.sequencer("Sequencer", sequencer, Configuration.sequencerInvoker());
        primaryRouteBuilder.sequencer("Sequencer", sequencerBuilder, Configuration.sequencerInvoker());
        primaryRouteBuilder.sequencer("Sequencer", sequencer, Configuration.sequencerInvoker().build());
        primaryRouteBuilder.sequencer("Sequencer", sequencerBuilder, Configuration.sequencerInvoker().build());

        // singleRecipientRouter combinations
        primaryRouteBuilder.singleRecipientRouter("SingleRecipientRouter", singleRecipientRouter);
        primaryRouteBuilder.singleRecipientRouter("SingleRecipientRouter", singleRecipientRouterBuilder);
        primaryRouteBuilder.singleRecipientRouter("SingleRecipientRouter", singleRecipientRouter, Configuration.singleRecipientRouterInvoker());
        primaryRouteBuilder.singleRecipientRouter("SingleRecipientRouter", singleRecipientRouterBuilder, Configuration.singleRecipientRouterInvoker());
        primaryRouteBuilder.singleRecipientRouter("SingleRecipientRouter", singleRecipientRouter, Configuration.singleRecipientRouterInvoker().build());
        primaryRouteBuilder.singleRecipientRouter("SingleRecipientRouter", singleRecipientRouterBuilder, Configuration.singleRecipientRouterInvoker().build());

        // multiRecipientRouter combinations
        primaryRouteBuilder.multiRecipientRouter("MultiRecipientRouter", multiRecipientRouter);
        primaryRouteBuilder.multiRecipientRouter("MultiRecipientRouter", multiRecipientRouterBuilder);
        primaryRouteBuilder.multiRecipientRouter("MultiRecipientRouter", multiRecipientRouter, Configuration.multiRecipientRouterInvoker());
        primaryRouteBuilder.multiRecipientRouter("MultiRecipientRouter", multiRecipientRouterBuilder, Configuration.multiRecipientRouterInvoker());
        primaryRouteBuilder.multiRecipientRouter("MultiRecipientRouter", multiRecipientRouter, Configuration.multiRecipientRouterInvoker().build());
        primaryRouteBuilder.multiRecipientRouter("MultiRecipientRouter", multiRecipientRouterBuilder, Configuration.multiRecipientRouterInvoker().build());

        // filter combinations
        primaryRouteBuilder.filter("Filter", filter);
        primaryRouteBuilder.filter("Filter", filterBuilder);
        primaryRouteBuilder.filter("Filter", filter, Configuration.filterInvoker());
        primaryRouteBuilder.filter("Filter", filterBuilder, Configuration.filterInvoker());
        primaryRouteBuilder.filter("Filter", filter, Configuration.filterInvoker().build());
        primaryRouteBuilder.filter("Filter", filterBuilder, Configuration.filterInvoker().build());

        // splitter combinations
        primaryRouteBuilder.splitter("Splitter", splitter);
        primaryRouteBuilder.splitter("Splitter", splitterBuilder);
        primaryRouteBuilder.splitter("Splitter", splitter, Configuration.splitterInvoker());
        primaryRouteBuilder.splitter("Splitter", splitterBuilder, Configuration.splitterInvoker());
        primaryRouteBuilder.splitter("Splitter", splitter, Configuration.splitterInvoker().build());
        primaryRouteBuilder.splitter("Splitter", splitterBuilder, Configuration.splitterInvoker().build());

        // concurrent splitter combinations
        primaryRouteBuilder.concurrentSplitter("ConcurrentSplitter", splitter);
        primaryRouteBuilder.concurrentSplitter("ConcurrentSplitter", concurrentSplitterBuilder);
        primaryRouteBuilder.concurrentSplitter("ConcurrentSplitter", splitter, Configuration.concurrentSplitterInvoker());
        primaryRouteBuilder.concurrentSplitter("ConcurrentSplitter", concurrentSplitterBuilder, Configuration.concurrentSplitterInvoker());
        primaryRouteBuilder.concurrentSplitter("ConcurrentSplitter", splitter, Configuration.concurrentSplitterInvoker().build());
        primaryRouteBuilder.concurrentSplitter("ConcurrentSplitter", concurrentSplitterBuilder, Configuration.concurrentSplitterInvoker().build());
    }

    /**
     * Test successful flow creation.
     */
    @Test
    public void test_successful_simple_transitions()
    {
        setupMockExpectations();
        BuilderFactory builderFactory = ikasanApplication.getBuilderFactory();
        Flow flow = builderFactory.getFlowBuilder("moduleName", "flowName")
                .isRecording(true)
                .withDescription("flowDescription")
                .withFlowInvocationContextListener(flowInvocationContextListener)
                .withFlowInvocationContextListener(flowInvocationContextListener)
                .withExclusionServiceFactory(exclusionServiceFactory)
                .withSerialiserFactory(serialiserFactory)
                .consumer("consumer", consumer)
                .converter("converter", converter)
                .translator("translator", translator)
                .splitter("splitter", splitter)
                .broker("broker", broker)
                .producer("producer", producer).build();

        Assert.assertTrue("flow name is incorrect", "flowName".equals(flow.getName()));
        Assert.assertTrue("module name is incorrect", "moduleName".equals(flow.getModuleName()));
        Assert.assertTrue("flow is recording", true == ((FlowPersistentConfiguration)((ConfiguredResource)flow).getConfiguration()).getIsRecording());
        List<FlowElement<?>> flowElements = flow.getFlowElements();
        Assert.assertTrue("Should be 6 flow elements", flowElements.size() == 6);
        Assert.assertNotNull("Flow elements cannot be 'null'", flowElements);

        Assert.assertTrue("Should have two FlowInvocationContextListener", flow.getFlowInvocationContextListeners().size() == 2);

        FlowElement fe = flowElements.get(0);
        Assert.assertTrue("flow element name should be 'consumer'", "consumer".equals(fe.getComponentName()));
        Assert.assertTrue("flow element component should be an instance of Consumer", fe.getFlowComponent() instanceof Consumer);
        Assert.assertTrue("flow element invoker should be an instance of ConsumerFlowElementInvoker", fe.getFlowElementInvoker() instanceof ConsumerFlowElementInvoker);
        Assert.assertTrue("flow element invoker should be an instance of ConfiguredResource", fe.getFlowElementInvoker() instanceof ConfiguredResource);
        Assert.assertTrue("flow element transition should be to coverter", fe.getTransitions().size() == 1);

        fe = flowElements.get(1);
        Assert.assertTrue("flow element name should be 'converter'", "converter".equals(fe.getComponentName()));
        Assert.assertTrue("flow element component should be an instance of Converter", fe.getFlowComponent() instanceof Converter);
        Assert.assertTrue("flow element invoker should be an instance of ConverterFlowElementInvoker", fe.getFlowElementInvoker() instanceof ConverterFlowElementInvoker);
        Assert.assertTrue("flow element transition should be to translator", fe.getTransitions().size() == 1);

        fe = flowElements.get(2);
        Assert.assertTrue("flow element name should be 'translator'", "translator".equals(fe.getComponentName()));
        Assert.assertTrue("flow element component should be an instance of Translator", fe.getFlowComponent() instanceof Translator);
        Assert.assertTrue("flow element invoker should be an instance of TranslatorFlowElementInvoker", fe.getFlowElementInvoker() instanceof TranslatorFlowElementInvoker);
        Assert.assertTrue("flow element transition should be to splitter", fe.getTransitions().size() == 1);

        fe = flowElements.get(3);
        Assert.assertTrue("flow element name should be 'splitter'", "splitter".equals(fe.getComponentName()));
        Assert.assertTrue("flow element component should be an instance of Splitter", fe.getFlowComponent() instanceof Splitter);
        Assert.assertTrue("flow element invoker should be an instance of SplitterFlowElementInvoker", fe.getFlowElementInvoker() instanceof SplitterFlowElementInvoker);
        Assert.assertTrue("flow element transition should be to broker", fe.getTransitions().size() == 1);

        fe = flowElements.get(4);
        Assert.assertTrue("flow element name should be 'broker'", "broker".equals(fe.getComponentName()));
        Assert.assertTrue("flow element component should be an instance of Broker", fe.getFlowComponent() instanceof Broker);
        Assert.assertTrue("flow element invoker should be an instance of BrokerFlowElementInvoker", fe.getFlowElementInvoker() instanceof BrokerFlowElementInvoker);
        Assert.assertTrue("flow element transition should be to producer", fe.getTransitions().size() == 1);

        fe = flowElements.get(5);
        Assert.assertTrue("flow element name should be 'producer'", "producer".equals(fe.getComponentName()));
        Assert.assertTrue("flow element component should be an instance of Producer", fe.getFlowComponent() instanceof Producer);
        Assert.assertTrue("flow element invoker should be an instance of ProducerFlowElementInvoker", fe.getFlowElementInvoker() instanceof ProducerFlowElementInvoker);
        Assert.assertTrue("flow element transition should be to 'null", fe.getTransitions().size() == 0);

        mockery.assertIsSatisfied();
    }

    /**
     * Test successful flow creation.
     */
    @Test
    public void test_successful_simple_transitions_with_concurrentSplitter()
    {
        setupMockExpectations();
        BuilderFactory builderFactory = ikasanApplication.getBuilderFactory();
        Flow flow = builderFactory.getFlowBuilder("moduleName", "flowName")
            .isRecording(true)
            .withDescription("flowDescription")
            .withFlowInvocationContextListener(flowInvocationContextListener)
            .withFlowInvocationContextListener(flowInvocationContextListener)
            .withExclusionServiceFactory(exclusionServiceFactory)
            .withSerialiserFactory(serialiserFactory)
            .consumer("consumer", consumer)
            .converter("converter", converter)
            .translator("translator", translator)
            .concurrentSplitter("splitter", splitter)
            .broker("broker", broker)
            .producer("producer", producer).build();

        Assert.assertTrue("flow name is incorrect", "flowName".equals(flow.getName()));
        Assert.assertTrue("module name is incorrect", "moduleName".equals(flow.getModuleName()));
        Assert.assertTrue("flow is recording", true == ((FlowPersistentConfiguration)((ConfiguredResource)flow).getConfiguration()).getIsRecording());
        List<FlowElement<?>> flowElements = flow.getFlowElements();
        Assert.assertTrue("Should be 6 flow elements", flowElements.size() == 6);
        Assert.assertNotNull("Flow elements cannot be 'null'", flowElements);

        Assert.assertTrue("Should have two FlowInvocationContextListener", flow.getFlowInvocationContextListeners().size() == 2);

        FlowElement fe = flowElements.get(0);
        Assert.assertTrue("flow element name should be 'consumer'", "consumer".equals(fe.getComponentName()));
        Assert.assertTrue("flow element component should be an instance of Consumer", fe.getFlowComponent() instanceof Consumer);
        Assert.assertTrue("flow element invoker should be an instance of ConsumerFlowElementInvoker", fe.getFlowElementInvoker() instanceof ConsumerFlowElementInvoker);
        Assert.assertTrue("flow element invoker should be an instance of ConfiguredResource", fe.getFlowElementInvoker() instanceof ConfiguredResource);
        Assert.assertTrue("flow element transition should be to coverter", fe.getTransitions().size() == 1);

        fe = flowElements.get(1);
        Assert.assertTrue("flow element name should be 'converter'", "converter".equals(fe.getComponentName()));
        Assert.assertTrue("flow element component should be an instance of Converter", fe.getFlowComponent() instanceof Converter);
        Assert.assertTrue("flow element invoker should be an instance of ConverterFlowElementInvoker", fe.getFlowElementInvoker() instanceof ConverterFlowElementInvoker);
        Assert.assertTrue("flow element transition should be to translator", fe.getTransitions().size() == 1);

        fe = flowElements.get(2);
        Assert.assertTrue("flow element name should be 'translator'", "translator".equals(fe.getComponentName()));
        Assert.assertTrue("flow element component should be an instance of Translator", fe.getFlowComponent() instanceof Translator);
        Assert.assertTrue("flow element invoker should be an instance of TranslatorFlowElementInvoker", fe.getFlowElementInvoker() instanceof TranslatorFlowElementInvoker);
        Assert.assertTrue("flow element transition should be to splitter", fe.getTransitions().size() == 1);

        fe = flowElements.get(3);
        Assert.assertTrue("flow element name should be 'splitter'", "splitter".equals(fe.getComponentName()));
        Assert.assertTrue("flow element component should be an instance of Splitter", fe.getFlowComponent() instanceof Splitter);
        Assert.assertTrue("flow element invoker should be an instance of ConcurrentSplitterFlowElementInvoker", fe.getFlowElementInvoker() instanceof ConcurrentSplitterFlowElementInvoker);
        Assert.assertTrue("flow element transition should be to broker", fe.getTransitions().size() == 1);

        fe = flowElements.get(4);
        Assert.assertTrue("flow element name should be 'broker'", "broker".equals(fe.getComponentName()));
        Assert.assertTrue("flow element component should be an instance of Broker", fe.getFlowComponent() instanceof Broker);
        Assert.assertTrue("flow element invoker should be an instance of BrokerFlowElementInvoker", fe.getFlowElementInvoker() instanceof BrokerFlowElementInvoker);
        Assert.assertTrue("flow element transition should be to producer", fe.getTransitions().size() == 1);

        fe = flowElements.get(5);
        Assert.assertTrue("flow element name should be 'producer'", "producer".equals(fe.getComponentName()));
        Assert.assertTrue("flow element component should be an instance of Producer", fe.getFlowComponent() instanceof Producer);
        Assert.assertTrue("flow element invoker should be an instance of ProducerFlowElementInvoker", fe.getFlowElementInvoker() instanceof ProducerFlowElementInvoker);
        Assert.assertTrue("flow element transition should be to 'null", fe.getTransitions().size() == 0);

        mockery.assertIsSatisfied();
    }

    /**
     * Test successful flow creation.
     */
    @Test
    public void test_successful_simple_transitions_not_recording()
    {
        setupMockExpectations();
        BuilderFactory builderFactory = ikasanApplication.getBuilderFactory();
        Flow flow = builderFactory.getFlowBuilder("moduleName", "flowName")
            .isRecording(false)
            .withDescription("flowDescription")
            .withFlowInvocationContextListener(flowInvocationContextListener)
            .withFlowInvocationContextListener(flowInvocationContextListener)
            .withExclusionServiceFactory(exclusionServiceFactory)
            .withSerialiserFactory(serialiserFactory)
            .consumer("consumer", consumer)
            .converter("converter", converter)
            .translator("translator", translator)
            .splitter("splitter", splitter)
            .broker("broker", broker)
            .producer("producer", producer).build();

        Assert.assertTrue("flow name is incorrect", "flowName".equals(flow.getName()));
        Assert.assertTrue("module name is incorrect", "moduleName".equals(flow.getModuleName()));
        Assert.assertTrue("flow is recording", false == ((FlowPersistentConfiguration)((ConfiguredResource)flow).getConfiguration()).getIsRecording());
        List<FlowElement<?>> flowElements = flow.getFlowElements();
        Assert.assertTrue("Should be 6 flow elements", flowElements.size() == 6);
        Assert.assertNotNull("Flow elements cannot be 'null'", flowElements);

        Assert.assertTrue("Should have two FlowInvocationContextListener", flow.getFlowInvocationContextListeners().size() == 2);

        FlowElement fe = flowElements.get(0);
        Assert.assertTrue("flow element name should be 'consumer'", "consumer".equals(fe.getComponentName()));
        Assert.assertTrue("flow element component should be an instance of Consumer", fe.getFlowComponent() instanceof Consumer);
        Assert.assertTrue("flow element invoker should be an instance of ConsumerFlowElementInvoker", fe.getFlowElementInvoker() instanceof ConsumerFlowElementInvoker);
        Assert.assertTrue("flow element invoker should be an instance of ConfiguredResource", fe.getFlowElementInvoker() instanceof ConfiguredResource);
        Assert.assertTrue("flow element transition should be to coverter", fe.getTransitions().size() == 1);

        fe = flowElements.get(1);
        Assert.assertTrue("flow element name should be 'converter'", "converter".equals(fe.getComponentName()));
        Assert.assertTrue("flow element component should be an instance of Converter", fe.getFlowComponent() instanceof Converter);
        Assert.assertTrue("flow element invoker should be an instance of ConverterFlowElementInvoker", fe.getFlowElementInvoker() instanceof ConverterFlowElementInvoker);
        Assert.assertTrue("flow element transition should be to translator", fe.getTransitions().size() == 1);

        fe = flowElements.get(2);
        Assert.assertTrue("flow element name should be 'translator'", "translator".equals(fe.getComponentName()));
        Assert.assertTrue("flow element component should be an instance of Translator", fe.getFlowComponent() instanceof Translator);
        Assert.assertTrue("flow element invoker should be an instance of TranslatorFlowElementInvoker", fe.getFlowElementInvoker() instanceof TranslatorFlowElementInvoker);
        Assert.assertTrue("flow element transition should be to splitter", fe.getTransitions().size() == 1);

        fe = flowElements.get(3);
        Assert.assertTrue("flow element name should be 'splitter'", "splitter".equals(fe.getComponentName()));
        Assert.assertTrue("flow element component should be an instance of Splitter", fe.getFlowComponent() instanceof Splitter);
        Assert.assertTrue("flow element invoker should be an instance of SplitterFlowElementInvoker", fe.getFlowElementInvoker() instanceof SplitterFlowElementInvoker);
        Assert.assertTrue("flow element transition should be to broker", fe.getTransitions().size() == 1);

        fe = flowElements.get(4);
        Assert.assertTrue("flow element name should be 'broker'", "broker".equals(fe.getComponentName()));
        Assert.assertTrue("flow element component should be an instance of Broker", fe.getFlowComponent() instanceof Broker);
        Assert.assertTrue("flow element invoker should be an instance of BrokerFlowElementInvoker", fe.getFlowElementInvoker() instanceof BrokerFlowElementInvoker);
        Assert.assertTrue("flow element transition should be to producer", fe.getTransitions().size() == 1);

        fe = flowElements.get(5);
        Assert.assertTrue("flow element name should be 'producer'", "producer".equals(fe.getComponentName()));
        Assert.assertTrue("flow element component should be an instance of Producer", fe.getFlowComponent() instanceof Producer);
        Assert.assertTrue("flow element invoker should be an instance of ProducerFlowElementInvoker", fe.getFlowElementInvoker() instanceof ProducerFlowElementInvoker);
        Assert.assertTrue("flow element transition should be to 'null", fe.getTransitions().size() == 0);

        mockery.assertIsSatisfied();
    }

    /**
     * Test successful flow creation.
     */
    @Test
    public void test_successful_simple_transitions_not_recording_with_concurrentSplitter()
    {
        setupMockExpectations();
        BuilderFactory builderFactory = ikasanApplication.getBuilderFactory();
        Flow flow = builderFactory.getFlowBuilder("moduleName", "flowName")
            .isRecording(false)
            .withDescription("flowDescription")
            .withFlowInvocationContextListener(flowInvocationContextListener)
            .withFlowInvocationContextListener(flowInvocationContextListener)
            .withExclusionServiceFactory(exclusionServiceFactory)
            .withSerialiserFactory(serialiserFactory)
            .consumer("consumer", consumer)
            .converter("converter", converter)
            .translator("translator", translator)
            .concurrentSplitter("splitter", splitter)
            .broker("broker", broker)
            .producer("producer", producer).build();

        Assert.assertTrue("flow name is incorrect", "flowName".equals(flow.getName()));
        Assert.assertTrue("module name is incorrect", "moduleName".equals(flow.getModuleName()));
        Assert.assertTrue("flow is recording", false == ((FlowPersistentConfiguration)((ConfiguredResource)flow).getConfiguration()).getIsRecording());
        List<FlowElement<?>> flowElements = flow.getFlowElements();
        Assert.assertTrue("Should be 6 flow elements", flowElements.size() == 6);
        Assert.assertNotNull("Flow elements cannot be 'null'", flowElements);

        Assert.assertTrue("Should have two FlowInvocationContextListener", flow.getFlowInvocationContextListeners().size() == 2);

        FlowElement fe = flowElements.get(0);
        Assert.assertTrue("flow element name should be 'consumer'", "consumer".equals(fe.getComponentName()));
        Assert.assertTrue("flow element component should be an instance of Consumer", fe.getFlowComponent() instanceof Consumer);
        Assert.assertTrue("flow element invoker should be an instance of ConsumerFlowElementInvoker", fe.getFlowElementInvoker() instanceof ConsumerFlowElementInvoker);
        Assert.assertTrue("flow element invoker should be an instance of ConfiguredResource", fe.getFlowElementInvoker() instanceof ConfiguredResource);
        Assert.assertTrue("flow element transition should be to coverter", fe.getTransitions().size() == 1);

        fe = flowElements.get(1);
        Assert.assertTrue("flow element name should be 'converter'", "converter".equals(fe.getComponentName()));
        Assert.assertTrue("flow element component should be an instance of Converter", fe.getFlowComponent() instanceof Converter);
        Assert.assertTrue("flow element invoker should be an instance of ConverterFlowElementInvoker", fe.getFlowElementInvoker() instanceof ConverterFlowElementInvoker);
        Assert.assertTrue("flow element transition should be to translator", fe.getTransitions().size() == 1);

        fe = flowElements.get(2);
        Assert.assertTrue("flow element name should be 'translator'", "translator".equals(fe.getComponentName()));
        Assert.assertTrue("flow element component should be an instance of Translator", fe.getFlowComponent() instanceof Translator);
        Assert.assertTrue("flow element invoker should be an instance of TranslatorFlowElementInvoker", fe.getFlowElementInvoker() instanceof TranslatorFlowElementInvoker);
        Assert.assertTrue("flow element transition should be to splitter", fe.getTransitions().size() == 1);

        fe = flowElements.get(3);
        Assert.assertTrue("flow element name should be 'splitter'", "splitter".equals(fe.getComponentName()));
        Assert.assertTrue("flow element component should be an instance of Splitter", fe.getFlowComponent() instanceof Splitter);
        Assert.assertTrue("flow element invoker should be an instance of ConcurrentSplitterFlowElementInvoker", fe.getFlowElementInvoker() instanceof ConcurrentSplitterFlowElementInvoker);
        Assert.assertTrue("flow element transition should be to broker", fe.getTransitions().size() == 1);

        fe = flowElements.get(4);
        Assert.assertTrue("flow element name should be 'broker'", "broker".equals(fe.getComponentName()));
        Assert.assertTrue("flow element component should be an instance of Broker", fe.getFlowComponent() instanceof Broker);
        Assert.assertTrue("flow element invoker should be an instance of BrokerFlowElementInvoker", fe.getFlowElementInvoker() instanceof BrokerFlowElementInvoker);
        Assert.assertTrue("flow element transition should be to producer", fe.getTransitions().size() == 1);

        fe = flowElements.get(5);
        Assert.assertTrue("flow element name should be 'producer'", "producer".equals(fe.getComponentName()));
        Assert.assertTrue("flow element component should be an instance of Producer", fe.getFlowComponent() instanceof Producer);
        Assert.assertTrue("flow element invoker should be an instance of ProducerFlowElementInvoker", fe.getFlowElementInvoker() instanceof ProducerFlowElementInvoker);
        Assert.assertTrue("flow element transition should be to 'null", fe.getTransitions().size() == 0);

        mockery.assertIsSatisfied();
    }

    /**
     * Test successful flow creation.
     */
    @Test
    public void test_successful_simple_transitions_with_invokerConfiguration()
    {
        setupMockExpectations();
        BuilderFactory builderFactory = ikasanApplication.getBuilderFactory();
        Flow flow = builderFactory.getFlowBuilder("moduleName", "flowName")
                .withDescription("flowDescription")
                .withFlowInvocationContextListener(flowInvocationContextListener)
                .withFlowInvocationContextListener(flowInvocationContextListener)
                .withExclusionServiceFactory(exclusionServiceFactory)
                .withSerialiserFactory(serialiserFactory)
                .consumer("consumer", consumer, Configuration.consumerInvoker().withDynamicConfiguration(true))
                .converter("converter", converter, Configuration.converterInvoker().withDynamicConfiguration(false))
                .translator("translator", translator, Configuration.translatorInvoker().withApplyTranslator(true).withDynamicConfiguration(true))
                .splitter("splitter", splitter, Configuration.splitterInvoker().withDynamicConfiguration(true))
                .broker("broker", broker, Configuration.brokerInvoker().withDynamicConfiguration(true))
                .producer("producer", producer, Configuration.producerInvoker().withDynamicConfiguration(true)).build();

        Assert.assertTrue("flow name is incorrect", "flowName".equals(flow.getName()));
        Assert.assertTrue("module name is incorrect", "moduleName".equals(flow.getModuleName()));
        List<FlowElement<?>> flowElements = flow.getFlowElements();
        Assert.assertTrue("Should be 6 flow elements", flowElements.size() == 6);
        Assert.assertNotNull("Flow elements cannot be 'null'", flowElements);

        Assert.assertTrue("Should have two FlowInvocationContextListener", flow.getFlowInvocationContextListeners().size() == 2);

        FlowElement fe = flowElements.get(0);
        Assert.assertTrue("flow element name should be 'consumer'", "consumer".equals(fe.getComponentName()));
        Assert.assertTrue("flow element component should be an instance of Consumer", fe.getFlowComponent() instanceof Consumer);
        Assert.assertTrue("flow element invoker should be an instance of ConsumerFlowElementInvoker", fe.getFlowElementInvoker() instanceof ConsumerFlowElementInvoker);
        Assert.assertTrue("flow element invoker should be an instance of ConfiguredResource", fe.getFlowElementInvoker() instanceof ConfiguredResource);
        Assert.assertTrue("flow element invoker should be have dynamicConfiguration set", ((ConfiguredResource<InvokerConfiguration>)fe.getFlowElementInvoker()).getConfiguration().isDynamicConfiguration());
        Assert.assertTrue("flow element transition should be to coverter", fe.getTransitions().size() == 1);

        fe = flowElements.get(1);
        Assert.assertTrue("flow element name should be 'converter'", "converter".equals(fe.getComponentName()));
        Assert.assertTrue("flow element component should be an instance of Converter", fe.getFlowComponent() instanceof Converter);
        Assert.assertTrue("flow element invoker should be an instance of ConverterFlowElementInvoker", fe.getFlowElementInvoker() instanceof ConverterFlowElementInvoker);
        Assert.assertTrue("flow element transition should be to translator", fe.getTransitions().size() == 1);

        fe = flowElements.get(2);
        Assert.assertTrue("flow element name should be 'translator'", "translator".equals(fe.getComponentName()));
        Assert.assertTrue("flow element component should be an instance of Translator", fe.getFlowComponent() instanceof Translator);
        Assert.assertTrue("flow element invoker should be an instance of TranslatorFlowElementInvoker", fe.getFlowElementInvoker() instanceof TranslatorFlowElementInvoker);
        Assert.assertTrue("flow element transition should be to splitter", fe.getTransitions().size() == 1);

        fe = flowElements.get(3);
        Assert.assertTrue("flow element name should be 'splitter'", "splitter".equals(fe.getComponentName()));
        Assert.assertTrue("flow element component should be an instance of Splitter", fe.getFlowComponent() instanceof Splitter);
        Assert.assertTrue("flow element invoker should be an instance of SplitterFlowElementInvoker", fe.getFlowElementInvoker() instanceof SplitterFlowElementInvoker);
        Assert.assertTrue("flow element transition should be to broker", fe.getTransitions().size() == 1);

        fe = flowElements.get(4);
        Assert.assertTrue("flow element name should be 'broker'", "broker".equals(fe.getComponentName()));
        Assert.assertTrue("flow element component should be an instance of Broker", fe.getFlowComponent() instanceof Broker);
        Assert.assertTrue("flow element invoker should be an instance of BrokerFlowElementInvoker", fe.getFlowElementInvoker() instanceof BrokerFlowElementInvoker);
        Assert.assertTrue("flow element transition should be to producer", fe.getTransitions().size() == 1);

        fe = flowElements.get(5);
        Assert.assertTrue("flow element name should be 'producer'", "producer".equals(fe.getComponentName()));
        Assert.assertTrue("flow element component should be an instance of Producer", fe.getFlowComponent() instanceof Producer);
        Assert.assertTrue("flow element invoker should be an instance of ProducerFlowElementInvoker", fe.getFlowElementInvoker() instanceof ProducerFlowElementInvoker);
        Assert.assertTrue("flow element transition should be to 'null", fe.getTransitions().size() == 0);

        mockery.assertIsSatisfied();
    }

    /**
     * Test successful flow creation.
     */
    @Test
    public void test_successful_simple_transitions_with_invokerConfiguration_with_concurrentSplitter()
    {
        setupMockExpectations();
        BuilderFactory builderFactory = ikasanApplication.getBuilderFactory();
        Flow flow = builderFactory.getFlowBuilder("moduleName", "flowName")
            .withDescription("flowDescription")
            .withFlowInvocationContextListener(flowInvocationContextListener)
            .withFlowInvocationContextListener(flowInvocationContextListener)
            .withExclusionServiceFactory(exclusionServiceFactory)
            .withSerialiserFactory(serialiserFactory)
            .consumer("consumer", consumer, Configuration.consumerInvoker().withDynamicConfiguration(true))
            .converter("converter", converter, Configuration.converterInvoker().withDynamicConfiguration(false))
            .translator("translator", translator, Configuration.translatorInvoker().withApplyTranslator(true).withDynamicConfiguration(true))
            .concurrentSplitter("splitter", splitter, Configuration.concurrentSplitterInvoker().withDynamicConfiguration(true))
            .broker("broker", broker, Configuration.brokerInvoker().withDynamicConfiguration(true))
            .producer("producer", producer, Configuration.producerInvoker().withDynamicConfiguration(true)).build();

        Assert.assertTrue("flow name is incorrect", "flowName".equals(flow.getName()));
        Assert.assertTrue("module name is incorrect", "moduleName".equals(flow.getModuleName()));
        List<FlowElement<?>> flowElements = flow.getFlowElements();
        Assert.assertTrue("Should be 6 flow elements", flowElements.size() == 6);
        Assert.assertNotNull("Flow elements cannot be 'null'", flowElements);

        Assert.assertTrue("Should have two FlowInvocationContextListener", flow.getFlowInvocationContextListeners().size() == 2);

        FlowElement fe = flowElements.get(0);
        Assert.assertTrue("flow element name should be 'consumer'", "consumer".equals(fe.getComponentName()));
        Assert.assertTrue("flow element component should be an instance of Consumer", fe.getFlowComponent() instanceof Consumer);
        Assert.assertTrue("flow element invoker should be an instance of ConsumerFlowElementInvoker", fe.getFlowElementInvoker() instanceof ConsumerFlowElementInvoker);
        Assert.assertTrue("flow element invoker should be an instance of ConfiguredResource", fe.getFlowElementInvoker() instanceof ConfiguredResource);
        Assert.assertTrue("flow element invoker should be have dynamicConfiguration set", ((ConfiguredResource<InvokerConfiguration>)fe.getFlowElementInvoker()).getConfiguration().isDynamicConfiguration());
        Assert.assertTrue("flow element transition should be to coverter", fe.getTransitions().size() == 1);

        fe = flowElements.get(1);
        Assert.assertTrue("flow element name should be 'converter'", "converter".equals(fe.getComponentName()));
        Assert.assertTrue("flow element component should be an instance of Converter", fe.getFlowComponent() instanceof Converter);
        Assert.assertTrue("flow element invoker should be an instance of ConverterFlowElementInvoker", fe.getFlowElementInvoker() instanceof ConverterFlowElementInvoker);
        Assert.assertTrue("flow element transition should be to translator", fe.getTransitions().size() == 1);

        fe = flowElements.get(2);
        Assert.assertTrue("flow element name should be 'translator'", "translator".equals(fe.getComponentName()));
        Assert.assertTrue("flow element component should be an instance of Translator", fe.getFlowComponent() instanceof Translator);
        Assert.assertTrue("flow element invoker should be an instance of TranslatorFlowElementInvoker", fe.getFlowElementInvoker() instanceof TranslatorFlowElementInvoker);
        Assert.assertTrue("flow element transition should be to splitter", fe.getTransitions().size() == 1);

        fe = flowElements.get(3);
        Assert.assertTrue("flow element name should be 'splitter'", "splitter".equals(fe.getComponentName()));
        Assert.assertTrue("flow element component should be an instance of Splitter", fe.getFlowComponent() instanceof Splitter);
        Assert.assertTrue("flow element invoker should be an instance of ConcurrentSplitterFlowElementInvoker", fe.getFlowElementInvoker() instanceof ConcurrentSplitterFlowElementInvoker);
        Assert.assertTrue("flow element transition should be to broker", fe.getTransitions().size() == 1);

        fe = flowElements.get(4);
        Assert.assertTrue("flow element name should be 'broker'", "broker".equals(fe.getComponentName()));
        Assert.assertTrue("flow element component should be an instance of Broker", fe.getFlowComponent() instanceof Broker);
        Assert.assertTrue("flow element invoker should be an instance of BrokerFlowElementInvoker", fe.getFlowElementInvoker() instanceof BrokerFlowElementInvoker);
        Assert.assertTrue("flow element transition should be to producer", fe.getTransitions().size() == 1);

        fe = flowElements.get(5);
        Assert.assertTrue("flow element name should be 'producer'", "producer".equals(fe.getComponentName()));
        Assert.assertTrue("flow element component should be an instance of Producer", fe.getFlowComponent() instanceof Producer);
        Assert.assertTrue("flow element invoker should be an instance of ProducerFlowElementInvoker", fe.getFlowElementInvoker() instanceof ProducerFlowElementInvoker);
        Assert.assertTrue("flow element transition should be to 'null", fe.getTransitions().size() == 0);

        mockery.assertIsSatisfied();
    }

    /**
     * Test successful flow creation.
     */
    @Test
    public void test_successful_simple_transitions_passing_builder_instances()
    {
        setupMockExpectations();

        mockery.checking(new Expectations()
        {
            {
                oneOf(consumerBuilder).build();
                will(returnValue(consumer));
                oneOf(converterBuilder).build();
                will(returnValue(converter));
                oneOf(translatorBuilder).build();
                will(returnValue(translator));
                oneOf(splitterBuilder).build();
                will(returnValue(splitter));
                oneOf(brokerBuilder).build();
                will(returnValue(broker));
                oneOf(producerBuilder).build();
                will(returnValue(producer));
            }
        });

        BuilderFactory builderFactory = ikasanApplication.getBuilderFactory();
        Flow flow = builderFactory.getFlowBuilder("moduleName", "flowName")
                .withDescription("flowDescription")
                .withFlowInvocationContextListener(flowInvocationContextListener)
                .withFlowInvocationContextListener(flowInvocationContextListener)
                .withExclusionServiceFactory(exclusionServiceFactory)
                .withSerialiserFactory(serialiserFactory)
                .consumer("consumer", consumerBuilder)
                .converter("converter", converterBuilder)
                .translator("translator", translatorBuilder)
                .splitter("splitter", splitterBuilder)
                .broker("broker", brokerBuilder)
                .producer("producer", producerBuilder).build();

        Assert.assertTrue("flow name is incorrect", "flowName".equals(flow.getName()));
        Assert.assertTrue("module name is incorrect", "moduleName".equals(flow.getModuleName()));
        List<FlowElement<?>> flowElements = flow.getFlowElements();
        Assert.assertTrue("Should be 6 flow elements", flowElements.size() == 6);
        Assert.assertNotNull("Flow elements cannot be 'null'", flowElements);

        Assert.assertTrue("Should have two FlowInvocationContextListener", flow.getFlowInvocationContextListeners().size() == 2);

        FlowElement fe = flowElements.get(0);
        Assert.assertTrue("flow element name should be 'consumer'", "consumer".equals(fe.getComponentName()));
        Assert.assertTrue("flow element component should be an instance of Consumer", fe.getFlowComponent() instanceof Consumer);
        Assert.assertTrue("flow element invoker should be an instance of ConsumerFlowElementInvoker", fe.getFlowElementInvoker() instanceof ConsumerFlowElementInvoker);
        Assert.assertTrue("flow element transition should be to coverter", fe.getTransitions().size() == 1);

        fe = flowElements.get(1);
        Assert.assertTrue("flow element name should be 'converter'", "converter".equals(fe.getComponentName()));
        Assert.assertTrue("flow element component should be an instance of Converter", fe.getFlowComponent() instanceof Converter);
        Assert.assertTrue("flow element invoker should be an instance of ConverterFlowElementInvoker", fe.getFlowElementInvoker() instanceof ConverterFlowElementInvoker);
        Assert.assertTrue("flow element transition should be to translator", fe.getTransitions().size() == 1);

        fe = flowElements.get(2);
        Assert.assertTrue("flow element name should be 'translator'", "translator".equals(fe.getComponentName()));
        Assert.assertTrue("flow element component should be an instance of Translator", fe.getFlowComponent() instanceof Translator);
        Assert.assertTrue("flow element invoker should be an instance of TranslatorFlowElementInvoker", fe.getFlowElementInvoker() instanceof TranslatorFlowElementInvoker);
        Assert.assertTrue("flow element transition should be to splitter", fe.getTransitions().size() == 1);

        fe = flowElements.get(3);
        Assert.assertTrue("flow element name should be 'splitter'", "splitter".equals(fe.getComponentName()));
        Assert.assertTrue("flow element component should be an instance of Splitter", fe.getFlowComponent() instanceof Splitter);
        Assert.assertTrue("flow element invoker should be an instance of SplitterFlowElementInvoker", fe.getFlowElementInvoker() instanceof SplitterFlowElementInvoker);
        Assert.assertTrue("flow element transition should be to broker", fe.getTransitions().size() == 1);

        fe = flowElements.get(4);
        Assert.assertTrue("flow element name should be 'broker'", "broker".equals(fe.getComponentName()));
        Assert.assertTrue("flow element component should be an instance of Broker", fe.getFlowComponent() instanceof Broker);
        Assert.assertTrue("flow element invoker should be an instance of BrokerFlowElementInvoker", fe.getFlowElementInvoker() instanceof BrokerFlowElementInvoker);
        Assert.assertTrue("flow element transition should be to producer", fe.getTransitions().size() == 1);

        fe = flowElements.get(5);
        Assert.assertTrue("flow element name should be 'producer'", "producer".equals(fe.getComponentName()));
        Assert.assertTrue("flow element component should be an instance of Producer", fe.getFlowComponent() instanceof Producer);
        Assert.assertTrue("flow element invoker should be an instance of ProducerFlowElementInvoker", fe.getFlowElementInvoker() instanceof ProducerFlowElementInvoker);
        Assert.assertTrue("flow element transition should be to 'null", fe.getTransitions().size() == 0);

        mockery.assertIsSatisfied();
    }

    /**
     * Test successful flow creation.
     */
    @Test
    public void test_successful_simple_transitions_passing_builder_instances_with_concurrentSplitter()
    {
        setupMockExpectations();

        mockery.checking(new Expectations()
        {
            {
                oneOf(consumerBuilder).build();
                will(returnValue(consumer));
                oneOf(converterBuilder).build();
                will(returnValue(converter));
                oneOf(translatorBuilder).build();
                will(returnValue(translator));
                oneOf(concurrentSplitterBuilder).build();
                will(returnValue(splitter));
                oneOf(brokerBuilder).build();
                will(returnValue(broker));
                oneOf(producerBuilder).build();
                will(returnValue(producer));
            }
        });

        BuilderFactory builderFactory = ikasanApplication.getBuilderFactory();
        Flow flow = builderFactory.getFlowBuilder("moduleName", "flowName")
            .withDescription("flowDescription")
            .withFlowInvocationContextListener(flowInvocationContextListener)
            .withFlowInvocationContextListener(flowInvocationContextListener)
            .withExclusionServiceFactory(exclusionServiceFactory)
            .withSerialiserFactory(serialiserFactory)
            .consumer("consumer", consumerBuilder)
            .converter("converter", converterBuilder)
            .translator("translator", translatorBuilder)
            .concurrentSplitter("splitter", concurrentSplitterBuilder)
            .broker("broker", brokerBuilder)
            .producer("producer", producerBuilder).build();

        Assert.assertTrue("flow name is incorrect", "flowName".equals(flow.getName()));
        Assert.assertTrue("module name is incorrect", "moduleName".equals(flow.getModuleName()));
        List<FlowElement<?>> flowElements = flow.getFlowElements();
        Assert.assertTrue("Should be 6 flow elements", flowElements.size() == 6);
        Assert.assertNotNull("Flow elements cannot be 'null'", flowElements);

        Assert.assertTrue("Should have two FlowInvocationContextListener", flow.getFlowInvocationContextListeners().size() == 2);

        FlowElement fe = flowElements.get(0);
        Assert.assertTrue("flow element name should be 'consumer'", "consumer".equals(fe.getComponentName()));
        Assert.assertTrue("flow element component should be an instance of Consumer", fe.getFlowComponent() instanceof Consumer);
        Assert.assertTrue("flow element invoker should be an instance of ConsumerFlowElementInvoker", fe.getFlowElementInvoker() instanceof ConsumerFlowElementInvoker);
        Assert.assertTrue("flow element transition should be to coverter", fe.getTransitions().size() == 1);

        fe = flowElements.get(1);
        Assert.assertTrue("flow element name should be 'converter'", "converter".equals(fe.getComponentName()));
        Assert.assertTrue("flow element component should be an instance of Converter", fe.getFlowComponent() instanceof Converter);
        Assert.assertTrue("flow element invoker should be an instance of ConverterFlowElementInvoker", fe.getFlowElementInvoker() instanceof ConverterFlowElementInvoker);
        Assert.assertTrue("flow element transition should be to translator", fe.getTransitions().size() == 1);

        fe = flowElements.get(2);
        Assert.assertTrue("flow element name should be 'translator'", "translator".equals(fe.getComponentName()));
        Assert.assertTrue("flow element component should be an instance of Translator", fe.getFlowComponent() instanceof Translator);
        Assert.assertTrue("flow element invoker should be an instance of TranslatorFlowElementInvoker", fe.getFlowElementInvoker() instanceof TranslatorFlowElementInvoker);
        Assert.assertTrue("flow element transition should be to splitter", fe.getTransitions().size() == 1);

        fe = flowElements.get(3);
        Assert.assertTrue("flow element name should be 'splitter'", "splitter".equals(fe.getComponentName()));
        Assert.assertTrue("flow element component should be an instance of Splitter", fe.getFlowComponent() instanceof Splitter);
        Assert.assertTrue("flow element invoker should be an instance of ConcurrentSplitterFlowElementInvoker", fe.getFlowElementInvoker() instanceof ConcurrentSplitterFlowElementInvoker);
        Assert.assertTrue("flow element transition should be to broker", fe.getTransitions().size() == 1);

        fe = flowElements.get(4);
        Assert.assertTrue("flow element name should be 'broker'", "broker".equals(fe.getComponentName()));
        Assert.assertTrue("flow element component should be an instance of Broker", fe.getFlowComponent() instanceof Broker);
        Assert.assertTrue("flow element invoker should be an instance of BrokerFlowElementInvoker", fe.getFlowElementInvoker() instanceof BrokerFlowElementInvoker);
        Assert.assertTrue("flow element transition should be to producer", fe.getTransitions().size() == 1);

        fe = flowElements.get(5);
        Assert.assertTrue("flow element name should be 'producer'", "producer".equals(fe.getComponentName()));
        Assert.assertTrue("flow element component should be an instance of Producer", fe.getFlowComponent() instanceof Producer);
        Assert.assertTrue("flow element invoker should be an instance of ProducerFlowElementInvoker", fe.getFlowElementInvoker() instanceof ProducerFlowElementInvoker);
        Assert.assertTrue("flow element transition should be to 'null", fe.getTransitions().size() == 0);

        mockery.assertIsSatisfied();
    }

    @Test
    public void test_build_with_resubmissionService_set_explicitly_to_consumer()
    {
        class ComplexConsumer implements Consumer,ResubmissionService{
            @Override public void onResubmission(Object o)
            {
            }

            @Override public void setResubmissionEventFactory(ResubmissionEventFactory resubmissionEventFactory)
            {
            }

            @Override public void setListener(Object o)
            {
            }

            @Override public void setEventFactory(Object o)
            {
            }

            @Override public Object getEventFactory()
            {
                return null;
            }

            @Override public void start()
            {
            }

            @Override public boolean isRunning()
            {
                return false;
            }

            @Override public void stop()
            {
            }
        }

        final Consumer consumer = mockery.mock(ComplexConsumer.class, "mockComplexConsumer");


        // expectations
        mockery.checking(new Expectations()
        {
            {
                // set event factory
                oneOf(consumer).setEventFactory(with(any(EventFactory.class)));
                atLeast(1).of(consumer).isRunning();
                will(returnValue(true));
                oneOf((ResubmissionService)consumer).setResubmissionEventFactory(with(any(ResubmissionEventFactory.class)));

            }
        });

        BuilderFactory builderFactory = ikasanApplication.getBuilderFactory();
        Flow flow = builderFactory.getFlowBuilder("moduleName", "flowName")
            .withDescription("flowDescription")
            .withResubmissionService((ResubmissionService) consumer)
            .consumer("consumer", consumer)
            .producer("producer", producer).build();


        mockery.assertIsSatisfied();
    }

    @Test
    public void test_build_with_resubmissionService_not_set_and_consumer_implementing_Resubmission()
    {
        class ComplexConsumer implements Consumer,ResubmissionService{
            @Override public void onResubmission(Object o)
            {
            }

            @Override public void setResubmissionEventFactory(ResubmissionEventFactory resubmissionEventFactory)
            {
            }

            @Override public void setListener(Object o)
            {
            }

            @Override public void setEventFactory(Object o)
            {
            }

            @Override public Object getEventFactory()
            {
                return null;
            }

            @Override public void start()
            {
            }

            @Override public boolean isRunning()
            {
                return false;
            }

            @Override public void stop()
            {
            }
        }

        final Consumer consumer = mockery.mock(ComplexConsumer.class, "mockComplexConsumer");


        // expectations
        mockery.checking(new Expectations()
        {
            {
                // set event factory
                oneOf(consumer).setEventFactory(with(any(EventFactory.class)));
                atLeast(1).of(consumer).isRunning();
                will(returnValue(true));
                oneOf((ResubmissionService)consumer).setResubmissionEventFactory(with(any(ResubmissionEventFactory.class)));

            }
        });

        BuilderFactory builderFactory = ikasanApplication.getBuilderFactory();
        Flow flow = builderFactory.getFlowBuilder("moduleName", "flowName")
            .withDescription("flowDescription")
            .consumer("consumer", consumer)
            .producer("producer", producer).build();


        mockery.assertIsSatisfied();
    }


    @Test
    public void test_successful_simple_transitions_with_resubmission_being_different_than_consumer()
    {


        final ResubmissionService resubmissionService = mockery.mock(ResubmissionService.class, "mockResubmissionService");


        // expectations
        mockery.checking(new Expectations()
        {
            {
                // set event factory
                oneOf(consumer).setEventFactory(with(any(EventFactory.class)));
                atLeast(1).of(consumer).isRunning();
                will(returnValue(true));
                oneOf(resubmissionService).setResubmissionEventFactory(with(any(ResubmissionEventFactory.class)));

            }
        });

        BuilderFactory builderFactory = ikasanApplication.getBuilderFactory();
        Flow flow = builderFactory.getFlowBuilder("moduleName", "flowName")
            .withDescription("flowDescription")
            .withResubmissionService(resubmissionService)
            .consumer("consumer", consumer)
            .producer("producer", producer).build();


        mockery.assertIsSatisfied();
    }

    @Test
    public void test_successful_simple_transitions_with_default_autowiring()
    {
        setupMockExpectations();

        BuilderFactory builderFactory = ikasanApplication.getBuilderFactory();
        Flow flow = builderFactory.getFlowBuilder("moduleName", "flowName")
            .withDescription("flowDescription")
            .withExclusionServiceFactory(exclusionServiceFactory)
            .consumer("consumer", consumer)
            .producer("producer", producer).build();

        Assert.assertTrue("flow name is incorrect", "flowName".equals(flow.getName()));
        Assert.assertTrue("module name is incorrect", "moduleName".equals(flow.getModuleName()));
        List<FlowElement<?>> flowElements = flow.getFlowElements();
        Assert.assertTrue("Should be 6 flow elements", flowElements.size() == 2);
        Assert.assertNotNull("Flow elements cannot be 'null'", flowElements);

        Assert.assertTrue("Should have SerialiserFactory", flow.getSerialiserFactory()!=null);
        Assert.assertTrue("Should have ErrorReportingService", ReflectionTestUtils.getField(flow,"errorReportingService")!=null);
        Assert.assertTrue("Should have ExclusionService", ReflectionTestUtils.getField(flow,"exclusionService")!=null);
        Assert.assertTrue("Should have RecoveryManager", ReflectionTestUtils.getField(flow,"recoveryManager")!=null);
        Assert.assertTrue("Should have one FlowInvocationContextListener", flow.getFlowInvocationContextListeners().size() == 1);

        FlowElement fe = flowElements.get(0);
        Assert.assertTrue("flow element name should be 'consumer'", "consumer".equals(fe.getComponentName()));
        Assert.assertTrue("flow element component should be an instance of Consumer", fe.getFlowComponent() instanceof Consumer);
        Assert.assertTrue("flow element invoker should be an instance of ConsumerFlowElementInvoker", fe.getFlowElementInvoker() instanceof ConsumerFlowElementInvoker);
        Assert.assertTrue("flow element transition should be to coverter", fe.getTransitions().size() == 1);

        fe = flowElements.get(1);
        Assert.assertTrue("flow element name should be 'producer'", "producer".equals(fe.getComponentName()));
        Assert.assertTrue("flow element component should be an instance of Producer", fe.getFlowComponent() instanceof Producer);
        Assert.assertTrue("flow element invoker should be an instance of ProducerFlowElementInvoker", fe.getFlowElementInvoker() instanceof ProducerFlowElementInvoker);
        Assert.assertTrue("flow element transition should be to 'null", fe.getTransitions().size() == 0);

        mockery.assertIsSatisfied();
    }

    @Test
    public void test_successful_simple_transitions_override_errorReportingServiceTimeToLive()
    {
        setupMockExpectations();

        Long timeToLive = new Long(100);
        BuilderFactory builderFactory = ikasanApplication.getBuilderFactory();
        Flow flow = builderFactory.getFlowBuilder("moduleName", "flowName")
                .withDescription("flowDescription")
                .withExclusionServiceFactory(exclusionServiceFactory)
                .withErrorReportingServiceTimeToLive(timeToLive)
                .consumer("consumer", consumer)
                .producer("producer", producer).build();

        Assert.assertTrue("flow name is incorrect", "flowName".equals(flow.getName()));
        Assert.assertTrue("module name is incorrect", "moduleName".equals(flow.getModuleName()));
        List<FlowElement<?>> flowElements = flow.getFlowElements();
        Assert.assertTrue("Should be 6 flow elements", flowElements.size() == 2);
        Assert.assertNotNull("Flow elements cannot be 'null'", flowElements);

        Assert.assertTrue("Should have SerialiserFactory", flow.getSerialiserFactory()!=null);
        Assert.assertTrue("Should have ErrorReportingService", ReflectionTestUtils.getField(flow,"errorReportingService")!=null);
        ErrorReportingService errorReportingService = (ErrorReportingService) ReflectionTestUtils.getField(flow,"errorReportingService");
        Long actualTimeToLive = (Long) ReflectionTestUtils.getField(errorReportingService,"timeToLive");
        Assert.assertTrue(actualTimeToLive == timeToLive);

        Assert.assertTrue("Should have ExclusionService", ReflectionTestUtils.getField(flow,"exclusionService")!=null);
        Assert.assertTrue("Should have RecoveryManager", ReflectionTestUtils.getField(flow,"recoveryManager")!=null);
        Assert.assertTrue("Should have one FlowInvocationContextListener", flow.getFlowInvocationContextListeners().size() == 1);

        FlowElement fe = flowElements.get(0);
        Assert.assertTrue("flow element name should be 'consumer'", "consumer".equals(fe.getComponentName()));
        Assert.assertTrue("flow element component should be an instance of Consumer", fe.getFlowComponent() instanceof Consumer);
        Assert.assertTrue("flow element invoker should be an instance of ConsumerFlowElementInvoker", fe.getFlowElementInvoker() instanceof ConsumerFlowElementInvoker);
        Assert.assertTrue("flow element transition should be to coverter", fe.getTransitions().size() == 1);

        fe = flowElements.get(1);
        Assert.assertTrue("flow element name should be 'producer'", "producer".equals(fe.getComponentName()));
        Assert.assertTrue("flow element component should be an instance of Producer", fe.getFlowComponent() instanceof Producer);
        Assert.assertTrue("flow element invoker should be an instance of ProducerFlowElementInvoker", fe.getFlowElementInvoker() instanceof ProducerFlowElementInvoker);
        Assert.assertTrue("flow element transition should be to 'null", fe.getTransitions().size() == 0);

        mockery.assertIsSatisfied();
    }

    /**
     * Test successful flow creation.
     */
    @Test
    public void test_successful_simple_router_transitions()
    {
        setupMockExpectations();
        BuilderFactory builderFactory = ikasanApplication.getBuilderFactory();
        Flow flow = builderFactory.getFlowBuilder("moduleName", "flowName")
                .withDescription("flowDescription")
                .withFlowInvocationContextListeners(Collections.singletonList(flowInvocationContextListener))
                .withExclusionServiceFactory(exclusionServiceFactory)
                .withSerialiserFactory(serialiserFactory)
                .consumer("consumer", consumer)
                .singleRecipientRouter("router", singleRecipientRouter)
                .when("route1", builderFactory.getRouteBuilder().producer("route1Publisher", producer))
                .when("route2", builderFactory.getRouteBuilder().splitter("route2Splitter", splitter).producer("route2Publisher", producer))
                .otherwise(builderFactory.getRouteBuilder().translator("otherwiseTranslator", translator).producer("otherwisePublisher", producer));

        Assert.assertTrue("flow name is incorrect", "flowName".equals(flow.getName()));
        Assert.assertTrue("module name is incorrect", "moduleName".equals(flow.getModuleName()));
        List<FlowElement<?>> flowElements = flow.getFlowElements();
        Assert.assertEquals("Should be 7 flow elements", 7, flowElements.size());
        Assert.assertNotNull("Flow elements cannot be 'null'", flowElements);

        Assert.assertTrue("Should have one FlowInvocationContextListener", flow.getFlowInvocationContextListeners().size() == 1);

        // Consumer
        FlowElement fe = flowElements.get(0);
        Assert.assertTrue("flow element name should be 'consumer'", "consumer".equals(fe.getComponentName()));
        Assert.assertTrue("flow element component should be an instance of Consumer", fe.getFlowComponent() instanceof Consumer);
        Assert.assertTrue("flow element should have a single transition", fe.getTransitions().size() == 1);

        // SRR
        fe = (FlowElement)fe.getTransitions().get("default");
        Assert.assertTrue("flow element name should be 'router'", "router".equals(fe.getComponentName()));
        Assert.assertTrue("flow element component should be an instance of SRR", fe.getFlowComponent() instanceof SingleRecipientRouter);
        Assert.assertTrue("flow element SRR shuold have 3 transitions not [" + fe.getTransitions().size() + "]", fe.getTransitions().size() == 3);

        // when route1
        FlowElement feRoute1 = (FlowElement)fe.getTransitions().get("route1");
        Assert.assertTrue("flow element name should be 'route1Publisher'", "route1Publisher".equals(feRoute1.getComponentName()));
        Assert.assertTrue("flow element transition should be to 0 for producer", feRoute1.getTransitions().size() == 0);

        // when route2
        FlowElement feRoute2 = (FlowElement)fe.getTransitions().get("route2");
        Assert.assertTrue("flow element name should be 'route2Splitter'", "route2Splitter".equals(feRoute2.getComponentName()));
        Assert.assertTrue("flow element component should be an instance of Spitter", feRoute2.getFlowComponent() instanceof Splitter);
        Assert.assertTrue("flow element transition should be to producer", feRoute2.getTransitions().size() == 1);
        FlowElement publisher2FlowElement = (FlowElement)feRoute2.getTransitions().get("default");
        Assert.assertTrue("flow element name should be 'route2Publisher'", "route2Publisher".equals(publisher2FlowElement.getComponentName()));
        Assert.assertTrue("flow element component should be an instance of Producer", publisher2FlowElement.getFlowComponent() instanceof Producer);
        Assert.assertTrue("flow element transition should be 0 for a producer", publisher2FlowElement.getTransitions().size() == 0);

        // otherwise route
        FlowElement feOtherwise = (FlowElement)fe.getTransitions().get("default");
        Assert.assertTrue("flow element name should be 'otherwiseTranslator'", "otherwiseTranslator".equals(feOtherwise.getComponentName()));
        Assert.assertTrue("flow element component should be an instance of Translator", feOtherwise.getFlowComponent() instanceof Translator);
        Assert.assertTrue("flow element transition should be to producer", feOtherwise.getTransitions().size() == 1);
        feOtherwise = (FlowElement)feOtherwise.getTransitions().get("default");
        Assert.assertTrue("flow element name should be 'otherwisePublisher'", "otherwisePublisher".equals(feOtherwise.getComponentName()));
        Assert.assertTrue("flow element component should be an instance of Producer", feOtherwise.getFlowComponent() instanceof Producer);
        Assert.assertTrue("flow element transition should be 0 for a producer", feOtherwise.getTransitions().size() == 0);

        mockery.assertIsSatisfied();
    }

    /**
     * Test successful flow creation.
     */
    @Test
    public void test_successful_simple_router_transitions_with_concurrentSplitter()
    {
        setupMockExpectations();
        BuilderFactory builderFactory = ikasanApplication.getBuilderFactory();
        Flow flow = builderFactory.getFlowBuilder("moduleName", "flowName")
            .withDescription("flowDescription")
            .withFlowInvocationContextListeners(Collections.singletonList(flowInvocationContextListener))
            .withExclusionServiceFactory(exclusionServiceFactory)
            .withSerialiserFactory(serialiserFactory)
            .consumer("consumer", consumer)
            .singleRecipientRouter("router", singleRecipientRouter)
            .when("route1", builderFactory.getRouteBuilder().producer("route1Publisher", producer))
            .when("route2", builderFactory.getRouteBuilder().concurrentSplitter("route2Splitter", splitter).producer("route2Publisher", producer))
            .otherwise(builderFactory.getRouteBuilder().translator("otherwiseTranslator", translator).producer("otherwisePublisher", producer));

        Assert.assertTrue("flow name is incorrect", "flowName".equals(flow.getName()));
        Assert.assertTrue("module name is incorrect", "moduleName".equals(flow.getModuleName()));
        List<FlowElement<?>> flowElements = flow.getFlowElements();
        Assert.assertEquals("Should be 7 flow elements", 7, flowElements.size());
        Assert.assertNotNull("Flow elements cannot be 'null'", flowElements);

        Assert.assertTrue("Should have one FlowInvocationContextListener", flow.getFlowInvocationContextListeners().size() == 1);

        // Consumer
        FlowElement fe = flowElements.get(0);
        Assert.assertTrue("flow element name should be 'consumer'", "consumer".equals(fe.getComponentName()));
        Assert.assertTrue("flow element component should be an instance of Consumer", fe.getFlowComponent() instanceof Consumer);
        Assert.assertTrue("flow element should have a single transition", fe.getTransitions().size() == 1);

        // SRR
        fe = (FlowElement)fe.getTransitions().get("default");
        Assert.assertTrue("flow element name should be 'router'", "router".equals(fe.getComponentName()));
        Assert.assertTrue("flow element component should be an instance of SRR", fe.getFlowComponent() instanceof SingleRecipientRouter);
        Assert.assertTrue("flow element SRR shuold have 3 transitions not [" + fe.getTransitions().size() + "]", fe.getTransitions().size() == 3);

        // when route1
        FlowElement feRoute1 = (FlowElement)fe.getTransitions().get("route1");
        Assert.assertTrue("flow element name should be 'route1Publisher'", "route1Publisher".equals(feRoute1.getComponentName()));
        Assert.assertTrue("flow element transition should be to 0 for producer", feRoute1.getTransitions().size() == 0);

        // when route2
        FlowElement feRoute2 = (FlowElement)fe.getTransitions().get("route2");
        Assert.assertTrue("flow element name should be 'route2Splitter'", "route2Splitter".equals(feRoute2.getComponentName()));
        Assert.assertTrue("flow element component should be an instance of Spitter", feRoute2.getFlowComponent() instanceof Splitter);
        Assert.assertTrue("flow element transition should be to producer", feRoute2.getTransitions().size() == 1);
        FlowElement publisher2FlowElement = (FlowElement)feRoute2.getTransitions().get("default");
        Assert.assertTrue("flow element name should be 'route2Publisher'", "route2Publisher".equals(publisher2FlowElement.getComponentName()));
        Assert.assertTrue("flow element component should be an instance of Producer", publisher2FlowElement.getFlowComponent() instanceof Producer);
        Assert.assertTrue("flow element transition should be 0 for a producer", publisher2FlowElement.getTransitions().size() == 0);

        // otherwise route
        FlowElement feOtherwise = (FlowElement)fe.getTransitions().get("default");
        Assert.assertTrue("flow element name should be 'otherwiseTranslator'", "otherwiseTranslator".equals(feOtherwise.getComponentName()));
        Assert.assertTrue("flow element component should be an instance of Translator", feOtherwise.getFlowComponent() instanceof Translator);
        Assert.assertTrue("flow element transition should be to producer", feOtherwise.getTransitions().size() == 1);
        feOtherwise = (FlowElement)feOtherwise.getTransitions().get("default");
        Assert.assertTrue("flow element name should be 'otherwisePublisher'", "otherwisePublisher".equals(feOtherwise.getComponentName()));
        Assert.assertTrue("flow element component should be an instance of Producer", feOtherwise.getFlowComponent() instanceof Producer);
        Assert.assertTrue("flow element transition should be 0 for a producer", feOtherwise.getTransitions().size() == 0);

        mockery.assertIsSatisfied();
    }

    /**
     * Test successful flow creation.
     */
    @Test
    public void test_successful_simple_router_transitions_refer_to_same_subroute()
    {

        setupMockExpectations();
        BuilderFactory builderFactory = ikasanApplication.getBuilderFactory();

        Route r1 = builderFactory.getRouteBuilder().producer("route1Publisher", producer);
        Route r2 = builderFactory.getRouteBuilder().translator("otherwiseTranslator", translator).producer("otherwisePublisher", producer);
        Flow flow = builderFactory.getFlowBuilder("moduleName", "flowName")
            .withDescription("flowDescription")
            .withFlowInvocationContextListeners(Collections.singletonList(flowInvocationContextListener))
            .withExclusionServiceFactory(exclusionServiceFactory)
            .withSerialiserFactory(serialiserFactory)
            .consumer("consumer", consumer)
            .singleRecipientRouter("router", singleRecipientRouter)
            .when("route1", r1)
            .when("route1a", r1)
            .otherwise(r2);

        Assert.assertTrue("flow name is incorrect", "flowName".equals(flow.getName()));
        Assert.assertTrue("module name is incorrect", "moduleName".equals(flow.getModuleName()));
        List<FlowElement<?>> flowElements = flow.getFlowElements();
        Assert.assertEquals("Should be 6 flow elements", 6, flowElements.size());
        Assert.assertNotNull("Flow elements cannot be 'null'", flowElements);

        Assert.assertTrue("Should have one FlowInvocationContextListener", flow.getFlowInvocationContextListeners().size() == 1);

        // Consumer
        FlowElement fe = flowElements.get(0);
        Assert.assertTrue("flow element name should be 'consumer'", "consumer".equals(fe.getComponentName()));
        Assert.assertTrue("flow element component should be an instance of Consumer", fe.getFlowComponent() instanceof Consumer);
        Assert.assertTrue("flow element should have a single transition", fe.getTransitions().size() == 1);


        // SRR
        fe = (FlowElement)fe.getTransitions().get("default");
        Assert.assertTrue("flow element name should be 'router'", "router".equals(fe.getComponentName()));
        Assert.assertTrue("flow element component should be an instance of SRR", fe.getFlowComponent() instanceof SingleRecipientRouter);
        Assert.assertTrue("flow element SRR shuold have 3 transitions not [" + fe.getTransitions().size() + "]", fe.getTransitions().size() == 3);


        // when route1
        FlowElement feRoute1a = (FlowElement)fe.getTransitions().get("route1a");
        Assert.assertTrue("flow element name should be 'route1Publisher'", "route1Publisher".equals(feRoute1a.getComponentName()));
        Assert.assertTrue("flow element transition should be to 0 for producer", feRoute1a.getTransitions().size() == 0);


        // when route1
        FlowElement feRoute1 = (FlowElement)fe.getTransitions().get("route1");
        Assert.assertTrue("flow element name should be 'route1Publisher'", "route1Publisher".equals(feRoute1.getComponentName()));
        Assert.assertTrue("flow element transition should be to 0 for producer", feRoute1.getTransitions().size() == 0);

        mockery.assertIsSatisfied();
    }


    /**
     * Test successful flow creation.
     *
     *  Consumer --> SRR --route1-->    producer
     *                   --route2-->    SRR (route2)    --nestedRoute1--> producer
     *                                                  --nestedRoute2--> producer
     *                                                  --  otherwise --> producer
     *
     *                   --otherwise--> translator  --> producer
     */
    @Test
    public void test_successful_nested_router_transitions()
    {
        setupMockExpectations();
        BuilderFactory builderFactory = ikasanApplication.getBuilderFactory();
        Route nestedRoute1 = builderFactory.getRouteBuilder().producer("nestedRoute1-publisher1", producer);
        Route nestedRoute2 = builderFactory.getRouteBuilder().producer("nestedRoute2-publisher2", producer);
        Route nestedRoute3 = builderFactory.getRouteBuilder().producer("nestedRoute3-publisher3", producer);
        Route route2 = builderFactory.getRouteBuilder().singleRecipientRouter("nestedSRR", singleRecipientRouter)
                .when("nestedRoute1", nestedRoute1)
                .when("nestedRoute2", nestedRoute2)
                .otherwise(nestedRoute3);

        Flow flow = builderFactory.getFlowBuilder("moduleName", "flowName")
                .withDescription("flowDescription")
                .withFlowInvocationContextListeners(Collections.singletonList(flowInvocationContextListener))
                .withExclusionServiceFactory(exclusionServiceFactory)
                .withSerialiserFactory(serialiserFactory)
                .consumer("consumer", consumer)
                .singleRecipientRouter("router", singleRecipientRouter)
                .when("route1", builderFactory.getRouteBuilder().producer("whenPublisher1", producer))
                .when("route2", route2)
                .otherwise(builderFactory.getRouteBuilder().translator("otherwiseTranslator", translator).producer("otherwisePublisher", producer));

        Assert.assertTrue("flow name is incorrect", "flowName".equals(flow.getName()));
        Assert.assertTrue("module name is incorrect", "moduleName".equals(flow.getModuleName()));
        List<FlowElement<?>> flowElements = flow.getFlowElements();
        Assert.assertNotNull("Flow elements cannot be 'null'", flowElements);
        Assert.assertTrue("Should be 9 flow elements", flowElements.size() == 9);

        Assert.assertTrue("Should have one FlowInvocationContextListener", flow.getFlowInvocationContextListeners().size() == 1);

        // Consumer
        FlowElement fe = flowElements.get(0);
        Assert.assertTrue("flow element name should be 'consumer'", "consumer".equals(fe.getComponentName()));
        Assert.assertTrue("flow element component should be an instance of Consumer", fe.getFlowComponent() instanceof Consumer);
        Assert.assertTrue("flow element should have a single transition", fe.getTransitions().size() == 1);

        // SRR
        fe = (FlowElement)fe.getTransitions().get("default");
        Assert.assertTrue("flow element name should be 'router'", "router".equals(fe.getComponentName()));
        Assert.assertTrue("flow element component should be an instance of SRR", fe.getFlowComponent() instanceof SingleRecipientRouter);
        Assert.assertTrue("flow element SRR shuold have 3 transitions not [" + fe.getTransitions().size() + "]", fe.getTransitions().size() == 3);

        // when route1
        FlowElement feRoute1 = (FlowElement)fe.getTransitions().get("route1");
        Assert.assertTrue("flow element name should be 'whenPublisher1'", "whenPublisher1".equals(feRoute1.getComponentName()));
        Assert.assertTrue("flow element transition should be to 0 for producer", feRoute1.getTransitions().size() == 0);

        // when route2
        FlowElement feRoute2 = (FlowElement)fe.getTransitions().get("route2");
        Assert.assertTrue("flow element name should be 'nestedSRR'", "nestedSRR".equals(feRoute2.getComponentName()));
        Assert.assertTrue("flow element SRR shuold have 3 transitions not [" + feRoute2.getTransitions().size() + "]", feRoute2.getTransitions().size() == 3);

        // route2 nested components
        FlowElement nestedFeRoute1 = (FlowElement)feRoute2.getTransitions().get("nestedRoute1");
        Assert.assertTrue("flow element name should be 'nestedRoute1-publisher1'", "nestedRoute1-publisher1".equals(nestedFeRoute1.getComponentName()));
        Assert.assertTrue("flow element transition should be to 0 for producer", nestedFeRoute1.getTransitions().size() == 0);
        FlowElement nestedFeRoute2 = (FlowElement)feRoute2.getTransitions().get("nestedRoute2");
        Assert.assertTrue("flow element name should be 'nestedRoute2-publisher2'", "nestedRoute2-publisher2".equals(nestedFeRoute2.getComponentName()));
        Assert.assertTrue("flow element transition should be to 0 for producer", nestedFeRoute2.getTransitions().size() == 0);
        FlowElement nestedFeOtherwise = (FlowElement)feRoute2.getTransitions().get("default");
        Assert.assertTrue("flow element name should be 'nestedRoute3-publisher3'", "nestedRoute3-publisher3".equals(nestedFeOtherwise.getComponentName()));
        Assert.assertTrue("flow element transition should be to 0 for producer", nestedFeOtherwise.getTransitions().size() == 0);

        // otherwise route
        FlowElement feOtherwise = (FlowElement)fe.getTransitions().get("default");
        Assert.assertTrue("flow element name should be 'otherwiseTranslator'", "otherwiseTranslator".equals(feOtherwise.getComponentName()));
        Assert.assertTrue("flow element component should be an instance of Translator", feOtherwise.getFlowComponent() instanceof Translator);
        Assert.assertTrue("flow element transition should be to producer", feOtherwise.getTransitions().size() == 1);
        feOtherwise = (FlowElement)feOtherwise.getTransitions().get("default");
        Assert.assertTrue("flow element name should be 'otherwisePublisher'", "otherwisePublisher".equals(feOtherwise.getComponentName()));
        Assert.assertTrue("flow element component should be an instance of Producer", feOtherwise.getFlowComponent() instanceof Producer);
        Assert.assertTrue("flow element transition should be 0 for a producer", feOtherwise.getTransitions().size() == 0);

        mockery.assertIsSatisfied();
    }

    @Test
    public void test_successful_nested_router_transitions_pointing_to_same_subroute()
    {
        setupMockExpectations();
        BuilderFactory builderFactory = ikasanApplication.getBuilderFactory();
        Route nestedRoute1 = builderFactory.getRouteBuilder().producer("nestedRoute-publisher1", producer);
        Route route2 = builderFactory.getRouteBuilder().singleRecipientRouter("nestedSRR", singleRecipientRouter)
            .when("nestedRoute1", nestedRoute1)
            .when("nestedRoute2", nestedRoute1)
            .otherwise(nestedRoute1);

        Flow flow = builderFactory.getFlowBuilder("moduleName", "flowName")
            .withDescription("flowDescription")
            .withFlowInvocationContextListeners(Collections.singletonList(flowInvocationContextListener))
            .withExclusionServiceFactory(exclusionServiceFactory)
            .withSerialiserFactory(serialiserFactory)
            .consumer("consumer", consumer)
            .singleRecipientRouter("router", singleRecipientRouter)
            .when("route1", builderFactory.getRouteBuilder().producer("whenPublisher1", producer))
            .when("route2", route2)
            .otherwise(builderFactory.getRouteBuilder().translator("otherwiseTranslator", translator).producer("otherwisePublisher", producer));

        Assert.assertTrue("flow name is incorrect", "flowName".equals(flow.getName()));
        Assert.assertTrue("module name is incorrect", "moduleName".equals(flow.getModuleName()));
        List<FlowElement<?>> flowElements = flow.getFlowElements();
        Assert.assertNotNull("Flow elements cannot be 'null'", flowElements);
        Assert.assertTrue("Should be 9 flow elements", flowElements.size() == 9);

        Assert.assertTrue("Should have one FlowInvocationContextListener", flow.getFlowInvocationContextListeners().size() == 1);

        // Consumer
        FlowElement fe = flowElements.get(0);
        Assert.assertTrue("flow element name should be 'consumer'", "consumer".equals(fe.getComponentName()));
        Assert.assertTrue("flow element component should be an instance of Consumer", fe.getFlowComponent() instanceof Consumer);
        Assert.assertTrue("flow element should have a single transition", fe.getTransitions().size() == 1);

        // SRR
        fe = (FlowElement)fe.getTransitions().get("default");
        Assert.assertTrue("flow element name should be 'router'", "router".equals(fe.getComponentName()));
        Assert.assertTrue("flow element component should be an instance of SRR", fe.getFlowComponent() instanceof SingleRecipientRouter);
        Assert.assertTrue("flow element SRR shuold have 3 transitions not [" + fe.getTransitions().size() + "]", fe.getTransitions().size() == 3);

        // when route1
        FlowElement feRoute1 = (FlowElement)fe.getTransitions().get("route1");
        Assert.assertTrue("flow element name should be 'whenPublisher1'", "whenPublisher1".equals(feRoute1.getComponentName()));
        Assert.assertTrue("flow element transition should be to 0 for producer", feRoute1.getTransitions().size() == 0);

        // when route2
        FlowElement feRoute2 = (FlowElement)fe.getTransitions().get("route2");
        Assert.assertTrue("flow element name should be 'nestedSRR'", "nestedSRR".equals(feRoute2.getComponentName()));
        Assert.assertTrue("flow element SRR shuold have 3 transitions not [" + feRoute2.getTransitions().size() + "]", feRoute2.getTransitions().size() == 3);

        // route2 nested components
        FlowElement nestedFeRoute1 = (FlowElement)feRoute2.getTransitions().get("nestedRoute1");
        Assert.assertTrue("flow element name should be 'nestedRoute-publisher1'", "nestedRoute-publisher1".equals(nestedFeRoute1.getComponentName()));
        Assert.assertTrue("flow element transition should be to 0 for producer", nestedFeRoute1.getTransitions().size() == 0);
        FlowElement nestedFeRoute2 = (FlowElement)feRoute2.getTransitions().get("nestedRoute2");
        Assert.assertTrue("flow element name should be 'nestedRoute-publisher1'", "nestedRoute-publisher1".equals(nestedFeRoute2.getComponentName()));
        Assert.assertTrue("flow element transition should be to 0 for producer", nestedFeRoute2.getTransitions().size() == 0);
        FlowElement nestedFeOtherwise = (FlowElement)feRoute2.getTransitions().get("default");
        Assert.assertTrue("flow element name should be 'nestedRoute-publisher1'", "nestedRoute-publisher1".equals(nestedFeOtherwise.getComponentName()));
        Assert.assertTrue("flow element transition should be to 0 for producer", nestedFeOtherwise.getTransitions().size() == 0);

        // otherwise route
        FlowElement feOtherwise = (FlowElement)fe.getTransitions().get("default");
        Assert.assertTrue("flow element name should be 'otherwiseTranslator'", "otherwiseTranslator".equals(feOtherwise.getComponentName()));
        Assert.assertTrue("flow element component should be an instance of Translator", feOtherwise.getFlowComponent() instanceof Translator);
        Assert.assertTrue("flow element transition should be to producer", feOtherwise.getTransitions().size() == 1);
        feOtherwise = (FlowElement)feOtherwise.getTransitions().get("default");
        Assert.assertTrue("flow element name should be 'otherwisePublisher'", "otherwisePublisher".equals(feOtherwise.getComponentName()));
        Assert.assertTrue("flow element component should be an instance of Producer", feOtherwise.getFlowComponent() instanceof Producer);
        Assert.assertTrue("flow element transition should be 0 for a producer", feOtherwise.getTransitions().size() == 0);

        mockery.assertIsSatisfied();
    }

    /**
     * Test successful flow creation.
     * deprecated with the test above
     */
    @Test
    public void test_successful_router_transitions()
    {
        setupMockExpectations();
        BuilderFactory builderFactory = ikasanApplication.getBuilderFactory();

        ExceptionResolver exceptionResolver = builderFactory.getExceptionResolverBuilder()
                .addExceptionToAction(Exception.class, OnException.ignoreException()).build();

        Route route1 = builderFactory.getRouteBuilder().producer("producer", producer);
        Route nestedRoute1 = builderFactory.getRouteBuilder().translator("nestedRoute1-name1",translator).producer("nestedRoute1-publisher1", producer);
        Route nestedRoute2 = builderFactory.getRouteBuilder().translator("nestedRoute2-name2",translator).producer("nestedRoute2-publisher2", producer);
        Route nestedRoute3 = builderFactory.getRouteBuilder().translator("nestedRoute3-name3",translator).producer("nestedRoute3-publisher3", producer);
        Route route2 = builderFactory.getRouteBuilder().singleRecipientRouter("nestedSRR", singleRecipientRouter)
                .when("nestedRoute1", nestedRoute1)
                .when("nestedRoute2", nestedRoute2)
                .otherwise(nestedRoute3);

        Flow flow = builderFactory.getFlowBuilder("moduleName", "flowName")
                .withDescription("flowDescription")
                .withFlowInvocationContextListeners(Collections.singletonList(flowInvocationContextListener))
                .withExclusionServiceFactory(exclusionServiceFactory)
                .withSerialiserFactory(serialiserFactory).withExceptionResolver(exceptionResolver)
                .consumer("consumer", consumer)
                .singleRecipientRouter("router", singleRecipientRouter)
                .when("route1", route1)
                .when("route2", route2)
                .otherwise(builderFactory.getRouteBuilder().translator("name4", translator).producer("publisher4", producer));

        Assert.assertTrue("flow name is incorrect", "flowName".equals(flow.getName()));
        Assert.assertTrue("module name is incorrect", "moduleName".equals(flow.getModuleName()));
        List<FlowElement<?>> flowElements = flow.getFlowElements();
        Assert.assertTrue("Should be 12 flow elements", flowElements.size() == 12);
        Assert.assertNotNull("Flow elements cannot be 'null'", flowElements);

        Assert.assertTrue("Should have one FlowInvocationContextListener", flow.getFlowInvocationContextListeners().size() == 1);

        // Consumer
        FlowElement fe = flowElements.get(0);
        Assert.assertTrue("flow element name should be 'consumer'", "consumer".equals(fe.getComponentName()));
        Assert.assertTrue("flow element component should be an instance of Consumer", fe.getFlowComponent() instanceof Consumer);
        Assert.assertTrue("flow element should have a single transition", fe.getTransitions().size() == 1);

        // SRR
        fe = (FlowElement)fe.getTransitions().get("default");
        Assert.assertTrue("flow element name should be 'router'", "router".equals(fe.getComponentName()));
        Assert.assertTrue("flow element component should be an instance of SRR", fe.getFlowComponent() instanceof SingleRecipientRouter);
        Assert.assertTrue("flow element SRR shuold have 3 transitions not [" + fe.getTransitions().size() + "]", fe.getTransitions().size() == 3);

        // when route1
        FlowElement feRoute1 = (FlowElement)fe.getTransitions().get("route1");
        Assert.assertTrue("flow element name should be 'producer'", "producer".equals(feRoute1.getComponentName()));
        Assert.assertTrue("flow element component should be an instance of Producer", feRoute1.getFlowComponent() instanceof Producer);

        // when route2
        FlowElement feRoute2 = (FlowElement)fe.getTransitions().get("route2");
        Assert.assertTrue("flow element name should be 'nestedSRR'", "nestedSRR".equals(feRoute2.getComponentName()));
        Assert.assertTrue("flow element component should be an instance of SRR", feRoute2.getFlowComponent() instanceof SingleRecipientRouter);
        Assert.assertTrue("flow element SRR shuold have 3 transitions not [" + feRoute2.getTransitions().size() + "]", feRoute2.getTransitions().size() == 3);

        // route2 nested components
        // route2 nestedRoute1
        FlowElement feNestedRoute1 = (FlowElement)feRoute2.getTransitions().get("nestedRoute1");
        Assert.assertTrue("flow element name should be 'nestedRoute1-name1'", "nestedRoute1-name1".equals(feNestedRoute1.getComponentName()));
        Assert.assertTrue("flow element component should be an instance of Translator", feNestedRoute1.getFlowComponent() instanceof Translator);
        feNestedRoute1 = (FlowElement)feNestedRoute1.getTransitions().get("default");
        Assert.assertTrue("flow element name should be 'nestedRoute1-publisher1'", "nestedRoute1-publisher1".equals(feNestedRoute1.getComponentName()));
        Assert.assertTrue("flow element component should be an instance of Producer", feNestedRoute1.getFlowComponent() instanceof Producer);

        // route2 nestedRoute2
        FlowElement feNestedRoute2 = (FlowElement)feRoute2.getTransitions().get("nestedRoute2");
        Assert.assertTrue("flow element name should be 'nestedRoute2-name2'", "nestedRoute2-name2".equals(feNestedRoute2.getComponentName()));
        Assert.assertTrue("flow element component should be an instance of Translator", feNestedRoute2.getFlowComponent() instanceof Translator);
        feNestedRoute2 = (FlowElement)feNestedRoute2.getTransitions().get("default");
        Assert.assertTrue("flow element name should be 'nestedRoute2-publisher2'", "nestedRoute2-publisher2".equals(feNestedRoute2.getComponentName()));
        Assert.assertTrue("flow element component should be an instance of Producer", feNestedRoute2.getFlowComponent() instanceof Producer);

        // route2 nestedOtherwise
        FlowElement feNestedOtherwise = (FlowElement)feRoute2.getTransitions().get("default");
        Assert.assertTrue("flow element name should be 'nestedRoute3-name3'", "nestedRoute3-name3".equals(feNestedOtherwise.getComponentName()));
        Assert.assertTrue("flow element component should be an instance of Translator", feNestedOtherwise.getFlowComponent() instanceof Translator);
        feNestedOtherwise = (FlowElement)feNestedOtherwise.getTransitions().get("default");
        Assert.assertTrue("flow element name should be 'nestedRoute3-publisher3'", "nestedRoute3-publisher3".equals(feNestedOtherwise.getComponentName()));
        Assert.assertTrue("flow element component should be an instance of Producer", feNestedOtherwise.getFlowComponent() instanceof Producer);

        // otherwise
        FlowElement feRouteOtherwise = (FlowElement)fe.getTransitions().get("default");
        Assert.assertTrue("flow element name should be 'name4'", "name4".equals(feRouteOtherwise.getComponentName()));
        Assert.assertTrue("flow element component should be an instance of Translator", feRouteOtherwise.getFlowComponent() instanceof Translator);
        Assert.assertTrue("flow element transition should be to prodcuer", feRouteOtherwise.getTransitions().size() == 1);
        feRouteOtherwise = (FlowElement)feRouteOtherwise.getTransitions().get("default");
        Assert.assertTrue("flow element name should be 'publisher4'", "publisher4".equals(feRouteOtherwise.getComponentName()));
        Assert.assertTrue("flow element component should be an instance of Producer", feRouteOtherwise.getFlowComponent() instanceof Producer);
        Assert.assertTrue("flow element transition should be to producer", feRouteOtherwise.getTransitions().size() == 0);

        mockery.assertIsSatisfied();
    }

    /**
     * Test successful flow creation.
     * deprecated with the test above
     */
    @Test
    public void test_successful_multiReceipientRouter_transitions()
    {
        setupMockExpectations();
        BuilderFactory builderFactory = ikasanApplication.getBuilderFactory();

        ExceptionResolver exceptionResolver = builderFactory.getExceptionResolverBuilder()
                .addExceptionToAction(Exception.class, OnException.ignoreException()).build();

        Route route1 = builderFactory.getRouteBuilder().producer("producer", producer);
        Route nestedRoute1 = builderFactory.getRouteBuilder().translator("nestedRoute1-name1",translator).producer("nestedRoute1-publisher1", producer);
        Route nestedRoute2 = builderFactory.getRouteBuilder().translator("nestedRoute2-name2",translator).producer("nestedRoute2-publisher2", producer);
        Route route2 = builderFactory.getRouteBuilder().multiRecipientRouter("nestedMRR", multiRecipientRouter)
                .when("nestedRoute1", nestedRoute1)
                .when("nestedRoute2", nestedRoute2)
                .build();

        Flow flow = builderFactory.getFlowBuilder("moduleName", "flowName")
                .withDescription("flowDescription")
                .withFlowInvocationContextListeners(Collections.singletonList(flowInvocationContextListener))
                .withExclusionServiceFactory(exclusionServiceFactory)
                .withSerialiserFactory(serialiserFactory).withExceptionResolver(exceptionResolver)
                .consumer("consumer", consumer)
                .singleRecipientRouter("router", singleRecipientRouter)
                .when("route1", route1)
                .when("route2", route2)
                .otherwise(builderFactory.getRouteBuilder().translator("name4", translator).producer("publisher4", producer));

        Assert.assertTrue("flow name is incorrect", "flowName".equals(flow.getName()));
        Assert.assertTrue("module name is incorrect", "moduleName".equals(flow.getModuleName()));
        List<FlowElement<?>> flowElements = flow.getFlowElements();
        Assert.assertTrue("Should be 10 flow elements", flowElements.size() == 10);
        Assert.assertNotNull("Flow elements cannot be 'null'", flowElements);

        Assert.assertTrue("Should have one FlowInvocationContextListener", flow.getFlowInvocationContextListeners().size() == 1);

        // Consumer
        FlowElement fe = flowElements.get(0);
        Assert.assertTrue("flow element name should be 'consumer'", "consumer".equals(fe.getComponentName()));
        Assert.assertTrue("flow element component should be an instance of Consumer", fe.getFlowComponent() instanceof Consumer);
        Assert.assertTrue("flow element should have a single transition", fe.getTransitions().size() == 1);

        // SRR
        fe = (FlowElement)fe.getTransitions().get("default");
        Assert.assertTrue("flow element name should be 'router'", "router".equals(fe.getComponentName()));
        Assert.assertTrue("flow element component should be an instance of SRR", fe.getFlowComponent() instanceof SingleRecipientRouter);
        Assert.assertTrue("flow element MRR should have 3 transitions not [" + fe.getTransitions().size() + "]", fe.getTransitions().size() == 3);

        // when route1
        FlowElement feRoute1 = (FlowElement)fe.getTransitions().get("route1");
        Assert.assertTrue("flow element name should be 'producer'", "producer".equals(feRoute1.getComponentName()));
        Assert.assertTrue("flow element component should be an instance of Producer", feRoute1.getFlowComponent() instanceof Producer);

        // when route2
        FlowElement feRoute2 = (FlowElement)fe.getTransitions().get("route2");
        Assert.assertTrue("flow element name should be 'nestedMRR'", "nestedMRR".equals(feRoute2.getComponentName()));
        Assert.assertTrue("flow element component should be an instance of MRR", feRoute2.getFlowComponent() instanceof MultiRecipientRouter);
        Assert.assertTrue("flow element MRR should have 2 transitions not [" + feRoute2.getTransitions().size() + "]", feRoute2.getTransitions().size() == 2);

        // route2 nested components
        // route2 nestedRoute1
        FlowElement feNestedRoute1 = (FlowElement)feRoute2.getTransitions().get("nestedRoute1");
        Assert.assertTrue("flow element name should be 'nestedRoute1-name1'", "nestedRoute1-name1".equals(feNestedRoute1.getComponentName()));
        Assert.assertTrue("flow element component should be an instance of Translator", feNestedRoute1.getFlowComponent() instanceof Translator);
        feNestedRoute1 = (FlowElement)feNestedRoute1.getTransitions().get("default");
        Assert.assertTrue("flow element name should be 'nestedRoute1-publisher1'", "nestedRoute1-publisher1".equals(feNestedRoute1.getComponentName()));
        Assert.assertTrue("flow element component should be an instance of Producer", feNestedRoute1.getFlowComponent() instanceof Producer);

        // route2 nestedRoute2
        FlowElement feNestedRoute2 = (FlowElement)feRoute2.getTransitions().get("nestedRoute2");
        Assert.assertTrue("flow element name should be 'nestedRoute2-name2'", "nestedRoute2-name2".equals(feNestedRoute2.getComponentName()));
        Assert.assertTrue("flow element component should be an instance of Translator", feNestedRoute2.getFlowComponent() instanceof Translator);
        feNestedRoute2 = (FlowElement)feNestedRoute2.getTransitions().get("default");
        Assert.assertTrue("flow element name should be 'nestedRoute2-publisher2'", "nestedRoute2-publisher2".equals(feNestedRoute2.getComponentName()));
        Assert.assertTrue("flow element component should be an instance of Producer", feNestedRoute2.getFlowComponent() instanceof Producer);

        // otherwise
        FlowElement feRouteOtherwise = (FlowElement)fe.getTransitions().get("default");
        Assert.assertTrue("flow element name should be 'name4'", "name4".equals(feRouteOtherwise.getComponentName()));
        Assert.assertTrue("flow element component should be an instance of Translator", feRouteOtherwise.getFlowComponent() instanceof Translator);
        Assert.assertTrue("flow element transition should be to producer", feRouteOtherwise.getTransitions().size() == 1);
        feRouteOtherwise = (FlowElement)feRouteOtherwise.getTransitions().get("default");
        Assert.assertTrue("flow element name should be 'publisher4'", "publisher4".equals(feRouteOtherwise.getComponentName()));
        Assert.assertTrue("flow element component should be an instance of Producer", feRouteOtherwise.getFlowComponent() instanceof Producer);
        Assert.assertTrue("flow element transition should be to producer", feRouteOtherwise.getTransitions().size() == 0);

        mockery.assertIsSatisfied();
    }

    /**
     * Test successful flow creation.
     * deprecated with the test above
     */
    @Test
    public void test_successful_multiReceipientRouter_transitions_with_mmr_invoker_configuration()
    {
        setupMockExpectations();
        BuilderFactory builderFactory = ikasanApplication.getBuilderFactory();

        ExceptionResolver exceptionResolver = builderFactory.getExceptionResolverBuilder()
                .addExceptionToAction(Exception.class, OnException.ignoreException()).build();

        Route route1 = builderFactory.getRouteBuilder().producer("producer", producer);
        Route nestedRoute1 = builderFactory.getRouteBuilder().translator("nestedRoute1-name1",translator).producer("nestedRoute1-publisher1", producer);
        Route nestedRoute2 = builderFactory.getRouteBuilder().translator("nestedRoute2-name2",translator).producer("nestedRoute2-publisher2", producer);
        Route nestedRoute3 = builderFactory.getRouteBuilder().translator("nestedRoute3-name3",translator).producer("nestedRoute3-publisher3", producer);

        MultiRecipientRouterInvokerConfiguration invokerConfiguration = InvokerConfigurationFactory.multiRecipientRouterInvokerConfiguration();
        invokerConfiguration.setCloneEventPerRoute(false);
        Route route2 = builderFactory.getRouteBuilder().multiRecipientRouter("nestedMRR", multiRecipientRouter, invokerConfiguration)
                .when("nestedRoute1", nestedRoute1)
                .when("nestedRoute2", nestedRoute2)
                .when("nestedRoute3", nestedRoute3)
                .build();

        Flow flow = builderFactory.getFlowBuilder("moduleName", "flowName")
                .withDescription("flowDescription")
                .withFlowInvocationContextListeners(Collections.singletonList(flowInvocationContextListener))
                .withExclusionServiceFactory(exclusionServiceFactory)
                .withSerialiserFactory(serialiserFactory).withExceptionResolver(exceptionResolver)
                .consumer("consumer", consumer)
                .singleRecipientRouter("router", singleRecipientRouter)
                .when("route1", route1)
                .when("route2", route2)
                .otherwise(builderFactory.getRouteBuilder().translator("name4", translator).producer("publisher4", producer));

        Assert.assertTrue("flow name is incorrect", "flowName".equals(flow.getName()));
        Assert.assertTrue("module name is incorrect", "moduleName".equals(flow.getModuleName()));
        List<FlowElement<?>> flowElements = flow.getFlowElements();
        Assert.assertTrue("Should be 12 flow elements", flowElements.size() == 12);
        Assert.assertNotNull("Flow elements cannot be 'null'", flowElements);

        Assert.assertTrue("Should have one FlowInvocationContextListener", flow.getFlowInvocationContextListeners().size() == 1);

        // Consumer
        FlowElement fe = flowElements.get(0);
        Assert.assertTrue("flow element name should be 'consumer'", "consumer".equals(fe.getComponentName()));
        Assert.assertTrue("flow element component should be an instance of Consumer", fe.getFlowComponent() instanceof Consumer);
        Assert.assertTrue("flow element should have a single transition", fe.getTransitions().size() == 1);

        // SRR
        fe = (FlowElement)fe.getTransitions().get("default");
        Assert.assertTrue("flow element name should be 'router'", "router".equals(fe.getComponentName()));
        Assert.assertTrue("flow element component should be an instance of SRR", fe.getFlowComponent() instanceof SingleRecipientRouter);
        Assert.assertTrue("flow element MRR shuold have 3 transitions not [" + fe.getTransitions().size() + "]", fe.getTransitions().size() == 3);

        // when route1
        FlowElement feRoute1 = (FlowElement)fe.getTransitions().get("route1");
        Assert.assertTrue("flow element name should be 'producer'", "producer".equals(feRoute1.getComponentName()));
        Assert.assertTrue("flow element component should be an instance of Producer", feRoute1.getFlowComponent() instanceof Producer);

        // when route2
        FlowElement feRoute2 = (FlowElement)fe.getTransitions().get("route2");
        Assert.assertTrue("flow element name should be 'nestedMRR'", "nestedMRR".equals(feRoute2.getComponentName()));
        Assert.assertTrue("flow element component should be an instance of MRR", feRoute2.getFlowComponent() instanceof MultiRecipientRouter);
        Assert.assertTrue("flow element MRR should have 3 transitions not [" + feRoute2.getTransitions().size() + "]", feRoute2.getTransitions().size() == 3);

        // route2 nested components
        // route2 nestedRoute1
        FlowElement feNestedRoute1 = (FlowElement)feRoute2.getTransitions().get("nestedRoute1");
        Assert.assertTrue("flow element name should be 'nestedRoute1-name1'", "nestedRoute1-name1".equals(feNestedRoute1.getComponentName()));
        Assert.assertTrue("flow element component should be an instance of Translator", feNestedRoute1.getFlowComponent() instanceof Translator);
        feNestedRoute1 = (FlowElement)feNestedRoute1.getTransitions().get("default");
        Assert.assertTrue("flow element name should be 'nestedRoute1-publisher1'", "nestedRoute1-publisher1".equals(feNestedRoute1.getComponentName()));
        Assert.assertTrue("flow element component should be an instance of Producer", feNestedRoute1.getFlowComponent() instanceof Producer);

        // route2 nestedRoute2
        FlowElement feNestedRoute2 = (FlowElement)feRoute2.getTransitions().get("nestedRoute2");
        Assert.assertTrue("flow element name should be 'nestedRoute2-name2'", "nestedRoute2-name2".equals(feNestedRoute2.getComponentName()));
        Assert.assertTrue("flow element component should be an instance of Translator", feNestedRoute2.getFlowComponent() instanceof Translator);

        // route2 nestedRoute3
        FlowElement feNestedRoute3 = (FlowElement)feRoute2.getTransitions().get("nestedRoute3");
        Assert.assertTrue("flow element name should be 'nestedRoute3-name3'", "nestedRoute3-name3".equals(feNestedRoute3.getComponentName()));
        Assert.assertTrue("flow element component should be an instance of Translator", feNestedRoute3.getFlowComponent() instanceof Translator);

        // otherwise
        FlowElement feRouteOtherwise = (FlowElement)fe.getTransitions().get("default");
        Assert.assertTrue("flow element name should be 'name4'", "name4".equals(feRouteOtherwise.getComponentName()));
        Assert.assertTrue("flow element component should be an instance of Translator", feRouteOtherwise.getFlowComponent() instanceof Translator);
        Assert.assertTrue("flow element transition should be to producer", feRouteOtherwise.getTransitions().size() == 1);
        feRouteOtherwise = (FlowElement)feRouteOtherwise.getTransitions().get("default");
        Assert.assertTrue("flow element name should be 'publisher4'", "publisher4".equals(feRouteOtherwise.getComponentName()));
        Assert.assertTrue("flow element component should be an instance of Producer", feRouteOtherwise.getFlowComponent() instanceof Producer);
        Assert.assertTrue("flow element transition should be to producer", feRouteOtherwise.getTransitions().size() == 0);

        mockery.assertIsSatisfied();
    }

    /**
     * Test successful flow creation.
     */
    @Test
    public void test_successful_sequencer_transitions()
    {
        setupMockExpectations();
        BuilderFactory builderFactory = ikasanApplication.getBuilderFactory();

        Flow flow = builderFactory.getFlowBuilder("moduleName", "flowName")
                .withDescription("flowDescription")
                .withExclusionServiceFactory(exclusionServiceFactory)
                .withSerialiserFactory(serialiserFactory)
                .consumer("consumer", consumer)
                .sequencer("sequencer", sequencer)
                .route("sequence name 1", builderFactory.getRouteBuilder().producer("name1", producer))
                .route("sequence name 2", builderFactory.getRouteBuilder().producer("name2", producer)).build();

        Assert.assertTrue("flow name is incorrect", "flowName".equals(flow.getName()));
        Assert.assertTrue("module name is incorrect", "moduleName".equals(flow.getModuleName()));
        List<FlowElement<?>> flowElements = flow.getFlowElements();
        Assert.assertNotNull("Flow elements cannot be 'null'", flowElements);
        Assert.assertTrue("Should be 4 flow elements", flowElements.size() == 4);

        // Consumer
        FlowElement fe = flowElements.get(0);
        Assert.assertTrue("flow element name should be 'consumer'", "consumer".equals(fe.getComponentName()));
        Assert.assertTrue("flow element component should be an instance of Consumer", fe.getFlowComponent() instanceof Consumer);
        Assert.assertTrue("flow element should have a single transition", fe.getTransitions().size() == 1);

        // Sequencer
        fe = (FlowElement)fe.getTransitions().get("default");
        Assert.assertTrue("flow element name should be 'sequencer'", "sequencer".equals(fe.getComponentName()));
        Assert.assertTrue("flow element component should be an instance of Sequencer", fe.getFlowComponent() instanceof Sequencer);
        Assert.assertTrue("flow element should have two transitions", fe.getTransitions().size() == 2);

        // ensure sequence order
        ArrayList<String> sequenceNames = new ArrayList<String>(fe.getTransitions().keySet());
        Assert.assertTrue("flow element transition sequence should be in order of 'sequence name 1'..." + " returned order is " + sequenceNames, sequenceNames.get(0).equals("sequence name 1"));
        Assert.assertTrue("flow element transition sequence should be in order of ...'sequence name 2'"  + " returned order is " + sequenceNames, sequenceNames.get(1).equals("sequence name 2"));

        // Producer 1
        FlowElement feRoute1 = (FlowElement)fe.getTransitions().get("sequence name 1");
        Assert.assertTrue("flow element name should be 'name1'", "name1".equals(feRoute1.getComponentName()));
        Assert.assertTrue("flow element component should be an instance of Producer", feRoute1.getFlowComponent() instanceof Producer);
        Assert.assertTrue("flow element transition should be null", feRoute1.getTransitions().size() == 0);

        // Producer 2
        FlowElement feRoute2 = (FlowElement)fe.getTransitions().get("sequence name 2");
        Assert.assertTrue("flow element name should be 'name2'", "name2".equals(feRoute2.getComponentName()));
        Assert.assertTrue("flow element component should be an instance of Producer", feRoute2.getFlowComponent() instanceof Producer);
        Assert.assertTrue("flow element transition should be null", feRoute2.getTransitions().size() == 0);

        mockery.assertIsSatisfied();
    }

    /**
     * Test successful flow creation.
     */
    @Test
    public void test_successful_sequencer_nested_transitions()
    {
        setupMockExpectations();
        BuilderFactory builderFactory = ikasanApplication.getBuilderFactory();

        Route nestedRoute1 = builderFactory.getRouteBuilder().producer("name1", producer);
        Route nestedRoute2 = builderFactory.getRouteBuilder().producer("name2", producer);

        Flow flow = builderFactory.getFlowBuilder("moduleName", "flowName")
                .withDescription("flowDescription")
                .withExclusionServiceFactory(exclusionServiceFactory)
                .withSerialiserFactory(serialiserFactory)
                .consumer("consumer", consumer)
                .sequencer("sequencer", sequencer)
                .route("sequence name 1",
                        builderFactory.getRouteBuilder().sequencer("sequencerNested1",sequencer)
                                .route("nestedSeq1", nestedRoute1)
                                .route("nestedSeq1a", nestedRoute1)
                                .route("nestedSeq2", nestedRoute2).build())
                .route("sequence name 2", builderFactory.getRouteBuilder().producer("name3", producer))
                .build();

        Assert.assertTrue("flow name is incorrect", "flowName".equals(flow.getName()));
        Assert.assertTrue("module name is incorrect", "moduleName".equals(flow.getModuleName()));
        List<FlowElement<?>> flowElements = flow.getFlowElements();
        Assert.assertNotNull("Flow elements cannot be 'null'", flowElements);
        Assert.assertTrue("Should be 6 flow elements was [" + flowElements.size() + "]", flowElements.size() == 7);

        // consumer
        FlowElement fe = flowElements.get(0);
        Assert.assertTrue("flow element name should be 'consumer'", "consumer".equals(fe.getComponentName()));
        Assert.assertTrue("flow element component should be an instance of Consumer", fe.getFlowComponent() instanceof Consumer);
        Assert.assertTrue("flow element should have a single transition", fe.getTransitions().size() == 1);

        // sequencer
        fe = (FlowElement)fe.getTransitions().get("default");
        Assert.assertTrue("flow element name should be 'sequencer'", "sequencer".equals(fe.getComponentName()));
        Assert.assertTrue("flow element component should be an instance of Sequencer", fe.getFlowComponent() instanceof Sequencer);
        Assert.assertTrue("flow element should have two transitions", fe.getTransitions().size() == 2);

        // ensure sequence order
        ArrayList<String> sequenceNames = new ArrayList<String>(fe.getTransitions().keySet());
        Assert.assertTrue("flow element transition sequence should be in order of 'sequence name 1'..." + " returned order is " + sequenceNames, sequenceNames.get(0).equals("sequence name 1"));
        Assert.assertTrue("flow element transition sequence should be in order of ...'sequence name 2'"  + " returned order is " + sequenceNames, sequenceNames.get(1).equals("sequence name 2"));

        // nested sequencer
        FlowElement feRoute1 = (FlowElement)fe.getTransitions().get("sequence name 1");
        Assert.assertTrue("flow element name should be 'sequencerNested1'", "sequencerNested1".equals(feRoute1.getComponentName()));
        Assert.assertTrue("flow element component should be an instance of Sequencer", feRoute1.getFlowComponent() instanceof Sequencer);
        Assert.assertTrue("flow element should have three transitions", feRoute1.getTransitions().size() == 3);

        // ensure sequence order
        ArrayList<String> nestedSequenceNames = new ArrayList<String>(feRoute1.getTransitions().keySet());
        Assert.assertTrue("flow element transition sequence should be in order of 'nestedSeq1'..." + " returned order is " + nestedSequenceNames, nestedSequenceNames.get(0).equals("nestedSeq1"));
        Assert.assertTrue("flow element transition sequence should be in order of ...'sequence name 1a'"  + " returned order is " + nestedSequenceNames, nestedSequenceNames.get(1).equals("nestedSeq1a"));
        Assert.assertTrue("flow element transition sequence should be in order of ...'sequence name 2'"  + " returned order is " + nestedSequenceNames, nestedSequenceNames.get(2).equals("nestedSeq2"));

        // nested sequencer1 nestedSeq1
        FlowElement feNestedRoute1 = (FlowElement)feRoute1.getTransitions().get("nestedSeq1");
        Assert.assertTrue("flow element name should be 'name1'", "name1".equals(feNestedRoute1.getComponentName()));
        Assert.assertTrue("flow element component should be an instance of Producer", feNestedRoute1.getFlowComponent() instanceof Producer);
        Assert.assertTrue("flow element should have null transitions", feNestedRoute1.getTransitions().size() == 0);

        // nested sequencer1 nestedSeq2
        FlowElement feNestedRoute2 = (FlowElement)feRoute1.getTransitions().get("nestedSeq2");
        Assert.assertTrue("flow element name should be 'name2'", "name2".equals(feNestedRoute2.getComponentName()));
        Assert.assertTrue("flow element component should be an instance of Producer", feNestedRoute1.getFlowComponent() instanceof Producer);
        Assert.assertTrue("flow element should have null transitions", feNestedRoute1.getTransitions().size() == 0);

        // sequencer route2
        FlowElement feRoute2 = (FlowElement)fe.getTransitions().get("sequence name 2");
        Assert.assertTrue("flow element name should be 'name3'", "name3".equals(feRoute2.getComponentName()));
        Assert.assertTrue("flow element component should be an instance of Producer", feRoute2.getFlowComponent() instanceof Producer);
        Assert.assertTrue("flow element transition should be null", feRoute2.getTransitions().size() == 0);

        mockery.assertIsSatisfied();
    }

    /**
     * Test successful flow creation.
     */
    @Test
    public void test_successful_concurrentSplitter() throws InterruptedException
    {
        BuilderFactory builderFactory = ikasanApplication.getBuilderFactory();

        RecordingProducer recordingProducer = new RecordingProducer();

        Flow flow = builderFactory.getFlowBuilder("moduleName", "flowName")
            .withDescription("flowDescription")
            .consumer("consumer", builderFactory.getComponentBuilder().eventGeneratingConsumer().setEndpointEventProvider( new ListEventsEndpointEndProvider() ))
            .splitter("split", builderFactory.getComponentBuilder().listSplitter())
            .producer("producer", recordingProducer)
            .build();

        flow.start();
        while(flow.isRunning())
        {
            Thread.sleep(100);
        }

        List recorded = recordingProducer.getRecorded();
        mockery.assertIsSatisfied();
    }

    /**
     * Test successful flow creation.
     * This test is to ensure we can pass exceptionResolver with explicit instance or builder instance
     */
    @Test
    public void test_successful_withExceptionResolver_implied_build()
    {
        BuilderFactory builderFactory = ikasanApplication.getBuilderFactory();

        // explicit exceptionResolver
        builderFactory.getFlowBuilder("moduleName", "flowName")
                .withExceptionResolver( builderFactory.getExceptionResolverBuilder().build() );

        // implied build on exceptionResolver
        builderFactory.getFlowBuilder("moduleName", "flowName")
                .withExceptionResolver( builderFactory.getExceptionResolverBuilder() );
    }

    /**
     * Test successful flow creation.
     * This test is to ensure we can pass monitor with explicit instance or builder instance
     */
    @Test
    public void test_successful_withMonitor_implied_build()
    {
        BuilderFactory builderFactory = ikasanApplication.getBuilderFactory();

        // explicit build() call
        builderFactory.getFlowBuilder("moduleName", "flowName")
                .withMonitor( builderFactory.getMonitorBuilder().withFlowStateChangeMonitor().build() );

        // implied build call
        builderFactory.getFlowBuilder("moduleName", "flowName")
                .withDescription("flowDescription")
                .withMonitor( builderFactory.getMonitorBuilder().withFlowStateChangeMonitor() );
    }

    /**
     * Test successful flow creation.
     * This test is to ensure we can pass monitor with explicit instance or builder instance
     */
    @Test
    public void test_successful_generateConfiguredInstance()
    {
        FlowBuilder flowBuilder = ikasanApplication.getBuilderFactory().getFlowBuilder("moduleName", "flowName");

        // test configured instance with specific configuration bean
        Configured configured = new ConfiguredWithMyConfiguration();
        Object object = flowBuilder.generateConfiguredInstance(configured);
        Assert.assertTrue("object is not an instanceof KyConfiguration", object instanceof MyConfiguration);

        // test configured instance with generic configuration bean
        configured = new ConfiguredWithT();
        object = flowBuilder.generateConfiguredInstance(configured);
        Assert.assertNull(object);
    }

    /**
     * Test successful flow creation.
     */
    @Test
    public void test_successful_consumer_implied_and_explicit_builds()
    {
        BuilderFactory builderFactory = ikasanApplication.getBuilderFactory();

        // implicit
        builderFactory.getFlowBuilder("moduleName", "flowName")
                .consumer("consumerName", builderFactory.getComponentBuilder().scheduledConsumer())
                .producer("producerName", producer)
                .build();

        // explicit
        builderFactory.getFlowBuilder("moduleName", "flowName")
                .consumer("consumerName", builderFactory.getComponentBuilder().scheduledConsumer().build())
                .producer("producerName", producer)
                .build();

        // implicit
        builderFactory.getFlowBuilder("moduleName", "flowName")
                .consumer("consumerName", builderFactory.getComponentBuilder().fileConsumer())
                .producer("producerName", producer)
                .build();

        // explicit
        builderFactory.getFlowBuilder("moduleName", "flowName")
                .consumer("consumerName", builderFactory.getComponentBuilder().fileConsumer().build())
                .producer("producerName", producer)
                .build();

        // implicit
        builderFactory.getFlowBuilder("moduleName", "flowName")
                .consumer("consumerName", builderFactory.getComponentBuilder().dbConsumer())
                .producer("producerName", producer)
                .build();

        // explicit
        builderFactory.getFlowBuilder("moduleName", "flowName")
                .consumer("consumerName", builderFactory.getComponentBuilder().dbConsumer().build())
                .producer("producerName", producer)
                .build();

        // implicit
        builderFactory.getFlowBuilder("moduleName", "flowName")
                .consumer("consumerName", builderFactory.getComponentBuilder().ftpConsumer())
                .producer("producerName", producer)
                .build();

        // explicit
        builderFactory.getFlowBuilder("moduleName", "flowName")
                .consumer("consumerName", builderFactory.getComponentBuilder().ftpConsumer().build())
                .producer("producerName", producer)
                .build();

        // implicit
        builderFactory.getFlowBuilder("moduleName", "flowName")
                .consumer("consumerName", builderFactory.getComponentBuilder().jmsConsumer())
                .producer("producerName", producer)
                .build();

        // explicit
        builderFactory.getFlowBuilder("moduleName", "flowName")
                .consumer("consumerName", builderFactory.getComponentBuilder().jmsConsumer().build())
                .producer("producerName", producer)
                .build();

        // implicit
        builderFactory.getFlowBuilder("moduleName", "flowName")
                .consumer("consumerName", builderFactory.getComponentBuilder().sftpConsumer())
                .producer("producerName", producer)
                .build();

        // explicit
        builderFactory.getFlowBuilder("moduleName", "flowName")
                .consumer("consumerName", builderFactory.getComponentBuilder().sftpConsumer().build())
                .producer("producerName", producer)
                .build();

        // implicit
        builderFactory.getFlowBuilder("moduleName", "flowName")
                .consumer("consumerName", builderFactory.getComponentBuilder().eventGeneratingConsumer())
                .producer("producerName", producer)
                .build();

        // explicit
        builderFactory.getFlowBuilder("moduleName", "flowName")
                .consumer("consumerName", builderFactory.getComponentBuilder().eventGeneratingConsumer().build())
                .producer("producerName", producer)
                .build();
    }

    class ListEventsEndpointEndProvider implements EndpointEventProvider<List<String>>
    {
        List<List<String>> events = new ArrayList<List<String>>();
        int count = 0;

        public ListEventsEndpointEndProvider()
        {
            List<String> event = new ArrayList<String>();
            event.add("one");
            event.add("two");
            event.add("three");

            events.add(event);
            events.add(null);
        }

        @Override
        public List<String> getEvent()
        {
            return events.get(count++);
        }

        @Override
        public void rollback()
        {
            // ignore
        }
    }

    class RecordingProducer implements Producer
    {
        List<Object> recorded = new ArrayList<Object>();

        @Override
        public void invoke(Object payload) throws EndpointException
        {
            recorded.add(payload);
        }

        public List getRecorded()
        {
            return recorded;
        }
    }

    class ConfiguredWithMyConfiguration implements Configured<MyConfiguration>
    {
        @Override
        public MyConfiguration getConfiguration()
        {
            return null;
        }

        @Override
        public void setConfiguration(MyConfiguration configuration)
        {

        }
    }

    class ConfiguredWithT<T> implements Configured<T>
    {
        @Override
        public T getConfiguration()
        {
            return null;
        }

        @Override
        public void setConfiguration(T configuration)
        {

        }
    }

}
