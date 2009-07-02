/*
 * $Id$
 * $URL$
 * 
 * ====================================================================
 * Ikasan Enterprise Integration Platform
 * Copyright (c) 2003-2008 Mizuho International plc. and individual contributors as indicated
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the 
 * Free Software Foundation Europe e.V. Talstrasse 110, 40217 Dusseldorf, Germany 
 * or see the FSF site: http://www.fsfeurope.org/.
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
     * Test constructor used to reconstitute Events
     */
    @Test
    public void testConstructor_String(){
    	String eventId = "eventId";
		Event event = new Event (eventId);
    	Assert.assertEquals("id should be athat passed to constructor",eventId, event.getId());
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
        
        Assert.assertEquals(clone.getNoNamespaceSchemaLocation(),
                event.getNoNamespaceSchemaLocation());
        
        Assert.assertEquals(clone.getSchemaInstanceNSURI(),
                event.getSchemaInstanceNSURI());

        Assert.assertEquals(clone.getId(),
                event.getId());

        Assert.assertTrue(clone.getTimestamp() != event.getTimestamp());
        Assert.assertEquals(clone.getTimestamp(),
                event.getTimestamp());

        Assert.assertEquals(clone.getTimestampFormat(),
                event.getTimestampFormat());
        
        Assert.assertEquals(clone.getTimezone(),
                event.getTimezone());
        
        Assert.assertTrue(clone.getPriority() != event.getPriority());
        Assert.assertEquals(clone.getPriority(),
                event.getPriority());
        
        Assert.assertEquals(clone.getName(),
                event.getName());
        
        Assert.assertEquals(clone.getSpec(),
                event.getSpec());
        
        Assert.assertEquals(clone.getEncoding(),
                event.getEncoding());
        
        Assert.assertEquals(clone.getFormat(),
                event.getFormat());
        
        Assert.assertEquals(clone.getCharset(),
                event.getCharset());
        
        Assert.assertEquals(clone.getChecksum(),
                event.getChecksum());
        
        Assert.assertEquals(clone.getChecksumAlg(),
                event.getChecksumAlg());
        
        Assert.assertEquals(clone.getSrcSystem(),
                event.getSrcSystem());

        Assert.assertEquals(clone.getTargetSystems(),
                event.getTargetSystems());

        Assert.assertEquals(clone.getProcessIds(),
                event.getProcessIds());

        Assert.assertEquals(clone.getResubmissionInfo(),
                event.getResubmissionInfo());
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
        
        Assert.assertEquals(spawned.getNoNamespaceSchemaLocation(),
                event.getNoNamespaceSchemaLocation());
        
        Assert.assertEquals(spawned.getSchemaInstanceNSURI(),
                event.getSchemaInstanceNSURI());

        Assert.assertFalse(spawned.getId().equals(event.getId()));

        Assert.assertTrue(spawned.getTimestamp() != event.getTimestamp());
        Assert.assertFalse(spawned.getTimestamp().equals(event.getTimestamp()));

        Assert.assertEquals(spawned.getTimestampFormat(),
                event.getTimestampFormat());
        
        Assert.assertEquals(spawned.getTimezone(),
                event.getTimezone());
        
        Assert.assertEquals(spawned.getPriority(),
                event.getPriority());
        
        Assert.assertEquals(spawned.getName(),
                event.getName());
        
        Assert.assertEquals(spawned.getSpec(),
                event.getSpec());
        
        Assert.assertEquals(spawned.getEncoding(),
                event.getEncoding());
        
        Assert.assertEquals(spawned.getFormat(),
                event.getFormat());
        
        Assert.assertEquals(spawned.getCharset(),
                event.getCharset());
        
        Assert.assertEquals(spawned.getChecksum(),
                event.getChecksum());
        
        Assert.assertEquals(spawned.getChecksumAlg(),
                event.getChecksumAlg());
        
        Assert.assertEquals(spawned.getSrcSystem(),
                event.getSrcSystem());

        Assert.assertEquals(spawned.getTargetSystems(),
                event.getTargetSystems());

        Assert.assertEquals(spawned.getProcessIds(),
                event.getProcessIds());

        Assert.assertEquals(spawned.getResubmissionInfo(),
                event.getResubmissionInfo());
    }

 
}
