package org.ikasan.rest.module;

import org.ikasan.module.SimpleModule;
import org.ikasan.spec.flow.Flow;
import org.ikasan.spec.flow.FlowElement;
import org.ikasan.spec.module.Module;
import org.ikasan.spec.module.ModuleService;
import org.ikasan.spec.module.StartupType;
import org.ikasan.spec.rest.ModuleResource;

import javax.ws.rs.core.Context;
import javax.ws.rs.core.SecurityContext;
import java.util.List;

/**
 * Module application implementing the REST contract
 */
public class ModuleApplication implements ModuleResource
{
    private ModuleService moduleService;

    @Override
    public List<Module> getModules()
    {
        return moduleService.getModules();
    }

    public SimpleModule getModule(String moduleName)
    {
        return (SimpleModule)moduleService.getModule(moduleName);
    }

    public List<Flow> getFlows(String moduleName)
    {
        return getModule(moduleName).getFlows();
    }

    public List<FlowElement<?>> getFlowElements(String moduleName, String flowName)
    {
        return getModule(moduleName).getFlow(flowName).getFlowElements();
    }

    public void controlFlowState(@Context SecurityContext context, String moduleName, String flowName, String action)
    {
        String user = "unknown";
        if (context != null)
        {
            user = context.getUserPrincipal().getName();
        }
        if (action.equalsIgnoreCase("start"))
        {
            this.moduleService.startFlow(moduleName, flowName, user);
        }
        else if (action.equalsIgnoreCase("startPause"))
        {
            this.moduleService.startPauseFlow(moduleName, flowName, user);
        }
        else if (action.equalsIgnoreCase("pause"))
        {
            this.moduleService.pauseFlow(moduleName, flowName, user);
        }
        else if (action.equalsIgnoreCase("resume"))
        {
            this.moduleService.resumeFlow(moduleName, flowName, user);
        }
        else if (action.equalsIgnoreCase("stop"))
        {
            this.moduleService.stopFlow(moduleName, flowName, user);
        }
        else
        {
            throw new RuntimeException("Unknown flow action [" + action + "].");
        }
    }

    @Override
    public void controlFlowStartupMode(@Context SecurityContext context, String moduleName, String flowName, String startupType, String startupComment)
    {
        String user = "unknown";
        if (context != null)
        {
            user = context.getUserPrincipal().getName();
        }
        if ("manual".equalsIgnoreCase(startupType)
                || "automatic".equalsIgnoreCase(startupType)
                || "disabled".equalsIgnoreCase(startupType))
        {
            //crude check to ensure comment is supplied when disabling
            if (startupType.equalsIgnoreCase("disabled") && (startupComment == null || "".equals(startupComment.trim()) ))
            {
                throw new IllegalArgumentException("Comment must be provided when disabling Flow startup");
            }

            moduleService.setStartupType(moduleName, flowName, StartupType.valueOf(startupType), startupComment, user);
        }
        else
        {
            throw new RuntimeException("Unknown startupType:" + startupType);
        }
    }

    public ModuleService getModuleService()
    {
        return moduleService;
    }

    public void setModuleService(ModuleService moduleService)
    {
        this.moduleService = moduleService;
    }
}
