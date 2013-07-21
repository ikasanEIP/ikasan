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
     * @author Ikasan Development Team
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
