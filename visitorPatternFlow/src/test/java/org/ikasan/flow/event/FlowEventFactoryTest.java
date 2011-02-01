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
import org.ikasan.spec.flow.event.EventFactory;
import org.ikasan.spec.flow.event.FlowEvent;
import org.junit.Test;

/**
 * This test class supports the <code>FlowEventFactory</code> class.
 * 
 * @author Ikasan Development Team
 */
public class FlowEventFactoryTest
{
    /**
     * Test success FlowEventFactory flowEvent creation.
     */
    @Test
    public void test_newEvent()
    {
        long before = System.currentTimeMillis();
        EventFactory<FlowEvent<?>> eventFactory = new FlowEventFactory();
        FlowEvent flowEvent = eventFactory.newEvent("identifier", "original payload");
        long after = System.currentTimeMillis();

        Assert.assertNotNull(flowEvent);
        Assert.assertEquals("identifier", flowEvent.getIdentifier());
        Assert.assertTrue(before <= flowEvent.getTimestamp() && flowEvent.getTimestamp() <= after);
        Assert.assertEquals("original payload", flowEvent.getPayload());
        
        flowEvent.setPayload(new String("Im a new payload"));
        Assert.assertEquals("Im a new payload", flowEvent.getPayload());

        flowEvent.setPayload(new Integer(10));
        Assert.assertEquals(new Integer(10), flowEvent.getPayload());
    }

}
