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
package org.ikasan.connector.basefiletransfer.outbound;

import java.io.InputStream;

import javax.resource.ResourceException;
import javax.resource.spi.ManagedConnection;

import org.ikasan.connector.base.outbound.EISConnectionImpl;

import org.ikasan.connector.base.command.ExecutionContext;
import org.ikasan.connector.base.command.ExecutionOutput;
import org.ikasan.connector.base.command.TransactionalResourceCommand;
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
