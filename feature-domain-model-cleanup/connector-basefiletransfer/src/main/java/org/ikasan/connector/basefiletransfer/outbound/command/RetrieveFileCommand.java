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
package org.ikasan.connector.basefiletransfer.outbound.command;

import java.io.File;

import javax.resource.ResourceException;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.log4j.Logger;
import org.ikasan.connector.base.command.ExecutionContext;
import org.ikasan.connector.base.command.ExecutionOutput;
import org.ikasan.connector.basefiletransfer.net.BaseFileTransferMappedRecord;
import org.ikasan.connector.basefiletransfer.net.ClientListEntry;
import org.ikasan.connector.basefiletransfer.outbound.persistence.BaseFileTransferDao;

/**
 * Retrieves a specified file from a remote directory
 * 
 * @author Ikasan Development Team 
 */
public class RetrieveFileCommand extends AbstractBaseFileTransferTransactionalResourceCommand
{
    /** The logger instance. */
    private static Logger logger = Logger.getLogger(RetrieveFileCommand.class);

    /** Data Access Object for Base File Transfer */
    protected BaseFileTransferDao dao;

    /** The original source path of the file we are retrieving */
    protected String sourcePath;
    
    /** Rename the file on successful retrieval */
    protected boolean renameOnSuccess;
    
    /** Extension with which to rename successfully retrieved files */
    protected String renameExtension;

    /** Move the remote file once successfully retrieved */
    private boolean moveOnSuccess;

    /** New path to move remote file to */
    private String moveOnSuccessNewPath;

    /** Whether or not to destroy the file after we pick it up */
    protected boolean destructive;
    
    /**
     * No args constructor required by Hibernate
     */
    protected  RetrieveFileCommand(){
        // Do Nothing
    }
    
    /**
     * Constructor
     *  
     * @param dao hibernateDAO
     * @param renameOnSuccess flag
     * @param renameExtension to rename file
     * @param moveOnSuccess flag
     * @param moveOnSuccessNewPath to move file
     * @param destructive flag
     */
    public RetrieveFileCommand(BaseFileTransferDao dao, boolean renameOnSuccess, String renameExtension,boolean moveOnSuccess, String moveOnSuccessNewPath,boolean destructive)
    {
        super();
        this.dao = dao;
        this.renameOnSuccess = renameOnSuccess;
        this.renameExtension = renameExtension;
        this.moveOnSuccess = moveOnSuccess;
        this.moveOnSuccessNewPath = moveOnSuccessNewPath;
        this.destructive = destructive;

        // This should never occur as we are checking for this earlier, but just in case...
        if (renameOnSuccess && destructive)
        {
            throw new IllegalArgumentException("RenameOnSuccess and Get Destructive are mutually exclusive."); //$NON-NLS-1$
        }
        if (moveOnSuccess && destructive)
        {
            throw new IllegalArgumentException("Moving the file and Get Destructive are mutually exclusive."); //$NON-NLS-1$
        }
        if (renameOnSuccess && moveOnSuccess)
        {
            throw new IllegalArgumentException("Moving the file and renaming it are mutually exclusive."); //$NON-NLS-1$
        }
        if (renameOnSuccess && renameExtension == null)
        {
            throw new IllegalArgumentException("renameExtension has not been configured."); //$NON-NLS-1$
        }
        if (moveOnSuccess&& moveOnSuccessNewPath == null)
        {
            throw new IllegalArgumentException("moveOnSuccessNewPath has not been configured."); //$NON-NLS-1$
        }
        logger.info("constructor called with: [" + this + "]"); //$NON-NLS-1$ //$NON-NLS-2$
    }

    /**
     *  (non-Javadoc)
     * @see org.ikasan.connector.basefiletransfer.outbound.command.AbstractBaseFileTransferTransactionalResourceCommand#performExecute()
     */
    @Override
    protected ExecutionOutput performExecute() throws ResourceException
    {
        logger.info("execute called on this command: [" + this + "]"); //$NON-NLS-1$ //$NON-NLS-2$
        
        ClientListEntry entry = (ClientListEntry) executionContext.get(ExecutionContext.RETRIEVABLE_FILE_PARAM);
        //sourcePath = entry.getUri().getPath();
        // We change the path to be file based as opposed to URI based, 
        // means that root starts as '/' as opposed to '//' which 
        // some FTP servers don't like
        String uriPath = entry.getUri().getPath();
        File path = new File(uriPath);
        sourcePath = path.getPath();
        BaseFileTransferMappedRecord record = getFile(entry);
        String clientId = (String) executionContext.get(ExecutionContext.CLIENT_ID);
        logger.debug("got clientId [" + clientId + "]"); //$NON-NLS-1$ //$NON-NLS-2$
        
        entry.setClientId(clientId);
        dao.persistClientListEntry(entry);
        return new ExecutionOutput(record);
    }

    /**
     * (non-Javadoc)
     * @see org.ikasan.connector.base.command.AbstractTransactionalResourceCommand#doCommit()
     */
    @Override
    protected void doCommit() throws ResourceException
    {
        logger.info("commit called on this command:" + this + "]"); //$NON-NLS-1$ //$NON-NLS-2$);
        logger.info("isMoveOnSuccess [" + moveOnSuccess + "]"); //$NON-NLS-1$ //$NON-NLS-2$);
        logger.debug("isRenameOnSuccess [" + renameOnSuccess + "]"); //$NON-NLS-1$ //$NON-NLS-2$);
        logger.debug("isDestructive [" + destructive + "]"); //$NON-NLS-1$ //$NON-NLS-2$);

        if (renameOnSuccess)
        {
            renameFile(sourcePath, sourcePath+renameExtension);
        }
        else if (moveOnSuccess)
        {
            logger.info("moving file.."); //$NON-NLS-1$
            renameFile(sourcePath, moveOnSuccessNewPath);
        }
        // TODO Delete the checksum
        else if (destructive)
        {
            deleteFile(sourcePath);
        }
    }

    /**
     *  (non-Javadoc)
     * @see org.ikasan.connector.base.command.AbstractTransactionalResourceCommand#doRollback()
     */
    @Override
    protected void doRollback()
    {
        logger.info("rollback called on this command:" + this + "]"); //$NON-NLS-1$ //$NON-NLS-2$
    }

    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        return new ToStringBuilder(this)
            .append("client", getClient()) //$NON-NLS-1$
            .append("dao", this.dao) //$NON-NLS-1$
            .append("fileSeparator", this.fileSeparator) //$NON-NLS-1$
            .append("sourcePath", this.sourcePath) //$NON-NLS-1$
            .append("renameOnSuccess", this.renameOnSuccess) //$NON-NLS-1$
            .append("renameExtension", this.renameExtension) //$NON-NLS-1$
            .append("moveOnSuccess", this.moveOnSuccess) //$NON-NLS-1$
            .append("newPath", this.moveOnSuccessNewPath) //$NON-NLS-1$
            .append("destructive", this.destructive) //$NON-NLS-1$
        .toString();
    }

    /**
     * Accessor method for source path
     * @return sourcePath
     */
    public String getSourcePath()
    {
        return sourcePath;
    }

    /**
     * Setter method for sourcePath, used by Hibernate
     * @param sourcePath of file
     */
    @SuppressWarnings("unused")
    private void setSourcePath(String sourcePath)
    {
        this.sourcePath = sourcePath;
    }

    /**
     * Do we rename the file on successful delivery?
     * @return true if we rename, else false
     */
    public boolean isRenameOnSuccess()
    {
        return renameOnSuccess;
    }

    /**
     * Private setter used by Hibernate
     * @param isRenameOnSuccess flag
     */
    @SuppressWarnings("unused")
    private void setRenameOnSuccess(boolean isRenameOnSuccess)
    {
        this.renameOnSuccess = isRenameOnSuccess;
    }

    /**
     * Get the extension that we'rerenaming the file with
     * @return file extension for the renamed file
     */
    public String getRenameExtension()
    {
        return renameExtension;
    }

    /**
     * Private setter sued by Hibernate
     * @param renameExtension when renaming file
     */
    @SuppressWarnings("unused")
    private void setRenameExtension(String renameExtension)
    {
        this.renameExtension = renameExtension;
    }

    /**
     * Do we delete the file on successful delivery?
     * @return true if we delete, else false
     */
    public boolean isDestructive()
    {
        return destructive;
    }

    /**
     * Private setter used by Hibernate
     * @param isDestructive flag
     */
    @SuppressWarnings("unused")
    private void setDestructive(boolean isDestructive)
    {
        this.destructive = isDestructive;
    }

    /**
     * Private setter used by Hibernate
     * @param moveOnSuccess flag
     */
    @SuppressWarnings("unused")
    private void setMoveOnSuccess(boolean moveOnSuccess)
    {
        this.moveOnSuccess = moveOnSuccess;
    }

    /**
     * Do we move the file on successful delivery?
     * @return moveOnSuccess
     */
    public boolean isMoveOnSuccess()
    {
        return moveOnSuccess;
    }

    /**
     * Private setter used by Hibernate
     * @param moveNewPath flag
     */
    @SuppressWarnings("unused")
    private void setMoveNewPath(String moveNewPath)
    {
        this.moveOnSuccessNewPath = moveNewPath;
    }

    /**
     * Get the new path a file is being moved to.
     * @return moveNewPath
     */
    public String getMoveNewPath()
    {
        return moveOnSuccessNewPath;
    }
}
