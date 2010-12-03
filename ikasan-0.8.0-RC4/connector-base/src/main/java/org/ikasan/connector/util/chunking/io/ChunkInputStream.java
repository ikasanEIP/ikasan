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
import java.util.Iterator;
import java.util.List;

import org.ikasan.common.util.checksum.DigestChecksum;
import org.ikasan.common.util.checksum.Md5Checksum;
import org.ikasan.connector.util.chunking.model.FileChunk;
import org.ikasan.connector.util.chunking.model.FileConstituentHandle;
import org.ikasan.connector.util.chunking.model.dao.ChunkLoadException;
import org.ikasan.connector.util.chunking.model.dao.FileChunkDao;

/**
 * An InputStream backed by the FilChunkDao Repository Allows stored chunked
 * data to be accessed via an InputStream
 * 
 * @author Ikasan Development Team
 * 
 */
public class ChunkInputStream extends InputStream
{

    /**
     * Cumulative checksum calculator
     */
    private DigestChecksum checksum = new Md5Checksum();

    /**
     * Data access object for retrieving chunks
     */
    private FileChunkDao dao;

    /**
     * Iterates through the fileConstituentHandles representing the chunks that
     * need loading
     */
    private Iterator<FileConstituentHandle> fileConstituentIterator;

    /**
     * The internal InputStream being used to support this class
     */
    private InputStream inputStream;

    /**
     * Record of no of chunks processed
     */
    private long readCount = 0;

    /**
     * Counter for bytes read in this chunk
     */
    private long readSinceRolloverCount = 0;

    /**
     * Counter for number of times we have rolled over to a new chunk
     */
    private long rolloverCount = 0;

    /**
     * Constructor
     * 
     * @param constituentHandles
     * @param dao
     * @throws IOException
     */
    public ChunkInputStream(List<FileConstituentHandle> constituentHandles, FileChunkDao dao) throws IOException
    {
        super();
        this.dao = dao;
        this.fileConstituentIterator = constituentHandles.iterator();
        checksum.reset();
        rolloverInputStream();
    }

    @Override
    public int read() throws IOException
    {
        readCount = readCount + 1;
        readSinceRolloverCount = readSinceRolloverCount + 1;

        if (inputStream == null)
        {
            return -1;
        }
        int result = inputStream.read();
        if (result == -1)
        {
            rolloverInputStream();
            result = read();
        }

        return result;
    }

    /**
     * Causes a new chunk to be loaded and its contents used to populate the
     * internal InputStream
     * 
     * @throws IOException
     */
    private void rolloverInputStream() throws IOException
    {
        rolloverCount = rolloverCount + 1;

        inputStream = null;
        if (fileConstituentIterator.hasNext())
        {
            FileChunk fileChunk;
            try
            {
                fileChunk = dao.load(fileConstituentIterator.next());
                byte[] content = fileChunk.getContent();
                updateChecksum(fileChunk);
                inputStream = new ByteArrayInputStream(content);
            }
            catch (ChunkLoadException e)
            {
                // Indicates a DigestChecksum failure reloading the chunk
                // What should we do here, retry 3 times??
                throw new IOException("Exception loading ChunkFile from perisistant storage " + e.getMessage()); //$NON-NLS-1$
            }
        }

        readSinceRolloverCount = 0;
    }

    /**
     * Updates the cumulative checksum with the content of the current chunk
     * 
     * @param fileChunk
     */
    private void updateChecksum(FileChunk fileChunk)
    {
        byte[] bytes = fileChunk.getContent();
        checksum.update(bytes);
    }

    /**
     * Provides access to the resultant checksum Note that calling this method
     * also results in the checksum itself being reset
     * 
     * @return String 32bytes of hex
     */
    public String getMd5Hash()
    {
        return checksum.digestToString();
    }

}