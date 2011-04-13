/*
 * $Id$
 * $URL$
 *
 * ====================================================================
 *
 * Copyright (c) 2000-20010 by Mizuho International plc.
 * All Rights Reserved.
 *
 * ====================================================================
 *
 */
package org.ikasan.flow.event;

import junit.framework.Assert;

import org.ikasan.flow.event.FlowEventFactory;
import org.ikasan.spec.event.EventFactory;
import org.ikasan.spec.flow.FlowEvent;
import org.junit.Before;
import org.junit.Test;

import com.rits.cloning.Cloner;

/**
 * This test class supports the <code>DefaultReplicationFactory</code> class.
 * 
 * @author Ikasan Development Team
 */
public class DefaultReplicationFactoryTest
{
    /** event factory */
    private EventFactory<FlowEvent<?,?>> eventFactory = new FlowEventFactory();

    /** flowEvent */
    private FlowEvent flowEvent;

    /** Replication factory on test */
    private ReplicationFactory<FlowEvent> replicationFactory = 
        new DefaultReplicationFactory(new Cloner());

    private boolean mutablePayload = Boolean.TRUE;
    
    private boolean immutablePayload = Boolean.FALSE;

    @Before
    public void setup()
    {
        flowEvent = eventFactory.newEvent("identifier", "payload");
    }
    
    /**
     * Test successful replication of a flowEvent with a 
     * StringBuilder payload.
     */
    @Test
    public void test_replication_flowEvent_StringBuilderPayload()
    {
        flowEvent.setPayload(new StringBuilder("this is a stringBuilder payload"));
        executeCommonAssertions(flowEvent, replicationFactory.replicate(flowEvent), mutablePayload);
    }

    /**
     * Test successful replication of a flowEvent with a 
     * Integer payload.
     */
    @Test
    public void test_replication_flowEvent_IntegerPayload()
    {
        flowEvent.setPayload(new Integer(10));
        executeCommonAssertions(flowEvent, replicationFactory.replicate(flowEvent), immutablePayload);

    }

    /**
     * Common assertions standard to all tests.
     * @param original
     * @param replicated
     */
    private void executeCommonAssertions(FlowEvent original, FlowEvent replicated, boolean mutablePayload)
    {
        Assert.assertNotNull(replicated);
        Assert.assertNotSame(original, replicated);

        // identifiers are Strings; Strings are immutable so no need to clone
        Assert.assertSame(original.getIdentifier(), replicated.getIdentifier());

        Assert.assertNotSame(original.getTimestamp(), replicated.getTimestamp());

        if(mutablePayload)
        {
            Assert.assertNotSame(flowEvent.getPayload(), replicated.getPayload());
        }
        else
        {
            Assert.assertSame(flowEvent.getPayload(), replicated.getPayload());
        }
    }
}
