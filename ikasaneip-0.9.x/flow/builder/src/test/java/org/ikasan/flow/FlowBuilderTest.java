/* 
 * $Id: SchedulerFactoryTest.java 3817 2011-10-30 23:40:35Z mitcje $
 * $URL: https://open.jira.com/svn/IKASAN/branches/ikasaneip-0.9.x/scheduler/src/test/java/org/ikasan/scheduler/SchedulerFactoryTest.java $
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
package org.ikasan.flow;

import junit.framework.Assert;

import org.ikasan.flow.FlowBuilder;
import org.ikasan.scheduler.ScheduledJobFactory;
import org.ikasan.spec.component.endpoint.Consumer;
import org.ikasan.spec.component.transformation.Converter;
import org.ikasan.spec.component.transformation.Translator;
import org.ikasan.spec.flow.Flow;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Test;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;

/**
 * This test class supports the <code>FlowBuilder</code> class.
 * 
 * @author Ikasan Development Team
 */
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

    /** Mock consumer */
    final Consumer consumer = mockery.mock(Consumer.class, "mockConsumer");

    /** Mock converter */
    final Converter converter = mockery.mock(Converter.class, "mockConverter");

    /** Mock transformer */
    final Translator translator = mockery.mock(Translator.class, "mockTranslator");

    /**
     * Test successful retrieval of the scheduler instance.
     * @throws SchedulerException 
     */
    @Test
    public void test_getScheduler() throws SchedulerException
    {
        // expectations
        mockery.checking(new Expectations()
        {
            {
            }
        });

        FlowBuilder flowBuilder = FlowBuilder.newInstance();
        flowBuilder.setConsumer("myConsumerName", consumer);
        flowBuilder.append("myConverter", converter);
        flowBuilder.append("myTranslator", translator);

        Flow flow = flowBuilder.create("flowName", "moduleName");
        Assert.assertNotNull(flow);
        mockery.assertIsSatisfied();
    }
}
