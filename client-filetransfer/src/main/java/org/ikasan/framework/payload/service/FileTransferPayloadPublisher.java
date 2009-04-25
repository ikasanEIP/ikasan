/* 
 * $Id: FileTransferPayloadPublisher.java 16744 2009-04-22 10:05:52Z mitcje $
 * $URL: svn+ssh://svc-vcsp/architecture/ikasan/trunk/client-filetransfer/src/main/java/org/ikasan/framework/payload/service/FileTransferPayloadPublisher.java $
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

import java.util.Map;

import javax.resource.ResourceException;
import javax.resource.cci.ConnectionFactory;
import javax.resource.cci.ConnectionSpec;

import org.ikasan.client.FileTransferConnectionTemplate;
import org.ikasan.common.Payload;

/**
 * @author Ikasan Development Team
 * 
 */
public class FileTransferPayloadPublisher implements PayloadPublisher
{
    /**
     * Flag defining whether this attempt should be made to unzip delivered
     * content
     */
    private boolean unzip = false;
    /**
     * Flag defining whether we should reload and checksum the delivered file,
     * against the checksum in the Payload
     */
    private boolean checksumDelivered = false;
    /**
     * Flag defining whether any existing file should be overwritten if/when
     * clashed occur with delivered files
     */
    private boolean overwrite = false;
    /** Dir path on remote system to deliver files */
    private String outputDir = null;
    /** File extension for partially delivered files */
    private String renameExtension = ".temp";
    /** Cleanup any file chunks after delivery */
    private boolean cleanup = true;
    /**
     * Map of sub directories to which files should be delivered, keyed by
     * regular expression, matched to the file name
     * 
     * Note that if this is null, or there is no match for a given filename
     * within the regular expressions in this map, then files are delivered
     * straight into the outputDir
     */
    private Map<String, String> outputTargets = null;
    /**
     * template for using a file transfer connection
     */
    private FileTransferConnectionTemplate fileTransferConnectionTemplate;

    /**
     * Constructor
     * @param outputDir The output directory
     * @param renameExtension The extension we'll rename the file to
     * @param connectionFactory The connection factory 
     * @param connectionSpec The connection spec
     */
    public FileTransferPayloadPublisher(String outputDir, String renameExtension, ConnectionFactory connectionFactory, ConnectionSpec connectionSpec)
    {
        super();
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
        fileTransferConnectionTemplate.deliverPayload(payload, outputDir, outputTargets, overwrite, renameExtension, checksumDelivered, unzip, cleanup);
    }

    /**
     * Return true if we are unzipping
     * @return true if we are unzipping else false
     */
    public boolean isUnzip()
    {
        return unzip;
    }

    /**
     * Set the unzip flag
     * @param unzip unzip flag
     */
    public void setUnzip(boolean unzip)
    {
        this.unzip = unzip;
    }

    /**
     * Return true if we are delivering and checksumming
     * @return true if we are delivering and checksumming else false
     */
    public boolean isChecksumDelivered()
    {
        return checksumDelivered;
    }

    /**
     * Set the checksumDelivered flag 
     * @param checksumDelivered flag
     */
    public void setChecksumDelivered(boolean checksumDelivered)
    {
        this.checksumDelivered = checksumDelivered;
    }

    /**
     * Return true if we are overwriting
     * @return true if we are overwriting else false
     */
    public boolean isOverwrite()
    {
        return overwrite;
    }

    /**
     * Set the overwrite flag
     * @param overwrite flag
     */
    public void setOverwrite(boolean overwrite)
    {
        this.overwrite = overwrite;
    }

    /**
     * Return true if we are cleaning up after delivery
     * @return true if we are cleaning up after delivery else false
     */
    public boolean isCleanup()
    {
        return cleanup;
    }

    /**
     * Set the cleanup flag
     * @param cleanup flag
     */
    public void setCleanup(boolean cleanup)
    {
        this.cleanup = cleanup;
    }

    /**
     * Get the output targets
     * @return the output targets
     */
    public Map<String, String> getOutputTargets()
    {
        return outputTargets;
    }

    /**
     * Set the output targets
     * @param outputTargets list of output targets to set
     */
    public void setOutputTargets(Map<String, String> outputTargets)
    {
        this.outputTargets = outputTargets;
    }
}
