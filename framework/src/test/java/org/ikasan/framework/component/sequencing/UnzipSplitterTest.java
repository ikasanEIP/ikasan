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

	/**
	 * ModuleName
	 */
	private final String moduleName = "moduleName";
	
	/**
	 * ComponentName
	 */
	private final String componentName = "componentName";


    /** The splitter to be tested. */
    private UnzipSplitter splitter = new UnzipSplitter();

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
        
        //create the original payload
        Payload payload = new DefaultPayload("incomingPayload", Spec.BYTE_ZIP.toString(), "finCal-test", zippedFileData);
        
        //create the original Event
        Event event = new Event("finCal", "finCal-calendarSrc",null,payload);

        //call the method under test
        List<Event> newEvents = splitter.onEvent(event, moduleName, componentName);
         
        //Assert that everything is in order on the resultant events
        Assert.assertTrue(newEvents.size() == 2);
        for (int i = 0; i < newEvents.size(); i++)
        {
            Assert.assertTrue("each new Event should only have 1 payload",newEvents.get(i).getPayloads().size() == 1);
        }
        //check the first event
        Assert.assertEquals(firstFileName, newEvents.get(0).getPayloads().get(0).getName());
        Assert.assertEquals(firstFileName, newEvents.get(0).getName());
        Assert.assertTrue(Arrays.equals(firstFileData, newEvents.get(0).getPayloads().get(0).getContent()));
        
        //check the second event
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
        
        //create the original payloads
        Payload firstOriginalPayload = new DefaultPayload("incomingPayload", Spec.BYTE_ZIP.toString(), "finCal-test", zippedFileData);
        Payload secondOriginalPayload = new DefaultPayload("incomingPayload", Spec.BYTE_ZIP.toString(), "finCal-test",
            zippedFileData);
        List<Payload> payloads = new ArrayList<Payload>();
        payloads.add(firstOriginalPayload);
        payloads.add(secondOriginalPayload);
        
        //create the original event
        Event event = new Event("finCal", "finCal-calendarSrc",null,payloads);
//        event.setPayload(this.payload);
//        event.setPayload(secondOriginalPayload);

        //call the method under test
        List<Event> newEvents = splitter.onEvent(event, moduleName, componentName);
        
        //Assert that everything is in order on the resultant events
        Assert.assertTrue(newEvents.size() == 4);
        for (int i = 0; i < newEvents.size(); i++)
        {
            Assert.assertTrue("each new Event should only have 1 payload",newEvents.get(i).getPayloads().size() == 1);
        }
        
        //check the first event/payload
        Assert.assertEquals(firstFileName, newEvents.get(0).getPayloads().get(0).getName());
        Assert.assertEquals(firstFileName, newEvents.get(0).getName());
        Assert.assertTrue(Arrays.equals(firstFileData, newEvents.get(0).getPayloads().get(0).getContent()));
        
        //check the second event/payload       
        Assert.assertEquals(secondFileName, newEvents.get(1).getPayloads().get(0).getName());
        Assert.assertEquals(secondFileName, newEvents.get(1).getName());
        Assert.assertTrue(Arrays.equals(secondFileData, newEvents.get(1).getPayloads().get(0).getContent()));
        
        //check the third event/payload
        Assert.assertEquals(firstFileName, newEvents.get(2).getPayloads().get(0).getName());
        Assert.assertEquals(firstFileName, newEvents.get(2).getName());
        Assert.assertTrue(Arrays.equals(firstFileData, newEvents.get(2).getPayloads().get(0).getContent()));
        
        //check the fourth event/payload
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
        final Payload payload = classMockery.mock(Payload.class, "incomingPayload");
        final Event event = classMockery.mock(Event.class, "incomingEvent");
        payloads.add(payload);

        
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
            List<Event> events = splitter.onEvent(event, moduleName, componentName);
            Assert.fail("Splitting fails due to CloningNotSupportedException being thrown when spawning payload.");
        }
        catch (SequencerException e)
        {
            Assert.assertEquals(payloadCloningException, e.getCause());
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
