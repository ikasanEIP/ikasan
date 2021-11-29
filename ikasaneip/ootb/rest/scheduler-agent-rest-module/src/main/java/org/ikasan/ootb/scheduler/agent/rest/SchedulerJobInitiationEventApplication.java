package org.ikasan.ootb.scheduler.agent.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.leansoft.bigqueue.IBigQueue;
import org.ikasan.ootb.scheduler.agent.rest.dto.ErrorDto;
import org.ikasan.ootb.scheduler.agent.rest.dto.SchedulerJobInitiationEventDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * Module application implementing the REST contract
 */
@RequestMapping("/rest/schedulerJobInitiation")
@RestController
public class SchedulerJobInitiationEventApplication
{
    private static Logger logger = LoggerFactory.getLogger(SchedulerJobInitiationEventApplication.class);

    private ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private IBigQueue inboundQueue;


    @RequestMapping(method = RequestMethod.PUT)
    @PreAuthorize("hasAnyAuthority('ALL','WebServiceAdmin')")
    public ResponseEntity raiseSchedulerJobInitiationEvent(@RequestBody SchedulerJobInitiationEventDto schedulerJobInitiationEvent)
    {
        try {
            this.inboundQueue.enqueue(objectMapper.writeValueAsBytes(schedulerJobInitiationEvent));
        }
        catch (Exception e) {
            return new ResponseEntity(new ErrorDto(e.getMessage()), HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity(HttpStatus.OK);
    }

}
