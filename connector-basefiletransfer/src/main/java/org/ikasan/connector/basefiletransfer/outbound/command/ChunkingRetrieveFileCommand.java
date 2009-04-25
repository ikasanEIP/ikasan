/*
 * $Id: ChunkingRetrieveFileCommand.java 16767 2009-04-23 12:37:52Z mitcje $
 * $URL: svn+ssh://svc-vcsp/architecture/ikasan/trunk/connector-basefiletransfer/src/main/java/org/ikasan/connector/basefiletransfer/outbound/command/ChunkingRetrieveFileCommand.java $
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
package org.ikasan.connector.basefiletransfer.outbound.command;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URISyntaxException;
import java.util.List;

import javax.resource.ResourceException;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.log4j.Logger;

import org.ikasan.connector.base.command.ExecutionContext;
import org.ikasan.connector.base.command.ExecutionOutput;
import org.ikasan.connector.basefiletransfer.net.ClientCommandGetException;
import org.ikasan.connector.basefiletransfer.net.ClientCommandLsException;
import org.ikasan.connector.basefiletransfer.net.ClientListEntry;
import org.ikasan.connector.basefiletransfer.outbound.persistence.BaseFileTransferDao;
import org.ikasan.connector.util.chunking.model.FileChunkHeader;
import org.ikasan.connector.util.chunking.model.dao.FileChunkDao;
import org.ikasan.connector.util.chunking.process.ChunkException;
import org.ikasan.connector.util.chunking.process.Chunker;
import org.ikasan.connector.util.chunking.process.ChunkerImpl;
import org.ikasan.connector.util.chunking.provider.ChunkableDataProvider;
import org.ikasan.connector.util.chunking.provider.ChunkableDataSourceException;

/**
 * Retrieves a specified file from a remote directory
 * 
 * @author Ikasan Development Team
 */
public class ChunkingRetrieveFileCommand extends RetrieveFileCommand implements ChunkableDataProvider
{
    /** The logger instance. */
    private static Logger logger = Logger.getLogger(RetrieveFileCommand.class);

    /** The chunker for large files */
    private Chunker chunker;

    /** The client that is invoking this command */
    private String clientId;

    /**
     * maximum size of a file chunk Note must be >0
     */
    private int chunkSize;

    /**
     * No args constructor as required by Hibernate
     */
    @SuppressWarnings("unused")
    private ChunkingRetrieveFileCommand()
    {
        super();
    }

    /**
     * Constructor
     * 
     * @param dao
     * @param clientId
     * @param renameOnSuccess
     * @param renameExtension
     * @param moveOnSuccess 
     * @param moveNewPath 
     * @param fileChunkDao
     * @param chunkSize
     * @param destructive 
     */
    public ChunkingRetrieveFileCommand(BaseFileTransferDao dao, String clientId, boolean renameOnSuccess,
            String renameExtension, boolean moveOnSuccess, String  moveNewPath, FileChunkDao fileChunkDao, int chunkSize, boolean destructive)
    {
        super(dao, renameOnSuccess, renameExtension,moveOnSuccess, moveNewPath , destructive);

        this.chunkSize = chunkSize;
        this.clientId = clientId;

        logger.info("constructor called with: [" + this + "]"); //$NON-NLS-1$ //$NON-NLS-2$
        chunker = new ChunkerImpl(fileChunkDao, this, Chunker.MODE_OUTPUT_STREAM, clientId);
    }

    @Override
    protected ExecutionOutput performExecute() throws ResourceException
    {
        logger.info("execute called on this command: [" + this + "]"); //$NON-NLS-1$ //$NON-NLS-2$

        ClientListEntry entry = (ClientListEntry) executionContext.get(ExecutionContext.RETRIEVABLE_FILE_PARAM);
        this.sourcePath = entry.getUri().getPath();

        File file = new File(entry.getUri().getPath());
        try
        {
            String directory = file.getParent() + "/";
            chunker.chunkFile(directory, file.getName(), chunkSize);
        }
        catch (ChunkException e)
        {
            throw new ResourceException("Exception caught whilst trying to chunk", e); //$NON-NLS-1$
        }

        FileChunkHeader fileChunkHeader = ((ChunkerImpl) chunker).getFileChunkHeader();

        clientId = (String) executionContext.get(ExecutionContext.CLIENT_ID);

        entry.setClientId(clientId);
        dao.persistClientListEntry(entry);

        return new ExecutionOutput(fileChunkHeader);
    }

    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        return new ToStringBuilder(this).append("client", getClient()).append("dao", this.dao).append( //$NON-NLS-1$ //$NON-NLS-2$
            "fileSeparator", this.fileSeparator).append("sourcePath", this.sourcePath).toString();  //$NON-NLS-1$//$NON-NLS-2$
    }

    /**
     * Accessor method for source path
     * 
     * @return sourcePath
     */
    @Override
    public String getSourcePath()
    {
        return sourcePath;
    }

    /**
     * Setter method for sourcePath, used by Hibernate
     * 
     * @param sourcePath
     */
    @SuppressWarnings("unused")
    private void setSourcePath(String sourcePath)
    {
        this.sourcePath = sourcePath;
    }

    public void connect()
    {
        // Do Nothing
    }

    public void disconnect()
    {
        // Do Nothing
    }

    public long getFileSize(String remoteDir, String fileName) throws ChunkableDataSourceException
    {

        List<ClientListEntry> entries = null;
        try
        {
            entries = getClient().ls(remoteDir + fileName);
            if (entries.size() != 1)
            {
                // We have failed to uniquely identify the file
                throw new ChunkableDataSourceException("Could not uniquely find file: [" //$NON-NLS-1$
                        + fileName + "], in remote dir: [" + remoteDir + "]"); //$NON-NLS-1$ //$NON-NLS-2$
            }
        }
        catch (ClientCommandLsException e)
        {
            throw new ChunkableDataSourceException("Could not perform ls over file transfer for file: [" //$NON-NLS-1$ 
                    + fileName + "], in remote dir: [" + remoteDir + "]", e); //$NON-NLS-1$ //$NON-NLS-2$
        }
        catch (URISyntaxException e)
        {
            throw new ChunkableDataSourceException("Could not perform ls over file transfer for file: [" //$NON-NLS-1$ 
                    + fileName + "], in remote dir:" + remoteDir + "]", e); //$NON-NLS-1$ //$NON-NLS-2$
        }

        ClientListEntry lsEntry = entries.get(0);
        return lsEntry.getSize();

    }

    public InputStream sourceChunkableData(String remoteDir, String fileName) throws ChunkableDataSourceException
    {
        try
        {
            return getClient().getAsInputStream(remoteDir + fileName);
        }
        catch (ClientCommandGetException e)
        {
            throw new ChunkableDataSourceException("Exception whilst performing file transfer get ", e); //$NON-NLS-1$
        }
    }

    public void sourceChunkableData(String remoteDir, String fileName, OutputStream outputStream, long offset)
            throws ChunkableDataSourceException
    {
        try
        {
            if (offset > 0)
            {
                // TODO Remove hard coded '1' and replace with value that means RESUME
                getClient().get(remoteDir + fileName, outputStream, 1, offset);
            }
            else
            {
                getClient().get(remoteDir + fileName, outputStream);
            }
        }
        catch (ClientCommandGetException e)
        {
            throw new ChunkableDataSourceException("Exception whilst performing file transfer get ", e); //$NON-NLS-1$
        }
    }

}
