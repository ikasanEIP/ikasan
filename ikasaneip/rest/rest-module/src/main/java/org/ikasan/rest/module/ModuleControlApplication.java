package org.ikasan.rest.module;

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

    @Deprecated
    @RequestMapping(method = RequestMethod.PUT,
        value = "/controlFlowState/{moduleName}/{flowName}")
    @PreAuthorize("hasAnyAuthority('ALL','WebServiceAdmin')")
    public ResponseEntity controlFlowState(
        @PathVariable("moduleName") String moduleName,
        @PathVariable("flowName") String flowName,
        @RequestBody String action)
    {
        try
        {
            String user = UserUtil.getUser();

            switch (action){
            case "start":
                this.moduleService.startFlow(moduleName, flowName, user);break;
            case "startPause":
                this.moduleService.startPauseFlow(moduleName, flowName, user);break;
            case "pause":
                this.moduleService.pauseFlow(moduleName, flowName, user);break;
            case "resume":
                this.moduleService.resumeFlow(moduleName, flowName, user);break;
            case "stop":
                this.moduleService.stopFlow(moduleName, flowName, user);break;
            default:
                return new ResponseEntity("Unknown flow action [" + action + "].", HttpStatus.FORBIDDEN);
            }

        }
        catch (Exception e)
        {
            return new ResponseEntity(e.getMessage(), HttpStatus.FORBIDDEN);
        }
        return new ResponseEntity("Flow state changed successfully!", HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.PUT)
    @PreAuthorize("hasAnyAuthority('ALL','WebServiceAdmin')")
    public ResponseEntity changeFlowState(@RequestBody ChangeFlowStateDto changeFlowStateDto)
    {
        try
        {
            String user = UserUtil.getUser();

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


    @Deprecated
    @RequestMapping(method = RequestMethod.PUT,
        value = "/controlFlowStartupMode/{moduleName}/{flowName}/{startupType}")
    @PreAuthorize("hasAnyAuthority('ALL','WebServiceAdmin')")
    public void controlFlowStartupMode(@PathVariable("moduleName") String moduleName,
        @PathVariable("flowName") String flowName, @PathVariable("startupType") String startupType,
        @RequestBody String startupComment)
    {
        String user = UserUtil.getUser();
        if ("manual".equalsIgnoreCase(startupType)
            || "automatic".equalsIgnoreCase(startupType)
            || "disabled".equalsIgnoreCase(startupType))
        {
            //crude check to ensure comment is supplied when disabling
            if (startupType.equalsIgnoreCase("disabled") && (startupComment == null || ""
                .equals(startupComment.trim())))
            {
                throw new IllegalArgumentException("Comment must be provided when disabling Flow startup");
            }
            moduleService.setStartupType(moduleName, flowName, StartupType.valueOf(startupType), startupComment, user);
            moduleMetadataDashboardRestService.publish(this.moduleService.getModule(moduleName));
        }
    }

    @RequestMapping(method = RequestMethod.PUT,
        value = "/startupMode")
    @PreAuthorize("hasAnyAuthority('ALL','WebServiceAdmin')")
    public ResponseEntity changeFlowStartupMode(
        @RequestBody ChangeFlowStartupModeDto changeFlowStartupModeDto)
    {
        String user = UserUtil.getUser();

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
            return new ResponseEntity(new ErrorDto("Invalid startupType["+startupType+"]."), HttpStatus.BAD_REQUEST);

        }
    }

    @RequestMapping(method = RequestMethod.PUT,
        value = "/startupMode/allFlows")
    @PreAuthorize("hasAnyAuthority('ALL','WebServiceAdmin')")
    public ResponseEntity changeAllFlowStartupMode(
        @RequestBody ChangeFlowStartupModeDto changeFlowStartupModeDto)
    {
        String user = UserUtil.getUser();

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
            return new ResponseEntity(new ErrorDto("Invalid startupType["+startupType+"]."), HttpStatus.BAD_REQUEST);

        }
    }

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

    @Deprecated
    @RequestMapping(method = RequestMethod.GET,
        value = "/flowState/{moduleName}/{flowName}")
    @PreAuthorize("hasAnyAuthority('ALL','WebServiceAdmin')")
    public String getFlowState(@PathVariable("moduleName") String moduleName,
        @PathVariable("flowName") String flowName)
    {
        Module<Flow> module = moduleService.getModule(moduleName);
        Flow flow = module.getFlow(flowName);
        return flow.getState();
    }

    @RequestMapping(method = RequestMethod.GET,
        value = "/{moduleName}/{flowName}",
        produces = "application/json")
    @PreAuthorize("hasAnyAuthority('ALL','WebServiceAdmin')")
    public ResponseEntity getFlow(@PathVariable("moduleName") String moduleName,
        @PathVariable("flowName") String flowName)
    {
        Module<Flow> module = moduleService.getModule(moduleName);
        Flow flow = module.getFlow(flowName);
        if (flow != null)
            return new ResponseEntity(new FlowDto(flow.getName(), flow.getState()), HttpStatus.OK);
        else
        {
            return new ResponseEntity(new ErrorDto("Module [" + moduleName + "] Flow [" + flowName + "] not found."),
                HttpStatus.NOT_FOUND);
        }
    }

    @Deprecated
    @RequestMapping(method = RequestMethod.GET,
        value = "/flowStates/{moduleName}")
    @PreAuthorize("hasAnyAuthority('ALL','WebServiceAdmin')")
    public Map<String, String> getFlowStates(@PathVariable("moduleName") String moduleName)
    {
        HashMap<String, String> results = new HashMap<String, String>();
        Module<Flow> module = moduleService.getModule(moduleName);
        List<Flow> flows = module.getFlows();
        for (Flow flow : flows)
        {
            results.put(module.getName() + "-" + flow.getName()
                , flow.getState());
        }
        return results;
    }

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
            return new ResponseEntity(new ErrorDto("Module [" + moduleName + "] not found."),
                HttpStatus.NOT_FOUND);
        }
    }

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

    @RequestMapping(method = RequestMethod.PUT,
        value = "/controlContextListenersState/{moduleName}/{flowName}")
    @PreAuthorize("hasAnyAuthority('ALL','WebServiceAdmin')")
    public ResponseEntity controlContextListenersState(@PathVariable("moduleName") String moduleName,
        @PathVariable("flowName") String flowName, @RequestBody String action)
    {
        try
        {
            String user = UserUtil.getUser();
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
                return new ResponseEntity("Unknown context listener action [" + action + "].", HttpStatus.FORBIDDEN);
            }
        }
        catch (Exception e)
        {
            return new ResponseEntity(e.getMessage(), HttpStatus.FORBIDDEN);
        }
        return new ResponseEntity("Context Listeners state changed successfully!", HttpStatus.OK);
    }

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
                return new ResponseEntity("Unknown module activation action [" + moduleActivationDto.getAction() + "].", HttpStatus.FORBIDDEN);
            }
        }
        catch (Exception e)
        {
            return new ResponseEntity(e.getMessage(), HttpStatus.FORBIDDEN);
        }
        return new ResponseEntity(String.format("Module action[%s] successfully applied!", moduleActivationDto.getAction()), HttpStatus.OK);
    }

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
