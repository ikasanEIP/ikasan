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
import org.ikasan.rest.module.dto.TriggerDto;
import org.ikasan.rest.module.util.DateTimeConverter;
import org.ikasan.rest.module.util.UserUtil;
import org.ikasan.security.service.UserService;
import org.ikasan.spec.flow.FlowEvent;
import org.ikasan.spec.module.ModuleService;
import org.ikasan.spec.search.PagedSearchResult;
import org.ikasan.spec.systemevent.SystemEventService;
import org.ikasan.spec.trigger.TriggerJobType;
import org.ikasan.spec.trigger.TriggerService;
import org.ikasan.spec.wiretap.WiretapEvent;
import org.ikasan.spec.wiretap.WiretapService;
import org.ikasan.spec.trigger.Trigger;
import org.ikasan.trigger.model.TriggerImpl;
import org.ikasan.wiretap.listener.JobAwareFlowEventListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Module application implementing the REST contract
 */
@RequestMapping("/rest/wiretap")
@RestController
public class WiretapApplication
{
    @Autowired
    private JobAwareFlowEventListener jobAwareFlowEventListener;

    @Autowired
    /** The wiretap service */
    private WiretapService<FlowEvent, PagedSearchResult<WiretapEvent>, Long> wiretapService;

    @Autowired
    /** The module container (effectively holds the DTO) */
    private ModuleService moduleService;

    @Autowired
    private SystemEventService systemEventService;

    private DateTimeConverter dateTimeConverter = new DateTimeConverter();

//    @Deprecated
//    @RequestMapping(method = RequestMethod.PUT,
//                    value = "/createTrigger/{moduleName}/{flowName}/{flowElementName}/{relationship}/{jobType}")
//    @PreAuthorize("hasAnyAuthority('ALL','WebServiceAdmin')")
//
//    public ResponseEntity createTrigger(@PathVariable("moduleName") String moduleName,
//                                        @PathVariable("flowName") String flowName,
//                                        @PathVariable("flowElementName") String flowElementName,
//                                        @PathVariable("relationship") String relationship,
//                                        @PathVariable("jobType") String jobType, @RequestBody String timeToLive)
//    {
//        HashMap<String, String> params = new HashMap<String, String>();
//
//        if ( timeToLive != null && timeToLive.length() > 0 )
//        {
//            params.put("timeToLive", timeToLive);
//        }
//
//        Trigger trigger = new TriggerImpl(moduleName, flowName, relationship, jobType, flowElementName, params);
//
//        try
//        {
//            this.jobAwareFlowEventListener.addDynamicTrigger(trigger);
//        }
//        catch (Exception e)
//        {
//            return new ResponseEntity("An error has occurred trying to create a new trigger: " + e.getMessage(),
//                HttpStatus.FORBIDDEN
//            );
//        }
//
//        return new ResponseEntity("Trigger successfully created!", HttpStatus.OK);
//    }

    @RequestMapping(method = RequestMethod.PUT,
                    value = "/trigger")
    @PreAuthorize("hasAnyAuthority('ALL','WebServiceAdmin')")
    public ResponseEntity createTrigger(@RequestBody TriggerDto triggerDto)
    {
        String userName = triggerDto.getUserName()!=null?triggerDto.getUserName(): UserUtil.getUser();
        HashMap<String, String> params = new HashMap<String, String>();

        if ( triggerDto.getTimeToLive() != null && !triggerDto.getTimeToLive().isEmpty() )
        {
            params.put("timeToLive", triggerDto.getTimeToLive());
        }

        Trigger trigger = new TriggerImpl(triggerDto.getModuleName(), triggerDto.getFlowName(),
            triggerDto.getRelationship(), triggerDto.getJobType(), triggerDto.getFlowElementName(), params
        );

        try
        {
            this.jobAwareFlowEventListener.addDynamicTrigger(trigger);
            systemEventService.logSystemEvent(
                trigger.getModuleName(),
                "%s-%s:%s".formatted(trigger.getModuleName(), trigger.getFlowName(), trigger.toString()),
                "Create Wiretap",
                userName);
            return new ResponseEntity(HttpStatus.CREATED);

        }
        catch (Exception e)
        {
            return new ResponseEntity(new ErrorDto(
                "An error has occurred trying to create a new trigger: [" + e.getMessage() + "] for request["
                    + triggerDto + "]"), HttpStatus.FORBIDDEN);

        }

    }

//    @Deprecated
//    @RequestMapping(method = RequestMethod.PUT,
//                    value = "/deleteTrigger")
//    @PreAuthorize("hasAnyAuthority('ALL','WebServiceAdmin')")
//    public ResponseEntity deleteTrigger(@RequestBody Long triggerId)
//    {
//        try
//        {
//            this.jobAwareFlowEventListener.deleteDynamicTrigger(triggerId);
//        }
//        catch (Exception e)
//        {
//
//            return new ResponseEntity("An error has occurred trying to delete a trigger: " + e.getMessage(),
//                HttpStatus.FORBIDDEN
//            );
//
//        }
//
//        return new ResponseEntity("Trigger successfully deleted!", HttpStatus.OK);
//
//    }

    @RequestMapping(method = RequestMethod.DELETE,
                    value = "/trigger/{triggerId}/{user}")
    @PreAuthorize("hasAnyAuthority('ALL','WebServiceAdmin')")
    public ResponseEntity delete(@PathVariable("triggerId") Long triggerId,
                                 @PathVariable("user") String user)
    {
        try
        {
            user = user!=null?user: UserUtil.getUser();
            Trigger trigger = jobAwareFlowEventListener.getTrigger(triggerId);
            this.jobAwareFlowEventListener.deleteDynamicTrigger(triggerId);
            if(trigger!=null){
                systemEventService.logSystemEvent(
                    trigger.getModuleName(),
                    "%s-%s:%s".formatted(trigger.getModuleName(), trigger.getFlowName(), trigger.toString()),
                    "Delete Wiretap",
                    user);
            }
            return new ResponseEntity(HttpStatus.OK);

        }
        catch (Exception e)
        {

            return new ResponseEntity(new ErrorDto(
                "An error has occurred trying to delete trigger: [" + e.getMessage() + "] for request[" + triggerId
                    + "]"), HttpStatus.FORBIDDEN);

        }

    }

    @RequestMapping(method = RequestMethod.GET,
                    value = "/triggers")
    @PreAuthorize("hasAnyAuthority('ALL','WebServiceAdmin')")
    public ResponseEntity get()
    {
        List<TriggerDto> dtos = jobAwareFlowEventListener.getTriggers().stream()
                                 .map( trigger -> convertDto(trigger))
                                 .collect(Collectors.toList());
        return new ResponseEntity(dtos, HttpStatus.OK);
    }

    private TriggerDto convertDto(Trigger trigger)
    {
        TriggerDto dto = new TriggerDto();
        dto.setId(trigger.getId());
        dto.setFlowName(trigger.getFlowName());
        dto.setModuleName(trigger.getModuleName());
        dto.setFlowElementName(trigger.getFlowElementName());
        dto.setRelationship(trigger.getRelationship().toString());
        if(trigger.getParams()!=null && !trigger.getParams().isEmpty() && trigger.getParams().containsKey("timeToLive"))
        {
            dto.setJobType(TriggerJobType.WIRETAP.getDescription());
            dto.setTimeToLive(trigger.getParams().get("timeToLive"));
        }else{
            dto.setJobType(TriggerJobType.LOG_WIRETAP.getDescription());
        }
        return dto;
    }

    @RequestMapping(method = RequestMethod.GET,
                    value = "/")
    @PreAuthorize("hasAnyAuthority('ALL','WebServiceAdmin')")
    public ResponseEntity get(@RequestParam(value = "pageNumber",
                                            defaultValue = "0") int pageNumber, @RequestParam(value = "pageSize",
                                                                                              defaultValue = "20") int pageSize,
                              @RequestParam(value = "orderBy",
                                            defaultValue = "identifier") String orderBy,
                              @RequestParam(value = "orderAscending",
                                            defaultValue = "true") boolean orderAscending, @RequestParam(value = "flow",
                                                                                                         required =
                                                                                                             false) String flow,
                              @RequestParam(value = "componentName",
                                            required = false) String componentName, @RequestParam(value = "payloadId",
                                                                                                  required = false) String payloadId,
                              @RequestParam(value = "eventId",
                                            required = false) String eventId, @RequestParam(value = "payloadContent",
                                                                                            required = false) String payloadContent,
                              @RequestParam(value = "fromDateTime",
                                            required = false) String fromDateTime,
                              @RequestParam(value = "untilDateTime",
                                            required = false) String untilDateTime)
    {

        String moduleName = moduleService.getModules().get(0).getName();
        PagedSearchResult<WiretapEvent> pagedResult = null;
        try
        {
            pagedResult = wiretapService.findWiretapEvents(pageNumber, pageSize, orderBy, orderAscending, new HashSet()
                {{add(moduleName);}}, flow, componentName, eventId, payloadId, dateTimeConverter.getDate(fromDateTime),
                dateTimeConverter.getDate(untilDateTime), payloadContent
                                                          );
            return new ResponseEntity(pagedResult, HttpStatus.OK);

        }
        catch (ParseException e)
        {
            return new ResponseEntity(new ErrorDto(
                "fromDateTime or untilDateTime has invalid dateTime format not following yyyy-MM-dd'T'HH:mm:ss."),
                HttpStatus.BAD_REQUEST
            );
        }

    }

}
