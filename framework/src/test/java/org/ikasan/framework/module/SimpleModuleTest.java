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
package org.ikasan.framework.module;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import junit.framework.Assert;

import org.ikasan.framework.flow.Flow;
import org.ikasan.framework.initiator.Initiator;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.Test;

/**
 * Test class for SimpleModule
 * 
 * @author Ikasan Development Team
 *
 */
public class SimpleModuleTest {
	
    /**
     * Mockery for interfaces
     */
    private Mockery  mockery = new Mockery();
    
	/**
	 * test name for the module
	 */
	private String moduleName = "moduleName";
	

	/**
	 * Tests the constructor
	 */
	@Test
	public void testSimpleModuleStringListOfInitiator() {
		Initiator mockInitiator1 = mockery.mock(Initiator.class, "initiator1");
		Initiator mockInitiator2 = mockery.mock(Initiator.class, "initiator2");
		List<Initiator> initiators = new ArrayList<Initiator>();
		initiators.add(mockInitiator1);
		initiators.add(mockInitiator2);
		SimpleModule simpleModule = new SimpleModule(moduleName, initiators);
		
		Assert.assertEquals("constructor should set module name", moduleName, simpleModule.getName());
		Assert.assertEquals("constructor should set initiators", initiators, simpleModule.getInitiators());

	}

	/**
	 * Tests the constructor
	 */
	@Test
	public void testSimpleModuleString() {
		
		SimpleModule simpleModule = new SimpleModule(moduleName);
		Assert.assertEquals("constructor should set module name", moduleName, simpleModule.getName());
	}
	
	/**
	 * Tests that getFlows returns all of the Initiators flows;
	 */
	@Test
	public void testGetFlows(){
		final Initiator mockInitiator1 = mockery.mock(Initiator.class, "initiator1");
		final Initiator mockInitiator2 = mockery.mock(Initiator.class, "initiator2");
		final Initiator mockInitiator3 = mockery.mock(Initiator.class, "initiator3");
		final String mockFlow1Name = "mockFlow1";
		final String mockFlow2Name = "mockFlow2";
		
		final Flow mockFlow1 = mockery.mock(Flow.class, mockFlow1Name);
		final Flow mockFlow2 = mockery.mock(Flow.class, mockFlow2Name);
		List<Initiator> initiators = new ArrayList<Initiator>();
		initiators.add(mockInitiator1);
		initiators.add(mockInitiator2);
		initiators.add(mockInitiator3);
		
        mockery.checking(new Expectations()
        {
            {
                one(mockInitiator1).getFlow();will(returnValue(mockFlow1));
                one(mockInitiator2).getFlow();will(returnValue(mockFlow1));
                one(mockInitiator3).getFlow();will(returnValue(mockFlow2));
                allowing(mockFlow1).getName();will(returnValue(mockFlow1Name));
                allowing(mockFlow2).getName();will(returnValue(mockFlow2Name));
            }
        });
		SimpleModule simpleModule = new SimpleModule(moduleName, initiators);
		
		
		Map<String, Flow> flows = simpleModule.getFlows();
		
		Assert.assertEquals("getFlows should return exactlry 2 flows", 2, flows.size());
		Assert.assertEquals("moduleFlows should contain mockFlow1", flows.get(mockFlow1Name),mockFlow1);
		Assert.assertEquals("moduleFlows should contain mockFlow2", flows.get(mockFlow2Name),mockFlow2);
		
	}
	
	/**
     * Tests that getInitiators returns all of the Initiators;
     */
    @Test
    public void testGetInitiators(){
        final Initiator mockInitiator1 = mockery.mock(Initiator.class, "initiator1");
        final Initiator mockInitiator2 = mockery.mock(Initiator.class, "initiator2");
        
        List<Initiator> initiators = new ArrayList<Initiator>();
        initiators.add(mockInitiator1);
        initiators.add(mockInitiator2);

        
        mockery.checking(new Expectations()
        {
            {
                allowing(mockInitiator1).getName();will(returnValue("initiator1"));
                allowing(mockInitiator2).getName();will(returnValue("initiator2"));
            }
        });
        SimpleModule simpleModule = new SimpleModule(moduleName, initiators);
        
        Assert.assertEquals("getInitiators should return all initiators", initiators, simpleModule.getInitiators());

    }

    /**
     * Tests that getInitiator returns the named Initiator or null if it doesnt exist;
     */
    @Test
    public void testGetInitiator(){
        final String initiator1Name = "initiator1";
        final Initiator mockInitiator1 = mockery.mock(Initiator.class, initiator1Name);
        
        List<Initiator> initiators = new ArrayList<Initiator>();
        initiators.add(mockInitiator1);

        
        mockery.checking(new Expectations()
        {
            {
                allowing(mockInitiator1).getName();will(returnValue(initiator1Name));
            }
        });
        SimpleModule simpleModule = new SimpleModule(moduleName, initiators);
        
        Assert.assertEquals("getInitiator should return the initiator for the name given", mockInitiator1, simpleModule.getInitiator(initiator1Name));
        Assert.assertNull("getInitiator should return null for an unknown initiator name", simpleModule.getInitiator("unknownInitiatorName"));

    }
}
