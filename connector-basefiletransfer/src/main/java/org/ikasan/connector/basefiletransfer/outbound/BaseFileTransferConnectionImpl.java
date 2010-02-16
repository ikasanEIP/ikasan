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
package org.ikasan.connector.basefiletransfer.outbound;

import java.io.InputStream;

import javax.resource.ResourceException;
import javax.resource.spi.ManagedConnection;

import org.ikasan.connector.base.command.ExecutionContext;
import org.ikasan.connector.base.command.ExecutionOutput;
import org.ikasan.connector.base.command.TransactionalResourceCommand;
import org.ikasan.connector.base.outbound.EISConnectionImpl;
import org.ikasan.connector.basefiletransfer.outbound.command.DeliverBatchCommand;
import org.ikasan.connector.basefiletransfer.outbound.command.DeliverFileCommand;
import org.ikasan.connector.basefiletransfer.outbound.command.util.UnzipNotSupportedException;
import org.ikasan.connector.basefiletransfer.outbound.command.util.UnzippingFileProvider;

/**
 * A Base implementation for File transfer connections
 * @author Ikasan Development Team
 */
public abstract class BaseFileTransferConnectionImpl extends EISConnectionImpl
{

    /**
     * Constructor which takes ManagedConnection as a parameter
     * 
     * @param mc The ManagedConnection
     */
    public BaseFileTransferConnectionImpl(ManagedConnection mc)
    {
        super(mc);
    }

    /**
     * Deliver the InputStream
     * 
     * TODO Add in the checksumDelivered feature
     * 
     * @param inputStream to deliver
     * @param fileName The name of the file
     * @param outputDir The output directory to deliver to
     * @param overwrite Whether to overwrite or not
     * @param renameExtension The extension for renaming the file
     * @param checksumDelivered A flag to see if we check the checksum on delivery
     * @param unzip flag
     * @throws ResourceException - Exception from the JCA connector
     */
    public void deliverInputStream(InputStream inputStream, String fileName, String outputDir, boolean overwrite,
            String renameExtension, boolean checksumDelivered, boolean unzip) throws ResourceException
    {
        ExecutionContext executionContext = new ExecutionContext();
        TransactionalResourceCommand deliveryCommand = null;
        if (!unzip)
        {
            executionContext.put(ExecutionContext.FILE_INPUT_STREAM, inputStream);
            executionContext.put(ExecutionContext.RELATIVE_FILE_PATH_PARAM, fileName);
            deliveryCommand = new DeliverFileCommand(outputDir, renameExtension, overwrite);
        }
        else
        // unzip
        {
            try
            {
                executionContext.put(ExecutionContext.BATCHED_FILE_PROVIDER, new UnzippingFileProvider(inputStream));
            }
            catch (UnzipNotSupportedException e)
            {
                throw new ResourceException("Exception trying to unzip stream", e); //$NON-NLS-1$
            }
            executionContext.put(ExecutionContext.BATCHED_FILE_NAME, fileName);
            deliveryCommand = new DeliverBatchCommand(outputDir, overwrite);
        }
        executeCommand(deliveryCommand, executionContext).getResult();
    }

    /**
     * Execute the command
     * 
     * @param deliveryCommand The command to execute
     * @param executionContext The execution context
     * @return The output of that Execution
     * @throws ResourceException The Exception from the JCA connector
     */
    protected abstract ExecutionOutput executeCommand(TransactionalResourceCommand deliveryCommand,
            ExecutionContext executionContext) throws ResourceException;
    

}
