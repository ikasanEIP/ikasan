/* 
 * $Id: PriceFlowSampleTest.java 4360 2013-07-17 14:16:07Z mitcje $
 * $URL: https://open.jira.com/svn/IKASAN/trunk/ikasaneip/sample/genericTechPriceSrc/module/src/test/java/org/ikasan/sample/genericTechDrivenPriceSrc/integrationTest/PriceFlowSampleTest.java $
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
package org.ikasan.platform;

import org.ikasan.testharness.flow.FlowSubject;
import org.ikasan.testharness.flow.rule.IkasanFlowTestRule;
import org.junit.Rule;
import org.springframework.test.context.ContextConfiguration;

import javax.annotation.Resource;

/**
 * Base test class for convenience of loading of all Ikasan EIP spring resources.
 *
 * @author Ikasan Development Team
 */
//specifies the Spring configuration to load for this test fixture
@ContextConfiguration(locations={
        "/recoveryManager-service-conf.xml",
        "/exclusion-service-conf.xml",
        "/serialiser-service-conf.xml",
        "/error-reporting-service-conf.xml",
        "/scheduler-service-conf.xml",
        "/configuration-service-conf.xml",
        "/systemevent-service-conf.xml",
        "/module-service-conf.xml",
        "/replay-service-conf.xml",
        "/ikasan-transaction-conf.xml",
        "/test-service-conf.xml"
        })

public class IkasanEIPTest
{

    @Resource
    protected FlowSubject testHarnessFlowEventListener;

    @Rule
    public IkasanFlowTestRule ikasanFlowTestRule = new IkasanFlowTestRule(); // needs to be public to make JUnit happy

    // Just use this class for Spring context loading.
}
