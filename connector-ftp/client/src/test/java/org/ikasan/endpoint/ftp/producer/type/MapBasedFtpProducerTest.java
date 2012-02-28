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
package org.ikasan.endpoint.ftp.producer.type;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import javax.resource.ResourceException;

import org.ikasan.client.FileTransferConnectionTemplate;
import org.ikasan.endpoint.ftp.producer.FtpProducerConfiguration;
import org.ikasan.spec.endpoint.Producer;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Assert;
import org.junit.Test;

/**
 * Test class for {@link MapBasedFtpProducer}
 * 
 * @author Ikasan Development Team
 *
 */
@SuppressWarnings("unqualified-field-access")
public class MapBasedFtpProducerTest
{
    /** The mockery */
    private final Mockery mockery = new Mockery()
    {
        {
            setImposteriser(ClassImposteriser.INSTANCE);
        }
    };

    /** Mock fileTransferConnectionTemplate */
    private final FileTransferConnectionTemplate fileTransferConnectionTemplate = this.mockery.mock(FileTransferConnectionTemplate.class, "mockFileTransferConnectionTemplate");

    /** Mock ftpConfiguration */
    private final FtpProducerConfiguration ftpConfiguration = this.mockery.mock(FtpProducerConfiguration.class, "mockFtpProducerConfiguration");

    /** Object being tested */
    private Producer<Map<String,InputStream>> ftpMapProducer = new MapBasedFtpProducer(this.fileTransferConnectionTemplate, this.ftpConfiguration);

    /**
     * Test failed constructor due to null fileTransferConnectionTemplate.
     */
    @SuppressWarnings("unused")
    @Test(expected = IllegalArgumentException.class)
    public void test_failedConstructor_nullFileTransferConnectionTemplate()
    {
        new MapBasedFtpProducer(null, null);
    }

    /**
     * Test failed constructor due to null ftpConfiguration.
     */
    @SuppressWarnings("unused")
    @Test(expected = IllegalArgumentException.class)
    public void test_failedConstructor_nullFtpConfiguration()
    {
        new MapBasedFtpProducer(this.fileTransferConnectionTemplate, null);
    }

    /**
     * Test successful invocation based on a single file.
     * @throws ResourceException if error invoking the endpoint
     */
    @Test public void test_successful_ftpMapProducer_invocation_single_file() throws ResourceException
    {
        final ByteArrayInputStream content = new ByteArrayInputStream("content".getBytes());
        final Map<String,InputStream> filenameContentPairsMap = new HashMap<String,InputStream>();
        filenameContentPairsMap.put("filename", content);
        
        // Expectations
        this.mockery.checking(new Expectations()
        {
            {
                one(ftpConfiguration).getOutputDirectory(); will(returnValue("outputDirectory"));
                one(ftpConfiguration).getOverwrite();will(returnValue(Boolean.FALSE));
                one(ftpConfiguration).getRenameExtension(); will(returnValue(""));
                one(ftpConfiguration).getChecksumDelivered(); will(returnValue(Boolean.FALSE));
                one(ftpConfiguration).getUnzip(); will(returnValue(Boolean.FALSE));
                one(ftpConfiguration).getCreateParentDirectory(); will(returnValue(Boolean.FALSE));
                one(ftpConfiguration).getTempFileName();will(returnValue("file.tmp"));
                one(fileTransferConnectionTemplate).deliverInputStream(content, "filename", "outputDirectory",
                        false, "", false, false, false, "file.tmp");
            }
        });

        this.ftpMapProducer.invoke(filenameContentPairsMap);
        this.mockery.assertIsSatisfied();
    }

    /**
     * Test successful invocation based on a multiple files.
     * @throws ResourceException if error invoking endpoint
     */
    @Test public void test_successful_ftpMapProducer_invocation_multiple_files() throws ResourceException
    {
        final ByteArrayInputStream content = new ByteArrayInputStream("content".getBytes());
        final Map<String,InputStream> filenameContentPairsMap = new HashMap<String,InputStream>();
        filenameContentPairsMap.put("filename1", content);
        filenameContentPairsMap.put("filename2", content);
        filenameContentPairsMap.put("filename3", content);
        
        // Expectations
        this.mockery.checking(new Expectations()
        {
            {
                exactly(3).of(ftpConfiguration).getOutputDirectory(); will(returnValue("outputDirectory"));
                exactly(3).of(ftpConfiguration).getOverwrite(); will(returnValue(Boolean.FALSE));
                exactly(3).of(ftpConfiguration).getRenameExtension(); will(returnValue(".tmp"));
                exactly(3).of(ftpConfiguration).getChecksumDelivered();will(returnValue(Boolean.FALSE));
                exactly(3).of(ftpConfiguration).getUnzip(); will(returnValue(Boolean.FALSE));
                exactly(3).of(ftpConfiguration).getCreateParentDirectory(); will(returnValue(Boolean.FALSE));
                exactly(3).of(MapBasedFtpProducerTest.this.ftpConfiguration).getTempFileName();will(returnValue(null));

                one(fileTransferConnectionTemplate).deliverInputStream(content, "filename1", "outputDirectory", 
                        false, ".tmp", false, false, false, null);
                one(fileTransferConnectionTemplate).deliverInputStream(content, "filename2", "outputDirectory",
                        false, ".tmp", false, false, false, null);
                one(fileTransferConnectionTemplate).deliverInputStream(content, "filename3", "outputDirectory", 
                        false, ".tmp", false, false, false, null);
            }
        });

        this.ftpMapProducer.invoke(filenameContentPairsMap);
        this.mockery.assertIsSatisfied();
    }

    /**
     * If no alternate connection details are provided, the producer will throw the exception and give up
     * @throws ResourceException if error invoking endpoint
     */
    @Test(expected=ResourceException.class)
    public void producer_fails() throws ResourceException
    {
        final ByteArrayInputStream content = new ByteArrayInputStream("content".getBytes());
        final Map<String,InputStream> filenameContentPairsMap = new HashMap<String,InputStream>();
        filenameContentPairsMap.put("filename", content);
        final ResourceException exception = new ResourceException(new RuntimeException("Something gone wrong"));

        // Expectations
        this.mockery.checking(new Expectations()
        {
            {
                one(ftpConfiguration).getOutputDirectory(); will(returnValue("outputDirectory"));
                one(ftpConfiguration).getOverwrite();will(returnValue(Boolean.FALSE));
                one(ftpConfiguration).getRenameExtension(); will(returnValue(""));
                one(ftpConfiguration).getChecksumDelivered(); will(returnValue(Boolean.FALSE));
                one(ftpConfiguration).getUnzip(); will(returnValue(Boolean.FALSE));
                one(ftpConfiguration).getCreateParentDirectory(); will(returnValue(Boolean.FALSE));
                one(ftpConfiguration).getTempFileName();will(returnValue("file.tmp"));
                one(fileTransferConnectionTemplate).deliverInputStream(content, "filename", "outputDirectory",
                        false, "", false, false, false, "file.tmp");will(throwException(exception));
            }
        });

        try
        {
            // TODO investigate why this is the only way to test this!
            this.ftpMapProducer.invoke(filenameContentPairsMap);
            
        }
        catch (ResourceException e)
        {
            Assert.assertEquals(this.fileTransferConnectionTemplate, ((MapBasedFtpProducer)this.ftpMapProducer).getActiveFileTransferConnectionTemplate());
            throw e;
        }
        this.mockery.assertIsSatisfied();
        Assert.fail("Unreachable code.");
    }

    /**
     * When the producer is configured with an alternate connection template, on failure, producer will switch to use the
     * alternate next time it is invoked.
     * 
     * @throws ResourceException if error invoking endpoint
     */
    @Test(expected=ResourceException.class)
    public void producer_fails_changes_to_alternate_connection_template() throws ResourceException
    {
        final ByteArrayInputStream content = new ByteArrayInputStream("content".getBytes());
        final Map<String,InputStream> filenameContentPairsMap = new HashMap<String,InputStream>();
        filenameContentPairsMap.put("filename", content);
        final ResourceException exception = new ResourceException(new RuntimeException("Something gone wrong"));
        final FileTransferConnectionTemplate mockAlternateConncetionTemplate = this.mockery.mock(FileTransferConnectionTemplate.class, "alternateConnectionTemplate");

        // Expectations
        this.mockery.checking(new Expectations()
        {
            {
                one(ftpConfiguration).getOutputDirectory(); will(returnValue("outputDirectory"));
                one(ftpConfiguration).getOverwrite();will(returnValue(Boolean.FALSE));
                one(ftpConfiguration).getRenameExtension(); will(returnValue(""));
                one(ftpConfiguration).getChecksumDelivered(); will(returnValue(Boolean.FALSE));
                one(ftpConfiguration).getUnzip(); will(returnValue(Boolean.FALSE));
                one(ftpConfiguration).getCreateParentDirectory(); will(returnValue(Boolean.FALSE));
                one(ftpConfiguration).getTempFileName();will(returnValue("file.tmp"));
                one(fileTransferConnectionTemplate).deliverInputStream(content, "filename", "outputDirectory",
                        false, "", false, false, false, "file.tmp");will(throwException(exception));
            }
        });

        ((MapBasedFtpProducer)this.ftpMapProducer).setAlternateFileTransferConnectionTemplate(mockAlternateConncetionTemplate);
        try
        {
            this.ftpMapProducer.invoke(filenameContentPairsMap);
        }
        catch (ResourceException e)
        {
            Assert.assertEquals(((MapBasedFtpProducer)this.ftpMapProducer).getActiveFileTransferConnectionTemplate(), mockAlternateConncetionTemplate);
            throw e;
        }
        this.mockery.assertIsSatisfied();
        Assert.fail("Unreachable code.");
    }

    /**
     * If an error occurs while producer using alternative connection template, switch back to original connection template
     * 
     * @throws ResourceException if error invoking endpoint
     */
    @Test(expected=ResourceException.class)
    public void producer_fails_changes_to_original_connection_template() throws ResourceException
    {
        final ByteArrayInputStream content = new ByteArrayInputStream("content".getBytes());
        final Map<String,InputStream> filenameContentPairsMap = new HashMap<String,InputStream>();
        filenameContentPairsMap.put("filename", content);
        final ResourceException exception = new ResourceException(new RuntimeException("Something gone wrong"));
        final FileTransferConnectionTemplate mockAlternateConncetionTemplate = this.mockery.mock(FileTransferConnectionTemplate.class, "alternateConnectionTemplate");

        // Expectations
        this.mockery.checking(new Expectations()
        {
            {
                exactly(2).of(ftpConfiguration).getOutputDirectory(); will(returnValue("outputDirectory"));
                exactly(2).of(ftpConfiguration).getOverwrite();will(returnValue(Boolean.FALSE));
                exactly(2).of(ftpConfiguration).getRenameExtension(); will(returnValue(""));
                exactly(2).of(ftpConfiguration).getChecksumDelivered(); will(returnValue(Boolean.FALSE));
                exactly(2).of(ftpConfiguration).getUnzip(); will(returnValue(Boolean.FALSE));
                exactly(2).of(ftpConfiguration).getCreateParentDirectory(); will(returnValue(Boolean.FALSE));
                exactly(2).of(ftpConfiguration).getTempFileName();will(returnValue("file.tmp"));
                one(fileTransferConnectionTemplate).deliverInputStream(content, "filename", "outputDirectory",
                        false, "", false, false, false, "file.tmp");will(throwException(exception));
                one(mockAlternateConncetionTemplate).deliverInputStream(content, "filename", "outputDirectory",
                        false, "", false, false, false, "file.tmp");will(throwException(exception));
            }
        });

        ((MapBasedFtpProducer)this.ftpMapProducer).setAlternateFileTransferConnectionTemplate(mockAlternateConncetionTemplate);
        try
        {
            this.ftpMapProducer.invoke(filenameContentPairsMap);
        }
        catch (ResourceException e)
        {
            Assert.assertEquals(((MapBasedFtpProducer)this.ftpMapProducer).getActiveFileTransferConnectionTemplate(), mockAlternateConncetionTemplate);
            try
            {
                this.ftpMapProducer.invoke(filenameContentPairsMap);
            }
            catch (ResourceException e2)
            {
                Assert.assertEquals(((MapBasedFtpProducer)this.ftpMapProducer).getActiveFileTransferConnectionTemplate(), this.fileTransferConnectionTemplate);
                throw e2;
            }
        }
        this.mockery.assertIsSatisfied();
        Assert.fail("Unreachable code.");
    }
}
