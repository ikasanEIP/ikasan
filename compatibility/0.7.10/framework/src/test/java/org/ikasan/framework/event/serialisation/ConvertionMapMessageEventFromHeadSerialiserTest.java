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
package org.ikasan.framework.event.serialisation;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Session;

import org.ikasan.common.Payload;
import org.ikasan.common.component.DefaultPayload;
import org.ikasan.framework.component.Event;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.Test;

/**
 * Unit test for {@link ConvertionMapMessageEventFromHeadSerialiser}
 * 
 * @author Ikasan Development Team
 *
 */
public class ConvertionMapMessageEventFromHeadSerialiserTest
{
    /** Serializer to test */
    private JmsMessageEventSerialiser<MapMessage> serializerToTest = new ConvertionMapMessageEventFromHeadSerialiser();

    /** Object mockery */
    private final Mockery mockery = new Mockery();

    // Various objects used in test cases
    /** Test module name */
    private final String moduleName = "testModuleName";

    /** Test component name */
    private final String componentName = "testComponentName";

    /** A mocked JMS Session */
    private final Session mockJmsSession = this.mockery.mock(Session.class, "jmsSession");

    /** A mocked map message */
    private final MapMessage mockMapMessage = this.mockery.mock(MapMessage.class, "mapMessage");

    /**
     * Given a 0.8.0 version of Ikasan {@link Event}, create a {@link MapMessage} that consumers
     * built on Ikasan 0.7.10 can deserialize.
     * 
     * @throws JMSException thrown if error deserializing map message
     */
    @Test public void convert_mapMessage_of_HEAD_event_to_07x_event() throws JMSException
    {
        // Test objects
        final String eventId = this.moduleName + this.componentName + "test-0.8.0-event";
        final int priority = 4;
        final String payloadId = "test-0.8.0-payload";
        final String content = "Some arbitrary content for testing";
        final Payload payload = new DefaultPayload(payloadId, content.getBytes());
        final String PAYLOD_NAME_ATTRIBUTE_KEY = "payloadName";
        payload.setAttribute(PAYLOD_NAME_ATTRIBUTE_KEY, "testPayload");
        final List<Payload> payloads = new ArrayList<Payload>();
        payloads.add(payload);
        final Event an080Event = new Event(eventId, priority, new Date(), payloads);

        final String expectedPayloadContentMapKey = ConvertionMapMessageEventFromHeadSerialiser.OLD_PAYLOAD_PREFIX + 0 + ConvertionMapMessageEventFromHeadSerialiser.OLD_PAYLOAD_CONTENT_SUFFIX;
        final String expectedPayloadIdMapKey = ConvertionMapMessageEventFromHeadSerialiser.OLD_PAYLOAD_PREFIX + 0 + ConvertionMapMessageEventFromHeadSerialiser.OLD_PAYLOAD_ID_SUFFIX;
        final String expectedPayloadAttributeKey = ConvertionMapMessageEventFromHeadSerialiser.OLD_PAYLOAD_PREFIX + 0 + ConvertionMapMessageEventFromHeadSerialiser.SEPARATOR + PAYLOD_NAME_ATTRIBUTE_KEY;

        // Expectations
        this.mockery.checking(new Expectations()
        {
            {
                // Create the map message
                one(mockJmsSession).createMapMessage(); will(returnValue(mockMapMessage));

                // Set the payload content
                one(mockMapMessage).setBytes(expectedPayloadContentMapKey, content.getBytes());

                // Set the payload id
                one(mockMapMessage).setString(expectedPayloadIdMapKey, payloadId);

                // Set the attribute
                one(mockMapMessage).setString(expectedPayloadAttributeKey, payload.getAttribute(PAYLOD_NAME_ATTRIBUTE_KEY));

                // Setting number of payloads
                one(mockMapMessage).setInt(ConvertionMapMessageEventFromHeadSerialiser.OLD_PAYLOAD_COUNT_PROPERTY, payloads.size());

                // Priority
                one(mockMapMessage).setJMSPriority(an080Event.getPriority());
            }
        });

        // Running the test
        this.serializerToTest.toMessage(an080Event, this.mockJmsSession);

        // Assertions
        this.mockery.assertIsSatisfied();
    }
}
