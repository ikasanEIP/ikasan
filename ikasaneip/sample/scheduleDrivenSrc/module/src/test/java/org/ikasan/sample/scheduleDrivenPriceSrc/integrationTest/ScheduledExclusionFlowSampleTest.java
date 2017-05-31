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
package org.ikasan.sample.scheduleDrivenPriceSrc.integrationTest;

import org.ikasan.platform.IkasanEIPTest;
import org.ikasan.spec.flow.Flow;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.quartz.SchedulerException;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;

/**
 * Test class for sample <code>SchedulerExclusion</code> flow.
 * 
 * @author Ikasan Development Team
 */
@RunWith(SpringJUnit4ClassRunner.class)
//specifies the Spring configuration to load for this test fixture
@ContextConfiguration(locations={
        "/demo-exclusion-flow-conf.xml",
        "/demo-exclusion-component-conf.xml",
        "/exclusion-flow-conf.xml",
        "/substitute-components.xml",
        "/exception-conf.xml",
        "/hsqldb-conf.xml"
      })
      
public class ScheduledExclusionFlowSampleTest extends IkasanEIPTest
{
    @Resource
    Flow demoExclusionScheduledConverterFlow;
    
    @Test
    public void test_flow_scheduledExclusionConsumer() throws SchedulerException
    {
        // setup the expected component invocations
        ikasanFlowTestRule.withFlow(demoExclusionScheduledConverterFlow)
                          .scheduledConsumer("Scheduled Consumer")
                          .converter("Scheduled Converter"); // note no producer since the converter throws exception

        // start the flow
        ikasanFlowTestRule.startFlow(testHarnessFlowEventListener);

        // invoke the scheduled consumer
        ikasanFlowTestRule.fireScheduledConsumer();

        // wait for a brief while to let the flow complete
        ikasanFlowTestRule.sleep(1000L);
    }
}
