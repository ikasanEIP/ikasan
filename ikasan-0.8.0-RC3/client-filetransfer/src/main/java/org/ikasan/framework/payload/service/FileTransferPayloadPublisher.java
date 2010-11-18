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

import java.util.Map;

import javax.resource.ResourceException;
import javax.resource.cci.ConnectionFactory;
import javax.resource.cci.ConnectionSpec;

import org.ikasan.client.FileTransferConnectionTemplate;
import org.ikasan.common.Payload;

/**
 * @author Ikasan Development Team
 * @deprecated - use Ikasan Client Endpoint implementation
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
