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
package org.ikasan.framework.component.sequencing;

import java.util.ArrayList;
import java.util.List;

import junit.framework.JUnit4TestAdapter;

import org.ikasan.common.Payload;
import org.ikasan.core.component.sequencing.SequencerException;
import org.ikasan.framework.component.Event;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * This test class supports the <code>TokenizingSplitter</code> class.
 *
 * @author Ikasan Development Team
 */
public class TokenizingSplitterTest
{
    /**
     * Mockery for mocking concrete classes
     */
    private Mockery classMockery = new Mockery()
    {
        {
            setImposteriser(ClassImposteriser.INSTANCE);
        }
    };

    /** class to be tested */
    private TokenizingSplitter tokenSplitter;

    /**
     * Mock the parameter objects
     */
    /** mock event */
    final Event event = classMockery.mock(Event.class);
    /** mock payload */
    final Payload payload = classMockery.mock(Payload.class);
    
    final String moduleName = "moduleName";
    
    final String componentName = "componentName";

    /**
     * Real objects
     */
    /** payload list */
    List<Payload> payloads = new ArrayList<Payload>();

    /**
     * Setup runs before each test
     */
    @Before
    public void setUp()
    {
        // Empty
    }

    /**
     * Test successful delimiter expression and encoding constructor.
     */
    @Test
    public void test_successfulDelimiterExpressionAndEncodingConstructor()
    {
        // Create the class to be tested with
        // delimiter expression and null encoding
        this.tokenSplitter = new TokenizingSplitter("\\$");
    }

    /**
     * Test successful illegal argument exception on failed constructor.
     */
    @Test(expected = IllegalArgumentException.class)
    public void test_failedConstructor()
    {
        // Create the class to be tested with
        // delimiter expression and null encoding
        this.tokenSplitter = new TokenizingSplitter(null);
    }

    /**
     * Test successful event(1):payload(1) with payload content
     * tokenized in to three giving event(3):payload(1).
     * @throws SequencerException Wrapper exception for CloneNotSupportedException.
     * @throws CloneNotSupportedException Thrown when cloning
     *         <code>Event</code>/<code>Payload</code>
     */
    @Test
    public void test_successfulTokenisingSinglePayloadTokenizedToThreeEvents()
        throws SequencerException, CloneNotSupportedException
    {
        final int NUM_EVENTS_OUT = 3; // one payload per event on the splitter

        // create the class to be tested with
        // delimiter (regular) expression and null encoding
        this.tokenSplitter = new TokenizingSplitter("\\$");

        /** Real payload list */
        this.payloads = new ArrayList<Payload>();
        /** Expected three payloads */
        final Payload firstNewPayload = this.classMockery.mock(Payload.class);
        final Payload secondNewPayload = this.classMockery.mock(Payload.class);
        final Payload thirdNewPayload = this.classMockery.mock(Payload.class);

        final Event firstNewEvent = this.classMockery.mock(Event.class);
        final Event secondNewEvent = this.classMockery.mock(Event.class);
        final Event thirdNewEvent = this.classMockery.mock(Event.class);

        // Populate two payload entries
        payloads.add(payload);

        /** Real content - from the mocked payload */
        final String payloadContentStr =
            new String("A, you're adorable$"
                     + "B, you're so beautiful$"
                     + "C, you have some cutiful charms!");
        /** Expected payload content after tokenizing. */
        final String [] expectedSinglePayloadContent =
                {"A, you're adorable",
                 "B, you're so beautiful",
                 "C, you have some cutiful charms!"};

        classMockery.checking(new Expectations()
        {
            {
                //Calls expected during logging.
                exactly(1).of(event).getId();
                exactly(1).of(event).idToString();
                exactly(NUM_EVENTS_OUT).of(payload).getId();

                //Calls expected on incoming event; will be called once only
                exactly(4).of(event).getPayloads();
                will(returnValue(payloads));

                //Calls expected on event's payload; here the original event has only one payload
                //and therefore these methods will be called once.
                exactly(1).of(payload).getContent();
                will(returnValue(payloadContentStr.getBytes()));

                //Calls when creating first payload
                exactly(1).of(payload).spawnChild(0);
                will(returnValue(firstNewPayload));
                exactly(1).of(firstNewPayload).setContent(expectedSinglePayloadContent[0].getBytes());
                exactly(1).of(firstNewPayload).getId();

                //Calls when creating second payload
                exactly(1).of(payload).spawnChild(1);
                will(returnValue(secondNewPayload));
                exactly(1).of(secondNewPayload).setContent(expectedSinglePayloadContent[1].getBytes());
                exactly(1).of(secondNewPayload).getId();

                //Calls when creating third payload
                exactly(1).of(payload).spawnChild(2);
                will(returnValue(thirdNewPayload));
                exactly(1).of(thirdNewPayload).setContent(expectedSinglePayloadContent[2].getBytes());
                exactly(1).of(thirdNewPayload).getId();

                //Calls when creating first event
                one(event).spawnChild(moduleName, componentName, 0, firstNewPayload);
                will(returnValue(firstNewEvent));
//                exactly(1).of(firstNewEvent).getPayloads();
//                exactly(1).of(firstNewEvent).setPayload(firstNewPayload);
                exactly(1).of(firstNewEvent).getId();

                //Calls when creating second event
                one(event).spawnChild(moduleName, componentName, 1, secondNewPayload);
                will(returnValue(secondNewEvent));
//                exactly(1).of(secondNewEvent).getPayloads();
//                exactly(1).of(secondNewEvent).setPayload(secondNewPayload);
                exactly(1).of(secondNewEvent).getId();

                //Calls when creating third event
                one(event).spawnChild(moduleName, componentName, 2, thirdNewPayload);
                will(returnValue(thirdNewEvent));

                exactly(1).of(thirdNewEvent).getId();

            }
        } );
        List<Event> events = this.tokenSplitter.onEvent(event,moduleName,componentName);
        Assert.assertTrue(events.size() == NUM_EVENTS_OUT);
        Assert.assertEquals(firstNewEvent, events.get(0));
        Assert.assertEquals(secondNewEvent, events.get(1));
        Assert.assertEquals(thirdNewEvent, events.get(2));
    }

    /**
     * Test successful event(1):payload(1) with payload content
     * that does not contain the specified token, and therefore returning a list
     * of events with original incoming event unchanged.
     * This was to test path where the scanner is created with an encoding parameter.
     * @throws SequencerException Wrapper exception for CloneNotSupportedException.
     * @throws CloneNotSupportedException Thrown when cloning 
     *         <code>Event</code>/<code>Payload</code> 
     */
    @Test
    public void test_successfulTokenisingSinglePayloadTokenizedToSingleEvents()
        throws SequencerException, CloneNotSupportedException
    {
        final int NUM_EVENTS_OUT = 1; // one payload per event on the splitter

        // create the class to be tested with 
        // delimiter (regular) expression and null encoding
        this.tokenSplitter = new TokenizingSplitter("token", "UTF-8");

        /** Real payload list */
        this.payloads = new ArrayList<Payload>();
        /** Expected three payloads */
        final Payload firstNewPayload = this.classMockery.mock(Payload.class);
        
        final Event firstNewEvent = this.classMockery.mock(Event.class);

        // Populate two payload entries
        payloads.add(payload);

        /** Real content - from the mocked payload */
        final String payloadContentStr = 
            new String("A, you're adorable$"
                     + "B, you're so beautiful$"
                     + "C, you have some cutiful charms!");

        classMockery.checking(new Expectations()
        {
            {
                //Calls expected during logging.
                exactly(1).of(event).getId();
                exactly(1).of(event).idToString();
                exactly(NUM_EVENTS_OUT).of(payload).getId();

                //Calls expected on incoming event; will be called once only
                exactly(1).of(event).getPayloads();
                will(returnValue(payloads));

                //Calls expected on event's payload; here the original event has only one payload
                //and therefore these methods will be called once.
                exactly(1).of(payload).getContent();
                will(returnValue(payloadContentStr.getBytes()));

                //Calls when creating third payload
                exactly(1).of(payload).spawnChild(0);
                will(returnValue(firstNewPayload));
                exactly(1).of(firstNewPayload).setContent(payloadContentStr.getBytes());
                exactly(1).of(firstNewPayload).getId();

                //Calls when creating first event
                exactly(1).of(event).spawnChild(moduleName, componentName, 0, firstNewPayload);
                will(returnValue(firstNewEvent));
//                exactly(1).of(firstNewEvent).getPayloads();
//                exactly(1).of(firstNewEvent).setPayload(firstNewPayload);
                exactly(1).of(firstNewEvent).getId();
            } 
        } );
        List<Event> events = this.tokenSplitter.onEvent(event,moduleName,componentName);
        Assert.assertTrue(events.size() == NUM_EVENTS_OUT);
        Assert.assertEquals(firstNewEvent, events.get(0));
    }

    /**
     * Test successful event(1):payload(1) with no payload content.
     * This will return a list with original event and payload unchanged.
     * This was to test the path where returned tockens list was empty.
     * @throws SequencerException Wrapper exception for CloneNotSupportedException.
     * @throws CloneNotSupportedException Thrown when cloning 
     *         <code>Event</code>/<code>Payload</code> 
     */
    @Test
    public void test_successfulTokenisingSinglePayloadTokenizedSingleEvent()
    throws SequencerException, CloneNotSupportedException
    {
        final int NUM_EVENTS_OUT = 1; // one payload per event on the splitter

        // create the class to be tested with 
        // delimiter (regular) expression and null encoding
        this.tokenSplitter = new TokenizingSplitter("\\$");

        /** Real payload list */
        this.payloads = new ArrayList<Payload>();
        /** Expected three payloads */

        final Event firstNewEvent = this.classMockery.mock(Event.class);

        // Populate two payload entries
        payloads.add(payload);

        /** Real content - from the mocked payload */
        final String payloadContentStr = new String();
        /** Expected payload content after tokenizing. */

        classMockery.checking(new Expectations()
        {
            {
                //Calls expected during logging.
                exactly(1).of(event).getId();
                exactly(1).of(event).idToString();
                exactly(NUM_EVENTS_OUT).of(payload).getId();

                //Calls expected on incoming event; will be called once only
                exactly(1).of(event).getPayloads();
                will(returnValue(payloads));

                //Calls expected on event's payload; here the original event has only one payload
                //and therefore these methods will be called once.
                exactly(1).of(payload).getContent();
                will(returnValue(payloadContentStr.getBytes()));

                //Calls when creating first event
                one(event).spawnChild(moduleName, componentName, 0, payload);
                will(returnValue(firstNewEvent));
//                exactly(1).of(firstNewEvent).getPayloads();
//                exactly(1).of(firstNewEvent).setPayload(payload);
                exactly(1).of(firstNewEvent).getId();
            } 
        } );
        List<Event> events = this.tokenSplitter.onEvent(event,moduleName,componentName);
        Assert.assertTrue(events.size() == NUM_EVENTS_OUT);
        Assert.assertEquals(firstNewEvent, events.get(0));
    }





    /**
     * Teardown after each test
     */
    @After
    public void tearDown()
    {
        //Empty
    }

    /**
     * The suite is this class
     *
     * @return JUnit Test class
     */
    public static junit.framework.Test suite()
    {
        return new JUnit4TestAdapter(TokenizingSplitterTest.class);
    }
}
