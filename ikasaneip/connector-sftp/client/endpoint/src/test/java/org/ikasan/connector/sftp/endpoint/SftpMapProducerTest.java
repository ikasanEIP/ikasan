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
package org.ikasan.connector.sftp.endpoint;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import javax.resource.ResourceException;

import org.ikasan.client.FileTransferConnectionTemplate;
import org.ikasan.connector.sftp.configuration.SftpConfiguration;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Test;

/**
 * Test class for {@link SftpMapProducer}
 * 
 * @author Ikasan Development Team
 *
 */
public class SftpMapProducerTest
{
    Mockery mockery = new Mockery()
    {
        {
            setImposteriser(ClassImposteriser.INSTANCE);
        }
    };

    /** mock fileTransferConnectionTemplate */
    final FileTransferConnectionTemplate fileTransferConnectionTemplate = mockery.mock(FileTransferConnectionTemplate.class, "mockFileTransferConnectionTemplate");
    
    /** mock sftpConfiguration */
    final SftpConfiguration sftpConfiguration = mockery.mock(SftpConfiguration.class, "mockSftpConfiguration");

    /** mock filenameContentPairsMap */
    final Map<String,InputStream> filenameContentPairsMap = mockery.mock(Map.class, "mockFilenameContentPairsMap");

    /** mock map entry */
    final Map.Entry<String,InputStream> filenameContent = mockery.mock(Map.Entry.class, "mockFilenameContent");

    /** instance on test */
    SftpEndpointManager sftpEndpointManager;

    /**
     * Test failed constructor due to null fileTransferConnectionTemplate.
     */
    @Test(expected = IllegalArgumentException.class)
    public void test_failedConstructor_nullFileTransferConnectionTemplate()
    {
        new SftpMapProducer(null, null);
    }

    /**
     * Test failed constructor due to null sftpConfiguration.
     */
    @Test(expected = IllegalArgumentException.class)
    public void test_failedConstructor_nullSftpConfiguration()
    {
        new SftpMapProducer(fileTransferConnectionTemplate, null);
    }

    /**
     * Test successful invocation based on a single file.
     * @throws ResourceException 
     */
    @Test
    public void test_successful_sftpMapProducer_invocation_single_file() throws ResourceException
    {
        final ByteArrayInputStream content = new ByteArrayInputStream("content".getBytes());
        final Map<String,InputStream> filenameContentPairsMap = new HashMap<String,InputStream>();
        filenameContentPairsMap.put("filename", content);
        
        // expectations
        mockery.checking(new Expectations()
        {
            {
                exactly(1).of(sftpConfiguration).getOutputDirectory();
                will(returnValue("outputDirectory"));
                exactly(1).of(sftpConfiguration).getOverwrite();
                will(returnValue(Boolean.FALSE));
                exactly(1).of(sftpConfiguration).getRenameExtension();
                will(returnValue(".tmp"));
                exactly(1).of(sftpConfiguration).getChecksumDelivered();
                will(returnValue(Boolean.FALSE));
                exactly(1).of(sftpConfiguration).getUnzip();
                will(returnValue(Boolean.FALSE));
                
                exactly(1).of(fileTransferConnectionTemplate).deliverInputStream(content, "filename", "outputDirectory", Boolean.FALSE, ".tmp", Boolean.FALSE, Boolean.FALSE);
            }
        });

        SftpMapProducer sftpMapProducer = new SftpMapProducer(fileTransferConnectionTemplate, sftpConfiguration);
        sftpMapProducer.invoke(filenameContentPairsMap);
        mockery.assertIsSatisfied();
    }

    /**
     * Test successful invocation based on a multiple files.
     * @throws ResourceException 
     */
    @Test
    public void test_successful_sftpMapProducer_invocation_multiple_files() throws ResourceException
    {
        final ByteArrayInputStream content = new ByteArrayInputStream("content".getBytes());
        final Map<String,InputStream> filenameContentPairsMap = new HashMap<String,InputStream>();
        filenameContentPairsMap.put("filename1", content);
        filenameContentPairsMap.put("filename2", content);
        filenameContentPairsMap.put("filename3", content);
        
        // expectations
        mockery.checking(new Expectations()
        {
            {
                exactly(3).of(sftpConfiguration).getOutputDirectory();
                will(returnValue("outputDirectory"));
                exactly(3).of(sftpConfiguration).getOverwrite();
                will(returnValue(Boolean.FALSE));
                exactly(3).of(sftpConfiguration).getRenameExtension();
                will(returnValue(".tmp"));
                exactly(3).of(sftpConfiguration).getChecksumDelivered();
                will(returnValue(Boolean.FALSE));
                exactly(3).of(sftpConfiguration).getUnzip();
                will(returnValue(Boolean.FALSE));
                
                exactly(1).of(fileTransferConnectionTemplate).deliverInputStream(content, "filename1", "outputDirectory", Boolean.FALSE, ".tmp", Boolean.FALSE, Boolean.FALSE);
                exactly(1).of(fileTransferConnectionTemplate).deliverInputStream(content, "filename2", "outputDirectory", Boolean.FALSE, ".tmp", Boolean.FALSE, Boolean.FALSE);
                exactly(1).of(fileTransferConnectionTemplate).deliverInputStream(content, "filename3", "outputDirectory", Boolean.FALSE, ".tmp", Boolean.FALSE, Boolean.FALSE);
            }
        });

        SftpMapProducer sftpMapProducer = new SftpMapProducer(fileTransferConnectionTemplate, sftpConfiguration);
        sftpMapProducer.invoke(filenameContentPairsMap);
        mockery.assertIsSatisfied();
    }
}
