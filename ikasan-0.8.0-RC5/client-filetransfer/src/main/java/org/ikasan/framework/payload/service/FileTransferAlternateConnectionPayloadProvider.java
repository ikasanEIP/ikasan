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
