package org.ikasan.rest.module;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.ikasan.configurationService.model.*;
import org.ikasan.rest.module.util.UserUtil;
import org.ikasan.spec.configuration.Configuration;
import org.ikasan.spec.configuration.ConfigurationManagement;
import org.ikasan.spec.configuration.ConfiguredResource;
import org.ikasan.spec.flow.Flow;
import org.ikasan.spec.flow.FlowElement;
import org.ikasan.spec.metadata.model.ConfigurationMetaData;
import org.ikasan.spec.metadata.model.ConfigurationMetaDataExtractor;
import org.ikasan.spec.metadata.model.ConfigurationParameterMetaData;
import org.ikasan.spec.module.Module;
import org.ikasan.spec.module.ModuleService;
import org.ikasan.spec.systemevent.SystemEventService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Configuration application implementing the REST contract
 */
@RequestMapping("/rest/configuration")
@RestController
public class ConfigurationApplication
{
    private static Logger logger = LoggerFactory.getLogger(ConfigurationApplication.class);

    @Autowired
    private ConfigurationManagement<ConfiguredResource, Configuration> configurationManagement;

    @Autowired
    private ConfigurationMetaDataExtractor<ConfigurationMetaData> configurationMetaDataExtractor;

    @Autowired
    private ModuleService moduleService;

    @Autowired
    private SystemEventService systemEventService;

    private ObjectMapper mapper;

    /**
     * Constructor for ConfigurationApplication class.
     * Initializes the ObjectMapper with custom configuration settings.
     */
    public ConfigurationApplication() {
        this.mapper = new ObjectMapper();
        this.mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        SimpleModule m = new SimpleModule();
        this.mapper.registerModule(m);
    }

    /**
     * Method to retrieve the configuration of flows.
     *
     * This method fetches the configuration metadata of flows from the module specified in the service.
     * The configuration metadata contains information about the configured resources of the flow.
     *
     * Requires 'ALL' or 'WebServiceAdmin' authority.
     *
     * @return ResponseEntity containing the list of ConfigurationMetaData for the configured resources of the flow
     */
    @RequestMapping(method = RequestMethod.GET,
                    value = "/flows")
    @PreAuthorize("hasAnyAuthority('ALL','WebServiceAdmin')")
    public ResponseEntity getFlowsConfiguration()
    {
        Module<Flow> module = moduleService.getModules().get(0);
        List<ConfigurationMetaData> configuredResources = configurationMetaDataExtractor.getFlowsConfiguration(module);
        return new ResponseEntity(configuredResources, HttpStatus.OK);
    }

    /**
     * Method to retrieve the configuration of the module.
     *
     * This method fetches the configuration metadata of the module.
     * If the module is an instance of ConfiguredResource, it extracts the configuration using ConfigurationMetaDataExtractor.
     *
     * Requires 'ALL' or 'WebServiceAdmin' authority for access.
     *
     * @return ResponseEntity containing the ConfigurationMetaData object for the configured resources of the module
     * if found, HttpStatus.NOT_FOUND otherwise.
     */
    @RequestMapping(method = RequestMethod.GET,
        value = "/module")
    @PreAuthorize("hasAnyAuthority('ALL','WebServiceAdmin')")
    public ResponseEntity getModuleConfiguration()
    {
        Module<Flow> module = moduleService.getModules().get(0);

        if(module instanceof ConfiguredResource resource) {
            ConfigurationMetaData configuredResource = configurationMetaDataExtractor.getConfiguration(resource);
            return new ResponseEntity(configuredResource, HttpStatus.OK);
        }

        return new ResponseEntity(HttpStatus.NOT_FOUND);
    }

    /**
     * Retrieves the configuration of a specific flow for a given module.
     *
     * @param moduleName The name of the module containing the flow.
     * @param flowName The name of the flow for which configuration is to be retrieved.
     * @return ResponseEntity containing the configuration metadata of the specified flow.
     */
    @RequestMapping(method = RequestMethod.GET,
                    value = "/{moduleName}/{flowName}/flow",
                    produces = { "application/json" })
    @PreAuthorize("hasAnyAuthority('ALL','WebServiceAdmin')")
    public ResponseEntity getFlowsConfiguration(@PathVariable("moduleName") String moduleName,
                                                @PathVariable("flowName") String flowName)
    {
        Flow flow = (Flow) moduleService.getModule(moduleName).getFlow(flowName);
        ConfigurationMetaData configuredResources = configurationMetaDataExtractor.getFlowConfiguration(flow);
        return new ResponseEntity(configuredResources, HttpStatus.OK);
    }

    /**
     * Method to retrieve the configuration of invokers.
     *
     * This method fetches the configuration metadata of invokers from the specified module using ConfigurationMetaDataExtractor.
     * The configuration metadata contains information about the configured resources of the invokers.
     *
     * Requires 'ALL' or 'WebServiceAdmin' authority for access.
     *
     * @return ResponseEntity containing the list of ConfigurationMetaData for the configured resources of the invokers.
     */
    @RequestMapping(method = RequestMethod.GET,
                    value = "/invokers",
                    produces = { "application/json" })
    @PreAuthorize("hasAnyAuthority('ALL','WebServiceAdmin')")
    public ResponseEntity getInvokersConfiguration()
    {
        Module<Flow> module = moduleService.getModules().get(0);
        List<ConfigurationMetaData> configuredResources = configurationMetaDataExtractor
            .getInvokersConfiguration(module);
        return new ResponseEntity(configuredResources, HttpStatus.OK);
    }

    /**
     * Method to retrieve the configuration of components.
     *
     * Fetches the configuration metadata of components from the specified module.
     * Configured resources contain information about the configured components.
     *
     * Requires 'ALL' or 'WebServiceAdmin' authority for access.
     *
     * @return ResponseEntity containing a list of ConfigurationMetaData for the configured components,
     *         with HTTP status OK if successful.
     */
    @RequestMapping(method = RequestMethod.GET,
                    value = "/components",
                    produces = { "application/json" })
    @PreAuthorize("hasAnyAuthority('ALL','WebServiceAdmin')")
    public ResponseEntity getComponentsConfiguration()
    {
        Module<Flow> module = moduleService.getModules().get(0);
        List<ConfigurationMetaData> configuredResources = configurationMetaDataExtractor
            .getComponentsConfiguration(module);
        return new ResponseEntity(configuredResources, HttpStatus.OK);
    }

    /**
     * Retrieves the configuration metadata of invokers.
     *
     * @param moduleName The name of the module containing the invoker configuration.
     * @param flowName The name of the flow for which invoker configuration is to be retrieved.
     * @return ResponseEntity containing the list of ConfigurationMetaData for the configured resources of the invokers.
     */
    @RequestMapping(method = RequestMethod.GET,
                    value = "/{moduleName}/{flowName}/invokers",
                    produces = { "application/json" })
    @PreAuthorize("hasAnyAuthority('ALL','WebServiceAdmin')")
    public ResponseEntity getInvokersConfiguration(@PathVariable("moduleName") String moduleName,
                                                   @PathVariable("flowName") String flowName)
    {
        Flow flow = (Flow) moduleService.getModule(moduleName).getFlow(flowName);
        List<ConfigurationMetaData> configuredResources = configurationMetaDataExtractor.getInvokersConfiguration(flow);
        return new ResponseEntity(configuredResources, HttpStatus.OK);
    }

    /**
     * Retrieves the configuration of a specific invoker for a given module, flow, and component.
     *
     * @param moduleName The name of the module.
     * @param flowName The name of the flow.
     * @param componentname The name of the component representing the invoker.
     * @return ResponseEntity containing the ConfigurationMetaData object for the configured resource of the invoker.
     */
    @RequestMapping(method = RequestMethod.GET,
        value = "/{moduleName}/{flowName}/{componentName}/invoker",
        produces = { "application/json" })
    @PreAuthorize("hasAnyAuthority('ALL','WebServiceAdmin')")
    public ResponseEntity getInvokerConfiguration(@PathVariable("moduleName") String moduleName,
                                                   @PathVariable("flowName") String flowName,
                                                   @PathVariable("componentName") String componentname)
    {
        Flow flow = (Flow) moduleService.getModule(moduleName).getFlow(flowName);
        FlowElement flowElement = flow.getFlowElement(componentname);

        ConfigurationMetaData configuredResource = configurationMetaDataExtractor
            .getConfiguration((ConfiguredResource)flowElement.getFlowElementInvoker());

        return new ResponseEntity(configuredResource, HttpStatus.OK);
    }

    /**
     * Retrieves the configuration metadata of components.
     *
     * @param moduleName The name of the module containing the components.
     * @param flowName The name of the flow for which component configuration is to be retrieved.
     * @return ResponseEntity containing a list of ConfigurationMetaData for the configured components,
     *         with HTTP status OK if successful.
     */
    @RequestMapping(method = RequestMethod.GET,
                    value = "/{moduleName}/{flowName}/components",
                    produces = { "application/json" })
    @PreAuthorize("hasAnyAuthority('ALL','WebServiceAdmin')")
    public ResponseEntity getComponentsConfiguration(@PathVariable("moduleName") String moduleName,
                                                     @PathVariable("flowName") String flowName)
    {
        Flow flow = (Flow) moduleService.getModule(moduleName).getFlow(flowName);
        List<ConfigurationMetaData> configuredResources = configurationMetaDataExtractor
            .getComponentsConfiguration(flow);
        return new ResponseEntity(configuredResources, HttpStatus.OK);
    }

    /**
     * Retrieves the configuration of a specific component within a specified flow and module.
     *
     * @param moduleName The name of the module containing the component.
     * @param flowName The name of the flow containing the component.
     * @param componentName The name of the specific component for which configuration is to be retrieved.
     * @return ResponseEntity containing the ConfigurationMetaData object for the configured resource of the component,
     * with HTTP status OK.
     */
    @RequestMapping(method = RequestMethod.GET,
        value = "/{moduleName}/{flowName}/{componentName}",
        produces = { "application/json" })
    @PreAuthorize("hasAnyAuthority('ALL','WebServiceAdmin')")
    public ResponseEntity getComponentConfiguration(@PathVariable("moduleName") String moduleName,
                                                    @PathVariable("flowName") String flowName,
                                                    @PathVariable("componentName") String componentName)
    {
        Flow flow = (Flow) moduleService.getModule(moduleName).getFlow(flowName);
        FlowElement flowElement = flow.getFlowElement(componentName);
        ConfigurationMetaData configuredResource = configurationMetaDataExtractor
            .getConfiguration((ConfiguredResource)flowElement.getFlowComponent());
        return new ResponseEntity(configuredResource, HttpStatus.OK);
    }

    /**
     * Updates the configuration with the provided configuration metadata and request headers.
     *
     * @param configurationMetaData The configuration metadata to update
     * @param headers The request headers containing the username for logging
     * @return ResponseEntity with HTTP status OK upon successful update
     */
    @RequestMapping(method = RequestMethod.PUT)
    @PreAuthorize("hasAnyAuthority('ALL','WebServiceAdmin')")
    public ResponseEntity putConfiguration(@RequestBody ConfigurationMetaData configurationMetaData,
                                           @RequestHeader HttpHeaders headers)
    {
        Configuration configuration = convert(configurationMetaData);

        try
        {
            Configuration oldConfig = this.configurationManagement.getConfiguration(configuration.getConfigurationId());
            String oldConfigJson = null;
            if(oldConfig!=null){
                oldConfigJson = mapper.writeValueAsString(oldConfig);
            }
            String newConfigJson = mapper.writeValueAsString(configuration);
            String username = headers.getFirst("username");
            this.systemEventService.logSystemEvent(
                configuration.getConfigurationId(),
                "Configuration Updated OldConfig [%s] NewConfig [%s]".formatted(oldConfigJson, newConfigJson),
                username != null ? username : UserUtil.getUser());
        }
        catch (JsonProcessingException e)
        {
            logger.warn("Issue converting configuration to json.", e);
        }

        this.configurationManagement.saveConfiguration(configuration);

        return new ResponseEntity(HttpStatus.OK);
    }

    /**
     * Delete the configuration with the specified configuration ID.
     *
     * @param configurationId the unique identifier of the configuration to be deleted
     * @param headers the HttpHeaders containing the username for logging
     * @return ResponseEntity with HttpStatus.OK if the configuration was successfully deleted,
     *         or ResponseEntity with HttpStatus.NOT_FOUND if the configuration was not found
     */
    @RequestMapping(method = RequestMethod.DELETE,
        value = "/{configurationId}")
    @PreAuthorize("hasAnyAuthority('ALL','WebServiceAdmin')")
    public ResponseEntity deleteConfiguration(@PathVariable("configurationId") String configurationId,
                                              @RequestHeader HttpHeaders headers)
    {
        Configuration configuration = this.configurationManagement.getConfiguration(configurationId);
        if ( configuration != null )
        {
            this.configurationManagement.deleteConfiguration(configuration);
            try
            {
                String deletedConfigJson = mapper.writeValueAsString(configuration);
                String username = headers.getFirst("username");
                this.systemEventService.logSystemEvent(
                    configuration.getConfigurationId(),
                    "Configuration Deleted OldConfig [%s]".formatted(deletedConfigJson),
                    username != null ? username : UserUtil.getUser());
            }
            catch (JsonProcessingException e)
            {
                logger.warn("Issue converting configuration to json.", e);
            }
            return new ResponseEntity(HttpStatus.OK);
        }
        else
        {
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        }
    }

    /**
     * Converts the given ConfigurationMetaData object into a Configuration object.
     *
     * @param metaData the ConfigurationMetaData object to be converted
     * @return a Configuration object created from the given metaData
     */
    private Configuration convert(ConfigurationMetaData<List<ConfigurationParameterMetaData>> metaData)
    {

        List<AbstractComponentParameter> list = metaData.getParameters().stream().map(p -> convertParam(p))
                                                    .collect(Collectors.toList());

        return new DefaultConfiguration(metaData.getConfigurationId(), metaData.getDescription(), list);

    }

    /**
     * Converts the provided ConfigurationParameterMetaData object into an AbstractComponentParameter object based on its implementing class.
     *
     * @param metaData the ConfigurationParameterMetaData object to be converted
     * @return an AbstractComponentParameter object created from the given metaData
     */
    private AbstractComponentParameter convertParam(ConfigurationParameterMetaData metaData)
    {
        AbstractComponentParameter cp = null;

        if ( ConfigurationParameterMapImpl.class.getName().equals(metaData.getImplementingClass()) )
        {
            cp = new ConfigurationParameterMapImpl(metaData.getName(), (Map) metaData.getValue(),
                metaData.getDescription()
            );
        }
        else if ( ConfigurationParameterListImpl.class.getName().equals(metaData.getImplementingClass()) )
        {
            cp = new ConfigurationParameterListImpl(metaData.getName(), (List) metaData.getValue(),
                metaData.getDescription()
            );
        }
        else if ( ConfigurationParameterBooleanImpl.class.getName().equals(metaData.getImplementingClass()) )
        {
            cp = new ConfigurationParameterBooleanImpl(metaData.getName(), (Boolean) metaData.getValue(),
                metaData.getDescription()
            );
        }
        else if ( ConfigurationParameterIntegerImpl.class.getName().equals(metaData.getImplementingClass()) )
        {
            cp = new ConfigurationParameterIntegerImpl(metaData.getName(), (Integer) metaData.getValue(),
                metaData.getDescription()
            );
        }
        else if ( ConfigurationParameterLongImpl.class.getName().equals(metaData.getImplementingClass()) )
        {
            cp = new ConfigurationParameterLongImpl(metaData.getName(),
                metaData.getValue() != null ? Long.valueOf(metaData.getValue().toString()) : null, metaData.getDescription()
            );
        }
        else if ( ConfigurationParameterMaskedStringImpl.class.getName().equals(metaData.getImplementingClass()) )
        {
            cp = new ConfigurationParameterMaskedStringImpl(metaData.getName(), (String) metaData.getValue(),
                metaData.getDescription()
            );
        }
        else
        {
            cp = new ConfigurationParameterStringImpl(metaData.getName(), (String) metaData.getValue(),
                metaData.getDescription()
            );
        }

        cp.setId(metaData.getId());
        return cp;
    }
}
