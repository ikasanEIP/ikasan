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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import junit.framework.Assert;

import org.ikasan.common.FilePayloadAttributeNames;
import org.ikasan.common.Payload;
import org.ikasan.common.component.DefaultPayload;
import org.ikasan.framework.component.Event;
import org.ikasan.spec.sequencing.SequencerException;
import org.junit.Test;

/**
 * This test class supports the {@link UncompressDataSplitter} class.
 * 
 * TODO testing exceptions for 100% coverage. Currently tests cover 97.5%
 * 
 * @author Ikasan Development Team
 */
public class UncompressDataSplitterTest
{
    /** Constant representing end-of-file is reached. */
    private static final int END_OF_FILE = -1;

	/**
	 * ModuleName
	 */
	private final String moduleName = "moduleName";
	
	/**
	 * ComponentName
	 */
	private final String componentName = "componentName";


    /** The splitter to be tested. */
    private UncompressDataSplitter splitter = new UncompressDataSplitter();



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
        final byte[] zippedFileData = this.loadFile("unzipTestZip.zip");
        final byte[] firstFileData = this.loadFile("secondTxt.txt");
        final String firstFileName = "second.txt";
        final byte[] secondFileData = this.loadFile("firstTxt.txt");
        final String secondFileName = "unziptest/first.txt";
        
        //create the original payload
        Payload payload = new DefaultPayload("incomingPayload",  zippedFileData);
        
        //create the original Event
        Event event = new Event("finCal", "finCal-calendarSrc","myEvent1",payload);

        //call the method under test
        List<Event> newEvents = splitter.onEvent(event, moduleName, componentName);
         
        //Assert that everything is in order on the resultant events
        Assert.assertTrue(newEvents.size() == 2);
        for (int i = 0; i < newEvents.size(); i++)
        {
            Assert.assertTrue("each new Event should only have 1 payload",newEvents.get(i).getPayloads().size() == 1);
        }
        //check the first event
        Assert.assertEquals(firstFileName, newEvents.get(0).getPayloads().get(0).getAttribute(FilePayloadAttributeNames.FILE_NAME));
        Assert.assertTrue(Arrays.equals(firstFileData, newEvents.get(0).getPayloads().get(0).getContent()));
        
        //check the second event
        Assert.assertEquals(secondFileName, newEvents.get(1).getPayloads().get(0).getAttribute(FilePayloadAttributeNames.FILE_NAME));
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
        final byte[] zippedFileData = this.loadFile("unzipTestZip.zip");// bb0.array();
        final byte[] firstFileData = this.loadFile("secondTxt.txt");
        final String firstFileName = "second.txt";
        final byte[] secondFileData = this.loadFile("firstTxt.txt");
        final String secondFileName = "unziptest/first.txt";
        
        //create the original payloads
        Payload firstOriginalPayload = new DefaultPayload("incomingPayload",  zippedFileData);
        Payload secondOriginalPayload = new DefaultPayload("incomingPayload", zippedFileData);
        List<Payload> payloads = new ArrayList<Payload>();
        payloads.add(firstOriginalPayload);
        payloads.add(secondOriginalPayload);
        
        //create the original event
        Event event = new Event("finCal", "finCal-calendarSrc","myEvent1",payloads);
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
        Assert.assertEquals(firstFileName, newEvents.get(0).getPayloads().get(0).getAttribute(FilePayloadAttributeNames.FILE_NAME));
        Assert.assertTrue(Arrays.equals(firstFileData, newEvents.get(0).getPayloads().get(0).getContent()));
        
        //check the second event/payload       
        Assert.assertEquals(secondFileName, newEvents.get(1).getPayloads().get(0).getAttribute(FilePayloadAttributeNames.FILE_NAME));
        Assert.assertTrue(Arrays.equals(secondFileData, newEvents.get(1).getPayloads().get(0).getContent()));
        
        //check the third event/payload
        Assert.assertEquals(firstFileName, newEvents.get(2).getPayloads().get(0).getAttribute(FilePayloadAttributeNames.FILE_NAME));
        Assert.assertTrue(Arrays.equals(firstFileData, newEvents.get(2).getPayloads().get(0).getContent()));
        
        //check the fourth event/payload
        Assert.assertEquals(secondFileName, newEvents.get(3).getPayloads().get(0).getAttribute(FilePayloadAttributeNames.FILE_NAME));
        Assert.assertTrue(Arrays.equals(secondFileData, newEvents.get(3).getPayloads().get(0).getContent()));
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

}
