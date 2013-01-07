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

import org.apache.log4j.Logger;
import org.ikasan.client.FileTransferConnectionTemplate;
import org.ikasan.common.Payload;
import org.ikasan.framework.payload.service.PayloadInputStreamAcquirer;

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
                activeFileTransferConnectionTemplate.deliverInputStream(inputStream, payload.getName(), outputDir,
                    overwrite, renameExtension, checksumDelivered, unzip);
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
