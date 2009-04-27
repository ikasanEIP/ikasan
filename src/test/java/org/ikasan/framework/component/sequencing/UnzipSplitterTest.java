/*
 * $Id: UnzipSplitterTest.java 16808 2009-04-27 07:28:17Z mitcje $
 * $URL: svn+ssh://svc-vcsp/architecture/ikasan/trunk/framework/src/test/java/org/ikasan/framework/component/sequencing/UnzipSplitterTest.java $
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
package org.ikasan.framework.component.sequencing;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.ikasan.common.Payload;
import org.ikasan.common.component.DefaultPayload;
import org.ikasan.common.component.Spec;
import org.ikasan.framework.component.Event;
import org.ikasan.framework.component.sequencing.SequencerException;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import junit.framework.Assert;
import junit.framework.JUnit4TestAdapter;

/**
 * This test class supports the <code>UnzipSplitter</class> class.
 * 
 * TODO testing exceptions for 100% coverage. Currently tests cover 97.5%
 * 
 * @author Ikasan Development Team
 */
public class UnzipSplitterTest
{
    /** Constant representing end-of-file is reached. */
    private static final int END_OF_FILE = -1;

    /** Mockery for mocking classes. */
    private Mockery classMockery = new Mockery()
    {
        {
            setImposteriser(ClassImposteriser.INSTANCE);
        }
    };

    /** Incoming payload */
    private Payload payload;

    /** Incoming event */
    private Event event;

    /** The splitter to be tested. */
    private UnzipSplitter splitter;

    /**
     * Setup runs before each test
     */
    @Before
    public void setUp()
    {
        // Empty
    }

    /**
     * Successfully unzips an incoming event with one payload (zip file with containing two files), into two events,
     * each with one payload for every file.
     * 
     * @throws SequencerException Wrapper for all exceptions thrown within the splitter.
     * @throws IOException Thrown when error reading in test zipped files.
     */
    @Test
    public void test_successfullUnzippingIntoTwoPayloads() throws SequencerException, IOException
    {
        final byte[] zippedFileData = this.loadFile("unzipTestZip");
        final byte[] firstFileData = this.loadFile("secondTxt");
        final String firstFileName = "second.txt";
        final byte[] secondFileData = this.loadFile("firstTxt");
        final String secondFileName = "unziptest/first.txt";
        this.payload = new DefaultPayload("incomingPayload", Spec.BYTE_ZIP.toString(), "finCal-test", zippedFileData);
        this.event = new Event("finCal", "finCal-calendarSrc");
        this.event.setPayload(this.payload);
        this.splitter = new UnzipSplitter();
        List<Event> newEvents = this.splitter.onEvent(this.event);
        Assert.assertTrue(newEvents.size() == 2);
        for (int i = 0; i < newEvents.size(); i++)
        {
            Assert.assertTrue(newEvents.get(i).getPayloads().size() == 1);
        }
        Assert.assertEquals(firstFileName, newEvents.get(0).getPayloads().get(0).getName());
        Assert.assertEquals(firstFileName, newEvents.get(0).getName());
        Assert.assertTrue(Arrays.equals(firstFileData, newEvents.get(0).getPayloads().get(0).getContent()));
        Assert.assertEquals(secondFileName, newEvents.get(1).getPayloads().get(0).getName());
        Assert.assertEquals(secondFileName, newEvents.get(1).getName());
        Assert.assertTrue(Arrays.equals(secondFileData, newEvents.get(1).getPayloads().get(0).getContent()));
    }

    /**
     * Successfully unzips an incoming event with two payloads (each is a zip file containing two files), into four
     * events, each with one payload for every file.
     * 
     * @throws SequencerException Wrapper for all exceptions thrown within the splitter.
     * @throws IOException Thrown when error reading in test zipped files.
     */
    @Test
    public void test_successfullUnzippingIntoFourPayloads() throws SequencerException, IOException
    {
        final byte[] zippedFileData = this.loadFile("unzipTestZip");// bb0.array();
        final byte[] firstFileData = this.loadFile("secondTxt");
        final String firstFileName = "second.txt";
        final byte[] secondFileData = this.loadFile("firstTxt");
        final String secondFileName = "unziptest/first.txt";
        this.payload = new DefaultPayload("incomingPayload", Spec.BYTE_ZIP.toString(), "finCal-test", zippedFileData);
        Payload anotherPayload = new DefaultPayload("incomingPayload", Spec.BYTE_ZIP.toString(), "finCal-test",
            zippedFileData);
        this.event = new Event("finCal", "finCal-calendarSrc");
        this.event.setPayload(this.payload);
        this.event.setPayload(anotherPayload);
        this.splitter = new UnzipSplitter();
        List<Event> newEvents = this.splitter.onEvent(this.event);
        Assert.assertTrue(newEvents.size() == 4);
        for (int i = 0; i < newEvents.size(); i++)
        {
            Assert.assertTrue(newEvents.get(i).getPayloads().size() == 1);
        }
        Assert.assertEquals(firstFileName, newEvents.get(0).getPayloads().get(0).getName());
        Assert.assertEquals(firstFileName, newEvents.get(0).getName());
        Assert.assertTrue(Arrays.equals(firstFileData, newEvents.get(0).getPayloads().get(0).getContent()));
        Assert.assertEquals(secondFileName, newEvents.get(1).getPayloads().get(0).getName());
        Assert.assertEquals(secondFileName, newEvents.get(1).getName());
        Assert.assertTrue(Arrays.equals(secondFileData, newEvents.get(1).getPayloads().get(0).getContent()));
        Assert.assertEquals(firstFileName, newEvents.get(2).getPayloads().get(0).getName());
        Assert.assertEquals(firstFileName, newEvents.get(2).getName());
        Assert.assertTrue(Arrays.equals(firstFileData, newEvents.get(2).getPayloads().get(0).getContent()));
        Assert.assertEquals(secondFileName, newEvents.get(3).getPayloads().get(0).getName());
        Assert.assertEquals(secondFileName, newEvents.get(3).getName());
        Assert.assertTrue(Arrays.equals(secondFileData, newEvents.get(3).getPayloads().get(0).getContent()));
    }

    /**
     * Test unsuccessful incoming event with one payload split into one event with two payloads.
     * CloneNotSupportedException will be thrown when cloning first payload.
     * 
     * @throws CloneNotSupportedException Thrown when cloning <code>Payload</code>
     * @throws IOException Thrown when error reading in test zipped files
     */
    // cannot qualify mocked objects with 'this' within anonymous class
    @SuppressWarnings( { "unqualified-field-access", "synthetic-access" })
    @Test
    public void test_unsuccessfulUnzipSplitterPayloadCloningException() throws CloneNotSupportedException, IOException
    {
        final List<Payload> payloads = new ArrayList<Payload>();
        final CloneNotSupportedException payloadCloningException = new CloneNotSupportedException(
            "Exception while cloning payload.");
        final byte[] zippedFileData = this.loadFile("unzipTestZip");
        payload = this.classMockery.mock(Payload.class, "incomingPayload");
        event = this.classMockery.mock(Event.class, "incomingEvent");
        payloads.add(this.payload);
        this.splitter = new UnzipSplitter();
        this.classMockery.checking(new Expectations()
        {
            {
                exactly(1).of(event).idToString();
                exactly(1).of(event).getPayloads();
                will(returnValue(payloads));
                exactly(1).of(payload).getContent();
                will(returnValue(zippedFileData));
                exactly(1).of(payload).spawn();
                will(throwException(payloadCloningException));
            }
        });
        try
        {
            /**
             * This event list is never actually returned as a <code>CloningNotSupportedException</code> was thrown when
             * cloning exception.
             */
            @SuppressWarnings("unused")
            List<Event> events = this.splitter.onEvent(this.event);
            Assert.fail("Splitting fails due to CloningNotSupportedException being thrown when spawning payload.");
        }
        catch (SequencerException e)
        {
            Assert.assertEquals(payloadCloningException, e.getCause());
        }
    }

    /**
     * Test unsuccessful incoming event with one payload split into one event with two payloads. Clonning the two
     * payloads will be successful. CloneNotSupportedException will be thrown when cloning first event.
     * 
     * @throws CloneNotSupportedException Thrown when cloning <code>Event</code>
     * @throws IOException Thrown when error reading in test zipped files
     */
    // cannot qualify mocked objects with 'this' within anonymous class
    @SuppressWarnings( { "unqualified-field-access", "synthetic-access" })
    @Test
    public void test_unsuccessfulUnzipSplitterEventCloningException() throws CloneNotSupportedException, IOException
    {
        final List<Payload> payloads = new ArrayList<Payload>();
        final CloneNotSupportedException eventCloningException = new CloneNotSupportedException(
            "Exception while cloning event.");
        final byte[] zippedFileData = this.loadFile("unzipTestZip");// bb0.array();
        final byte[] firstFileData = this.loadFile("secondTxt");
        final String firstFileName = "second.txt";
        final byte[] secondFileData = this.loadFile("firstTxt");
        final String secondFileName = "unziptest/first.txt";
        this.payload = this.classMockery.mock(Payload.class, "incomingPayload");
        this.event = this.classMockery.mock(Event.class, "incomingEvent");
        payloads.add(this.payload);
        this.splitter = new UnzipSplitter();
        final Payload firstNewPayload = this.classMockery.mock(Payload.class, "firstNewPayload");
        final Payload secondNewPayload = this.classMockery.mock(Payload.class, "secondNewPayload");
        this.classMockery.checking(new Expectations()
        {
            {
                exactly(1).of(event).idToString();
                exactly(2).of(payload).getId();
                exactly(1).of(event).getPayloads();
                will(returnValue(payloads));
                exactly(1).of(payload).getContent();
                will(returnValue(zippedFileData));
                exactly(1).of(payload).spawn();
                will(returnValue(firstNewPayload));
                exactly(1).of(firstNewPayload).setContent(firstFileData);
                exactly(1).of(firstNewPayload).setName(firstFileName);
                exactly(1).of(firstNewPayload).getId();
                exactly(1).of(payload).spawn();
                will(returnValue(secondNewPayload));
                exactly(1).of(secondNewPayload).setContent(secondFileData);
                exactly(1).of(secondNewPayload).setName(secondFileName);
                exactly(1).of(secondNewPayload).getId();
                exactly(1).of(event).spawn();
                will(throwException(eventCloningException));
            }
        });
        try
        {
            /**
             * This event list is never actually returned as a <code>CloningNotSupportedException</code> was thrown when
             * cloning exception.
             */
            @SuppressWarnings("unused")
            List<Event> events = this.splitter.onEvent(this.event);
            Assert.fail("Splitting fails due to CloningNotSupportedException being thrown when spawning payload.");
        }
        catch (SequencerException e)
        {
            Assert.assertEquals(eventCloningException, e.getCause());
        }
    }

    /**
     * Load test files from classpath.
     * 
     * @param fileName The name of file to be loaded.
     * @return byte array representation of loaded file
     * @throws IOException Thrown if file could not be read.
     */
    private byte[] loadFile(String fileName) throws IOException
    {
        InputStream resourceAsStream = getClass().getClassLoader().getResourceAsStream(fileName);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        for (int c = resourceAsStream.read(); c != END_OF_FILE; c = resourceAsStream.read())
        {
            // Write each byte into the output stream.
            byteArrayOutputStream.write(c);
        }
        byte[] content = byteArrayOutputStream.toByteArray();
        return content;
    }

    /**
     * Teardown after each test
     */
    @After
    public void tearDown()
    {
        // Empty
    }

    /**
     * The suite is this class
     * 
     * @return JUnit Test class
     */
    public static junit.framework.Test suite()
    {
        return new JUnit4TestAdapter(UnzipSplitterTest.class);
    }
}
