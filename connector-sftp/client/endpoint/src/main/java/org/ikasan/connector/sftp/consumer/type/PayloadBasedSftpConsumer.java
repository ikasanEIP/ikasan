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
package org.ikasan.connector.sftp.consumer.type;

import javax.resource.ResourceException;

import org.ikasan.common.Payload;
import org.ikasan.connector.sftp.consumer.SftpConsumerConfiguration;
import org.ikasan.framework.payload.service.FileTransferPayloadProvider;
import org.ikasan.spec.endpoint.Consumer;

/**
 * Sftp endpoint based on a Payload.
 * @author Ikasan Development Team
 */
public class PayloadBasedSftpConsumer implements Consumer<Payload>
{
    /** existing template */
    private FileTransferPayloadProvider fileTransferPayloadProvider;
    
    /** configuration */
    private SftpConsumerConfiguration configuration;

    /**
     * Constructor
     * @param fileTransferConnectionTemplate
     */
    public PayloadBasedSftpConsumer(FileTransferPayloadProvider fileTransferPayloadProvider, SftpConsumerConfiguration configuration)
    {
        this.fileTransferPayloadProvider = fileTransferPayloadProvider;
        if(fileTransferPayloadProvider == null)
        {
            throw new IllegalArgumentException("fileTransferPayloadProvider cannot be 'null'");
        }

        this.configuration = configuration;
        if(configuration == null)
        {
            throw new IllegalArgumentException("configuration cannot be 'null'");
        }
    }
    
    /**
     * Return a map of filename and content entries. If no file is available for
     * return then null is returned.
     */
    public Payload invoke() throws ResourceException
    {
        return this.fileTransferPayloadProvider.getFileTransferConnectionTemplate().getDiscoveredFile(
            configuration.getSourceDirectory(), configuration.getFilenamePattern(), 
            configuration.getRenameOnSuccess(), configuration.getRenameOnSuccessExtension(),
            configuration.getMoveOnSuccess(), configuration.getMoveOnSuccessNewPath(),
            configuration.getChunking(), configuration.getChunkSize(), configuration.getChecksum(),
            configuration.getMinAge(), configuration.getDestructive(), 
            configuration.getFilterDuplicates(), configuration.getFilterOnFilename(),
            configuration.getFilterOnLastModifiedDate(), configuration.getChronological());
    }
}
