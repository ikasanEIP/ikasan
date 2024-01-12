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

import org.ikasan.builder.sample.SampleConsumer;
import org.ikasan.spec.flow.Flow;
import org.ikasan.spec.flow.FlowElement;
import org.ikasan.testharness.flow.rule.IkasanFlowTestRule;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;

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
public class SampleFlowTest
{
    @Resource
    Flow flow;


    @Rule
    public IkasanFlowTestRule ikasanFlowTestRule = new IkasanFlowTestRule();


    @Before
    public void setup()
    {
        ikasanFlowTestRule.withFlow(flow);
    }

    /**
     * Test successful flow creation.
     */
    @Test
    public void test_successful_flow_execution() throws InterruptedException
    {

        ikasanFlowTestRule.withFlow(flow)
                .consumer("consumer")
                .converter("converter")
                .translator("translator")
                .broker("broker")
                .multiRecipientRouter("mrRouter")
                .sequencer("sequencerA")
                .producer("producerA")
                .producer("producerA")
                .sequencer("sequencerB")
                .producer("producerB")
                .producer("producerB")
                .router("srRouter")
                .sequencer("sequencerA")
                .blockStart()
                    .producer("producerA")
                    .repeat(2)
                .blockEnd();

        ikasanFlowTestRule.startFlow();

        FlowElement flowElement = flow.getFlowElement("consumer");
        SampleConsumer consumer = (SampleConsumer)flowElement.getFlowComponent();
        consumer.onMessage("test");

        ikasanFlowTestRule.sleep(1L);

        ikasanFlowTestRule.assertIsSatisfied();

    }
}
