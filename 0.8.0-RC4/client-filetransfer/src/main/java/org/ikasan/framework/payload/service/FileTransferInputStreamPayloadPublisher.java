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

import org.ikasan.client.FileTransferConnectionTemplate;
import org.ikasan.common.FilePayloadAttributeNames;
import org.ikasan.common.Payload;

/**
 * <code>PayloadPublisher</code> implementation that delivers a <code>Payload</code> referenced <code>InputStream</code>
 * via the FileTransfer API
 * 
 * @author Ikasan Development Team
 */
public class FileTransferInputStreamPayloadPublisher implements PayloadPublisher
{
    /** A payload input stream acquirer */
    protected PayloadInputStreamAcquirer payloadInputStreamProvider;

    /** Template for using a file transfer connection */
    protected FileTransferConnectionTemplate fileTransferConnectionTemplate;

    /** The out put directory to publish to */
    protected String outputDir;

    /** The file extension to rename to */
    protected String renameExtension;

    /** Overwrite existing file flag */
    protected boolean overwrite = false;

    /** Unzip flag */
    protected boolean unzip = false;

    /** Deliver using a checksum flag */
    protected boolean checksumDelivered = false;
    
    /** dont allow payload driven methods to create missing parent dirs on file delivery 
     * as we really want to discourage Payload delivery usage - use InputStream instead. */
    protected boolean createParentDirectory = false;

    /**
     * Constructor
     * 
     * @param payloadInputStreamProvider - The input stream for the payload
     * @param outputDir - The directory to put the file to
     * @param renameExtension - The file extension for temporary renaming of the file
     * @param connectionFactory - The connection factory
     * @param connectionSpec - The connection specification
     */
    public FileTransferInputStreamPayloadPublisher(PayloadInputStreamAcquirer payloadInputStreamProvider,
            String outputDir, String renameExtension, ConnectionFactory connectionFactory, ConnectionSpec connectionSpec)
    {
        super();
        this.payloadInputStreamProvider = payloadInputStreamProvider;
        this.outputDir = outputDir;
        this.renameExtension = renameExtension;
        this.fileTransferConnectionTemplate = new FileTransferConnectionTemplate(connectionFactory, connectionSpec);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.ikasan.framework.payload.service.PayloadPublisher#publish(org.ikasan.common.Payload)
     */
    public void publish(Payload payload) throws ResourceException
    {
        try
        {
            InputStream inputStream = payloadInputStreamProvider.acquireInputStream(payload);
            fileTransferConnectionTemplate.deliverInputStream(inputStream, payload.getAttribute(FilePayloadAttributeNames.FILE_NAME), outputDir, overwrite,
                renameExtension, checksumDelivered, unzip, createParentDirectory);
        }
        catch (IOException e)
        {
            throw new ResourceException(e);
        }
    }

    /**
     * Accessor for payloadInputStreamProvider
     * 
     * @return payloadInputStreamProvider
     */
    public PayloadInputStreamAcquirer getPayloadInputStreamProvider()
    {
        return payloadInputStreamProvider;
    }

    /**
     * Accessor for overwrite
     * 
     * @return overwrite
     */
    public boolean isOverwrite()
    {
        return overwrite;
    }

    /**
     * Mutator for overwrite
     * 
     * @param overwrite - overwrite flag to set
     */
    public void setOverwrite(boolean overwrite)
    {
        this.overwrite = overwrite;
    }

    /**
     * Accessor for unzip
     * 
     * @return unzip
     */
    public boolean isUnzip()
    {
        return unzip;
    }

    /**
     * Mutator for unzip
     * 
     * @param unzip - unzip flag to set
     */
    public void setUnzip(boolean unzip)
    {
        this.unzip = unzip;
    }

    /**
     * Accessor for checksumDelivered
     * 
     * @return checksumDelivered
     */
    public boolean isChecksumDelivered()
    {
        return checksumDelivered;
    }

    /**
     * Mutator for checksumDelivered
     * 
     * @param checksumDelivered - checksum delivered flag to set
     */
    public void setChecksumDelivered(boolean checksumDelivered)
    {
        this.checksumDelivered = checksumDelivered;
    }

    /**
     * Accessor for outputDir
     * 
     * @return outputDir
     */
    public String getOutputDir()
    {
        return outputDir;
    }

    /**
     * Accessor for renameExtension
     * 
     * @return renameExtension
     */
    public String getRenameExtension()
    {
        return renameExtension;
    }
}
