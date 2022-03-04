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

package org.ikasan.ootb.scheduler.agent.module.pointcut;

import java.util.List;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.ikasan.spec.scheduled.dryrun.DryRunModeService;
import org.quartz.JobExecutionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

@Aspect
public class FileMessageProviderAspect {

    private static Logger LOGGER = LoggerFactory.getLogger(FileMessageProviderAspect.class);

    @Autowired
    private DryRunModeService dryRunModeService;

    @Around("execution(* org.ikasan.component.endpoint.filesystem.messageprovider.FileMessageProvider.invoke(..))")
    public Object fileMessageProviderInvoke(ProceedingJoinPoint joinPoint) throws Throwable {
        if (dryRunModeService.getDryRunMode()) {
            return intercept(joinPoint);
        } else {
            return joinPoint.proceed();
        }
    }

    private Object intercept(ProceedingJoinPoint joinPoint) {
        JobExecutionContext jobExecutionContext = (JobExecutionContext) joinPoint.getArgs()[0];
        String jobName = jobExecutionContext.getTrigger().getKey().getName();

        String dryRunFileName = dryRunModeService.getJobFileName(jobName);
        String message = "In dry run mode intercepting FileMessageProvider.invoke() for file name: " + dryRunFileName;
        LOGGER.info(message);
        return dryRunFileName == null ? null : List.of(dryRunFileName);
    }
}
