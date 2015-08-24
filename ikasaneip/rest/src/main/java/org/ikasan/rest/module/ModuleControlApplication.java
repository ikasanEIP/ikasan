package org.ikasan.rest.module;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;

import org.apache.log4j.Logger;
import org.ikasan.rest.IkasanRestApplication;
import org.ikasan.spec.flow.Flow;
import org.ikasan.spec.module.Module;
import org.ikasan.spec.module.ModuleService;
import org.ikasan.spec.module.StartupType;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Module application implementing the REST contract
 */
@Path("/moduleControl")
public class ModuleControlApplication extends IkasanRestApplication
{
	private static Logger logger = Logger.getLogger(ModuleControlApplication.class);
	
	@Autowired
    private ModuleService moduleService;
    
    public ModuleControlApplication()
    {
//    	this.moduleService = moduleService;
//    	if(this.moduleService == null)
//    	{
//    		throw new IllegalArgumentException("moduleService cannot be null!");
//    	}
    }

    @PUT
	@Path("/controlFlowState/{moduleName}/{flowName}")
    @Consumes("application/octet-stream")
    public Response controlFlowState(@Context SecurityContext context, @PathParam("moduleName") String moduleName, 
    		@PathParam("flowName") String flowName, String action)
    {
    	if(!context.isUserInRole("WebServiceAdmin"))
		{
			throw new WebApplicationException(Response.status(Response.Status.FORBIDDEN).type("text/plain")
	                .entity("You are not authorised to access this resource.").build());
		}
    	
    	try
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
	        	throw new WebApplicationException(Response.status(Response.Status.FORBIDDEN).type("text/plain")
		                .entity("Unknown flow action [" + action + "].").build());
	        }
    	}	
        catch(Exception e)
        {
        	throw new WebApplicationException(Response.status(Response.Status.FORBIDDEN).type("text/plain")
	                .entity(e.getMessage()).build());
        }
        
        return Response.ok("Flow state changed successfully!").build();
    }

    @PUT
	@Path("/controlFlowStartupMode/{moduleName}/{flowName}/{startupType}")
    @Consumes("application/octet-stream")
    public void controlFlowStartupMode(@Context SecurityContext context, @PathParam("moduleName") String moduleName, 
    		@PathParam("flowName") String flowName, @PathParam("startupType") String startupType, String startupComment)
    {
    	if(!context.isUserInRole("WebServiceAdmin"))
		{
			throw new WebApplicationException(Response.status(Response.Status.FORBIDDEN).type("text/plain")
	                .entity("You are not authorised to access this resource.").build());
		}
    	
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
        	throw new WebApplicationException(Response.status(Response.Status.FORBIDDEN).type("text/plain")
	                .entity("Unknown startup type!.").build());
        }
    }
    
    @SuppressWarnings("unchecked")
	@GET
	@Path("/flowState/{moduleName}/{flowName}")
	@Produces(MediaType.APPLICATION_JSON)	
	public String getFlowState(@Context SecurityContext context, @PathParam("moduleName") String moduleName, @PathParam("flowName") String flowName)
	{
		if(!context.isUserInRole("WebServiceAdmin"))
		{
			throw new WebApplicationException(Response.status(Response.Status.FORBIDDEN).type("text/plain")
	                .entity("You are not authorised to access this resource.").build());
		}
		
		Module<Flow> module = moduleService.getModule(moduleName);
		Flow flow = module.getFlow(flowName);
		
		return flow.getState();
	}
    
    @SuppressWarnings("unchecked")
	@GET
	@Path("/flowStates/{moduleName}")
	@Produces(MediaType.APPLICATION_JSON)	
	public Map<String, String> getFlowStates(@Context SecurityContext context, @PathParam("moduleName") String moduleName)
	{
		logger.info("Attempting to get states for module: " + moduleName);
		
    	if(!context.isUserInRole("WebServiceAdmin"))
		{
			throw new WebApplicationException(Response.status(Response.Status.FORBIDDEN).type("text/plain")
	                .entity("You are not authorised to access this resource.").build());
		}
		
		HashMap<String, String> results = new HashMap<String, String>();

		Module<Flow> module = moduleService.getModule(moduleName);
		
		logger.info("moduleService: " + moduleService);
		
		List<Flow> flows = module.getFlows();
		
		for(Flow flow: flows)
		{
			results.put(module.getName() + "-" + flow.getName()
					, flow.getState());
		}
		
		logger.info("results: " + results);
		
		return results;
	}

}
