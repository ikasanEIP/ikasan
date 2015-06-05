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
package org.ikasan.endpoint.sftp.producer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.resource.ResourceException;
import javax.resource.cci.ConnectionFactory;

import org.ikasan.client.FileTransferConnectionTemplate;
import org.ikasan.filetransfer.Payload;
import org.ikasan.endpoint.sftp.producer.SftpProducerConfiguration;
import org.ikasan.spec.component.endpoint.EndpointException;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.test.util.ReflectionTestUtils;

/**
 * Test class for {@link SftpProducer}
 * 
 * @author Ikasan Development Team
 *
 */
@SuppressWarnings("unqualified-field-access")
public class SftpProducerTest
{
    /** The mockery */
    private final Mockery mockery = new Mockery()
    {
        {
            setImposteriser(ClassImposteriser.INSTANCE);
        }
    };


    /** Mock ftpConfiguration */
    private final SftpProducerConfiguration sftpConfiguration = this.mockery.mock(SftpProducerConfiguration.class, "mockSftpProducerConfiguration");

    private final ConnectionFactory connectionFactory = mockery.mock(ConnectionFactory.class, "mockConnectionFactory");

    private final FileTransferConnectionTemplate activeFileTransferConnectionTemplate = mockery.mock(FileTransferConnectionTemplate.class,"mockactiveFileTransferConnectionTemplate");

    private final FileTransferConnectionTemplate alternateFileTransferConnectionTemplate = mockery.mock(FileTransferConnectionTemplate.class,"mockalternateFileTransferConnectionTemplate");

    /** Object being tested */
    private SftpProducer uut = new SftpProducer(connectionFactory);

    /**
     * Test failed constructor due to null fileTransferConnectionTemplate.
     */    
    @Test(expected = IllegalArgumentException.class)
    public void test_failedConstructor_nullFileTransferConnectionTemplate()
    {
        new SftpProducer(null);
    }


    /**
     * Test successful invocation based on a single file.
     * @throws EndpointException if error invoking the endpoint
     */
    @Test public void test_successful_sftpPayloadProducer_invocation_single_file() throws ResourceException
    {
        uut.setConfiguration(sftpConfiguration);
        ReflectionTestUtils.setField(uut, "fileTransferConnectionTemplate", activeFileTransferConnectionTemplate);
        ReflectionTestUtils.setField(uut, "activeFileTransferConnectionTemplate", activeFileTransferConnectionTemplate);
        ReflectionTestUtils.setField(uut, "alternateFileTransferConnectionTemplate", alternateFileTransferConnectionTemplate);


        final Payload payload = mockery.mock(Payload.class, "mockPayload");

        // Expectations
        this.mockery.checking(new Expectations()
        {
            {
                exactly(1).of(sftpConfiguration).getOutputDirectory();
                will(returnValue("outputDirectory"));
                exactly(1).of(sftpConfiguration).getOverwrite();
                will(returnValue(Boolean.TRUE));
                exactly(1).of(sftpConfiguration).getRenameExtension();
                will(returnValue("rename"));
                exactly(1).of(sftpConfiguration).getChecksumDelivered();
                will(returnValue(Boolean.FALSE));
                exactly(1).of(sftpConfiguration).getUnzip();
                will(returnValue(Boolean.TRUE));
                exactly(1).of(sftpConfiguration).getCleanUpChunks();
                will(returnValue(Boolean.FALSE));
                exactly(1).of(activeFileTransferConnectionTemplate).deliverPayload(payload, "outputDirectory",
                    new HashMap<String, String>(), true, "rename", false, true, false);
            }
        });

        this.uut.invoke(payload);
        this.mockery.assertIsSatisfied();
    }

    /**
     * If no alternate connection details are provided, the producer will throw the exception and give up
     * @throws ResourceException if error invoking endpoint
     */
    @Test(expected=EndpointException.class)
    public void producer_fails() throws ResourceException
    {
        uut.setConfiguration(sftpConfiguration);
        ReflectionTestUtils.setField(uut, "fileTransferConnectionTemplate", activeFileTransferConnectionTemplate);
        ReflectionTestUtils.setField(uut, "activeFileTransferConnectionTemplate", activeFileTransferConnectionTemplate);
        ReflectionTestUtils.setField(uut, "alternateFileTransferConnectionTemplate", alternateFileTransferConnectionTemplate);

        final Payload payload = mockery.mock(Payload.class, "mockPayload");
        final ResourceException exception = new ResourceException(new RuntimeException("Something gone wrong"));

        // Expectations
        this.mockery.checking(new Expectations()
        {
            {
                one(sftpConfiguration).getOutputDirectory();
                will(returnValue("outputDirectory"));
                one(sftpConfiguration).getOverwrite();
                will(returnValue(Boolean.TRUE));
                one(sftpConfiguration).getRenameExtension();
                will(returnValue("rename"));
                one(sftpConfiguration).getChecksumDelivered();
                will(returnValue(Boolean.FALSE));
                one(sftpConfiguration).getUnzip();
                will(returnValue(Boolean.TRUE));
                one(sftpConfiguration).getCleanUpChunks();
                will(returnValue(Boolean.FALSE));
                one(activeFileTransferConnectionTemplate).deliverPayload(payload, "outputDirectory",
                    new HashMap<String, String>(), true, "rename", false, true, false);
                will(throwException(exception));
            }
        });

        try
        {  
            this.uut.invoke(payload);
        }
        catch (EndpointException e)
        {
            Assert.assertEquals(this.alternateFileTransferConnectionTemplate,
                    this.uut.getActiveFileTransferConnectionTemplate());
            throw e;
        }
        this.mockery.assertIsSatisfied();
        Assert.fail("Unreachable code.");
    }

    /**
     * When the producer is configured with an alternate connection template, on failure, producer will switch to use the
     * alternate next time it is invoked.
     * 
     * @throws EndpointException if error invoking endpoint
     */
    @Test(expected=EndpointException.class)
    public void producer_fails_changes_to_alternate_connection_template() throws ResourceException
    {
        uut.setConfiguration(sftpConfiguration);
        ReflectionTestUtils.setField(uut, "fileTransferConnectionTemplate", activeFileTransferConnectionTemplate);
        ReflectionTestUtils.setField(uut, "activeFileTransferConnectionTemplate", activeFileTransferConnectionTemplate);
        ReflectionTestUtils.setField(uut, "alternateFileTransferConnectionTemplate", alternateFileTransferConnectionTemplate);

        final Payload payload = mockery.mock(Payload.class, "mockPayload");

        final ResourceException exception = new ResourceException(new RuntimeException("Something gone wrong"));

        // Expectations
        this.mockery.checking(new Expectations()
        {
            {
                one(sftpConfiguration).getOutputDirectory();
                will(returnValue("outputDirectory"));
                one(sftpConfiguration).getOverwrite();
                will(returnValue(Boolean.TRUE));
                one(sftpConfiguration).getRenameExtension();
                will(returnValue("rename"));
                one(sftpConfiguration).getChecksumDelivered();
                will(returnValue(Boolean.FALSE));
                one(sftpConfiguration).getUnzip();
                will(returnValue(Boolean.TRUE));
                one(sftpConfiguration).getCleanUpChunks();
                will(returnValue(Boolean.FALSE));
                one(activeFileTransferConnectionTemplate).deliverPayload(payload, "outputDirectory",
                    new HashMap<String, String>(), true, "rename", false, true, false);
                will(throwException(exception));
            }
        });

        try
        {
            this.uut.invoke(payload);
        }
        catch (EndpointException e)
        {
            Assert.assertEquals(alternateFileTransferConnectionTemplate, this.uut.getActiveFileTransferConnectionTemplate());
            throw e;
        }
        this.mockery.assertIsSatisfied();
        Assert.fail("Unreachable code.");
    }

    /**
     * If an error occurs while producer using alternative connection template, switch back to original connection template
     * 
     * @throws EndpointException if error invoking endpoint
     */
    @Test(expected=EndpointException.class)
    public void producer_fails_changes_to_original_connection_template() throws ResourceException
    {
        uut.setConfiguration(sftpConfiguration);
        ReflectionTestUtils.setField(uut, "fileTransferConnectionTemplate", activeFileTransferConnectionTemplate);
        ReflectionTestUtils.setField(uut, "activeFileTransferConnectionTemplate", activeFileTransferConnectionTemplate);
        ReflectionTestUtils.setField(uut, "alternateFileTransferConnectionTemplate", alternateFileTransferConnectionTemplate);

        final Payload payload = mockery.mock(Payload.class, "mockPayload");

        final ResourceException exception = new ResourceException(new RuntimeException("Something gone wrong"));

        // Expectations
        this.mockery.checking(new Expectations()
        {
            {
                exactly(2).of(sftpConfiguration).getOutputDirectory();
                will(returnValue("outputDirectory"));
                exactly(2).of(sftpConfiguration).getOverwrite();
                will(returnValue(Boolean.TRUE));
                exactly(2).of(sftpConfiguration).getRenameExtension();
                will(returnValue("rename"));
                exactly(2).of(sftpConfiguration).getChecksumDelivered();
                will(returnValue(Boolean.FALSE));
                exactly(2).of(sftpConfiguration).getUnzip();
                will(returnValue(Boolean.TRUE));
                exactly(2).of(sftpConfiguration).getCleanUpChunks();
                will(returnValue(Boolean.FALSE));
                one(activeFileTransferConnectionTemplate).deliverPayload(payload, "outputDirectory",
                    new HashMap<String, String>(), true, "rename", false, true, false);
                will(throwException(exception));
                one(alternateFileTransferConnectionTemplate).deliverPayload(payload, "outputDirectory",
                    new HashMap<String, String>(), true, "rename", false, true, false);
                will(throwException(exception));
            }
        });

        try
        {
            this.uut.invoke(payload);        }
        catch (Exception e)
        {
            Assert.assertEquals(this.uut.getActiveFileTransferConnectionTemplate(), alternateFileTransferConnectionTemplate);
            try
            {
                this.uut.invoke(payload);
            }
            catch (Exception e2)
            {
                Assert.assertEquals( this.activeFileTransferConnectionTemplate,this.uut.getActiveFileTransferConnectionTemplate());
                throw e2;
            }
        }
        this.mockery.assertIsSatisfied();
        Assert.fail("Unreachable code.");
    }
}
