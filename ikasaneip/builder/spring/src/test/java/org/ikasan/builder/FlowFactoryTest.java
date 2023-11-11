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

import jakarta.annotation.Resource;
import org.ikasan.builder.sample.SampleExclusionServiceAwareConverter;
import org.ikasan.flow.visitorPattern.invoker.*;
import org.ikasan.spec.component.endpoint.Broker;
import org.ikasan.spec.component.endpoint.Consumer;
import org.ikasan.spec.component.endpoint.Producer;
import org.ikasan.spec.component.routing.MultiRecipientRouter;
import org.ikasan.spec.component.routing.SingleRecipientRouter;
import org.ikasan.spec.component.sequencing.Sequencer;
import org.ikasan.spec.component.transformation.Converter;
import org.ikasan.spec.component.transformation.Translator;
import org.ikasan.spec.flow.Flow;
import org.ikasan.spec.flow.FlowElement;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * This test class supports the <code>FlowFactory</code> class.
 *
 * @author Ikasan Development Team
 */
@SpringBootTest(classes = {MyApplication.class},
                webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class FlowFactoryTest
{
    @Resource
    Flow flow;

    @Resource
    Flow flowWithExclusionFlow;

    @Resource
    SampleExclusionServiceAwareConverter exclusionServiceAwareConverter;

    /**
     * Test successful flow creation.
     */
    @Test
    @DirtiesContext
    void test_successful_flowCreation()
    {
        assertEquals("flowName", flow.getName(), "flow name is incorrect");
        assertEquals("moduleName", flow.getModuleName(), "module name is incorrect");
        List<FlowElement<?>> flowElements = flow.getFlowElements();
        assertEquals(11, flowElements.size(), "Flow elements should total 11");

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
        assertEquals("mrRouter", fe.getComponentName(), "flow element name should be 'mrRouter'");
        assertTrue(fe.getFlowComponent() instanceof MultiRecipientRouter, "flow element component should be an instance of MultiRecipientRouter");
        assertTrue(fe.getFlowElementInvoker() instanceof MultiRecipientRouterFlowElementInvoker, "flow element invoker should be an instance of MultiRecipientRouterFlowElementInvoker");
        assertEquals(3, fe.getTransitions().size(), "flow element should have 3 routable transitions");

        fe = flowElements.get(5);
        assertEquals("sequencerA", fe.getComponentName(), "flow element name should be 'sequencerA'");
        assertTrue(fe.getFlowComponent() instanceof Sequencer, "flow element component should be an instance of Sequencer");
        assertTrue(fe.getFlowElementInvoker() instanceof SequencerFlowElementInvoker, "flow element invoker should be an instance of SequencerFlowElementInvoker");
        assertEquals(1, fe.getTransitions().size(), "flow element transition should be to producer");

        fe = flowElements.get(6);
        assertEquals("sequencerB", fe.getComponentName(), "flow element name should be 'sequencerB'");
        assertTrue(fe.getFlowComponent() instanceof Sequencer, "flow element component should be an instance of Sequencer");
        assertTrue(fe.getFlowElementInvoker() instanceof SequencerFlowElementInvoker, "flow element invoker should be an instance of SequencerFlowElementInvoker");
        assertEquals(1, fe.getTransitions().size(), "flow element should have 2 sequenced transitions");


        fe = flowElements.get(7);
        assertEquals("srRouter", fe.getComponentName(), "flow element name should be 'srRouter'");
        assertTrue(fe.getFlowComponent() instanceof SingleRecipientRouter, "flow element component should be an instance of SingleRecipientRouter");
        assertTrue(fe.getFlowElementInvoker() instanceof SingleRecipientRouterFlowElementInvoker, "flow element invoker should be an instance of SingleRecipientRouterFlowElementInvoker");
        assertEquals(1, fe.getTransitions().size(), "flow element transition should be to '1");


        fe = flowElements.get(8);
        assertEquals("producerA", fe.getComponentName(), "flow element name should be 'producerA'");
        assertTrue(fe.getFlowComponent() instanceof Producer, "flow element component should be an instance of Producer");
        assertTrue(fe.getFlowElementInvoker() instanceof ProducerFlowElementInvoker, "flow element invoker should be an instance of ProducerFlowElementInvoker");
        assertEquals(0, fe.getTransitions().size(), "flow element transition should be to 'null");

        fe = flowElements.get(9);
        assertEquals("producerB", fe.getComponentName(), "flow element name should be 'producerB'");
        assertTrue(fe.getFlowComponent() instanceof Producer, "flow element component should be an instance of Producer");
        assertTrue(fe.getFlowElementInvoker() instanceof ProducerFlowElementInvoker, "flow element invoker should be an instance of ProducerFlowElementInvoker");
        assertEquals(0, fe.getTransitions().size(), "flow element transition should be to 'null");

        fe = flowElements.get(10);
        assertEquals("sequencerA", fe.getComponentName(), "flow element name should be 'sequencerA'");
        assertTrue(fe.getFlowComponent() instanceof Sequencer, "flow element component should be an instance of Sequencer");
        assertTrue(fe.getFlowElementInvoker() instanceof SequencerFlowElementInvoker, "flow element invoker should be an instance of SequencerFlowElementInvoker");
        assertEquals(1, fe.getTransitions().size(), "flow element transition should be to producer");
    }

    /**
     * Exclusion service should be injected into isExclusionServiceAware exclusion flow elements
     * that exist in the exclusion flow.
     */
    @DirtiesContext
    public void test_injectExclusionService_exclusionFlowElement() {
        assertNotNull(flowWithExclusionFlow);
        assertNotNull(exclusionServiceAwareConverter.getExclusionService());
    }

}
