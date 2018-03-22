/*
 * $Id$  
 * $URL$
 * 
 * ====================================================================
 * Ikasan Enterprise Integration Platform
 * 
 * Distributed under the Modified BSD License.
 * Copyright notice: The copyright for this software and a full listing 
 * of individual contributors are as shown in the packaged copyright.txt 
 * file. 
 * 
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without 
 * modification, are permitted provided that the following conditions are met:
 *
 *  - Redistributions of source code must retain the above copyright notice, 
 *    this list of conditions and the following disclaimer.
 *
 *  - Redistributions in binary form must reproduce the above copyright notice, 
 *    this list of conditions and the following disclaimer in the documentation 
 *    and/or other materials provided with the distribution.
 *
 *  - Neither the name of the ORGANIZATION nor the names of its contributors may
 *    be used to endorse or promote products derived from this software without 
 *    specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" 
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE 
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE 
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE 
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL 
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR 
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER 
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE 
 * USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 * ====================================================================
 */
package org.ikasan.dashboard.configurationManagement.rest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.ikasan.configurationService.util.*;
import org.ikasan.dashboard.ui.framework.util.DocumentValidator;
import org.ikasan.dashboard.ui.framework.util.SchemaValidationErrorHandler;
import org.ikasan.dashboard.ui.framework.util.XmlFormatter;
import org.ikasan.spec.configuration.Configuration;
import org.ikasan.spec.configuration.ConfigurationManagement;
import org.ikasan.spec.configuration.ConfiguredResource;
import org.ikasan.spec.configuration.PlatformConfigurationService;
import org.ikasan.topology.model.Flow;
import org.ikasan.topology.model.Module;
import org.ikasan.topology.service.TopologyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.xml.sax.SAXParseException;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import java.util.List;

/**
 * Module application implementing the REST contract
 */
@Component
@Path("/configuration")
public class ConfigurationApplication
{
	private static Logger logger = LoggerFactory.getLogger(ConfigurationApplication.class);

	@Autowired
	private TopologyService topologyService;

    @Autowired
    private ModuleConfigurationExportHelper moduleConfigurationExportHelper;

    @Autowired
    private ModuleConfigurationImportHelper moduleConfigurationImportHelper;

    @Autowired
    private FlowConfigurationExportHelper flowConfigurationExportHelper;

    @Autowired
    private FlowConfigurationImportHelper flowConfigurationImportHelper;

    @Autowired
    private ComponentConfigurationExportHelper componentConfigurationExportHelper;

    @Autowired
    private ComponentConfigurationImportHelper componentConfigurationImportHelper;

    @Autowired
    private ConfigurationManagement<ConfiguredResource, Configuration> configurationService;

    @Autowired
    private PlatformConfigurationService platformConfigurationService;

    /**
     * Registers the applications we implement and the Spring-Jersey glue
     */
    public ConfigurationApplication()
    {
    }

    @GET
	@Path("/module/{moduleName}")
    @Produces(MediaType.APPLICATION_JSON)
    public String getModuleConfiguration(@Context SecurityContext context, @PathParam("moduleName") String moduleName)
    {
        if(!context.isUserInRole("WebServiceAdmin") && !context.isUserInRole("ALL"))
        {
            throw new WebApplicationException(Response.status(Response.Status.FORBIDDEN).type("text/plain")
                    .entity("You are not authorised to access this resource.").build());
        }

        logger.info("Getting configuration: ModuleName: "
        		+ moduleName);

        Module module = this.topologyService.getModuleByName(moduleName);

        if(module != null)
        {
            this.moduleConfigurationExportHelper.setSchemaLocation(this.getSchemaLocation("moduleConfigurationSchemaLocation"));
            return XmlFormatter.format(moduleConfigurationExportHelper.getModuleConfigurationExportXml(module));
        }
        else
        {
            throw new WebApplicationException(Response.status(Response.Status.NOT_FOUND).type("text/plain")
                    .entity("Cannot find configuration for module: ModuleName [" + moduleName + "]").build());
        }
    }

    @PUT
    @Path("/update/{moduleName}")
    @Consumes("application/octet-stream")
    public Response updateModuleConfigurations(@Context SecurityContext context, @PathParam("moduleName") String moduleName,
                           byte[] moduleConfiguration)
    {
        if(!context.isUserInRole("WebServiceAdmin") && !context.isUserInRole("ALL"))
        {
            return Response.status(Response.Status.FORBIDDEN).type("text/plain")
                    .entity("You are not authorised to access this resource.").build();
        }

        String documentValidationError = this.validateConfigurationDocument(moduleConfiguration);

        if(!documentValidationError.isEmpty())
        {
            return Response.status(Response.Status.NOT_FOUND).type("text/plain")
                    .entity("An error has occurred trying to update a module configuration. The configuration document failed schema validation: " + documentValidationError).build();
        }

        Module module = this.topologyService.getModuleByName(moduleName);

        if(module == null)
        {
            return Response.status(Response.Status.NOT_FOUND).type("text/plain")
                    .entity("An error has occurred trying to update a module configuration. Module[" + moduleName + "] is NULL!").build();
        }

        try
        {
            this.moduleConfigurationImportHelper.updateModuleConfiguration(module, moduleConfiguration);
            this.moduleConfigurationImportHelper.save();
        }
        catch (Exception e)
        {
            e.printStackTrace();

            logger.error("An error has occurred trying to update a module configuration: ", e);

            return Response.status(Response.Status.NOT_FOUND).type("text/plain")
                    .entity("An error has occurred trying to update a module configuration. " + e.getMessage()).build();
        }

        return Response.ok("Module component configurations updated!").build();
    }

    @GET
    @Path("/flow/{moduleName}/{flowName}")
    @Produces(MediaType.APPLICATION_JSON)
    public String getFlowConfiguration(@Context SecurityContext context, @PathParam("moduleName") String moduleName,
                                       @PathParam("flowName") String flowName)
    {
        if(!context.isUserInRole("WebServiceAdmin") && !context.isUserInRole("ALL"))
        {
            throw new WebApplicationException(Response.status(Response.Status.FORBIDDEN).type("text/plain")
                    .entity("You are not authorised to access this resource.").build());
        }

        logger.info("Getting flow configuration: ModuleName [" + moduleName + "] FlowName[" + flowName + "]");

        Flow returnFlow = this.getFlow(moduleName, flowName);

        if(returnFlow != null)
        {
            this.flowConfigurationExportHelper.setSchemaLocation(this.getSchemaLocation("flowConfigurationSchemaLocation"));
            return XmlFormatter.format(this.flowConfigurationExportHelper.getFlowConfigurationExportXml(returnFlow));
        }
        else
        {
            throw new WebApplicationException(Response.status(Response.Status.NOT_FOUND).type("text/plain")
                    .entity("Cannot find configuration for flow: ModuleName [" + moduleName + "] FlowName[" + flowName + "]").build());
        }
    }

    @PUT
    @Path("/update/{moduleName}/{flowName}")
    @Consumes("application/octet-stream")
    public Response updateFlowConfigurations(@Context SecurityContext context, @PathParam("moduleName") String moduleName, @PathParam("flowName") String flowName,
                                               byte[] flowConfiguration)
    {
        if(!context.isUserInRole("WebServiceAdmin") && !context.isUserInRole("ALL"))
        {
            return Response.status(Response.Status.FORBIDDEN).type("text/plain")
                    .entity("You are not authorised to access this resource.").build();
        }

        String documentValidationError = this.validateConfigurationDocument(flowConfiguration);

        if(!documentValidationError.isEmpty())
        {
            return Response.status(Response.Status.NOT_FOUND).type("text/plain")
                    .entity("An error has occurred trying to update a flow configuration. The configuration document failed schema validation: " + documentValidationError).build();
        }

        Flow flow = this.getFlow(moduleName, flowName);

        if(flow == null)
        {
            return Response.status(Response.Status.NOT_FOUND).type("text/plain")
                    .entity("An error has occurred trying to update a flow configuration. Flow (ModuleName[" + moduleName + "], FlowName[" + flowName + "]) is NULL!").build();
        }

        try
        {
            this.flowConfigurationImportHelper.updateFlowConfiguration(flow, flowConfiguration);
            this.flowConfigurationImportHelper.save();
        }
        catch (Exception e)
        {
            e.printStackTrace();

            logger.error("An error has occurred trying to update a flow configuration: ", e);

            return Response.status(Response.Status.NOT_FOUND).type("text/plain")
                    .entity("An error has occurred trying to update a flow configuration. " + e.getMessage()).build();
        }

        return Response.ok("Flow component configurations updated!").build();
    }

    @GET
    @Path("/component/{moduleName}/{flowName}/{componentIdentifier}")
    @Produces(MediaType.APPLICATION_JSON)
    public String getComponentConfiguration(@Context SecurityContext context, @PathParam("moduleName") String moduleName,
                                            @PathParam("flowName") String flowName, @PathParam("componentIdentifier") String componentIdentifier)
    {
        if(!context.isUserInRole("WebServiceAdmin") && !context.isUserInRole("ALL"))
        {
            throw new WebApplicationException(Response.status(Response.Status.FORBIDDEN).type("text/plain")
                    .entity("You are not authorised to access this resource.").build());
        }

        logger.info("Getting configuration: ModuleName: "
                + moduleName);

        Flow flow = this.getFlow(moduleName, flowName);

        org.ikasan.topology.model.Component returnComponent = this.getComponent(moduleName, flowName, componentIdentifier);

        if(returnComponent != null)
        {
            if(!returnComponent.isConfigurable())
            {
                throw new WebApplicationException(Response.status(Response.Status.NOT_FOUND).type("text/plain")
                        .entity("Component is not a configured resource: ModuleName [" + moduleName + "] FlowName[" + flowName + "]"
                                + "] ComponentName[" + componentIdentifier + "]").build());
            }

            Configuration configuration = this.configurationService.getConfiguration(returnComponent.getConfigurationId());

            if(configuration == null)
            {
                throw new WebApplicationException(Response.status(Response.Status.NOT_FOUND).type("text/plain")
                        .entity("Cannot find configuration for component. It may not have been created yet: ModuleName [" + moduleName + "] FlowName[" + flowName + "]"
                                + "] ComponentName[" + componentIdentifier + "]").build());

            }

            this.componentConfigurationExportHelper.setSchemaLocation(this.getSchemaLocation("componentConfigurationSchemaLocation"));
            return XmlFormatter.format(this.componentConfigurationExportHelper.getComponentConfigurationExportXml(configuration));
        }
        else
        {
            throw new WebApplicationException(Response.status(Response.Status.NOT_FOUND).type("text/plain")
                    .entity("Cannot find component: ModuleName [" + moduleName + "] FlowName[" + flowName + "]"
                            + "] ComponentName[" + componentIdentifier + "]").build());
        }
    }

    @PUT
    @Path("/update/{moduleName}/{flowName}/{componentIdentifier}")
    @Consumes("application/octet-stream")
    public Response updateComponentConfiguration(@Context SecurityContext context, @PathParam("moduleName") String moduleName, @PathParam("flowName") String flowName,
                                                 @PathParam("componentIdentifier") String componentIdentifier, byte[] componentConfiguration)
    {
        if(!context.isUserInRole("WebServiceAdmin") && !context.isUserInRole("ALL"))
        {
            return Response.status(Response.Status.FORBIDDEN).type("text/plain")
                    .entity("You are not authorised to access this resource.").build();
        }

        String documentValidationError = this.validateConfigurationDocument(componentConfiguration);

        if(!documentValidationError.isEmpty())
        {
            return Response.status(Response.Status.NOT_FOUND).type("text/plain")
                    .entity("An error has occurred trying to update a component configuration. The configuration document failed schema validation: " + documentValidationError).build();
        }

        org.ikasan.topology.model.Component component = this.getComponent(moduleName, flowName, componentIdentifier);

        if(component == null)
        {
            return Response.status(Response.Status.NOT_FOUND).type("text/plain")
                    .entity("An error has occurred trying to update a component configuration. Component (ModuleName [" + moduleName + "] FlowName[" + flowName + "]"
                            + "] Component Identifier[" + componentIdentifier + "]) is NULL!").build();
        }

        Configuration configuration = this.configurationService.getConfiguration(component.getConfigurationId());

        if(configuration == null)
        {
            return Response.status(Response.Status.NOT_FOUND).type("text/plain")
                    .entity("Cannot find configuration for component. It may not have been created yet: ModuleName [" + moduleName + "] FlowName[" + flowName + "]"
                            + "] Component Identifier[" + componentIdentifier + "]").build();
        }

        try
        {
            this.componentConfigurationImportHelper.updateComponentConfiguration(configuration, componentConfiguration);
            this.configurationService.saveConfiguration(configuration);

        }
        catch (Exception e)
        {
            e.printStackTrace();

            logger.error("An error has occurred trying to update a flow configuration: ", e);

            return Response.status(Response.Status.NOT_FOUND).type("text/plain")
                    .entity("An error has occurred trying to update a flow configuration. " + e.getMessage()).build();
        }

        return Response.ok("Component configuration updated!").build();
    }

    private Flow getFlow(String moduleName, String flowName)
    {
        Flow returnFlow = null;

        List<Flow> flows = this.topologyService.getAllFlows();
        
        for(Flow flow: flows)
        {
            if((flow.getModule() != null && flow.getModule().getName().equals(moduleName)
                    && flow.getName().equals(flowName)))
            {
                returnFlow = flow;
                break;
            }
        }

        return returnFlow;
    }


    private org.ikasan.topology.model.Component getComponent(String moduleName, String flowName, String componentIdentifier)
    {
        Flow flow = this.getFlow(moduleName, flowName);

        org.ikasan.topology.model.Component returnComponent = null;

        // Try to get the component using the configured resource id.
        if(flow != null)
        {
            for(org.ikasan.topology.model.Component component: flow.getComponents())
            {
                if(component.getConfigurationId() != null && component.getConfigurationId().equals(componentIdentifier))
                {
                    returnComponent = component;
                    break;
                }
            }
        }

        // If the component is not found using the configured resource id, then try with the component name.
        if(flow != null && returnComponent == null)
        {
            for(org.ikasan.topology.model.Component component: flow.getComponents())
            {
                if(component.getName() != null && component.getName().equals(componentIdentifier))
                {
                    returnComponent = component;
                    break;
                }
            }
        }

        return returnComponent;
    }

    private String getSchemaLocation(String configurationName)
    {
        String schemaLocation = (String)this.platformConfigurationService.getConfigurationValue(configurationName);

        if(schemaLocation == null || schemaLocation.length() == 0)
        {
            throw new RuntimeException("Cannot resolve the platform configuration mappingExportSchemaLocation!");
        }

        return schemaLocation;
    }

    private String validateConfigurationDocument(byte[] configurationDocument)
    {
        StringBuffer errors = new StringBuffer();

        SchemaValidationErrorHandler errorHandler = null;

        try
        {
            errorHandler = DocumentValidator.validateUploadedDocument(configurationDocument);
        }
        catch (Exception e)
        {
            e.printStackTrace();

            return "Error occurred trying to validate configuration document: " + e.getMessage();
        }

        if(errorHandler.isInError())
        {
            for(SAXParseException exception: errorHandler.getErrors())
            {
                errors.append(exception.getMessage()).append("\n");
            }

            for(SAXParseException exception: errorHandler.getFatal())
            {
                errors.append(exception.getMessage()).append("\n");
            }
        }

        return errors.toString();
    }
}
