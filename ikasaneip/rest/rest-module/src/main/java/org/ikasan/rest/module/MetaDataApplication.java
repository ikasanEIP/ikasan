package org.ikasan.rest.module;

import org.ikasan.spec.flow.Flow;
import org.ikasan.spec.metadata.FlowMetaData;
import org.ikasan.spec.metadata.FlowMetaDataProvider;
import org.ikasan.spec.metadata.ModuleManifestMetaDataProvider;
import org.ikasan.spec.metadata.ModuleMetaDataProvider;
import org.ikasan.spec.module.Module;
import org.ikasan.spec.module.ModuleContainer;
import org.ikasan.spec.module.ModuleService;
import org.ikasan.spec.module.StartupControl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;


/**
 * Metadata application implementing the REST contract
 */

@RequestMapping("/rest/metadata")
@RestController
public class MetaDataApplication
{
    private static Logger logger = LoggerFactory.getLogger(MetaDataApplication.class);

    @Autowired
    private ModuleContainer moduleContainer;

    @Autowired
    private FlowMetaDataProvider<String> flowMetaDataProvider;

    @Autowired
    private ModuleMetaDataProvider<String> moduleMetaDataProvider;

    @Autowired
    private ModuleManifestMetaDataProvider<String> moduleManifestMetaDataProvider;

    @Autowired
    private ModuleService moduleService;


    /**
     * Retrieves metadata for a specific flow.
     *
     * @param moduleName the name of the module containing the flow
     * @param flowName the name of the flow to retrieve metadata for
     * @return ResponseEntity containing the metadata of the specified flow
     */
    @RequestMapping(method = RequestMethod.GET,
            value = "/flow/{moduleName}/{flowName}",
            produces = {"application/json"})
    @PreAuthorize("hasAnyAuthority('ALL','WebServiceAdmin')")
    public ResponseEntity getFlowMetadata(@PathVariable("moduleName") String moduleName,
                                              @PathVariable("flowName") String flowName) {

        Module<Flow> module = moduleContainer.getModule(moduleName);

        Flow flow = module.getFlow(flowName);

        StartupControl startupControl = this.moduleService.getStartupControl(moduleName, flowName);

        return new ResponseEntity(this.flowMetaDataProvider.describeFlow(flow, startupControl), HttpStatus.OK);
    }



    /**
     * Retrieves the metadata of a module based on the provided module name.
     *
     * @param moduleName the name of the module for which metadata is to be retrieved
     * @return ResponseEntity containing the metadata of the specified module
     */
    @RequestMapping(method = RequestMethod.GET,
        value = "/module/{moduleName}",
        produces = {"application/json"})
    @PreAuthorize("hasAnyAuthority('ALL','WebServiceAdmin')")
    public ResponseEntity getModuleMetadata(@PathVariable("moduleName") String moduleName) {

        Module<Flow> module = moduleContainer.getModule(moduleName);

        Map<String,StartupControl> stringStartupControlMap = new HashMap<>();

        module.getFlows().forEach(flow -> {
            StartupControl startupControl = moduleService.getStartupControl(moduleName, flow.getName());
            if(startupControl != null) {
                stringStartupControlMap.put(flow.getName(), startupControl);
            }
        });

        return new ResponseEntity(this.moduleMetaDataProvider.describeModule(module, stringStartupControlMap), HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.GET,
        value = "/moduleManifest/{moduleName}",
        produces = {"application/json"})
    @PreAuthorize("hasAnyAuthority('ALL','WebServiceAdmin')")
    public ResponseEntity getModuleManifestMetadata(@PathVariable("moduleName") String moduleName) {

        Module<Flow> module = moduleContainer.getModule(moduleName);

        Map<String,StartupControl> stringStartupControlMap = new HashMap<>();

        module.getFlows().forEach(flow -> {
            StartupControl startupControl = moduleService.getStartupControl(moduleName, flow.getName());
            if(startupControl != null) {
                stringStartupControlMap.put(flow.getName(), startupControl);
            }
        });

        return new ResponseEntity(this.moduleManifestMetaDataProvider.describeModuleManifest(module, stringStartupControlMap), HttpStatus.OK);
    }
}
