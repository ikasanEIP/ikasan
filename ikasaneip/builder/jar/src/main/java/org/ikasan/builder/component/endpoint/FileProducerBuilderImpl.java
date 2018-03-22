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
package org.ikasan.builder.component.endpoint;


import org.ikasan.component.endpoint.filesystem.producer.FileProducer;
import org.ikasan.component.endpoint.filesystem.producer.FileProducerConfiguration;
import org.ikasan.spec.component.endpoint.Producer;

/**
 * Ikasan provided file producer builder default implementation.
 *
 * @author Ikasan Development Team
 */
public class FileProducerBuilderImpl implements FileProducerBuilder
{
    FileProducer fileProducer = new FileProducer();

    @Override
    public FileProducerBuilder setConfiguredResourceId(String configuredResourceId)
    {
        this.fileProducer.setConfiguredResourceId(configuredResourceId);
        return this;
    }

    @Override
    public FileProducerBuilder setConfiguration(FileProducerConfiguration fileProducerConfiguration)
    {
        this.fileProducer.setConfiguration(fileProducerConfiguration);
        return this;
    }

    @Override
    public FileProducerBuilder setFilename(String filename)
    {
        this.fileProducer.getConfiguration().setFilename(filename);
        return this;
    }

    @Override
    public FileProducerBuilder setTempFilename(String tempFilename)
    {
        this.fileProducer.getConfiguration().setTempFilename(tempFilename);
        return this;
    }

    @Override
    public FileProducerBuilder setUseTempFile(boolean useTempFile)
    {
        this.fileProducer.getConfiguration().setUseTempFile(useTempFile);
        return this;
    }

    @Override
    public FileProducerBuilder setWriteChecksum(boolean writeChecksum)
    {
        this.fileProducer.getConfiguration().setWriteChecksum(writeChecksum);
        return this;
    }

    @Override
    public FileProducerBuilder setEncoding(String encoding)
    {
        this.fileProducer.getConfiguration().setEncoding(encoding);
        return this;
    }

    @Override
    public FileProducerBuilder setOverwrite(boolean overwrite)
    {
        this.fileProducer.getConfiguration().setOverwrite(overwrite);
        return this;
    }

    @Override
    public FileProducerBuilder setLineEnding(String lineEnding)
    {
        this.fileProducer.getConfiguration().setLineEnding(lineEnding);
        return this;
    }

    @Override
    public Producer build()
    {
        return fileProducer;
    }

}

