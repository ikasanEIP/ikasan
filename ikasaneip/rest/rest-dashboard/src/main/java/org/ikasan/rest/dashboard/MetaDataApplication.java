package org.ikasan.rest.dashboard;

import org.ikasan.spec.flow.Flow;
import org.ikasan.spec.metadata.FlowMetaDataProvider;
import org.ikasan.spec.metadata.ModuleMetaDataProvider;
import org.ikasan.spec.module.Module;
import org.ikasan.spec.module.ModuleContainer;
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

    /**
     * TODO: work out how to get annotation security working.
     *
     * @param moduleName
     * @param flowName
     * @return
     */
    @RequestMapping(method = RequestMethod.GET,
            value = "/flow/{moduleName}/{flowName}",
            produces = {"application/json"})
    @PreAuthorize("hasAnyAuthority('ALL','WebServiceAdmin')")
    public ResponseEntity getFlowMetadata(@PathVariable("moduleName") String moduleName,
                                              @PathVariable("flowName") String flowName) {

        Module<Flow> module = moduleContainer.getModule(moduleName);

        Flow flow = module.getFlow(flowName);

        return new ResponseEntity(this.flowMetaDataProvider.describeFlow(flow), HttpStatus.OK);
    }

    /**
     * TODO: work out how to get annotation security working.
     *
     * @param moduleName
     * @return
     */
    @RequestMapping(method = RequestMethod.GET,
        value = "/module/{moduleName}",
        produces = {"application/json"})
    @PreAuthorize("hasAnyAuthority('ALL','WebServiceAdmin')")
    public ResponseEntity getModuleMetadata(@PathVariable("moduleName") String moduleName) {

        Module<Flow> module = moduleContainer.getModule(moduleName);

        return new ResponseEntity(this.moduleMetaDataProvider.describeModule(module), HttpStatus.OK);
    }
}
