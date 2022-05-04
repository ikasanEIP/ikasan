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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.ikasan.ootb.scheduler.agent.module.model.EnrichedContextualisedScheduledProcessEvent;
import org.ikasan.ootb.scheduler.agent.rest.dto.ContextParameterDto;
import org.ikasan.ootb.scheduler.agent.rest.dto.SchedulerJobInitiationEventDto;
import org.ikasan.spec.component.transformation.Converter;
import org.ikasan.spec.component.transformation.TransformationException;
import org.ikasan.spec.scheduled.context.model.ContextParameter;
import org.ikasan.spec.scheduled.event.model.SchedulerJobInitiationEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Convert a job initiation event to a scheduled process event.
 *
 * @author Ikasan Development Team
 */
public class JobInitiationToContextualisedScheduledProcessEventConverter implements Converter<String, EnrichedContextualisedScheduledProcessEvent>
{
    private String moduleName;
    private ObjectMapper objectMapper;
    private String logParentFolder;
    private String logParentFolderParenthesis;

    /**
     * Constructor
     * @param moduleName
     */
    public JobInitiationToContextualisedScheduledProcessEventConverter(String moduleName, String logParentFolder, String logParentFolderParenthesis)
    {
        this.moduleName = moduleName;
        if(moduleName == null)
        {
            throw new IllegalArgumentException("moduleName cannot be 'null'");
        }
        this.logParentFolder = logParentFolder;
        if(logParentFolder == null)
        {
            throw new IllegalArgumentException("logParentFolder cannot be 'null'");
        }
        this.logParentFolderParenthesis = logParentFolderParenthesis;
        if(logParentFolderParenthesis == null)
        {
            throw new IllegalArgumentException("logParentFolderParenthesis cannot be 'null'");
        }

        this.objectMapper = new ObjectMapper();
        final var simpleModule = new SimpleModule()
            .addAbstractTypeMapping(List.class, ArrayList.class)
            .addAbstractTypeMapping(Map.class, HashMap.class)
            .addAbstractTypeMapping(ContextParameter.class, ContextParameterDto.class);
        objectMapper.registerModule(simpleModule);
    }

    @Override
    public EnrichedContextualisedScheduledProcessEvent convert(String event) throws TransformationException
    {
        try {
            SchedulerJobInitiationEvent schedulerJobInitiationEvent = this.objectMapper.readValue(event, SchedulerJobInitiationEventDto.class);

            EnrichedContextualisedScheduledProcessEvent scheduledProcessEvent = getScheduledProcessEvent();
            scheduledProcessEvent.setFireTime(System.currentTimeMillis());
            scheduledProcessEvent.setAgentName(moduleName);
            scheduledProcessEvent.setJobName(schedulerJobInitiationEvent.getJobName());
            scheduledProcessEvent.setContextId(schedulerJobInitiationEvent.getContextId());
            scheduledProcessEvent.setContextInstanceId(schedulerJobInitiationEvent.getContextInstanceId());
            scheduledProcessEvent.setChildContextIds(schedulerJobInitiationEvent.getChildContextIds());
            scheduledProcessEvent.setJobStarting(true);
            scheduledProcessEvent.setSuccessful(false);
            scheduledProcessEvent.setDryRun(schedulerJobInitiationEvent.isDryRun());
            scheduledProcessEvent.setDryRunParameters(schedulerJobInitiationEvent.getDryRunParameters());
            scheduledProcessEvent.setSkipped(schedulerJobInitiationEvent.isSkipped());
            scheduledProcessEvent.setInternalEventDrivenJob(schedulerJobInitiationEvent.getInternalEventDrivenJob());
            scheduledProcessEvent.setContextParameters(schedulerJobInitiationEvent.getContextParameters());

            // We are going to use a file naming convention for the log files used by the process to write
            // stdout and stderr. The convention is 'contextId'-'contextInstanceId'-'agentName'-'jobName'-currentMillis-suffix.log
            long currentTimeMillis = System.currentTimeMillis();
            scheduledProcessEvent.setResultOutput(fixParenthesis() + schedulerJobInitiationEvent.getContextId() + "-" +
                schedulerJobInitiationEvent.getContextInstanceId() + "-" + schedulerJobInitiationEvent.getAgentName() + "-"
                + schedulerJobInitiationEvent.getJobName() + "-" + currentTimeMillis + "-" + "out.log");

            scheduledProcessEvent.setResultError(fixParenthesis() + schedulerJobInitiationEvent.getContextId() + "-" +
                schedulerJobInitiationEvent.getContextInstanceId() + "-" + schedulerJobInitiationEvent.getAgentName() + "-"
                + schedulerJobInitiationEvent.getJobName() + "-" + currentTimeMillis + "-" + "err.log");

            return scheduledProcessEvent;
        }
        catch (Exception e) {
            throw new TransformationException("An error has occurred converting SchedulerJobInitiationEvent to SchedulerJobInitiationEvent!", e);
        }
    }

    private String fixParenthesis() {
        if (logParentFolder.endsWith(logParentFolderParenthesis)) {
            return logParentFolder;
        }
        return logParentFolder+logParentFolderParenthesis;
    }

    /**
     * Factory method to aid testing.
     *
     * @return
     */
    protected EnrichedContextualisedScheduledProcessEvent getScheduledProcessEvent()
    {
        return new EnrichedContextualisedScheduledProcessEvent();
    }
}
