/*
 * $Id: ChunkingInputStreamConsumerTest.java 16767 2009-04-23 12:37:52Z mitcje $
 * $URL: svn+ssh://svc-vcsp/architecture/ikasan/trunk/connector-basefiletransfer/src/test/java/org/ikasan/connector/util/chunking/io/ChunkingInputStreamConsumerTest.java $
 * 
 * ====================================================================
 * Ikasan Enterprise Integration Platform
 * Copyright (c) 2003-2008 Mizuho International plc. and individual contributors as indicated
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the 
 * Free Software Foundation Europe e.V. Talstrasse 110, 40217 Dusseldorf, Germany 
 * or see the FSF site: http://www.fsfeurope.org/.
 * ====================================================================
 */
package org.ikasan.connector.util.chunking.io;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.ikasan.connector.util.chunking.ChunkTestUtils;
import org.ikasan.connector.util.chunking.process.ChunkHandleException;
import org.ikasan.connector.util.chunking.process.ChunkHandler;
import org.ikasan.connector.util.chunking.provider.ChunkableDataSourceException;

/**
 * Tests the ChunkingInputStreamConsumer class
 * 
 * @author Ikasan Development Team
 */
public class ChunkingInputStreamConsumerTest extends IoJUnitHelper
{

    /**
     * tests the consumeInputStream method
     * 
     * @throws ChunkableDataSourceException
     */
    public void testConsumeInputStream() throws ChunkableDataSourceException
    {
        MockChunkHandler mockChunkHandler = new MockChunkHandler();
        ChunkingInputStreamConsumer inputStreamConsumer = new ChunkingInputStreamConsumer(mockChunkHandler);

        InputStream inputStream = new ByteArrayInputStream(ChunkTestUtils.getTestData());
        int chunkSize = 100;
        int sequenceLength = 11;

        inputStreamConsumer.consumeInputStream(inputStream, chunkSize, sequenceLength);

        assertChunkHandlerResults(mockChunkHandler, sequenceLength);
    }

    /**
     * Test to ensure that IOExceptions thrown by underlying InputStream are
     * caught, wrapped and thrown as ChunkableDataSourceException
     */
    public void testConsumeInputStream_catchingIoException()
    {
        MockChunkHandler mockChunkHandler = new MockChunkHandler();
        ChunkingInputStreamConsumer inputStreamConsumer = new ChunkingInputStreamConsumer(mockChunkHandler);

        int chunkSize = 100;
        int sequenceLength = 11;

        try
        {
            inputStreamConsumer.consumeInputStream(new MockInputStream(), chunkSize, sequenceLength);
            fail("should have thrown ChunkableDataSourceException when underlying InputStream throws IOException"); //$NON-NLS-1$
        }
        catch (ChunkableDataSourceException e)
        {
            assertTrue(e.getCause() instanceof IOException);
        }
    }

    /**
     * Test to ensure that any chunk handling exceptions thrown by underlying
     * chunk handler are caught, wrapped and thrown as
     * ChunkableDataSourceException
     */
    public void testConsumeInputStream_catchingChunkHandleException()
    {

        final ChunkHandleException chunkHandleException = new ChunkHandleException("Houston, we have a problem"); //$NON-NLS-1$

        ChunkHandler mockHandler = new ExceptionThrowingMockChunkHandler(chunkHandleException);
        ChunkingInputStreamConsumer inputStreamConsumer = new ChunkingInputStreamConsumer(mockHandler);

        InputStream inputStream = new ByteArrayInputStream(ChunkTestUtils.getTestData());

        int chunkSize = 100;
        int sequenceLength = 11;

        try
        {
            inputStreamConsumer.consumeInputStream(inputStream, chunkSize, sequenceLength);
            fail("should have thrown ChunkableDataSourceException when underlying InputStream throws IOException"); //$NON-NLS-1$
        }
        catch (ChunkableDataSourceException e)
        {
            assertEquals("underlying exception should be the chunkHandleException", chunkHandleException, e.getCause()); //$NON-NLS-1$
        }
    }

    /**
     * Mock InputStream to throw an IOException on read
     * 
     * @author duncro
     * 
     */
    class MockInputStream extends InputStream
    {

        @Override
        public int read() throws IOException
        {
            throw new IOException();
        }

    }

}
