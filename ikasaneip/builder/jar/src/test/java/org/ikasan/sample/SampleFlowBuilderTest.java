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
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.*;
import org.springframework.test.context.ContextConfiguration;

import java.util.List;

/**
 * This test class supports the <code>FlowBuilder</code> class.
 * 
 * @author Ikasan Development Team
 */
@SuppressWarnings("unchecked")
//specifies the Spring configuration to load for this test fixture
@ContextConfiguration(locations={
		"/exclusion-service-conf.xml",
		"/datasource-conf.xml"
})
public class SampleFlowBuilderTest
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

	@Before
    public void setup()
    {
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

        String[] args = { "--server.port=" + SocketUtils.findAvailableTcpPort(8000, 9000) };
		ikasanApplication = IkasanApplicationFactory.getIkasanApplication(args);
	}

	@After
	public void teardown()
	{
		ikasanApplication.close();
	}

	/**
	 * Test successful flow creation.
	 */
	@Test
	public void test_successful_simple_transitions()
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

		Assert.assertTrue("flow name is incorrect", "flowName".equals(flow.getName()));
		Assert.assertTrue("module name is incorrect", "moduleName".equals(flow.getModuleName()));
		List<FlowElement<?>> flowElements = flow.getFlowElements();
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
		Assert.assertTrue("flow element transition should be to broker", fe.getTransitions().size() == 1);

		fe = flowElements.get(3);
		Assert.assertTrue("flow element name should be 'broker'", "broker".equals(fe.getComponentName()));
		Assert.assertTrue("flow element component should be an instance of Broker", fe.getFlowComponent() instanceof Broker);
		Assert.assertTrue("flow element invoker should be an instance of BrokerFlowElementInvoker", fe.getFlowElementInvoker() instanceof BrokerFlowElementInvoker);
		Assert.assertTrue("flow element transition should be to producer", fe.getTransitions().size() == 1);

		fe = flowElements.get(4);
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
    public void test_successful_router_transitions()
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
					.otherwise(builderFactory.getRouteBuilder().producer("end3",producer) )
    	    	.build();

    	Assert.assertTrue("flow name is incorrect", "flowName".equals(flow.getName()));
    	Assert.assertTrue("module name is incorrect", "moduleName".equals(flow.getModuleName()));
       	List<FlowElement<?>> flowElements = flow.getFlowElements();
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
    	Assert.assertTrue("flow element transition should be to broker", fe.getTransitions().size() == 1);
       	
       	fe = flowElements.get(3);
       	Assert.assertTrue("flow element name should be 'routerName'", "routerName".equals(fe.getComponentName()));
    	Assert.assertTrue("flow element component should be an instance of SRR", fe.getFlowComponent() instanceof SingleRecipientRouter);
        Assert.assertTrue("flow element invoker should be an instance of SingleRecipientRouterFlowElementInvoker", fe.getFlowElementInvoker() instanceof SingleRecipientRouterFlowElementInvoker);
    	Assert.assertTrue("flow element transitions should be to 3 routes", fe.getTransitions().size() == 3);
       	
       	fe = flowElements.get(4);
       	Assert.assertTrue("flow element name should be 'end1'", "end1".equals(fe.getComponentName()));
    	Assert.assertTrue("flow element component should be an instance of Producer", fe.getFlowComponent() instanceof Producer);
        Assert.assertTrue("flow element invoker should be an instance of ProducerFlowElementInvoker", fe.getFlowElementInvoker() instanceof ProducerFlowElementInvoker);
    	Assert.assertTrue("flow element transition should be to 'null", fe.getTransitions().size() == 0);

        mockery.assertIsSatisfied();
    }

}
