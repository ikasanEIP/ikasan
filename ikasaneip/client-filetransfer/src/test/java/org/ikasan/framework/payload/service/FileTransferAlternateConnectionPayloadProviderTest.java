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
package org.ikasan.framework.payload.service;

import static org.junit.Assert.*;

import java.util.List;

import javax.resource.ResourceException;
import javax.resource.cci.ConnectionSpec;

import org.ikasan.common.Payload;
import org.ikasan.connector.base.outbound.EISConnectionFactory;
import org.ikasan.connector.basefiletransfer.outbound.BaseFileTransferConnection;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Test class for <code>FileTransferAlternateConnectionPayloadProvider</code>
 * 
 * @author Ikasan Development Team
 *
 */
public class FileTransferAlternateConnectionPayloadProviderTest 
{
    /** Mockery instance */
    private Mockery mockery = new Mockery();
    /** Mocked Payload */
    Payload payload = mockery.mock(Payload.class);
    /** Mocked BaseFileTransferConnection */
    BaseFileTransferConnection fileTransferConnection = mockery
        .mock(BaseFileTransferConnection.class);
    /** Mocked ConnectionSpec */
    ConnectionSpec connectionSpec = mockery.mock(ConnectionSpec.class);
    /** Mocked Alternate ConnectionSpec */
    ConnectionSpec alternateConnectionSpec = mockery.mock(ConnectionSpec.class);
    /** Mocked EISConnectionFactory */
    EISConnectionFactory connectionFactory = 
        mockery.mock(EISConnectionFactory.class);
    /** SourceDir */
    String sourceDir = "sourceDir";
    /** filenamePattern */
    String filenamePattern = "filenamePattern";

    /**
     * Setup runs before each test
     */
    @Before
    public void setUp()
    {
        // nothing to do here
    }

    /**
     * Testing getNextRelatedPayloads method with moveOnSuccess,
     * renameOnSuccess, and destructive, all set to false. This path is expected
     * to not throw any exceptions.
     * 
     * @throws ResourceException if getting payload fails
     */
    @Test
    public void testGetNextRelatedPayloads_FalseAllFlags()
            throws ResourceException
    {
        FileTransferAlternateConnectionPayloadProvider payloadProvider = 
            new FileTransferAlternateConnectionPayloadProvider(
            sourceDir, filenamePattern, connectionFactory, connectionSpec, 
            alternateConnectionSpec);
        
        final boolean move = false;
        final boolean rename = false;
        final boolean destructive = false;
        final String renameExtention = null;
        final String newPath = null;
        payloadProvider.setMoveOnSuccess(move);
        payloadProvider.setRenameOnSuccess(rename);
        payloadProvider.setDestructive(destructive);
        payloadProvider.setMoveOnSuccessNewPath(newPath);
        payloadProvider.setRenameOnSuccessExtension(renameExtention);
        mockery.checking(new Expectations()
        {
            {
                exactly(1).of(connectionFactory).getConnection(with(any(ConnectionSpec.class)));
                will(returnValue(fileTransferConnection));
                one(fileTransferConnection).getDiscoveredFile(sourceDir,
                    filenamePattern, rename, renameExtention, move, newPath,
                    false, 1048576, false, 120, destructive, true,
                    true, true, false); // call the connector
                will(returnValue(payload));
                one(fileTransferConnection).close();
            }
        });
        List<Payload> nextRelatedPayloads = payloadProvider
            .getNextRelatedPayloads();
        assertEquals("returned Payload should be that from connection",
            payload, nextRelatedPayloads.get(0));
        assertEquals(
            "The destructive flag is expected to be false.", payloadProvider.isDestructive(), destructive);//$NON-NLS-1$
        assertEquals(
            "The renameOnSuccess flag is expected to be false.", payloadProvider.isRenameOnSuccess(), rename);//$NON-NLS-1$
        assertEquals(
            "The moveOnSuccess flag is expected to be false.", payloadProvider.isMoveOnSuccess(), move);//$NON-NLS-1$
        mockery.assertIsSatisfied();
    }

    /**
     * Testing getNextRelatedPayloads method with only renameOnSuccess set to
     * true. This path is expected to not throw any exceptions.
     * 
     * @throws ResourceException because renameOnSuccessExtention is null
     */
    @Test
    public void testGetNextRelatedPayloads_TrueRename()
            throws ResourceException
    {
        FileTransferAlternateConnectionPayloadProvider payloadProvider = 
            new FileTransferAlternateConnectionPayloadProvider(
                sourceDir, filenamePattern, connectionFactory, connectionSpec, 
                alternateConnectionSpec);

        final boolean move = false;
        final boolean rename = true;
        final boolean destructive = false;
        final String renameExtention = ".rename"; //$NON-NLS-1$
        final String newPath = null;
        payloadProvider.setMoveOnSuccess(move);
        payloadProvider.setRenameOnSuccess(rename);
        payloadProvider.setDestructive(destructive);
        payloadProvider.setMoveOnSuccessNewPath(newPath);
        payloadProvider.setRenameOnSuccessExtension(renameExtention);
        mockery.checking(new Expectations()
        {
            {
                one(connectionFactory).getConnection(with(any(ConnectionSpec.class)));
                will(returnValue(fileTransferConnection));
                one(fileTransferConnection).getDiscoveredFile(sourceDir,
                    filenamePattern, rename, renameExtention, move, newPath,
                    false, 1048576, false, 120, destructive, true,
                    true, true, false); // call the connector
                will(returnValue(payload));
                one(fileTransferConnection).close();
            }
        });
        assertEquals("returned Payload should be that from connection",
            payload, payloadProvider.getNextRelatedPayloads().get(0));
        assertEquals(
            "The destructive flag is expected to be false.", payloadProvider.isDestructive(), destructive);//$NON-NLS-1$
        assertEquals(
            "The renameOnSuccess flag is expected to be true.", payloadProvider.isRenameOnSuccess(), rename);//$NON-NLS-1$
        assertEquals(
            "The moveOnSuccess flag is expected to be false.", payloadProvider.isMoveOnSuccess(), move);//$NON-NLS-1$
        mockery.assertIsSatisfied();
    }

    /**
     * Testing getNextRelatedPayloads method with only destructive set to true.
     * This path is expected to not throw any exceptions.
     * 
     * @throws ResourceException when performing execute
     */
    @Test
    public void testGetNextRelatedPayloads_TrueDestructive()
            throws ResourceException
    {
        FileTransferAlternateConnectionPayloadProvider payloadProvider = 
            new FileTransferAlternateConnectionPayloadProvider(
                sourceDir, filenamePattern, connectionFactory, connectionSpec, 
                alternateConnectionSpec);

        final boolean move = false;
        final boolean rename = false;
        final boolean destructive = true;
        final String renameExtention = null;
        final String newPath = null;
        payloadProvider.setMoveOnSuccess(move);
        payloadProvider.setRenameOnSuccess(rename);
        payloadProvider.setDestructive(destructive);
        payloadProvider.setMoveOnSuccessNewPath(newPath);
        payloadProvider.setRenameOnSuccessExtension(renameExtention);
        mockery.checking(new Expectations()
        {
            {
                one(connectionFactory).getConnection(with(any(ConnectionSpec.class)));
                will(returnValue(fileTransferConnection));
                one(fileTransferConnection).getDiscoveredFile(sourceDir,
                    filenamePattern, rename, renameExtention, move, newPath,
                    false, 1048576, false, 120, destructive, true,
                    true, true, false); // call the connector
                will(returnValue(payload));
                one(fileTransferConnection).close();
            }
        });
        assertEquals("returned Payload should be that from connection",
            payload, payloadProvider.getNextRelatedPayloads().get(0));
        assertEquals(
            "The destructive flag is expected to be true.", payloadProvider.isDestructive(), destructive);//$NON-NLS-1$
        assertEquals(
            "The renameOnSuccess flag is expected to be false.", payloadProvider.isRenameOnSuccess(), rename);//$NON-NLS-1$
        assertEquals(
            "The moveOnSuccess flag is expected to be false.", payloadProvider.isMoveOnSuccess(), move);//$NON-NLS-1$
        mockery.assertIsSatisfied();
    }

    /**
     * Testing getNextRelatedPayloads method with only moveOnSuccess set to
     * true. This path is expected to not throw any exceptions.
     * 
     * @throws ResourceException when performing execute
     */
    @Test
    public void testGetNextRelatedPayloads_TrueMove() throws ResourceException
    {
        FileTransferAlternateConnectionPayloadProvider payloadProvider = 
            new FileTransferAlternateConnectionPayloadProvider(
                sourceDir, filenamePattern, connectionFactory, connectionSpec, 
                alternateConnectionSpec);

        final boolean move = true;
        final boolean rename = false;
        final boolean destructive = false;
        final String renameExtention = null;
        final String newPath = "/archDir"; //$NON-NLS-1$
        payloadProvider.setMoveOnSuccess(move);
        payloadProvider.setRenameOnSuccess(rename);
        payloadProvider.setDestructive(destructive);
        payloadProvider.setMoveOnSuccessNewPath(newPath);
        payloadProvider.setRenameOnSuccessExtension(renameExtention);
        mockery.checking(new Expectations()
        {
            {
                one(connectionFactory).getConnection(with(any(ConnectionSpec.class)));
                will(returnValue(fileTransferConnection));
                one(fileTransferConnection).getDiscoveredFile(sourceDir,
                    filenamePattern, rename, renameExtention, move, newPath,
                    false, 1048576, false, 120, destructive, true,
                    true, true, false); // call the connector
                will(returnValue(payload));
                one(fileTransferConnection).close();
            }
        });
        assertEquals("returned Payload should be that from connection",
            payload, payloadProvider.getNextRelatedPayloads().get(0));
        assertEquals(
            "The destructive flag is expected to be false.", payloadProvider.isDestructive(), destructive);//$NON-NLS-1$
        assertEquals(
            "The renameOnSuccess flag is expected to be false.", payloadProvider.isRenameOnSuccess(), rename);//$NON-NLS-1$
        assertEquals(
            "The moveOnSuccess flag is expected to be true.", payloadProvider.isMoveOnSuccess(), move);//$NON-NLS-1$
        mockery.assertIsSatisfied();
    }

    /**
     * Testing getNextRelatedPayloads method with moveOnSuccess,
     * renameOnSuccess, and destructive all set to true. This path is expected
     * to not throw any exceptions.
     */
    @Test
    public void testGetNextRelatedPayloads_TrueRenameAndMoveAndDestructive()
    {
        FileTransferAlternateConnectionPayloadProvider payloadProvider = 
            new FileTransferAlternateConnectionPayloadProvider(
                sourceDir, filenamePattern, connectionFactory, connectionSpec, 
                alternateConnectionSpec);

        boolean move = true;
        boolean rename = true;
        boolean destructive = true;
        String renameExtention = null;
        String newPath = null;
        payloadProvider.setMoveOnSuccess(move);
        payloadProvider.setRenameOnSuccess(rename);
        payloadProvider.setDestructive(destructive);
        payloadProvider.setMoveOnSuccessNewPath(renameExtention);
        payloadProvider.setRenameOnSuccessExtension(newPath);
        ResourceException exception = null;
        try
        {
            payloadProvider.getNextRelatedPayloads();
        }
        catch (ResourceException e)
        {
            exception = e;
        }
        assertNotNull(
            "When moveOnSuccess, renameOnSuccess, and destructive are all true,an IllegalArgumentException should be thrown.", exception); //$NON-NLS-1$
    }

    /**
     * Testing getNextRelatedPayloads method with renameOnSuccess true but a
     * null renameOnSuccessExtention Expected to throw a ResourceException.
     */
    @SuppressWarnings("null")
    @Test
    public void testGetNextRelatedPayloads_RenameNullExtention()
    {
        FileTransferAlternateConnectionPayloadProvider payloadProvider = 
            new FileTransferAlternateConnectionPayloadProvider(
                sourceDir, filenamePattern, connectionFactory, connectionSpec, 
                alternateConnectionSpec);

        boolean move = false;
        boolean rename = true;
        boolean destructive = false;
        String renameExtention = null;
        String newPath = null;
        payloadProvider.setMoveOnSuccess(move);
        payloadProvider.setRenameOnSuccess(rename);
        payloadProvider.setDestructive(destructive);
        payloadProvider.setMoveOnSuccessNewPath(renameExtention);
        payloadProvider.setRenameOnSuccessExtension(newPath);
        ResourceException exception = null;
        try
        {
            payloadProvider.getNextRelatedPayloads();
        }
        catch (ResourceException e)
        {
            exception = e;
        }
        assertTrue(exception.getMessage().equals(
            "renameExtension has not been configured.")); //$NON-NLS-1$
    }

    /**
     * Testing getNextRelatedPayloads method with moveOnSuccess true but a null
     * moveOnSuccessNewPath Expected to throw a ResourceException.
     */
    @SuppressWarnings("null")
    @Test
    public void testGetNextRelatedPayloads_MoveNullNewPath()
    {
        FileTransferAlternateConnectionPayloadProvider payloadProvider = 
            new FileTransferAlternateConnectionPayloadProvider(
                sourceDir, filenamePattern, connectionFactory, connectionSpec, 
                alternateConnectionSpec);

        boolean move = true;
        boolean rename = false;
        boolean destructive = false;
        String renameExtention = null;
        String newPath = null;
        payloadProvider.setMoveOnSuccess(move);
        payloadProvider.setRenameOnSuccess(rename);
        payloadProvider.setDestructive(destructive);
        payloadProvider.setMoveOnSuccessNewPath(newPath);
        payloadProvider.setRenameOnSuccessExtension(renameExtention);
        ResourceException exception = null;
        try
        {
            payloadProvider.getNextRelatedPayloads();
        }
        catch (ResourceException e)
        {
            exception = e;
        }
        assertTrue(exception.getMessage().equals(
            "moveOnSucccessNewPath has not been configured.")); //$NON-NLS-1$
    }

    /**
     * Testing getNextRelatedPayloads method with both moveOnSuccess and
     * renameOnSuccess set to true. Expected to throw an
     * IllegalArgumentException
     */
    @SuppressWarnings("null")
    @Test
    public void testGetNextRelatedPayloads_TrueMoveAndRename()
    {
        FileTransferAlternateConnectionPayloadProvider payloadProvider = 
            new FileTransferAlternateConnectionPayloadProvider(
                sourceDir, filenamePattern, connectionFactory, connectionSpec, 
                alternateConnectionSpec);

        boolean move = true;
        boolean rename = true;
        boolean destructive = false;
        String renameExtention = null;
        String newPath = null;
        payloadProvider.setMoveOnSuccess(move);
        payloadProvider.setRenameOnSuccess(rename);
        payloadProvider.setDestructive(destructive);
        payloadProvider.setMoveOnSuccessNewPath(newPath);
        payloadProvider.setRenameOnSuccessExtension(renameExtention);
        ResourceException exception = null;
        try
        {
            payloadProvider.getNextRelatedPayloads();
        }
        catch (ResourceException e)
        {
            exception = e;
        }
        assertTrue(exception.getMessage().equals(
            "Moving the file and renaming it are mutually exclusive.")); //$NON-NLS-1$
    }

    /**
     * Testing getNextRelatedPayloads method with both moveOnSuccess and
     * destructive set to true. Expected to throw an IllegalArgumentExcepton
     */
    @SuppressWarnings("null")
    @Test
    public void testGetNextRelatedPayloads_TrueMoveAndDestrucive()
    {
        FileTransferAlternateConnectionPayloadProvider payloadProvider = 
            new FileTransferAlternateConnectionPayloadProvider(
                sourceDir, filenamePattern, connectionFactory, connectionSpec, 
                alternateConnectionSpec);

        boolean move = true;
        boolean rename = false;
        boolean destructive = true;
        String renameExtention = null;
        String newPath = null;
        payloadProvider.setMoveOnSuccess(move);
        payloadProvider.setRenameOnSuccess(rename);
        payloadProvider.setDestructive(destructive);
        payloadProvider.setMoveOnSuccessNewPath(newPath);
        payloadProvider.setRenameOnSuccessExtension(renameExtention);
        ResourceException exception = null;
        try
        {
            payloadProvider.getNextRelatedPayloads();
        }
        catch (ResourceException e)
        {
            exception = e;
        }
        assertTrue(exception.getMessage().equals(
            "Moving the file and Get Destructive are mutually exclusive.")); //$NON-NLS-1$
    }

    /**
     * Testing getNextRelatedPayloads method with both renameOnSuccess and
     * destructive set to true. Expected to throw an IllegalArgumentException.
     */
    @SuppressWarnings("null")
    @Test
    public void testGetNextRelatedPayloads_TrueRenameAndDestrucive()
    {
        FileTransferAlternateConnectionPayloadProvider payloadProvider = 
            new FileTransferAlternateConnectionPayloadProvider(
                sourceDir, filenamePattern, connectionFactory, connectionSpec, 
                alternateConnectionSpec);

        boolean move = false;
        boolean rename = true;
        boolean destructive = true;
        String renameExtention = null;
        String newPath = null;
        payloadProvider.setMoveOnSuccess(move);
        payloadProvider.setRenameOnSuccess(rename);
        payloadProvider.setDestructive(destructive);
        payloadProvider.setMoveOnSuccessNewPath(newPath);
        payloadProvider.setRenameOnSuccessExtension(renameExtention);
        ResourceException exception = null;
        try
        {
            payloadProvider.getNextRelatedPayloads();
        }
        catch (ResourceException e)
        {
            exception = e;
        }
        assertTrue(exception.getMessage().equals(
            "RenameOnSuccess and Get Destructive are mutually exclusive.")); //$NON-NLS-1$
    }

    /**
     * Tests the function of the execute method when the connection does not
     * return a payload
     * 
     * @throws ResourceException -
     */
    @Test
    public void testGetNextRelatedPayloads_ReturningNull()
            throws ResourceException
    {
        mockery.checking(new Expectations()
        {
            {
                one(connectionFactory).getConnection(with(any(ConnectionSpec.class)));
                will(returnValue(fileTransferConnection));
                one(fileTransferConnection).getDiscoveredFile(sourceDir,
                    filenamePattern, false, null, false, null, false, 1048576,
                    false, 120, false, true, true, true, false); // call
                                                                        // the
                                                                        // connector
                will(returnValue(null));
                one(fileTransferConnection).close();
            }
        });

        FileTransferAlternateConnectionPayloadProvider payloadProvider = 
            new FileTransferAlternateConnectionPayloadProvider(
                sourceDir, filenamePattern, connectionFactory, connectionSpec, 
                alternateConnectionSpec);

        assertNull(
            "should return null, when the connection returns no Payloads",
            payloadProvider.getNextRelatedPayloads());
        mockery.assertIsSatisfied();
    }

    // TODO - how to test the private switchActiveConnection method is successful?

    /**
     * Test the isFilterDuplicates method call
     */
    @Test
    public void testIsFilterDuplicates()
    {
        FileTransferAlternateConnectionPayloadProvider payloadProvider = 
            new FileTransferAlternateConnectionPayloadProvider(
                null, null, connectionFactory, connectionSpec, 
                alternateConnectionSpec);

        payloadProvider.setFilterDuplicates(true);
        assertTrue(payloadProvider.isFilterDuplicates());
        payloadProvider.setFilterDuplicates(false);
        assertFalse(payloadProvider.isFilterDuplicates());
    }

    /**
     * Test the isFilterOnFilename method call
     */
    @Test
    public void testIsFilterOnFilename()
    {
        FileTransferAlternateConnectionPayloadProvider payloadProvider = 
            new FileTransferAlternateConnectionPayloadProvider(
                null, null, connectionFactory, connectionSpec, 
                alternateConnectionSpec);

        payloadProvider.setFilterOnFilename(true);
        assertTrue(payloadProvider.isFilterOnFilename());
        payloadProvider.setFilterOnFilename(false);
        assertFalse(payloadProvider.isFilterOnFilename());
    }

    /**
     * Test the isFilterOnLastModifiedDate method call
     */
    @Test
    public void testIsFilterOnLastModifiedDate()
    {
        FileTransferAlternateConnectionPayloadProvider payloadProvider = 
            new FileTransferAlternateConnectionPayloadProvider(
                null, null, connectionFactory, connectionSpec, 
                alternateConnectionSpec);

        payloadProvider.setFilterOnLastModifiedDate(true);
        assertTrue(payloadProvider.isFilterOnLastModifiedDate());
        payloadProvider.setFilterOnLastModifiedDate(false);
        assertFalse(payloadProvider.isFilterOnLastModifiedDate());
    }

    /**
     * Test the isChunking method call
     */
    @Test
    public void testIsChunking()
    {
        FileTransferAlternateConnectionPayloadProvider payloadProvider = 
            new FileTransferAlternateConnectionPayloadProvider(
                null, null, connectionFactory, connectionSpec, 
                alternateConnectionSpec);

        payloadProvider.setChunking(true);
        assertTrue(payloadProvider.isChunking());
        payloadProvider.setChunking(false);
        assertFalse(payloadProvider.isChunking());
    }

    /**
     * Test the isChecksum method call
     */
    @Test
    public void testIsChecksum()
    {
        FileTransferAlternateConnectionPayloadProvider payloadProvider = 
            new FileTransferAlternateConnectionPayloadProvider(
                null, null, connectionFactory, connectionSpec, 
                alternateConnectionSpec);

        payloadProvider.setChecksum(true);
        assertTrue(payloadProvider.isChecksum());
        payloadProvider.setChecksum(false);
        assertFalse(payloadProvider.isChecksum());
    }

    /**
     * Test the getRenameOnSuccessExtension method call
     */
    @Test
    public void testGetRenameOnSuccessExtension()
    {
        String renameOnSuccessExtension = ".blah";
        FileTransferAlternateConnectionPayloadProvider payloadProvider = 
            new FileTransferAlternateConnectionPayloadProvider(
                null, null, connectionFactory, connectionSpec, 
                alternateConnectionSpec);

        payloadProvider.setRenameOnSuccessExtension(renameOnSuccessExtension);
        assertEquals(renameOnSuccessExtension, payloadProvider
            .getRenameOnSuccessExtension());
    }

    /**
     * Test the getMoveOnSuccessNewPath method call
     */
    @Test
    public void testGetMoveOnSuccessNewPath()
    {
        String moveOnSuccessNewPath = ".blah";
        FileTransferAlternateConnectionPayloadProvider payloadProvider = 
            new FileTransferAlternateConnectionPayloadProvider(
                null, null, connectionFactory, connectionSpec, 
                alternateConnectionSpec);

        payloadProvider.setMoveOnSuccessNewPath(moveOnSuccessNewPath);
        assertEquals(moveOnSuccessNewPath, payloadProvider
            .getMoveOnSuccessNewPath());
    }

    /**
     * Test the getChunkSize method call
     */
    @Test
    public void testGetChunkSize()
    {
        int chunkSize = 99;
        FileTransferAlternateConnectionPayloadProvider payloadProvider = 
            new FileTransferAlternateConnectionPayloadProvider(
                null, null, connectionFactory, connectionSpec, 
                alternateConnectionSpec);

        payloadProvider.setChunkSize(chunkSize);
        assertEquals(chunkSize, payloadProvider.getChunkSize());
    }

    /**
     * Test the getMinAge method call
     */
    @Test
    public void testGetMinAge()
    {
        int minAge = 99;
        FileTransferAlternateConnectionPayloadProvider payloadProvider = 
            new FileTransferAlternateConnectionPayloadProvider(
                null, null, connectionFactory, connectionSpec, 
                alternateConnectionSpec);

        payloadProvider.setMinAge(minAge);
        assertEquals(minAge, payloadProvider.getMinAge());
    }

    /**
     * Test the testGetSrcDirectory method call
     */
    @Test
    public void testGetSrcDirectory()
    {
        FileTransferAlternateConnectionPayloadProvider payloadProvider = 
            new FileTransferAlternateConnectionPayloadProvider(
                sourceDir, null, connectionFactory, connectionSpec, 
                alternateConnectionSpec);

        assertEquals(sourceDir, payloadProvider.getSrcDirectory());
    }

    /**
     * Test the getFilenamePattern method call
     */
    @Test
    public void testGetFilenamePattern()
    {
        FileTransferAlternateConnectionPayloadProvider payloadProvider = 
            new FileTransferAlternateConnectionPayloadProvider(
                null, filenamePattern, connectionFactory, connectionSpec, 
                alternateConnectionSpec);

        assertEquals(filenamePattern, payloadProvider.getFilenamePattern());
    }

    /**
     * test the getFileTransferConnectionTemplate method call
     */
    @Test
    public void testGetFileTransferConnectionTemplate()
    {
        FileTransferAlternateConnectionPayloadProvider payloadProvider = 
            new FileTransferAlternateConnectionPayloadProvider(
                null, null, connectionFactory, connectionSpec, 
                alternateConnectionSpec);

        assertNotNull(payloadProvider.getFileTransferConnectionTemplate());
    }
    
    /**
     * Test the setMaxRows and getMaxRows method calls
     */
    @Test
    public void testSetAndGetMaxRows()
    {
        int maxRows = 99;
        FileTransferAlternateConnectionPayloadProvider payloadProvider = 
            new FileTransferAlternateConnectionPayloadProvider(
                null, null, connectionFactory, connectionSpec, 
                alternateConnectionSpec);

        // Make sure the default is correct
        assertEquals(-1, payloadProvider.getMaxRows());
        payloadProvider.setMaxRows(maxRows);
        assertEquals(maxRows, payloadProvider.getMaxRows());
    }

    /**
     * Test the setAgeOfFiles and getAgeOfFiles method calls
     */
    @Test
    public void testSetAndGetAgeOfFiles()
    {
        int ageOfFiles = 99;
        FileTransferAlternateConnectionPayloadProvider payloadProvider = 
            new FileTransferAlternateConnectionPayloadProvider(
                null, null, connectionFactory, connectionSpec, 
                alternateConnectionSpec);

        // Make sure the default is correct
        assertEquals(-1, payloadProvider.getAgeOfFiles());
        payloadProvider.setAgeOfFiles(ageOfFiles);
        assertEquals(ageOfFiles, payloadProvider.getAgeOfFiles());
    }
    
    /**
     * Teardown after each test
     */
    @After
    public void tearDown()
    {
        // check all expectations were satisfied
        mockery.assertIsSatisfied();
    }
}
