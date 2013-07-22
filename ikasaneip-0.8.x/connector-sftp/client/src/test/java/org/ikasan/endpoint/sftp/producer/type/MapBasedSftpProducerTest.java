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
package org.ikasan.endpoint.sftp.producer.type;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import javax.resource.ResourceException;

import org.ikasan.client.FileTransferConnectionTemplate;
import org.ikasan.endpoint.sftp.producer.SftpProducerConfiguration;
import org.ikasan.endpoint.sftp.producer.type.MapBasedSftpProducer;
import org.ikasan.spec.endpoint.Producer;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Assert;
import org.junit.Test;

/**
 * Test class for {@link MapBasedSftpProducer}
 * 
 * @author Ikasan Development Team
 *
 */
@SuppressWarnings("unqualified-field-access")
public class MapBasedSftpProducerTest
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
    private final SftpProducerConfiguration sftpConfiguration = this.mockery.mock(SftpProducerConfiguration.class, "mockSftpProducerConfiguration");

    /** Object being tested */
    private Producer<Map<String,InputStream>> sftpMapProducer = new MapBasedSftpProducer(this.fileTransferConnectionTemplate, this.sftpConfiguration);

    /**
     * Test failed constructor due to null fileTransferConnectionTemplate.
     */
    @SuppressWarnings("unused")
    @Test(expected = IllegalArgumentException.class)
    public void test_failedConstructor_nullFileTransferConnectionTemplate()
    {
        new MapBasedSftpProducer(null, null);
    }

    /**
     * Test failed constructor due to null ftpConfiguration.
     */
    @SuppressWarnings("unused")
    @Test(expected = IllegalArgumentException.class)
    public void test_failedConstructor_nullSftpConfiguration()
    {
        new MapBasedSftpProducer(this.fileTransferConnectionTemplate, null);
    }

    /**
     * Test successful invocation based on a single file.
     * @throws ResourceException if error invoking the endpoint
     */
    @Test public void test_successful_ssftpMapProducer_invocation_single_file() throws ResourceException
    {
        final ByteArrayInputStream content = new ByteArrayInputStream("content".getBytes());
        final Map<String,InputStream> filenameContentPairsMap = new HashMap<String,InputStream>();
        filenameContentPairsMap.put("filename", content);
        
        // Expectations
        this.mockery.checking(new Expectations()
        {
            {
                one(sftpConfiguration).getOutputDirectory(); will(returnValue("outputDirectory"));
                one(sftpConfiguration).getOverwrite();will(returnValue(Boolean.FALSE));
                one(sftpConfiguration).getRenameExtension(); will(returnValue(""));
                one(sftpConfiguration).getChecksumDelivered(); will(returnValue(Boolean.FALSE));
                one(sftpConfiguration).getUnzip(); will(returnValue(Boolean.FALSE));
                one(sftpConfiguration).getCreateParentDirectory(); will(returnValue(Boolean.FALSE));
                one(sftpConfiguration).getTempFileName();will(returnValue("file.tmp"));
                one(fileTransferConnectionTemplate).deliverInputStream(content, "filename", "outputDirectory",
                        false, "", false, false, false, "file.tmp");
            }
        });

        this.sftpMapProducer.invoke(filenameContentPairsMap);
        this.mockery.assertIsSatisfied();
    }

    /**
     * Test successful invocation based on a multiple files.
     * @throws ResourceException if error invoking endpoint
     */
    @Test public void test_successful_sftpMapProducer_invocation_multiple_files() throws ResourceException
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
                exactly(3).of(sftpConfiguration).getOutputDirectory(); will(returnValue("outputDirectory"));
                exactly(3).of(sftpConfiguration).getOverwrite(); will(returnValue(Boolean.FALSE));
                exactly(3).of(sftpConfiguration).getRenameExtension(); will(returnValue(".tmp"));
                exactly(3).of(sftpConfiguration).getChecksumDelivered();will(returnValue(Boolean.FALSE));
                exactly(3).of(sftpConfiguration).getUnzip(); will(returnValue(Boolean.FALSE));
                exactly(3).of(sftpConfiguration).getCreateParentDirectory(); will(returnValue(Boolean.FALSE));
                exactly(3).of(MapBasedSftpProducerTest.this.sftpConfiguration).getTempFileName();will(returnValue(null));

                one(fileTransferConnectionTemplate).deliverInputStream(content, "filename1", "outputDirectory", 
                        false, ".tmp", false, false, false, null);
                one(fileTransferConnectionTemplate).deliverInputStream(content, "filename2", "outputDirectory",
                        false, ".tmp", false, false, false, null);
                one(fileTransferConnectionTemplate).deliverInputStream(content, "filename3", "outputDirectory", 
                        false, ".tmp", false, false, false, null);
            }
        });

        this.sftpMapProducer.invoke(filenameContentPairsMap);
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
                one(sftpConfiguration).getOutputDirectory(); will(returnValue("outputDirectory"));
                one(sftpConfiguration).getOverwrite();will(returnValue(Boolean.FALSE));
                one(sftpConfiguration).getRenameExtension(); will(returnValue(""));
                one(sftpConfiguration).getChecksumDelivered(); will(returnValue(Boolean.FALSE));
                one(sftpConfiguration).getUnzip(); will(returnValue(Boolean.FALSE));
                one(sftpConfiguration).getCreateParentDirectory(); will(returnValue(Boolean.FALSE));
                one(sftpConfiguration).getTempFileName();will(returnValue("file.tmp"));
                one(fileTransferConnectionTemplate).deliverInputStream(content, "filename", "outputDirectory",
                        false, "", false, false, false, "file.tmp");will(throwException(exception));
            }
        });

        try
        {
            // TODO investigate why this is the only way to test this!
            this.sftpMapProducer.invoke(filenameContentPairsMap);
            
        }
        catch (ResourceException e)
        {
            Assert.assertEquals(this.fileTransferConnectionTemplate, ((MapBasedSftpProducer)this.sftpMapProducer).getActiveFileTransferConnectionTemplate());
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
                one(sftpConfiguration).getOutputDirectory(); will(returnValue("outputDirectory"));
                one(sftpConfiguration).getOverwrite();will(returnValue(Boolean.FALSE));
                one(sftpConfiguration).getRenameExtension(); will(returnValue(""));
                one(sftpConfiguration).getChecksumDelivered(); will(returnValue(Boolean.FALSE));
                one(sftpConfiguration).getUnzip(); will(returnValue(Boolean.FALSE));
                one(sftpConfiguration).getCreateParentDirectory(); will(returnValue(Boolean.FALSE));
                one(sftpConfiguration).getTempFileName();will(returnValue("file.tmp"));
                one(fileTransferConnectionTemplate).deliverInputStream(content, "filename", "outputDirectory",
                        false, "", false, false, false, "file.tmp");will(throwException(exception));
            }
        });

        ((MapBasedSftpProducer)this.sftpMapProducer).setAlternateFileTransferConnectionTemplate(mockAlternateConncetionTemplate);
        try
        {
            this.sftpMapProducer.invoke(filenameContentPairsMap);
        }
        catch (ResourceException e)
        {
            Assert.assertEquals(((MapBasedSftpProducer)this.sftpMapProducer).getActiveFileTransferConnectionTemplate(), mockAlternateConncetionTemplate);
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
                exactly(2).of(sftpConfiguration).getOutputDirectory(); will(returnValue("outputDirectory"));
                exactly(2).of(sftpConfiguration).getOverwrite();will(returnValue(Boolean.FALSE));
                exactly(2).of(sftpConfiguration).getRenameExtension(); will(returnValue(""));
                exactly(2).of(sftpConfiguration).getChecksumDelivered(); will(returnValue(Boolean.FALSE));
                exactly(2).of(sftpConfiguration).getUnzip(); will(returnValue(Boolean.FALSE));
                exactly(2).of(sftpConfiguration).getCreateParentDirectory(); will(returnValue(Boolean.FALSE));
                exactly(2).of(sftpConfiguration).getTempFileName();will(returnValue("file.tmp"));
                one(fileTransferConnectionTemplate).deliverInputStream(content, "filename", "outputDirectory",
                        false, "", false, false, false, "file.tmp");will(throwException(exception));
                one(mockAlternateConncetionTemplate).deliverInputStream(content, "filename", "outputDirectory",
                        false, "", false, false, false, "file.tmp");will(throwException(exception));
            }
        });

        ((MapBasedSftpProducer)this.sftpMapProducer).setAlternateFileTransferConnectionTemplate(mockAlternateConncetionTemplate);
        try
        {
            this.sftpMapProducer.invoke(filenameContentPairsMap);
        }
        catch (ResourceException e)
        {
            Assert.assertEquals(((MapBasedSftpProducer)this.sftpMapProducer).getActiveFileTransferConnectionTemplate(), mockAlternateConncetionTemplate);
            try
            {
                this.sftpMapProducer.invoke(filenameContentPairsMap);
            }
            catch (ResourceException e2)
            {
                Assert.assertEquals(((MapBasedSftpProducer)this.sftpMapProducer).getActiveFileTransferConnectionTemplate(), this.fileTransferConnectionTemplate);
                throw e2;
            }
        }
        this.mockery.assertIsSatisfied();
        Assert.fail("Unreachable code.");
    }
}
