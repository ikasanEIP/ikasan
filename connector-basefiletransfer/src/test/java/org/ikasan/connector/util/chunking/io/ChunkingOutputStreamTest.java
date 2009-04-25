/*
 * $Id: ChunkingOutputStreamTest.java 16767 2009-04-23 12:37:52Z mitcje $
 * $URL: svn+ssh://svc-vcsp/architecture/ikasan/trunk/connector-basefiletransfer/src/test/java/org/ikasan/connector/util/chunking/io/ChunkingOutputStreamTest.java $
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
