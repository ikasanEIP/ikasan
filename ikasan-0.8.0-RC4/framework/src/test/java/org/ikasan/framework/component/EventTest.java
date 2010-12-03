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
package org.ikasan.framework.component;

import java.util.ArrayList;
import java.util.List;

import org.ikasan.common.Payload;
import org.jmock.Mockery;
import org.junit.Assert;
import org.junit.Test;

/**
 * This test class supports the <code>EventTest</code> class.
 * 
 * @author Ikasan Development Team
 */
public class EventTest
{
	
	/**
	 * Mockery for mocking stuff
	 */
	private Mockery mockery = new Mockery();
	
	/**
	 * Name for the module that creates an event for the first time
	 */
	private String originatingModuleName = "originatingModuleName";
	
	
	/**
	 * Name for the component that creates an event for the first time
	 */
	private String originatingComponentName = "originatingComponentName";
	
	/**
	 * Id supplied by the component that creates an event for the first time
	 */	
	private String originatorGeneratedId = "originatorGeneratedId";
    

    /**
     * Test constructor used to originate new Events
     */
    @Test
    public void testConstructor_StringStringStringListOfPayloads(){
    	List<Payload> payloads = new ArrayList<Payload>();
    	
    	Event event = new Event (originatingModuleName, originatingComponentName, originatorGeneratedId,payloads);
    	Assert.assertEquals("id should be a function of the originating moduleName, componentName, and generatedId", originatingModuleName+"_"+originatingComponentName+"_"+originatorGeneratedId, event.getId());
    	Assert.assertEquals("event payloads should be those supplied to constructor", payloads, event.getPayloads());
    }

    /**
     * Test constructor used to originate new Events
     */
    @Test
    public void testConstructor_StringStringStringPayload(){
    	Payload payload = mockery.mock(Payload.class);
    	
    	Event event = new Event (originatingModuleName, originatingComponentName, originatorGeneratedId,payload);
    	Assert.assertEquals("id should be a function of the originating moduleName, componentName, and generatedId", originatingModuleName+"_"+originatingComponentName+"_"+originatorGeneratedId, event.getId());
    	Assert.assertEquals("only payload should be that supplied to constructor", payload, event.getPayloads().get(0));
    }
    

    
    /**
     * Test cloning an event.
     * @throws CloneNotSupportedException 
     */
    @Test
    public void test_EventClone()
        throws CloneNotSupportedException
    {
    	Event event = new Event(originatingModuleName, originatingComponentName, originatorGeneratedId,new ArrayList<Payload>());
    	
    	
        Event clone = event.clone();
        
        Assert.assertTrue(clone != event);

        Assert.assertTrue(clone.getPayloads() != event.getPayloads());
        for(int i=0; i < event.getPayloads().size(); i++)
        {
            Payload eventPayload = event.getPayloads().get(i);
            Payload clonePayload = clone.getPayloads().get(i);
            
            Assert.assertTrue(eventPayload != clonePayload);
            Assert.assertEquals(eventPayload, clonePayload);
        }
        


        Assert.assertEquals(clone.getId(),
                event.getId());

        Assert.assertEquals(clone.getTimestamp(),
                event.getTimestamp());


        Assert.assertEquals(clone.getPriority(),
                event.getPriority());
        


 
    }

    /**
     * Test spawning a new event.
     * @throws CloneNotSupportedException 
     * @throws InterruptedException 
     */
    @Test
    public void testSpawnChild()
        throws CloneNotSupportedException, InterruptedException
    {
    	Event event = new Event(originatingModuleName, originatingComponentName, originatorGeneratedId,new ArrayList<Payload>());
    	String spawningModuleName = "moduleName";
    	String spawningComponentName = "componentName";
    	
    	List<Payload> childPayloads = new ArrayList<Payload>();
    	
    	
        // TODO - we should look to move to nano precision on timestamp, however,
        // in the meantime....
        // need to sleep to force a change in the timestamp between
        // the current event and the spawned event
        Thread.sleep(100);
        
        //spawn the child Event
        Event spawned = event.spawnChild(spawningModuleName, spawningComponentName, 0, childPayloads);
        
        String expectedFirstChildsId = spawningModuleName+"_"+spawningComponentName+"_#"+event.getId()+".0";
        Assert.assertEquals("spawned child should have an id that is based on it's parents", expectedFirstChildsId, spawned.getId());
        
      
        Assert.assertTrue(spawned != event);

        Assert.assertEquals(childPayloads, spawned.getPayloads());
        
 
        Assert.assertFalse(spawned.getId().equals(event.getId()));

        Assert.assertTrue(spawned.getTimestamp() != event.getTimestamp());

        
        Assert.assertEquals(spawned.getPriority(),
                event.getPriority());
        
        
   


      
    }

 
}
