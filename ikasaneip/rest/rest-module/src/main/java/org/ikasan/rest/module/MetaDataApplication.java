package org.ikasan.rest.module;

import org.ikasan.spec.flow.Flow;
import org.ikasan.spec.metadata.FlowMetaData;
import org.ikasan.spec.metadata.FlowMetaDataProvider;
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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
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
    private ModuleService moduleService;

    /**
     *
     * @param moduleName
     * @param flowName
     * @return
     */
    @GetMapping(
            value = "/flow/{moduleName}/{flowName}",
            produces = {"application/json"})
    @PreAuthorize("hasAnyAuthority('ALL','WebServiceAdmin')")
    public ResponseEntity getFlowMetadata(@PathVariable String moduleName,
                                              @PathVariable String flowName) {

        Module<Flow> module = moduleContainer.getModule(moduleName);

        Flow flow = module.getFlow(flowName);

        StartupControl startupControl = this.moduleService.getStartupControl(moduleName, flowName);

        return new ResponseEntity(this.flowMetaDataProvider.describeFlow(flow, startupControl), HttpStatus.OK);
    }

    /**
     *
     * @param moduleName
     * @return
     */
    @GetMapping(
        value = "/module/{moduleName}",
        produces = {"application/json"})
    @PreAuthorize("hasAnyAuthority('ALL','WebServiceAdmin')")
    public ResponseEntity getModuleMetadata(@PathVariable String moduleName) {

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
}
