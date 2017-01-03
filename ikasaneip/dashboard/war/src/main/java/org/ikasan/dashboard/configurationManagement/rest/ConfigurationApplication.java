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

import com.vaadin.ui.Notification;
import org.apache.log4j.Logger;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.client.authentication.HttpAuthenticationFeature;
import org.ikasan.configurationService.util.*;
import org.ikasan.dashboard.ui.framework.util.DocumentValidator;
import org.ikasan.dashboard.ui.framework.util.SchemaValidationErrorHandler;
import org.ikasan.dashboard.ui.framework.util.XmlFormatter;
import org.ikasan.spec.configuration.Configuration;
import org.ikasan.spec.configuration.ConfigurationManagement;
import org.ikasan.spec.configuration.ConfiguredResource;
import org.ikasan.spec.configuration.PlatformConfigurationService;
import org.ikasan.topology.model.Component;
import org.ikasan.topology.model.Flow;
import org.ikasan.topology.model.Module;
import org.ikasan.topology.service.TopologyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import javax.ws.rs.*;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;
import java.io.IOException;
import java.util.List;

/**
 * Module application implementing the REST contract
 */
@Path("/configuration")
public class ConfigurationApplication
{
	private static Logger logger = Logger.getLogger(ConfigurationApplication.class);

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
            throw new RuntimeException("You are not authorised to access this resource.");
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
            throw new RuntimeException("Cannot find configuration for module: ModuleName [" + moduleName + "]");
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
            throw new RuntimeException("You are not authorised to access this resource.");
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
            throw new RuntimeException("Cannot find configuration for flow: ModuleName [" + moduleName + "] FlowName[" + flowName + "]");
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
            throw new RuntimeException("You are not authorised to access this resource.");
        }

        logger.info("Getting configuration: ModuleName: "
                + moduleName);

        Flow flow = this.getFlow(moduleName, flowName);

        Component returnComponent = this.getComponent(moduleName, flowName, componentIdentifier);

        if(returnComponent != null)
        {
            if(!returnComponent.isConfigurable())
            {
                throw new RuntimeException("Component is not a configured resource: ModuleName [" + moduleName + "] FlowName[" + flowName + "]"
                        + "] ComponentName[" + componentIdentifier + "]");
            }

            Configuration configuration = this.configurationService.getConfiguration(returnComponent.getConfigurationId());

            if(configuration == null)
            {
                throw new RuntimeException("Cannot find configuration for component. It may not have been created yet: ModuleName [" + moduleName + "] FlowName[" + flowName + "]"
                        + "] ComponentName[" + componentIdentifier + "]");
            }

            this.componentConfigurationExportHelper.setSchemaLocation(this.getSchemaLocation("componentConfigurationSchemaLocation"));
            return XmlFormatter.format(this.componentConfigurationExportHelper.getComponentConfigurationExportXml(configuration));
        }
        else
        {
            throw new RuntimeException("Cannot find configuration for component: ModuleName [" + moduleName + "] FlowName[" + flowName + "]"
                    + "] ComponentName[" + componentIdentifier + "]");
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

        Component component = this.getComponent(moduleName, flowName, componentIdentifier);

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

    private Component getComponent(String moduleName, String flowName, String componentIdentifier)
    {
        Flow flow = this.getFlow(moduleName, flowName);

        Component returnComponent = null;

        // Try to get the component using the configured resource id.
        if(flow != null)
        {
            for(Component component: flow.getComponents())
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
            for(Component component: flow.getComponents())
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

    public static final void main(String[] args)
    {
        String url = "http://svc-ikasand:8080/ikasan-dashboard/rest/configuration/flow/cdw-eod/EOD%20Non%20Trade%20File%20Serial%20Load%20Flow";

        HttpAuthenticationFeature feature = HttpAuthenticationFeature.basic("admin", "admin");

        ClientConfig clientConfig = new ClientConfig();
        clientConfig.register(feature);

        Client client = ClientBuilder.newClient(clientConfig);

        WebTarget webTarget = client.target(url);

        Response response = webTarget.request().get();

        response.bufferEntity();

        String responseMessage = response.readEntity(String.class);

        System.out.println(responseMessage);

        url = "http://svc-ikasand:8080/ikasan-dashboard/rest/configuration/update/cdw-eod/EOD%20Non%20Trade%20File%20Serial%20Load%20Flow";

        webTarget = client.target(url);
        response = webTarget.request().put(Entity.entity(responseMessage
                , MediaType.APPLICATION_OCTET_STREAM));

        responseMessage = response.readEntity(String.class);

        System.out.println(responseMessage);

        String badFlow = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><flowConfiguration xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:noNamespaceSchemaLocation=\"http://svc-ikasand:8080/ikasan-dashboard/static/org/ikasan/dashboard/flowConfiguration.xsd\">  <module>cdw-eod</module>  <name>EOD Non Trade File Serial Load Flow</name>  <componentConfigurations>    <componentConfiguration>      <id>cdw-eod-fileConsumer</id>      <description/>      <parameters>        <integerParameter>          <name>directoryDepth</name>          <value>1</value>          <description/>        </integerParameter>        <booleanParameter>          <name>ignoreMisfire</name>          <value>bad</value>          <description/>        </booleanParameter>        <booleanParameter>          <name>includeHeader</name>          <value>true</value>          <description/>        </booleanParameter>        <listParameter>          <name>filenames</name>          <description/>          <value>/opt/devdata/murex/interfaces/cdw/BootstrappedCurve_\\d{8}_\\d+_\\d{14}.txt</value>          <value>/opt/devdata/xenoTs/CDW/OUT//BbgMxCurveMapping_\\d{8}_\\d{14}.txt</value>          <value>/opt/devdata/murex/interfaces/cdw/Events_\\d{8}_\\d+_\\d{14}.txt</value>          <value>/opt/devdata/murex/interfaces/cdw/TradePosition_\\d{8}_\\d+_\\d{14}.txt</value>        </listParameter>        <booleanParameter>          <name>sortAscending</name>          <value>true</value>          <description/>        </booleanParameter>        <stringParameter>          <name>renameFileSuffix</name>          <value/>          <description/>        </stringParameter>        <stringParameter>          <name>encoding</name>          <value>UTF-8</value>          <description/>        </stringParameter>        <booleanParameter>          <name>includeTrailer</name>          <value>true</value>          <description/>        </booleanParameter>        <booleanParameter>          <name>eager</name>          <value>false</value>          <description/>        </booleanParameter>        <booleanParameter>          <name>sortByModifiedDateTime</name>          <value>true</value>          <description/>        </booleanParameter>        <stringParameter>          <name>cronExpression</name>          <value>0/5 * * * * ?</value>          <description/>        </stringParameter>      </parameters>    </componentConfiguration>    <componentConfiguration>      <id>cdw-eod-fileArchive</id>      <description/>      <parameters>        <booleanParameter>          <name>returnTargetFile</name>          <value>false</value>          <description/>        </booleanParameter>        <booleanParameter>          <name>atomicMove</name>          <value>true</value>          <description/>        </booleanParameter>        <booleanParameter>          <name>copyAttributes</name>          <value>false</value>          <description/>        </booleanParameter>        <booleanParameter>          <name>replaceExisting</name>          <value>true</value>          <description/>        </booleanParameter>        <stringParameter>          <name>targetDirectory</name>          <value>/opt/devdata/murex/interfaces/archive</value>          <description/>        </stringParameter>      </parameters>    </componentConfiguration>    <componentConfiguration>      <id>cdw-eod-mongoProducer</id>      <description/>      <parameters>        <integerParameter>          <name>heartbeatSocketTimeout</name>          <value/>          <description/>        </integerParameter>        <integerParameter>          <name>heartbeatConnectTimeout</name>          <value/>          <description/>        </integerParameter>        <booleanParameter>          <name>cursorFinalizerEnabled</name>          <value>true</value>          <description/>        </booleanParameter>        <integerParameter>          <name>connectionsPerHost</name>          <value/>          <description/>        </integerParameter>        <booleanParameter>          <name>alwaysUseMBeans</name>          <value>false</value>          <description/>        </booleanParameter>        <integerParameter>          <name>minHeartbeatFrequency</name>          <value/>          <description/>        </integerParameter>        <stringParameter>          <name>password</name>          <value>XKoqnNwLaH3w4FWcUjaT</value>          <description/>        </stringParameter>        <integerParameter>          <name>maxConnectionLifeTime</name>          <value/>          <description/>        </integerParameter>        <listParameter>          <name>connectionUrls</name>          <description/>          <value>svc-mng01-dev:60200</value>        </listParameter>        <stringParameter>          <name>requiredReplicaSetName</name>          <value/>          <description/>        </stringParameter>        <stringParameter>          <name>username</name>          <value>svc_cdw_dev_rw</value>          <description/>        </stringParameter>        <mapParameter>          <name>collectionNames</name>          <description/>          <item>            <name>eodBootstrapCurve</name>            <value>rawEODBootstrapCurve</value>          </item>          <item>            <name>positionFlatRawLatest</name>            <value>positionFlatRawLatest</value>          </item>          <item>            <name>consolidatedTradeTradeLeg</name>            <value>consolidatedFlatTradeSnapshot</value>          </item>          <item>            <name>tradeGlossFlatRawLatest</name>            <value>tradeGlossFlatRawLatest</value>          </item>          <item>            <name>tradeLastKnownVersion</name>            <value>tradeLastKnownVersion</value>          </item>          <item>            <name>fpmlTradeEodLatest</name>            <value>fpmlTradeEodLatest</value>          </item>          <item>            <name>tradeEvent</name>            <value>tradeEventRawLatest</value>          </item>          <item>            <name>fpmlTradeEodVersion</name>            <value>fpmlTradeEodVersion</value>          </item>          <item>            <name>eodCashflow</name>            <value>rawEODCashflow</value>          </item>          <item>            <name>bes</name>            <value>dailyBesEvents</value>          </item>          <item>            <name>batches</name>            <value>batches</value>          </item>          <item>            <name>eodGlPosting</name>            <value>rawEODGlPosting</value>          </item>          <item>            <name>fpmlPositionEodLatest</name>            <value>fpmlPositionEodLatest</value>          </item>        </mapParameter>        <integerParameter>          <name>socketTimeout</name>          <value/>          <description/>        </integerParameter>        <stringParameter>          <name>description</name>          <value/>          <description/>        </stringParameter>        <integerParameter>          <name>threadsAllowedToBlockForConnectionMultiplier</name>          <value/>          <description/>        </integerParameter>        <booleanParameter>          <name>legacyDefaults</name>          <value>false</value>          <description/>        </booleanParameter>        <integerParameter>          <name>heartbeatFrequency</name>          <value/>          <description/>        </integerParameter>        <booleanParameter>          <name>authenticated</name>          <value>true</value>          <description/>        </booleanParameter>        <integerParameter>          <name>localThreshold</name>          <value/>          <description/>        </integerParameter>        <integerParameter>          <name>maxWaitTime</name>          <value/>          <description/>        </integerParameter>        <stringParameter>          <name>databaseName</name>          <value>CDW</value>          <description/>        </stringParameter>        <booleanParameter>          <name>socketKeepAlive</name>          <value>false</value>          <description/>        </booleanParameter>        <integerParameter>          <name>maxConnectionIdleTime</name>          <value/>          <description/>        </integerParameter>        <integerParameter>          <name>connectionTimeout</name>          <value/>          <description/>        </integerParameter>        <integerParameter>          <name>minConnectionsPerHost</name>          <value/>          <description/>        </integerParameter>      </parameters>    </componentConfiguration>    <componentConfiguration>      <id>cdw-eod-cacheBroker</id>      <description/>      <parameters>        <integerParameter>          <name>heartbeatSocketTimeout</name>          <value/>          <description/>        </integerParameter>        <integerParameter>          <name>heartbeatConnectTimeout</name>          <value/>          <description/>        </integerParameter>        <booleanParameter>          <name>cursorFinalizerEnabled</name>          <value>false</value>          <description/>        </booleanParameter>        <integerParameter>          <name>connectionsPerHost</name>          <value/>          <description/>        </integerParameter>        <booleanParameter>          <name>alwaysUseMBeans</name>          <value>false</value>          <description/>        </booleanParameter>        <integerParameter>          <name>minHeartbeatFrequency</name>          <value/>          <description/>        </integerParameter>        <stringParameter>          <name>password</name>          <value>XKoqnNwLaH3w4FWcUjaT</value>          <description/>        </stringParameter>        <integerParameter>          <name>maxConnectionLifeTime</name>          <value/>          <description/>        </integerParameter>        <listParameter>          <name>connectionUrls</name>          <description/>          <value>svc-mng01-dev:60200</value>        </listParameter>        <stringParameter>          <name>requiredReplicaSetName</name>          <value/>          <description/>        </stringParameter>        <stringParameter>          <name>username</name>          <value>svc_cdw_dev_rw</value>          <description/>        </stringParameter>        <mapParameter>          <name>collectionNames</name>          <description/>          <item>            <name>cashflowCache</name>            <value>esbCashflowCache</value>          </item>          <item>            <name>bootstrappedCurveCache</name>            <value>esbBootstrappedCurveCache</value>          </item>          <item>            <name>xenomorphCurveCache</name>            <value>esbXenomorphCurveCache</value>          </item>          <item>            <name>tradeCashCache</name>            <value>esbTradeCashCache</value>          </item>          <item>            <name>tradeLegCache</name>            <value>esbTradeLegCache</value>          </item>          <item>            <name>tradeCache</name>            <value>esbTradeCache</value>          </item>        </mapParameter>        <integerParameter>          <name>socketTimeout</name>          <value/>          <description/>        </integerParameter>        <stringParameter>          <name>description</name>          <value/>          <description/>        </stringParameter>        <integerParameter>          <name>threadsAllowedToBlockForConnectionMultiplier</name>          <value/>          <description/>        </integerParameter>        <booleanParameter>          <name>legacyDefaults</name>          <value>false</value>          <description/>        </booleanParameter>        <integerParameter>          <name>heartbeatFrequency</name>          <value/>          <description/>        </integerParameter>        <booleanParameter>          <name>authenticated</name>          <value>true</value>          <description/>        </booleanParameter>        <integerParameter>          <name>localThreshold</name>          <value/>          <description/>        </integerParameter>        <integerParameter>          <name>maxWaitTime</name>          <value/>          <description/>        </integerParameter>        <stringParameter>          <name>databaseName</name>          <value>CDW</value>          <description/>        </stringParameter>        <booleanParameter>          <name>socketKeepAlive</name>          <value>false</value>          <description/>        </booleanParameter>        <integerParameter>          <name>maxConnectionIdleTime</name>          <value/>          <description/>        </integerParameter>        <integerParameter>          <name>connectionTimeout</name>          <value/>          <description/>        </integerParameter>        <integerParameter>          <name>minConnectionsPerHost</name>          <value/>          <description/>        </integerParameter>      </parameters>    </componentConfiguration>    <componentConfiguration>      <id>cdw-eod-prepopulateIlsIdentifierCacheBroker</id>      <description/>      <parameters>        <stringParameter>          <name>ilsBaseUrl</name>          <value>http://svc-eai01d:8080/ils-tradeIdentifierPublish/rest/lookup,http://svc-eai02d:8080/ils-tradeIdentifierPublish/rest/lookup</value>          <description/>        </stringParameter>        <integerParameter>          <name>numberOfThreads</name>          <value>1</value>          <description/>        </integerParameter>        <mapParameter>          <name>columnNamesForFileTypeMap</name>          <description/>          <item>            <name>Events</name>            <value>MurexRootContractID</value>          </item>        </mapParameter>      </parameters>    </componentConfiguration>    <componentConfiguration>      <id>cdw-eod-prepopulateSecurityIdentifierCacheBroker</id>      <description/>      <parameters>        <stringParameter>          <name>baseUrl</name>          <value>http://cdwi:3030</value>          <description/>        </stringParameter>        <stringParameter>          <name>futureProductTypologyIds</name>          <value>1251,1253</value>          <description/>        </stringParameter>        <integerParameter>          <name>batchSize</name>          <value>250</value>          <description/>        </integerParameter>        <listParameter>          <name>fileTypesToConsider</name>          <description/>          <value>Trade</value>          <value>TradeCash</value>          <value>TradePosition</value>        </listParameter>      </parameters>    </componentConfiguration>    <componentConfiguration>      <id>cdw-eod-prepopulateCounterpartyIdentifierCacheBroker</id>      <description/>      <parameters>        <stringParameter>          <name>baseUrl</name>          <value>http://cdwi:3030</value>          <description/>        </stringParameter>        <integerParameter>          <name>batchSize</name>          <value>250</value>          <description/>        </integerParameter>        <listParameter>          <name>fileTypesToConsider</name>          <description/>          <value>Trade</value>          <value>TradeCash</value>        </listParameter>      </parameters>    </componentConfiguration>    <componentConfiguration>      <id>cdw-eod-curve-readyForPublish</id>      <description/>      <parameters>        <booleanParameter>          <name>applyFilter</name>          <value>true</value>          <description/>        </booleanParameter>        <booleanParameter>          <name>logFiltered</name>          <value>false</value>          <description/>        </booleanParameter>      </parameters>    </componentConfiguration>    <componentConfiguration>      <id>cdw-eod-positionReportErrorsToDashboard</id>      <description/>      <parameters>        <stringParameter>          <name>flowElementName</name>          <value>Report Position Errors to Dashboard</value>          <description/>        </stringParameter>        <listParameter>          <name>configurationExpressionList</name>          <description/>        </listParameter>        <mapParameter>          <name>excludeErrorsMap</name>          <description/>        </mapParameter>        <listParameter>          <name>errorExpressionList</name>          <description/>          <value>@errorCachingService.getAndRemoveErrorsAsCsvString('CDW-EOD module unable to locate following POSITION SECURITY identifiers, requires URGENT investigation :-', 'POSITION_SECURITY')</value>        </listParameter>      </parameters>    </componentConfiguration>    <componentConfiguration>      <id>cdw-eod-positionBesEventProducer</id>      <description/>      <parameters>        <booleanParameter>          <name>deliveryPersistent</name>          <value>true</value>          <description/>        </booleanParameter>        <booleanParameter>          <name>messageTimestampEnabled</name>          <value>false</value>          <description/>        </booleanParameter>        <mapParameter>          <name>destinationJndiProperties</name>          <description/>          <item>            <name>java.naming.factory.initial</name>            <value>org.jboss.naming.remote.client.InitialContextFactory</value>          </item>          <item>            <name>java.naming.provider.url</name>            <value>remote://svc-bdmmessaging01d:4447</value>          </item>          <item>            <name>java.naming.factory.url.pkgs</name>            <value>java.naming.factory.url.pkgs</value>          </item>        </mapParameter>        <stringParameter>          <name>destinationJndiName</name>          <value>/jms/queue/esb.cdw.eod.bes</value>          <description/>        </stringParameter>        <booleanParameter>          <name>explicitQosEnabled</name>          <value>false</value>          <description/>        </booleanParameter>        <integerParameter>          <name>deliveryMode</name>          <value/>          <description/>        </integerParameter>        <longParameter>          <name>receiveTimeout</name>          <value/>          <description/>        </longParameter>        <mapParameter>          <name>connectionFactoryJndiProperties</name>          <description/>        </mapParameter>        <booleanParameter>          <name>pubSubNoLocal</name>          <value>false</value>          <description/>        </booleanParameter>        <booleanParameter>          <name>pubSubDomain</name>          <value>false</value>          <description/>        </booleanParameter>        <booleanParameter>          <name>messageIdEnabled</name>          <value>true</value>          <description/>        </booleanParameter>        <booleanParameter>          <name>sessionTransacted</name>          <value>true</value>          <description/>        </booleanParameter>        <stringParameter>          <name>connectionFactoryPassword</name>          <value/>          <description/>        </stringParameter>        <longParameter>          <name>timeToLive</name>          <value/>          <description/>        </longParameter>        <stringParameter>          <name>connectionFactoryUsername</name>          <value/>          <description/>        </stringParameter>        <integerParameter>          <name>priority</name>          <value/>          <description/>        </integerParameter>        <integerParameter>          <name>sessionAcknowledgeMode</name>          <value/>          <description/>        </integerParameter>        <stringParameter>          <name>sessionAcknowledgeModeName</name>          <value/>          <description/>        </stringParameter>        <stringParameter>          <name>connectionFactoryName</name>          <value>java:/BdmJmsXA</value>          <description/>        </stringParameter>      </parameters>    </componentConfiguration>    <componentConfiguration>      <id>cdw-eod-partiallyPopulatedCurve</id>      <description/>      <parameters>        <booleanParameter>          <name>applyFilter</name>          <value>true</value>          <description/>        </booleanParameter>        <booleanParameter>          <name>logFiltered</name>          <value>false</value>          <description/>        </booleanParameter>      </parameters>    </componentConfiguration>    <componentConfiguration>      <id>cdw-eod-position-cdwSecurityService</id>      <description/>      <parameters>        <stringParameter>          <name>baseUrl</name>          <value/>          <description/>        </stringParameter>        <stringParameter>          <name>futureProductTypologyIds</name>          <value>1251,1253</value>          <description/>        </stringParameter>        <booleanParameter>          <name>errorOnFailedLookup</name>          <value>false</value>          <description/>        </booleanParameter>      </parameters>    </componentConfiguration>    <componentConfiguration>      <id>cdw-eod-cdwCounterpartyService</id>      <description/>      <parameters>        <stringParameter>          <name>baseUrl</name>          <value>http://cdwi:3030/identifier/counterparties/ACCOUNTID/</value>          <description>http://cdwd/identifier/counterparties/ACCOUNTID/</description>        </stringParameter>        <booleanParameter>          <name>errorOnFailedLookup</name>          <value>false</value>          <description/>        </booleanParameter>      </parameters>    </componentConfiguration>    <componentConfiguration>      <id>cdw-eodfpml-trade-fpmlconverter</id>      <description/>      <parameters>        <stringParameter>          <name>mcsClientName</name>          <value>CMI2</value>          <description/>        </stringParameter>        <stringParameter>          <name>mcsSourceContext</name>          <value>Murex OTC</value>          <description/>        </stringParameter>        <stringParameter>          <name>mcsTargetContext</name>          <value>MHI</value>          <description/>        </stringParameter>        <stringParameter>          <name>mcsProductType</name>          <value>ProductType</value>          <description/>        </stringParameter>        <stringParameter>          <name>mcsDayCountFraction</name>          <value>DayCountFraction</value>          <description/>        </stringParameter>      </parameters>    </componentConfiguration>    <componentConfiguration>      <id>cdw-eod-positionVersionMongoProducer</id>      <description/>      <parameters>        <integerParameter>          <name>heartbeatSocketTimeout</name>          <value/>          <description/>        </integerParameter>        <integerParameter>          <name>heartbeatConnectTimeout</name>          <value/>          <description/>        </integerParameter>        <booleanParameter>          <name>cursorFinalizerEnabled</name>          <value>true</value>          <description/>        </booleanParameter>        <integerParameter>          <name>connectionsPerHost</name>          <value/>          <description/>        </integerParameter>        <booleanParameter>          <name>alwaysUseMBeans</name>          <value>false</value>          <description/>        </booleanParameter>        <integerParameter>          <name>minHeartbeatFrequency</name>          <value/>          <description/>        </integerParameter>        <stringParameter>          <name>password</name>          <value>XKoqnNwLaH3w4FWcUjaT</value>          <description/>        </stringParameter>        <integerParameter>          <name>maxConnectionLifeTime</name>          <value/>          <description/>        </integerParameter>        <listParameter>          <name>connectionUrls</name>          <description/>          <value>svc-mng01-dev:60200</value>        </listParameter>        <stringParameter>          <name>requiredReplicaSetName</name>          <value/>          <description/>        </stringParameter>        <stringParameter>          <name>username</name>          <value>svc_cdw_dev_rw</value>          <description/>        </stringParameter>        <mapParameter>          <name>collectionNames</name>          <description/>          <item>            <name>fpmlPositionEodVersion</name>            <value>fpmlPositionEodVersion</value>          </item>          <item>            <name>positionFlatRawVersion</name>            <value>positionFlatRawVersion</value>          </item>        </mapParameter>        <integerParameter>          <name>socketTimeout</name>          <value/>          <description/>        </integerParameter>        <stringParameter>          <name>description</name>          <value/>          <description/>        </stringParameter>        <integerParameter>          <name>threadsAllowedToBlockForConnectionMultiplier</name>          <value/>          <description/>        </integerParameter>        <booleanParameter>          <name>legacyDefaults</name>          <value>false</value>          <description/>        </booleanParameter>        <integerParameter>          <name>heartbeatFrequency</name>          <value/>          <description/>        </integerParameter>        <booleanParameter>          <name>authenticated</name>          <value>true</value>          <description/>        </booleanParameter>        <integerParameter>          <name>localThreshold</name>          <value/>          <description/>        </integerParameter>        <integerParameter>          <name>maxWaitTime</name>          <value/>          <description/>        </integerParameter>        <stringParameter>          <name>databaseName</name>          <value>CDWVersion</value>          <description/>        </stringParameter>        <booleanParameter>          <name>socketKeepAlive</name>          <value>false</value>          <description/>        </booleanParameter>        <integerParameter>          <name>maxConnectionIdleTime</name>          <value/>          <description/>        </integerParameter>        <integerParameter>          <name>connectionTimeout</name>          <value/>          <description/>        </integerParameter>        <integerParameter>          <name>minConnectionsPerHost</name>          <value/>          <description/>        </integerParameter>      </parameters>    </componentConfiguration>  </componentConfigurations></flowConfiguration>";

        response = webTarget.request().put(Entity.entity(badFlow
                , MediaType.APPLICATION_OCTET_STREAM));

        responseMessage = response.readEntity(String.class);

        System.out.println(responseMessage);

        url = "http://svc-ikasand:8080/ikasan-dashboard/rest/configuration/module/cdw-eod";

        webTarget = client.target(url);

        response = webTarget.request().get();

        response.bufferEntity();

        responseMessage = response.readEntity(String.class);

        System.out.println(responseMessage);

        url = "http://svc-ikasand:8080/ikasan-dashboard/rest/configuration/update/cdw-eod";

        webTarget = client.target(url);
        response = webTarget.request().put(Entity.entity(responseMessage
                , MediaType.APPLICATION_OCTET_STREAM));

        responseMessage = response.readEntity(String.class);

        System.out.println(responseMessage);

        String badModule = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><moduleConfiguration  xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:noNamespaceSchemaLocation=\"http://svc-ikasand:8080/ikasan-dashboard/static/org/ikasan/dashboard/moduleConfiguration.xsd\">  <name>cdw-eod</name>  <flowConfigurations>    <flowConfiguration>      <name>EOD Non Trade File Serial Load Flow</name>      <componentConfigurations>        <componentConfiguration>          <id>cdw-eod-fileConsumer</id>          <description/>          <parameters>            <integerParameter>              <name>directoryDepth</name>              <value>bad</value>              <description/>            </integerParameter>            <booleanParameter>              <name>ignoreMisfire</name>              <value>true</value>              <description/>            </booleanParameter>            <booleanParameter>              <name>includeHeader</name>              <value>true</value>              <description/>            </booleanParameter>            <listParameter>              <name>filenames</name>              <description/>              <value>/opt/devdata/murex/interfaces/cdw/BootstrappedCurve_\\d{8}_\\d+_\\d{14}.txt</value>              <value>/opt/devdata/xenoTs/CDW/OUT//BbgMxCurveMapping_\\d{8}_\\d{14}.txt</value>              <value>/opt/devdata/murex/interfaces/cdw/Events_\\d{8}_\\d+_\\d{14}.txt</value>              <value>/opt/devdata/murex/interfaces/cdw/TradePosition_\\d{8}_\\d+_\\d{14}.txt</value>            </listParameter>            <booleanParameter>              <name>sortAscending</name>              <value>true</value>              <description/>            </booleanParameter>            <stringParameter>              <name>renameFileSuffix</name>              <value/>              <description/>            </stringParameter>            <stringParameter>              <name>encoding</name>              <value>UTF-8</value>              <description/>            </stringParameter>            <booleanParameter>              <name>includeTrailer</name>              <value>true</value>              <description/>            </booleanParameter>            <booleanParameter>              <name>eager</name>              <value>false</value>              <description/>            </booleanParameter>            <booleanParameter>              <name>sortByModifiedDateTime</name>              <value>true</value>              <description/>            </booleanParameter>            <stringParameter>              <name>cronExpression</name>              <value>0/5 * * * * ?</value>              <description/>            </stringParameter>          </parameters>        </componentConfiguration>       </componentConfigurations>    </flowConfiguration>  </flowConfigurations></moduleConfiguration>";

        response = webTarget.request().put(Entity.entity(badModule
                , MediaType.APPLICATION_OCTET_STREAM));

        responseMessage = response.readEntity(String.class);

        System.out.println(responseMessage);

        url = "http://svc-ikasand:8080/ikasan-dashboard/rest/configuration/component/cdw-eod/EOD%20Non%20Trade%20File%20Serial%20Load%20Flow/Scheduled%20Consumer";

        webTarget = client.target(url);

        response = webTarget.request().get();

        response.bufferEntity();

        responseMessage = response.readEntity(String.class);

        System.out.println(responseMessage);

        url = "http://svc-ikasand:8080/ikasan-dashboard/rest/configuration/update/cdw-eod/EOD%20Non%20Trade%20File%20Serial%20Load%20Flow/Scheduled%20Consumer";

        webTarget = client.target(url);
        response = webTarget.request().put(Entity.entity(responseMessage
                , MediaType.APPLICATION_OCTET_STREAM));

        responseMessage = response.readEntity(String.class);

        System.out.println(responseMessage);

        String componentBad = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><componentConfiguration  xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:noNamespaceSchemaLocation=\"http://svc-ikasand:8080/ikasan-dashboard/static/org/ikasan/dashboard/componentConfiguration.xsd\">  <id>cdw-eod-fileConsumer</id>  <description/>  <parameters>    <integerParameter>      <name>directoryDepth</name>      <value>1</value>      <description/>    </integerParameter>    <booleanParameter>      <name>ignoreMisfire</name>      <value>bad</value>      <description/>    </booleanParameter>    <booleanParameter>      <name>includeHeader</name>      <value>true</value>      <description/>    </booleanParameter>    <listParameter>      <name>filenames</name>      <description/>      <value>/opt/devdata/murex/interfaces/cdw/BootstrappedCurve_\\d{8}_\\d+_\\d{14}.txt</value>      <value>/opt/devdata/xenoTs/CDW/OUT//BbgMxCurveMapping_\\d{8}_\\d{14}.txt</value>      <value>/opt/devdata/murex/interfaces/cdw/Events_\\d{8}_\\d+_\\d{14}.txt</value>      <value>/opt/devdata/murex/interfaces/cdw/TradePosition_\\d{8}_\\d+_\\d{14}.txt</value>    </listParameter>    <booleanParameter>      <name>sortAscending</name>      <value>true</value>      <description/>    </booleanParameter>    <stringParameter>      <name>renameFileSuffix</name>      <value/>      <description/>    </stringParameter>    <stringParameter>      <name>encoding</name>      <value>UTF-8</value>      <description/>    </stringParameter>    <booleanParameter>      <name>includeTrailer</name>      <value>true</value>      <description/>    </booleanParameter>    <booleanParameter>      <name>eager</name>      <value>false</value>      <description/>    </booleanParameter>    <booleanParameter>      <name>sortByModifiedDateTime</name>      <value>true</value>      <description/>    </booleanParameter>    <stringParameter>      <name>cronExpression</name>      <value>0/5 * * * * ?</value>      <description/>    </stringParameter>  </parameters></componentConfiguration>";

        webTarget = client.target(url);
        response = webTarget.request().put(Entity.entity(componentBad
                , MediaType.APPLICATION_OCTET_STREAM));

        responseMessage = response.readEntity(String.class);

        System.out.println(responseMessage);

        url = "http://svc-ikasand:8080/ikasan-dashboard/rest/configuration/component/cdw-eod/EOD%20Non%20Trade%20File%20Serial%20Load%20Flow/bad-component";

        webTarget = client.target(url);

        response = webTarget.request().get();

        response.bufferEntity();

        responseMessage = response.readEntity(String.class);

        System.out.println("Bad component: " + responseMessage);

        url = "http://svc-ikasand:8080/ikasan-dashboard/rest/configuration/flow/cdw-eod/bad-flow";

        webTarget = client.target(url);

        response = webTarget.request().get();

        response.bufferEntity();

        responseMessage = response.readEntity(String.class);

        System.out.println("Bad flow: " + responseMessage);

        url = "http://svc-ikasand:8080/ikasan-dashboard/rest/configuration/module/bad-module";

        webTarget = client.target(url);

        response = webTarget.request().get();

        response.bufferEntity();

        responseMessage = response.readEntity(String.class);

        System.out.println("Bad module: " + responseMessage);
    }
    
}
