/*
 * $Id: RenameFileCommand.java 16767 2009-04-23 12:37:52Z mitcje $
 * $URL: svn+ssh://svc-vcsp/architecture/ikasan/trunk/connector-basefiletransfer/src/main/java/org/ikasan/connector/basefiletransfer/outbound/command/RenameFileCommand.java $
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

import org.ikasan.connector.base.command.ExecutionContext;
import org.ikasan.connector.base.command.ExecutionOutput;

/**
 * Renames files from a remote browsable directory
 * 
 * @author Ikasan Development Team
 */
public class RenameFileCommand extends AbstractBaseFileTransferTransactionalResourceCommand
{
    /** The logger instance. */
    private static Logger logger = Logger.getLogger(RenameFileCommand.class);

    /** The old path of the file we are persisting */
    private String oldFilePath;
    
    /** The new path of the file we are persisting */
    private String newFilePath;
    
    /** Constructor */
    public RenameFileCommand()
    {
        super();
    }
    
    /**
     * Initialises the command for execution
     * Retrieves required parameters from the execution context
     * 
     * @throws ResourceException Exception from Connector
     */
    private void initialise() throws ResourceException
    {
        oldFilePath = executionContext.getRequiredString(ExecutionContext.RENAMABLE_FILE_PATH_PARAM);
        newFilePath = executionContext.getRequiredString(ExecutionContext.NEW_FILE_PATH_PARAM);
    }

    /**
     * @see AbstractBaseFileTransferTransactionalResourceCommand#commit()
     */
    @Override
    protected void doCommit()
    {
        // Do Nothing
    }

    /** 
     * @see org.ikasan.connector.basefiletransfer.outbound.command.AbstractBaseFileTransferTransactionalResourceCommand#performExecute()
     */
    @Override
    protected ExecutionOutput performExecute() throws ResourceException
    {
        logger.info("execute called on command: [" + this + "]"); //$NON-NLS-1$ //$NON-NLS-2$
        initialise();
        
        renameFile(this.oldFilePath, this.newFilePath);
        return new ExecutionOutput();
    }

    /** 
     * @see AbstractBaseFileTransferTransactionalResourceCommand#rollback()
     */
    @Override
    protected void doRollback() throws ResourceException
    {
        logger.info("doRollback called on command: [" + this + "]"); //$NON-NLS-1$ //$NON-NLS-2$
        renameFile(this.newFilePath, this.oldFilePath);
    }
}
