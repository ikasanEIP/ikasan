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

import java.util.ArrayList;
import java.util.List;

import javax.resource.ResourceException;
import javax.resource.cci.ConnectionSpec;

import org.apache.log4j.Logger;
import org.ikasan.client.FileTransferConnectionTemplate;
import org.ikasan.common.Payload;
import org.ikasan.connector.base.outbound.EISConnectionFactory;

/**
 * <code>PayloadProvider</code> implementation that delivers a <code>Payload</code>
 * via the FileTransfer API.
 * This class supports two alternate connections.
 * The first defined connection is set as the active connection and remains so
 * until any exception on delivery. On exception the active connection is
 * changed to the alternate connection for the next attempt. If successful
 * the active connection remains unchanged. If unsuccessful the active
 * connection is flipped between the two supplied connections.
 *
 * NOTE: The payload is only sourced from one <b>one</b> connection
 * as the same sourced <code>payload</code> from an alternate connection
 * should be treated as duplicate and filtered out.
 *
 * @author Ikasan Development Team
 */
public class FileTransferAlternateConnectionPayloadProvider
    extends FileTransferPayloadProvider
{
    /** Logger */
    private static Logger logger = Logger.getLogger(FileTransferAlternateConnectionPayloadProvider.class);
    /** The alternate file transfer connection template */
    private FileTransferConnectionTemplate alternateFileTransferConnectionTemplate;
    /** The active file transfer connection template */
    private FileTransferConnectionTemplate activeFileTransferConnectionTemplate;

    /**
     * Constructor
     * 
     * @param srcDirectory The directory to get the file from
     * @param filenamePattern The filename pattern to search on
     * @param connectionFactory The connection factory
     * @param connectionSpec The connection spec
     * @param alternateConnectionSpec The alternative connection spec
     */
    public FileTransferAlternateConnectionPayloadProvider(String srcDirectory,
            String filenamePattern,
            EISConnectionFactory connectionFactory,
            ConnectionSpec connectionSpec, ConnectionSpec alternateConnectionSpec)
    {
        super(srcDirectory, filenamePattern, connectionFactory, connectionSpec);
        this.alternateFileTransferConnectionTemplate = new FileTransferConnectionTemplate(
            connectionFactory, alternateConnectionSpec);

        // default the active template
        this.activeFileTransferConnectionTemplate = this.fileTransferConnectionTemplate;
    }

    /* (non-Javadoc)
     * @see org.ikasan.framework.payload.service.FileTransferPayloadProvider#getNextRelatedPayloads()
     */
    @Override
    public List<Payload> getNextRelatedPayloads()
        throws ResourceException
    {
        validateConfiguration();

        List<Payload> result = null;
        List<String> dirs = this.getSrcDirs();

        for (String source : dirs)
        {
            try
            {
                Payload discoveredFile = activeFileTransferConnectionTemplate
                    .getDiscoveredFile(source, filenamePattern, renameOnSuccess,
                        renameOnSuccessExtension, moveOnSuccess, moveOnSuccessNewPath,
                        chunking, chunkSize, checksum, minAge, destructive, filterDuplicates,
                        filterOnFilename, filterOnLastModifiedDate, chronological);

                if (discoveredFile != null)
                {
                    result = new ArrayList<Payload>();
                    result.add(discoveredFile);
                    break;
                }
            }
            catch(ResourceException e)
            {
                switchActiveConnection();
                throw e;
            }
        }

        this.housekeep();
        return result;
    }

    /**
     * Get the file connection transfer template
     * @return the active file connection transfer template
     */
    @Override
    public FileTransferConnectionTemplate getFileTransferConnectionTemplate()
    {
        return this.activeFileTransferConnectionTemplate;
    }

    /**
     * Apply any configured housekeeping on this connection template.
     * @throws ResourceException Exception thrown by connector
     */
    @Override
    protected void housekeep()
        throws ResourceException
    {
        // If the values have been set then housekeep, else don't
        if (this.maxRows > -1 && this.ageOfFiles > -1)
        {
            activeFileTransferConnectionTemplate.housekeep(this.maxRows, this.ageOfFiles);
        }
        else
        {
            logger.debug("FileFilter Housekeeping is not configured");
        }
    }

    /**
     * Switch the active connection to the other connection template.
     */
    protected void switchActiveConnection()
    {
        if(activeFileTransferConnectionTemplate == fileTransferConnectionTemplate)
        {
            activeFileTransferConnectionTemplate = alternateFileTransferConnectionTemplate;
            if(logger.isDebugEnabled())
            {
                logger.debug("Exception on active connection. "
                    + "Will use secondary connection as active on next attempt.");
            }
        }
        else
        {
            activeFileTransferConnectionTemplate = fileTransferConnectionTemplate;
            if(logger.isDebugEnabled())
            {
                logger.debug("Exception on active connection. "
                    + "Will use primary connection as active on next attempt.");
            }
        }
    }

}
