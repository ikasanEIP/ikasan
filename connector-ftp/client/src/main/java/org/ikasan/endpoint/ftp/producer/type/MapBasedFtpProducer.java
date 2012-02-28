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
package org.ikasan.endpoint.ftp.producer.type;

import java.io.InputStream;
import java.util.Map;

import javax.resource.ResourceException;

import org.ikasan.client.FileTransferConnectionTemplate;
import org.ikasan.endpoint.ftp.producer.FtpProducerConfiguration;
import org.ikasan.spec.endpoint.Producer;


/**
 * Sftp producer based on a Map<String,InputStream>.
 * This implementation is provided as a stepping stone to moving away from
 * the FileTransfer client approach. Ideally, this class should be interacting
 * with the SFTP API and not another File Transfer client layer. TODO!
 * @author Ikasan Development Team
 */
public class MapBasedFtpProducer implements Producer<Map<String,InputStream>>
{
    /** Currently active connection template */
    protected FileTransferConnectionTemplate activeFileTransferConnectionTemplate;

    /** A connection template */
    protected FileTransferConnectionTemplate fileTransferConnectionTemplate;

    /** Alternate template to be used in cases of failure */
    protected FileTransferConnectionTemplate alternateFileTransferConnectionTemplate;

    /** Configuration */
    protected FtpProducerConfiguration ftpProducerConfiguration;

    /**
     * Constructor
     * @param fileTransferConnectionTemplate a {@link FileTransferConnectionTemplate}
     * @param sftpProducerConfiguration the Endpoint configuration
     */
    public MapBasedFtpProducer(FileTransferConnectionTemplate fileTransferConnectionTemplate, FtpProducerConfiguration sftpProducerConfiguration)
    {
        this.fileTransferConnectionTemplate = fileTransferConnectionTemplate;
        if(this.fileTransferConnectionTemplate == null)
        {
            throw new IllegalArgumentException("fileTransferConnectionTemplate cannot be 'null'");
        }
        this.activeFileTransferConnectionTemplate = this.fileTransferConnectionTemplate;

        this.ftpProducerConfiguration = sftpProducerConfiguration;
        if(sftpProducerConfiguration == null)
        {
            throw new IllegalArgumentException("sftpProducerConfiguration cannot be 'null'");
        }
    }
    
    /**
     * Invoke delivery based on passing a map of String (filename) and InputStream (content) pairs
     * to allow compatibility with the existing FileTransferConnectionTemplate.
     * 
     * @param filenameContentPairs file content to deliver
     */
    public void invoke(Map<String,InputStream> filenameContentPairs) throws ResourceException
    {
        for(Map.Entry<String,InputStream> filenameContent : filenameContentPairs.entrySet())
        {
            try
            {
                this.activeFileTransferConnectionTemplate.deliverInputStream(filenameContent.getValue(), filenameContent.getKey(), 
                    this.ftpProducerConfiguration.getOutputDirectory(), this.ftpProducerConfiguration.getOverwrite().booleanValue(), 
                    this.ftpProducerConfiguration.getRenameExtension(), this.ftpProducerConfiguration.getChecksumDelivered().booleanValue(), 
                    this.ftpProducerConfiguration.getUnzip().booleanValue(), this.ftpProducerConfiguration.getCreateParentDirectory().booleanValue(), 
                    this.ftpProducerConfiguration.getTempFileName());
            }
            catch (ResourceException e)
            {
                this.switchActiveConnection();
                throw e;
            }
        }
    }

    /**
     * 
     * @param alternate the {@link FileTransferConnectionTemplate} alternate to use
     */
    public void setAlternateFileTransferConnectionTemplate(final FileTransferConnectionTemplate alternate)
    {
        this.alternateFileTransferConnectionTemplate = alternate;
    }

    /**
     * This method is only used for testing purposes
     * @return the alternateFileTransferConnectionTemplate
     */
    FileTransferConnectionTemplate getAlternateFileTransferConnectionTemplate()
    {
        return this.alternateFileTransferConnectionTemplate;
    }

    /**
     * This method is only used for testing purposes
     * @return the activeFileTransferConnectionTemplate
     */
    FileTransferConnectionTemplate getActiveFileTransferConnectionTemplate()
    {
        return this.activeFileTransferConnectionTemplate;
    }

    /**
     * Switch the active connection to the other connection template.
     */
    protected void switchActiveConnection()
    {
        if (this.alternateFileTransferConnectionTemplate != null)
        {
            if(this.activeFileTransferConnectionTemplate == this.fileTransferConnectionTemplate)
            {
                this.activeFileTransferConnectionTemplate = this.alternateFileTransferConnectionTemplate;
            }
            else
            {
                this.activeFileTransferConnectionTemplate = this.fileTransferConnectionTemplate;
            }
        }
    }
}

