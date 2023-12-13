package org.ikasan.rest.module;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.ikasan.configurationService.model.*;
import org.ikasan.rest.module.util.UserUtil;
import org.ikasan.spec.configuration.Configuration;
import org.ikasan.spec.configuration.ConfigurationManagement;
import org.ikasan.spec.configuration.ConfigurationParameter;
import org.ikasan.spec.configuration.ConfiguredResource;
import org.ikasan.spec.flow.Flow;
import org.ikasan.spec.flow.FlowElement;
import org.ikasan.spec.metadata.ConfigurationMetaData;
import org.ikasan.spec.metadata.ConfigurationMetaDataExtractor;
import org.ikasan.spec.metadata.ConfigurationParameterMetaData;
import org.ikasan.spec.module.Module;
import org.ikasan.spec.module.ModuleService;
import org.ikasan.spec.systemevent.SystemEventService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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

    public ConfigurationApplication() {
        this.mapper = new ObjectMapper();
        this.mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        SimpleModule m = new SimpleModule();
        this.mapper.registerModule(m);
    }

    @Deprecated
    @RequestMapping(method = RequestMethod.GET,
                    value = "/createConfiguration/{moduleName}/{flowName}/{componentName}",
                    produces = { "application/json" })
    @PreAuthorize("hasAnyAuthority('ALL','WebServiceAdmin')")
    public ResponseEntity createConfiguration(@PathVariable("moduleName") String moduleName,
                                              @PathVariable("flowName") String flowName,
                                              @PathVariable("componentName") String componentName)
    {
        Module<Flow> module = moduleService.getModule(moduleName);
        Flow flow = module.getFlow(flowName);
        FlowElement<?> flowElement = flow.getFlowElement(componentName);
        Configuration configuration = null;
        if ( flowElement.getFlowComponent() instanceof ConfiguredResource )
        {
            ConfiguredResource configuredResource = (ConfiguredResource) flowElement.getFlowComponent();
            configuration = this.configurationManagement.getConfiguration(configuredResource.getConfiguredResourceId());
            if ( configuration == null )
            {
                configuration = this.configurationManagement.createConfiguration(configuredResource);
                this.configurationManagement.saveConfiguration(configuration);
            }
            else
            {
                return new ResponseEntity("This configuration already exists!", HttpStatus.UNAUTHORIZED);
            }
        }
        else
        {
            return new ResponseEntity("This component is not configurable!", HttpStatus.UNAUTHORIZED);
        }
        return new ResponseEntity(configuration, HttpStatus.OK);
    }

    /**
     * TODO: work out how to get annotation security working.
     *
     * @param moduleName
     * @param flowName
     * @return
     */
    @Deprecated
    @RequestMapping(method = RequestMethod.GET,
                    value = "/createFlowElementConfiguration/{moduleName}/{flowName}/{componentName}",
                    produces = { "application/json" })
    @PreAuthorize("hasAnyAuthority('ALL','WebServiceAdmin')")
    public ResponseEntity createFlowElementConfiguration(@PathVariable("moduleName") String moduleName,
                                                         @PathVariable("flowName") String flowName,
                                                         @PathVariable("componentName") String componentName)
    {
        Module<Flow> module = moduleService.getModule(moduleName);
        Flow flow = module.getFlow(flowName);
        FlowElement<?> flowElement = flow.getFlowElement(componentName);
        Configuration configuration = null;
        if ( flowElement instanceof ConfiguredResource configuredResource )
        {
            String configurationId = moduleName + flowName + componentName + "_element";
            configuredResource.setConfiguredResourceId(configurationId);
            configuration = this.configurationManagement.getConfiguration(configuredResource.getConfiguredResourceId());
            if ( configuration == null )
            {
                configuration = this.configurationManagement.createConfiguration(configuredResource);
                this.configurationManagement.saveConfiguration(configuration);
            }
            else
            {
                return new ResponseEntity("This flow element configuration already exists!", HttpStatus.UNAUTHORIZED);
            }
        }
        else
        {
            return new ResponseEntity("This component is not configurable!", HttpStatus.UNAUTHORIZED);
        }
        return new ResponseEntity(configuration, HttpStatus.OK);
    }

    @Deprecated
    @RequestMapping(method = RequestMethod.GET,
                    value = "/createConfiguration/{moduleName}/{flowName}",
                    produces = { "application/json" })
    @PreAuthorize("hasAnyAuthority('ALL','WebServiceAdmin')")
    public ResponseEntity createFlowConfiguration(@PathVariable("moduleName") String moduleName,
                                                  @PathVariable("flowName") String flowName)
    {
        Module<Flow> module = moduleService.getModule(moduleName);
        Flow flow = module.getFlow(flowName);
        Configuration configuration = null;
        if ( flow instanceof ConfiguredResource configuredResource )
        {
            configuration = this.configurationManagement.getConfiguration(configuredResource.getConfiguredResourceId());
            if ( configuration == null )
            {
                configuration = this.configurationManagement.createConfiguration(configuredResource);
                this.configurationManagement.saveConfiguration(configuration);
            }
            else
            {
                return new ResponseEntity("This flow element configuration already exists!", HttpStatus.UNAUTHORIZED);
            }
        }
        else
        {
            return new ResponseEntity("This flow is not configurable!", HttpStatus.UNAUTHORIZED);
        }
        return new ResponseEntity(configuration, HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.GET,
                    value = "/flows")
    @PreAuthorize("hasAnyAuthority('ALL','WebServiceAdmin')")
    public ResponseEntity getFlowsConfiguration()
    {
        Module<Flow> module = moduleService.getModules().get(0);
        List<ConfigurationMetaData> configuredResources = configurationMetaDataExtractor.getFlowsConfiguration(module);
        return new ResponseEntity(configuredResources, HttpStatus.OK);
    }

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

    @Deprecated
    @RequestMapping(method = RequestMethod.GET,
                    value = "/createInvokerConfiguration/{moduleName}/{flowName}/{componentName}",
                    produces = { "application/json" })
    @PreAuthorize("hasAnyAuthority('ALL','WebServiceAdmin')")
    public ResponseEntity createInvokerConfiguration(@PathVariable("moduleName") String moduleName,
                                                     @PathVariable("flowName") String flowName,
                                                     @PathVariable("componentName") String componentName)
    {
        Flow flow = (Flow) moduleService.getModule(moduleName).getFlow(flowName);
        FlowElement<?> flowElement = flow.getFlowElement(componentName);
        Configuration configuration = null;
        if ( flowElement.getFlowElementInvoker() instanceof ConfiguredResource )
        {
            ConfiguredResource configuredResource = (ConfiguredResource) flowElement.getFlowElementInvoker();
            configuration = this.configurationManagement.getConfiguration(configuredResource.getConfiguredResourceId());
            if ( configuration == null )
            {
                configuration = this.configurationManagement.createConfiguration(configuredResource);
                this.configurationManagement.saveConfiguration(configuration);
            }
            else
            {
                return new ResponseEntity("This configuration already  exists!", HttpStatus.UNAUTHORIZED);
            }
        }
        else
        {
            return new ResponseEntity("This component is not configurable!", HttpStatus.UNAUTHORIZED);
        }
        return new ResponseEntity("Configuration created!", HttpStatus.OK);
    }

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

    @RequestMapping(method = RequestMethod.PUT)
    @PreAuthorize("hasAnyAuthority('ALL','WebServiceAdmin')")
    public ResponseEntity putConfiguration(@RequestBody ConfigurationMetaData configurationMetaData)
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

            this.systemEventService.logSystemEvent(
                configuration.getConfigurationId(),
                "Configuration Updated OldConfig [%s] NewConfig [%s]".formatted(oldConfigJson, newConfigJson),
                UserUtil.getUser());
        }
        catch (JsonProcessingException e)
        {
            logger.warn("Issue converting configuration to json.", e);
        }

        this.configurationManagement.saveConfiguration(configuration);

        return new ResponseEntity(HttpStatus.OK);
    }

    private Configuration convert(ConfigurationMetaData<List<ConfigurationParameterMetaData>> metaData)
    {

//        List<AbstractComponentParameter> list = metaData.getParameters().stream().map(p -> convertParam(p))
//                                                    .collect(Collectors.toList());
//
//        return new DefaultConfiguration(metaData.getConfigurationId(), metaData.getDescription(), list);
        return null;

    }

    private ConfigurationParameter convertParam(ConfigurationParameterMetaData metaData)
    {
        ConfigurationParameter cp = null;

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

    @RequestMapping(method = RequestMethod.DELETE,
                    value = "/{configurationId}")
    @PreAuthorize("hasAnyAuthority('ALL','WebServiceAdmin')")
    public ResponseEntity deleteConfiguration(@PathVariable("configurationId") String configurationId)
    {
        Configuration configuration = this.configurationManagement.getConfiguration(configurationId);
        if ( configuration != null )
        {
            this.configurationManagement.deleteConfiguration(configuration);
            try
            {
                String deletedConfigJson = mapper.writeValueAsString(configuration);

                this.systemEventService.logSystemEvent(
                    configuration.getConfigurationId(),
                    "Configuration Deleted OldConfig [%s]".formatted(deletedConfigJson),
                    UserUtil.getUser());
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

}
