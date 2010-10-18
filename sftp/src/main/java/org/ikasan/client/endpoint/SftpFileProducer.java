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
package org.ikasan.client.endpoint;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import javax.resource.ResourceException;

import org.ikasan.client.FileTransferConnectionTemplate;
import org.ikasan.client.configuration.SftpConfiguration;
import org.ikasan.client.endpoint.Producer;

/**
 * @author Ikasan Development Team
 */
public class SftpFileProducer implements Producer<File>, ManagedEndpoint
{
    /** existing template */
    private FileTransferConnectionTemplate fileTransferConnectionTemplate;
    
    /** configuration */
    private SftpConfiguration configuration;

    /**
     * Constructor
     * @param fileTransferConnectionTemplate
     */
    public SftpFileProducer(FileTransferConnectionTemplate fileTransferConnectionTemplate, SftpConfiguration configuration)
    {
        this.fileTransferConnectionTemplate = fileTransferConnectionTemplate;
        this.configuration = configuration;
    }
    
    /**
     * Invoke delivery
     */
    public void invoke(File file) throws ResourceException
    {
        try
        {
            InputStream inputStream = new FileInputStream(file);
            this.fileTransferConnectionTemplate.deliverInputStream(inputStream, file.getName(), configuration.getOutputDirectory(),
                    configuration.getOverwrite(), configuration.getRenameExtension(), configuration.getChecksumDelivered(), 
                    configuration.getUnzip());
        }
        catch(FileNotFoundException e)
        {
            throw new ResourceException(e);
        }
    }
}
