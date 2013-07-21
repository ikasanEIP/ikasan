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
