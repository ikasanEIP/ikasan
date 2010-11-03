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

import junit.framework.TestCase;

/**
 * Base Test class for IO testing
 * 
 * @author Ikasan Development Team
 */
public abstract class IoJUnitHelper extends TestCase
{

    /**
     * Assert that all of the bytes are equal
     * 
     * @param message
     * @param expected What each byte should be
     * @param cs
     */
    protected void assertAllBytesEqual(String message, byte expected, byte[] cs)
    {
        for (int i = 0; i < cs.length; i++)
        {
            byte b = cs[i];
            assertEquals(message, expected, b);
        }
    }

    /**
     * Assert that the chunk handler got the chunks correct
     * 
     * @param chunkHandler
     * @param sequenceLength
     */
    protected void assertChunkHandlerResults(MockChunkHandler chunkHandler, int sequenceLength)
    {
        // assert that the handler get the chunks right
        assertEquals("chunk handler should have collected 11 chunks", sequenceLength, chunkHandler.getChunks().size()); //$NON-NLS-1$

        // assert that each of the first 10 chunks only contains bytes that
        // equal that chunk's ordinal
        for (int i = 0; i < 10; i++)
        {
            assertAllBytesEqual("byte should equal", (byte) i, chunkHandler.getChunks().get(i)); //$NON-NLS-1$
        }

        // check that the last chunk is only 1 byte long and contains a 1
        byte[] lastChunk = chunkHandler.getChunks().get(10);
        assertEquals("last chunk size should be 1 byte", 1, lastChunk.length); //$NON-NLS-1$
        assertEquals("single byte in last chunk should be 1, as per the test data", (byte) 1, lastChunk[0]); //$NON-NLS-1$
    }

}
