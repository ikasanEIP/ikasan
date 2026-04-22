package org.ikasan.ootb.scheduler.agent.rest;

import org.ikasan.ootb.scheduled.processtracker.ProcessKillUtils;
import org.ikasan.ootb.scheduled.processtracker.service.SchedulerPersistenceService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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
    private static final Logger logger = LoggerFactory.getLogger(JobUtilsApplication.class);

    @Autowired
    private SchedulerPersistenceService schedulerPersistenceService;

    /**
     * Terminates a process and all its descendant processes by the given process ID.
     * <p>
     * Uses ProcessHandle.destroy() or destroyForcibly() based on the destroy flag.
     * The discovery of descendant processes and termination are performed atomically within
     * destroyProcessTree() to minimize the race condition window where new child processes
     * could be created and escape the kill.
     * </p>
     * After successful termination, removes all associated records from persistence service.
     *
     * @param pid the process ID to terminate
     * @param destroy if true, forcibly destroys the process on Unix systems; if false, sends graceful termination signal
     * @return ResponseEntity with HTTP 200 OK if successful, HTTP 400 BAD_REQUEST if PID not found or termination fails
     */
    @RequestMapping(method = RequestMethod.GET,
                    value = "/kill/{pid}",
                    produces = { MediaType.APPLICATION_JSON_VALUE })
    @PreAuthorize("hasAnyAuthority('ALL','WebServiceAdmin')")
    public ResponseEntity<?> killPid(@PathVariable("pid") long pid, @RequestParam(name = "destroy", defaultValue = "false") boolean destroy)
    {
        try
        {
            if (this.schedulerPersistenceService.findByPid(pid) == null) {
                logger.warn("Kill request rejected: pid {} is not managed by this agent", pid);
                return new ResponseEntity<>("The requested PID is not managed by this agent", HttpStatus.FORBIDDEN);
            }

            Optional<ProcessHandle> processHandleOptional = ProcessHandle.of(pid);

            if (processHandleOptional.isEmpty()) {
                return new ResponseEntity<>("pid not found!", HttpStatus.BAD_REQUEST);
            }
            else {
                ProcessHandle processHandle = processHandleOptional.get();
                boolean result = ProcessKillUtils.destroyProcessTree(processHandle, destroy);
                if (result) {
                    this.schedulerPersistenceService.removeAll(pid);
                    return new ResponseEntity<>(HttpStatus.OK);
                }
                else {
                    return new ResponseEntity<>("could not kill the pid!", HttpStatus.BAD_REQUEST);
                }
            }
        }
        catch (Exception e)
        {
            return new ResponseEntity<>(e, HttpStatus.BAD_REQUEST);
        }
    }

}
