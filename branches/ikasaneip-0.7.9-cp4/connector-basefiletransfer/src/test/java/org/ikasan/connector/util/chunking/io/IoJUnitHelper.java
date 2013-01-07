/*
 * $Id$
 * $URL$
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
