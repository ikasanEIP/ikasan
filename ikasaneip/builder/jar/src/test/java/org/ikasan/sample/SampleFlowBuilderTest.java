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
package org.ikasan.sample;

import org.ikasan.builder.BuilderFactory;
import org.ikasan.builder.IkasanApplication;
import org.ikasan.builder.IkasanApplicationFactory;
import org.ikasan.exclusion.service.ExclusionServiceFactory;
import org.ikasan.flow.visitorPattern.invoker.*;
import org.ikasan.spec.component.endpoint.Broker;
import org.ikasan.spec.component.endpoint.Consumer;
import org.ikasan.spec.component.endpoint.Producer;
import org.ikasan.spec.component.routing.SingleRecipientRouter;
import org.ikasan.spec.component.sequencing.Sequencer;
import org.ikasan.spec.component.transformation.Converter;
import org.ikasan.spec.component.transformation.Translator;
import org.ikasan.spec.event.EventFactory;
import org.ikasan.spec.exclusion.ExclusionService;
import org.ikasan.spec.flow.Flow;
import org.ikasan.spec.flow.FlowElement;
import org.ikasan.spec.flow.FlowInvocationContextListener;
import org.ikasan.spec.serialiser.SerialiserFactory;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.imposters.ByteBuddyClassImposteriser;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.util.SocketUtils;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * This test class supports the <code>FlowBuilder</code> class.
 * 
 * @author Ikasan Development Team
 */
@SuppressWarnings("unchecked")
//specifies the Spring configuration to load for this test fixture
@ContextConfiguration(locations = {
        "/exclusion-service-conf.xml",
        "/datasource-conf.xml"
})
class SampleFlowBuilderTest
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
    /** Mock Producer */
    final Producer producer = mockery.mock(Producer.class, "mockProducer");
	/** Mock Broker */
	final Broker broker = mockery.mock(Broker.class, "mockBroker");
	/** Mock SRR */
	final SingleRecipientRouter singleRecipientRouter = mockery.mock(SingleRecipientRouter.class, "mockSingleRecipientRouter");

    // Transformers
    /** Mock Translator */
    final Translator translator = mockery.mock(Translator.class, "mockTranslator");
    /** Mock Converter */
    final Converter converter = mockery.mock(Converter.class, "mockConverter");

    // Sequencers
    /** Mock Sequencer */
    final Sequencer sequencer = mockery.mock(Sequencer.class, "mockSequencingRouter");

    /** Context Listener */
    final FlowInvocationContextListener flowInvocationContextListener = mockery.mock(FlowInvocationContextListener.class, "flowInvocationContextListener");

    final ExclusionServiceFactory exclusionServiceFactory = mockery.mock(ExclusionServiceFactory.class, "exclusionServiceFactory");

    final ExclusionService exclusionService = mockery.mock(ExclusionService.class, "exclusionService");

    final SerialiserFactory serialiserFactory = mockery.mock(SerialiserFactory.class, "serialiserFactory");

	IkasanApplication ikasanApplication;

    @BeforeEach
    void setup()
    {
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

        String[] args = { "--server.port=" + SocketUtils.findAvailableTcpPort(8000, 9000),
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

    @AfterEach
    void teardown()
	{
		ikasanApplication.close();
	}

    /**
     * Test successful flow creation.
     */
    @Test
    void test_successful_simple_transitions()
	{
		BuilderFactory builderFactory = ikasanApplication.getBuilderFactory();

		Flow flow = builderFactory.getFlowBuilder("moduleName", "flowName")
				.withDescription("flowDescription")
				.withFlowInvocationContextListener(flowInvocationContextListener)
				.withFlowInvocationContextListener(flowInvocationContextListener)
				.withExclusionServiceFactory(exclusionServiceFactory)
				.withSerialiserFactory(serialiserFactory)
				.consumer("consumer", consumer)
				.converter("converter", converter)
				.translator("translator", translator)
				.broker("broker", broker)
				.producer("producer", producer).build();

        assertEquals("flowName", flow.getName(), "flow name is incorrect");
        assertEquals("moduleName", flow.getModuleName(), "module name is incorrect");
		List<FlowElement<?>> flowElements = flow.getFlowElements();
		assertNotNull(flowElements, "Flow elements cannot be 'null'");

        assertEquals(2, flow.getFlowInvocationContextListeners().size(), "Should have two FlowInvocationContextListener");

		FlowElement fe = flowElements.get(0);
        assertEquals("consumer", fe.getComponentName(), "flow element name should be 'consumer'");
		assertTrue(fe.getFlowComponent() instanceof Consumer, "flow element component should be an instance of Consumer");
		assertTrue(fe.getFlowElementInvoker() instanceof ConsumerFlowElementInvoker, "flow element invoker should be an instance of ConsumerFlowElementInvoker");
        assertEquals(1, fe.getTransitions().size(), "flow element transition should be to coverter");

		fe = flowElements.get(1);
        assertEquals("converter", fe.getComponentName(), "flow element name should be 'converter'");
		assertTrue(fe.getFlowComponent() instanceof Converter, "flow element component should be an instance of Converter");
		assertTrue(fe.getFlowElementInvoker() instanceof ConverterFlowElementInvoker, "flow element invoker should be an instance of ConverterFlowElementInvoker");
        assertEquals(1, fe.getTransitions().size(), "flow element transition should be to translator");

		fe = flowElements.get(2);
        assertEquals("translator", fe.getComponentName(), "flow element name should be 'translator'");
		assertTrue(fe.getFlowComponent() instanceof Translator, "flow element component should be an instance of Translator");
		assertTrue(fe.getFlowElementInvoker() instanceof TranslatorFlowElementInvoker, "flow element invoker should be an instance of TranslatorFlowElementInvoker");
        assertEquals(1, fe.getTransitions().size(), "flow element transition should be to broker");

		fe = flowElements.get(3);
        assertEquals("broker", fe.getComponentName(), "flow element name should be 'broker'");
		assertTrue(fe.getFlowComponent() instanceof Broker, "flow element component should be an instance of Broker");
		assertTrue(fe.getFlowElementInvoker() instanceof BrokerFlowElementInvoker, "flow element invoker should be an instance of BrokerFlowElementInvoker");
        assertEquals(1, fe.getTransitions().size(), "flow element transition should be to producer");

		fe = flowElements.get(4);
        assertEquals("producer", fe.getComponentName(), "flow element name should be 'producer'");
		assertTrue(fe.getFlowComponent() instanceof Producer, "flow element component should be an instance of Producer");
		assertTrue(fe.getFlowElementInvoker() instanceof ProducerFlowElementInvoker, "flow element invoker should be an instance of ProducerFlowElementInvoker");
        assertEquals(0, fe.getTransitions().size(), "flow element transition should be to 'null");

		mockery.assertIsSatisfied();
	}

    /**
     * Test successful flow creation.
     */
    @Test
    void test_successful_router_transitions()
    {
		BuilderFactory builderFactory = ikasanApplication.getBuilderFactory();

		Flow flow = builderFactory.getFlowBuilder("moduleName", "flowName")
				.withFlowInvocationContextListener(flowInvocationContextListener)
				.withFlowInvocationContextListener(flowInvocationContextListener)
				.withExclusionServiceFactory(exclusionServiceFactory)
				.consumer("consumer", consumer)
    	    	.converter("converter", converter)
    	    	.translator("translator", translator)
				.singleRecipientRouter("routerName", singleRecipientRouter)
					.when("a", builderFactory.getRouteBuilder().producer("end1",producer) )
					.when("b", builderFactory.getRouteBuilder().producer("end2",producer) )
					.otherwise(builderFactory.getRouteBuilder().producer("end3",producer) );

        assertEquals("flowName", flow.getName(), "flow name is incorrect");
        assertEquals("moduleName", flow.getModuleName(), "module name is incorrect");
       	List<FlowElement<?>> flowElements = flow.getFlowElements();
       	assertNotNull(flowElements, "Flow elements cannot be 'null'");

        assertEquals(2, flow.getFlowInvocationContextListeners().size(), "Should have two FlowInvocationContextListener");

       	FlowElement fe = flowElements.get(0);
        assertEquals("consumer", fe.getComponentName(), "flow element name should be 'consumer'");       		
    	assertTrue(fe.getFlowComponent() instanceof Consumer, "flow element component should be an instance of Consumer");
        assertTrue(fe.getFlowElementInvoker() instanceof ConsumerFlowElementInvoker, "flow element invoker should be an instance of ConsumerFlowElementInvoker");
        assertEquals(1, fe.getTransitions().size(), "flow element transition should be to coverter");
       	
       	fe = flowElements.get(1);
        assertEquals("converter", fe.getComponentName(), "flow element name should be 'converter'");       		
    	assertTrue(fe.getFlowComponent() instanceof Converter, "flow element component should be an instance of Converter");
        assertTrue(fe.getFlowElementInvoker() instanceof ConverterFlowElementInvoker, "flow element invoker should be an instance of ConverterFlowElementInvoker");
        assertEquals(1, fe.getTransitions().size(), "flow element transition should be to translator");
       	
       	fe = flowElements.get(2);
        assertEquals("translator", fe.getComponentName(), "flow element name should be 'translator'");       		
    	assertTrue(fe.getFlowComponent() instanceof Translator, "flow element component should be an instance of Translator");
        assertTrue(fe.getFlowElementInvoker() instanceof TranslatorFlowElementInvoker, "flow element invoker should be an instance of TranslatorFlowElementInvoker");
        assertEquals(1, fe.getTransitions().size(), "flow element transition should be to broker");
       	
       	fe = flowElements.get(3);
        assertEquals("routerName", fe.getComponentName(), "flow element name should be 'routerName'");
    	assertTrue(fe.getFlowComponent() instanceof SingleRecipientRouter, "flow element component should be an instance of SRR");
        assertTrue(fe.getFlowElementInvoker() instanceof SingleRecipientRouterFlowElementInvoker, "flow element invoker should be an instance of SingleRecipientRouterFlowElementInvoker");
        assertEquals(3, fe.getTransitions().size(), "flow element transitions should be to 3 routes");
       	
       	fe = flowElements.get(4);
        assertEquals("end1", fe.getComponentName(), "flow element name should be 'end1'");
    	assertTrue(fe.getFlowComponent() instanceof Producer, "flow element component should be an instance of Producer");
        assertTrue(fe.getFlowElementInvoker() instanceof ProducerFlowElementInvoker, "flow element invoker should be an instance of ProducerFlowElementInvoker");
        assertEquals(0, fe.getTransitions().size(), "flow element transition should be to 'null");

        mockery.assertIsSatisfied();
    }

}
