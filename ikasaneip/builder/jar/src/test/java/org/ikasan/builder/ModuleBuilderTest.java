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


import static org.ikasan.builder.FlowBuilder.newFlow;

import org.ikasan.exclusion.service.ExclusionServiceFactory;
import org.ikasan.spec.component.endpoint.Consumer;
import org.ikasan.spec.component.endpoint.Producer;
import org.ikasan.spec.event.EventFactory;
import org.ikasan.spec.module.Module;
import org.ikasan.spec.replay.ReplayRecordService;
import org.ikasan.spec.serialiser.SerialiserFactory;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * This test class supports the <code>ModuleBuilder</code> class.
 * 
 * @author Ikasan Development Team
 */
public class ModuleBuilderTest
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
    
    /** Mock Consumer */
    final Consumer consumer = mockery.mock(Consumer.class, "mockConsumer");

    /** Mock Producer */
    final Producer producer = mockery.mock(Producer.class, "mockProducer");

    /** Mock Producer */
    final ExclusionServiceFactory exclusionServiceFactory = mockery.mock(ExclusionServiceFactory.class, "mockExclusionServiceFactory");
    
    /** Mock serialiserFactory */
    final SerialiserFactory serialiserFactory = mockery.mock(SerialiserFactory.class, "mockSerialiserFactory");
    
    /** Mock serialiserFactory */
    final ReplayRecordService replayRecordService = mockery.mock(ReplayRecordService.class, "mockReplayRecordService");

    @Before
    public void setup()
    {
        // expectations
        mockery.checking(new Expectations()
        {
            {
                // set event factory per consumer
                exactly(2).of(consumer).setEventFactory(with(any(EventFactory.class)));

                // get exclusionService instance per flow
                exactly(1).of(exclusionServiceFactory).getExclusionService("moduleName", "flowName1");
                exactly(1).of(exclusionServiceFactory).getExclusionService("moduleName", "flowName2");
            }
        });
    }
    
    /**
     * Test successful flow creation.
     */
    @Test
    public void test_successful_flowCreation() 
    {
    	Module module = ModuleBuilder.newModule("module name").withDescription("module description")
    	.addFlow(newFlow("flowName1", "moduleName").withExclusionServiceFactory(exclusionServiceFactory).withSerialiserFactory(serialiserFactory).withReplayRecordService(replayRecordService)
                .consumer("consumer", consumer)
                .publisher("producer", producer)
                .build())
    	.addFlow(newFlow("flowName2", "moduleName").withExclusionServiceFactory(exclusionServiceFactory).withSerialiserFactory(serialiserFactory).withReplayRecordService(replayRecordService)
                .consumer("consumer", consumer)
                .publisher("producer", producer)
                .build())
    	.build();

    	Assert.assertTrue("module name should be 'module name'", "module name".equals(module.getName()));
    	Assert.assertTrue("module description should be 'module description'", "module description".equals(module.getDescription()));
    	Assert.assertNotNull("module should contain a flow named 'flowName1''", module.getFlow("flowName1"));
    	Assert.assertNotNull("module should contain a flow named 'flowName2''", module.getFlow("flowName2"));
    }
}
