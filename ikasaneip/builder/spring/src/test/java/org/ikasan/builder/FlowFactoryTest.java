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

import org.ikasan.builder.sample.SampleExclusionServiceAwareConverter;
import org.ikasan.flow.visitorPattern.invoker.*;
import org.ikasan.spec.component.endpoint.Broker;
import org.ikasan.spec.component.endpoint.Consumer;
import org.ikasan.spec.component.endpoint.Producer;
import org.ikasan.spec.component.routing.MultiRecipientRouter;
import org.ikasan.spec.component.sequencing.Sequencer;
import org.ikasan.spec.component.transformation.Converter;
import org.ikasan.spec.component.transformation.Translator;
import org.ikasan.spec.flow.Flow;
import org.ikasan.spec.flow.FlowElement;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;
import java.util.List;

/**
 * This test class supports the <code>FlowFactory</code> class.
 *
 * @author Ikasan Development Team
 */
@RunWith(SpringJUnit4ClassRunner.class)
//specifies the Spring configuration to load for this test fixture
@ContextConfiguration(locations = {
        "/flow-conf.xml",
        "/sample-component-conf.xml",
        "/recoveryManager-service-conf.xml",
        "/substitute-components.xml",
        "/ikasan-transaction-conf.xml",
        "/error-reporting-service-conf.xml",
        "/exclusion-service-conf.xml",
        "/serialiser-service-conf.xml",
        "/error-reporting-service-conf.xml",
        "/scheduler-service-conf.xml",
        "/configuration-service-conf.xml",
        "/systemevent-service-conf.xml",
        "/module-service-conf.xml",
        "/wiretap-service-conf.xml",
        "/replay-service-conf.xml",
        "/exception-conf.xml",
        "/hsqldb-datasource-conf.xml"
})
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
    public void test_successful_flowCreation()
    {
        Assert.assertTrue("flow name is incorrect", "flowName".equals(flow.getName()));
        Assert.assertTrue("module name is incorrect", "moduleName".equals(flow.getModuleName()));
        List<FlowElement<?>> flowElements = flow.getFlowElements();
        Assert.assertTrue("Flow elements should total 11", flowElements.size() == 11);

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
        Assert.assertTrue("flow element name should be 'router'", "router".equals(fe.getComponentName()));
        Assert.assertTrue("flow element component should be an instance of MultiRecipientRouter", fe.getFlowComponent() instanceof MultiRecipientRouter);
        Assert.assertTrue("flow element invoker should be an instance of MultiRecipientRouterFlowElementInvoker", fe.getFlowElementInvoker() instanceof MultiRecipientRouterFlowElementInvoker);
        Assert.assertTrue("flow element should have 2 routable transitions", fe.getTransitions().size() == 2);

        fe = flowElements.get(5);
        Assert.assertTrue("flow element name should be 'sequencerA'", "sequencerA".equals(fe.getComponentName()));
        Assert.assertTrue("flow element component should be an instance of Sequencer", fe.getFlowComponent() instanceof Sequencer);
        Assert.assertTrue("flow element invoker should be an instance of SequencerFlowElementInvoker", fe.getFlowElementInvoker() instanceof SequencerFlowElementInvoker);
        Assert.assertTrue("flow element transition should be to producer", fe.getTransitions().size() == 2);

        fe = flowElements.get(6);
        Assert.assertTrue("flow element name should be 'sequencerB'", "sequencerB".equals(fe.getComponentName()));
        Assert.assertTrue("flow element component should be an instance of Sequencer", fe.getFlowComponent() instanceof Sequencer);
        Assert.assertTrue("flow element invoker should be an instance of SequencerFlowElementInvoker", fe.getFlowElementInvoker() instanceof SequencerFlowElementInvoker);
        Assert.assertTrue("flow element should have 2 sequenced transitions", fe.getTransitions().size() == 2);

        fe = flowElements.get(7);
        Assert.assertTrue("flow element name should be 'producerA'", "producerA".equals(fe.getComponentName()));
        Assert.assertTrue("flow element component should be an instance of Producer", fe.getFlowComponent() instanceof Producer);
        Assert.assertTrue("flow element invoker should be an instance of ProducerFlowElementInvoker", fe.getFlowElementInvoker() instanceof ProducerFlowElementInvoker);
        Assert.assertTrue("flow element transition should be to 'null", fe.getTransitions().size() == 0);

        fe = flowElements.get(8);
        Assert.assertTrue("flow element name should be 'producerB'", "producerB".equals(fe.getComponentName()));
        Assert.assertTrue("flow element component should be an instance of Producer", fe.getFlowComponent() instanceof Producer);
        Assert.assertTrue("flow element invoker should be an instance of ProducerFlowElementInvoker", fe.getFlowElementInvoker() instanceof ProducerFlowElementInvoker);
        Assert.assertTrue("flow element transition should be to 'null", fe.getTransitions().size() == 0);

        fe = flowElements.get(9);
        Assert.assertTrue("flow element name should be 'producerA'", "producerA".equals(fe.getComponentName()));
        Assert.assertTrue("flow element component should be an instance of Producer", fe.getFlowComponent() instanceof Producer);
        Assert.assertTrue("flow element invoker should be an instance of ProducerFlowElementInvoker", fe.getFlowElementInvoker() instanceof ProducerFlowElementInvoker);
        Assert.assertTrue("flow element transition should be to 'null", fe.getTransitions().size() == 0);

        fe = flowElements.get(10);
        Assert.assertTrue("flow element name should be 'producerB'", "producerB".equals(fe.getComponentName()));
        Assert.assertTrue("flow element component should be an instance of Producer", fe.getFlowComponent() instanceof Producer);
        Assert.assertTrue("flow element invoker should be an instance of ProducerFlowElementInvoker", fe.getFlowElementInvoker() instanceof ProducerFlowElementInvoker);
        Assert.assertTrue("flow element transition should be to 'null", fe.getTransitions().size() == 0);
    }

    /**
     * Exclusion service should be injected into isExclusionServiceAware exclusion flow elements
     * that exist in the exclusion flow.
     */
    @DirtiesContext
    public void test_injectExclusionService_exclusionFlowElement() {
        Assert.assertNotNull(flowWithExclusionFlow);
        Assert.assertNotNull(exclusionServiceAwareConverter.getExclusionService());
    }

}
