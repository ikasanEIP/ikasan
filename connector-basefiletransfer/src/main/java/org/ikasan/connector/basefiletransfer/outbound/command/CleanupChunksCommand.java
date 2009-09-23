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
