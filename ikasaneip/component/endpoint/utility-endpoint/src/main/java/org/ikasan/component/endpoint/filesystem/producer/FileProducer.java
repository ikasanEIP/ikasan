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
package org.ikasan.component.endpoint.filesystem.producer;

import org.apache.commons.io.FileUtils;
import org.ikasan.spec.component.endpoint.EndpointException;
import org.ikasan.spec.component.endpoint.Producer;
import org.ikasan.spec.configuration.ConfiguredResource;
import org.ikasan.spec.flow.FlowEvent;

import java.io.File;
import java.io.IOException;
import java.util.Collection;

/**
 * File producer.
 * 
 * @author mitcje
 */
public class FileProducer<T> implements Producer<T>, ConfiguredResource<FileProducerConfiguration>
{
    // configured resource identifier
    String configurationId;

    // configuration bean
    FileProducerConfiguration configuration;

    @Override
    public String getConfiguredResourceId() {
        return this.configurationId;
    }

    @Override
    public void setConfiguredResourceId(String configurationId) {
        this.configurationId = configurationId;
    }

    @Override
    public FileProducerConfiguration getConfiguration() {
        return configuration;
    }

    @Override
    public void setConfiguration(FileProducerConfiguration configuration) {
        this.configuration = configuration;
    }

    @Override
    public void invoke(T message) throws EndpointException
    {
        if(message instanceof FlowEvent)
        {
            this.process(((FlowEvent) message).getPayload());
        }
        else
        {
            this.process(message);
        }
    }

    protected void process(Object payload)
    {
        try
        {
            File file = new File(configuration.getFilename());
            if(file.exists())
            {
                if(!configuration.isOverwrite())
                {
                    throw new EndpointException("File [" + configuration.getFilename() + "] already exists and overwrite option is set to [" + configuration.isOverwrite() + "]");
                }

                file.delete();
            }

            if(configuration.isUseTempFile())
            {
                File tempFile = new File(configuration.getTempFilename());
                this.writeFile(tempFile, payload);
                FileUtils.moveFile(tempFile, file);
            }
            else
            {
                this.writeFile(file, payload);
            }

            if(configuration.isWriteChecksum())
            {
                long checksum = FileUtils.checksumCRC32(file);
                File checksumFile = new File(configuration.getFilename() + ".cr32");
                FileUtils.writeStringToFile(checksumFile, String.valueOf(checksum), configuration.getEncoding());
            }
        }
        catch (IOException e)
        {
            throw new EndpointException(e);
        }
    }

    /**
     * Write the payload to the file based on the incoming payload type.
     * @param file
     * @param payload
     * @throws IOException
     */
    protected void writeFile(File file, Object payload) throws IOException
    {
        if(payload instanceof String)
        {
            FileUtils.writeStringToFile(file, (String)payload, configuration.getEncoding());
        }
        else if(payload instanceof byte[])
        {
            FileUtils.writeByteArrayToFile(file, (byte[])payload);
        }
        if(payload instanceof Collection)
        {
            if(configuration.getLineEnding() != null)
            {
                FileUtils.writeLines(file, (Collection)payload, configuration.getLineEnding());
            }
            else
            {
                FileUtils.writeLines(file, (Collection)payload);
            }
        }

    }
}
