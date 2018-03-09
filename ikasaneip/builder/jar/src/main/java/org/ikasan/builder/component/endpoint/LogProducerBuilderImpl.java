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

import org.ikasan.component.endpoint.util.producer.LogProducer;
import org.ikasan.component.endpoint.util.producer.LogProducerConfiguration;
import org.ikasan.spec.component.endpoint.Producer;
import org.ikasan.spec.configuration.ConfiguredResource;

/**
 * Ikasan provided logging producer builder implementation.
 *
 * @author Ikasan Development Team
 */
public class LogProducerBuilderImpl implements LogProducerBuilder
{
    LogProducer logProducer;

    public LogProducerBuilderImpl(LogProducer logProducer)
    {
        this.logProducer = logProducer;
        if(logProducer == null)
        {
            throw new IllegalArgumentException("logProducer cannot be 'null'");
        }
    }

    /**
     * Build component.
     * @return
     */
    public Producer build()
    {
        return logProducer;
    }

    @Override
    public LogProducerBuilder setReplacementText(String replacementText)
    {
        this.logProducer.getConfiguration().setReplacementText(replacementText);
        return this;
    }

    @Override
    public LogProducerBuilder setRegExpPattern(String regExpPattern)
    {
        this.logProducer.getConfiguration().setRegExpPattern(regExpPattern);
        return null;
    }

    @Override
    public LogProducerBuilder setConfiguredResourceId(String configuredResourceId)
    {
        ((ConfiguredResource)this.logProducer).setConfiguredResourceId(configuredResourceId);
        return this;
    }

    @Override
    public LogProducerBuilder setLogEveryNth(long logEveryNth)
    {
        this.logProducer.getConfiguration().setLogEveryNth(logEveryNth);
        return this;
    }

    @Override
    public LogProducerBuilder setConfiguration(LogProducerConfiguration logProducerConfiguration)
    {
        ((ConfiguredResource)this.logProducer).setConfiguration(logProducerConfiguration);
        return this;
    }
}

