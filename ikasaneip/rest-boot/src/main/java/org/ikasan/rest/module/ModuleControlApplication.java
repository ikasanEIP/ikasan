package org.ikasan.rest.module;

import org.apache.log4j.Logger;
import org.ikasan.spec.flow.Flow;
import org.ikasan.spec.module.Module;
import org.ikasan.spec.module.ModuleService;
import org.ikasan.spec.module.StartupType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Module application implementing the REST contract
 */
@RequestMapping("/rest/moduleControl")
@RestController
public class ModuleControlApplication {
    private static Logger logger = Logger.getLogger(ModuleControlApplication.class);

    @Autowired
    private ModuleService moduleService;

    @RequestMapping(method = RequestMethod.PUT,
            value = "/controlFlowState/{moduleName}/{flowName}")
    //  @Consumes("application/octet-stream")
    @PreAuthorize("hasAnyRole('ALL','WebServiceAdmin')")
    public ResponseEntity controlFlowState(
            @PathVariable("moduleName") String moduleName,
            @PathVariable("flowName") String flowName, @RequestBody String action) {

        try {

            String user = getUser();

            if (action.equalsIgnoreCase("start")) {
                this.moduleService.startFlow(moduleName, flowName, user);
            } else if (action.equalsIgnoreCase("startPause")) {
                this.moduleService.startPauseFlow(moduleName, flowName, user);
            } else if (action.equalsIgnoreCase("pause")) {
                this.moduleService.pauseFlow(moduleName, flowName, user);
            } else if (action.equalsIgnoreCase("resume")) {
                this.moduleService.resumeFlow(moduleName, flowName, user);
            } else if (action.equalsIgnoreCase("stop")) {
                this.moduleService.stopFlow(moduleName, flowName, user);
            } else {
                return new ResponseEntity("Unknown flow action [" + action + "].", HttpStatus.FORBIDDEN);
            }

        } catch (Exception e) {
            return new ResponseEntity(e.getMessage(), HttpStatus.FORBIDDEN);
        }

        return new ResponseEntity("Flow state changed successfully!", HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.PUT,
            value = "/controlFlowStartupMode/{moduleName}/{flowName}/{startupType}")
    @PreAuthorize("hasAnyRole('ALL','WebServiceAdmin')")
    public void controlFlowStartupMode(@PathVariable("moduleName") String moduleName,
                                       @PathVariable("flowName") String flowName, @PathVariable("startupType") String startupType,
                                       @RequestBody String startupComment) {

        String user = getUser();

        if ("manual".equalsIgnoreCase(startupType)
                || "automatic".equalsIgnoreCase(startupType)
                || "disabled".equalsIgnoreCase(startupType)) {
            //crude check to ensure comment is supplied when disabling
            if (startupType.equalsIgnoreCase("disabled") && (startupComment == null || "".equals(startupComment.trim()))) {
                throw new IllegalArgumentException("Comment must be provided when disabling Flow startup");
            }

            moduleService.setStartupType(moduleName, flowName, StartupType.valueOf(startupType), startupComment, user);
        }
//        else
        //      {
        //      	throw new WebApplicationException(Response.status(Response.Status.FORBIDDEN).type("text/plain")
        //            .entity("Unknown startup type!.").build());
        //	return new ResponseEntity("Unknown startup type!.",HttpStatus.FORBIDDEN);

        //	}
    }

    @SuppressWarnings("unchecked")
    @RequestMapping(method = RequestMethod.GET,
            value = "/flowState/{moduleName}/{flowName}")
    //@Produces(MediaType.APPLICATION_JSON)
    @PreAuthorize("hasRole('ALL') OR hasRole('WebServiceAdmin')")
    public String getFlowState(@PathVariable("moduleName") String moduleName,
                               @PathVariable("flowName") String flowName) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();


        Module<Flow> module = moduleService.getModule(moduleName);
        Flow flow = module.getFlow(flowName);

        return flow.getState();
    }

    @SuppressWarnings("unchecked")
    @RequestMapping(method = RequestMethod.GET,
            value = "/flowState/{moduleName}")
    @PreAuthorize("hasAnyAuthority('ALL','WebServiceAdmin')")
    public Map<String, String> getFlowStates(@PathVariable("moduleName") String moduleName) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();


        HashMap<String, String> results = new HashMap<String, String>();

        Module<Flow> module = moduleService.getModule(moduleName);

        List<Flow> flows = module.getFlows();

        for (Flow flow : flows) {
            results.put(module.getName() + "-" + flow.getName()
                    , flow.getState());
        }

        return results;
    }

    @SuppressWarnings("unchecked")
    @RequestMapping(method = RequestMethod.GET,
            value = "/contextListenersState/{moduleName}/{flowName}")
    @PreAuthorize("hasAnyAuthority('ALL','WebServiceAdmin')")
    public String getContextListenersState(@PathVariable("moduleName") String moduleName,
                                           @PathVariable("flowName") String flowName) {

        Module<Flow> module = moduleService.getModule(moduleName);
        Flow flow = module.getFlow(flowName);

        return flow.areContextListenersRunning() ? "running" : "stopped";
    }

    @RequestMapping(method = RequestMethod.PUT,
            value = "/controlContextListenersState/{moduleName}/{flowName}")
    @PreAuthorize("hasAnyRole('ALL','WebServiceAdmin')")
    public ResponseEntity controlContextListenersState(@PathVariable("moduleName") String moduleName,
                                                       @PathVariable("flowName") String flowName, @RequestBody String action) {

        try {
            String user = getUser();
            if (action.equalsIgnoreCase("start")) {
                this.moduleService.startContextListeners(moduleName, flowName, user);
            } else if (action.equalsIgnoreCase("stop")) {
                this.moduleService.stopContextListeners(moduleName, flowName, user);
            } else {
                return new ResponseEntity("Unknown context listener action [" + action + "].", HttpStatus.FORBIDDEN);

            }
        } catch (Exception e) {
            return new ResponseEntity(e.getMessage(), HttpStatus.FORBIDDEN);

        }

        return new ResponseEntity("Context Listeners state changed successfully!", HttpStatus.OK);
    }


    private String getUser() {
        String user = "unknown";

        SecurityContext context = SecurityContextHolder.getContext();
        if (context != null) {
            user = context.getAuthentication().getPrincipal().toString();
        }
        return user;
    }
}
