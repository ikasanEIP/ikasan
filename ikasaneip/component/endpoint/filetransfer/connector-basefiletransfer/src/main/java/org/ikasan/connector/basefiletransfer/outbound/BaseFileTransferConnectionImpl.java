/*
 * $Id:$
 * $URL:$
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
import java.util.ArrayList;
import java.util.List;
import javax.resource.NotSupportedException;
import javax.resource.ResourceException;
import javax.resource.cci.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.ikasan.connector.BaseFileTransferConnection;
import org.ikasan.filetransfer.FilePayloadAttributeNames;
import org.ikasan.filetransfer.Payload;
import org.ikasan.filetransfer.factory.PayloadFactory;
import org.ikasan.filetransfer.factory.PayloadFactoryImpl;
import org.ikasan.connector.base.command.ExecutionContext;
import org.ikasan.connector.base.command.ExecutionOutput;
import org.ikasan.connector.base.command.TransactionalResourceCommand;
import org.ikasan.connector.base.outbound.EISConnectionImpl;
import org.ikasan.connector.basefiletransfer.outbound.command.DeliverBatchCommand;
import org.ikasan.connector.basefiletransfer.outbound.command.DeliverFileCommand;
import org.ikasan.connector.basefiletransfer.outbound.command.util.UnzipNotSupportedException;
import org.ikasan.connector.basefiletransfer.outbound.command.util.UnzippingFileProvider;
import org.ikasan.connector.listener.TransactionCommitFailureListener;
import org.ikasan.connector.listener.TransactionCommitFailureObserverable;
import org.ikasan.connector.util.chunking.model.FileChunkHeader;

/**
 * A Base implementation for File transfer connections
 * @author Ikasan Development Team
 */
public abstract class BaseFileTransferConnectionImpl implements BaseFileTransferConnection,Connection
{
    /** The logger instance. */
    private static Logger logger = LoggerFactory.getLogger(BaseFileTransferConnectionImpl.class);

	protected static final String REFERENCE_PAYLOAD_ATTRIBUTE = "referencePayload";
	
	private PayloadFactory payloadFactory = new PayloadFactoryImpl();

	protected List<TransactionCommitFailureListener> listeners = new ArrayList<TransactionCommitFailureListener>();

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
            String renameExtension, boolean checksumDelivered, boolean unzip, boolean createParentDirectory,
            final String tempFileName) throws ResourceException
    {
        ExecutionContext executionContext = new ExecutionContext();
        TransactionalResourceCommand deliveryCommand = null;
        if (!unzip)
        {
            executionContext.put(ExecutionContext.FILE_INPUT_STREAM, inputStream);
            executionContext.put(ExecutionContext.RELATIVE_FILE_PATH_PARAM, fileName);
            deliveryCommand = new DeliverFileCommand(outputDir, renameExtension, overwrite, createParentDirectory, tempFileName);
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
        this.addListenersToCommand(deliveryCommand);
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
    
    /**
     * Method used to map an <code>FTPMappedRecord</code> object to a
     * <code>Payload</code> object.
     * 
     * TODO Is there any other Payload stuff to set here?
     * 
     * @param header The record as returned from the FileTransferProtocolClient
     * @return A payload constructed from the record.
     */
    protected  Payload fileChunkHeaderToPayload(FileChunkHeader header)
    {    	
    	String paylaodId = ""+header.getFileName().hashCode();

    	Payload payload = payloadFactory.newPayload(paylaodId,  header.toXml().getBytes());
        
        payload.setAttribute(REFERENCE_PAYLOAD_ATTRIBUTE, Boolean.TRUE.toString());
        payload.setAttribute(FilePayloadAttributeNames.FILE_NAME, header.getFileName());

        

//        // need to do checksumming
//        payload.setChecksum(header.getInternalMd5Hash());
//        payload.setChecksumAlg(Md5ChecksumSupplier.MD5);
        return payload;
    }
    
    /**
     * Determines if we need to handle this as a chunk reference
     * 
     * @param payload - The payload to check
     * @return true if the payload is simply a reference to a set of chunks
     */
    protected boolean isChunkReference(Payload payload)
    {
        boolean result = false;
        String referencePayloadAttribute = payload.getAttribute(REFERENCE_PAYLOAD_ATTRIBUTE);
        
        if(referencePayloadAttribute != null && referencePayloadAttribute.equals(Boolean.TRUE.toString()))
        {
            result = true;
        }
        return result;
    }

    /**
     * 
     * @param command
     */
    protected void addListenersToCommand(TransactionalResourceCommand command)
    {
        logger.info("Attempting to add listeners to the command!");
        for(TransactionCommitFailureListener listener: this.listeners)
        {
            logger.info("Adding listener: " + listener);
            command.addListener(listener);
        }
    }

    /* (non-Javadoc)
     * @see org.ikasan.client.listener.TransactionCommitFailureObserverable#addListener(org.ikasan.client.listener.TransactionCommitFailureListener)
     */
    @Override
    public void addListener(TransactionCommitFailureListener listener)
    {
        if(!listeners.contains(listener))
        {
            logger.info("Adding listener to connection: " + listener);
            this.listeners.add(listener);
        }
    }

    /**
     * Connection Methods
     *
     */
    public Interaction createInteraction()
    {
        return null;
    }

    public LocalTransaction getLocalTransaction() throws ResourceException
    {
        throw new NotSupportedException("CCI Local Transaction is not supported."); //$NON-NLS-1$
    }

    public ConnectionMetaData getMetaData()
    {
        // TODO Auto-generated method stub
        return null;
    }

    public ResultSetInfo getResultSetInfo()
    {
        // TODO Auto-generated method stub
        return null;
    }
}
