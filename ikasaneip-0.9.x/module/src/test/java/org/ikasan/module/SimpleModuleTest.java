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
package org.ikasan.module;

import java.util.ArrayList;
import java.util.List;

import junit.framework.Assert;

import org.ikasan.module.SimpleModule;
import org.ikasan.spec.module.Module;
import org.ikasan.spec.flow.Flow;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.Test;

/**
 * This test class supports the <code>SimpleModule</code> class.
 * 
 * @author Ikasan Development Team
 */
public class SimpleModuleTest
{
    /**
     * Mockery for mocking concrete classes
     */
    private Mockery mockery = new Mockery();

    /** Mock flow */
    final Flow flow = mockery.mock(Flow.class, "mockFlow");

    /**
     * Test failed constructor due to null flow name.
     */
    @Test(expected = IllegalArgumentException.class)
    public void test_failed_constructorDueToNullName()
    {
        new SimpleModule(null);
    }

    /**
     * Test successful getters/setters.
     */
    @Test
    public void test_successful_mutators()
    {
        Module<Flow> module = new SimpleModule("testModule");
        Assert.assertNull("description should be null", module.getDescription() );
        Assert.assertTrue("name should be 'testModule'", "testModule".equals( module.getName() ) );
        
        module.setDescription("description");
        Assert.assertTrue("description should be 'description", "description".equals( module.getDescription() ) );
    }

    /**
     * Test successful flow accessors.
     */
    @Test
    public void test_successful_flow_accessor()
    {
        List<Flow> flows = new ArrayList<Flow>();
        flows.add(flow);
        flows.add(flow);
        flows.add(flow);
        
        // expectations
        mockery.checking(new Expectations()
        {
            {
                // get each flow name
                one(flow).getName();
                will(returnValue("flowName1"));

                // get each flow name
                one(flow).getName();
                will(returnValue("flowName2"));
            }
        });

        Module<Flow> module = new SimpleModule("testModule", flows);
        Assert.assertTrue("number of flows on module should be 3", module.getFlows().size() == 3 );
        Assert.assertNotNull("Should have returned flowName2", module.getFlow("flowName2"));
    }
}
