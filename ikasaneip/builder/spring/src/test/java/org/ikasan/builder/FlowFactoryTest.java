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
import org.ikasan.spec.component.routing.SingleRecipientRouter;
import org.ikasan.spec.component.sequencing.Sequencer;
import org.ikasan.spec.component.transformation.Converter;
import org.ikasan.spec.component.transformation.Translator;
import org.ikasan.spec.flow.Flow;
import org.ikasan.spec.flow.FlowElement;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.List;

import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.AFTER_TEST_METHOD;

/**
 * This test class supports the <code>FlowFactory</code> class.
 *
 * @author Ikasan Development Team
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = {MyApplication.class},
                webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql(scripts = {"/cleanDbTables.sql"}, executionPhase = AFTER_TEST_METHOD)
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
        Assert.assertTrue("flow element invoker capture metrics should be true", ((InvokerConfiguration)fe.getFlowElementInvoker().getConfiguration()).getCaptureMetrics());
        Assert.assertFalse("flow element invoker snap event should be false", ((InvokerConfiguration)fe.getFlowElementInvoker().getConfiguration()).getSnapEvent());
        Assert.assertFalse("flow element invoker dynamic configuration should be false", ((InvokerConfiguration)fe.getFlowElementInvoker().getConfiguration()).isDynamicConfiguration());

        Assert.assertTrue("flow element transition should be to coverter", fe.getTransitions().size() == 1);

        fe = flowElements.get(1);
        Assert.assertTrue("flow element name should be 'converter'", "converter".equals(fe.getComponentName()));
        Assert.assertTrue("flow element component should be an instance of Converter", fe.getFlowComponent() instanceof Converter);
        Assert.assertTrue("flow element invoker should be an instance of ConverterFlowElementInvoker", fe.getFlowElementInvoker() instanceof ConverterFlowElementInvoker);
        Assert.assertFalse("flow element invoker capture metrics should be false", ((InvokerConfiguration)fe.getFlowElementInvoker().getConfiguration()).getCaptureMetrics());
        Assert.assertFalse("flow element invoker snap event should be false", ((InvokerConfiguration)fe.getFlowElementInvoker().getConfiguration()).getSnapEvent());
        Assert.assertFalse("flow element invoker dynamic configuration should be false", ((InvokerConfiguration)fe.getFlowElementInvoker().getConfiguration()).isDynamicConfiguration());
        Assert.assertTrue("flow element transition should be to translator", fe.getTransitions().size() == 1);

        fe = flowElements.get(2);
        Assert.assertTrue("flow element name should be 'translator'", "translator".equals(fe.getComponentName()));
        Assert.assertTrue("flow element component should be an instance of Translator", fe.getFlowComponent() instanceof Translator);
        Assert.assertTrue("flow element invoker should be an instance of TranslatorFlowElementInvoker", fe.getFlowElementInvoker() instanceof TranslatorFlowElementInvoker);
        Assert.assertFalse("flow element invoker capture metrics should be false", ((InvokerConfiguration)fe.getFlowElementInvoker().getConfiguration()).getCaptureMetrics());
        Assert.assertFalse("flow element invoker snap event should be false", ((InvokerConfiguration)fe.getFlowElementInvoker().getConfiguration()).getSnapEvent());
        Assert.assertFalse("flow element invoker dynamic configuration should be false", ((InvokerConfiguration)fe.getFlowElementInvoker().getConfiguration()).isDynamicConfiguration());
        Assert.assertTrue("flow element transition should be to broker", fe.getTransitions().size() == 1);

        fe = flowElements.get(3);
        Assert.assertTrue("flow element name should be 'broker'", "broker".equals(fe.getComponentName()));
        Assert.assertTrue("flow element component should be an instance of Broker", fe.getFlowComponent() instanceof Broker);
        Assert.assertTrue("flow element invoker should be an instance of BrokerFlowElementInvoker", fe.getFlowElementInvoker() instanceof BrokerFlowElementInvoker);
        Assert.assertFalse("flow element invoker capture metrics should be false", ((InvokerConfiguration)fe.getFlowElementInvoker().getConfiguration()).getCaptureMetrics());
        Assert.assertFalse("flow element invoker snap event should be false", ((InvokerConfiguration)fe.getFlowElementInvoker().getConfiguration()).getSnapEvent());
        Assert.assertFalse("flow element invoker dynamic configuration should be false", ((InvokerConfiguration)fe.getFlowElementInvoker().getConfiguration()).isDynamicConfiguration());
        Assert.assertTrue("flow element transition should be to producer", fe.getTransitions().size() == 1);

        fe = flowElements.get(4);
        Assert.assertTrue("flow element name should be 'mrRouter'", "mrRouter".equals(fe.getComponentName()));
        Assert.assertTrue("flow element component should be an instance of MultiRecipientRouter", fe.getFlowComponent() instanceof MultiRecipientRouter);
        Assert.assertTrue("flow element invoker should be an instance of MultiRecipientRouterFlowElementInvoker", fe.getFlowElementInvoker() instanceof MultiRecipientRouterFlowElementInvoker);
        Assert.assertFalse("flow element invoker capture metrics should be false", ((InvokerConfiguration)fe.getFlowElementInvoker().getConfiguration()).getCaptureMetrics());
        Assert.assertFalse("flow element invoker snap event should be false", ((InvokerConfiguration)fe.getFlowElementInvoker().getConfiguration()).getSnapEvent());
        Assert.assertFalse("flow element invoker dynamic configuration should be false", ((InvokerConfiguration)fe.getFlowElementInvoker().getConfiguration()).isDynamicConfiguration());
        Assert.assertTrue("flow element should have 3 routable transitions", fe.getTransitions().size() == 3);

        fe = flowElements.get(5);
        Assert.assertTrue("flow element name should be 'sequencerA'", "sequencerA".equals(fe.getComponentName()));
        Assert.assertTrue("flow element component should be an instance of Sequencer", fe.getFlowComponent() instanceof Sequencer);
        Assert.assertTrue("flow element invoker should be an instance of SequencerFlowElementInvoker", fe.getFlowElementInvoker() instanceof SequencerFlowElementInvoker);
        Assert.assertFalse("flow element invoker capture metrics should be false", ((InvokerConfiguration)fe.getFlowElementInvoker().getConfiguration()).getCaptureMetrics());
        Assert.assertFalse("flow element invoker snap event should be false", ((InvokerConfiguration)fe.getFlowElementInvoker().getConfiguration()).getSnapEvent());
        Assert.assertFalse("flow element invoker dynamic configuration should be false", ((InvokerConfiguration)fe.getFlowElementInvoker().getConfiguration()).isDynamicConfiguration());
        Assert.assertTrue("flow element transition should be to producer", fe.getTransitions().size() == 1);

        fe = flowElements.get(6);
        Assert.assertTrue("flow element name should be 'sequencerB'", "sequencerB".equals(fe.getComponentName()));
        Assert.assertTrue("flow element component should be an instance of Sequencer", fe.getFlowComponent() instanceof Sequencer);
        Assert.assertTrue("flow element invoker should be an instance of SequencerFlowElementInvoker", fe.getFlowElementInvoker() instanceof SequencerFlowElementInvoker);
        Assert.assertFalse("flow element invoker capture metrics should be false", ((InvokerConfiguration)fe.getFlowElementInvoker().getConfiguration()).getCaptureMetrics());
        Assert.assertFalse("flow element invoker snap event should be false", ((InvokerConfiguration)fe.getFlowElementInvoker().getConfiguration()).getSnapEvent());
        Assert.assertFalse("flow element invoker dynamic configuration should be false", ((InvokerConfiguration)fe.getFlowElementInvoker().getConfiguration()).isDynamicConfiguration());
        Assert.assertTrue("flow element should have 2 sequenced transitions", fe.getTransitions().size() == 1);


        fe = flowElements.get(7);
        Assert.assertTrue("flow element name should be 'srRouter'", "srRouter".equals(fe.getComponentName()));
        Assert.assertTrue("flow element component should be an instance of SingleRecipientRouter", fe.getFlowComponent() instanceof SingleRecipientRouter);
        Assert.assertTrue("flow element invoker should be an instance of SingleRecipientRouterFlowElementInvoker", fe.getFlowElementInvoker() instanceof SingleRecipientRouterFlowElementInvoker);
        Assert.assertFalse("flow element invoker capture metrics should be false", ((InvokerConfiguration)fe.getFlowElementInvoker().getConfiguration()).getCaptureMetrics());
        Assert.assertFalse("flow element invoker snap event should be false", ((InvokerConfiguration)fe.getFlowElementInvoker().getConfiguration()).getSnapEvent());
        Assert.assertFalse("flow element invoker dynamic configuration should be false", ((InvokerConfiguration)fe.getFlowElementInvoker().getConfiguration()).isDynamicConfiguration());
        Assert.assertTrue("flow element transition should be to '1", fe.getTransitions().size() == 1);


        fe = flowElements.get(8);
        Assert.assertTrue("flow element name should be 'producerA'", "producerA".equals(fe.getComponentName()));
        Assert.assertTrue("flow element component should be an instance of Producer", fe.getFlowComponent() instanceof Producer);
        Assert.assertTrue("flow element invoker should be an instance of ProducerFlowElementInvoker", fe.getFlowElementInvoker() instanceof ProducerFlowElementInvoker);
        Assert.assertFalse("flow element invoker capture metrics should be false", ((InvokerConfiguration)fe.getFlowElementInvoker().getConfiguration()).getCaptureMetrics());
        Assert.assertFalse("flow element invoker snap event should be false", ((InvokerConfiguration)fe.getFlowElementInvoker().getConfiguration()).getSnapEvent());
        Assert.assertTrue("flow element invoker dynamic configuration should be true", ((InvokerConfiguration)fe.getFlowElementInvoker().getConfiguration()).isDynamicConfiguration());
        Assert.assertTrue("flow element transition should be to 'null", fe.getTransitions().size() == 0);

        fe = flowElements.get(9);
        Assert.assertTrue("flow element name should be 'producerB'", "producerB".equals(fe.getComponentName()));
        Assert.assertTrue("flow element component should be an instance of Producer", fe.getFlowComponent() instanceof Producer);
        Assert.assertTrue("flow element invoker should be an instance of ProducerFlowElementInvoker", fe.getFlowElementInvoker() instanceof ProducerFlowElementInvoker);
        Assert.assertFalse("flow element invoker capture metrics should be false", ((InvokerConfiguration)fe.getFlowElementInvoker().getConfiguration()).getCaptureMetrics());
        Assert.assertFalse("flow element invoker snap event should be false", ((InvokerConfiguration)fe.getFlowElementInvoker().getConfiguration()).getSnapEvent());
        Assert.assertFalse("flow element invoker dynamic configuration should be false", ((InvokerConfiguration)fe.getFlowElementInvoker().getConfiguration()).isDynamicConfiguration());
        Assert.assertTrue("flow element transition should be to 'null", fe.getTransitions().size() == 0);

        fe = flowElements.get(10);
        Assert.assertTrue("flow element name should be 'sequencerA'", "sequencerA".equals(fe.getComponentName()));
        Assert.assertTrue("flow element component should be an instance of Sequencer", fe.getFlowComponent() instanceof Sequencer);
        Assert.assertTrue("flow element invoker should be an instance of SequencerFlowElementInvoker", fe.getFlowElementInvoker() instanceof SequencerFlowElementInvoker);
        Assert.assertFalse("flow element invoker capture metrics should be false", ((InvokerConfiguration)fe.getFlowElementInvoker().getConfiguration()).getCaptureMetrics());
        Assert.assertFalse("flow element invoker snap event should be false", ((InvokerConfiguration)fe.getFlowElementInvoker().getConfiguration()).getSnapEvent());
        Assert.assertFalse("flow element invoker dynamic configuration should be false", ((InvokerConfiguration)fe.getFlowElementInvoker().getConfiguration()).isDynamicConfiguration());
        Assert.assertTrue("flow element transition should be to producer", fe.getTransitions().size() == 1);
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
