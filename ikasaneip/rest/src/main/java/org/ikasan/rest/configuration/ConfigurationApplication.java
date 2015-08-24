package org.ikasan.rest.configuration;

import javax.ws.rs.GET;
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
import org.ikasan.rest.submit.ResubmissionApplication;
import org.ikasan.spec.configuration.Configuration;
import org.ikasan.spec.configuration.ConfigurationManagement;
import org.ikasan.spec.configuration.ConfiguredResource;
import org.ikasan.spec.flow.Flow;
import org.ikasan.spec.flow.FlowElement;
import org.ikasan.spec.module.Module;
import org.ikasan.spec.module.ModuleContainer;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Configuration application implementing the REST contract
 */
@Path("/configuration")
public class ConfigurationApplication extends IkasanRestApplication
{

private static Logger logger = Logger.getLogger(ResubmissionApplication.class);
	
	@Autowired
	private ConfigurationManagement<ConfiguredResource, Configuration> configurationManagement;
	@Autowired
	private ModuleContainer moduleContainer;

	/**
	 * @param hospitalService
	 */
	public ConfigurationApplication()
	{
		super();
//		this.configurationManagement = configurationManagement;
//		if(this.configurationManagement == null)
//		{
//			throw new IllegalArgumentException("configurationManagement cannot be null!");
//		}
//		this.moduleContainer = moduleContainer;
//		if(this.moduleContainer == null)
//		{
//			throw new IllegalArgumentException("moduleContainer cannot be null!");
//		}
	}

	/**
	 * TODO: work out how to get annotation security working.
	 * 
	 * @param context
	 * @param moduleName
	 * @param flowName
	 * @return
	 */
	@GET
	@Path("/createConfiguration/{moduleName}/{flowName}/{componentName}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response createConfiguration(@Context SecurityContext context, @PathParam("moduleName") String moduleName, @PathParam("flowName") String flowName,
			@PathParam("componentName") String componentName)
	{
		if(!context.isUserInRole("WebServiceAdmin"))
		{
			throw new WebApplicationException(Response.status(Response.Status.FORBIDDEN).type("text/plain")
	                .entity("You are not authorised to access this resource.").build());
		}
		
		Module<Flow> module = moduleContainer.getModule(moduleName);
		
		Flow flow = module.getFlow(flowName);
		
		FlowElement<?> flowElement = flow.getFlowElement(componentName);
		
		Configuration configuration = null;
		
		if(flowElement.getFlowComponent() instanceof ConfiguredResource)
		{
			ConfiguredResource configuredResource = (ConfiguredResource)flowElement.getFlowComponent();
			
			configuration = this.configurationManagement.getConfiguration(configuredResource.getConfiguredResourceId());
			
			if(configuration == null)
			{
				configuration = this.configurationManagement.createConfiguration(configuredResource);
				this.configurationManagement.saveConfiguration(configuration);
			}
			else
			{
				throw new WebApplicationException(Response.status(Response.Status.UNAUTHORIZED).type("text/plain")
	                .entity("This configuration alread exists!").build());
			}
		}
		else
		{
			throw new WebApplicationException(Response.status(Response.Status.UNAUTHORIZED).type("text/plain")
	                .entity("This component is not configurable!").build());
		}
		
		return Response.ok("Configuration created!").build();
	}
}
