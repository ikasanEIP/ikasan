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

import org.ikasan.ootb.scheduled.model.ScheduledProcessEventImpl;
import org.ikasan.spec.component.transformation.Converter;
import org.ikasan.spec.component.transformation.TransformationException;
import org.ikasan.spec.scheduled.event.model.ScheduledProcessEvent;
import org.quartz.*;

/**
 * Quartz Job Execution Context converter to Scheduled Process Event.
 *
 * @author Ikasan Development Team
 */
public class JobExecutionConverter implements Converter<JobExecutionContext, ScheduledProcessEvent>
{
    String moduleName;
    String jobName;
    boolean markAsSuccessful;

    /**
     * Constructor
     * @param moduleName
     */
    public JobExecutionConverter(String moduleName, String jobName, boolean markAsSuccessful)
    {
        this.moduleName = moduleName;
        if(moduleName == null)
        {
            throw new IllegalArgumentException("moduleName cannot be 'null'");
        }
        this.jobName = jobName;
        if(jobName == null)
        {
            throw new IllegalArgumentException("jobName cannot be 'null'");
        }

        this.markAsSuccessful = markAsSuccessful;
    }

    @Override
    public ScheduledProcessEvent convert(JobExecutionContext jobExecutionContext) throws TransformationException
    {
        ScheduledProcessEvent scheduledProcessEvent = getScheduledProcessEvent();
        scheduledProcessEvent.setFireTime( jobExecutionContext.getFireTime().getTime() );
        scheduledProcessEvent.setAgentName(moduleName);
        scheduledProcessEvent.setContextId("test");
        scheduledProcessEvent.setJobName(this.jobName);

        if(this.markAsSuccessful) {
            scheduledProcessEvent.setSuccessful(true);
        }

        Trigger jobTrigger = jobExecutionContext.getTrigger();
        if(jobTrigger != null)
        {
            scheduledProcessEvent.setJobDescription(jobTrigger.getDescription());

            TriggerKey triggerKey = jobTrigger.getKey();
            if(triggerKey != null)
            {
                scheduledProcessEvent.setJobName(triggerKey.getName());
                scheduledProcessEvent.setJobGroup(triggerKey.getGroup());
            }
        }

        if(jobExecutionContext.getNextFireTime() != null)
        {
            scheduledProcessEvent.setNextFireTime(jobExecutionContext.getNextFireTime().getTime());
        }

        return scheduledProcessEvent;
    }

    /**
     * Factory method to aid testing.
     *
     * @return
     */
    protected ScheduledProcessEvent getScheduledProcessEvent()
    {
        return new ScheduledProcessEventImpl();
    }
}
