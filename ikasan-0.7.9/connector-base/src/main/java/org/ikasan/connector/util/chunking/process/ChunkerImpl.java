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
package org.ikasan.connector.util.chunking.process;

import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;

import org.ikasan.common.util.checksum.DigestChecksum;
import org.ikasan.common.util.checksum.Md5Checksum;
import org.ikasan.connector.util.chunking.io.ChunkingInputStreamConsumer;
import org.ikasan.connector.util.chunking.io.ChunkingOutputStream;
import org.ikasan.connector.util.chunking.model.FileChunk;
import org.ikasan.connector.util.chunking.model.FileChunkHeader;
import org.ikasan.connector.util.chunking.model.FileConstituentHandle;
import org.ikasan.connector.util.chunking.model.dao.ChunkLoadException;
import org.ikasan.connector.util.chunking.model.dao.FileChunkDao;
import org.ikasan.connector.util.chunking.provider.ChunkableDataProvider;
import org.ikasan.connector.util.chunking.provider.ChunkableDataProviderAccessException;
import org.ikasan.connector.util.chunking.provider.ChunkableDataSourceException;

/**
 * Transport agnostic base class for Chunker
 * 
 * @author Ikasan Development Team
 * 
 */
public class ChunkerImpl implements Chunker, ChunkHandler
{

    /** Logger */
    private static Logger logger = Logger.getLogger(ChunkerImpl.class);

    /**
     * Underlying chunk data provider
     */
    private ChunkableDataProvider dataProvider;

    /**
     * Format for time stamping the chunking process
     */
    public static final SimpleDateFormat chunkTimeStampFormat = new SimpleDateFormat("yyyyMMddHHmmss");

    /**
     * No of milliseconds in 1 hour
     */
    public static final long ONE_HOUR = 1000 * 60 * 60;

    /**
     * Maximum age of resumable sequences
     */
    private long maxResumableAge = ONE_HOUR;

    /**
     * DigestChecksum implementation
     */
    protected DigestChecksum checksum = new Md5Checksum();

    /**
     * Dao for FileChunk/Header persistence
     */
    protected FileChunkDao dao;

    /**
     * FileChunkHeader
     */
    protected FileChunkHeader fileChunkHeader = null;

    /**
     * Consumer object for processing InputStreams
     * 
     * Only required when running in InputStream mode
     */
    protected ChunkingInputStreamConsumer consumer;

    /**
     * Specifies whether to use InputStreams or OutputStreams when accessing the
     * SftpProvider
     */
    protected int streamMode = MODE_OUTPUT_STREAM;

    /**
     * Client Id
     */
    private String clientId;

    /**
     * Constructor
     * 
     * @param dao
     * @param dataProvider
     * @param streamMode
     * @param clientId
     */
    public ChunkerImpl(FileChunkDao dao, ChunkableDataProvider dataProvider, int streamMode, String clientId)
    {
        this.dao = dao;
        this.dataProvider = dataProvider;
        this.streamMode = streamMode;
        this.consumer = new ChunkingInputStreamConsumer(this);
        this.clientId = clientId;
    }

    /**
     * Constructor
     * 
     * @param dao
     * @param dataProvider
     * @param streamMode
     */
    public ChunkerImpl(FileChunkDao dao, ChunkableDataProvider dataProvider, int streamMode)
    {
        this(dao, dataProvider, streamMode, null);
    }

    /**
     * Chunk a remote file
     * 
     * @param remoteDir
     * @param fileName
     * @param chunkSize
     * @throws ChunkException
     */
    public void chunkFile(String remoteDir, String fileName, int chunkSize) throws ChunkException
    {

        if (chunkSize <= 0)
        {
            throw new ChunkException("chunkSize must be greater than 0"); //$NON-NLS-1$
        }

        try
        {
            dataProvider.connect();
        }
        catch (ChunkableDataProviderAccessException e1)
        {
            throw new ChunkException("Exception connecting to data provider ", e1); //$NON-NLS-1$
        }

        try
        {
            long fileSize = dataProvider.getFileSize(remoteDir, fileName);
            long noOfChunks = getExpectedNumberOfChunks(chunkSize, fileSize);
            long startingChunk = initialise(fileName, noOfChunks);
            if (startingChunk > 0)
            {
                logger.info("Resuming at chunk:" + startingChunk); //$NON-NLS-1$
            }

            sourceChunks(remoteDir, fileName, chunkSize, noOfChunks, startingChunk);

            afterLastChunk();
        }
        catch (ChunkableDataSourceException cse)
        {
            throw new ChunkException("Exception accessing remote resource: remoteDir=" + remoteDir + ", fileName="  //$NON-NLS-1$//$NON-NLS-2$
                    + fileName, cse);
        }
        catch (ChunkLoadException e)
        {
            throw new ChunkException("Exception reloading an existing chunk prior to resume", e); //$NON-NLS-1$
        }

        try
        {
            dataProvider.disconnect();
        }
        catch (ChunkableDataProviderAccessException e)
        {
            throw new ChunkException("Exception disconnecting from data provider ", e); //$NON-NLS-1$
        }

    }

    /**
     * Handle each chunk as it is generated
     * 
     * @param chunk
     * @param ordinal
     * @param sequenceLength
     */
    public void handleChunk(byte[] chunk, long ordinal, long sequenceLength)
    {
        long chunkNumber = ordinal;
        logger.debug("handling chunk [" + chunkNumber + " of " + sequenceLength + "]");  //$NON-NLS-1$ //$NON-NLS-2$//$NON-NLS-3$
        FileChunk fileChunk = new FileChunk(fileChunkHeader, chunkNumber++, chunk);
        dao.save(fileChunk);
        checksum.update(chunk);
    }

    /**
     * Creates the Header record
     * 
     * @param fileName
     * @param noOfChunks
     */
    protected void createHeader(String fileName, Long noOfChunks)
    {
        Long chunkTimeStamp = Long.parseLong(chunkTimeStampFormat.format(new Date()));
        fileChunkHeader = new FileChunkHeader(noOfChunks, null, fileName, chunkTimeStamp, clientId);
        dao.save(fileChunkHeader);

    }

    /**
     * Called after the last chunk has been handled
     */
    protected void afterLastChunk()
    {
        fileChunkHeader.setInternalMd5Hash(checksum.digestToString());
        dao.save(fileChunkHeader);
    }

    /**
     * Called prior to the first chunk being handled
     * 
     * @param fileName
     * @param noOfChunks
     * @return ordinal of the starting chunk
     * @throws ChunkLoadException
     */
    protected long initialise(String fileName, long noOfChunks) throws ChunkLoadException
    {
        List<FileConstituentHandle> existingChunks = dao.findChunks(fileName, null, noOfChunks, maxResumableAge);
        logger.info("existing chunks : [" + existingChunks + "]"); //$NON-NLS-1$ //$NON-NLS-2$
        long startingChunk = 0;
        if (existingChunks.size() == 0)
        {
            // this is a completely new run
        }
        else if (existingChunks.size() == noOfChunks)
        {
            // there is a completed chunking run for this file, younger than the
            // maxResumableAge
        }
        else
        {
            startingChunk = existingChunks.size();
        }

        checksum.reset();

        if (startingChunk == 0)
        {
            // only need to create the header if this is a new run
            createHeader(fileName, noOfChunks);
        }
        else
        {
            // restore the header from one of the existing chunks
            fileChunkHeader = existingChunks.get(0).getFileChunkHeader();

            // need to replay the existing chunks through the checksum
            for (Iterator<FileConstituentHandle> iterator = existingChunks.iterator(); iterator.hasNext();)
            {
                FileConstituentHandle fileConstituentHandle = iterator.next();
                FileChunk fileChunk = dao.load(fileConstituentHandle);
                checksum.update(fileChunk.getContent());
            }
        }

        return startingChunk;

    }

    /**
     * Calculates the expected number of chunks based on the known file size and
     * the known chunk size
     * 
     * @param chunkSize
     * @param fileSize
     * @return the number of expected chunks
     */
    protected static long getExpectedNumberOfChunks(int chunkSize, long fileSize)
    {
        long complete_chunks = fileSize / chunkSize;
        int partialChunks = 0;
        if ((fileSize % chunkSize) > 0)
        {
            partialChunks = 1;
        }
        return complete_chunks + partialChunks;
    }

    /**
     * Calls the underlying data provider's source method that returns an
     * InputStream
     * 
     * Alternatively, use sourceChunksWithOutputStream for OutputStream
     * implementation
     * 
     * @param remoteDir
     * @param fileName
     * @param chunkSize
     * @param noOfChunks
     * @throws ChunkableDataSourceException
     */
    protected void sourceChunksWithInputStream(String remoteDir, String fileName, int chunkSize, long noOfChunks)
            throws ChunkableDataSourceException
    {

        InputStream inputStream = dataProvider.sourceChunkableData(remoteDir, fileName);
        consumer.consumeInputStream(inputStream, chunkSize, noOfChunks);

    }

    /**
     * Calls the underlying data provider's source method that gets passed an
     * OutputStream
     * 
     * Alternatively, use sourceChunksWithInputStream for InputStream
     * implementation
     * 
     * @param remoteDir
     * @param fileName
     * @param chunkSize
     * @param noOfChunks
     * @param startingChunk
     * @throws ChunkableDataSourceException
     */
    protected void sourceChunksWithOutputStream(String remoteDir, String fileName, int chunkSize, long noOfChunks,
            long startingChunk) throws ChunkableDataSourceException
    {
        ChunkingOutputStream chunkingOutputStream = new ChunkingOutputStream(chunkSize, this, noOfChunks, startingChunk);

        long offset = startingChunk * chunkSize;

        logger.debug("SftpDataProvider.sourceChunksWithOutputStream called with remoteDir=" + remoteDir //$NON-NLS-1$
                + ", fileName = " + fileName); //$NON-NLS-1$
        dataProvider.sourceChunkableData(remoteDir, fileName, chunkingOutputStream, offset);
        // TODO use output stream in the future
        try
        {
            chunkingOutputStream.close();
        }
        catch (IOException e)
        {
            throw new ChunkableDataSourceException("Exception caught trying to close the OutputStream", e);
        }
    }

    /**
     * Calls either of the source methods, depending on the stream mode set
     * 
     * @param remoteDir
     * @param fileName
     * @param chunkSize
     * @param noOfChunks
     * @param startingChunk
     * @throws ChunkableDataSourceException
     */
    protected void sourceChunks(String remoteDir, String fileName, int chunkSize, long noOfChunks, long startingChunk)
            throws ChunkableDataSourceException
    {
        if (streamMode == MODE_INPUT_STREAM)
        {
            if (startingChunk > 0)
            {
                throw new ChunkableDataSourceException("Cannot resume in InputStream mode"); //$NON-NLS-1$
            }
            sourceChunksWithInputStream(remoteDir, fileName, chunkSize, noOfChunks);
        }
        else if (streamMode == MODE_OUTPUT_STREAM)
        {
            sourceChunksWithOutputStream(remoteDir, fileName, chunkSize, noOfChunks, startingChunk);
        }
        else
        {
            throw new ChunkableDataSourceException("Unknown mode:" + streamMode); //$NON-NLS-1$
        }
    }

    /**
     * Accessor method for the FileCHunkHeader
     * 
     * @return fileChunkHeader
     */
    public FileChunkHeader getFileChunkHeader()
    {
        return fileChunkHeader;
    }

}
