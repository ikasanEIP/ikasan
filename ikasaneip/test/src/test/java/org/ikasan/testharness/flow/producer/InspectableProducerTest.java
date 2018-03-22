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
package org.ikasan.testharness.flow.producer;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.jms.core.IkasanJmsTemplate;

/**
 * Tests for the <code>InspectableProducer</code> class.
 *
 * @author Ikasan Development Team
 *
 */
public class InspectableProducerTest
{
    private final String DUMMY_MESSAGE1 = "message1";
    private final String DUMMY_MESSAGE2 = "message2";
    private final String DUMMY_MESSAGE3 = "message3";

    @Test
    public void testInspectableProducerSimple()
    {
        InspectableProducer<String> inspectableProducer = new InspectableProducer<>();
        Assert.assertEquals("useJms property should be false", false, inspectableProducer.isUseJms());

        inspectableProducer.invoke(DUMMY_MESSAGE1);
        Assert.assertEquals("message count", 1, inspectableProducer.getEventCount());

        inspectableProducer.invoke(DUMMY_MESSAGE2);
        Assert.assertEquals("message count", 2, inspectableProducer.getEventCount());

        inspectableProducer.getEvents().clear();
        inspectableProducer.invoke(DUMMY_MESSAGE3);
        Assert.assertEquals("message count", 1, inspectableProducer.getEventCount());
    }

    @Test
    public void testInspectableProducerJms()
    {
        InspectableProducer<String> inspectableProducer = new InspectableProducer<>(new IkasanJmsTemplate());
        Assert.assertEquals("useJms property should be true", true, inspectableProducer.isUseJms());
        inspectableProducer.setUseJms(false);

        inspectableProducer.invoke(DUMMY_MESSAGE1);
        Assert.assertEquals("message count", 1, inspectableProducer.getEventCount());

        inspectableProducer.invoke(DUMMY_MESSAGE2);
        Assert.assertEquals("message count", 2, inspectableProducer.getEventCount());

        inspectableProducer.getEvents().clear();
        inspectableProducer.invoke(DUMMY_MESSAGE3);
        Assert.assertEquals("message count", 1, inspectableProducer.getEventCount());
    }
}



