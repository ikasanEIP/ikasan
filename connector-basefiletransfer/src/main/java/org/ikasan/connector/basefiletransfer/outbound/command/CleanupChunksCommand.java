/*
 * $Id: CleanupChunksCommand.java 16767 2009-04-23 12:37:52Z mitcje $
 * $URL: svn+ssh://svc-vcsp/architecture/ikasan/trunk/connector-basefiletransfer/src/main/java/org/ikasan/connector/basefiletransfer/outbound/command/CleanupChunksCommand.java $
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

import javax.resource.ResourceException;

import org.apache.log4j.Logger;

import org.ikasan.connector.ConnectorException;
import org.ikasan.connector.base.command.ExecutionContext;
import org.ikasan.connector.base.command.ExecutionOutput;
import org.ikasan.connector.util.chunking.model.FileChunkHeader;
import org.ikasan.connector.util.chunking.model.dao.ChunkHeaderLoadException;
import org.ikasan.connector.util.chunking.model.dao.FileChunkDao;

/**
 * Delivers a specified payload to a remote File Transfer directory
 * 
 * @author Ikasan Development Team
 */
public class CleanupChunksCommand extends AbstractBaseFileTransferTransactionalResourceCommand
{
    /** The logger instance. */
    private static Logger logger = Logger.getLogger(CleanupChunksCommand.class);

    /**
     * FileChunkHeader that we will be cleaning up
     * 
     * Note that this is not persisted. Only its id is persisted, and if
     * necessary this is later reloaded from the id
     */
    private FileChunkHeader fileChunkHeader;

    /**
     * Id for the fileChunkHeader.
     * 
     * Note that this is persisted
     */
    private Long fileChunkHeaderId;

    /**
     * Constructor
     */
    public CleanupChunksCommand()
    {
        super();
    }

    @Override
    protected ExecutionOutput performExecute()
    {
        logger.info("execute called on this command: [" + this + "]"); //$NON-NLS-1$ //$NON-NLS-2$

        // Don't need to do anything on execute except remember the
        // fileChunkHeader Id
        this.fileChunkHeader = ((FileChunkHeader) executionContext.get(ExecutionContext.FILE_CHUNK_HEADER));
        this.fileChunkHeaderId = fileChunkHeader.getId();

        return new ExecutionOutput();
    }

    @Override
    protected void doCommit() throws ResourceException
    {
        logger.info("commit called on this command:" + this + "]"); //$NON-NLS-1$ //$NON-NLS-2$);
        FileChunkDao fileChunkDao = (FileChunkDao) getBeanFactory().getBean("fileChunkDao");

        if (fileChunkHeader == null)
        {
            try
            {
                fileChunkHeader = fileChunkDao.load(fileChunkHeaderId);
            }
            catch (ChunkHeaderLoadException e)
            {
                throw new ConnectorException("FileChunkHeader with pk [" + fileChunkHeaderId //$NON-NLS-1$
                        + "] could not be reloaded from the database", e); //$NON-NLS-1$
            }
        }

        // do the cleanup
        fileChunkDao.delete(fileChunkHeader);
    }

    @Override
    protected void doRollback()
    {
        logger.info("rollback called on this command:" + this + "]"); //$NON-NLS-1$ //$NON-NLS-2$
    }

    /**
     * Accessor method required by Hibernate
     * 
     * @return fileChunkHeaderId
     */
    @SuppressWarnings("unused")
    private Long getFileChunkHeaderId()
    {
        return fileChunkHeaderId;
    }

    /**
     * Setter required by Hibernate
     * 
     * @param fileChunkHeaderId the file chunk header id to set 
     */
    @SuppressWarnings("unused")
    private void setFileChunkHeaderId(Long fileChunkHeaderId)
    {
        this.fileChunkHeaderId = fileChunkHeaderId;
    }

}
