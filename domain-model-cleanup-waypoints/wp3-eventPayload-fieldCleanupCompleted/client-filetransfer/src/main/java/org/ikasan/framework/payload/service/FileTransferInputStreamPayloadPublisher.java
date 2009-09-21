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
                renameExtension, checksumDelivered, unzip);
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
