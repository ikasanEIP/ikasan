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

import org.slf4j.Logger; import org.slf4j.LoggerFactory;
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

private static Logger logger = LoggerFactory.getLogger(ResubmissionApplication.class);
	
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
	}

	/**
	 * Create component configuration.
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
		if(!context.isUserInRole("WebServiceAdmin") && !context.isUserInRole("ALL"))
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
	
	/**
	 * TODO: work out how to get annotation security working.
	 * 
	 * @param context
	 * @param moduleName
	 * @param flowName
	 * @return
	 */
	@GET
	@Path("/createFlowElementConfiguration/{moduleName}/{flowName}/{componentName}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response createFlowElementConfiguration(@Context SecurityContext context, @PathParam("moduleName") String moduleName, @PathParam("flowName") String flowName,
			@PathParam("componentName") String componentName)
	{
		if(!context.isUserInRole("WebServiceAdmin") && !context.isUserInRole("ALL"))
		{
			throw new WebApplicationException(Response.status(Response.Status.FORBIDDEN).type("text/plain")
	                .entity("You are not authorised to access this resource.").build());
		}
		
		Module<Flow> module = moduleContainer.getModule(moduleName);
		
		Flow flow = module.getFlow(flowName);
		
		FlowElement<?> flowElement = flow.getFlowElement(componentName);
		
		Configuration configuration = null;		
		
		if(flowElement instanceof ConfiguredResource)
		{
			ConfiguredResource configuredResource = (ConfiguredResource)flowElement;
			
			String configurationId = moduleName + flowName + componentName + "_element";
			
			configuredResource.setConfiguredResourceId(configurationId);
			
			configuration = this.configurationManagement.getConfiguration(configuredResource.getConfiguredResourceId());
			
			if(configuration == null)
			{
				configuration = this.configurationManagement.createConfiguration(configuredResource);
				this.configurationManagement.saveConfiguration(configuration);
			}
			else
			{
				throw new WebApplicationException(Response.status(Response.Status.UNAUTHORIZED).type("text/plain")
	                .entity("This flow element configuration alread exists!").build());
			}
		}
		else
		{
			throw new WebApplicationException(Response.status(Response.Status.UNAUTHORIZED).type("text/plain")
	                .entity("This flow element is not configurable!").build());
		}
		
		return Response.ok("Configuration created!").build();
	}
	
	/**
	 * Create the configuration for a flow
	 * 
	 * @param context
	 * @param moduleName
	 * @param flowName
	 * @return
	 */
	@GET
	@Path("/createConfiguration/{moduleName}/{flowName}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response createFlowConfiguration(@Context SecurityContext context, @PathParam("moduleName") String moduleName, @PathParam("flowName") String flowName)
	{
		if(!context.isUserInRole("WebServiceAdmin") && !context.isUserInRole("ALL"))
		{
			throw new WebApplicationException(Response.status(Response.Status.FORBIDDEN).type("text/plain")
	                .entity("You are not authorised to access this resource.").build());
		}
		
		Module<Flow> module = moduleContainer.getModule(moduleName);
		
		Flow flow = module.getFlow(flowName);
		
		Configuration configuration = null;
		
		if(flow instanceof ConfiguredResource)
		{
			ConfiguredResource configuredResource = (ConfiguredResource)flow;
			
			configuration = this.configurationManagement.getConfiguration(configuredResource.getConfiguredResourceId());
			
			if(configuration == null)
			{
				configuration = this.configurationManagement.createConfiguration(configuredResource);
				this.configurationManagement.saveConfiguration(configuration);
			}
			else
			{
				throw new WebApplicationException(Response.status(Response.Status.UNAUTHORIZED).type("text/plain")
	                .entity("This flow configuration alread exists!").build());
			}
		}
		else
		{
			throw new WebApplicationException(Response.status(Response.Status.UNAUTHORIZED).type("text/plain")
	                .entity("This flow is not configurable!").build());
		}
		
		return Response.ok("Configuration created!").build();
	}

    /**
     * Create invoker configuration.
	 *
     * @param context
	 * @param moduleName
	 * @param flowName
	 * @return
     */
    @GET
    @Path("/createInvokerConfiguration/{moduleName}/{flowName}/{componentName}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response createInvokerConfiguration(@Context SecurityContext context, @PathParam("moduleName") String moduleName, @PathParam("flowName") String flowName,
                                        @PathParam("componentName") String componentName)
    {
        if(!context.isUserInRole("WebServiceAdmin") && !context.isUserInRole("ALL"))
        {
            throw new WebApplicationException(Response.status(Response.Status.FORBIDDEN).type("text/plain")
                .entity("You are not authorised to access this resource.").build());
        }

        Module<Flow> module = moduleContainer.getModule(moduleName);

        Flow flow = module.getFlow(flowName);

        FlowElement<?> flowElement = flow.getFlowElement(componentName);

        Configuration configuration = null;

        if(flowElement.getFlowElementInvoker() instanceof ConfiguredResource)
        {
            ConfiguredResource configuredResource = (ConfiguredResource)flowElement.getFlowElementInvoker();

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
