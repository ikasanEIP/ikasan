/*
 * $Id: EventTest.java 16808 2009-04-27 07:28:17Z mitcje $
 * $URL: svn+ssh://svc-vcsp/architecture/ikasan/trunk/framework/src/test/java/org/ikasan/framework/component/EventTest.java $
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

import junit.framework.JUnit4TestAdapter;

import org.ikasan.common.Payload;
import org.ikasan.framework.component.Event;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * This test class supports the <code>EventTest</code> class.
 * 
 * @author Ikasan Development Team
 */
public class EventTest
{
    /** CG name */
    private String componentGroupName = "groupName";
    
    /** Component Name */
    private String componentName = "componentName";
    
    /** The event */
    private Event event;
    
    /**
     * Setup runs before each test
     */
    @Before
    public void setUp()
    {
        // create a dummy event
        this.event = new Event(this.componentGroupName, this.componentName);
    }

    /**
     * Test cloning an event.
     * @throws CloneNotSupportedException 
     */
    @Test
    public void test_EventClone()
        throws CloneNotSupportedException
    {
        Event clone = this.event.clone();
        
        Assert.assertTrue(clone != this.event);

        Assert.assertTrue(clone.getPayloads() != this.event.getPayloads());
        for(int i=0; i < this.event.getPayloads().size(); i++)
        {
            Payload eventPayload = this.event.getPayloads().get(i);
            Payload clonePayload = clone.getPayloads().get(i);
            
            Assert.assertTrue(eventPayload != clonePayload);
            Assert.assertEquals(eventPayload, clonePayload);
        }
        
        Assert.assertEquals(clone.getNoNamespaceSchemaLocation(),
                this.event.getNoNamespaceSchemaLocation());
        
        Assert.assertEquals(clone.getSchemaInstanceNSURI(),
                this.event.getSchemaInstanceNSURI());

        Assert.assertEquals(clone.getId(),
                this.event.getId());

        Assert.assertTrue(clone.getTimestamp() != this.event.getTimestamp());
        Assert.assertEquals(clone.getTimestamp(),
                this.event.getTimestamp());

        Assert.assertEquals(clone.getTimestampFormat(),
                this.event.getTimestampFormat());
        
        Assert.assertEquals(clone.getTimezone(),
                this.event.getTimezone());
        
        Assert.assertTrue(clone.getPriority() != this.event.getPriority());
        Assert.assertEquals(clone.getPriority(),
                this.event.getPriority());
        
        Assert.assertEquals(clone.getName(),
                this.event.getName());
        
        Assert.assertEquals(clone.getSpec(),
                this.event.getSpec());
        
        Assert.assertEquals(clone.getEncoding(),
                this.event.getEncoding());
        
        Assert.assertEquals(clone.getFormat(),
                this.event.getFormat());
        
        Assert.assertEquals(clone.getCharset(),
                this.event.getCharset());
        
        Assert.assertTrue(clone.getSize() != this.event.getSize());
        Assert.assertEquals(clone.getSize(),
                this.event.getSize());
        
        Assert.assertEquals(clone.getChecksum(),
                this.event.getChecksum());
        
        Assert.assertEquals(clone.getChecksumAlg(),
                this.event.getChecksumAlg());
        
        Assert.assertEquals(clone.getSrcSystem(),
                this.event.getSrcSystem());

        Assert.assertEquals(clone.getTargetSystems(),
                this.event.getTargetSystems());

        Assert.assertEquals(clone.getProcessIds(),
                this.event.getProcessIds());

        Assert.assertEquals(clone.getResubmissionInfo(),
                this.event.getResubmissionInfo());
    }

    /**
     * Test spawning a new event.
     * @throws CloneNotSupportedException 
     * @throws InterruptedException 
     */
    @Test
    public void test_EventSpawn()
        throws CloneNotSupportedException, InterruptedException
    {
        // TODO - we should look to move to nano precision on timestamp, however,
        // in the meantime....
        // need to sleep to force a change in the timestamp between
        // the current event and the spawned event
        Thread.sleep(100);
        
        Event spawned = this.event.spawn();
        
        Assert.assertTrue(spawned != this.event);

        Assert.assertTrue(spawned.getPayloads() != this.event.getPayloads());
        for(int i=0; i < this.event.getPayloads().size(); i++)
        {
            Payload eventPayload = this.event.getPayloads().get(i);
            Payload clonePayload = spawned.getPayloads().get(i);
            
            Assert.assertTrue(eventPayload != clonePayload);
            Assert.assertEquals(eventPayload, clonePayload);
        }
        
        Assert.assertEquals(spawned.getNoNamespaceSchemaLocation(),
                this.event.getNoNamespaceSchemaLocation());
        
        Assert.assertEquals(spawned.getSchemaInstanceNSURI(),
                this.event.getSchemaInstanceNSURI());

        Assert.assertFalse(spawned.getId().equals(this.event.getId()));

        Assert.assertTrue(spawned.getTimestamp() != this.event.getTimestamp());
        Assert.assertFalse(spawned.getTimestamp().equals(this.event.getTimestamp()));

        Assert.assertEquals(spawned.getTimestampFormat(),
                this.event.getTimestampFormat());
        
        Assert.assertEquals(spawned.getTimezone(),
                this.event.getTimezone());
        
        Assert.assertTrue(spawned.getPriority() != this.event.getPriority());
        Assert.assertEquals(spawned.getPriority(),
                this.event.getPriority());
        
        Assert.assertEquals(spawned.getName(),
                this.event.getName());
        
        Assert.assertEquals(spawned.getSpec(),
                this.event.getSpec());
        
        Assert.assertEquals(spawned.getEncoding(),
                this.event.getEncoding());
        
        Assert.assertEquals(spawned.getFormat(),
                this.event.getFormat());
        
        Assert.assertEquals(spawned.getCharset(),
                this.event.getCharset());
        
        Assert.assertTrue(spawned.getSize() != this.event.getSize());
        Assert.assertEquals(spawned.getSize(),
                this.event.getSize());
        
        Assert.assertEquals(spawned.getChecksum(),
                this.event.getChecksum());
        
        Assert.assertEquals(spawned.getChecksumAlg(),
                this.event.getChecksumAlg());
        
        Assert.assertEquals(spawned.getSrcSystem(),
                this.event.getSrcSystem());

        Assert.assertEquals(spawned.getTargetSystems(),
                this.event.getTargetSystems());

        Assert.assertEquals(spawned.getProcessIds(),
                this.event.getProcessIds());

        Assert.assertEquals(spawned.getResubmissionInfo(),
                this.event.getResubmissionInfo());
    }

    /**
     * Teardown after each test
     */
    @After
    public void tearDown()
    {
        this.event = null;
    }

    /**
     * The suite is this class
     * 
     * @return JUnit Test class
     */
    public static junit.framework.Test suite()
    {
        return new JUnit4TestAdapter(EventTest.class);
    }
}
