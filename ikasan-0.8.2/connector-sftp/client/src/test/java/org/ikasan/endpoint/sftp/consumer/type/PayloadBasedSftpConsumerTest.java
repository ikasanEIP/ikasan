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
package org.ikasan.endpoint.sftp.consumer.type;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.resource.ResourceException;

import org.ikasan.client.FileTransferConnectionTemplate;
import org.ikasan.common.Payload;
import org.ikasan.endpoint.sftp.consumer.SftpConsumerConfiguration;
import org.ikasan.endpoint.sftp.consumer.type.PayloadBasedSftpConsumer;
import org.ikasan.framework.factory.DirectoryURLFactory;
import org.ikasan.spec.endpoint.Consumer;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Assert;
import org.junit.Test;

/**
 * Test class for {@link PayloadBasedSftpConsumer}
 * 
 * @author Ikasan Development Team
 *
 */
@SuppressWarnings("unqualified-field-access")
public class PayloadBasedSftpConsumerTest
{
    /** The Mockery */
    private final Mockery mockery = new Mockery()
    {
        {
            setImposteriser(ClassImposteriser.INSTANCE);
        }
    };

    /** Mock fileTransferConnectionTemplate */
    private final FileTransferConnectionTemplate fileTransferConnectionTemplate = this.mockery.mock(FileTransferConnectionTemplate.class, "mockFileTransferConnectionTemplate");

    /** Mock ssftpConfiguration */
    private final SftpConsumerConfiguration sftpConfiguration = this.mockery.mock(SftpConsumerConfiguration.class, "mockSftpConsumerConfiguration");

    /** Mock sourceDirectoryURLFactory */
    private final DirectoryURLFactory sourceDirectoryURLFactory = this.mockery.mock(DirectoryURLFactory.class, "mockDirectoryURLFactory");

    /** Mock payload */
    private final Payload payload = this.mockery.mock(Payload.class, "mockPayload");

    /** Object to test */
    private Consumer<Payload> payloadBasedSftpConsumer = new PayloadBasedSftpConsumer(this.fileTransferConnectionTemplate, this.sftpConfiguration);

    /**
     * Test failed constructor due to null fileTransferPayloadProvider.
     */
    @SuppressWarnings("unused")
    @Test(expected = IllegalArgumentException.class)
    public void test_failedConstructor_nullFileTransferConnectionTemplate()
    {
        new PayloadBasedSftpConsumer(null, null);
    }

    /**
     * Test failed constructor due to null ssftpConfiguration.
     */
    @SuppressWarnings("unused")
    @Test(expected = IllegalArgumentException.class)
    public void test_failedConstructor_nullSsftpConfiguration()
    {
        new PayloadBasedSftpConsumer(this.fileTransferConnectionTemplate, null);
    }

    /**
     * Test successful invocation based on injected DirectoryURLFactory, 
     * but no discovered files.
     * 
     * @throws ResourceException if error invoking endpoint
     */
    @Test public void test_successful_invocation_withDirectoryURLFactory_no_discovered_file()
        throws ResourceException
    {
        final ByteArrayInputStream content = new ByteArrayInputStream("content".getBytes());
        final List<String> sourceDirectories = new ArrayList<String>();
        sourceDirectories.add("sourceDirectory");
        final Map<String,InputStream> filenameContentPairsMap = new HashMap<String,InputStream>();
        filenameContentPairsMap.put("filename", content);

        // Expectations
        this.mockery.checking(new Expectations()
        {
            {
                // We have a sourceDirectoryURLFactory specified
                exactly(2).of(sftpConfiguration).getSourceDirectoryURLFactory(); will(returnValue(sourceDirectoryURLFactory));
                one(sftpConfiguration).getSourceDirectory(); will(returnValue("sourceDirectory"));
                one(sourceDirectoryURLFactory).getDirectoriesURLs("sourceDirectory"); will(returnValue(sourceDirectories));

                one(sftpConfiguration).getFilenamePattern(); will(returnValue("filenamePattern"));
                one(sftpConfiguration).getRenameOnSuccess(); will(returnValue(Boolean.TRUE));
                one(sftpConfiguration).getRenameOnSuccessExtension(); will(returnValue("renameExtention"));
                one(sftpConfiguration).getMoveOnSuccess(); will(returnValue(Boolean.FALSE));
                one(sftpConfiguration).getMoveOnSuccessNewPath(); will(returnValue("moveOnSuccessNewPath"));
                one(sftpConfiguration).getChunking(); will(returnValue(Boolean.FALSE));
                one(sftpConfiguration).getChunkSize(); will(returnValue(Integer.valueOf(-1)));
                one(sftpConfiguration).getChecksum(); will(returnValue(Boolean.FALSE));
                one(sftpConfiguration).getMinAge(); will(returnValue(Long.valueOf(1)));
                one(sftpConfiguration).getDestructive(); will(returnValue(Boolean.TRUE));
                one(sftpConfiguration).getFilterDuplicates(); will(returnValue(Boolean.TRUE));
                one(sftpConfiguration).getFilterOnFilename(); will(returnValue(Boolean.TRUE));
                one(sftpConfiguration).getFilterOnLastModifiedDate(); will(returnValue(Boolean.TRUE));
                one(sftpConfiguration).getChronological(); will(returnValue(Boolean.TRUE));

                one(fileTransferConnectionTemplate).getDiscoveredFile("sourceDirectory", "filenamePattern", 
                    true, "renameExtention", false, "moveOnSuccessNewPath", false, -1, 
                    false, 1, true, true, true, true, true); will(returnValue(null));

                // With housekeeping
                one(sftpConfiguration).getMaxRows(); will(returnValue(Integer.valueOf(1)));
                one(sftpConfiguration).getAgeOfFiles(); will(returnValue(Integer.valueOf(1)));
                one(fileTransferConnectionTemplate).housekeep(1, 1);
            }
        });

        Assert.assertNull(this.payloadBasedSftpConsumer.invoke());
        this.mockery.assertIsSatisfied();
    }

    /**
     * Test successful invocation based on no discovered files.
     * @throws ResourceException if error invoking endpoint
     */
    @Test public void test_successful_invocation_no_discovered_file()
        throws ResourceException
    {
        final ByteArrayInputStream content = new ByteArrayInputStream("content".getBytes());
        final Map<String,InputStream> filenameContentPairsMap = new HashMap<String,InputStream>();
        filenameContentPairsMap.put("filename", content);

        // Expectations
        this.mockery.checking(new Expectations()
        {
            {
                // We don't have a sourceDirectoryURLFactory specified
                one(sftpConfiguration).getSourceDirectoryURLFactory(); will(returnValue(null));
                one(sftpConfiguration).getSourceDirectory(); will(returnValue("sourceDirectory"));

                one(sftpConfiguration).getFilenamePattern(); will(returnValue("filenamePattern"));
                one(sftpConfiguration).getRenameOnSuccess(); will(returnValue(Boolean.TRUE));
                one(sftpConfiguration).getRenameOnSuccessExtension(); will(returnValue("renameExtention"));
                one(sftpConfiguration).getMoveOnSuccess(); will(returnValue(Boolean.FALSE));
                one(sftpConfiguration).getMoveOnSuccessNewPath(); will(returnValue("moveOnSuccessNewPath"));
                one(sftpConfiguration).getChunking(); will(returnValue(Boolean.FALSE));
                one(sftpConfiguration).getChunkSize(); will(returnValue(Integer.valueOf(-1)));
                one(sftpConfiguration).getChecksum(); will(returnValue(Boolean.FALSE));
                one(sftpConfiguration).getMinAge(); will(returnValue(Long.valueOf(1)));
                one(sftpConfiguration).getDestructive(); will(returnValue(Boolean.TRUE));
                one(sftpConfiguration).getFilterDuplicates(); will(returnValue(Boolean.TRUE));
                one(sftpConfiguration).getFilterOnFilename(); will(returnValue(Boolean.TRUE));
                one(sftpConfiguration).getFilterOnLastModifiedDate(); will(returnValue(Boolean.TRUE));
                one(sftpConfiguration).getChronological(); will(returnValue(Boolean.TRUE));

                one(fileTransferConnectionTemplate).getDiscoveredFile("sourceDirectory", "filenamePattern", 
                        true, "renameExtention", false, "moveOnSuccessNewPath", false, -1, 
                        false, 1, true, true, true, true, true); will(returnValue(null));

                //With housekeeping
                one(sftpConfiguration).getMaxRows(); will(returnValue(Integer.valueOf(1)));
                one(sftpConfiguration).getAgeOfFiles(); will(returnValue(Integer.valueOf(1)));
                one(fileTransferConnectionTemplate).housekeep(1, 1);
            }
        });

        Assert.assertNull(this.payloadBasedSftpConsumer.invoke());
        this.mockery.assertIsSatisfied();
    }

    /**
     * Housekeeping is an optional operation. If configuration have maximum rows to housekeep set to <code>-1</code>,
     * consumer must not housekeep.
     * 
     * @throws ResourceException if error invoking endpoint
     */
    @Test public void consumer_must_not_perform_housekeeping_if_maxRows_not_configured()
        throws ResourceException
    {
        final ByteArrayInputStream content = new ByteArrayInputStream("content".getBytes());
        final Map<String,InputStream> filenameContentPairsMap = new HashMap<String,InputStream>();
        filenameContentPairsMap.put("filename", content);

        // Expectations
        this.mockery.checking(new Expectations()
        {
            {
                // We don't have a sourceDirectoryURLFactory specified
                one(sftpConfiguration).getSourceDirectoryURLFactory(); will(returnValue(null));
                one(sftpConfiguration).getSourceDirectory(); will(returnValue("sourceDirectory"));

                one(sftpConfiguration).getFilenamePattern(); will(returnValue("filenamePattern"));
                one(sftpConfiguration).getRenameOnSuccess(); will(returnValue(Boolean.TRUE));
                one(sftpConfiguration).getRenameOnSuccessExtension(); will(returnValue("renameExtention"));
                one(sftpConfiguration).getMoveOnSuccess(); will(returnValue(Boolean.FALSE));
                one(sftpConfiguration).getMoveOnSuccessNewPath(); will(returnValue("moveOnSuccessNewPath"));
                one(sftpConfiguration).getChunking(); will(returnValue(Boolean.FALSE));
                one(sftpConfiguration).getChunkSize(); will(returnValue(Integer.valueOf(-1)));
                one(sftpConfiguration).getChecksum(); will(returnValue(Boolean.FALSE));
                one(sftpConfiguration).getMinAge(); will(returnValue(Long.valueOf(1)));
                one(sftpConfiguration).getDestructive(); will(returnValue(Boolean.TRUE));
                one(sftpConfiguration).getFilterDuplicates(); will(returnValue(Boolean.TRUE));
                one(sftpConfiguration).getFilterOnFilename(); will(returnValue(Boolean.TRUE));
                one(sftpConfiguration).getFilterOnLastModifiedDate(); will(returnValue(Boolean.TRUE));
                one(sftpConfiguration).getChronological(); will(returnValue(Boolean.TRUE));

                one(fileTransferConnectionTemplate).getDiscoveredFile("sourceDirectory", "filenamePattern", 
                        true, "renameExtention", false, "moveOnSuccessNewPath", false, -1, 
                        false, 1, true, true, true, true, true); will(returnValue(null));

                //With housekeeping
                one(sftpConfiguration).getMaxRows(); will(returnValue(Integer.valueOf(-1)));
                one(sftpConfiguration).getAgeOfFiles(); will(returnValue(Integer.valueOf(1)));
            }
        });

        Assert.assertNull(this.payloadBasedSftpConsumer.invoke());
        this.mockery.assertIsSatisfied();
    }

    /**
     * Housekeeping is an optional operation. If configuration have min age of files to housekeep set to <code>-1</code>,
     * consumer must not housekeep.
     * 
     * @throws ResourceException if error invoking endpoint
     */
    @Test public void consumer_must_not_perform_housekeeping_if_ageOfFiles_not_configured()
        throws ResourceException
    {
        final ByteArrayInputStream content = new ByteArrayInputStream("content".getBytes());
        final Map<String,InputStream> filenameContentPairsMap = new HashMap<String,InputStream>();
        filenameContentPairsMap.put("filename", content);

        // Expectations
        this.mockery.checking(new Expectations()
        {
            {
                // We don't have a sourceDirectoryURLFactory specified
                one(sftpConfiguration).getSourceDirectoryURLFactory(); will(returnValue(null));
                one(sftpConfiguration).getSourceDirectory(); will(returnValue("sourceDirectory"));

                one(sftpConfiguration).getFilenamePattern(); will(returnValue("filenamePattern"));
                one(sftpConfiguration).getRenameOnSuccess(); will(returnValue(Boolean.TRUE));
                one(sftpConfiguration).getRenameOnSuccessExtension(); will(returnValue("renameExtention"));
                one(sftpConfiguration).getMoveOnSuccess(); will(returnValue(Boolean.FALSE));
                one(sftpConfiguration).getMoveOnSuccessNewPath(); will(returnValue("moveOnSuccessNewPath"));
                one(sftpConfiguration).getChunking(); will(returnValue(Boolean.FALSE));
                one(sftpConfiguration).getChunkSize(); will(returnValue(Integer.valueOf(-1)));
                one(sftpConfiguration).getChecksum(); will(returnValue(Boolean.FALSE));
                one(sftpConfiguration).getMinAge(); will(returnValue(Long.valueOf(1)));
                one(sftpConfiguration).getDestructive(); will(returnValue(Boolean.TRUE));
                one(sftpConfiguration).getFilterDuplicates(); will(returnValue(Boolean.TRUE));
                one(sftpConfiguration).getFilterOnFilename(); will(returnValue(Boolean.TRUE));
                one(sftpConfiguration).getFilterOnLastModifiedDate(); will(returnValue(Boolean.TRUE));
                one(sftpConfiguration).getChronological(); will(returnValue(Boolean.TRUE));

                one(fileTransferConnectionTemplate).getDiscoveredFile("sourceDirectory", "filenamePattern", 
                        true, "renameExtention", false, "moveOnSuccessNewPath", false, -1, 
                        false, 1, true, true, true, true, true); will(returnValue(null));

                //With housekeeping
                one(sftpConfiguration).getMaxRows(); will(returnValue(Integer.valueOf(1)));
                one(sftpConfiguration).getAgeOfFiles(); will(returnValue(Integer.valueOf(-1)));
            }
        });

        Assert.assertNull(this.payloadBasedSftpConsumer.invoke());
        this.mockery.assertIsSatisfied();
    }

    /**
     * Housekeeping is an optional operation.
     * 
     * @throws ResourceException if error invoking endpoint
     */
    @Test public void consumer_must_not_perform_housekeeping_if_either_maxRows_or_ageOfFiles_not_configured()
        throws ResourceException
    {
        final ByteArrayInputStream content = new ByteArrayInputStream("content".getBytes());
        final Map<String,InputStream> filenameContentPairsMap = new HashMap<String,InputStream>();
        filenameContentPairsMap.put("filename", content);

        // Expectations
        this.mockery.checking(new Expectations()
        {
            {
                // We don't have a sourceDirectoryURLFactory specified
                one(sftpConfiguration).getSourceDirectoryURLFactory(); will(returnValue(null));
                one(sftpConfiguration).getSourceDirectory(); will(returnValue("sourceDirectory"));

                one(sftpConfiguration).getFilenamePattern(); will(returnValue("filenamePattern"));
                one(sftpConfiguration).getRenameOnSuccess(); will(returnValue(Boolean.TRUE));
                one(sftpConfiguration).getRenameOnSuccessExtension(); will(returnValue("renameExtention"));
                one(sftpConfiguration).getMoveOnSuccess(); will(returnValue(Boolean.FALSE));
                one(sftpConfiguration).getMoveOnSuccessNewPath(); will(returnValue("moveOnSuccessNewPath"));
                one(sftpConfiguration).getChunking(); will(returnValue(Boolean.FALSE));
                one(sftpConfiguration).getChunkSize(); will(returnValue(Integer.valueOf(-1)));
                one(sftpConfiguration).getChecksum(); will(returnValue(Boolean.FALSE));
                one(sftpConfiguration).getMinAge(); will(returnValue(Long.valueOf(1)));
                one(sftpConfiguration).getDestructive(); will(returnValue(Boolean.TRUE));
                one(sftpConfiguration).getFilterDuplicates(); will(returnValue(Boolean.TRUE));
                one(sftpConfiguration).getFilterOnFilename(); will(returnValue(Boolean.TRUE));
                one(sftpConfiguration).getFilterOnLastModifiedDate(); will(returnValue(Boolean.TRUE));
                one(sftpConfiguration).getChronological(); will(returnValue(Boolean.TRUE));

                one(fileTransferConnectionTemplate).getDiscoveredFile("sourceDirectory", "filenamePattern", 
                        true, "renameExtention", false, "moveOnSuccessNewPath", false, -1, 
                        false, 1, true, true, true, true, true); will(returnValue(null));

                //With housekeeping
                one(sftpConfiguration).getMaxRows(); will(returnValue(Integer.valueOf(-1)));
                one(sftpConfiguration).getAgeOfFiles(); will(returnValue(Integer.valueOf(-1)));
            }
        });

        Assert.assertNull(this.payloadBasedSftpConsumer.invoke());
        this.mockery.assertIsSatisfied();
    }

    /**
     * Test successful invocation based on a discovered file.
     * @throws ResourceException if error invoking endpoint
     */
    @Test public void test_successful_invocation_discovering_a_file() throws ResourceException
    {
        final ByteArrayInputStream content = new ByteArrayInputStream("content".getBytes());
        final Map<String,InputStream> filenameContentPairsMap = new HashMap<String,InputStream>();
        filenameContentPairsMap.put("filename", content);

        // Expectations
        this.mockery.checking(new Expectations()
        {
            {
                // We dont have a sourceDirectoryURLFactory specified
                one(sftpConfiguration).getSourceDirectoryURLFactory(); will(returnValue(null));
                one(sftpConfiguration).getSourceDirectory(); will(returnValue("sourceDirectory"));

                one(sftpConfiguration).getFilenamePattern(); will(returnValue("filenamePattern"));
                one(sftpConfiguration).getRenameOnSuccess(); will(returnValue(Boolean.TRUE));
                one(sftpConfiguration).getRenameOnSuccessExtension(); will(returnValue("renameExtention"));
                one(sftpConfiguration).getMoveOnSuccess(); will(returnValue(Boolean.FALSE));
                one(sftpConfiguration).getMoveOnSuccessNewPath(); will(returnValue("moveOnSuccessNewPath"));
                one(sftpConfiguration).getChunking(); will(returnValue(Boolean.FALSE));
                one(sftpConfiguration).getChunkSize(); will(returnValue(Integer.valueOf(-1)));
                one(sftpConfiguration).getChecksum(); will(returnValue(Boolean.FALSE));
                one(sftpConfiguration).getMinAge(); will(returnValue(Long.valueOf(1)));
                one(sftpConfiguration).getDestructive(); will(returnValue(Boolean.TRUE));
                one(sftpConfiguration).getFilterDuplicates(); will(returnValue(Boolean.TRUE));
                one(sftpConfiguration).getFilterOnFilename(); will(returnValue(Boolean.TRUE));
                one(sftpConfiguration).getFilterOnLastModifiedDate(); will(returnValue(Boolean.TRUE));
                one(sftpConfiguration).getChronological(); will(returnValue(Boolean.TRUE));

                one(fileTransferConnectionTemplate).getDiscoveredFile("sourceDirectory", "filenamePattern", 
                        true, "renameExtention", false, "moveOnSuccessNewPath", false, -1, 
                        false, 1, true, true, true, true, true); will(returnValue(payload));
            }
        });

        Assert.assertNotNull(this.payloadBasedSftpConsumer.invoke());
        this.mockery.assertIsSatisfied();
    }

    /**
     * Test successful invocation based on a discovered file using the DirectoryURLFactory.
     * @throws ResourceException if error invoking endpoint
     */
    @Test public void test_successful_invocation_with_DirectoryURLFactory_discovering_a_file() throws ResourceException
    {
        final ByteArrayInputStream content = new ByteArrayInputStream("content".getBytes());
        final List<String> sourceDirectories = new ArrayList<String>();
        sourceDirectories.add("sourceDirectory");
        final Map<String,InputStream> filenameContentPairsMap = new HashMap<String,InputStream>();
        filenameContentPairsMap.put("filename", content);

        // Expectations
        this.mockery.checking(new Expectations()
        {
            {
                // We have a sourceDirectoryURLFactory specified
                // We have a sourceDirectoryURLFactory specified
                exactly(2).of(sftpConfiguration).getSourceDirectoryURLFactory(); will(returnValue(sourceDirectoryURLFactory));
                one(sftpConfiguration).getSourceDirectory(); will(returnValue("sourceDirectory"));
                one(sourceDirectoryURLFactory).getDirectoriesURLs("sourceDirectory"); will(returnValue(sourceDirectories));

                one(sftpConfiguration).getFilenamePattern(); will(returnValue("filenamePattern"));
                one(sftpConfiguration).getRenameOnSuccess(); will(returnValue(Boolean.TRUE));
                one(sftpConfiguration).getRenameOnSuccessExtension(); will(returnValue("renameExtention"));
                one(sftpConfiguration).getMoveOnSuccess(); will(returnValue(Boolean.FALSE));
                one(sftpConfiguration).getMoveOnSuccessNewPath(); will(returnValue("moveOnSuccessNewPath"));
                one(sftpConfiguration).getChunking(); will(returnValue(Boolean.FALSE));
                one(sftpConfiguration).getChunkSize(); will(returnValue(Integer.valueOf(-1)));
                one(sftpConfiguration).getChecksum(); will(returnValue(Boolean.FALSE));
                one(sftpConfiguration).getMinAge(); will(returnValue(Long.valueOf(1)));
                one(sftpConfiguration).getDestructive(); will(returnValue(Boolean.TRUE));
                one(sftpConfiguration).getFilterDuplicates(); will(returnValue(Boolean.TRUE));
                one(sftpConfiguration).getFilterOnFilename(); will(returnValue(Boolean.TRUE));
                one(sftpConfiguration).getFilterOnLastModifiedDate(); will(returnValue(Boolean.TRUE));
                one(sftpConfiguration).getChronological(); will(returnValue(Boolean.TRUE));


                one(fileTransferConnectionTemplate).getDiscoveredFile("sourceDirectory", "filenamePattern", 
                        true, "renameExtention", false, "moveOnSuccessNewPath", false, -1, 
                        false, 1, true, true, true, true, true); will(returnValue(payload));
                will(returnValue(payload));
            }
        });

        Assert.assertNotNull(this.payloadBasedSftpConsumer.invoke());
        this.mockery.assertIsSatisfied();
    }

    /**
     * If no alternate connection details are provided, the consumer will throw the exception and give up
     * @throws ResourceException if error invoking endpoint
     */
    @Test(expected=ResourceException.class)
    public void consumer_fails() throws ResourceException
    {
        final ResourceException exception = new ResourceException(new RuntimeException("Something gone wrong"));

        this.mockery.checking(new Expectations()
        {
            {
                // We dont have a sourceDirectoryURLFactory specified
                one(sftpConfiguration).getSourceDirectoryURLFactory(); will(returnValue(null));
                one(sftpConfiguration).getSourceDirectory(); will(returnValue("sourceDirectory"));

                one(sftpConfiguration).getFilenamePattern(); will(returnValue("filenamePattern"));
                one(sftpConfiguration).getRenameOnSuccess(); will(returnValue(Boolean.TRUE));
                one(sftpConfiguration).getRenameOnSuccessExtension(); will(returnValue("renameExtention"));
                one(sftpConfiguration).getMoveOnSuccess(); will(returnValue(Boolean.FALSE));
                one(sftpConfiguration).getMoveOnSuccessNewPath(); will(returnValue("moveOnSuccessNewPath"));
                one(sftpConfiguration).getChunking(); will(returnValue(Boolean.FALSE));
                one(sftpConfiguration).getChunkSize(); will(returnValue(Integer.valueOf(-1)));
                one(sftpConfiguration).getChecksum(); will(returnValue(Boolean.FALSE));
                one(sftpConfiguration).getMinAge(); will(returnValue(Long.valueOf(1)));
                one(sftpConfiguration).getDestructive(); will(returnValue(Boolean.TRUE));
                one(sftpConfiguration).getFilterDuplicates(); will(returnValue(Boolean.TRUE));
                one(sftpConfiguration).getFilterOnFilename(); will(returnValue(Boolean.TRUE));
                one(sftpConfiguration).getFilterOnLastModifiedDate(); will(returnValue(Boolean.TRUE));
                one(sftpConfiguration).getChronological(); will(returnValue(Boolean.TRUE));

                one(fileTransferConnectionTemplate).getDiscoveredFile("sourceDirectory", "filenamePattern", 
                        true, "renameExtention", false, "moveOnSuccessNewPath", false, -1, 
                        false, 1, true, true, true, true, true); will(throwException(exception));
            }
        });

        // Test
        try
        {
            this.payloadBasedSftpConsumer.invoke();
        }
        catch (ResourceException e)
        {
            Assert.assertEquals(this.fileTransferConnectionTemplate, ((PayloadBasedSftpConsumer)this.payloadBasedSftpConsumer).getActiveFileTransferConnectionTemplate());
            throw e;
        }
        this.mockery.assertIsSatisfied();
        Assert.fail("Unreachable code.");
    }

    /**
     * When the consumer is configured with an alternate connection template, on failure, consumer will switch to use the
     * alternate next time it is invoked.
     * @throws ResourceException if error invoking endpoint
     */
    @Test(expected=ResourceException.class)
    public void consumer_fails_changes_to_alternate_connection_template() throws ResourceException
    {
        final ResourceException exception = new ResourceException(new RuntimeException("Something gone wrong"));

        this.mockery.checking(new Expectations()
        {
            {
                // We dont have a sourceDirectoryURLFactory specified
                one(sftpConfiguration).getSourceDirectoryURLFactory(); will(returnValue(null));
                one(sftpConfiguration).getSourceDirectory(); will(returnValue("sourceDirectory"));

                one(sftpConfiguration).getFilenamePattern(); will(returnValue("filenamePattern"));
                one(sftpConfiguration).getRenameOnSuccess(); will(returnValue(Boolean.TRUE));
                one(sftpConfiguration).getRenameOnSuccessExtension(); will(returnValue("renameExtention"));
                one(sftpConfiguration).getMoveOnSuccess(); will(returnValue(Boolean.FALSE));
                one(sftpConfiguration).getMoveOnSuccessNewPath(); will(returnValue("moveOnSuccessNewPath"));
                one(sftpConfiguration).getChunking(); will(returnValue(Boolean.FALSE));
                one(sftpConfiguration).getChunkSize(); will(returnValue(Integer.valueOf(-1)));
                one(sftpConfiguration).getChecksum(); will(returnValue(Boolean.FALSE));
                one(sftpConfiguration).getMinAge(); will(returnValue(Long.valueOf(1)));
                one(sftpConfiguration).getDestructive(); will(returnValue(Boolean.TRUE));
                one(sftpConfiguration).getFilterDuplicates(); will(returnValue(Boolean.TRUE));
                one(sftpConfiguration).getFilterOnFilename(); will(returnValue(Boolean.TRUE));
                one(sftpConfiguration).getFilterOnLastModifiedDate(); will(returnValue(Boolean.TRUE));
                one(sftpConfiguration).getChronological(); will(returnValue(Boolean.TRUE));

                one(fileTransferConnectionTemplate).getDiscoveredFile("sourceDirectory", "filenamePattern", 
                        true, "renameExtention", false, "moveOnSuccessNewPath", false, -1, 
                        false, 1, true, true, true, true, true); will(throwException(exception));
            }
        });

        // Test
        final FileTransferConnectionTemplate mockAlternateConnectionTemplate = this.mockery.mock(FileTransferConnectionTemplate.class, "alternateConnectionTemplate");
        ((PayloadBasedSftpConsumer)this.payloadBasedSftpConsumer).setAlternateFileTransferConnectionTemplate(mockAlternateConnectionTemplate);
        try
        {
            this.payloadBasedSftpConsumer.invoke();
        }
        catch (ResourceException e)
        {
            Assert.assertEquals(mockAlternateConnectionTemplate, ((PayloadBasedSftpConsumer)this.payloadBasedSftpConsumer).getActiveFileTransferConnectionTemplate());
            throw e;
        }
        this.mockery.assertIsSatisfied();
        Assert.fail("Unreachable code.");
    }

    /**
     * If an error occurs while consumer using alternative connection template, switch back to original connection template
     * 
     * @throws ResourceException if error invoking endpoint
     */
    @Test(expected=ResourceException.class)
    public void consumer_fails_changes_to_original_connection_template() throws ResourceException
    {
        final ResourceException exception = new ResourceException(new RuntimeException("Something gone wrong"));
        final FileTransferConnectionTemplate mockAlternateConnectionTemplate = this.mockery.mock(FileTransferConnectionTemplate.class, "alternateConnectionTemplate");

        this.mockery.checking(new Expectations()
        {
            {
                // We dont have a sourceDirectoryURLFactory specified
                exactly(2).of(sftpConfiguration).getSourceDirectoryURLFactory(); will(returnValue(null));
                exactly(2).of(sftpConfiguration).getSourceDirectory(); will(returnValue("sourceDirectory"));

                exactly(2).of(sftpConfiguration).getFilenamePattern(); will(returnValue("filenamePattern"));
                exactly(2).of(sftpConfiguration).getRenameOnSuccess(); will(returnValue(Boolean.TRUE));
                exactly(2).of(sftpConfiguration).getRenameOnSuccessExtension(); will(returnValue("renameExtention"));
                exactly(2).of(sftpConfiguration).getMoveOnSuccess(); will(returnValue(Boolean.FALSE));
                exactly(2).of(sftpConfiguration).getMoveOnSuccessNewPath(); will(returnValue("moveOnSuccessNewPath"));
                exactly(2).of(sftpConfiguration).getChunking(); will(returnValue(Boolean.FALSE));
                exactly(2).of(sftpConfiguration).getChunkSize(); will(returnValue(Integer.valueOf(-1)));
                exactly(2).of(sftpConfiguration).getChecksum(); will(returnValue(Boolean.FALSE));
                exactly(2).of(sftpConfiguration).getMinAge(); will(returnValue(Long.valueOf(1)));
                exactly(2).of(sftpConfiguration).getDestructive(); will(returnValue(Boolean.TRUE));
                exactly(2).of(sftpConfiguration).getFilterDuplicates(); will(returnValue(Boolean.TRUE));
                exactly(2).of(sftpConfiguration).getFilterOnFilename(); will(returnValue(Boolean.TRUE));
                exactly(2).of(sftpConfiguration).getFilterOnLastModifiedDate(); will(returnValue(Boolean.TRUE));
                exactly(2).of(sftpConfiguration).getChronological(); will(returnValue(Boolean.TRUE));

                one(fileTransferConnectionTemplate).getDiscoveredFile("sourceDirectory", "filenamePattern", 
                        true, "renameExtention", false, "moveOnSuccessNewPath", false, -1, 
                        false, 1, true, true, true, true, true); will(throwException(exception));

                one(mockAlternateConnectionTemplate).getDiscoveredFile("sourceDirectory", "filenamePattern", 
                        true, "renameExtention", false, "moveOnSuccessNewPath", false, -1, 
                        false, 1, true, true, true, true, true); will(throwException(exception));
            }
        });

        // Test
        ((PayloadBasedSftpConsumer)this.payloadBasedSftpConsumer).setAlternateFileTransferConnectionTemplate(mockAlternateConnectionTemplate);
        try
        {
            this.payloadBasedSftpConsumer.invoke();
        }
        catch (ResourceException e)
        {
            Assert.assertEquals(mockAlternateConnectionTemplate, ((PayloadBasedSftpConsumer)this.payloadBasedSftpConsumer).getActiveFileTransferConnectionTemplate());
            try
            {
                this.payloadBasedSftpConsumer.invoke();
            }
            catch (ResourceException e2)
            {
                Assert.assertEquals(this.fileTransferConnectionTemplate, ((PayloadBasedSftpConsumer)this.payloadBasedSftpConsumer).getActiveFileTransferConnectionTemplate());
                throw e2;
            }
        }
        this.mockery.assertIsSatisfied();
        Assert.fail("Unreachable code.");
    }
}
