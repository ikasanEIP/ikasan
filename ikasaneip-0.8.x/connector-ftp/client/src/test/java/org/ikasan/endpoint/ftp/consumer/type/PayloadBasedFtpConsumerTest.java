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
package org.ikasan.endpoint.ftp.consumer.type;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.resource.ResourceException;

import org.ikasan.client.FileTransferConnectionTemplate;
import org.ikasan.common.Payload;
import org.ikasan.endpoint.ftp.consumer.FtpConsumerConfiguration;
import org.ikasan.endpoint.ftp.consumer.type.PayloadBasedFtpConsumer;
import org.ikasan.framework.factory.DirectoryURLFactory;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Assert;
import org.junit.Test;

/**
 * Test class for {@link PayloadBasedFtpConsumer}
 * 
 * @author Ikasan Development Team
 *
 */
public class PayloadBasedFtpConsumerTest
{
    Mockery mockery = new Mockery()
    {
        {
            setImposteriser(ClassImposteriser.INSTANCE);
        }
    };

    /** mock fileTransferConnectionTemplate */
    final FileTransferConnectionTemplate fileTransferConnectionTemplate = mockery.mock(FileTransferConnectionTemplate.class, "mockFileTransferConnectionTemplate");

    /** mock ftpConfiguration */
    final FtpConsumerConfiguration ftpConfiguration = mockery.mock(FtpConsumerConfiguration.class, "mockFtpConsumerConfiguration");

    /** mock sourceDirectoryURLFactory */
    final DirectoryURLFactory sourceDirectoryURLFactory = mockery.mock(DirectoryURLFactory.class, "mockDirectoryURLFactory");

    /** mock payload */
    final Payload payload = mockery.mock(Payload.class, "mockPayload");

    /**
     * Test failed constructor due to null fileTransferPayloadProvider.
     */
    @Test(expected = IllegalArgumentException.class)
    public void test_failedConstructor_nullFileTransferPayloadProvider()
    {
        new PayloadBasedFtpConsumer(null, null);
    }

    /**
     * Test failed constructor due to null sftpConfiguration.
     */
    @Test(expected = IllegalArgumentException.class)
    public void test_failedConstructor_nullFtpConfiguration()
    {
        new PayloadBasedFtpConsumer(fileTransferConnectionTemplate, null);
    }

    /**
     * Test successful invocation based on injected DirectoryURLFactory, 
     * but no discovered files.
     * @throws ResourceException 
     * @throws IOException 
     */
    @Test
    public void test_successful_invocation_withDirectoryURLFactory_no_discovered_file()
        throws ResourceException, IOException
    {
        final ByteArrayInputStream content = new ByteArrayInputStream("content".getBytes());
        final List<String> sourceDirectories = new ArrayList<String>();
        sourceDirectories.add("sourceDirectory");
        final Map<String,InputStream> filenameContentPairsMap = new HashMap<String,InputStream>();
        filenameContentPairsMap.put("filename", content);
        
        // expectations
        mockery.checking(new Expectations()
        {
            {
                // we have a sourceDirectoryURLFactory specified
                exactly(2).of(ftpConfiguration).getSourceDirectoryURLFactory();
                will(returnValue(sourceDirectoryURLFactory));

                exactly(1).of(ftpConfiguration).getSourceDirectory();
                will(returnValue("sourceDirectory"));
                exactly(1).of(sourceDirectoryURLFactory).getDirectoriesURLs("sourceDirectory");
                will(returnValue(sourceDirectories));
                
                exactly(1).of(ftpConfiguration).getFilenamePattern();
                will(returnValue("filenamePattern"));
                exactly(1).of(ftpConfiguration).getRenameOnSuccess();
                will(returnValue(Boolean.TRUE));
                exactly(1).of(ftpConfiguration).getRenameOnSuccessExtension();
                will(returnValue("renameExtention"));
                exactly(1).of(ftpConfiguration).getMoveOnSuccess();
                will(returnValue(Boolean.FALSE));
                exactly(1).of(ftpConfiguration).getMoveOnSuccessNewPath();
                will(returnValue("moveOnSuccessNewPath"));
                exactly(1).of(ftpConfiguration).getChunking();
                will(returnValue(Boolean.FALSE));
                exactly(1).of(ftpConfiguration).getChunkSize();
                will(returnValue(Integer.valueOf(-1)));
                exactly(1).of(ftpConfiguration).getChecksum();
                will(returnValue(Boolean.FALSE));
                exactly(1).of(ftpConfiguration).getMinAge();
                will(returnValue(Long.valueOf(1)));
                exactly(1).of(ftpConfiguration).getDestructive();
                will(returnValue(Boolean.TRUE));
                exactly(1).of(ftpConfiguration).getFilterDuplicates();
                will(returnValue(Boolean.TRUE));
                exactly(1).of(ftpConfiguration).getFilterOnFilename();
                will(returnValue(Boolean.TRUE));
                exactly(1).of(ftpConfiguration).getFilterOnLastModifiedDate();
                will(returnValue(Boolean.TRUE));
                exactly(1).of(ftpConfiguration).getChronological();
                will(returnValue(Boolean.TRUE));

                exactly(1).of(fileTransferConnectionTemplate).getDiscoveredFile("sourceDirectory", "filenamePattern", 
                    Boolean.TRUE, "renameExtention", Boolean.FALSE, "moveOnSuccessNewPath", Boolean.FALSE, Integer.valueOf(-1), 
                    Boolean.FALSE, Long.valueOf(1), Boolean.TRUE, Boolean.TRUE, Boolean.TRUE, Boolean.TRUE, Boolean.TRUE);
                will(returnValue(null));
                
                // with housekeeping
                exactly(1).of(ftpConfiguration).getMaxRows();
                will(returnValue(new Integer(1)));
                exactly(1).of(ftpConfiguration).getAgeOfFiles();
                will(returnValue(new Integer(1)));
                exactly(1).of(fileTransferConnectionTemplate).housekeep(1, 1);
            }
        });

        PayloadBasedFtpConsumer payloadBasedFtpConsumer = 
            new PayloadBasedFtpConsumer(fileTransferConnectionTemplate, ftpConfiguration);
        Assert.assertNull(payloadBasedFtpConsumer.invoke());
        mockery.assertIsSatisfied();
    }

    /**
     * Test successful invocation based on no discovered files.
     * @throws ResourceException 
     * @throws IOException 
     */
    @Test
    public void test_successful_invocation_no_discovered_file()
        throws ResourceException, IOException
    {
        final ByteArrayInputStream content = new ByteArrayInputStream("content".getBytes());
        final Map<String,InputStream> filenameContentPairsMap = new HashMap<String,InputStream>();
        filenameContentPairsMap.put("filename", content);
        
        // expectations
        mockery.checking(new Expectations()
        {
            {
                // we have a sourceDirectoryURLFactory specified
                exactly(1).of(ftpConfiguration).getSourceDirectoryURLFactory();
                will(returnValue(null));

                exactly(1).of(ftpConfiguration).getSourceDirectory();
                will(returnValue("sourceDirectory"));
                
                exactly(1).of(ftpConfiguration).getFilenamePattern();
                will(returnValue("filenamePattern"));
                exactly(1).of(ftpConfiguration).getRenameOnSuccess();
                will(returnValue(Boolean.TRUE));
                exactly(1).of(ftpConfiguration).getRenameOnSuccessExtension();
                will(returnValue("renameExtention"));
                exactly(1).of(ftpConfiguration).getMoveOnSuccess();
                will(returnValue(Boolean.FALSE));
                exactly(1).of(ftpConfiguration).getMoveOnSuccessNewPath();
                will(returnValue("moveOnSuccessNewPath"));
                exactly(1).of(ftpConfiguration).getChunking();
                will(returnValue(Boolean.FALSE));
                exactly(1).of(ftpConfiguration).getChunkSize();
                will(returnValue(Integer.valueOf(-1)));
                exactly(1).of(ftpConfiguration).getChecksum();
                will(returnValue(Boolean.FALSE));
                exactly(1).of(ftpConfiguration).getMinAge();
                will(returnValue(Long.valueOf(1)));
                exactly(1).of(ftpConfiguration).getDestructive();
                will(returnValue(Boolean.TRUE));
                exactly(1).of(ftpConfiguration).getFilterDuplicates();
                will(returnValue(Boolean.TRUE));
                exactly(1).of(ftpConfiguration).getFilterOnFilename();
                will(returnValue(Boolean.TRUE));
                exactly(1).of(ftpConfiguration).getFilterOnLastModifiedDate();
                will(returnValue(Boolean.TRUE));
                exactly(1).of(ftpConfiguration).getChronological();
                will(returnValue(Boolean.TRUE));

                exactly(1).of(fileTransferConnectionTemplate).getDiscoveredFile("sourceDirectory", "filenamePattern", 
                    Boolean.TRUE, "renameExtention", Boolean.FALSE, "moveOnSuccessNewPath", Boolean.FALSE, Integer.valueOf(-1), 
                    Boolean.FALSE, Long.valueOf(1), Boolean.TRUE, Boolean.TRUE, Boolean.TRUE, Boolean.TRUE, Boolean.TRUE);
                will(returnValue(null));
                
                // with housekeeping
                exactly(1).of(ftpConfiguration).getMaxRows();
                will(returnValue(new Integer(1)));
                exactly(1).of(ftpConfiguration).getAgeOfFiles();
                will(returnValue(new Integer(1)));
                exactly(1).of(fileTransferConnectionTemplate).housekeep(1, 1);
            }
        });

        PayloadBasedFtpConsumer payloadBasedFtpConsumer = 
            new PayloadBasedFtpConsumer(fileTransferConnectionTemplate, ftpConfiguration);
        Assert.assertNull(payloadBasedFtpConsumer.invoke());
        mockery.assertIsSatisfied();
    }

    /**
     * Test successful invocation based on a discovered file.
     * @throws ResourceException 
     * @throws IOException 
     */
    @Test
    public void test_successful_invocation_discovering_a_file() throws ResourceException, IOException
    {
        final ByteArrayInputStream content = new ByteArrayInputStream("content".getBytes());
        final Map<String,InputStream> filenameContentPairsMap = new HashMap<String,InputStream>();
        filenameContentPairsMap.put("filename", content);
        
        // expectations
        mockery.checking(new Expectations()
        {
            {
                // we have a sourceDirectoryURLFactory specified
                exactly(1).of(ftpConfiguration).getSourceDirectoryURLFactory();
                will(returnValue(null));

                exactly(1).of(ftpConfiguration).getSourceDirectory();
                will(returnValue("sourceDirectory"));
                exactly(1).of(ftpConfiguration).getFilenamePattern();
                will(returnValue("filenamePattern"));
                exactly(1).of(ftpConfiguration).getRenameOnSuccess();
                will(returnValue(Boolean.TRUE));
                exactly(1).of(ftpConfiguration).getRenameOnSuccessExtension();
                will(returnValue("renameExtention"));
                exactly(1).of(ftpConfiguration).getMoveOnSuccess();
                will(returnValue(Boolean.FALSE));
                exactly(1).of(ftpConfiguration).getMoveOnSuccessNewPath();
                will(returnValue("moveOnSuccessNewPath"));
                exactly(1).of(ftpConfiguration).getChunking();
                will(returnValue(Boolean.FALSE));
                exactly(1).of(ftpConfiguration).getChunkSize();
                will(returnValue(Integer.valueOf(-1)));
                exactly(1).of(ftpConfiguration).getChecksum();
                will(returnValue(Boolean.FALSE));
                exactly(1).of(ftpConfiguration).getMinAge();
                will(returnValue(Long.valueOf(1)));
                exactly(1).of(ftpConfiguration).getDestructive();
                will(returnValue(Boolean.TRUE));
                exactly(1).of(ftpConfiguration).getFilterDuplicates();
                will(returnValue(Boolean.TRUE));
                exactly(1).of(ftpConfiguration).getFilterOnFilename();
                will(returnValue(Boolean.TRUE));
                exactly(1).of(ftpConfiguration).getFilterOnLastModifiedDate();
                will(returnValue(Boolean.TRUE));
                exactly(1).of(ftpConfiguration).getChronological();
                will(returnValue(Boolean.TRUE));

                exactly(1).of(fileTransferConnectionTemplate).getDiscoveredFile("sourceDirectory", "filenamePattern", 
                    Boolean.TRUE, "renameExtention", Boolean.FALSE, "moveOnSuccessNewPath", Boolean.FALSE, Integer.valueOf(-1), 
                    Boolean.FALSE, Long.valueOf(1), Boolean.TRUE, Boolean.TRUE, Boolean.TRUE, Boolean.TRUE, Boolean.TRUE);
                will(returnValue(payload));
            }
        });

        PayloadBasedFtpConsumer payloadBasedFtpConsumer = 
            new PayloadBasedFtpConsumer(fileTransferConnectionTemplate, ftpConfiguration);
        Assert.assertNotNull(payloadBasedFtpConsumer.invoke());
        mockery.assertIsSatisfied();
    }

    /**
     * Test successful invocation based on a discovered file using the DirectoryURLFactory.
     * @throws ResourceException 
     * @throws IOException 
     */
    @Test
    public void test_successful_invocation_with_DirectoryURLFactory_discovering_a_file() throws ResourceException, IOException
    {
        final ByteArrayInputStream content = new ByteArrayInputStream("content".getBytes());
        final List<String> sourceDirectories = new ArrayList<String>();
        sourceDirectories.add("sourceDirectory");
        final Map<String,InputStream> filenameContentPairsMap = new HashMap<String,InputStream>();
        filenameContentPairsMap.put("filename", content);
        
        // expectations
        mockery.checking(new Expectations()
        {
            {
                // we have a sourceDirectoryURLFactory specified
                exactly(2).of(ftpConfiguration).getSourceDirectoryURLFactory();
                will(returnValue(sourceDirectoryURLFactory));

                exactly(1).of(ftpConfiguration).getSourceDirectory();
                will(returnValue("sourceDirectory"));
                exactly(1).of(sourceDirectoryURLFactory).getDirectoriesURLs("sourceDirectory");
                will(returnValue(sourceDirectories));
                
                exactly(1).of(ftpConfiguration).getFilenamePattern();
                will(returnValue("filenamePattern"));
                exactly(1).of(ftpConfiguration).getRenameOnSuccess();
                will(returnValue(Boolean.TRUE));
                exactly(1).of(ftpConfiguration).getRenameOnSuccessExtension();
                will(returnValue("renameExtention"));
                exactly(1).of(ftpConfiguration).getMoveOnSuccess();
                will(returnValue(Boolean.FALSE));
                exactly(1).of(ftpConfiguration).getMoveOnSuccessNewPath();
                will(returnValue("moveOnSuccessNewPath"));
                exactly(1).of(ftpConfiguration).getChunking();
                will(returnValue(Boolean.FALSE));
                exactly(1).of(ftpConfiguration).getChunkSize();
                will(returnValue(Integer.valueOf(-1)));
                exactly(1).of(ftpConfiguration).getChecksum();
                will(returnValue(Boolean.FALSE));
                exactly(1).of(ftpConfiguration).getMinAge();
                will(returnValue(Long.valueOf(1)));
                exactly(1).of(ftpConfiguration).getDestructive();
                will(returnValue(Boolean.TRUE));
                exactly(1).of(ftpConfiguration).getFilterDuplicates();
                will(returnValue(Boolean.TRUE));
                exactly(1).of(ftpConfiguration).getFilterOnFilename();
                will(returnValue(Boolean.TRUE));
                exactly(1).of(ftpConfiguration).getFilterOnLastModifiedDate();
                will(returnValue(Boolean.TRUE));
                exactly(1).of(ftpConfiguration).getChronological();
                will(returnValue(Boolean.TRUE));

                exactly(1).of(fileTransferConnectionTemplate).getDiscoveredFile("sourceDirectory", "filenamePattern", 
                    Boolean.TRUE, "renameExtention", Boolean.FALSE, "moveOnSuccessNewPath", Boolean.FALSE, Integer.valueOf(-1), 
                    Boolean.FALSE, Long.valueOf(1), Boolean.TRUE, Boolean.TRUE, Boolean.TRUE, Boolean.TRUE, Boolean.TRUE);
                will(returnValue(payload));
            }
        });

        PayloadBasedFtpConsumer payloadBasedFtpConsumer = 
            new PayloadBasedFtpConsumer(fileTransferConnectionTemplate, ftpConfiguration);
        Assert.assertNotNull(payloadBasedFtpConsumer.invoke());
        mockery.assertIsSatisfied();
    }
}
