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

import junit.framework.Assert;

import org.ikasan.flow.event.FlowEventFactory;
import org.ikasan.spec.event.EventFactory;
import org.ikasan.spec.flow.FlowEvent;
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
        EventFactory<FlowEvent<?,?>> eventFactory = new FlowEventFactory();
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
