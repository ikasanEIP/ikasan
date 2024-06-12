package org.ikasan.ootb.scheduler.agent.rest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RequestMapping("/rest/jobUtils")
@RestController
public class JobUtilsApplication
{
    /** logger */
    private static Logger logger = LoggerFactory.getLogger(JobUtilsApplication.class);

//    private SchedulerPersistenceService schedulerPersistenceService;


    /**
     * Allows to kill a job by a given pid.
     */
    @RequestMapping(method = RequestMethod.GET,
                    value = "/kill/{pid}",
                    produces = { MediaType.APPLICATION_JSON_VALUE })
    @PreAuthorize("hasAnyAuthority('ALL','WebServiceAdmin')")
    public ResponseEntity killPid(@PathVariable("pid") long pid, @RequestParam(name = "destroy", defaultValue = "false") boolean destroy)
    {
        try
        {
            Optional<ProcessHandle> processHandleOptional = ProcessHandle.of(pid);

            if (processHandleOptional.isEmpty()) {
                return new ResponseEntity("pid not found!", HttpStatus.BAD_REQUEST);
            }
            else {
                boolean result = destroy ? processHandleOptional.get().destroyForcibly() : processHandleOptional.get().destroy();
                if (result) {
                    return new ResponseEntity(HttpStatus.OK);
                }
                else {
                    return new ResponseEntity("could not kill the pid!", HttpStatus.BAD_REQUEST);
                }
            }

        }
        catch (Exception e)
        {
            return new ResponseEntity(e, HttpStatus.BAD_REQUEST);
        }
    }

}
