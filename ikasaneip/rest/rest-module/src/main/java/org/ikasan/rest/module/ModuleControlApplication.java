package org.ikasan.rest.module;
import org.apache.commons.text.StringEscapeUtils;

import org.ikasan.rest.module.dto.*;
import org.ikasan.rest.module.util.UserUtil;
import org.ikasan.spec.dashboard.DashboardRestService;
import org.ikasan.spec.module.*;
import org.ikasan.spec.module.Module;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.ikasan.spec.flow.Flow;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

/**
 * Module application implementing the REST contract
 */
@RequestMapping("/rest/moduleControl")
@RestController
public class ModuleControlApplication
{
    private static Logger logger = LoggerFactory.getLogger(ModuleControlApplication.class);

    @Autowired
    private ModuleService moduleService;

    @Autowired
    private ModuleActivator moduleActivator;


    /**
     * Dashboard client used for publishing module metadata to dashboard
     */
    @Autowired
    private DashboardRestService moduleMetadataDashboardRestService;

    /**
     * Changes the state of a flow based on the specified action in the ChangeFlowStateDto object.
     *
     * @param changeFlowStateDto The data transfer object containing the module name, flow name, action,
     *                           and optional username.
     * @return A ResponseEntity object indicating the success or failure of the flow state change.
     */
    @RequestMapping(method = RequestMethod.PUT)
    @PreAuthorize("hasAnyAuthority('ALL','WebServiceAdmin')")
    public ResponseEntity changeFlowState(@RequestBody ChangeFlowStateDto changeFlowStateDto)
    {
        try
        {
            String user = changeFlowStateDto.getUsername()!=null?changeFlowStateDto.getUsername(): UserUtil.getUser();

            switch (changeFlowStateDto.getAction()){
            case "start":
                this.moduleService.startFlow(changeFlowStateDto.getModuleName(), changeFlowStateDto.getFlowName(), user);break;
            case "startPause":
                this.moduleService.startPauseFlow(changeFlowStateDto.getModuleName(), changeFlowStateDto.getFlowName(), user);break;
            case "pause":
                this.moduleService.pauseFlow(changeFlowStateDto.getModuleName(), changeFlowStateDto.getFlowName(), user);break;
            case "resume":
                this.moduleService.resumeFlow(changeFlowStateDto.getModuleName(), changeFlowStateDto.getFlowName(), user);break;
            case "stop":
                this.moduleService.stopFlow(changeFlowStateDto.getModuleName(), changeFlowStateDto.getFlowName(), user);break;
            default:
                return new ResponseEntity(new ErrorDto("Unknown flow action [" + changeFlowStateDto.getAction() + "]."), HttpStatus.FORBIDDEN);
            }


        }
        catch (Exception e)
        {
            return new ResponseEntity(new ErrorDto(e.getMessage()), HttpStatus.FORBIDDEN);
        }
        return new ResponseEntity(HttpStatus.OK);
    }

    /**
     * Changes the startup mode of a flow based on the provided DTO.
     *
     * @param changeFlowStartupModeDto The data transfer object containing information for the startup mode change.
     *                               Requires moduleName, flowName, startupType, comment, and optionally username.
     * @return ResponseEntity indicating the success or failure of the startup mode change.
     */
    @RequestMapping(method = RequestMethod.PUT,
        value = "/startupMode")
    @PreAuthorize("hasAnyAuthority('ALL','WebServiceAdmin')")
    public ResponseEntity changeFlowStartupMode(
        @RequestBody ChangeFlowStartupModeDto changeFlowStartupModeDto)
    {
        String user = changeFlowStartupModeDto.getUsername()!=null?changeFlowStartupModeDto.getUsername(): UserUtil.getUser();

        String startupType = changeFlowStartupModeDto.getStartupType();
        String moduleName = changeFlowStartupModeDto.getModuleName();
        String flowName = changeFlowStartupModeDto.getFlowName();
        String startupComment = changeFlowStartupModeDto.getComment();

        if ("manual".equalsIgnoreCase(startupType)
            || "automatic".equalsIgnoreCase(startupType)
            || "disabled".equalsIgnoreCase(startupType))
        {
            //crude check to ensure comment is supplied when disabling
            if (startupType.equalsIgnoreCase("disabled") && (startupComment == null || ""
                .equals(startupComment.trim())))
            {
                return new ResponseEntity(new ErrorDto("Comment must be provided when disabling Flow startup"), HttpStatus.BAD_REQUEST);
            }
            moduleService.setStartupType(moduleName, flowName, StartupType.valueOf(startupType.toUpperCase()), startupComment, user);
            moduleMetadataDashboardRestService.publish(this.moduleService.getModule(moduleName));
            return new ResponseEntity(HttpStatus.OK);
        }
        else{
            return new ResponseEntity(new ErrorDto("Invalid startupType["
                + StringEscapeUtils.escapeHtml4(startupType) +"]."), HttpStatus.BAD_REQUEST);

        }
    }

    /**
     * Changes the startup mode of all flows within a specified module.
     *
     * @param changeFlowStartupModeDto The data transfer object containing information for the startup mode change.
     *                                 Requires moduleName, startupType, and comment.
     * @return ResponseEntity indicating the success or failure of the startup mode change.
     */
    @RequestMapping(method = RequestMethod.PUT,
        value = "/startupMode/allFlows")
    @PreAuthorize("hasAnyAuthority('ALL','WebServiceAdmin')")
    public ResponseEntity changeAllFlowStartupMode(
        @RequestBody ChangeFlowStartupModeDto changeFlowStartupModeDto)
    {
        String user = changeFlowStartupModeDto.getUsername()!=null?changeFlowStartupModeDto.getUsername(): UserUtil.getUser();

        String startupType = changeFlowStartupModeDto.getStartupType();
        String moduleName = changeFlowStartupModeDto.getModuleName();
        String startupComment = changeFlowStartupModeDto.getComment();

        if ("manual".equalsIgnoreCase(startupType)
            || "automatic".equalsIgnoreCase(startupType)
            || "disabled".equalsIgnoreCase(startupType))
        {
            //crude check to ensure comment is supplied when disabling
            if (startupType.equalsIgnoreCase("disabled") && (startupComment == null || ""
                .equals(startupComment.trim())))
            {
                return new ResponseEntity(new ErrorDto("Comment must be provided when disabling Flow startup"), HttpStatus.BAD_REQUEST);
            }
            this.moduleService.getModule(moduleName).getFlows().forEach(flow -> {
                moduleService.setStartupType(moduleName, ((Flow)flow).getName()
                    , StartupType.valueOf(startupType.toUpperCase()), startupComment, user);
            });

            moduleMetadataDashboardRestService.publish(this.moduleService.getModule(moduleName));
            return new ResponseEntity(HttpStatus.OK);
        }
        else{
            return new ResponseEntity(new ErrorDto("Invalid startupType["
                + StringEscapeUtils.escapeHtml4(startupType) +"]."), HttpStatus.BAD_REQUEST);

        }
    }

    /**
     * Retrieves the startup mode of a specified module and flow.
     *
     * @param moduleName The name of the module to retrieve the startup mode for.
     * @param flowName The name of the flow within the module to retrieve the startup mode for.
     * @return ResponseEntity containing a FlowStartupTypeDto object representing the startup mode
     * of the specified module and flow.
     */
    @RequestMapping(method = RequestMethod.GET,
        value = "/startupMode/{moduleName}/{flowName}")
    @PreAuthorize("hasAnyAuthority('ALL','WebServiceAdmin')")
    public ResponseEntity getStartupMode(@PathVariable("moduleName") String moduleName,
                                  @PathVariable("flowName") String flowName)
    {
        StartupControl startupControl = moduleService.getStartupControl(moduleName, flowName);

        if(startupControl == null) {
            return new ResponseEntity(new FlowStartupTypeDto(moduleName, flowName, StartupType.MANUAL.name(), ""), HttpStatus.OK);
        }

        return new ResponseEntity(new FlowStartupTypeDto(moduleName, flowName, startupControl.getStartupType().name(), startupControl.getComment()), HttpStatus.OK);
    }

    /**
     * Retrieves a specific flow within a module based on the provided module name and flow name.
     *
     * The module is explicitly null-checked before accessing its flows to return a clean 404
     * rather than leaking a NullPointerException stack trace to the caller (IKASAN-2738).
     *
     * @param moduleName The name of the module containing the flow.
     * @param flowName The name of the specific flow to retrieve.
     * @return ResponseEntity representing the retrieved FlowDto containing flow information.
     */
    @RequestMapping(method = RequestMethod.GET,
        value = "/{moduleName}/{flowName}",
        produces = "application/json")
    @PreAuthorize("hasAnyAuthority('ALL','WebServiceAdmin')")
    public ResponseEntity getFlow(@PathVariable("moduleName") String moduleName,
        @PathVariable("flowName") String flowName)
    {
        Module<Flow> module = moduleService.getModule(moduleName);
        if (module == null)
        {
            return new ResponseEntity(new ErrorDto("Module [" + moduleName + "] not found."),
                HttpStatus.NOT_FOUND);
        }

        Flow flow = module.getFlow(flowName);
        if (flow != null)
            return new ResponseEntity(new FlowDto(flow.getName(), flow.getState()), HttpStatus.OK);
        else
        {
            return new ResponseEntity(new ErrorDto("Module [" + StringEscapeUtils.escapeHtml4(moduleName)
                + "] Flow [" + StringEscapeUtils.escapeHtml4(flowName) + "] not found."),
                HttpStatus.NOT_FOUND);
        }
    }

    /**
     * Retrieves information about a specific module based on the module name.
     *
     * @param moduleName The name of the module to retrieve information for.
     * @return ResponseEntity containing a ModuleDto object representing the module information if found.
     *         If the module with the provided name or its flows are not found, an ErrorDto object is returned.
     */
    @RequestMapping(method = RequestMethod.GET,
        value = "/{moduleName}")
    @PreAuthorize("hasAnyAuthority('ALL','WebServiceAdmin')")
    public ResponseEntity getModule(@PathVariable("moduleName") String moduleName)
    {
        Module<Flow> module = moduleService.getModule(moduleName);
        if (module != null && module.getFlows()!=null)
        {
            List<FlowDto> flows = module.getFlows().stream()
                .map(flow -> new FlowDto(flow.getName(), flow.getState()))
                .collect(Collectors.toList());
            return new ResponseEntity(new ModuleDto(module.getName(), flows), HttpStatus.OK);
        }
        else
        {
            return new ResponseEntity(new ErrorDto("Module [" + StringEscapeUtils.escapeHtml4(moduleName) + "] not found."),
                HttpStatus.NOT_FOUND);
        }
    }

    /**
     * Retrieves the state of context listeners for a specific module and flow.
     *
     * @param moduleName The name of the module to retrieve context listener state for.
     * @param flowName The name of the flow within the module to retrieve context listener state for.
     * @return The state of context listeners for the specified module and flow. Returns "running"
     * if the context listeners are running, "stopped" otherwise.
     */
    @SuppressWarnings("unchecked")
    @RequestMapping(method = RequestMethod.GET,
        value = "/contextListenersState/{moduleName}/{flowName}")
    @PreAuthorize("hasAnyAuthority('ALL','WebServiceAdmin')")
    public String getContextListenersState(@PathVariable("moduleName") String moduleName,
        @PathVariable("flowName") String flowName)
    {
        Module<Flow> module = moduleService.getModule(moduleName);
        Flow flow = module.getFlow(flowName);
        return flow.areContextListenersRunning() ? "running" : "stopped";
    }

    /**
     * Changes the state of context listeners for a specific module and flow based on the action provided.
     *
     * @param moduleName The name of the module.
     * @param flowName The name of the flow within the module.
     * @param user The user performing the action.
     * @param action The action to be performed on the context listeners. Allowed values are "start" and "stop".
     *
     * @return A ResponseEntity object indicating the status of the context listeners state change.
     *         Returns a success response if the state change was successful.
     *         Returns a Forbidden response with an error message if the action is unknown or an exception occurs.
     */
    @RequestMapping(method = RequestMethod.PUT,
        value = "/controlContextListenersState/{moduleName}/{flowName}/{user}")
    @PreAuthorize("hasAnyAuthority('ALL','WebServiceAdmin')")
    public ResponseEntity controlContextListenersState(@PathVariable("moduleName") String moduleName,
        @PathVariable("flowName") String flowName, @PathVariable("user") String user, @RequestBody String action)
    {
        try
        {
            if (action.equalsIgnoreCase("start"))
            {
                this.moduleService.startContextListeners(moduleName, flowName, user);
            }
            else if (action.equalsIgnoreCase("stop"))
            {
                this.moduleService.stopContextListeners(moduleName, flowName, user);
            }
            else
            {
                return new ResponseEntity("Unknown context listener action ["
                    + StringEscapeUtils.escapeHtml4(action) + "].", HttpStatus.FORBIDDEN);
            }
        }
        catch (Exception e)
        {
            logger.error("Error changing context listeners state for module [{}], flow [{}], user [{}]."
                , moduleName, flowName, user, e);
            return new ResponseEntity("Unable to change Context Listeners state.", HttpStatus.FORBIDDEN);
        }
        return new ResponseEntity("Context Listeners state changed successfully!", HttpStatus.OK);
    }

    /**
     * Handles activation or deactivation of a module based on the provided ModuleActivationDto.
     *
     * @param moduleActivationDto The data transfer object containing the action to be performed on the module.
     *                           The action can be either "activate" or "deactivate".
     *
     * @return A ResponseEntity object indicating the success or failure of the module activation or deactivation.
     *         If the action is unknown, a Forbidden response is returned.
     *         If an exception occurs during the activation or deactivation process, a Forbidden response with the error message is returned.
     *         If the activation or deactivation is successful, a success response is returned.
     */
    @RequestMapping(method = RequestMethod.PUT,
        value = "/activator")
    @PreAuthorize("hasAnyAuthority('ALL','WebServiceAdmin')")
    public ResponseEntity activator(@RequestBody ModuleActivationDto moduleActivationDto)
    {
        try
        {
            Module<Flow> module = this.getModule();

            if (moduleActivationDto.getAction().equalsIgnoreCase("activate"))
            {
                this.moduleActivator.activate(module);
            }
            else if (moduleActivationDto.getAction().equalsIgnoreCase("deactivate"))
            {
                this.moduleActivator.deactivate(module);
            }
            else
            {
                return new ResponseEntity("Unknown module activation action ["
                    + StringEscapeUtils.escapeHtml4(moduleActivationDto.getAction()) + "].", HttpStatus.FORBIDDEN);
            }
        }
        catch (Exception e)
        {
            logger.error("Error applying module action [{}].", moduleActivationDto.getAction(), e);
            return new ResponseEntity("Unable to apply module activation / deactivation!", HttpStatus.FORBIDDEN);
        }
        return new ResponseEntity("Module action[%s] successfully applied!"
            .formatted(StringEscapeUtils.escapeHtml4(moduleActivationDto.getAction())), HttpStatus.OK);
    }

    /**
     * Determines whether a specified module is activated or deactivated.
     *
     * @param moduleName The name of the module to check activation status for.
     * @return "activated" if the module is activated, "deactivated" if the module is deactivated.
     */
    @RequestMapping(method = RequestMethod.GET,
        value = "/isActivated/{moduleName}")
    @PreAuthorize("hasAnyAuthority('ALL','WebServiceAdmin')")
    public String isActivated(@PathVariable("moduleName") String moduleName)
    {
        Module<Flow> module = moduleService.getModule(moduleName);
        return this.moduleActivator.isActivated(module) ? "activated" : "deactivated";
    }

    /**
     * Helper method to get a handle to the module.
     *
     * @return
     */
    private Module getModule() {
        AtomicReference<Module> module = new AtomicReference<>();

        this.moduleService.getModules().stream()
            .findFirst()
            .ifPresentOrElse(module::set, () -> {throw new RuntimeException("Could not load module!");});

        return module.get();
    }

}
