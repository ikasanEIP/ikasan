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
package org.ikasan.ootb.scheduler.agent.rest;

import org.ikasan.ootb.scheduler.agent.rest.dto.DryRunFileListParameterDto;
import org.ikasan.ootb.scheduler.agent.rest.dto.DryRunModeDto;
import org.ikasan.ootb.scheduler.agent.rest.dto.ErrorDto;
import org.ikasan.ootb.scheduler.agent.rest.dto.JobDryRunModeDto;
import org.ikasan.spec.scheduled.dryrun.DryRunModeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/rest/dryRun")
@RestController
public class DryRunSchedulerApplication {

    @Autowired
    private DryRunModeService dryRunModeService;

    @RequestMapping(path = "/mode", method = RequestMethod.PUT)
    @PreAuthorize("hasAnyAuthority('ALL','WebServiceAdmin')")
    public ResponseEntity setDryRunMode(@RequestBody DryRunModeDto dryRunDto) {
        try {
            dryRunModeService.setDryRunMode(dryRunDto.getDryRunMode());
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity(
                new ErrorDto("An error has occurred attempting to set dry run mode! Error message ["
                    + e.getMessage() + "]"), HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity(HttpStatus.OK);
    }

    @RequestMapping(path = "/jobmode", method = RequestMethod.PUT)
    @PreAuthorize("hasAnyAuthority('ALL','WebServiceAdmin')")
    public ResponseEntity setJobDryRunMode(@RequestBody JobDryRunModeDto jobDryRunDto) {
        try {
            dryRunModeService.setJobDryRun(jobDryRunDto.getJobName(), jobDryRunDto.isDryRun());
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity(
                new ErrorDto("An error has occurred attempting to set the job dry run mode! Error message ["
                    + e.getMessage() + "]"), HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity(HttpStatus.OK);
    }

    @RequestMapping(path = "/fileList", method = RequestMethod.PUT)
    @PreAuthorize("hasAnyAuthority('ALL','WebServiceAdmin')")
    public ResponseEntity exerciseDryRunModeWith(@RequestBody DryRunFileListParameterDto dryRunFileListParameterDto) {
        try {
            dryRunModeService.addDryRunFileList(dryRunFileListParameterDto.getFileList());
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity(
                new ErrorDto("An error has occurred attempting to set dry run file list! Error message ["
                    + e.getMessage() + "]"), HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity(HttpStatus.OK);
    }
}
