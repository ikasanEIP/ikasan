package org.ikasan.ootb.scheduler.agent.rest;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.BasicPolymorphicTypeValidator;
import com.fasterxml.jackson.databind.jsontype.PolymorphicTypeValidator;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.ikasan.job.orchestration.model.job.SchedulerJobWrapperImpl;
import org.ikasan.ootb.scheduler.agent.rest.dto.ErrorDto;
import org.ikasan.spec.scheduled.job.model.SchedulerJobWrapper;
import org.ikasan.spec.scheduled.provision.JobProvisionService;
import org.ikasan.spec.scheduled.provision.JobProvisionServiceLockedException;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequestMapping("/rest/jobProvision")
@RestController
public class JobProvisionApplication {

    Logger logger = LoggerFactory.getLogger(JobProvisionApplication.class);

    private ObjectMapper mapper;

    @Autowired
    private JobProvisionService jobProvisionService;

    /**
     * Constructor
     */
    public JobProvisionApplication() {
        this.mapper = new ObjectMapper();
        PolymorphicTypeValidator ptv = BasicPolymorphicTypeValidator.builder()
            .allowIfSubType("org.ikasan.spec.scheduled.job.model")
            .allowIfSubType("org.ikasan.job.orchestration.model.job")
            .allowIfSubType("org.ikasan.job.orchestration.model.context")
            .allowIfSubType("org.ikasan.job.orchestration.model.profile")
            .allowIfSubType("org.ikasan.job.orchestration.model.notification")
            .allowIfSubType("org.ikasan.spec.scheduled.notification.model")
            .allowIfSubType("java.util.concurrent.CopyOnWriteArrayList")
            .allowIfSubType("java.util.concurrent.ConcurrentHashMap")
            .allowIfSubType("java.util.ArrayList")
            .allowIfSubType("java.util.HashMap")
            .allowIfSubType("java.util.HashSet")
            .allowIfSubType("java.util.concurrent.CopyOnWriteArraySet")
            .build();
        final var simpleModule = new SimpleModule()
            .addAbstractTypeMapping(List.class, ArrayList.class)
            .addAbstractTypeMapping(Map.class, HashMap.class);

        this.mapper.registerModule(simpleModule);
        this.mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        this.mapper.activateDefaultTyping(ptv, ObjectMapper.DefaultTyping.NON_FINAL);
        this.mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    @RequestMapping(method = RequestMethod.PUT)
    @PreAuthorize("hasAnyAuthority('ALL','WebServiceAdmin')")
    public ResponseEntity provisionJobs(@RequestBody String schedulerJobs) {
        try
        {
            SchedulerJobWrapper schedulerJobWrapper = this.mapper.readValue(schedulerJobs
                , SchedulerJobWrapperImpl.class);

            this.jobProvisionService.provisionJobs(schedulerJobWrapper.getJobs(), "system");
        }
        catch (JobProvisionServiceLockedException e)
        {
            logger.error("An error has occurred attempting to provision scheduler jobs! Could not acquire lock!", e);
            return new ResponseEntity(
                new ErrorDto("LOCK_ACQUISITION_ERROR", "An error has occurred attempting to " +
                    "provision scheduler jobs! Error message [" + e.getMessage() + "]"), HttpStatus.BAD_REQUEST);
        }
        catch (Exception e)
        {
            logger.error("An error has occurred attempting to provision scheduler jobs!", e);
            return new ResponseEntity(
                new ErrorDto("An error has occurred attempting to provision scheduler jobs! Error message ["
                    + e.getMessage() + "]"), HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity(HttpStatus.OK);
    }

    @RequestMapping(path = "/remove", method = RequestMethod.DELETE)
    @PreAuthorize("hasAnyAuthority('ALL','WebServiceAdmin')")
    public ResponseEntity removeJobsForContext(@RequestBody String contextName) {
        try {
            this.jobProvisionService.removeJobs(contextName);
        }
        catch (JobProvisionServiceLockedException e)
        {
            logger.error("An error has occurred attempting to remove scheduler jobs for context[%s]!".formatted(contextName), e);
            return new ResponseEntity(
                new ErrorDto("LOCK_ACQUISITION_ERROR", "An error has occurred attempting to remove " +
                    "scheduler jobs for context[%s]! Error message [%s]".formatted(contextName, e.getMessage())), HttpStatus.BAD_REQUEST);
        }
        catch (Exception e) {
            e.printStackTrace();
            logger.error("An error has occurred attempting to remove scheduler jobs for context[%s]!".formatted(contextName), e);
            return new ResponseEntity(
                new ErrorDto(("An error has occurred attempting to remove scheduler jobs for context[%s]! " +
                    "Error message [%s]").formatted(contextName, e.getMessage())), HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity(HttpStatus.OK);
    }
}
