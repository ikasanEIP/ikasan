package org.ikasan.ootb.scheduler.agent.rest;

import org.ikasan.ootb.scheduled.processtracker.service.SchedulerPersistenceService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RequestMapping("/rest/jobUtils")
@RestController
public class JobUtilsApplication
{
    /** logger */
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
            Optional<ProcessHandle> processHandleOptional = ProcessHandle.of(pid);

            if (processHandleOptional.isEmpty()) {
                return new ResponseEntity<>("pid not found!", HttpStatus.BAD_REQUEST);
            }
            else {
                ProcessHandle processHandle = processHandleOptional.get();
                boolean result = destroyProcessTree(processHandle, destroy);
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

    /**
     * Recursively retrieves all descendant processes of the given process handle.
     *
     * @param processHandle the process handle to query for children
     * @return a list of all direct and indirect child processes
     */
    private List<ProcessHandle> getDescendantProcesses(ProcessHandle processHandle)
    {
        List<ProcessHandle> descendantProcesses = new ArrayList<>();

        processHandle.children().forEach(childProcess -> {
            descendantProcesses.addAll(getDescendantProcesses(childProcess));
            descendantProcesses.add(childProcess);
        });

        return descendantProcesses;
    }

    /**
     * Destroys a process tree by discovering descendant processes, which are 
     * sorted in reverse order (newest PIDs first) to ensure newer processes
     * terminated before older ones, finally the parent process is terminated, 
     * reducing the risk of orphaned processes.
     *
     * @param processHandle the parent process handle
     * @param destroy if true, uses forceful termination; if false, sends graceful signal
     * @return true if all processes in the tree were successfully destroyed, false otherwise
     */
    private boolean destroyProcessTree(ProcessHandle processHandle, boolean destroy)
    {
        // Discover descendants immediately before termination to minimize race condition window
        List<ProcessHandle> descendantProcesses = getDescendantProcesses(processHandle);
        
        // Log the operation with discovered descendant PIDs
        logger.warn("About to kill pid {} with process handle {} and descendant pids {}",
            processHandle.pid(), processHandle, descendantProcesses.stream().map(ProcessHandle::pid).collect(Collectors.toList()));
        
        // Sort descendants in reverse order (newest PIDs first)
        List<ProcessHandle> sortedDescendants = new ArrayList<>(descendantProcesses);
        sortedDescendants.sort((p1, p2) -> Long.compare(p2.pid(), p1.pid()));

        boolean descendantsDestroyed = sortedDescendants.stream()
            .map(childProcess -> destroyProcess(childProcess, destroy))
            .reduce(true, Boolean::logicalAnd);

        return descendantsDestroyed && destroyProcess(processHandle, destroy);
    }

    /**
     * Destroys a single process by sending a termination signal or forcibly killing it.
     *
     * @param processHandle the process to terminate
     * @param destroy if true, uses destroyForcibly(); if false, uses destroy() for graceful shutdown
     * @return true if the process was successfully terminated or is already not alive, false otherwise
     */
    private boolean destroyProcess(ProcessHandle processHandle, boolean destroy)
    {
        if (!processHandle.isAlive()) {
            logger.info("Pid {} is already terminated", processHandle.pid());
            return true;
        }

        boolean result = destroy ? processHandle.destroyForcibly() : processHandle.destroy();
        logger.info("Termination request for pid {} returned {} using destroyForcibly={}",
            processHandle.pid(), result, destroy);

        return result || !processHandle.isAlive();
    }
}
