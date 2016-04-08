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

import org.ikasan.spec.flow.Flow;
import org.ikasan.spec.module.Module;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;

/**
 * This test class supports the <code>ModuleFactory</code> class.
 * 
 * @author Ikasan Development Team
 */
@RunWith(SpringJUnit4ClassRunner.class)
//specifies the Spring configuration to load for this test fixture
@ContextConfiguration(locations = { 
        "/module-conf.xml", 
        "/flow-conf.xml",
        "/sample-component-conf.xml",
        "/exclusion-service-conf.xml",
        "/serialiser-service-conf.xml",
        "/substitute-components.xml",
        "/ikasan-transaction-conf.xml",
        "/error-reporting-service-conf.xml",
        "/recoveryManager-service-conf.xml",
        "/scheduler-service-conf.xml", 
        "/configuration-service-conf.xml",
        "/systemevent-service-conf.xml",
        "/module-service-conf.xml",
        "/wiretap-service-conf.xml",
        "/exception-conf.xml",
        "/replay-service-conf.xml",
        "/hsqldb-datasource-conf.xml"
        })
public class ModuleFactoryTest
{
    @Resource
    Module<Flow> module;
    
    /**
     * Test successful flow creation.
     */
    @Test
    public void test_successful_moduleCreation() 
    {
    	Assert.assertTrue("module name should be 'moduleName'", "moduleName".equals(module.getName()));
    	Assert.assertTrue("module description should be 'moduleDescription'", "moduleDescription".equals(module.getDescription()));
    	Assert.assertNotNull("module should contain a flow named 'flowName''", module.getFlow("flowName"));
    }
}
