package org.ikasan.rest.module;

import org.ikasan.spec.configuration.Configuration;
import org.ikasan.spec.configuration.ConfigurationManagement;
import org.ikasan.spec.configuration.ConfiguredResource;
import org.ikasan.spec.flow.Flow;
import org.ikasan.spec.flow.FlowElement;
import org.ikasan.spec.metadata.ConfigurationMetaData;
import org.ikasan.spec.metadata.ConfigurationMetaDataExtractor;
import org.ikasan.spec.metadata.ConfigurationMetaDataProvider;
import org.ikasan.spec.module.Module;
import org.ikasan.spec.module.ModuleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Configuration application implementing the REST contract
 */
@RequestMapping("/rest/configuration")
@RestController
public class ConfigurationApplication
{
    @Autowired
    private ConfigurationManagement<ConfiguredResource, Configuration> configurationManagement;

    @Autowired
    private ConfigurationMetaDataExtractor<String> configurationMetaDataExtractor;

    @Autowired
    private ConfigurationMetaDataProvider<String> configurationMetaDataProvider;

    /** The module service */
    @Autowired
    private ModuleService moduleService;

    @Deprecated
    @RequestMapping(method = RequestMethod.GET,
        value = "/createConfiguration/{moduleName}/{flowName}/{componentName}",
        produces = { "application/json" })
    @PreAuthorize("hasAnyAuthority('ALL','WebServiceAdmin')")
    public ResponseEntity createConfiguration(
        @PathVariable("moduleName") String moduleName,
        @PathVariable("flowName") String flowName,
        @PathVariable("componentName") String componentName)
    {
        Module<Flow> module = moduleService.getModule(moduleName);
        Flow flow = module.getFlow(flowName);
        FlowElement<?> flowElement = flow.getFlowElement(componentName);
        Configuration configuration = null;
        if (flowElement.getFlowComponent() instanceof ConfiguredResource)
        {
            ConfiguredResource configuredResource = (ConfiguredResource) flowElement.getFlowComponent();
            configuration = this.configurationManagement.getConfiguration(configuredResource.getConfiguredResourceId());
            if (configuration == null)
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
    @RequestMapping(
        method = RequestMethod.GET,
        value = "/createFlowElementConfiguration/{moduleName}/{flowName}/{componentName}",
        produces = { "application/json" })
    @PreAuthorize("hasAnyAuthority('ALL','WebServiceAdmin')")
    public ResponseEntity createFlowElementConfiguration(
        @PathVariable("moduleName") String moduleName,
        @PathVariable("flowName") String flowName,
        @PathVariable("componentName") String componentName)
    {
        Module<Flow> module = moduleService.getModule(moduleName);
        Flow flow = module.getFlow(flowName);
        FlowElement<?> flowElement = flow.getFlowElement(componentName);
        Configuration configuration = null;
        if (flowElement instanceof ConfiguredResource)
        {
            ConfiguredResource configuredResource = (ConfiguredResource) flowElement;
            String configurationId = moduleName + flowName + componentName + "_element";
            configuredResource.setConfiguredResourceId(configurationId);
            configuration = this.configurationManagement.getConfiguration(configuredResource.getConfiguredResourceId());
            if (configuration == null)
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
    public ResponseEntity createFlowConfiguration(
        @PathVariable("moduleName") String moduleName,
        @PathVariable("flowName") String flowName)
    {
        Module<Flow> module = moduleService.getModule(moduleName);
        Flow flow = module.getFlow(flowName);
        Configuration configuration = null;
        if (flow instanceof ConfiguredResource)
        {
            ConfiguredResource configuredResource = (ConfiguredResource) flow;
            configuration = this.configurationManagement.getConfiguration(configuredResource.getConfiguredResourceId());
            if (configuration == null)
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

    @RequestMapping(
        method = RequestMethod.GET,
        value = "/flows", produces = {
        "application/json" })
    @PreAuthorize("hasAnyAuthority('ALL','WebServiceAdmin')")
    public ResponseEntity getFlowsConfiguration()
    {
        Module<Flow> module = moduleService.getModules().get(0);
        String configuredResources = configurationMetaDataExtractor.getFlowsConfiguration(module);
        return new ResponseEntity(configuredResources, HttpStatus.OK);
    }

    @RequestMapping(
        method = RequestMethod.GET,
        value = "/{moduleName}/{flowName}/flow",
        produces = { "application/json" })
    @PreAuthorize("hasAnyAuthority('ALL','WebServiceAdmin')")
    public ResponseEntity getFlowsConfiguration(
        @PathVariable("moduleName") String moduleName,
        @PathVariable("flowName") String flowName)
    {
        Flow flow = (Flow) moduleService.getModule(moduleName).getFlow(flowName);
        String configuredResources = configurationMetaDataExtractor.getFlowConfiguration(flow);
        return new ResponseEntity(configuredResources, HttpStatus.OK);
    }

    @Deprecated
    @RequestMapping(
        method = RequestMethod.GET,
        value = "/createInvokerConfiguration/{moduleName}/{flowName}/{componentName}",
        produces = {
        "application/json" })
    @PreAuthorize("hasAnyAuthority('ALL','WebServiceAdmin')")
    public ResponseEntity createInvokerConfiguration(
        @PathVariable("moduleName") String moduleName,
        @PathVariable("flowName") String flowName,
        @PathVariable("componentName") String componentName)
    {
        Flow flow = (Flow) moduleService.getModule(moduleName).getFlow(flowName);
        FlowElement<?> flowElement = flow.getFlowElement(componentName);
        Configuration configuration = null;
        if (flowElement.getFlowElementInvoker() instanceof ConfiguredResource)
        {
            ConfiguredResource configuredResource = (ConfiguredResource) flowElement.getFlowElementInvoker();
            configuration = this.configurationManagement.getConfiguration(configuredResource.getConfiguredResourceId());
            if (configuration == null)
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

    @RequestMapping(
        method = RequestMethod.GET,
        value = "/invokers",
        produces = { "application/json" })
    @PreAuthorize("hasAnyAuthority('ALL','WebServiceAdmin')")
    public ResponseEntity getInvokersConfiguration()
    {
        Module<Flow> module = moduleService.getModules().get(0);
        String configuredResources = configurationMetaDataExtractor.getInvokersConfiguration(module);
        return new ResponseEntity(configuredResources, HttpStatus.OK);
    }

    @RequestMapping(
        method = RequestMethod.GET,
        value = "/components",
        produces = { "application/json" })
    @PreAuthorize("hasAnyAuthority('ALL','WebServiceAdmin')")
    public ResponseEntity getComponentsConfiguration()
    {
        Module<Flow> module = moduleService.getModules().get(0);
        String configuredResources = configurationMetaDataExtractor.getComponentsConfiguration(module);
        return new ResponseEntity(configuredResources, HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.GET,
        value = "/{moduleName}/{flowName}/invokers",
        produces = { "application/json" })
    @PreAuthorize("hasAnyAuthority('ALL','WebServiceAdmin')")
    public ResponseEntity getInvokersConfiguration(
        @PathVariable("moduleName") String moduleName,
        @PathVariable("flowName") String flowName)
    {
        Flow flow = (Flow) moduleService.getModule(moduleName).getFlow(flowName);
        String configuredResources = configurationMetaDataExtractor.getInvokersConfiguration(flow);
        return new ResponseEntity(configuredResources, HttpStatus.OK);
    }

    @RequestMapping(
        method = RequestMethod.GET,
        value = "/{moduleName}/{flowName}/components",
        produces = { "application/json" })
    @PreAuthorize("hasAnyAuthority('ALL','WebServiceAdmin')")
    public ResponseEntity getComponentsConfiguration(
        @PathVariable("moduleName") String moduleName,
        @PathVariable("flowName") String flowName)
    {
        Flow flow = (Flow) moduleService.getModule(moduleName).getFlow(flowName);
        String configuredResources = configurationMetaDataExtractor.getComponentsConfiguration(flow);
        return new ResponseEntity(configuredResources, HttpStatus.OK);
    }

//    @RequestMapping(
//        method = RequestMethod.PUT,
//        value = "/{moduleName}/{flowName}/components",
//        produces = { "application/json" })
//    @PreAuthorize("hasAnyAuthority('ALL','WebServiceAdmin')")
//    public ResponseEntity getComponentsConfiguration(
//        @PathVariable("moduleName") String moduleName,
//        @PathVariable("flowName") String flowName,
//        @RequestBody String body)
//    {
//
//        List<ConfigurationMetaData> configurationMetaDataList = configurationMetaDataProvider.deserialiseMetadataConfigurations(body);
//
//        configurationMetaDataList.stream().map(metaData ->);
//
//        return new ResponseEntity(configuredResources, HttpStatus.OK);
//    }
}
