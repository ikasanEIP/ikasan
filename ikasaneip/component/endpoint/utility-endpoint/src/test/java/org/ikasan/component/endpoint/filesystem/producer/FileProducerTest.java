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
package org.ikasan.component.endpoint.filesystem.producer;

import org.apache.commons.io.FileUtils;
import org.ikasan.spec.component.endpoint.EndpointException;
import org.ikasan.spec.flow.FlowEvent;
import org.ikasan.spec.management.ManagedResourceRecoveryManager;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.After;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


/**
 * Functional unit test cases for <code>FileProducer</code>.
 * 
 * @author Ikasan Development Team
 */
public class FileProducerTest
{
    /**
     * Mockery for mocking concrete classes
     */
    private final Mockery mockery = new Mockery()
    {
        {
            setImposteriser(ClassImposteriser.INSTANCE);
        }
    };

    private FileProducerConfiguration configuration = mockery.mock(FileProducerConfiguration.class);

    private FlowEvent flowEvent = mockery.mock(FlowEvent.class);

    private ManagedResourceRecoveryManager managedResourceRecoveryManager = mockery.mock(ManagedResourceRecoveryManager.class);

    final String filename = "src/test/resources/data/unit/Trade.txt";
    File testFile = new File(filename);
    final String tempFilename = "src/test/resources/data/unit/Trade.txt.tmp";
    File tempTestFile = new File(tempFilename);
    final String encoding = "UTF-8";

    /**
     * Test successful string to file write.
     */
    @Test
    public void test_successful_flowEventFileWrite()
    {

        // set test expectations
        mockery.checking(new Expectations() {
            {
                exactly(1).of(flowEvent).getPayload();
                will(returnValue("flow event payload"));
                exactly(1).of(configuration).getFilename();
                will(returnValue(filename));
                exactly(1).of(configuration).isUseTempFile();
                will(returnValue(false));
                exactly(1).of(configuration).getEncoding();
                will(returnValue(encoding));
                exactly(1).of(configuration).isWriteChecksum();
                will(returnValue(false));
            }
        });

        FileProducer fileProducer = new FileProducer();
        fileProducer.setConfiguration(configuration);
        fileProducer.invoke(flowEvent);
        mockery.assertIsSatisfied();
    }

    /**
     * Test successful string to file write.
     */
    @Test
    public void test_successful_stringFileWrite()
    {

        // set test expectations
        mockery.checking(new Expectations() {
            {
                exactly(1).of(configuration).getFilename();
                will(returnValue(filename));
                exactly(1).of(configuration).isUseTempFile();
                will(returnValue(false));
                exactly(1).of(configuration).getEncoding();
                will(returnValue(encoding));
                exactly(1).of(configuration).isWriteChecksum();
                will(returnValue(false));
            }
        });

        FileProducer fileProducer = new FileProducer();
        fileProducer.setConfiguration(configuration);
        fileProducer.invoke("text to write");
        mockery.assertIsSatisfied();
    }

    /**
     * Test successful string to file write.
     */
    @Test
    public void test_successful_stringFileWrite_with_temp_file()
    {

        // set test expectations
        mockery.checking(new Expectations() {
            {
                exactly(1).of(configuration).getFilename();
                will(returnValue(filename));
                exactly(1).of(configuration).isUseTempFile();
                will(returnValue(true));
                exactly(1).of(configuration).getTempFilename();
                will(returnValue(tempFilename));
                exactly(1).of(configuration).getEncoding();
                will(returnValue(encoding));
                exactly(1).of(configuration).isWriteChecksum();
                will(returnValue(false));
            }
        });

        FileProducer fileProducer = new FileProducer();
        fileProducer.setConfiguration(configuration);
        fileProducer.invoke("text to write");
        mockery.assertIsSatisfied();
    }

    /**
     * Test successful string to file write.
     */
    @Test
    public void test_successful_stringFileWrite_with_temp_file_with_checksum()
    {

        // set test expectations
        mockery.checking(new Expectations() {
            {
                exactly(2).of(configuration).getFilename();
                will(returnValue(filename));
                exactly(1).of(configuration).isUseTempFile();
                will(returnValue(true));
                exactly(1).of(configuration).getTempFilename();
                will(returnValue(tempFilename));
                exactly(2).of(configuration).getEncoding();
                will(returnValue(encoding));
                exactly(1).of(configuration).isWriteChecksum();
                will(returnValue(true));
            }
        });

        FileProducer fileProducer = new FileProducer();
        fileProducer.setConfiguration(configuration);
        fileProducer.invoke("text to write");
        mockery.assertIsSatisfied();
    }

    /**
     * Test successful string to file write.
     */
    @Test (expected = EndpointException.class)
    public void test_successful_stringFileWrite_with_temp_file_with_checksum_existing_no_overwrite() throws IOException
    {

        // set test expectations
        mockery.checking(new Expectations() {
            {
                exactly(2).of(configuration).isOverwrite();
                will(returnValue(false));
                exactly(3).of(configuration).getFilename();
                will(returnValue(filename));
                exactly(1).of(configuration).isUseTempFile();
                will(returnValue(true));
                exactly(1).of(configuration).getTempFilename();
                will(returnValue(tempFilename));
                exactly(2).of(configuration).getEncoding();
                will(returnValue(encoding));
                exactly(1).of(configuration).isWriteChecksum();
                will(returnValue(true));
            }
        });

        FileUtils.writeStringToFile(testFile, "test existing file");
        FileProducer fileProducer = new FileProducer();
        fileProducer.setConfiguration(configuration);
        fileProducer.invoke("text to write");
        mockery.assertIsSatisfied();
    }

    /**
     * Test successful string to file write.
     */
    @Test
    public void test_successful_stringFileWrite_with_temp_file_with_checksum_existing_with_overwrite() throws IOException
    {

        // set test expectations
        mockery.checking(new Expectations() {
            {
                exactly(1).of(configuration).isOverwrite();
                will(returnValue(true));
                exactly(2).of(configuration).getFilename();
                will(returnValue(filename));
                exactly(1).of(configuration).isUseTempFile();
                will(returnValue(true));
                exactly(1).of(configuration).getTempFilename();
                will(returnValue(tempFilename));
                exactly(2).of(configuration).getEncoding();
                will(returnValue(encoding));
                exactly(1).of(configuration).isWriteChecksum();
                will(returnValue(true));
            }
        });

        FileUtils.writeStringToFile(testFile, "test existing file");
        FileProducer fileProducer = new FileProducer();
        fileProducer.setConfiguration(configuration);
        fileProducer.invoke("text to write");
        mockery.assertIsSatisfied();
    }

    /**
     * Test successful bytes to file write.
     */
    @Test
    public void test_successful_bytesFileWrite()
    {

        // set test expectations
        mockery.checking(new Expectations() {
            {
                exactly(1).of(configuration).getFilename();
                will(returnValue(filename));
                exactly(1).of(configuration).isUseTempFile();
                will(returnValue(false));
                exactly(1).of(configuration).isWriteChecksum();
                will(returnValue(false));
            }
        });

        FileProducer fileProducer = new FileProducer();
        fileProducer.setConfiguration(configuration);
        fileProducer.invoke("text to write".getBytes());
        mockery.assertIsSatisfied();
    }

    /**
     * Test successful bytes to file write.
     */
    @Test
    public void test_successful_bytesFileWrite_with_temp_file()
    {

        // set test expectations
        mockery.checking(new Expectations() {
            {
                exactly(1).of(configuration).getFilename();
                will(returnValue(filename));
                exactly(1).of(configuration).isUseTempFile();
                will(returnValue(true));
                exactly(1).of(configuration).getTempFilename();
                will(returnValue(tempFilename));
                exactly(1).of(configuration).isWriteChecksum();
                will(returnValue(false));
            }
        });

        FileProducer fileProducer = new FileProducer();
        fileProducer.setConfiguration(configuration);
        fileProducer.invoke("text to write".getBytes());
        mockery.assertIsSatisfied();
    }

    /**
     * Test successful bytes to file write.
     */
    @Test
    public void test_successful_bytesFileWrite_with_temp_file_with_checksum()
    {

        // set test expectations
        mockery.checking(new Expectations() {
            {
                exactly(2).of(configuration).getFilename();
                will(returnValue(filename));
                exactly(1).of(configuration).isUseTempFile();
                will(returnValue(true));
                exactly(1).of(configuration).getTempFilename();
                will(returnValue(tempFilename));
                exactly(1).of(configuration).isWriteChecksum();
                will(returnValue(true));
                exactly(1).of(configuration).getEncoding();
                will(returnValue(encoding));
            }
        });

        FileProducer fileProducer = new FileProducer();
        fileProducer.setConfiguration(configuration);
        fileProducer.invoke("text to write".getBytes());
        mockery.assertIsSatisfied();
    }

    /**
     * Test successful collection to file write.
     */
    @Test
    public void test_successful_collectionFileWrite()
    {

        // set test expectations
        mockery.checking(new Expectations() {
            {
                exactly(1).of(configuration).getFilename();
                will(returnValue(filename));
                exactly(1).of(configuration).isUseTempFile();
                will(returnValue(false));
                exactly(1).of(configuration).isWriteChecksum();
                will(returnValue(false));
                exactly(1).of(configuration).getLineEnding();
                will(returnValue(null));
            }
        });

        FileProducer fileProducer = new FileProducer();
        fileProducer.setConfiguration(configuration);

        List<String> strings = new ArrayList<String>();
        strings.add("one");
        strings.add("two");
        strings.add("three");
        fileProducer.invoke(strings);
        mockery.assertIsSatisfied();
    }

    /**
     * Test successful collection to file write.
     */
    @Test
    public void test_successful_collectionFileWrite_with_temp_file()
    {

        // set test expectations
        mockery.checking(new Expectations() {
            {
                exactly(1).of(configuration).getFilename();
                will(returnValue(filename));
                exactly(1).of(configuration).isUseTempFile();
                will(returnValue(true));
                exactly(1).of(configuration).getTempFilename();
                will(returnValue(tempFilename));
                exactly(1).of(configuration).isWriteChecksum();
                will(returnValue(false));
                exactly(1).of(configuration).getLineEnding();
                will(returnValue(null));
            }
        });

        FileProducer fileProducer = new FileProducer();
        fileProducer.setConfiguration(configuration);

        List<String> strings = new ArrayList<String>();
        strings.add("one");
        strings.add("two");
        strings.add("three");
        fileProducer.invoke(strings);
        mockery.assertIsSatisfied();
    }

    /**
     * Test successful collection to file write.
     */
    @Test
    public void test_successful_collectionFileWrite_with_temp_file_with_checksum()
    {

        // set test expectations
        mockery.checking(new Expectations() {
            {
                exactly(2).of(configuration).getFilename();
                will(returnValue(filename));
                exactly(1).of(configuration).isUseTempFile();
                will(returnValue(true));
                exactly(1).of(configuration).getTempFilename();
                will(returnValue(tempFilename));
                exactly(1).of(configuration).isWriteChecksum();
                will(returnValue(true));
                exactly(1).of(configuration).getEncoding();
                will(returnValue(encoding));
                exactly(1).of(configuration).getLineEnding();
                will(returnValue(null));
            }
        });

        FileProducer fileProducer = new FileProducer();
        fileProducer.setConfiguration(configuration);

        List<String> strings = new ArrayList<String>();
        strings.add("one");
        strings.add("two");
        strings.add("three");
        fileProducer.invoke(strings);
        mockery.assertIsSatisfied();
    }

    /**
     * Test successful collection to file write.
     */
    @Test
    public void test_successful_collectionFileWrite_with_temp_file_with_checksum_with_lineEnding()
    {

        // set test expectations
        mockery.checking(new Expectations() {
            {
                exactly(2).of(configuration).getFilename();
                will(returnValue(filename));
                exactly(1).of(configuration).isUseTempFile();
                will(returnValue(true));
                exactly(1).of(configuration).getTempFilename();
                will(returnValue(tempFilename));
                exactly(1).of(configuration).isWriteChecksum();
                will(returnValue(true));
                exactly(1).of(configuration).getEncoding();
                will(returnValue(encoding));
                exactly(2).of(configuration).getLineEnding();
                will(returnValue("\n"));
            }
        });

        FileProducer fileProducer = new FileProducer();
        fileProducer.setConfiguration(configuration);

        List<String> strings = new ArrayList<String>();
        strings.add("one");
        strings.add("two");
        strings.add("three");
        fileProducer.invoke(strings);
        mockery.assertIsSatisfied();
    }

    @After
    public void teardown()
    {
        if(testFile.exists())
        {
            testFile.delete();
        }

        if(tempTestFile.exists())
        {
            tempTestFile.delete();
        }
    }
}