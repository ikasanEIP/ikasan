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

import java.io.IOException;

import org.ikasan.connector.util.chunking.ChunkTestUtils;
import org.ikasan.connector.util.chunking.process.ChunkHandleException;
import org.ikasan.connector.util.chunking.process.ChunkHandler;

/**
 * Test for ChunkingOutputStream
 * 
 * @author Ikasan Development Team
 */
public class ChunkingOutputStreamTest extends IoJUnitHelper
{

    /**
     * tests the write method
     * 
     * @throws IOException
     */
    public void testWriteInt() throws IOException
    {
        MockChunkHandler chunkHandler = new MockChunkHandler();
        int chunkSize = 100;
        int sequenceLength = 11;

        ChunkingOutputStream cos = new ChunkingOutputStream(chunkSize, chunkHandler, sequenceLength, 0);

        byte[] unchunkedArray = ChunkTestUtils.getTestData();

        for (int i = 0; i < unchunkedArray.length; i++)
        {
            byte b = unchunkedArray[i];
            cos.write(b);
        }
        cos.flush();

        assertChunkHandlerResults(chunkHandler, sequenceLength);

    }

    /**
     * tests that handle exceptions are caught and properly wrapped
     */
    public void testWriteInt_catchingHandleException()
    {

        String exceptionMessage = "Houston, we have a problem"; //$NON-NLS-1$
        ChunkHandleException chunkHandleException = new ChunkHandleException(exceptionMessage);

        ChunkHandler mockChunkHandler = new ExceptionThrowingMockChunkHandler(chunkHandleException);
        int chunkSize = 10;
        int sequenceLength = 11;

        ChunkingOutputStream cos = new ChunkingOutputStream(chunkSize, mockChunkHandler, sequenceLength, 0);

        byte[] unchunkedArray = ChunkTestUtils.getTestData();

        try
        {
            for (int i = 0; i < unchunkedArray.length; i++)
            {
                byte b = unchunkedArray[i];
                cos.write(b);
            }
            fail("should have thrown IOException when underlying handler throws handler exception"); //$NON-NLS-1$
        }
        catch (IOException e)
        {
            assertEquals("underlying exception should be the chunkHandleException", chunkHandleException.getMessage(), //$NON-NLS-1$
                e.getMessage());
        }

        try
        {
            cos.flush();
            fail("should have thrown IOException when underlying handler throws handler exception"); //$NON-NLS-1$
        }
        catch (IOException e)
        {
            assertEquals("underlying exception should be the chunkHandleException", chunkHandleException.getMessage(), //$NON-NLS-1$
                e.getMessage());
        }

    }
}
