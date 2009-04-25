/*
 * $Id: ChunkInputStream.java 16756 2009-04-22 12:35:57Z mitcje $
 * $URL: svn+ssh://svc-vcsp/architecture/ikasan/trunk/connector-base/src/main/java/org/ikasan/connector/util/chunking/io/ChunkInputStream.java $
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