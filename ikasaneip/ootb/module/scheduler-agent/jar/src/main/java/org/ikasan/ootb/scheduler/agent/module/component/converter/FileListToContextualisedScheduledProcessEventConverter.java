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
package org.ikasan.ootb.scheduler.agent.module.component.converter;

import org.ikasan.ootb.scheduled.model.ContextualisedScheduledProcessEventImpl;
import org.ikasan.ootb.scheduler.agent.module.component.converter.configuration.ContextualisedConverterConfiguration;
import org.ikasan.spec.component.transformation.Converter;
import org.ikasan.spec.component.transformation.TransformationException;
import org.ikasan.spec.configuration.ConfiguredResource;
import org.ikasan.spec.scheduled.event.model.ContextualisedScheduledProcessEvent;

import java.io.File;
import java.util.List;

/**
 * File list to contextualised scheduled process event converter.
 *
 * @author Ikasan Development Team
 */
public class FileListToContextualisedScheduledProcessEventConverter implements Converter<List<File>, ContextualisedScheduledProcessEvent>
    , ConfiguredResource<ContextualisedConverterConfiguration>
{
    private String agentName;
    private String jobName;
    private String configurationId;
    private ContextualisedConverterConfiguration configuration;


    /**
     * Constructor
     * @param agentName
     */
    public FileListToContextualisedScheduledProcessEventConverter(String agentName, String jobName)
    {
        this.agentName = agentName;
        if(agentName == null)
        {
            throw new IllegalArgumentException("moduleName cannot be 'null'");
        }
        this.jobName = jobName;
        if(jobName == null)
        {
            throw new IllegalArgumentException("jobName cannot be 'null'");
        }
    }

    @Override
    public ContextualisedScheduledProcessEvent convert(List<File> event) throws TransformationException
    {
        ContextualisedScheduledProcessEvent scheduledProcessEvent = new ContextualisedScheduledProcessEventImpl();
        scheduledProcessEvent.setFireTime(System.currentTimeMillis());
        scheduledProcessEvent.setAgentName(this.agentName);
        scheduledProcessEvent.setJobName(this.jobName);
        scheduledProcessEvent.setSuccessful(true);
        scheduledProcessEvent.setContextName(this.configuration.getContextId());
        scheduledProcessEvent.setChildContextNames(this.configuration.getChildContextIds());

        return scheduledProcessEvent;
    }

    @Override
    public String getConfiguredResourceId() {
        return this.configurationId;
    }

    @Override
    public void setConfiguredResourceId(String id) {
        this.configurationId = id;
    }

    @Override
    public ContextualisedConverterConfiguration getConfiguration() {
        return this.configuration;
    }

    @Override
    public void setConfiguration(ContextualisedConverterConfiguration configuration) {
        this.configuration = configuration;
    }
}
