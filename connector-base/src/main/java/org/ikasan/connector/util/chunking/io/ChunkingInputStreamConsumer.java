/*
 * $Id: ChunkingInputStreamConsumer.java 16756 2009-04-22 12:35:57Z mitcje $
 * $URL: svn+ssh://svc-vcsp/architecture/ikasan/trunk/connector-base/src/main/java/org/ikasan/connector/util/chunking/io/ChunkingInputStreamConsumer.java $
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
import java.io.InputStream;

import org.ikasan.connector.util.chunking.process.ChunkHandleException;
import org.ikasan.connector.util.chunking.process.ChunkHandler;
import org.ikasan.connector.util.chunking.provider.ChunkableDataSourceException;

/**
 * InputStream consumer class that chunks the data from the InputStream into
 * regular chunks, and passes each off to a <code>ChunkHandler</code> to
 * process
 * 
 * @author Ikasan Development Team
 * 
 */
public class ChunkingInputStreamConsumer
{

    /**
     * Handler to process newly separated chunk
     */
    private ChunkHandler chunkHandler;

    /**
     * Constructor
     * 
     * @param chunkHandler
     */
    public ChunkingInputStreamConsumer(ChunkHandler chunkHandler)
    {
        super();
        this.chunkHandler = chunkHandler;
    }

    /**
     * Consumes the input stream, chunking into regular sized chunks (allowing
     * for a smaller remainder) passing each off to a handler for further
     * processing
     * 
     * @param inputStream
     * @param chunkSize
     * @param noOfChunks
     * @throws ChunkableDataSourceException
     */
    public void consumeInputStream(InputStream inputStream, int chunkSize, long noOfChunks)
            throws ChunkableDataSourceException
    {

        if (inputStream == null)
        {
            return;
        }
        try
        {
            boolean streamClosed = false;

            long chunkCount = 0;

            while (!streamClosed)
            {

                int bytesRead = 0;
                int bytesToRead = chunkSize;
                byte[] input = new byte[bytesToRead];
                while (bytesRead < bytesToRead)
                {
                    int result = inputStream.read(input, bytesRead, bytesToRead - bytesRead);
                    if (result == -1)
                    {
                        streamClosed = true;
                        break;
                    }
                    bytesRead += result;
                }

                if (streamClosed)
                {
                    // return a smaller array of only the bytes that were
                    // retrieved
                    byte[] smaller = new byte[bytesRead];
                    System.arraycopy(input, 0, smaller, 0, bytesRead);
                    input = smaller;
                }
                byte[] chunk = input;
                chunkCount = chunkCount + 1;

                // handle chunk
                try
                {
                    chunkHandler.handleChunk(chunk, chunkCount, noOfChunks);
                }
                catch (ChunkHandleException che)
                {
                    throw new ChunkableDataSourceException(
                        "Exception handling data sourced from ChunkingInputStream", che); //$NON-NLS-1$
                }

            }
        }
        catch (IOException e)
        {
            throw new ChunkableDataSourceException(e.getMessage(), e);
        }
    }

}
