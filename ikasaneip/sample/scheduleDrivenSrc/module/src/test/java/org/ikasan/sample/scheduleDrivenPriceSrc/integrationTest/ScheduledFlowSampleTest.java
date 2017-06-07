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

import javax.annotation.Resource;

import org.ikasan.sample.scheduleDrivenSrc.component.converter.ScheduleEventFailingConverter;
import org.ikasan.spec.flow.Flow;
import org.ikasan.spec.module.Module;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.quartz.SchedulerException;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.ikasan.platform.IkasanEIPTest;

/**
 * Test class for <code>PriceFlowSample</code>.
 * 
 * @author Ikasan Development Team
 */
@RunWith(SpringJUnit4ClassRunner.class)
//specifies the Spring configuration to load for this test fixture
@ContextConfiguration(locations={
        "/demo-flow-conf.xml",
        "/demo-component-conf.xml",
        "/demo-exclusion-flow-conf.xml",
        "/demo-exclusion-component-conf.xml",
        "/exclusion-flow-conf.xml",
        "/substitute-components.xml",
        "/ikasan-transaction-conf.xml",
        "/module-conf.xml",
        "/exception-conf.xml", 
        "/replay-service-conf.xml",
        "/configuration-service-conf.xml",
        "/hsqldb-conf.xml"
      })
@DirtiesContext
public class ScheduledFlowSampleTest extends IkasanEIPTest
{
    @Resource
    Module<Flow> module;

    @Resource
    private Flow demoExclusionScheduledConverterFlow;

    @Resource
    private ScheduleEventFailingConverter scheduleEventFailingConverter;
    
    @Test
    public void test_flow_consumer_translator_producer() throws SchedulerException
    {
        for(Flow flow:module.getFlows())
        {
            flow.start();
        }
        
        try
        {
            Thread.sleep(5000);
        }
        catch(InterruptedException e)
        {
            // dont care
        }
        
        for(Flow flow:module.getFlows())
        {
            flow.stop();
        }
    }

    @Test
    @Ignore
    public void test_recovery_flow() throws InterruptedException
    {

        demoExclusionScheduledConverterFlow.start();

        Thread.sleep(7000L);

        //Assert.assertEquals("recovering", demoExclusionScheduledConverterFlow.getState());
        //Assert.assertEquals(2, scheduleEventFailingConverter.getInvocationCount());
        demoExclusionScheduledConverterFlow.stop();
        Assert.assertEquals("stopped", demoExclusionScheduledConverterFlow.getState());


        Thread.sleep(7000L);
        //Assert.assertEquals("stopped", demoExclusionScheduledConverterFlow.getState());
        // no more invocations
        //Assert.assertEquals(2, scheduleEventFailingConverter.getInvocationCount());

    }
}
