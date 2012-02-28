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
package org.ikasan.framework.payload.service;

import java.io.IOException;
import java.io.InputStream;

import javax.resource.ResourceException;
import javax.resource.cci.ConnectionFactory;
import javax.resource.cci.ConnectionSpec;

import org.apache.log4j.Logger;
import org.ikasan.client.FileTransferConnectionTemplate;
import org.ikasan.common.FilePayloadAttributeNames;
import org.ikasan.common.Payload;

/**
 * <code>PayloadPublisher</code> implementation that delivers a <code>Payload</code> referenced <code>InputStream</code>
 * via the FileTransfer API. This class supports two alternate connections. The first defined connection is set as the
 * active connection and remains so until any exception on delivery. On exception the active connection is changed to
 * the alternate connection for the next attempt. If successful the active connection remains unchanged. If unsuccessful
 * the active connection is flipped between the two supplied connections.
 * 
 * NOTE: The payload is only successfully delivered on <b>one</b> connection.
 * 
 * @author Ikasan Development Team
 * @deprecated - use Ikasan Client Endpoint implementation
 */
public class FileTransferAlternateConnectionPayloadPublisher extends FileTransferInputStreamPayloadPublisher
{
    /** Logger */
    private static Logger logger = Logger.getLogger(FileTransferAlternateConnectionPayloadPublisher.class);

    /** Alternate template for using a file transfer connection */
    private FileTransferConnectionTemplate alternateFileTransferConnectionTemplate;

    /** Current active template will point to either connection template */
    private FileTransferConnectionTemplate activeFileTransferConnectionTemplate;

    /**
     * Constructor
     * 
     * @param payloadInputStreamProvider - The input stream for the payload
     * @param outputDir - The directory to put the file to
     * @param renameExtension - The file extension for temporary renaming of the file
     * @param connectionFactory - The connection factory
     * @param connectionSpec - The connection specification
     * @param alternateConnectionSpec - The alternate connection specification (for a secondary server)
     */
    public FileTransferAlternateConnectionPayloadPublisher(PayloadInputStreamAcquirer payloadInputStreamProvider,
            String outputDir, String renameExtension, ConnectionFactory connectionFactory,
            ConnectionSpec connectionSpec, ConnectionSpec alternateConnectionSpec)
    {
        super(payloadInputStreamProvider, outputDir, renameExtension, connectionFactory, connectionSpec);
        this.alternateFileTransferConnectionTemplate = new FileTransferConnectionTemplate(connectionFactory,
            alternateConnectionSpec);
        this.activeFileTransferConnectionTemplate = this.fileTransferConnectionTemplate;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.ikasan.framework.payload.service.FileTransferInputStreamPayloadPublisher#publish(org.ikasan.common.Payload)
     */
    @Override
    public void publish(Payload payload) throws ResourceException
    {
        try
        {
            InputStream inputStream = payloadInputStreamProvider.acquireInputStream(payload);
            try
            {
                activeFileTransferConnectionTemplate.deliverInputStream(inputStream, payload.getAttribute(FilePayloadAttributeNames.FILE_NAME), outputDir,
                    overwrite, renameExtension, checksumDelivered, unzip, createParentDirectory, tempFileName);
            }
            catch (ResourceException e)
            {
                switchActiveConnection();
                throw e;
            }
        }
        catch (IOException e)
        {
            throw new ResourceException(e);
        }
    }

    /**
     * Switch the active connection to the other connection template.
     */
    private void switchActiveConnection()
    {
        if (activeFileTransferConnectionTemplate == fileTransferConnectionTemplate)
        {
            activeFileTransferConnectionTemplate = alternateFileTransferConnectionTemplate;
            if (logger.isDebugEnabled())
            {
                logger.debug("Exception on active connection. "
                        + "Will use secondary connection as active on next attempt.");
            }
        }
        else
        {
            activeFileTransferConnectionTemplate = fileTransferConnectionTemplate;
            if (logger.isDebugEnabled())
            {
                logger.debug("Exception on active connection. "
                        + "Will use primary connection as active on next attempt.");
            }
        }
    }
}
