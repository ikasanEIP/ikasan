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
package org.ikasan.endpoint.ftp.consumer.type;

import java.util.ArrayList;
import java.util.List;

import javax.resource.ResourceException;

import org.ikasan.client.FileTransferConnectionTemplate;
import org.ikasan.common.Payload;
import org.ikasan.endpoint.ftp.consumer.FtpConsumerConfiguration;
import org.ikasan.spec.endpoint.Consumer;

/**
 * Sftp endpoint based on a Payload.
 * @author Ikasan Development Team
 */
public class PayloadBasedFtpConsumer implements Consumer<Payload>
{
    /** existing template */
    protected FileTransferConnectionTemplate fileTransferConnectionTemplate;

    /** configuration */
    protected FtpConsumerConfiguration configuration;

    /**
     * Constructor
     * @param fileTransferConnectionTemplate
     */
    public PayloadBasedFtpConsumer(FileTransferConnectionTemplate fileTransferConnectionTemplate, FtpConsumerConfiguration configuration)
    {
        this.fileTransferConnectionTemplate = fileTransferConnectionTemplate;
        if(fileTransferConnectionTemplate == null)
        {
            throw new IllegalArgumentException("fileTransferConnectionTemplate cannot be 'null'");
        }

        this.configuration = configuration;
        if(configuration == null)
        {
            throw new IllegalArgumentException("configuration cannot be 'null'");
        }
    }
    
    /**
     * Return the first file found as a payload. If no file is available 
     * then null is returned.
     */
    public Payload invoke() throws ResourceException
    {
        Payload payload = null;
        for (String sourceDirectory : this.getSourceDirectories())
        {
            payload = this.fileTransferConnectionTemplate.getDiscoveredFile(
                    sourceDirectory, 
                    this.configuration.getFilenamePattern(), 
                    this.configuration.getRenameOnSuccess(), this.configuration.getRenameOnSuccessExtension(),
                    this.configuration.getMoveOnSuccess(), this.configuration.getMoveOnSuccessNewPath(),
                    this.configuration.getChunking(), this.configuration.getChunkSize(), this.configuration.getChecksum(),
                    this.configuration.getMinAge(), this.configuration.getDestructive(), 
                    this.configuration.getFilterDuplicates(), this.configuration.getFilterOnFilename(),
                    this.configuration.getFilterOnLastModifiedDate(), this.configuration.getChronological());

            if(payload != null)
            {
                return payload;
            }
        }
        
        this.housekeep();
        return payload;
    }
    
    /**
     * Apply any configured housekeeping on this connection template.
     * 
     * @throws ResourceException - Exception if the JCA connector fails
     */
    protected void housekeep() throws ResourceException
    {
        int maxRows = this.configuration.getMaxRows().intValue();
        int ageOfFiles = this.configuration.getAgeOfFiles().intValue();
        
        // If the values have been set then housekeep, else don't
        if(maxRows > -1 && ageOfFiles > -1)
        {
            this.fileTransferConnectionTemplate.housekeep(maxRows, ageOfFiles);
        }
    }

    /**
     * Return a list of src directories to be polled.
     * 
     * @return List of src directories
     */
    protected List<String> getSourceDirectories()
    {
        List<String> dirs = new ArrayList<String>();
        // If we've been passed a factory it means there are multiple directories to
        // poll, starting from this.srcDirectory
        if (this.configuration.getSourceDirectoryURLFactory() != null)
        {
            dirs = this.configuration.getSourceDirectoryURLFactory().getDirectoriesURLs(this.configuration.getSourceDirectory());
        }
        else
        {
            dirs.add(this.configuration.getSourceDirectory());
        }
        return dirs;
    }
    
}
