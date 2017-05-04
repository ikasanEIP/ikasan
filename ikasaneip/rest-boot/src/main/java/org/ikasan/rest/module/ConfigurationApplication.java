package org.ikasan.rest.module;

import org.apache.log4j.Logger;
import org.ikasan.spec.configuration.Configuration;
import org.ikasan.spec.configuration.ConfigurationManagement;
import org.ikasan.spec.configuration.ConfiguredResource;
import org.ikasan.spec.flow.Flow;
import org.ikasan.spec.flow.FlowElement;
import org.ikasan.spec.module.Module;
import org.ikasan.spec.module.ModuleContainer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;


/**
 * Configuration application implementing the REST contract
 */

@RequestMapping("/rest/configuration")
@RestController
public class ConfigurationApplication {

    private static Logger logger = Logger.getLogger(ConfigurationApplication.class);

    @Autowired
    private ConfigurationManagement<ConfiguredResource, Configuration> configurationManagement;
    @Autowired
    private ModuleContainer moduleContainer;

    /**
     * TODO: work out how to get annotation security working.
     *
     * @param moduleName
     * @param flowName
     * @return
     */
    @RequestMapping(method = RequestMethod.GET,
            value = "/createConfiguration/{moduleName}/{flowName}/{componentName}",
            produces = {"application/json"})
    public ResponseEntity createConfiguration(@PathVariable("moduleName") String moduleName,
                                              @PathVariable("flowName") String flowName,
                                              @PathVariable("componentName") String componentName) {

        Module<Flow> module = moduleContainer.getModule(moduleName);

        Flow flow = module.getFlow(flowName);

        FlowElement<?> flowElement = flow.getFlowElement(componentName);

        Configuration configuration = null;

        if (flowElement.getFlowComponent() instanceof ConfiguredResource) {
            ConfiguredResource configuredResource = (ConfiguredResource) flowElement.getFlowComponent();

            configuration = this.configurationManagement.getConfiguration(configuredResource.getConfiguredResourceId());

            if (configuration == null) {
                configuration = this.configurationManagement.createConfiguration(configuredResource);
                this.configurationManagement.saveConfiguration(configuration);
            } else {
                return new ResponseEntity("This configuration alread exists!", HttpStatus.UNAUTHORIZED);
            }
        } else {
            return new ResponseEntity("This component is not configurable!", HttpStatus.UNAUTHORIZED);
        }

        return new ResponseEntity("Configuration created!", HttpStatus.OK);
    }

    /**
     * TODO: work out how to get annotation security working.
     *
     * @param moduleName
     * @param flowName
     * @return
     */
    @RequestMapping(method = RequestMethod.GET,
            value = "/createFlowElementConfiguration/{moduleName}/{flowName}/{componentName}",
            produces = {"application/json"})
    public ResponseEntity createFlowElementConfiguration(@PathVariable("moduleName") String moduleName, @PathVariable("flowName") String flowName,
                                                         @PathVariable("componentName") String componentName) {

        Module<Flow> module = moduleContainer.getModule(moduleName);

        Flow flow = module.getFlow(flowName);

        FlowElement<?> flowElement = flow.getFlowElement(componentName);

        Configuration configuration = null;

        if (flowElement instanceof ConfiguredResource) {
            ConfiguredResource configuredResource = (ConfiguredResource) flowElement;

            String configurationId = moduleName + flowName + componentName + "_element";

            configuredResource.setConfiguredResourceId(configurationId);

            configuration = this.configurationManagement.getConfiguration(configuredResource.getConfiguredResourceId());

            if (configuration == null) {
                configuration = this.configurationManagement.createConfiguration(configuredResource);
                this.configurationManagement.saveConfiguration(configuration);
            } else {
                return new ResponseEntity("This flow element configuration alread exists!", HttpStatus.UNAUTHORIZED);

            }
        } else {
            return new ResponseEntity("This component is not configurable!", HttpStatus.UNAUTHORIZED);

        }

        return new ResponseEntity("Configuration created!", HttpStatus.OK);

    }

    /**
     * TODO: work out how to get annotation security working.
     *
     * @param context
     * @param moduleName
     * @param flowName
     * @return
     */
    @RequestMapping(method = RequestMethod.GET,
            value = "/createConfiguration/{moduleName}/{flowName}",
            produces = {"application/json"})
    public ResponseEntity createFlowConfiguration(@PathVariable("moduleName") String moduleName, @PathVariable("flowName") String flowName) {
        Module<Flow> module = moduleContainer.getModule(moduleName);

        Flow flow = module.getFlow(flowName);

        Configuration configuration = null;

        if (flow instanceof ConfiguredResource) {
            ConfiguredResource configuredResource = (ConfiguredResource) flow;

            configuration = this.configurationManagement.getConfiguration(configuredResource.getConfiguredResourceId());

            if (configuration == null) {
                configuration = this.configurationManagement.createConfiguration(configuredResource);
                this.configurationManagement.saveConfiguration(configuration);
            } else {
                return new ResponseEntity("This flow element configuration alread exists!", HttpStatus.UNAUTHORIZED);


            }
        } else {
            return new ResponseEntity("This flow is not configurable!", HttpStatus.UNAUTHORIZED);

        }

        return new ResponseEntity("Configuration created!", HttpStatus.OK);

    }
}
