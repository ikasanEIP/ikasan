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
package org.ikasan.flow.event;

import com.rits.cloning.Cloner;
import org.ikasan.spec.event.EventFactory;
import org.ikasan.spec.event.ReplicationFactory;
import org.ikasan.spec.flow.FlowEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;

/**
 * This test class supports the <code>ClonerReplicationFactory</code> class.
 * 
 * @author Ikasan Development Team
 */
class ClonerReplicationFactoryTest
{
    /** event factory */
    private EventFactory<FlowEvent<?,?>> eventFactory = new FlowEventFactory();

    /** flowEvent */
    private FlowEvent flowEvent;

    /** Replication factory on test */
    private ReplicationFactory<FlowEvent> replicationFactory = 
        new ClonerReplicationFactory(new Cloner());

    private boolean mutablePayload = Boolean.TRUE;
    
    private boolean immutablePayload = Boolean.FALSE;

    @BeforeEach
    void setup()
    {
        flowEvent = eventFactory.newEvent("identifier", "payload");
    }

    /**
     * Test successful replication of a flowEvent with a 
     * StringBuilder payload.
     */
    // TODO: 07/11/2023 revist this test
    @Test
    @Disabled
    void test_replication_flowEvent_StringBuilderPayload()
    {
        flowEvent.setPayload(new StringBuilder("this is a stringBuilder payload"));
        executeCommonAssertions(flowEvent, replicationFactory.replicate(flowEvent), mutablePayload);
    }

    /**
     * Test successful replication of a flowEvent with a 
     * Integer payload.
     */
    @Test
    void test_replication_flowEvent_IntegerPayload()
    {
        flowEvent.setPayload(Integer.valueOf(10));
        executeCommonAssertions(flowEvent, replicationFactory.replicate(flowEvent), immutablePayload);

    }

    /**
     * Common assertions standard to all tests.
     * @param original
     * @param replicated
     */
    private void executeCommonAssertions(FlowEvent original, FlowEvent replicated, boolean mutablePayload)
    {
        assertNotNull(replicated);
        assertNotSame(original, replicated);

        // identifiers are Strings; Strings are immutable so no need to clone
        assertSame(original.getIdentifier(), replicated.getIdentifier());

        assertNotSame(original.getTimestamp(), replicated.getTimestamp());

        if(mutablePayload)
        {
            assertNotSame(flowEvent.getPayload(), replicated.getPayload());
        }
        else
        {
            assertSame(flowEvent.getPayload(), replicated.getPayload());
        }
    }
}
