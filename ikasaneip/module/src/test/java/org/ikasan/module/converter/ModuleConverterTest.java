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
package org.ikasan.module.converter;

import org.ikasan.flow.visitorPattern.VisitingInvokerFlow;
import org.ikasan.module.SimpleModule;
import org.ikasan.module.service.ModuleInitialisationServiceImplTest;
import org.ikasan.spec.component.endpoint.Consumer;
import org.ikasan.spec.component.endpoint.Producer;
import org.ikasan.spec.exclusion.ExclusionService;
import org.ikasan.spec.flow.*;
import org.ikasan.spec.module.Module;
import org.ikasan.spec.recovery.RecoveryManager;
import org.ikasan.spec.serialiser.SerialiserFactory;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.lib.concurrent.Synchroniser;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

/**
 * Test cases for ModuleServiceImpl
 *
 * @author Ikasan Development Team
 */
public class ModuleConverterTest
{
    private Mockery mockery = new Mockery() {{
        setImposteriser(ClassImposteriser.INSTANCE);
        setThreadingPolicy(new Synchroniser());
    }};

    /**
     * mocked container, service and dao
     */
    FlowConfiguration flowConfiguration = mockery.mock(FlowConfiguration.class);
    RecoveryManager recoveryManager = mockery.mock(RecoveryManager.class);
    SerialiserFactory serialiserFactory = mockery.mock(SerialiserFactory.class);
    ExclusionService exclusionService = mockery.mock(ExclusionService.class);

    Module module = mockery.mock(Module.class);

    private static final String MODULE_NAME = "moduleName";
    private static final String FLOW_NAME = "flowName";
    private static final String ACTOR = "actor";

    /**
     * Class under test
     */
    ModuleConverter uut = new ModuleConverter();


    @Test
    public void convert() throws Exception {

        // Setup test data

        FlowElement consumerElement = mockery.mock(FlowElement.class,"mockConsumerElement");
        FlowElement producerElement = mockery.mock(FlowElement.class,"mockProducerElement");

        Consumer consumer = mockery.mock(Consumer.class,"mockConsumer");
        Producer producer = mockery.mock(Producer.class,"mockProducer");

        List<FlowElement<?>> flowElements = Arrays.asList(consumerElement,producerElement);

        VisitingInvokerFlow flow = new VisitingInvokerFlow("sampleFlow",MODULE_NAME,flowConfiguration,recoveryManager,exclusionService,serialiserFactory);
        Module<org.ikasan.spec.flow.Flow> module = new SimpleModule(MODULE_NAME,Arrays.asList(flow));


        mockery.checking(new Expectations() {{

            // discovery
            exactly(1).of(flowConfiguration).getFlowElements();
            will(returnValue(flowElements));

            exactly(1).of(consumerElement).getComponentName();
            will(returnValue("consumer"));

            exactly(2).of(consumerElement).getDescription();
            will(returnValue("consumer description"));

            exactly(1).of(consumerElement).getFlowComponent();
            will(returnValue(consumer));

            exactly(1).of(consumerElement).getFlowElementInvoker();
            will(returnValue(new TestInvoker()));

            exactly(1).of(producerElement).getComponentName();
            will(returnValue("producer"));

            exactly(2).of(producerElement).getDescription();
            will(returnValue("producer description"));

            exactly(1).of(producerElement).getFlowComponent();
            will(returnValue(producer));

            exactly(1).of(producerElement).getFlowElementInvoker();
            will(returnValue(new TestInvoker()));


        }});

        org.ikasan.topology.model.Module result = uut.convert(module);

        mockery.assertIsSatisfied();
    }

    public class TestInvoker implements FlowElementInvoker
    {
        @Override
        public FlowElement invoke(FlowEventListener flowEventListener, String moduleName, String flowName, FlowInvocationContext flowInvocationContext, FlowEvent flowEvent, FlowElement flowElement)
        {
            return null;
        }

        @Override
        public void setIgnoreContextInvocation(boolean ignoreContextInvocation)
        {

        }

        @Override
        public void setFlowInvocationContextListeners(List list)
        {

        }

        @Override
        public void setInvokeContextListeners(boolean invokeContextListeners)
        {

        }

        @Override
        public String getInvokerType()
        {
            return null;
        }
    }

}
