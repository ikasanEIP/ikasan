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
package org.ikasan.connector.sftp.consumer;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import javax.resource.ResourceException;

import org.ikasan.client.FileTransferConnectionTemplate;
import org.ikasan.common.Payload;
import org.ikasan.framework.payload.service.FileTransferPayloadProvider;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Assert;
import org.junit.Test;

/**
 * Test class for {@link SftpPayloadConsumer}
 * 
 * @author Ikasan Development Team
 *
 */
public class SftpPayloadConsumerTest
{
    Mockery mockery = new Mockery()
    {
        {
            setImposteriser(ClassImposteriser.INSTANCE);
        }
    };

    /** mock fileTransferPayloadProvider */
    final FileTransferPayloadProvider fileTransferPayloadProvider = mockery.mock(FileTransferPayloadProvider.class, "mockFileTransferPayloadProvider");
    
    /** mock fileTransferConnectionTemplate */
    final FileTransferConnectionTemplate fileTransferConnectionTemplate = mockery.mock(FileTransferConnectionTemplate.class, "mockFileTransferConnectionTemplate");

    /** mock sftpConfiguration */
    final SftpConsumerConfiguration sftpConfiguration = mockery.mock(SftpConsumerConfiguration.class, "mockSftpConsumerConfiguration");

    /** mock filenameContentPairsMap */
    final Map<String,InputStream> filenameContentPairsMap = mockery.mock(Map.class, "mockFilenameContentPairsMap");

    /** mock map entry */
    final Map.Entry<String,InputStream> filenameContent = mockery.mock(Map.Entry.class, "mockFilenameContent");

    /** mock payload */
    final Payload payload = mockery.mock(Payload.class, "mockPayload");

    /**
     * Test failed constructor due to null fileTransferConnectionTemplate.
     */
    @Test(expected = IllegalArgumentException.class)
    public void test_failedConstructor_nullFileTransferPayloadProvider()
    {
        new SftpPayloadConsumer(null, null);
    }

    /**
     * Test failed constructor due to null sftpConfiguration.
     */
    @Test(expected = IllegalArgumentException.class)
    public void test_failedConstructor_nullSftpConfiguration()
    {
        new SftpMapConsumer(fileTransferPayloadProvider, null);
    }

    /**
     * Test successful invocation based on no discovered files.
     * @throws ResourceException 
     * @throws IOException 
     */
    @Test
    public void test_successful_sftpMapConsumer_invocation_no_discovered_file() throws ResourceException, IOException
    {
        final ByteArrayInputStream content = new ByteArrayInputStream("content".getBytes());
        final Map<String,InputStream> filenameContentPairsMap = new HashMap<String,InputStream>();
        filenameContentPairsMap.put("filename", content);
        
        // expectations
        mockery.checking(new Expectations()
        {
            {
                exactly(1).of(sftpConfiguration).getSourceDirectory();
                will(returnValue("sourceDirectory"));
                exactly(1).of(sftpConfiguration).getFilenamePattern();
                will(returnValue("filenamePattern"));
                exactly(1).of(sftpConfiguration).getRenameOnSuccess();
                will(returnValue(Boolean.TRUE));
                exactly(1).of(sftpConfiguration).getRenameOnSuccessExtension();
                will(returnValue("renameExtention"));
                exactly(1).of(sftpConfiguration).getMoveOnSuccess();
                will(returnValue(Boolean.FALSE));
                exactly(1).of(sftpConfiguration).getMoveOnSuccessNewPath();
                will(returnValue("moveOnSuccessNewPath"));
                exactly(1).of(sftpConfiguration).getChunking();
                will(returnValue(Boolean.FALSE));
                exactly(1).of(sftpConfiguration).getChunkSize();
                will(returnValue(Integer.valueOf(-1)));
                exactly(1).of(sftpConfiguration).getChecksum();
                will(returnValue(Boolean.FALSE));
                exactly(1).of(sftpConfiguration).getMinAge();
                will(returnValue(Long.valueOf(1)));
                exactly(1).of(sftpConfiguration).getDestructive();
                will(returnValue(Boolean.TRUE));
                exactly(1).of(sftpConfiguration).getFilterDuplicates();
                will(returnValue(Boolean.TRUE));
                exactly(1).of(sftpConfiguration).getFilterOnFilename();
                will(returnValue(Boolean.TRUE));
                exactly(1).of(sftpConfiguration).getFilterOnLastModifiedDate();
                will(returnValue(Boolean.TRUE));
                exactly(1).of(sftpConfiguration).getChronological();
                will(returnValue(Boolean.TRUE));

                exactly(1).of(fileTransferPayloadProvider).getFileTransferConnectionTemplate();
                will(returnValue(fileTransferConnectionTemplate));
                exactly(1).of(fileTransferConnectionTemplate).getDiscoveredFile("sourceDirectory", "filenamePattern", 
                    Boolean.TRUE, "renameExtention", Boolean.FALSE, "moveOnSuccessNewPath", Boolean.FALSE, Integer.valueOf(-1), 
                    Boolean.FALSE, Long.valueOf(1), Boolean.TRUE, Boolean.TRUE, Boolean.TRUE, Boolean.TRUE, Boolean.TRUE);
                will(returnValue(null));
            }
        });

        SftpMapConsumer sftpMapConsumer = new SftpMapConsumer(fileTransferPayloadProvider, sftpConfiguration);
        Assert.assertNull(sftpMapConsumer.invoke());
        mockery.assertIsSatisfied();
    }

    /**
     * Test successful invocation based on a discovered file.
     * @throws ResourceException 
     * @throws IOException 
     */
    @Test
    public void test_successful_sftpMapConsumer_invocation_discovering_a_file() throws ResourceException, IOException
    {
        final ByteArrayInputStream content = new ByteArrayInputStream("content".getBytes());
        final Map<String,InputStream> filenameContentPairsMap = new HashMap<String,InputStream>();
        filenameContentPairsMap.put("filename", content);
        
        // expectations
        mockery.checking(new Expectations()
        {
            {
                exactly(1).of(sftpConfiguration).getSourceDirectory();
                will(returnValue("sourceDirectory"));
                exactly(1).of(sftpConfiguration).getFilenamePattern();
                will(returnValue("filenamePattern"));
                exactly(1).of(sftpConfiguration).getRenameOnSuccess();
                will(returnValue(Boolean.TRUE));
                exactly(1).of(sftpConfiguration).getRenameOnSuccessExtension();
                will(returnValue("renameExtention"));
                exactly(1).of(sftpConfiguration).getMoveOnSuccess();
                will(returnValue(Boolean.FALSE));
                exactly(1).of(sftpConfiguration).getMoveOnSuccessNewPath();
                will(returnValue("moveOnSuccessNewPath"));
                exactly(1).of(sftpConfiguration).getChunking();
                will(returnValue(Boolean.FALSE));
                exactly(1).of(sftpConfiguration).getChunkSize();
                will(returnValue(Integer.valueOf(-1)));
                exactly(1).of(sftpConfiguration).getChecksum();
                will(returnValue(Boolean.FALSE));
                exactly(1).of(sftpConfiguration).getMinAge();
                will(returnValue(Long.valueOf(1)));
                exactly(1).of(sftpConfiguration).getDestructive();
                will(returnValue(Boolean.TRUE));
                exactly(1).of(sftpConfiguration).getFilterDuplicates();
                will(returnValue(Boolean.TRUE));
                exactly(1).of(sftpConfiguration).getFilterOnFilename();
                will(returnValue(Boolean.TRUE));
                exactly(1).of(sftpConfiguration).getFilterOnLastModifiedDate();
                will(returnValue(Boolean.TRUE));
                exactly(1).of(sftpConfiguration).getChronological();
                will(returnValue(Boolean.TRUE));

                exactly(1).of(fileTransferPayloadProvider).getFileTransferConnectionTemplate();
                will(returnValue(fileTransferConnectionTemplate));
                exactly(1).of(fileTransferConnectionTemplate).getDiscoveredFile("sourceDirectory", "filenamePattern", 
                    Boolean.TRUE, "renameExtention", Boolean.FALSE, "moveOnSuccessNewPath", Boolean.FALSE, Integer.valueOf(-1), 
                    Boolean.FALSE, Long.valueOf(1), Boolean.TRUE, Boolean.TRUE, Boolean.TRUE, Boolean.TRUE, Boolean.TRUE);
                will(returnValue(payload));
                
                exactly(1).of(payload).getId();
                will(returnValue("filename"));
                exactly(1).of(payload).getContent();
                will(returnValue("fileContents".getBytes()));
            }
        });

        SftpMapConsumer sftpMapConsumer = new SftpMapConsumer(fileTransferPayloadProvider, sftpConfiguration);
        Map<String,InputStream> discoveredFiles = sftpMapConsumer.invoke();
        Assert.assertTrue(discoveredFiles.size() == 1);
        for(Map.Entry<String,InputStream> discoveredFile : discoveredFiles.entrySet())
        {
            Assert.assertTrue("filename".equals(discoveredFile.getKey()));
            ByteArrayInputStream bais = (ByteArrayInputStream)discoveredFile.getValue();
            byte[] contentBytes = new byte[12];
            bais.read(contentBytes);
            Assert.assertTrue("fileContents".equals(new String(contentBytes)));
        }        
        
        mockery.assertIsSatisfied();
    }

}
