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
package org.ikasan.rest.module;

import org.apache.log4j.Logger;
import org.ikasan.spec.configuration.ConfiguredResource;
import org.ikasan.spec.flow.Flow;
import org.ikasan.spec.flow.FlowElement;
import org.ikasan.spec.module.Module;
import org.ikasan.spec.module.ModuleContainer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;


import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author Ikasan Development Team
 */
@RequestMapping("/rest/discovery")
@RestController
public class DiscoveryApplication {
    private static Logger logger = Logger.getLogger(DiscoveryApplication.class);

    @Autowired
    private ModuleContainer moduleContainer;

    /**
     * Method to get the flows associated with a module.
     *
     * @param moduleName
     * @return List of Flows
     */
    @SuppressWarnings("unchecked")
    @RequestMapping(method = RequestMethod.GET,
            value = "/flows/{moduleName}",
            produces = {"application/json"})
    public List<org.ikasan.topology.model.Flow> getFlows(@PathVariable("moduleName") String moduleName) {

        List<org.ikasan.topology.model.Flow> flows = new ArrayList<org.ikasan.topology.model.Flow>();

        Module<Flow> module = moduleContainer.getModule(moduleName);

        org.ikasan.topology.model.Module topologyModule = new org.ikasan.topology.model.Module(moduleName, moduleName, module.getDescription(),
                "", null, "");

        int flowOrder = 0;
        for (Flow flow : module.getFlows()) {

            org.ikasan.topology.model.Flow topologyFlow = new org.ikasan.topology.model.Flow(flow.getName(), "description", topologyModule);
            topologyFlow.setOrder(flowOrder++);

            if (flow instanceof ConfiguredResource) {
                topologyFlow.setConfigurationId(((ConfiguredResource) flow).getConfiguredResourceId());
                topologyFlow.setConfigurable(true);
            } else {
                topologyFlow.setConfigurable(false);
            }

            flows.add(topologyFlow);

            Set<org.ikasan.topology.model.Component> components
                    = new HashSet<org.ikasan.topology.model.Component>();

            int order = 0;

            for (FlowElement<?> flowElement : flow.getFlowElements()) {

                org.ikasan.topology.model.Component component = new org.ikasan.topology.model.Component();
                component.setName(flowElement.getComponentName());
                if (flowElement.getDescription() != null) {
                    component.setDescription(flowElement.getDescription());
                } else {
                    component.setDescription("No description.");
                }

                if (flowElement.getFlowComponent() instanceof ConfiguredResource) {
                    component.setConfigurationId(((ConfiguredResource) flowElement.getFlowComponent()).getConfiguredResourceId());
                    component.setConfigurable(true);
                } else {
                    component.setConfigurable(false);
                }

                component.setOrder(order++);
                components.add(component);
            }

            topologyFlow.setComponents(components);
        }

        return flows;
    }

    /**
     * Method to get the flows associated with a module.
     *
     * @param moduleName
     * @return List of Flows
     */
    @SuppressWarnings("unchecked")
    @RequestMapping(method = RequestMethod.GET,
            value = "/components/{moduleName}/{flowName}",
            produces = {"application/json"})

    public List<org.ikasan.topology.model.Component> getComponents(@PathVariable("moduleName") String moduleName,
                                                                   @PathVariable("flowName") String flowName) {

        List<org.ikasan.topology.model.Component> components
                = new ArrayList<org.ikasan.topology.model.Component>();

        Module<Flow> module = moduleContainer.getModule(moduleName);

        Flow flow = module.getFlow(flowName);

        for (FlowElement<?> flowElement : flow.getFlowElements()) {
            org.ikasan.topology.model.Component component = new org.ikasan.topology.model.Component();
            component.setName(flowElement.getComponentName());
            if (flowElement.getDescription() != null) {
                component.setDescription(flowElement.getDescription());
            } else {
                component.setDescription("No description.");
            }

            if (flowElement.getFlowComponent() instanceof ConfiguredResource) {
                component.setConfigurationId(((ConfiguredResource) flowElement.getFlowComponent()).getConfiguredResourceId());
                component.setConfigurable(true);
            } else {
                component.setConfigurable(false);
            }

            components.add(component);
        }

        return components;
    }
}
