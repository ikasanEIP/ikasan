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
import org.ikasan.exclusion.service.ExclusionServiceFactory;
import org.ikasan.spec.component.endpoint.Broker;
import org.ikasan.spec.component.endpoint.Consumer;
import org.ikasan.spec.component.endpoint.Producer;
import org.ikasan.spec.component.filter.Filter;
import org.ikasan.spec.component.routing.MultiRecipientRouter;
import org.ikasan.spec.component.routing.SingleRecipientRouter;
import org.ikasan.spec.component.sequencing.Sequencer;
import org.ikasan.spec.component.splitting.Splitter;
import org.ikasan.spec.component.transformation.Converter;
import org.ikasan.spec.component.transformation.Translator;
import org.ikasan.spec.event.EventFactory;
import org.ikasan.spec.exclusion.ExclusionService;
import org.ikasan.spec.flow.FlowElement;
import org.ikasan.spec.flow.FlowInvocationContextListener;
import org.ikasan.spec.serialiser.SerialiserFactory;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.imposters.ByteBuddyClassImposteriser;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.test.util.TestSocketUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * This test class supports the <code>FlowBuilder</code> class.
 *
 * @author Ikasan Development Team
 */
@SuppressWarnings("unchecked")

public class RouteBuilderTest
{
    /**
     * Mockery for mocking concrete classes
     */
    private Mockery mockery = new Mockery()
    {
        {
            setImposteriser(ByteBuddyClassImposteriser.INSTANCE);
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

    // Splitters
    /** Mock Splitter */
    final Splitter splitter = mockery.mock(Splitter.class, "mockSplitter");
    /** Mock Splitter Builder */
    final Builder<Splitter> splitterBuilder = mockery.mock(Builder.class, "mockSplitterBuilder");
    /** Mock Concurrent Splitter Builder */
    final Builder<Splitter> concurrentSplitterBuilder = mockery.mock(Builder.class, "mockConcurrentSplitterBuilder");

    /** Mock Converter */
    final Converter converter = mockery.mock(Converter.class, "mockConverter");
    /** Mock Converter Builder */
    final Builder<Converter> converterBuilder = mockery.mock(Builder.class, "mockConverterBuilder");

    // Routers
    /** Mock SRR Router */
    final SingleRecipientRouter singleRecipientRouter = mockery.mock(SingleRecipientRouter.class, "mockSingleRecipientRouter");
    /** Mock SRR Router Builder */
    final Builder<SingleRecipientRouter> singleRecipientRouterBuilder = mockery.mock(Builder.class, "mockSingleRecipientRouterBuilder");
    /** Mock MRR Router */
    final MultiRecipientRouter multiRecipientRouter = mockery.mock(MultiRecipientRouter.class, "mockMultiRecipientRouter");
    /** Mock MRR Router Builder */
    final Builder<MultiRecipientRouter> multiRecipientRouterBuilder = mockery.mock(Builder.class, "mockMultiRecipientRouterBuilder");

    // filters
    /** Mock Filters */
    final Filter filter = mockery.mock(Filter.class, "mockFilter");
    /** Mock Filter Builder */
    final Builder<Filter> filterBuilder = mockery.mock(Builder.class, "mockFilterBuilder");

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

    final EventFactory eventFactory = mockery.mock(EventFactory.class, "eventFactory");

    IkasanApplication ikasanApplication;

    @Before
    public void setup()
    {

        String[] args = { "--server.port=" + TestSocketUtils.findAvailableTcpPort(),
            "--spring.liquibase.change-log=classpath:db-changelog.xml",
            "--server.tomcat.additional-tld-skip-patterns=xercesImpl.jar,xml-apis.jar,serializer.jar",
            """
            --spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration\
            ,org.springframework.boot.autoconfigure.quartz.QuartzAutoConfiguration\
            ,org.springframework.boot.autoconfigure.security.servlet.SecurityFilterAutoConfiguration\
            ,me.snowdrop.boot.narayana.autoconfigure.NarayanaConfiguration\
            ,org.springframework.boot.autoconfigure.context.MessageSourceAutoConfiguration\
            """

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
        List<FlowElement> flowELements = new ArrayList<FlowElement>();
        RouteBuilder routeBuilder = new RouteBuilder( new RouteImpl(flowELements), eventFactory);

        mockery.checking(new Expectations()
        {
            {
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

        // converter combinations
        routeBuilder.converter("Converter", converter);
        routeBuilder.converter("Converter", converterBuilder);
        routeBuilder.converter("Converter", converter, Configuration.converterInvoker());
        routeBuilder.converter("Converter", converterBuilder, Configuration.converterInvoker());
        routeBuilder.converter("Converter", converter, Configuration.converterInvoker().build());
        routeBuilder.converter("Converter", converterBuilder, Configuration.converterInvoker().build());

        // translator combinations
        routeBuilder.translator("Translator", translator);
        routeBuilder.translator("Translator", translatorBuilder);
        routeBuilder.translator("Translator", translator, Configuration.translatorInvoker());
        routeBuilder.translator("Translator", translatorBuilder, Configuration.translatorInvoker());
        routeBuilder.translator("Translator", translator, Configuration.translatorInvoker().build());
        routeBuilder.translator("Translator", translatorBuilder, Configuration.translatorInvoker().build());

        // producer combinations
        routeBuilder.producer("Producer", producer);
        routeBuilder.producer("Producer", producerBuilder);
        routeBuilder.producer("Producer", producer, Configuration.producerInvoker());
        routeBuilder.producer("Producer", producerBuilder, Configuration.producerInvoker());
        routeBuilder.producer("Producer", producer, Configuration.producerInvoker().build());
        routeBuilder.producer("Producer", producerBuilder, Configuration.producerInvoker().build());

        // broker combinations
        routeBuilder.broker("Broker", broker);
        routeBuilder.broker("Broker", brokerBuilder);
        routeBuilder.broker("Broker", broker, Configuration.brokerInvoker());
        routeBuilder.broker("Broker", brokerBuilder, Configuration.brokerInvoker());
        routeBuilder.broker("Broker", broker, Configuration.brokerInvoker().build());
        routeBuilder.broker("Broker", brokerBuilder, Configuration.brokerInvoker().build());

        // sequencer combinations
        routeBuilder.sequencer("Sequencer", sequencer);
        routeBuilder.sequencer("Sequencer", sequencerBuilder);
        routeBuilder.sequencer("Sequencer", sequencer, Configuration.sequencerInvoker());
        routeBuilder.sequencer("Sequencer", sequencerBuilder, Configuration.sequencerInvoker());
        routeBuilder.sequencer("Sequencer", sequencer, Configuration.sequencerInvoker().build());
        routeBuilder.sequencer("Sequencer", sequencerBuilder, Configuration.sequencerInvoker().build());

        // singleRecipientRouter combinations
        routeBuilder.singleRecipientRouter("SingleRecipientRouter", singleRecipientRouter);
        routeBuilder.singleRecipientRouter("SingleRecipientRouter", singleRecipientRouterBuilder);
        routeBuilder.singleRecipientRouter("SingleRecipientRouter", singleRecipientRouter, Configuration.singleRecipientRouterInvoker());
        routeBuilder.singleRecipientRouter("SingleRecipientRouter", singleRecipientRouterBuilder, Configuration.singleRecipientRouterInvoker());
        routeBuilder.singleRecipientRouter("SingleRecipientRouter", singleRecipientRouter, Configuration.singleRecipientRouterInvoker().build());
        routeBuilder.singleRecipientRouter("SingleRecipientRouter", singleRecipientRouterBuilder, Configuration.singleRecipientRouterInvoker().build());

        // multiRecipientRouter combinations
        routeBuilder.multiRecipientRouter("MultiRecipientRouter", multiRecipientRouter);
        routeBuilder.multiRecipientRouter("MultiRecipientRouter", multiRecipientRouterBuilder);
        routeBuilder.multiRecipientRouter("MultiRecipientRouter", multiRecipientRouter, Configuration.multiRecipientRouterInvoker());
        routeBuilder.multiRecipientRouter("MultiRecipientRouter", multiRecipientRouterBuilder, Configuration.multiRecipientRouterInvoker());
        routeBuilder.multiRecipientRouter("MultiRecipientRouter", multiRecipientRouter, Configuration.multiRecipientRouterInvoker().build());
        routeBuilder.multiRecipientRouter("MultiRecipientRouter", multiRecipientRouterBuilder, Configuration.multiRecipientRouterInvoker().build());

        // filter combinations
        routeBuilder.filter("Filter", filter);
        routeBuilder.filter("Filter", filterBuilder);
        routeBuilder.filter("Filter", filter, Configuration.filterInvoker());
        routeBuilder.filter("Filter", filterBuilder, Configuration.filterInvoker());
        routeBuilder.filter("Filter", filter, Configuration.filterInvoker().build());
        routeBuilder.filter("Filter", filterBuilder, Configuration.filterInvoker().build());

        // splitter combinations
        routeBuilder.splitter("Splitter", splitter);
        routeBuilder.splitter("Splitter", splitterBuilder);
        routeBuilder.splitter("Splitter", splitter, Configuration.splitterInvoker());
        routeBuilder.splitter("Splitter", splitterBuilder, Configuration.splitterInvoker());
        routeBuilder.splitter("Splitter", splitter, Configuration.splitterInvoker().build());
        routeBuilder.splitter("Splitter", splitterBuilder, Configuration.splitterInvoker().build());

        // concurrent splitter combinations
        routeBuilder.concurrentSplitter("Splitter", splitter);
        routeBuilder.concurrentSplitter("Splitter", concurrentSplitterBuilder);
        routeBuilder.concurrentSplitter("Splitter", splitter, Configuration.concurrentSplitterInvoker());
        routeBuilder.concurrentSplitter("Splitter", concurrentSplitterBuilder, Configuration.concurrentSplitterInvoker());
        routeBuilder.concurrentSplitter("Splitter", splitter, Configuration.concurrentSplitterInvoker().build());
        routeBuilder.concurrentSplitter("Splitter", concurrentSplitterBuilder, Configuration.concurrentSplitterInvoker().build());
    }

}
