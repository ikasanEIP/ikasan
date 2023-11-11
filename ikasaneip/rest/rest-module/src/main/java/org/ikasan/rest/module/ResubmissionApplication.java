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
package org.ikasan.rest.module;

import org.ikasan.rest.module.dto.ErrorDto;
import org.ikasan.rest.module.dto.ResubmissionRequestDto;
import org.ikasan.rest.module.util.UserUtil;
import org.ikasan.spec.hospital.service.HospitalService;
import org.ikasan.spec.systemevent.SystemEventService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

/**
 * @author Ikasan Development Team
 */
@RequestMapping("/rest/resubmission")
@RestController
public class ResubmissionApplication
{
    private static Logger logger = LoggerFactory.getLogger(ResubmissionApplication.class);

    @Autowired
    private HospitalService hospitalService;

    @Autowired
    private SystemEventService systemEventService;

    /**
     * REST endpoint to resubmit excluded event.
     *
     * @param moduleName The name of the module we are re-submitting to.
     * @param flowName   The name of the flow we are re-submitting to.
     * @param errorUri   The error uri of the event being resubmitted.
     * @param event      The event we are resubmitting.
     * @return ResponseEntity with HTTP status 200 if successful or 404 if request failed
     */
    @Deprecated
    @PutMapping("/resubmit/{moduleName}/{flowName}/{errorUri}")
    @PreAuthorize("hasAnyAuthority('ALL','WebServiceAdmin')")
    public ResponseEntity resubmit(@PathVariable String moduleName,
                                   @PathVariable String flowName, @PathVariable String errorUri,
                                   @RequestBody byte[] event)
    {
        try
        {
            logger.debug("Re-submitting event " + errorUri);
            this.hospitalService.resubmit(moduleName, flowName, errorUri, event, getPrincipal());
        }
        catch (Exception e)
        {
            logger.error("An error has occurred on the server when trying to resubmit the event. ", e);
            return new ResponseEntity(
                "An error has occurred on the server when trying to resubmit the event. " + e.getMessage(),
                HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity("Event resubmitted!", HttpStatus.OK);
    }

    /**
     * REST endpoint to ignore excluded event.
     *
     * @param moduleName The name of the module we are ignoring to.
     * @param flowName   The name of the flow we are ignoring to.
     * @param errorUri   The error uri of the event being ignored.
     * @param event      The event we are resubmitting.
     * @return ResponseEntity with HTTP status 200 if successful or 404 if request failed
     */
    @Deprecated
    @PutMapping("/ignore/{moduleName}/{flowName}/{errorUri}")
    @PreAuthorize("hasAnyAuthority('ALL','WebServiceAdmin')")
    public ResponseEntity ignore(@PathVariable String moduleName,
                                 @PathVariable String flowName, @PathVariable String errorUri,
                                 @RequestBody byte[] event)
    {
        try
        {
            logger.debug("Ignoring event " + errorUri);
            this.hospitalService.ignore(moduleName, flowName, errorUri, event, getPrincipal());
        }
        catch (Exception e)
        {
            logger.error("An error has occurred on the server when trying to ignore the event. ", e);
            return new ResponseEntity(
                "An error has occurred on the server when trying to ignore the event. " + e.getMessage(),
                HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity("Event resubmitted!", HttpStatus.OK);
    }

    /**
     * REST endpoint to re-submit or ignore excluded event.
     *
     * @param requestDto The request dto describing resubmission being performed.
     *
     * @return ResponseEntity with HTTP status 200 if successful or 404 if request failed
     */
    @PutMapping
    @PreAuthorize("hasAnyAuthority('ALL','WebServiceAdmin')")
    public ResponseEntity resubmit(@RequestBody ResubmissionRequestDto requestDto)
    {
        try
        {
            logger.debug("Request " + requestDto);
            String userName = requestDto.getUserName()!=null?requestDto.getUserName():UserUtil.getUser();
            switch (requestDto.getAction())
            {
            case "ignore":
                this.hospitalService.ignore(requestDto.getModuleName(), requestDto.getFlowName(),
                    requestDto.getErrorUri(),
                    UserUtil.getUser());
                systemEventService.logSystemEvent(
                    requestDto.getModuleName(),
                    "%s-%s:%s".formatted(requestDto.getModuleName(), requestDto.getFlowName(), requestDto.getErrorUri()),
                    "Ignoring Exclusion",
                    userName);
                break;
            case "resubmit":
                this.hospitalService.resubmit(requestDto.getModuleName(), requestDto.getFlowName(),
                    requestDto.getErrorUri(),
                    UserUtil.getUser());
                systemEventService.logSystemEvent(
                    requestDto.getModuleName(),
                    "%s-%s:%s".formatted(requestDto.getModuleName(), requestDto.getFlowName(), requestDto.getErrorUri()),
                    "Resubmitting Exclusion",
                    userName);
                break;
            default:
                return new ResponseEntity(
                    new ErrorDto("Invalid action["+requestDto.getAction()+"] allowed values [resubmit|ignore] "),
                    HttpStatus.BAD_REQUEST);
            }
            return new ResponseEntity(HttpStatus.OK);
        }
        catch (Exception e)
        {
            logger.error("An error has occurred when trying to resubmit or ignore the event. ", e);

            return new ResponseEntity(
                new ErrorDto("An error has occurred when trying to resubmit or ignore the event. " + e.getMessage()),
                HttpStatus.NOT_FOUND);
        }
    }


    private Principal getPrincipal()
    {
        // String user = "unknown";
        org.springframework.security.core.context.SecurityContext context = SecurityContextHolder.getContext();
        if (context != null)
        {
            return (Principal) context.getAuthentication().getPrincipal();
        }
        return null;
    }

}
