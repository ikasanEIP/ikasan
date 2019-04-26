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
package org.ikasan.component.endpoint.util.producer;

import org.ikasan.spec.component.endpoint.EndpointException;
import org.ikasan.spec.component.endpoint.Producer;
import org.ikasan.spec.configuration.ConfiguredResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Implementation of a producer as a logger.
 *
 * @author Ikasan Development Team
 */
public class LogProducer<T> implements Producer<T>, ConfiguredResource<LogProducerConfiguration>
{
    /**
     * logger instance
     */
    private static final Logger logger = LoggerFactory.getLogger(LogProducer.class);

    private String configuredResourceId;

    private LogProducerConfiguration configuration = new LogProducerConfiguration();

    private long loggedCount;
    
    /**
     * regexp pattern to apply
     */
    private Pattern pattern;

    @Override
    public void invoke(T payload) throws EndpointException
    {
        loggedCount++;

        if(configuration.getLogEveryNth() == 0 || loggedCount % configuration.getLogEveryNth() == 0)
        {
            if (logger.isInfoEnabled())
            {
                if (pattern == null || configuration.getReplacementText() == null)
                {
                    logger.info(payload.toString());
                }
                else
                {
                    Matcher matcher = pattern.matcher(configuration.getReplacementText());
                    logger.info(matcher.replaceAll(payload.toString()));
                }
            }
            
            loggedCount = 0;
        }
        
    }

    @Override
    public String getConfiguredResourceId() {
        return configuredResourceId;
    }

    @Override
    public void setConfiguredResourceId(String configuredResourceId) {
        this.configuredResourceId = configuredResourceId;
    }

    @Override
    public LogProducerConfiguration getConfiguration() {
        return configuration;
    }

    @Override
    public void setConfiguration(LogProducerConfiguration configuration)
    {
        this.configuration = configuration;
        if(configuration.getRegExpPattern() == null)
        {
            this.pattern = null;
        }
        else
        {
            this.pattern = Pattern.compile(configuration.getRegExpPattern());
        }
    }
}
